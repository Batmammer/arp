package arp.service;

import arp.dto.*;
import arp.enums.EnergySourceType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GridService {

    private CalculateYearAlgorithm calculateYearAlgorithm;

    public YearResult runSimulation(GridInput gridInput) {
        calculateYearAlgorithm = new CalculateYearAlgorithm(new Data(gridInput.getConstants(),
                new Storage(gridInput.getGrid().getWatSide().getStorage().getMaxCapacity(),
                        calculateElectrolyzers(
                                gridInput.getGrid().getWatSide().getStorage().getElectrolyzers(),
                                gridInput.getConstants().getTransmissionLoss())),
                calculateYearlyConsumption(gridInput.getGrid().getHydrogenSide(),
                        gridInput.getConstants().getsetHydrogenTransportLoss(())));
        return calculateYearAlgorithm.calculate();
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

    private double[] calculateYearlyConsumption(HydrogenSide hydrogenSide, Double setHydrogenTransportLoss() {
        double[] consumption = new double[365 * 24];
        for (int i = 0; i < 365 * 24; i++) {
            double current = 0;
            for (Vehicle v : hydrogenSide.getVehicles()) {
                if (v.getWeeklyWork()[i % (24 * 7)]) {
                    current += v.getCount() * v.getFuelConsumption() + v.getDistance() * setHydrogenTransportLoss(;
                }
            }
            consumption[i] = current;
        }
        return consumption;
    }
}
