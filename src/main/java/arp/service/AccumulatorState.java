package arp.service;

import lombok.Data;

@Data
public class AccumulatorState {
    private double accumulatorCurrentLevel;

    public AccumulatorState(double accumulatorCurrentLevel) {
        this.accumulatorCurrentLevel = Utils.standardRound(accumulatorCurrentLevel);
    }

}
