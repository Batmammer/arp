package arp.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaxConsumptionYearResult {
    private YearResult yearResult;
    private double maxConsumption;
}
