package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridConstants {
    public double hydrogenTransportLoss;
    public double storageLoss;
    public double transmissionLoss;
    public double pvCost;
    public double[] pvDailyProduction;
    public double windCost;
    public double[] windDailyProduction;
    public double storagePowerCost;
    public double electrolizerCost;
    public double storageHydrogenCost;

}
