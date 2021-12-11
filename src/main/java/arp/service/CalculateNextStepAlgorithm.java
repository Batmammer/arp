package arp.service;

import arp.dto.Electrolyzer;
import arp.exception.BusinessException;
import arp.exception.FailureReason;

import java.util.HashMap;
import java.util.Map;

public class CalculateNextStepAlgorithm {
    private final Data data;
    private Electrolyzer electrolyzer;

    CalculateNextStepAlgorithm(Data data) {
        this.data = data;
    }

    public Step calculate(Step step) {
        int hour = step.hour;
        Step newStep = new Step();
        newStep.hour = hour + 1;
        double hydrogenLevel = Math.max(step.storageState.currentLevel, 0);
        hydrogenLevel = calculateStorageLoss(hydrogenLevel, data.gridConstants.storageLoss);
        Map<Electrolyzer, ElectrolyzerState> newElectrolyzerStates = new HashMap<>();
        double overflowPowerProduction = 0;
        for (Map.Entry<Electrolyzer, ElectrolyzerState> entry : step.electorizersStates.entrySet()) {
            double newAccumulatorCurrentLevel = entry.getValue().accumulatorCurrentLevel + entry.getKey().summaryEnergyProduction[step.hour];
            if (newAccumulatorCurrentLevel < entry.getKey().minPower) {
                throw new BusinessException("Luck of power on Electrolyzer: " + hour + " power: " + newAccumulatorCurrentLevel, FailureReason.LUCK_OF_POWER_ON_ELECTROLIZER);
            }
            double usedPower = Math.min(entry.getKey().maxPower, newAccumulatorCurrentLevel);
            newAccumulatorCurrentLevel -= usedPower;
            if (newAccumulatorCurrentLevel > entry.getKey().accumulatorMaxSize) {
                overflowPowerProduction += newAccumulatorCurrentLevel - entry.getKey().accumulatorMaxSize;
                newAccumulatorCurrentLevel = entry.getKey().accumulatorMaxSize;
            }
            hydrogenLevel += usedPower * entry.getKey().efficiency;
            newElectrolyzerStates.put(entry.getKey(), new ElectrolyzerState(newAccumulatorCurrentLevel));
        }
        newStep.overflowPowerProduction = overflowPowerProduction;
        newStep.electorizersStates = newElectrolyzerStates;
        hydrogenLevel -= data.vehiclesConsumption[hour];

        double overFlowHydrogenProduction = 0;
        if (hydrogenLevel > data.summaryStorage.maxCapacity) {
            overFlowHydrogenProduction += hydrogenLevel - data.summaryStorage.maxCapacity;
            hydrogenLevel = data.summaryStorage.maxCapacity;
        }
        newStep.overflowHydrogenProduction = overFlowHydrogenProduction;

        newStep.storageState = new StorageState(hydrogenLevel);

        return newStep;
    }

    private double calculateStorageLoss(double hydrogenLevel, double storageLoss) {
        if (hydrogenLevel <= 0) {
            return hydrogenLevel;
        }
        return hydrogenLevel * (1.0 - storageLoss / 24.0);
    }
}
