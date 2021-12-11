package arp.dto.output;

import lombok.Data;

@Data
public class VehicleOutput {
    private String name;
    private Long count;
    private boolean[] weeklyWork;
    private Double fuelConsumption;
    private Long distance;
}
