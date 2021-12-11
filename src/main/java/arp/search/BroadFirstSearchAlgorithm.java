package arp.search;

import arp.enums.EnergySourceType;
import arp.exception.BusinessException;
import arp.service.CalculateYearAlgorithm;
import arp.service.Data;
import arp.service.Utils;
import arp.service.YearResult;

import java.util.*;

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
        return new State(data.summaryStorage, yearResult.isGood(), yearResult.minHourHydrogenLevel, null, null, 0, 0);
    }

    private State getNextState(State state, ActionType actionType) {
        Storage nextStorage = state.storage.clone();
        double totalCost = state.totalCost;
        double actionCost = 0;

        if (nextStorage.electrolyzers.isEmpty()) {
            nextStorage.electrolyzers.add(createNewElectorlyzer());
            actionCost = data.gridConstants.electrolizerCost;
        } else {
            Electrolyzer electrolyzer = nextStorage.electrolyzers.get(0);
            switch (actionType) {
                case WIND:
                    actionCost = addWind(electrolyzer);
                    break;
                case PV:
                    actionCost = addPv(electrolyzer);
                    break;
                case ELECTROLIZER:
                    actionCost = data.gridConstants.electrolizerCost;
                    electrolyzer.maxPower += 1.0;
                    break;
                case STORAGE_POWER:
                    actionCost = data.gridConstants.storagePowerCost;
                    electrolyzer.accumulatorMaxSize += 1.0;
                    break;
                case STORAGE_HYDROGEN:
                    actionCost = data.gridConstants.storageHydrogenCost;
                    nextStorage.maxCapacity += 1.0;
                    break;
            }
        }

        totalCost += actionCost;
        Data newData = new Data(data.gridConstants, nextStorage, data.vehiclesConsumption);
        CalculateYearAlgorithm calculateYearAlgorithm = new CalculateYearAlgorithm(newData);
        YearResult yearResult = calculateYearAlgorithm.calculate();
        State newState = new State(nextStorage, yearResult.isGood(), yearResult.minHourHydrogenLevel, state, actionType, actionCost, totalCost);
        System.out.println("\tADDING NEW STATE: " + yearResult.isGood() + ": " + yearResult + ": " + newState.toString());
        return newState;
    }

    private Electrolyzer createNewElectorlyzer() {
        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.efficiency = data.gridConstants.electrolizerEfficiency;
        electrolyzer.summaryEnergyProduction = createTableOfValue(0.0);
        electrolyzer.maxPower = 1.0;
        return electrolyzer;
    }

    private double addPv(Electrolyzer electrolyzer) {
        double actionCost = data.gridConstants.pvCost;

        EnergySource energySource = getOrCreate(electrolyzer, EnergySourceType.PV);
        energySource.maxPower += 1.0;

        for (int hour = 0; hour < Utils.HOURS_OF_YEAR; ++hour) {
            electrolyzer.summaryEnergyProduction[hour] += data.gridConstants.pvDailyProduction[hour];
        }
        return actionCost;
    }

    private double addWind(Electrolyzer electrolyzer) {
        double actionCost = data.gridConstants.windCost;

        EnergySource energySource = getOrCreate(electrolyzer, EnergySourceType.WIND);
        energySource.maxPower += 1.0;

        for (int hour = 0; hour < Utils.HOURS_OF_YEAR; ++hour) {
            electrolyzer.summaryEnergyProduction[hour] += data.gridConstants.windDailyProduction[hour];
        }
        return actionCost;
    }

    private EnergySource getOrCreate(Electrolyzer electrolyzer, EnergySourceType pv) {
        EnergySource energySource = findEnergySource(electrolyzer, pv);
        if (energySource == null) {
            energySource = new EnergySource();
            energySource.type = pv;
            energySource.distance = 0.d;
            energySource.maxPower = 0.d;
            electrolyzer.sources.add(energySource);
        }
        return energySource;
    }

    private EnergySource findEnergySource(Electrolyzer electrolyzer, EnergySourceType x) {
        return electrolyzer.sources.stream()
                .filter(energy -> energy.type.equals(x))
                .findFirst()
                .orElse(null);
    }

}
