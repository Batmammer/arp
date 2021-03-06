package arp.dto.grid;

import arp.enums.EnergySourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergySource implements Cloneable {

    @Schema(description = "Energy source id", example = "41", required = true)
    private Long id;

    @Schema(description = "Energy source type WIND, PV", example = "PV", required = true)
    private EnergySourceType type;

    @Schema(description = "Energy source max power", example = "50", required = true)
    private Double maxPower;

    @Schema(description = "Energy source distance from electrolyzer", example = "10", required = true)
    private Double distance;


    public double[] getDailyProduction(arp.service.Data data) {
        if (EnergySourceType.WIND.equals(getType())) {
            if (data.getGridConstants().getWindDailyProduction() != null) {
                return data.getGridConstants().getWindDailyProduction();
            } else {
                return data.getWindMultiplier();
            }
        } else {
            if (data.getGridConstants().getPvDailyProduction() != null) {
                return data.getGridConstants().getPvDailyProduction();
            } else {
                return data.getPvMultiplier();
            }
        }
    }

    public EnergySource clone() {
        try {
            return (EnergySource)super.clone();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
