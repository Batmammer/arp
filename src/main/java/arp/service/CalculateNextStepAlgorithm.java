package arp.service;

import arp.dto.grid.Accumulator;
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
        Map<Accumulator, AcumulatorState> newAcumulatorStates = new HashMap<>();
        Map<Long, StorageState> newStorageStates = new HashMap<>();
        double overflowPowerProduction = 0;
        for (Storage storage : data.storages) {
            double hydrogenLevel = Math.max(step.storageStates.get(storage).currentLevel, 0);
            hydrogenLevel = calculateStorageLoss(hydrogenLevel, data.gridConstants.getStorageLoss());
            for (Electrolyzer electrolyzer : storage.getElectrolyzers()) {
                double newAccumulatorCurrentLevel = step.acumulatorsStates.get(electrolyzer.getAccumulator()).accumulatorCurrentLevel + data.summaryEnergyProduction.get(electrolyzer.getId())[step.hour];
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
                newAcumulatorStates.put(electrolyzer.getAccumulator(), new AcumulatorState(newAccumulatorCurrentLevel));
            }
            newStorageStates.put(storage.getId(), new StorageState(hydrogenLevel));
        }
        newStep.acumulatorsStates = newAcumulatorStates;
        newStep.storageStates = newStorageStates;
        newStep.overflowPowerProduction = overflowPowerProduction;
        double neededHydrogen = data.vehiclesConsumption[hour];
        double currentHydrogen = newStorageStates.values().stream().mapToDouble(storageState -> storageState.currentLevel).sum();
        double ratio = 1 - neededHydrogen / currentHydrogen;
        newStorageStates.values().forEach(storageState -> storageState.setCurrentLevel(ratio * storageState.getCurrentLevel()));

        double overFlowHydrogenProduction = 0;
        for (Storage storage: data.storages) {
            if (newStorageStates.get(storage.getId()).getCurrentLevel() > storage.getMaxCapacity()) {
                overFlowHydrogenProduction += newStorageStates.get(storage.getId()).getCurrentLevel() - storage.getMaxCapacity();
                newStorageStates.get(storage.getId()).setCurrentLevel(storage.getMaxCapacity());
            }
        }
        newStep.overflowHydrogenProduction = overFlowHydrogenProduction;
        newStep.storageStates = newStorageStates;

        return newStep;
    }

    private double calculateStorageLoss(double hydrogenLevel, double storageLoss) {
        if (hydrogenLevel <= 0) {
            return hydrogenLevel;
        }
        return hydrogenLevel * (1.0 - storageLoss / 24.0);
    }
}
