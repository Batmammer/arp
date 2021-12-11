package arp.service;

import arp.dto.GridInput;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Vehicle;
import arp.dto.util.WeeklyPeriod;
import arp.enums.EnergySourceType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GridService {

    private CalculateYearAlgorithm calculateYearAlgorithm;

    public YearResult runSimulation(GridInput gridInput) {
        calculateYearAlgorithm = new CalculateYearAlgorithm(
                new Data(
                        gridInput.getConstants(),
                        gridInput.getCosts(),
                        gridInput.getGrid().getStorages(),
                        calculateYearlyConsumption(gridInput.getGrid().getVehicles(),
                                gridInput.getConstants().getHydrogenTransportLoss()),
                        calculateElectrolyzers(gridInput.getGrid().getStorages().stream()
                                        .map(s -> s.getElectrolyzers()).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList()),
                                gridInput.getConstants().getTransmissionLoss())
                );
        return calculateYearAlgorithm.calculate();
    }

    private Map<Long, double[]> calculateElectrolyzers(List<Electrolyzer> input, double transmissionLoss) {
        return input.stream().collect(Collectors.toMap(Electrolyzer::getId, e -> calculateSummaryEnergyProduction(e, transmissionLoss)));
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

    private double[] calculateYearlyConsumption(List<Vehicle> vehicles, Double hydrogenTransportLoss) {
        double[] consumption = new double[365 * 24];
        Map<Long, boolean[]> weeklyWork = new HashMap<>();
        for (Vehicle v : vehicles) {
            weeklyWork.put(v.getId(), calculateWeekly(v.getWeeklyWork()));
        }
        for (int i = 0; i < 365 * 24; i++) {
            double current = 0;
            for (Vehicle v : vehicles) {
                if (weeklyWork.get(v.getId())[i % (24 * 7)]) {
                    current += v.getCount() * v.getFuelConsumption() + v.getDistance() * hydrogenTransportLoss;
                }
            }
            consumption[i] = current;
        }
        return consumption;
    }

    private boolean[] calculateWeekly(List<WeeklyPeriod> periods) {
        boolean[] weekly = new boolean[24 * 7];
        Arrays.fill(weekly, false);
        for (WeeklyPeriod p : periods) {
            if (p.getDayFrom() < 0 || p.getDayTo() > 6 || p.getDayFrom() > p.getDayTo())
                continue;
            if (p.getHourFrom() < 0 || p.getHourTo() > 23 || p.getHourFrom() > p.getHourTo())
                continue;
            for (int i = p.getDayFrom(); i <= p.getDayTo(); i++) {
                for (int j = p.getHourFrom(); j <= p.getHourTo(); j++) {
                    weekly[i * 7 + j] = true;
                }
            }

        }
        return weekly;
    }
}
