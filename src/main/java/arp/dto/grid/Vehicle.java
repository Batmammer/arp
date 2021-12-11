package arp.dto.grid;

import arp.dto.util.WeeklyPeriod;
import arp.service.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    @Schema(description = "Vehicle type id", example = "11", required = true)
    private Long id;

    @Schema(description = "Vehicles count", example = "100", required = true)
    private Long count;

    @Schema(description = "List of Periods when Vehicles work", required = true)
    private List<WeeklyPeriod> weeklyWork;

    @Schema(description = "Single Vehicle hydrogen consumption", example = "4.5", required = true)
    private Double fuelConsumption;

    @Schema(description = "Distance from refueling station", example = "30", required = true)
    private Long distance;

    public void setFuelConsumption(Double fuelConsumption) {
        this.fuelConsumption = Utils.standardRound(fuelConsumption);
    }
}
