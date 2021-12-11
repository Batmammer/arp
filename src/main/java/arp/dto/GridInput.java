package arp.dto;

import arp.dto.grid.Grid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridInput {

    @Schema(description = "Grid graph", required = true)
    private Grid grid;

    @Schema(description = "Grid constants", required = true)
    private GridConstants constants;

    @Schema(description = "Grid elements costs", required = true)
    private GridCosts costs;
}
