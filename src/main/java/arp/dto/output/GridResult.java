package arp.dto.output;

import lombok.Data;

import java.util.List;

@Data
public class GridResult {
    private GridOutput grid;
    private List<Double> electricityProduction;
    private List<Double> hydrogenProduction;
    private List<Double> hydrogenLevel;
    private Double maxVehicleConsumption;
    private Double minHydrogenProduction;
    private Double minCapex;
    private Double resMaxPower;
    private Double resAnnualCapacity;
}
