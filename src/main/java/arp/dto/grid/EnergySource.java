package arp.dto.grid;

import arp.enums.EnergySourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergySource implements Cloneable {
    private Long id;
    private EnergySourceType type;
    private Double maxPower;
    private Double distance;

    public EnergySource clone() {
        return new EnergySource(id, type, maxPower, distance);
    }
}
