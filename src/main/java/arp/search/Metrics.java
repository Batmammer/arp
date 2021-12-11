package arp.search;

import arp.service.Utils;
import lombok.Data;

@Data
public class Metrics implements Comparable<Metrics> {
    private boolean good;
    private double minHourHydrogenLevel;
    private double totalCost;

    @Override
    public String toString() {
        return Utils.roundDouble(totalCost);
    }

    @Override
    public int compareTo(Metrics o) {
        return Double.compare(totalCost, o.totalCost);
    }
}
