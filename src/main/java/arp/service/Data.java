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
import java.util.stream.Collectors;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Data implements Cloneable {
    private GridConstants gridConstants;
    private GridCosts gridCosts;
    private List<Storage> storages = new ArrayList<>();
    private double[] vehiclesConsumption; // godzina w roku

    @Override
    public Data clone()  {
        return clone(true);
    }

    public Data clone(boolean withStorages)  {
        try {
            Data data = (Data)super.clone();
            if (withStorages) {
                data.storages = storages.stream().map(s -> s.clone()).collect(Collectors.toList());
            }
//            if (withStorages)
            return data;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
