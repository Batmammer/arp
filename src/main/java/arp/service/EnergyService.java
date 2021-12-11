package arp.service;

import arp.enums.EnergySourceType;
import org.springframework.stereotype.Service;

@Service
public class EnergyService {

    public double getPowerMultipler(int day, int hour, EnergySourceType type) {
        return 1.0;
    }
}
