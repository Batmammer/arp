package arp.dto.output;

import lombok.Data;

import java.util.List;

@Data
public class ElectrolyzerOutput {
    private List<EnergySourceOutput> sources;
    private AccumulatorOutput accumulatorOutput;
    private double efficiency;
    private double maxPower;
}
