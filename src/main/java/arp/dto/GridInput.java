package arp.dto;

import arp.dto.grid.Grid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridInput {
    private Grid grid;
    private GridConstants constants;
    private GridCosts costs;
}
