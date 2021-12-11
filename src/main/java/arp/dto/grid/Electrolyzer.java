package arp.dto.grid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Electrolyzer implements Cloneable {

    @Schema(description = "Electrolyzer id", example = "31", required = true)
    private Long id;

    @Schema(description = "List of energy sources", required = true)
    private List<EnergySource> sources;

    @Schema(description = "Electrolyzer accumulator", required = true)
    private Accumulator accumulator;

    @Schema(description = "Electrolyzer efficiency", example = "25.0", required = true)
    private double efficiency;

    @Schema(description = "Electrolyzer max power", example = "25.0", required = true)
    private double minPower;

    @Schema(description = "Electrolyzer min power", example = "5.0", required = true)
    private double maxPower;

    public Electrolyzer clone() {
        List<EnergySource> newSources = new ArrayList<>();
        for (EnergySource energySource: sources)
            newSources.add(energySource.clone());
        return new Electrolyzer(id, newSources, new Accumulator(accumulator.getAccumulatorMaxSize()), efficiency, minPower, maxPower);
    }
}
