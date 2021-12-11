package arp.dto.grid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Storage implements Cloneable {
    private Long id;
    private double maxCapacity;
    private List<Electrolyzer> electrolyzers = new ArrayList<>();

    public Storage clone() {
        List<Electrolyzer> newElectrolyzers = new ArrayList<>();
        for (Electrolyzer electrolyzer: electrolyzers)
            newElectrolyzers.add(electrolyzer.clone());
        return new Storage(id, maxCapacity, newElectrolyzers);
    }
}
