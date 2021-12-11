package arp.dto;

import arp.enums.EnergySourceType;

public class EnergySource {
    public EnergySourceType type;
    public Double maxPower;
    public Double distance;
    public double[] energyProduction; // rzeczywista moc dostarczona z uwzględnieniem charakterystyki rocznej produkcji i straty przesyłowej
}
