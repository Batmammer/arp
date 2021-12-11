package arp.service;

import arp.dto.warming.BusinessError;
import arp.dto.warming.Warning;
import lombok.Data;

import java.util.List;

@Data
public class YearResult {
    private double minHourHydrogenLevel;
    private List<Step> steps;
    private double sumHydrogenOverflow;
    private double sumPowerOverflow;
    private List<Warning> warnings;
    private List<BusinessError> errors;
    private double totalHydrogenWasted;

    public YearResult(double minHourHydrogenLevel, List<Step> steps, double sumHydrogenOverflow, double sumPowerOverflow, List<Warning> warnings, List<BusinessError> errors, double totalHydrogenWasted) {
        this.minHourHydrogenLevel = minHourHydrogenLevel;
        this.steps = steps;
        this.sumHydrogenOverflow = sumHydrogenOverflow;
        this.sumPowerOverflow = sumPowerOverflow;
        this.warnings = warnings;
        this.errors = errors;
        this.totalHydrogenWasted = totalHydrogenWasted;
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

    public void setSumHydrogenOverflow(double sumHydrogenOverflow) {
        this.sumHydrogenOverflow = Utils.standardRound(sumHydrogenOverflow);
    }

    public void setSumPowerOverflow(double sumPowerOverflow) {
        this.sumPowerOverflow = Utils.standardRound(sumPowerOverflow);
    }

    public void setTotalHydrogenWasted(double totalHydrogenWasted) {
        this.totalHydrogenWasted = Utils.standardRound(totalHydrogenWasted);
    }
}
