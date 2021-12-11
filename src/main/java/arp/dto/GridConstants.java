package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridConstants {
    private double hydrogenTransportLoss;
    private double storageLoss;
    private double transmissionLoss;
    private double[] pvDailyProduction;
    private double[] windDailyProduction;
    private double electrolizerEfficiency;
}
