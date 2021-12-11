package arp.service;

import arp.dto.*;
import arp.enums.EnergySourceType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GridService {

    public void runSimulation(InputGrid inputGrid) {
        Data data = new Data(inputGrid.getConstants(),
                new Storage(inputGrid.getGrid().getWatSide().getStorage().getMaxCapacity(),
                        calculateElectrolyzers(
                                inputGrid.getGrid().getWatSide().getStorage().getElectrolyzers(),
                        inputGrid.getConstants().getTransmissionLoss())),
                calculateYearlyConsumption(inputGrid.getGrid().getHydrogenSide(),
                        inputGrid.getConstants().getHydrogenTransportLoss()));
    }

    private List<Electrolyzer> calculateElectrolyzers(List<Electrolyzer> input, double transmissionLoss) {
        return input.stream().map(e -> {
            Electrolyzer electrolyzer =new Electrolyzer();
            electrolyzer.setEfficiency(e.getEfficiency());
            electrolyzer.setSources(e.getSources());
            electrolyzer.setAccumulatorMaxSize(e.getAccumulatorMaxSize());
            electrolyzer.setMinPower(e.getMinPower());
            electrolyzer.setMaxPower(e.getMaxPower());
            electrolyzer.setSummaryEnergyProduction(calculateSummaryEnergyProduction(e, transmissionLoss));
            return electrolyzer;
        }).collect(Collectors.toList());
    }

    private double[] calculateSummaryEnergyProduction(Electrolyzer electrolyzer, double transmissionLoss) {
        double[] production = new double[365 * 24];
        for (int i = 0; i < 365 * 24; i++) {
            double current = 0;
            for (EnergySource es : electrolyzer.getSources()) {
                current += es.getMaxPower() * getPowerMultiplier(i, es.getType()) * (1.0 - transmissionLoss);
            }
            production[i] = current;
        }
        return production;
    }

    private double getPowerMultiplier(int hour, EnergySourceType type) {
        return 1.0;
    }

    private double[] calculateYearlyConsumption(HydrogenSide hydrogenSide, Double hydrogenTransportLoss) {
        double[] consumption = new double[365 * 24];
        for (int i = 0; i < 365 * 24; i++) {
            double current = 0;
            for (Vehicle v : hydrogenSide.getVehicles()) {
                if (v.getWeeklyWork()[i % (24 * 7)]) {
                    current += v.getCount() * v.getFuelConsumption() + v.getDistance() * hydrogenTransportLoss;
                }
            }
            consumption[i] = current;
        }
        return consumption;
    }
}
