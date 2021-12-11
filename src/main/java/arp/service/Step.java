package arp.service;

import arp.dto.Electrolyzer;
import arp.dto.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Step {
    public int hour;
    public Map<Electrolyzer, ElectrolyzerState> electorizersStates = new HashMap<>();
    public StorageState storageState;
    public double overflowHydrogenProduction;
    public double overflowPowerProduction;
    public double storageLuck;

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[hour=" + hour + ", electorizersStates=");
        b.append(electorizersStates.entrySet().stream()
                .map(e -> e.getKey().lp + " => " + e.getValue().accumulatorCurrentLevel)
                .collect(Collectors.joining(", ", "{", "},")));
        b.append("storageState = " + storageState.currentLevel + "]");
        return b.toString();
    }
}
