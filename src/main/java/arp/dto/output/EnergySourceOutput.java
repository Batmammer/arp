package arp.dto.output;

import arp.enums.EnergySourceType;
import lombok.Data;

@Data
public class EnergySourceOutput {
    private EnergySourceType type;
    private Double maxPower;
    private Double distance;
}
