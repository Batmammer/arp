package arp.dto.grid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Electrolyzer implements Cloneable {
    private Long id;
    private List<EnergySource> sources;
    private Accumulator accumulator;
    private double efficiency;
    private double maxPower;

    public Electrolyzer clone() {
        List<EnergySource> newSources = new ArrayList<>();
        for (EnergySource energySource: sources)
            newSources.add(energySource.clone());
        return new Electrolyzer(id, newSources, new Accumulator(accumulator.getAccumulatorMaxSize()), efficiency, maxPower);
    }
}
