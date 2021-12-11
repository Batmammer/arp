package arp.controller;

import arp.dto.GridInput;
import arp.dto.GridResult;
import arp.service.GridService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GridController {
    private final GridService gridService;

    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @Operation(summary = "Validate grid given as an input")
    @PutMapping("/validateGrid")
    public GridResult validateGrid(@RequestBody GridInput gridInput) {
        gridService.runSimulation(gridInput);
        return new GridResult();
    }

    @Operation(summary = "Calculate minimal hydrogen production during year")
    @PutMapping("/hydrogenProduction")
    public GridResult hydrogenProduction(@RequestBody GridInput gridInput) {
        gridService.runSimulation(gridInput);
        return new GridResult();
    }

    @Operation(summary = "Calculate minimal CAPEX (grid investment cost)")
    @PutMapping("/minCapex")
    public GridResult minCapex(@RequestBody GridInput gridInput) {
        gridService.runSimulation(gridInput);
        return new GridResult();
    }
}
