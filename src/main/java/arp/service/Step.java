package arp.service;

import arp.dto.grid.Accumulator;
import arp.dto.grid.Storage;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Step {
    private int hour;
    private Map<Accumulator, AccumulatorState> accumulatorsStates = new HashMap<>();
    private Map<Storage, StorageState> storageStates = new HashMap<>();
    private double overflowHydrogenProduction;
    private double overflowPowerProduction;
    private double hydrogenProduction;
    private double electricityProduction;
    private double totalHydrogenWasted;

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[hour=" + hour + ", electorizersStates=");
        b.append(accumulatorsStates.entrySet().stream()
                .map(e -> e.getKey() + " => " + Utils.toString(e.getValue().getAccumulatorCurrentLevel()))
                .collect(Collectors.joining(", ", "{", "},")));
        b.append(storageStates.entrySet().stream()
                .map(e -> e.getKey() + " => " + Utils.toString(e.getValue().getCurrentLevel()))
                .collect(Collectors.joining(", ", "{", "},")));
        b.append("setOverflowHydrogenProduction(" + Utils.toString(overflowHydrogenProduction) + ",");
        b.append("setOverflowPowerProduction(" + Utils.toString(overflowPowerProduction) + "]");
        return b.toString();
    }

    public void setOverflowHydrogenProduction(double overflowHydrogenProduction) {
        this.overflowHydrogenProduction = Utils.standardRound(overflowHydrogenProduction);
    }

    public void setOverflowPowerProduction(double overflowPowerProduction) {
        this.overflowPowerProduction = Utils.standardRound(overflowPowerProduction);
    }

    public void setHydrogenProduction(double hydrogenProduction) {
        this.hydrogenProduction = Utils.standardRound(hydrogenProduction);
    }

    public void setElectricityProduction(double electricityProduction) {
        this.electricityProduction = Utils.standardRound(electricityProduction);
    }

    public void setTotalHydrogenWasted(double totalHydrogenWasted) {
        this.totalHydrogenWasted = Utils.standardRound(totalHydrogenWasted);
    }
}
