package arp.controller;

import arp.dto.GridInput;
import arp.dto.GridResult;
import arp.search.State;
import arp.service.GridService;
import arp.service.YearResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GridController {
    private final GridService gridService;

    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @Operation(summary = "Validate grid given as an input")
    @PostMapping("/validateGrid")
    public GridResult validateGrid(@RequestBody GridInput gridInput) {
        YearResult yearResult = gridService.runSimulation(gridInput);
        GridResult gridResult = new GridResult();
        gridResult.setGrid(gridInput.getGrid());
        return gridResult;
    }

    @Operation(summary = "Calculate minimal hydrogen production during year")
    @PostMapping("/hydrogenProduction")
    public GridResult hydrogenProduction(@RequestBody GridInput gridInput) {
        Double minHydrogenProduction = gridService.calculateHydrogen(gridInput);
        GridResult gridResult = new GridResult();
        gridResult.setGrid(gridInput.getGrid());
        gridResult.setMinHydrogenProduction(minHydrogenProduction);
        return gridResult;
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
