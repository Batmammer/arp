package arp.service;

import lombok.Data;

@Data
public class StorageState {
    private double currentLevel;

    public StorageState(double currentLevel) {
        this.currentLevel = currentLevel;
    }
}
