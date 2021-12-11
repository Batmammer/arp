package arp.service;

import java.util.List;

public class YearResult {
    public boolean good;
    public double minHourHydrogenLevel;
    public List<Step> steps;
    public double sumHydrogenOverflow;
    public double sumPowerOverflow;

    public YearResult(boolean good, double minHourHydrogenLevel, List<Step> steps, double sumHydrogenOverflow, double sumPowerOverflow) {
        this.good = good;
        this.minHourHydrogenLevel = minHourHydrogenLevel;
        this.steps = steps;
        this.sumHydrogenOverflow = sumHydrogenOverflow;
        this.sumPowerOverflow = sumPowerOverflow;
    }
}
