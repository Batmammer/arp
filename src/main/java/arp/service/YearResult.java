package arp.service;

import arp.dto.warming.BusinessError;
import arp.dto.warming.Warning;

import java.util.List;

public class YearResult {
    public double minHourHydrogenLevel;
    public List<Step> steps;
    public double sumHydrogenOverflow;
    public double sumPowerOverflow;
    public List<Warning> warnings;
    public List<BusinessError> errors;

    public YearResult(double minHourHydrogenLevel, List<Step> steps, double sumHydrogenOverflow, double sumPowerOverflow, List<Warning> warnings, List<BusinessError> errors) {
        this.minHourHydrogenLevel = minHourHydrogenLevel;
        this.steps = steps;
        this.sumHydrogenOverflow = sumHydrogenOverflow;
        this.sumPowerOverflow = sumPowerOverflow;
        this.warnings = warnings;
        this.errors = errors;
    }

    public boolean isGood() {
        return errors.size() == 0 && minHourHydrogenLevel >= 0;
    }

    @Override
    public String toString() {
        return "YearResult{" +
                "minHourHydrogenLevel=" + minHourHydrogenLevel +
                ", sumHydrogenOverflow=" + sumHydrogenOverflow +
                ", sumPowerOverflow=" + sumPowerOverflow +
                '}';
    }
}
