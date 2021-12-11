package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Storage implements Cloneable {
    public Double maxCapacity;
    public List<Electrolyzer> electrolyzers;

    public Storage clone() {
        List<Electrolyzer> newElectrolyzers = new ArrayList<>();
        for (Electrolyzer electrolyzer: electrolyzers)
            newElectrolyzers.add(electrolyzer.clone());
        return new Storage(maxCapacity, newElectrolyzers);
    }

}
