package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Electrolyzer implements Cloneable {
    public List<EnergySource> sources; // w algorytmie korzystamy tylko z summary

    public int no;
    public double accumulatorMaxSize;
    public double efficiency;
    public double maxPower;
    public double minPower;

    /**
     * rzeczywista moc dostarczona przez wszyskie źródła energii
     * z uwzględnieniem charakterystyki rocznej produkcji i straty przesyłowej
     */
    public double[] summaryEnergyProduction; // godzina w roku

    public Electrolyzer clone() {
        List<EnergySource> newSources = new ArrayList<>();
        for (EnergySource energySource: sources)
            newSources.add(energySource.clone());
        return new Electrolyzer(newSources, no, accumulatorMaxSize, efficiency, maxPower, minPower, summaryEnergyProduction);
    }
}
