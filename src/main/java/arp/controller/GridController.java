package arp.controller;

import arp.dto.GridInput;
import arp.dto.GridResult;
import arp.service.GridService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GridController {
    private final GridService gridService;

    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @Operation(summary = "Validate grid given as an input")
    @GetMapping("/validateGrid")
    public GridResult validateGrid(@Parameter(description = "Input grid") GridInput gridInput) {
        gridService.runSimulation(gridInput);
        return new GridResult();
    }

    @Operation(summary = "Calculate minimal hydrogen production during year")
    @GetMapping("/hydrogenProduction")
    public GridResult hydrogenProduction(@Parameter(description = "Input grid") GridInput gridInput) {
        gridService.runSimulation(gridInput);
        return new GridResult();
    }

    @Operation(summary = "Calculate minimal CAPEX (grid investment cost)")
    @GetMapping("/minCapex")
    public GridResult minCapex(@Parameter(description = "Input grid") GridInput gridInput) {
        gridService.runSimulation(gridInput);
        return new GridResult();
    }
}
