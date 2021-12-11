package arp.service;

import arp.dto.GridConstants;
import arp.dto.GridCosts;
import arp.dto.grid.Storage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    private GridConstants gridConstants;
    private GridCosts gridCosts;
    private List<Storage> storages = new ArrayList<>();
    private double[] vehiclesConsumption; // godzina w roku

    /**
     * rzeczywista moc dostarczona przez wszyskie źródła energii
     * z uwzględnieniem charakterystyki rocznej produkcji i straty przesyłowej
     */
    private Map<Long, double[]> summaryEnergyProduction = new HashMap<>(); // godzina w roku


}
