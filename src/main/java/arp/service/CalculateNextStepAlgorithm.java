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
        int hour = step.getHour();
        Step newStep = new Step();
        newStep.setHour(hour + 1);
        Map<Accumulator, AccumulatorState> newAcumulatorStates = new HashMap<>();
        Map<Storage, StorageState> newStorageStates = new HashMap<>();
        double overflowPowerProduction = 0;
        double totalHydrogenStorageLoss = 0;
        double electricityProduction = 0;
        double hydrogenProduction = 0;
        for (Storage storage : data.getStorages()) {
            double hydrogenLevel = Math.max(step.getStorageStates().get(storage).getCurrentLevel(), 0);
            double hydrogenStorageLoss = calculateStorageLoss(hydrogenLevel, data.getGridConstants().getStorageLoss());
            totalHydrogenStorageLoss += hydrogenStorageLoss;
            hydrogenLevel -= hydrogenStorageLoss;
            for (Electrolyzer electrolyzer : storage.getElectrolyzers()) {
                double newAccumulatorCurrentLevel = step.getAccumulatorsStates().get(electrolyzer.getAccumulator()).getAccumulatorCurrentLevel() +
                        electrolyzer.getSummaryEnergyProduction(data, step.getHour());
                electricityProduction += electrolyzer.getSummaryEnergyProduction(data, step.getHour());
                if (newAccumulatorCurrentLevel < electrolyzer.getMinPower()) {
                    throw new BusinessException("Lack of power on Electrolyzer: " + hour + " power: " + newAccumulatorCurrentLevel, FailureReason.LACK_OF_POWER_ON_ELECTROLIZER);
                }
                double usedPower = Math.min(electrolyzer.getMaxPower(), newAccumulatorCurrentLevel);
                newAccumulatorCurrentLevel -= usedPower;
                if (newAccumulatorCurrentLevel > electrolyzer.getAccumulator().getAccumulatorMaxSize()) {
                    overflowPowerProduction += newAccumulatorCurrentLevel - electrolyzer.getAccumulator().getAccumulatorMaxSize();
                    newAccumulatorCurrentLevel = electrolyzer.getAccumulator().getAccumulatorMaxSize();
                }
                hydrogenProduction += usedPower * electrolyzer.getEfficiency();
                hydrogenLevel += usedPower * electrolyzer.getEfficiency();
                newAcumulatorStates.put(electrolyzer.getAccumulator(), new AccumulatorState(newAccumulatorCurrentLevel));
            }
            newStorageStates.put(storage, new StorageState(hydrogenLevel));
        }
        newStep.setAccumulatorsStates(newAcumulatorStates);
        newStep.setStorageStates(newStorageStates);
        newStep.setOverflowPowerProduction(overflowPowerProduction);
        newStep.setElectricityProduction(electricityProduction);
        newStep.setHydrogenProduction(hydrogenProduction);
        double neededHydrogen = data.getVehiclesConsumption()[hour];
        double currentHydrogen = newStorageStates.values().stream().mapToDouble(StorageState::getCurrentLevel).sum();
        if (currentHydrogen > 0) {
            double ratio = 1 - neededHydrogen / currentHydrogen;
            newStorageStates.values().forEach(storageState -> storageState.setCurrentLevel(ratio * storageState.getCurrentLevel()));
        } else {
            double part = neededHydrogen / newStorageStates.values().size();
            newStorageStates.values().forEach(storageState -> storageState.setCurrentLevel(storageState.getCurrentLevel() - part));
        }


        double overFlowHydrogenProduction = 0;
        for (Storage storage: data.getStorages()) {
            StorageState newStorageState = newStorageStates.get(storage);
            if (newStorageState.getCurrentLevel() > storage.getMaxCapacity()) {
                overFlowHydrogenProduction += newStorageState.getCurrentLevel() - storage.getMaxCapacity();
                newStorageState.setCurrentLevel(storage.getMaxCapacity());
            }
        }
        newStep.setOverflowHydrogenProduction(overFlowHydrogenProduction);
        newStep.setStorageStates(newStorageStates);
        newStep.setTotalHydrogenWasted(totalHydrogenStorageLoss);
        return newStep;
    }

    private double calculateStorageLoss(double hydrogenLevel, double storageLoss) {
        if (hydrogenLevel <= 0) {
            return hydrogenLevel;
        }
        return hydrogenLevel * (storageLoss / 24.0);
    }
}
