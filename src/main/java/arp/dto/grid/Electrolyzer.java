package arp.dto.grid;

import arp.service.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Electrolyzer implements Cloneable {

    @Schema(description = "Electrolyzer id", example = "31", required = true)
    private Long id;

    @Schema(description = "List of energy sources", required = true)
    private List<EnergySource> sources = new ArrayList<>();

    @Schema(description = "Electrolyzer accumulator", required = true)
    private Accumulator accumulator;

    @Schema(description = "Electrolyzer efficiency", example = "25.0", required = true)
    private double efficiency;

    @Schema(description = "Electrolyzer max power", example = "25.0", required = true)
    private double minPower;

    @Schema(description = "Electrolyzer min power", example = "5.0", required = true)
    private double maxPower;

    /**
     * rzeczywista moc dostarczona przez wszyskie źródła energii
     * z uwzględnieniem charakterystyki rocznej produkcji i straty przesyłowej
     */
    @JsonIgnore
    private double[] summaryEnergyProduction; // godzina w roku

    public Electrolyzer clone() {
        try {
            Electrolyzer cloned = (Electrolyzer) super.clone();
            cloned.setSources(getSources().stream().map(s -> s.clone()).collect(Collectors.toList()));
            cloned.setAccumulator(accumulator.clone());
            return cloned;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public double getSummaryEnergyProduction(int hour) {
        if (summaryEnergyProduction != null && hour < summaryEnergyProduction.length) {
            return summaryEnergyProduction[hour];
        } else {
            return 0d;
        }
    }

    public void recalculateSummaryEnergyProduction(arp.service.Data data) {
        int size = summaryEnergyProduction != null ? summaryEnergyProduction.length : Utils.HOURS_OF_YEAR;
        summaryEnergyProduction = Utils.createTableOfValue(0, size);
        for (EnergySource source : getSources()) {
            double[] dailyProduction = source.getDailyProduction(data);
            for (int i = 0; i < size; i++) {
                double v = source.getMaxPower() * dailyProduction[i % dailyProduction.length];
                summaryEnergyProduction[i] += v * (1.0 - data.getGridConstants().getTransmissionLoss());
            }
        }
    }

}
