package arp.dto;

import java.util.List;

public class Electrolyzer {
    public List<EnergySource> sources; // w algorytmie korzystamy tylko z summary

    public Double accumulatorMaxSize;
    public Double efficiency;
    public Double maxPower;
    public Double minPower;

    /**
     * rzeczywista moc dostarczona przez wszyskie źródła energii
     * z uwzględnieniem charakterystyki rocznej produkcji i straty przesyłowej
     */
    public double[] summaryEnergyProduction; // godzina w roku
}
