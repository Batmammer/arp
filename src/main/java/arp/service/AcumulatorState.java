package arp.service;

import lombok.Data;

@Data
public class AcumulatorState {
    private double accumulatorCurrentLevel = 0.0;

    public AcumulatorState(double accumulatorCurrentLevel) {
        this.accumulatorCurrentLevel = accumulatorCurrentLevel;
    }
}
