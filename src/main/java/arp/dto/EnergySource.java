package arp.dto;

import arp.enums.EnergySourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class EnergySource implements Cloneable {
    public EnergySourceType type;
    public Double maxPower;
    public Double distance;

    public EnergySource clone() {
        return new EnergySource(type, maxPower, distance);
    }
}
