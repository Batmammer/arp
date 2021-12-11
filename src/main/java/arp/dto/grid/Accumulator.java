package arp.dto.grid;

import arp.service.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accumulator {
    @Schema(description = "Accumulator capacity", example = "200", required = true)
    private double accumulatorMaxSize;

    public void setAccumulatorMaxSize(double accumulatorMaxSize) {
        this.accumulatorMaxSize = Utils.standardRound(accumulatorMaxSize);
    }
}
