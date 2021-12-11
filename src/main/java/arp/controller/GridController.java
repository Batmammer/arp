package arp.controller;

import arp.dto.InputGrid;
import arp.dto.output.GridResult;
import arp.service.GridService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GridController {
    private final GridService gridService;

    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @GetMapping("/calculate")
    public GridResult calculateGrid(InputGrid inputGrid) {
        gridService.runSimulation(inputGrid);
        return new GridResult();
    }
}
