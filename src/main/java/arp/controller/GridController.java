package arp.controller;

import arp.dto.GridInput;
import arp.dto.GridResult;
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
    public GridResult calculateGrid(GridInput gridInput) {
        gridService.runSimulation(gridInput);
        return new GridResult();
    }
}
