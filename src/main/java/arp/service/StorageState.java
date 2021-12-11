package arp.service;

import lombok.Data;

@Data
public class StorageState {
    public double currentLevel;

    public StorageState(double currentLevel) {
        this.currentLevel = currentLevel;
    }
}
