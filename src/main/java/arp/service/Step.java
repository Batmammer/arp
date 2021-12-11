package arp.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Step {
    public int hour;
    public Map<Electrolyzer, ElectrolyzerState> electorizersStates = new HashMap<>();
    public StorageState storageState;
    public double overflowHydrogenProduction;
    public double overflowPowerProduction;

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[hour=" + hour + ", electorizersStates=");
        b.append(electorizersStates.entrySet().stream()
                .map(e -> e.getKey().no + " => " + e.getValue().accumulatorCurrentLevel)
                .collect(Collectors.joining(", ", "{", "},")));
        b.append("storageState = " + toString(storageState.currentLevel) + ",");
        b.append("overflowHydrogenProduction = " + toString(overflowHydrogenProduction) + ",");
        b.append("overflowPowerProduction = " + toString(overflowPowerProduction) + "]");
        return b.toString();
    }

    public String toString(double value) {
        return String.format("%.2f", value);
    }
}
