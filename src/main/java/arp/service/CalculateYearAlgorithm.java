package arp.service;

import arp.dto.grid.Electrolyzer;
import arp.dto.grid.Storage;
import arp.dto.warming.BusinessError;
import arp.dto.warming.Warning;
import arp.exception.BusinessException;
import arp.exception.FailureReason;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

        System.out.print("START CALCULATE WHOLE YEAR ");
        for (int hour = 1; hour < getHoursOfSimulation(data); ++hour) {
            if (hour % 1000 == 0)
                System.out.print(".");
            try {
                Step newStep = calculateNextStepAlgorithm.calculate(step);
                steps.add(newStep);
                minHourHydrogenLevel = Math.min(minHourHydrogenLevel, newStep.getStorageStates().values().stream().mapToDouble(StorageState::getCurrentLevel).sum());
                sumHydrogenOverflow += newStep.getOverflowHydrogenProduction();
                sumPowerOverflow += newStep.getOverflowPowerProduction();
                step = newStep;
            } catch (BusinessException exception) {
                switch (exception.type) {
                    case LUCK_OF_POWER_ON_ELECTROLIZER:
                        errors.add(new BusinessError(FailureReason.LUCK_OF_POWER_ON_ELECTROLIZER, "Luck of power on electrolizer: failure"));
                        break;
                }
                break;
            }
        }
        System.out.println(" FINISH");
        finalValidation(minHourHydrogenLevel, sumHydrogenOverflow, sumPowerOverflow);
        return new YearResult(minHourHydrogenLevel, steps, sumHydrogenOverflow, sumPowerOverflow, warnings, errors);
    }

    private void finalValidation(double minHourHydrogenLevel, double sumHydrogenOverflow, double sumPowerOverflow) {
        if (minHourHydrogenLevel < 0) {
            warnings.add(new Warning("There biggest luck of hydrogen was: " + Utils.standardRound(-minHourHydrogenLevel)));
        }
        if (minHourHydrogenLevel > 10) {
            warnings.add(new Warning("For whole year there was at least: " + Utils.standardRound(minHourHydrogenLevel) + " kg of hydroxen in storage"));
        }
        if (sumHydrogenOverflow > 0) {
            warnings.add(new Warning("In whole year we lost: " + Utils.standardRound(sumHydrogenOverflow) + " kg of hydroxen because luck of storage"));
        }
        if (sumPowerOverflow > 0) {
            warnings.add(new Warning("In whole year we lost: " + Utils.standardRound(sumPowerOverflow) + " MWh because luck on accumulator size"));
        }
    }

    private Step initializeFirstStep() {
        Step firstStep = new Step();
        firstStep.setHour(0);
        firstStep.setAcumulatorsStates(new HashMap<>());
        for (Storage storage : data.getStorages()) {
            for (Electrolyzer electrolyzer : storage.getElectrolyzers()) {
                firstStep.getAcumulatorsStates().put(electrolyzer.getAccumulator(), new AcumulatorState(0));
            }
        }
        firstStep.setStorageStates(data.getStorages().stream().collect(Collectors.toMap(storage -> storage, storage -> new StorageState(0))));
        firstStep.setOverflowHydrogenProduction(0);
        firstStep.setOverflowPowerProduction(0);
        return firstStep;
    }
}
