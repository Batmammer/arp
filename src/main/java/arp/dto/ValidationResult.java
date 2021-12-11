package arp.dto;

import arp.dto.grid.Grid;
import arp.dto.warming.BusinessError;
import arp.dto.warming.Warning;
import lombok.Data;

import java.util.List;

@Data
public class ValidationResult {
    private Grid grid;
    private List<Double> electricityProduction;
    private List<Double> hydrogenProduction;
    private List<Double> hydrogenLevel;
    private Double maxVehicleConsumption;
    private Double minHydrogenProduction;
    private Double resMaxPower;
    private Double resAnnualCapacity;
    private Boolean isValid;
    private List<Warning> warnings;
    private List<BusinessError> errors;
}
