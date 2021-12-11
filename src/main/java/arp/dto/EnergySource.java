package arp.dto;

import arp.enums.EnergySourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergySource {
    public EnergySourceType type;
    public Double maxPower;
    public Double distance;
}
