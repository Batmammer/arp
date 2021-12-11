package arp.dto.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyPeriod {
    private Integer dayFrom;
    private Integer dayTo;
    private Integer hourFrom;
    private Integer hourTo;
}
