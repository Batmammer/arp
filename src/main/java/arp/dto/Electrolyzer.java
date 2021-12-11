package arp.dto;

import arp.service.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Electrolyzer implements Cloneable {
    public List<EnergySource> sources = new ArrayList<>(); // w algorytmie korzystamy tylko z summary

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
        double[] newSummaryEnergyProduction = Arrays.copyOf(summaryEnergyProduction, summaryEnergyProduction.length);
        return new Electrolyzer(newSources, no, accumulatorMaxSize, efficiency, maxPower, minPower, newSummaryEnergyProduction);
    }
}
