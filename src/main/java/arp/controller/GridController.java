package arp.controller;

import arp.dto.GridInput;
import arp.dto.GridResult;
import arp.dto.ValidationResult;
import arp.search.State;
import arp.service.GridService;
import arp.service.MaxConsumptionYearResult;
import arp.service.Step;
import arp.service.YearResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GridController {
    private final GridService gridService;

    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @Operation(summary = "Validate grid given as an input")
    @PostMapping("/validateGrid")
    public ValidationResult validateGrid(@RequestBody GridInput gridInput) {
        YearResult yearResult = gridService.runSimulation(gridInput);
        ValidationResult validationResult = new ValidationResult();
        validationResult.setGrid(gridInput.getGrid());
        validationResult.setIsValid(yearResult.isGood());
        double[] vehicleConsumption = gridService.calculateYearlyConsumption(gridInput.getGrid().getVehicles(),
                gridInput.getConstants().getHydrogenTransportLoss());
        validationResult.setMaxVehicleConsumption(Arrays.stream(vehicleConsumption).max().getAsDouble());
        List<Double> electricityProduction = new ArrayList<>();
        List<Double> hydrogenProduction = new ArrayList<>();
        List<Double> hydrogenLevel = new ArrayList<>();
        for (Step s : yearResult.getSteps()) {
            hydrogenProduction.add(s.getHydrogenProduction());
            electricityProduction.add(s.getElectricityProduction());
            hydrogenLevel.add(s.getStorageStates().values().stream().map(ss ->
                    ss.getCurrentLevel()).collect(Collectors.summingDouble(Double::doubleValue)));
        }
        validationResult.setMinHydrogenProduction(hydrogenProduction.stream().min(Double::compareTo).get());
        validationResult.setResMaxPower(electricityProduction.stream().max(Double::compareTo).get());
        validationResult.setResAnnualCapacity(electricityProduction.stream()
                .collect(Collectors.summingDouble(Double::doubleValue)));
        validationResult.setHydrogenProduction(hydrogenProduction);
        validationResult.setElectricityProduction(electricityProduction);
        validationResult.setErrors(yearResult.getErrors());
        validationResult.setWarnings(yearResult.getWarnings());
        validationResult.setHydrogenLevel(hydrogenLevel);
        return validationResult;
    }

    @Operation(summary = "Calculate minimal hydrogen production during year")
    @PostMapping("/hydrogenProduction")
    public ValidationResult hydrogenProduction(@RequestBody GridInput gridInput) {
        MaxConsumptionYearResult minHydrogenProduction = gridService.calculateHydrogen(gridInput);
        YearResult yearResult = minHydrogenProduction.getYearResult();
        ValidationResult validationResult = new ValidationResult();
        validationResult.setGrid(gridInput.getGrid());
        validationResult.setIsValid(yearResult.isGood());
        double[] vehicleConsumption = gridService.calculateYearlyConsumption(gridInput.getGrid().getVehicles(),
                gridInput.getConstants().getHydrogenTransportLoss());
        validationResult.setMaxVehicleConsumption(Arrays.stream(vehicleConsumption).max().getAsDouble());
        List<Double> electricityProduction = new ArrayList<>();
        List<Double> hydrogenProduction = new ArrayList<>();
        List<Double> hydrogenLevel = new ArrayList<>();
        for (Step s : yearResult.getSteps()) {
            hydrogenProduction.add(s.getHydrogenProduction());
            electricityProduction.add(s.getElectricityProduction());
            hydrogenLevel.add(s.getStorageStates().values().stream().map(ss ->
                    ss.getCurrentLevel()).collect(Collectors.summingDouble(Double::doubleValue)));
        }
        validationResult.setMinHydrogenProduction(minHydrogenProduction.getMaxConsumption());
        validationResult.setResMaxPower(electricityProduction.stream().max(Double::compareTo).get());
        validationResult.setResAnnualCapacity(electricityProduction.stream()
                .collect(Collectors.summingDouble(Double::doubleValue)));
        validationResult.setHydrogenProduction(hydrogenProduction);
        validationResult.setElectricityProduction(electricityProduction);
        validationResult.setErrors(yearResult.getErrors());
        validationResult.setWarnings(yearResult.getWarnings());
        validationResult.setHydrogenLevel(hydrogenLevel);
        return validationResult;
    }

    @Operation(summary = "Calculate minimal CAPEX (grid investment cost)")
    @PostMapping("/minCapex")
    public GridResult minCapex(@RequestBody GridInput gridInput) {
        State state = gridService.calculateCapex(gridInput);
        GridResult gridResult = new GridResult();
        gridResult.setGrid(gridInput.getGrid());
        return gridResult;
    }
}
