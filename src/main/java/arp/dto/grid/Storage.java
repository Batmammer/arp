package arp.dto.grid;

import arp.service.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Storage implements Cloneable {

    @Schema(description = "Storage id", example = "21", required = true)
    private Long id;

    @Schema(description = "Storage max capacity", example = "300", required = true)
    private double maxCapacity;

    @Schema(description = "List of electrolyzers", required = true)
    private List<Electrolyzer> electrolyzers = new ArrayList<>();

    public Storage clone() {
        List<Electrolyzer> newElectrolyzers = new ArrayList<>();
        for (Electrolyzer electrolyzer: electrolyzers)
            newElectrolyzers.add(electrolyzer.clone());
        return new Storage(id, maxCapacity, newElectrolyzers);
    }

    public void setMaxCapacity(double maxCapacity) {
        this.maxCapacity = Utils.standardRound(maxCapacity);
    }
}
