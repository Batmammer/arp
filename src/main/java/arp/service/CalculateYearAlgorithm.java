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

import static arp.exception.FailureReason.LACK_OF_HYDROGEN;
import static arp.service.Utils.getHoursOfSimulation;

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
        double minHourHydrogenLevel = step.getStorageStates().values().stream().mapToDouble(StorageState::getCurrentLevel).sum();
        double sumHydrogenOverflow = 0;
        double sumPowerOverflow = 0;
        List<Step> steps = new ArrayList<>();
        steps.add(step);

        for (int hour = 1; hour < getHoursOfSimulation(data); ++hour) {
            try {
                Step newStep = calculateNextStepAlgorithm.calculate(step);
                steps.add(newStep);
                minHourHydrogenLevel = Math.min(minHourHydrogenLevel, newStep.getStorageStates().values().stream().mapToDouble(StorageState::getCurrentLevel).sum());
                sumHydrogenOverflow += newStep.getOverflowHydrogenProduction();
                sumPowerOverflow += newStep.getOverflowPowerProduction();
                step = newStep;
            } catch (BusinessException exception) {
                switch (exception.type) {
                    case LACK_OF_POWER_ON_ELECTROLIZER:
                        errors.add(new BusinessError(FailureReason.LACK_OF_POWER_ON_ELECTROLIZER, "Lack of power on electrolizer: failure"));
                        break;
                }
                break;
            }
        }
        finalValidation(minHourHydrogenLevel, sumHydrogenOverflow, sumPowerOverflow, step.getTotalHydrogenWasted());
        return new YearResult(minHourHydrogenLevel, steps, sumHydrogenOverflow, sumPowerOverflow, warnings, errors, step.getTotalHydrogenWasted());
    }

    private void finalValidation(double minHourHydrogenLevel, double sumHydrogenOverflow, double sumPowerOverflow, double totalHydrogenWasted) {
        if (minHourHydrogenLevel < 0) {
            errors.add(new BusinessError(LACK_OF_HYDROGEN, "During the year lowest hydrogen level during was: " + Utils.standardRound(minHourHydrogenLevel)));
        }
        if (minHourHydrogenLevel > 10) {
            warnings.add(new Warning("During the year there was at least: " + Utils.standardRound(minHourHydrogenLevel) + " kg of hydrogen in storage"));
        }
        if (sumHydrogenOverflow > 0) {
            warnings.add(new Warning("During the year grid lost: " + Utils.standardRound(sumHydrogenOverflow) + " kg of hydrogen because of low storage capacity"));
        }
        if (sumPowerOverflow > 0) {
            warnings.add(new Warning("During the year grid lost: " + Utils.standardRound(sumPowerOverflow) + " MWh because low accumulator capacity"));
        }
        if (totalHydrogenWasted > 0) {
            warnings.add(new Warning("During the year grid lost: " + Utils.standardRound(totalHydrogenWasted) + " kg of hydrogen in evaporation process"));
        }
    }

    private Step initializeFirstStep() {
        Step firstStep = new Step();
        firstStep.setHour(0);
        firstStep.setAccumulatorsStates(new HashMap<>());
        for (Storage storage : data.getStorages()) {
            for (Electrolyzer electrolyzer : storage.getElectrolyzers()) {
                firstStep.getAccumulatorsStates().put(electrolyzer.getAccumulator(), new AccumulatorState(0));
            }
        }
        firstStep.setStorageStates(data.getStorages().stream().collect(Collectors.toMap(storage -> storage, storage -> new StorageState(0))));
        firstStep.setOverflowHydrogenProduction(0);
        firstStep.setOverflowPowerProduction(0);
        firstStep.setTotalHydrogenWasted(0);
        return firstStep;
    }
}
