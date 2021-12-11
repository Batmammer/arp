package arp.dto;

import lombok.Data;

@Data
public class Vehicle {
    public String name;
    public Long count;
    public boolean[] weeklyWork;
    public Double fuelConsumption;
    public Long distance;
}
