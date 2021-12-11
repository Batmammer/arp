package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridCosts {
    private double pvCost;
    private double windCost;
    private double storagePowerCost;
    private double electrolyzerCost;
    private double storageHydrogenCost;
}
