package arp.dto.grid;

import arp.dto.util.WeeklyPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {
    private Long id;
    private Long count;
    private List<WeeklyPeriod> weeklyWork;
    private Double fuelConsumption;
    private Long distance;
}
