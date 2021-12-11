package arp.search;

import arp.dto.Storage;
import arp.enums.EnergySourceType;
import arp.exception.BusinessException;
import arp.service.CalculateYearAlgorithm;
import arp.service.Data;
import arp.service.Utils;
import arp.service.YearResult;

import java.util.*;

import static arp.exception.FailureReason.SOLUTION_NOT_FOUND;

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

        switch (actionType) {
            case WIND:
                actionCost = data.gridConstants.WIND_COST;
                nextStorage.getElectrolyzers().get(0).sources.stream().filter(energy -> energy.getType().equals(EnergySourceType.WIND)).findFirst().get().maxPower += 1.0;
                for (int hour = 0; hour < Utils.HOURS_OF_YEAR; ++hour) {
                    nextStorage.electrolyzers.get(0).summaryEnergyProduction[hour] += 1.0;
                }
                break;
            case PV:
                actionCost = data.gridConstants.PV_COST;
                nextStorage.getElectrolyzers().get(0).sources.stream().filter(energy -> energy.getType().equals(EnergySourceType.PV)).findFirst().get().maxPower += 1.0;
                for (int hour = 0; hour < Utils.HOURS_OF_YEAR; ++hour) {
                    nextStorage.electrolyzers.get(0).summaryEnergyProduction[hour] += 1.0;
                }
                break;
            case ELECTROLIZER:
                actionCost = data.gridConstants.ELECTROLIZER_COST;
                nextStorage.electrolyzers.get(0).maxPower += 1.0;
                break;
            case STORAGE_POWER:
                actionCost = data.gridConstants.STORAGE_POWER_COST;
                nextStorage.electrolyzers.get(0).accumulatorMaxSize += 1.0;
                break;
            case STORAGE_HYDROGEN:
                actionCost = data.gridConstants.STORAGE_HYDROGEN_COST;
                nextStorage.maxCapacity += 1.0;
                break;
        }
        totalCost += actionCost;
        Data newData = new Data(data.gridConstants, nextStorage, data.vehiclesConsumption);
        CalculateYearAlgorithm calculateYearAlgorithm = new CalculateYearAlgorithm(newData);
        YearResult yearResult = calculateYearAlgorithm.calculate();
        State newState = new State(nextStorage, yearResult.isGood(), yearResult.minHourHydrogenLevel, state, actionType, actionCost, totalCost);
//        System.out.println("\tADDING NEW STATE: " + yearResult.isGood() + ": " + yearResult + ": " + newState.toString());
        return newState;
    }

}
