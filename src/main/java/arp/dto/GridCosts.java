package arp.dto;

import arp.service.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridCosts {

    @Schema(description = "Cost of 1MW PV installation", example = "5.0", required = true)
    private double pvCost;

    @Schema(description = "Cost of 1MW Windmill installation", example = "3.0", required = true)
    private double windCost;

    @Schema(description = "Cost of 1MWh electricity storage", example = "2.0", required = true)
    private double storagePowerCost;

    @Schema(description = "Cost of 1MWh electrolyzer", example = "5.0", required = true)
    private double electrolyzerCost;

    @Schema(description = "Cost of 1MWh hydrogen storage", example = "2.0", required = true)
    private double storageHydrogenCost;

    public void setPvCost(double pvCost) {
        this.pvCost = Utils.standardRound(pvCost);
    }

    public void setWindCost(double windCost) {
        this.windCost = Utils.standardRound(windCost);
    }

    public void setStoragePowerCost(double storagePowerCost) {
        this.storagePowerCost = Utils.standardRound(storagePowerCost);
    }

    public void setElectrolyzerCost(double electrolyzerCost) {
        this.electrolyzerCost = Utils.standardRound(electrolyzerCost);
    }

    public void setStorageHydrogenCost(double storageHydrogenCost) {
        this.storageHydrogenCost = Utils.standardRound(storageHydrogenCost);
    }
}
