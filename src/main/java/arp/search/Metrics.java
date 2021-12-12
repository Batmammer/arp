package arp.search;

import arp.service.Utils;
import arp.service.YearResult;
import lombok.Data;

@Data
public class Metrics implements Comparable<Metrics> {
    private boolean good;
    private double minHourHydrogenLevel;
    private double totalCost;
    private double hydrogenProduction;
    private double electricityProduction;
    private double totalHydrogenWasted;
    private YearResult yearResult;

    @Override
    public String toString() {
        return Utils.roundDouble(totalCost);
    }

    @Override
    public int compareTo(Metrics o) {
        return Double.compare(totalCost, o.totalCost);
    }
}
