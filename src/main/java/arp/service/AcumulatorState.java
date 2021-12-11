package arp.service;

import lombok.Data;

@Data
public class AcumulatorState {
    public double accumulatorCurrentLevel;

    public AcumulatorState(double accumulatorCurrentLevel) {
        this.accumulatorCurrentLevel = accumulatorCurrentLevel;
    }
}
