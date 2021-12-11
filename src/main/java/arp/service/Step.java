package arp.service;

import arp.dto.grid.Accumulator;
import arp.dto.grid.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Step {
    public int hour;
    public Map<Accumulator, AcumulatorState> acumulatorsStates = new HashMap<>();
    public Map<Storage, StorageState> storageStates = new HashMap<>();
    public double overflowHydrogenProduction;
    public double overflowPowerProduction;

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[hour=" + hour + ", electorizersStates=");
        b.append(acumulatorsStates.entrySet().stream()
                .map(e -> e.getKey() + " => " + e.getValue().getAccumulatorCurrentLevel())
                .collect(Collectors.joining(", ", "{", "},")));
        b.append(storageStates.entrySet().stream()
                .map(e -> e.getKey() + " => " + e.getValue().getCurrentLevel())
                .collect(Collectors.joining(", ", "{", "},")));
        b.append("overflowHydrogenProduction = " + toString(overflowHydrogenProduction) + ",");
        b.append("overflowPowerProduction = " + toString(overflowPowerProduction) + "]");
        return b.toString();
    }

    public String toString(double value) {
        return String.format("%.2f", value);
    }
}
