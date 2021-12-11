package arp.service;

import arp.dto.Electrolyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static arp.service.Utils.HOURS_OF_YEAR;

public class CalculateYearAlgorithm {
    private final Data data;
    private final CalculateNextStepAlgorithm calculateNextStepAlgorithm;

    public CalculateYearAlgorithm(Data data) {
        this.data = data;
        this.calculateNextStepAlgorithm = new CalculateNextStepAlgorithm(data);
    }

    public YearResult calculate() {
        Step step = initializeFirstStep();
        double minHourHydrogenLevel = step.storageState.currentLevel;
        double sumHydrogenOverflow = 0;
        double sumPowerOverflow = 0;
        List<Step> steps = new ArrayList<>();
        steps.add(step);
        for (int hour = 1; hour < HOURS_OF_YEAR; ++hour) {
            Step newStep = calculateNextStepAlgorithm.calculate(step);
            steps.add(newStep);
            minHourHydrogenLevel = Math.min(minHourHydrogenLevel, newStep.storageState.currentLevel);
            sumHydrogenOverflow += newStep.overflowHydrogenProduction;
            sumPowerOverflow += newStep.overflowPowerProduction;
            step = newStep;
        }
        return new YearResult(minHourHydrogenLevel, steps, sumHydrogenOverflow, sumPowerOverflow);
    }

    private Step initializeFirstStep() {
        Step firstStep = new Step();
        firstStep.hour = 0;
        firstStep.electorizersStates = new HashMap<>();
        for (Electrolyzer electrolyzer : data.summaryStorage.electrolyzers) {
            firstStep.electorizersStates.put(electrolyzer, new ElectrolyzerState(0));
        }
        firstStep.storageState = new StorageState(0);
        firstStep.overflowHydrogenProduction = 0;
        firstStep.overflowPowerProduction = 0;
        return firstStep;
    }


}
