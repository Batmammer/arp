package arp.dto;

import arp.enums.EnergySourceType;
import lombok.Data;

@Data
public class EnergySource {
    public EnergySourceType type;
    public Double maxPower;
    public Double distance;
}
