package arp.service;

import arp.dto.grid.Electrolyzer;
import arp.dto.grid.Storage;
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
        Map<Long, AcumulatorState> newAcumulatorStates = new HashMap<>();
        Map<Long, StorageState> newStorageStates = new HashMap<>();
        double overflowPowerProduction = 0;
        for (Storage storage : data.storages) {
            double hydrogenLevel = Math.max(step.storageStates.get(storage.getId()).currentLevel, 0);
            hydrogenLevel = calculateStorageLoss(hydrogenLevel, data.gridConstants.getStorageLoss());
            for (Electrolyzer electrolyzer : storage.getElectrolyzers()) {
                double newAccumulatorCurrentLevel = step.acumulatorsStates.get(electrolyzer.getId()).accumulatorCurrentLevel + data.summaryEnergyProduction.get(electrolyzer.getId())[step.hour];
                if (newAccumulatorCurrentLevel < electrolyzer.getMinPower()) {
                    throw new BusinessException("Luck of power on Electrolyzer: " + hour + " power: " + newAccumulatorCurrentLevel, FailureReason.LUCK_OF_POWER_ON_ELECTROLIZER);
                }
                double usedPower = Math.min(electrolyzer.getMaxPower(), newAccumulatorCurrentLevel);
                newAccumulatorCurrentLevel -= usedPower;
                if (newAccumulatorCurrentLevel > electrolyzer.getAccumulator().getAccumulatorMaxSize()) {
                    overflowPowerProduction += newAccumulatorCurrentLevel - electrolyzer.getAccumulator().getAccumulatorMaxSize();
                    newAccumulatorCurrentLevel = electrolyzer.getAccumulator().getAccumulatorMaxSize();
                }
                hydrogenLevel += usedPower * electrolyzer.getEfficiency();
                newAcumulatorStates.put(electrolyzer.getId(), new AcumulatorState(newAccumulatorCurrentLevel));
            }
            newStorageStates.put(storage.getId(), new StorageState());
        }
//        for (Map.Entry<Long, ElectrolyzerState> entry : step.electorizersStates.entrySet()) {
//            Electrolyzer electrolyzer = entry.getKey();
//            double newAccumulatorCurrentLevel = entry.getValue().accumulatorCurrentLevel + electrolyzer.summaryEnergyProduction[step.hour];
//            if (newAccumulatorCurrentLevel < electrolyzer.minPower) {
//                throw new BusinessException("Luck of power on Electrolyzer: " + hour + " power: " + newAccumulatorCurrentLevel, FailureReason.LUCK_OF_POWER_ON_ELECTROLIZER);
//            }
//            double usedPower = Math.min(electrolyzer.maxPower, newAccumulatorCurrentLevel);
//            newAccumulatorCurrentLevel -= usedPower;
//            if (newAccumulatorCurrentLevel > electrolyzer.accumulatorMaxSize) {
//                overflowPowerProduction += newAccumulatorCurrentLevel - electrolyzer.accumulatorMaxSize;
//                newAccumulatorCurrentLevel = electrolyzer.accumulatorMaxSize;
//            }
//            hydrogenLevel += usedPower * electrolyzer.efficiency;
//            newElectrolyzerStates.put(electrolyzer, new ElectrolyzerState(newAccumulatorCurrentLevel));
//        }
//        newStep.overflowPowerProduction = overflowPowerProduction;
//        newStep.electorizersStates = newElectrolyzerStates;
//        hydrogenLevel -= data.vehiclesConsumption[hour];

        double overFlowHydrogenProduction = 0;
        if (hydrogenLevel > data.summaryStorage.maxCapacity) {
            overFlowHydrogenProduction += hydrogenLevel - data.summaryStorage.maxCapacity;
            hydrogenLevel = data.summaryStorage.maxCapacity;
        }
        newStep.overflowHydrogenProduction = overFlowHydrogenProduction;

        newStep.storageStates = new StorageState(hydrogenLevel);

        return newStep;
    }

    private double calculateStorageLoss(double hydrogenLevel, double storageLoss) {
        if (hydrogenLevel <= 0) {
            return hydrogenLevel;
        }
        return hydrogenLevel * (1.0 - storageLoss / 24.0);
    }
}
