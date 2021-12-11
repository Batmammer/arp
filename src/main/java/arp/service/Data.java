package arp.service;

import arp.dto.GridConstants;
import arp.dto.GridCosts;
import arp.dto.grid.Storage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    public GridConstants gridConstants;
    public GridCosts gridCosts;
    public List<Storage> storages;
    public double[] vehiclesConsumption; // godzina w roku

    /**
     * rzeczywista moc dostarczona przez wszyskie źródła energii
     * z uwzględnieniem charakterystyki rocznej produkcji i straty przesyłowej
     */
    public Map<Long, double[]> summaryEnergyProduction = new HashMap<>(); // godzina w roku


}
