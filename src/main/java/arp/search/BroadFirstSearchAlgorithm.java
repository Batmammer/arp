package arp.search;

import arp.dto.grid.Accumulator;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Storage;
import arp.enums.EnergySourceType;
import arp.exception.BusinessException;
import arp.service.CalculateYearAlgorithm;
import arp.service.Data;
import arp.service.Utils;
import arp.service.YearResult;

import java.util.*;
import java.util.stream.Collectors;

import static arp.exception.FailureReason.SOLUTION_NOT_FOUND;
import static arp.service.Utils.createTableOfValue;

public class BroadFirstSearchAlgorithm {
    public Data data;
    public PriorityQueue<State> priorityQueue;
    public Set<String> visitedStates;

    public BroadFirstSearchAlgorithm(Data data) {
        this.data = data;
        this.priorityQueue = new PriorityQueue<>();
        this.visitedStates = new HashSet<>();
    }

    public State calculate() {
        State initialState = initialState();
        priorityQueue.add(initialState);
        visitedStates.add(initialState.toString());
        while (!priorityQueue.isEmpty()) {
            State state = priorityQueue.poll();
            if (state.good)
                return state;
            priorityQueue.addAll(processState(state));
        }
        throw new BusinessException("BroadSearchAlgorithm has no state to process", SOLUTION_NOT_FOUND);
    }

    private List<State> processState(State state) {
        List<State> result = new ArrayList<>();
        System.out.println("@@@: PROCESSING STATE: " + state);
        for (ActionType actionType : ActionType.values()) {
            State nextState = getNextState(state, actionType);
            if (!visitedStates.contains(nextState.toString())) {
                visitedStates.add(nextState.toString());
                result.add(nextState);
            }
        }
        return result;
    }

    private State initialState() {
        CalculateYearAlgorithm calculateYearAlgorithm = new CalculateYearAlgorithm(data);
        YearResult yearResult = calculateYearAlgorithm.calculate();
        return new State(data.getStorages(), yearResult.isGood(), yearResult.minHourHydrogenLevel, null, null, 0, 0, data);
    }

    private State getNextState(State state, ActionType actionType) {
        List<Storage> nextStorages = state.storages.stream().map(storage -> storage.clone()).collect(Collectors.toList());
        double totalCost = state.totalCost;
        double actionCost = 0;
        Map<Long, double[]> newSummaryEnergyProduction = new HashMap<>();
        for (Map.Entry<Long, double[]> entry : state.data.getSummaryEnergyProduction().entrySet()) {
            newSummaryEnergyProduction.put(entry.getKey(), Arrays.copyOf(entry.getValue(), entry.getValue().length));
        }

        for (Storage storage : nextStorages) {
            if (storage.getElectrolyzers().isEmpty()) {
                storage.getElectrolyzers().add(createNewElectorlyzer(newSummaryEnergyProduction));
                actionCost = state.data.getGridCosts().getElectrolyzerCost();
            } else {
                Electrolyzer electrolyzer = storage.getElectrolyzers().get(0);
//                if (!newSummaryEnergyProduction.containsKey(electrolyzer.getId())) {
//                    newSummaryEnergyProduction.put(electrolyzer.getId(), createTableOfValue(0.0));
//                }
                switch (actionType) {
                    case WIND:
                        actionCost = addWind(electrolyzer, newSummaryEnergyProduction);
                        break;
                    case PV:
                        actionCost = addPv(electrolyzer, newSummaryEnergyProduction);
                        break;
                    case ELECTROLIZER:
                        actionCost = state.data.getGridCosts().getElectrolyzerCost();
                        electrolyzer.setMaxPower(electrolyzer.getMaxPower() + 1.0);
                        break;
                    case STORAGE_POWER:
                        actionCost = state.data.getGridCosts().getStoragePowerCost();
                        electrolyzer.getAccumulator().setAccumulatorMaxSize(electrolyzer.getAccumulator().getAccumulatorMaxSize() + 1.0);
                        break;
                    case STORAGE_HYDROGEN:
                        actionCost = state.data.getGridCosts().getStorageHydrogenCost();
                        storage.setMaxCapacity(storage.getMaxCapacity() + 1.0);
                        break;
                }
            }
        }


        totalCost += actionCost;
        Data newData = new Data(state.data.getGridConstants(), state.data.getGridCosts(), nextStorages, state.data.getVehiclesConsumption(), newSummaryEnergyProduction);
        CalculateYearAlgorithm calculateYearAlgorithm = new CalculateYearAlgorithm(newData);
        YearResult yearResult = calculateYearAlgorithm.calculate();
        State newState = new State(nextStorages, yearResult.isGood(), yearResult.minHourHydrogenLevel, state, actionType, actionCost, totalCost, newData);
        System.out.println("\tADDING NEW STATE: " + yearResult.isGood() + ": " + yearResult + ": " + newState.toString());
        return newState;
    }

    private Electrolyzer createNewElectorlyzer(Map<Long, double[]> newSummaryEnergyProduction) {
        Electrolyzer electrolyzer = new Electrolyzer();
        if (newSummaryEnergyProduction.isEmpty())
            electrolyzer.setId(1L);
        else
            electrolyzer.setId(newSummaryEnergyProduction.keySet().stream().mapToLong(l -> l).max().getAsLong() + 1L);
        electrolyzer.setEfficiency(data.getGridConstants().getElectrolyzerEfficiency());
        newSummaryEnergyProduction.put(electrolyzer.getId(), createTableOfValue(0.0));
        electrolyzer.setAccumulator(new Accumulator(0.0));
        electrolyzer.setSources(new ArrayList<>());
        electrolyzer.setMaxPower(1.0);
        return electrolyzer;
    }

    private double addPv(Electrolyzer electrolyzer, Map<Long, double[]> newSummaryEnergyProduction) {
        double actionCost = data.getGridCosts().getPvCost();

        EnergySource energySource = getOrCreate(electrolyzer, EnergySourceType.PV);
        energySource.setMaxPower(energySource.getMaxPower() + 1.0);

        for (int hour = 0; hour < Utils.HOURS_OF_YEAR; ++hour) {
            newSummaryEnergyProduction.get(electrolyzer.getId())[hour] += data.getGridConstants().getPvDailyProduction()[hour];
        }
        return actionCost;
    }

    private double addWind(Electrolyzer electrolyzer, Map<Long, double[]> newSummaryEnergyProduction) {
        double actionCost = data.getGridCosts().getWindCost();

        EnergySource energySource = getOrCreate(electrolyzer, EnergySourceType.WIND);
        energySource.setMaxPower(energySource.getMaxPower() + 1.0);

        for (int hour = 0; hour < Utils.HOURS_OF_YEAR; ++hour) {
            newSummaryEnergyProduction.get(electrolyzer.getId())[hour] += data.getGridConstants().getWindDailyProduction()[hour];
        }
        return actionCost;
    }

    private EnergySource getOrCreate(Electrolyzer electrolyzer, EnergySourceType pv) {
        EnergySource energySource = findEnergySource(electrolyzer, pv);
        if (energySource == null) {
            energySource = new EnergySource();
            energySource.setType(pv);
            energySource.setDistance(0.d);
            energySource.setMaxPower(0.d);
            electrolyzer.getSources().add(energySource);
        }
        return energySource;
    }

    private EnergySource findEnergySource(Electrolyzer electrolyzer, EnergySourceType x) {
        return electrolyzer.getSources().stream()
                .filter(energy -> energy.getType().equals(x))
                .findFirst()
                .orElse(null);
    }

}
