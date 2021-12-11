package arp.dto;

import java.util.List;

public class Electrolyzer {
    public List<EnergySource> sources; // w algorytmie korzystamy tylko z summary

    public int lp;
    public double accumulatorMaxSize;
    public double efficiency;
    public double maxPower;
    public double minPower;

    /**
     * rzeczywista moc dostarczona przez wszyskie źródła energii
     * z uwzględnieniem charakterystyki rocznej produkcji i straty przesyłowej
     */
    public double[] summaryEnergyProduction; // godzina w roku
}
