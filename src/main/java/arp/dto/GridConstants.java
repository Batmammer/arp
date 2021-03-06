package arp.dto;

import arp.service.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridConstants {
    @Schema(description = "Hours of Simimulation (1..8760)", example = "8760")
    private Integer hoursOfSimulation;

    @Schema(description = "Hydrogen Transport Loss", example = "0.1", required = true)
    private double hydrogenTransportLoss;

    @Schema(description = "Storage Hydrogen Loss", example = "0.005", required = true)
    private double storageLoss;

    @Schema(description = "Transmission Electricity Loss", example = "0.1", required = true)
    private double transmissionLoss;

    @Schema(description = "PV production histogram")
    private double[] pvDailyProduction;

    @Schema(description = "Windmill production histogram")
    private double[] windDailyProduction;

    @Schema(description = "Electrolyzer Efficiency", example = "25.0", required = true)
    private double electrolyzerEfficiency;

    public void setHydrogenTransportLoss(double hydrogenTransportLoss) {
        this.hydrogenTransportLoss = Utils.standardRound(hydrogenTransportLoss);
    }

    public void setStorageLoss(double storageLoss) {
        this.storageLoss = Utils.standardRound(storageLoss);
    }

    public void setTransmissionLoss(double transmissionLoss) {
        this.transmissionLoss = Utils.standardRound(transmissionLoss);
    }

    public void setElectrolyzerEfficiency(double electrolyzerEfficiency) {
        this.electrolyzerEfficiency = Utils.standardRound(electrolyzerEfficiency);
    }
}
