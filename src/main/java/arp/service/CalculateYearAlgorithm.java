package arp.service;

import arp.dto.grid.Electrolyzer;
import arp.dto.grid.Storage;
import arp.dto.warming.BusinessError;
import arp.dto.warming.Warning;
import arp.exception.BusinessException;
import arp.exception.FailureReason;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static arp.service.Utils.HOURS_OF_YEAR;

public class CalculateYearAlgorithm {
    private final Data data;
    private final CalculateNextStepAlgorithm calculateNextStepAlgorithm;
    private List<Warning> warnings;
    private List<BusinessError> errors;

    public CalculateYearAlgorithm(Data data) {
        this.data = data;
        this.calculateNextStepAlgorithm = new CalculateNextStepAlgorithm(data);
        this.warnings = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public YearResult calculate() {
        Step step = initializeFirstStep();
        double minHourHydrogenLevel = step.storageStates.values().stream().mapToDouble(storageState -> storageState.currentLevel).sum();
        double sumHydrogenOverflow = 0;
        double sumPowerOverflow = 0;
        List<Step> steps = new ArrayList<>();
        steps.add(step);
        for (int hour = 1; hour < HOURS_OF_YEAR; ++hour) {
            try {
                Step newStep = calculateNextStepAlgorithm.calculate(step);
                steps.add(newStep);
                minHourHydrogenLevel = Math.min(minHourHydrogenLevel, newStep.storageStates.values().stream().mapToDouble(storageState -> storageState.currentLevel).sum());
                sumHydrogenOverflow += newStep.overflowHydrogenProduction;
                sumPowerOverflow += newStep.overflowPowerProduction;
                step = newStep;
            } catch (BusinessException exception) {
                switch (exception.type) {
                    case LUCK_OF_POWER_ON_ELECTROLIZER:
                        errors.add(new BusinessError(FailureReason.LUCK_OF_POWER_ON_ELECTROLIZER));
                        break;
                }
            }
        }
        finalValidation(minHourHydrogenLevel, sumHydrogenOverflow, sumPowerOverflow);
        return new YearResult(minHourHydrogenLevel, steps, sumHydrogenOverflow, sumPowerOverflow, warnings, errors);
    }

    private void finalValidation(double minHourHydrogenLevel, double sumHydrogenOverflow, double sumPowerOverflow) {
        if (minHourHydrogenLevel < 0) {

        }
    }

    private Step initializeFirstStep() {
        Step firstStep = new Step();
        firstStep.hour = 0;
        firstStep.acumulatorsStates = new HashMap<>();
        for (Storage storage: data.getStorages()) {
            for (Electrolyzer electrolyzer : storage.getElectrolyzers()) {
                firstStep.acumulatorsStates.put(electrolyzer.getAccumulator(), new AcumulatorState(0));
            }
        }
        firstStep.storageStates =  data.storages.stream().collect(Collectors.toMap(storage -> storage, storage -> new StorageState(0)));
        firstStep.overflowHydrogenProduction = 0;
        firstStep.overflowPowerProduction = 0;
        return firstStep;
    }


}
