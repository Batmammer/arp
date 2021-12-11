package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    public String name;
    public Long count;
    public boolean[] weeklyWork;
    public Double fuelConsumption;
    public Long distance;
}
