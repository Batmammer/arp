package arp.dto.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyPeriod {

    @Schema(description = "Day from 0-6", example = "0", required = true)
    private Integer dayFrom;

    @Schema(description = "Day to 0-6 inclusive", example = "6", required = true)
    private Integer dayTo;

    @Schema(description = "Hour from 0-23", example = "0", required = true)
    private Integer hourFrom;

    @Schema(description = "Hour to 0-23 inclusive", example = "23", required = true)
    private Integer hourTo;
}
