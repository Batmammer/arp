package arp.search;

import arp.dto.grid.Accumulator;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Storage;
import arp.enums.EnergySourceType;
import arp.exception.BusinessException;
import arp.service.Data;

import java.util.*;

import static arp.exception.FailureReason.SOLUTION_NOT_FOUND;
import static arp.service.Utils.createTableOfValue;

@lombok.Data
public class BroadFirstSearchAlgorithm {
    private Data data;
    private PriorityQueue<State> priorityQueue;
    private Set<String> visitedStates;

    public BroadFirstSearchAlgorithm(Data data) {
        this.data = data;
        this.priorityQueue = new PriorityQueue<>();
        this.visitedStates = new HashSet<>();
    }

    public State calculate() {
        State initialState = new State(this.data);
        priorityQueue.add(initialState);
        visitedStates.add(initialState.toString());
        while (!priorityQueue.isEmpty()) {
            State state = priorityQueue.poll();
            state.updateMetrics(data);
            if (state.getMetrics().isGood())
                return state;
            priorityQueue.addAll(processState(state));
        }
        throw new BusinessException("BroadSearchAlgorithm has no state to process", SOLUTION_NOT_FOUND);
    }

    private List<State> processState(State state) {
        List<State> result = new ArrayList<>();

        List<State> nextStates = getNextStates(state);
        for (State nextState : nextStates) {
            if (!visitedStates.contains(nextState.toString())) {
                visitedStates.add(nextState.toString());

                result.add(nextState);
            }
        }
        return result;
    }

    private List<State> getNextStates(State state) {
        List<State> results = new ArrayList<>();

        for (Storage storage : state.getStorages()) {
            updateStorage(results, state, storage.getId());

            for (Electrolyzer electrolyzer : storage.getElectrolyzers()) {
                updateElectrolizer(results, state, electrolyzer.getId());
                updateAccumulator(results, state, electrolyzer.getId());

                for (EnergySource source : electrolyzer.getSources()) {
                    if (EnergySourceType.WIND.equals(source.getType())) {
                        updateWindSource(results, state, electrolyzer.getId(), source.getId());
                    } else {
                        updatePvSource(results, state, electrolyzer.getId(), source.getId());
                    }
                }
                addWindSource(results, state, electrolyzer.getId());
                addPvSource(results, state, electrolyzer.getId());
            }
            addElectrolizer(results, state, storage.getId());
        }
        addStorage(results, state);

        return results;
    }


    /** =-=-=-=-= STORAGE =-=-=-=-= */

    private void addStorage(List<State> results, State state) {
        long newId = state.nextStorageId();

        Action action = createStorageAction(newId);

        State nextState = state.buildNextState(action);
        Storage storage = new Storage();
        storage.setId(newId);
        storage.setMaxCapacity(1.0);

        nextState.getStorages().add(storage);

        results.add(nextState);
    }

    private void updateStorage(List<State> results, State state, Long id) {
        Action action = createStorageAction(id);


        State nextState = state.buildNextState(action);
        Storage storage = nextState.findStorageById(id);
        storage.setMaxCapacity(storage.getMaxCapacity() + 1.0);

        results.add(nextState);
    }

    private Action createStorageAction(long newId) {
        Action action = new Action();
        action.setType(ActionType.STORAGE);
        action.setObjectId(newId);
        action.setActionCost(this.data.getGridCosts().getStorageHydrogenCost());
        return action;
    }

    /** =-=-=-=-= ELECTROLYZER =-=-=-=-= */

    private void addElectrolizer(List<State> results, State state, Long parentId) {
        long newId = state.nextElectrolyzerId();

        Action action = createElectrolizerAction(newId);
        State nextState = state.buildNextState(action);

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.setId(newId);
        electrolyzer.setAccumulator(new Accumulator());
        electrolyzer.setEfficiency(data.getGridConstants().getElectrolyzerEfficiency());
        electrolyzer.setMaxPower(1.0);

        Storage storage = nextState.findStorageById(parentId);
        storage.getElectrolyzers().add(electrolyzer);

        results.add(nextState);
    }

    private void updateElectrolizer(List<State> results, State state, Long id) {
        Action action = createElectrolizerAction(id);
        State nextState = state.buildNextState(action);
        Electrolyzer electrolyzer = nextState.findElectrolyzerById(id);
        electrolyzer.setMaxPower(electrolyzer.getMaxPower() + 1.0);

        results.add(nextState);
    }

    private Action createElectrolizerAction(long newId) {
        Action action = new Action();
        action.setType(ActionType.ELECTROLYZER);
        action.setObjectId(newId);
        action.setActionCost(this.data.getGridCosts().getElectrolyzerCost());
        return action;
    }

    /** =-=-=-=-= ACCUMULATOR =-=-=-=-= */

    private void updateAccumulator(List<State> results, State state, Long id) {
        Action action = createAccumulatorAction(id);

        State nextState = state.buildNextState(action);
        Accumulator accumulator = nextState.findAccumulatorById(id);
        accumulator.setAccumulatorMaxSize(accumulator.getAccumulatorMaxSize() + 1.0);

        results.add(nextState);
    }

    private Action createAccumulatorAction(Long id) {
        Action action = new Action();
        action.setType(ActionType.ACCUMULATOR);
        action.setObjectId(id);
        action.setActionCost(this.data.getGridCosts().getStoragePowerCost());
        return action;
    }

    /** =-=-=-=-= SOURCE WIND =-=-=-=-= */

    private void addWindSource(List<State> results, State state, Long parentId) {
        long newId = state.nextEnergySourceId();
        Action action = createWindAction(newId);

        State nextState = state.buildNextState(action);
        EnergySource energySource = new EnergySource();
        energySource.setId(newId);
        energySource.setType(EnergySourceType.WIND);
        energySource.setMaxPower(1.0);
        energySource.setDistance(0.0);

        Electrolyzer electrolyzer = nextState.findElectrolyzerById(parentId);
        electrolyzer.getSources().add(energySource);
        electrolyzer.recalculateSummaryEnergyProduction(data);

        results.add(nextState);
    }

    private void updateWindSource(List<State> results, State state, Long parentId, Long id) {
        Action action = createWindAction(id);

        State nextState = state.buildNextState(action);
        EnergySource energySource = nextState.findEnergySourceById(id);
        energySource.setMaxPower(energySource.getMaxPower() + 1.0);

        Electrolyzer electrolyzer = nextState.findElectrolyzerById(parentId);
        electrolyzer.getSources().add(energySource);
        electrolyzer.recalculateSummaryEnergyProduction(data);

        results.add(nextState);
    }


    private Action createWindAction(Long id) {
        Action action = new Action();
        action.setType(ActionType.WIND);
        action.setObjectId(id);
        action.setActionCost(this.data.getGridCosts().getWindCost());
        return action;
    }

    /** =-=-=-=-= SOURCE PV =-=-=-=-= */

    private void addPvSource(List<State> results, State state, Long parentId) {
        long newId = state.nextEnergySourceId();
        Action action = createPvAction(newId);

        State nextState = state.buildNextState(action);
        EnergySource energySource = new EnergySource();
        energySource.setId(newId);
        energySource.setType(EnergySourceType.PV);
        energySource.setMaxPower(1.0);
        energySource.setDistance(0.0);

        Electrolyzer electrolyzer = nextState.findElectrolyzerById(parentId);
        electrolyzer.getSources().add(energySource);
        electrolyzer.recalculateSummaryEnergyProduction(data);

        results.add(nextState);
    }

    private void updatePvSource(List<State> results, State state, Long parentId, Long id) {
        Action action = createPvAction(id);

        State nextState = state.buildNextState(action);
        EnergySource energySource = nextState.findEnergySourceById(id);
        energySource.setMaxPower(energySource.getMaxPower() + 1.0);

        Electrolyzer electrolyzer = nextState.findElectrolyzerById(parentId);
        electrolyzer.getSources().add(energySource);
        electrolyzer.recalculateSummaryEnergyProduction(data);

        results.add(nextState);
    }

    private Action createPvAction(Long id) {
        Action action = new Action();
        action.setType(ActionType.PV);
        action.setObjectId(id);
        action.setActionCost(this.data.getGridCosts().getPvCost());
        return action;
    }


}
