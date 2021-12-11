package arp.service;

import java.util.List;

public class YearResult {
    public double minHourHydrogenLevel;
    public List<Step> steps;
    public double sumHydrogenOverflow;
    public double sumPowerOverflow;

    public YearResult(double minHourHydrogenLevel, List<Step> steps, double sumHydrogenOverflow, double sumPowerOverflow) {
        this.minHourHydrogenLevel = minHourHydrogenLevel;
        this.steps = steps;
        this.sumHydrogenOverflow = sumHydrogenOverflow;
        this.sumPowerOverflow = sumPowerOverflow;
    }

    public boolean isGood() {
        return minHourHydrogenLevel >= 0;
    }

}
