package arp.dto;

import arp.dto.grid.Grid;
import arp.service.Utils;
import lombok.Data;

import java.util.List;

@Data
public class GridResult {
    private Grid grid;
    private List<Double> electricityProduction;
    private List<Double> hydrogenProduction;
    private List<Double> hydrogenLevel;
    private Double maxVehicleConsumption;
    private Double minHydrogenProduction;
    private Double minCapex;
    private Double resMaxPower;
    private Double resAnnualCapacity;
    private Boolean isValid;

    public void setMaxVehicleConsumption(Double maxVehicleConsumption) {
        this.maxVehicleConsumption = Utils.standardRound(maxVehicleConsumption);
    }

    public void setMinHydrogenProduction(Double minHydrogenProduction) {
        this.minHydrogenProduction = Utils.standardRound(minHydrogenProduction);
    }

    public void setMinCapex(Double minCapex) {
        this.minCapex = Utils.standardRound(minCapex);
    }

    public void setResMaxPower(Double resMaxPower) {
        this.resMaxPower = Utils.standardRound(resMaxPower);
    }

    public void setResAnnualCapacity(Double resAnnualCapacity) {
        this.resAnnualCapacity = Utils.standardRound(resAnnualCapacity);
    }
}
