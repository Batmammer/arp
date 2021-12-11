package arp.service;

import arp.dto.GridConstants;
import arp.dto.GridInput;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Vehicle;
import arp.dto.util.WeeklyPeriod;
import arp.enums.EnergySourceType;
import arp.search.BroadFirstSearchAlgorithm;
import arp.search.State;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GridService {

    private CalculateYearAlgorithm calculateYearAlgorithm;
    private BroadFirstSearchAlgorithm broadFirstSearchAlgorithm;
    private CalculateMaximumConsumption calculateMaximumConsumption;
    private static Double pvMultiplier[] = null;
    private static Double windMultiplier[] = null;

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
                        gridInput.getConstants())
                ));
        return calculateYearAlgorithm.calculate();
    }

    public State calculateCapex(GridInput gridInput) {
        broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(
                new Data(
                        gridInput.getConstants(),
                        gridInput.getCosts(),
                        gridInput.getGrid().getStorages(),
                        calculateYearlyConsumption(gridInput.getGrid().getVehicles(),
                                gridInput.getConstants().getHydrogenTransportLoss()),
                        calculateElectrolyzers(gridInput.getGrid().getStorages().stream()
                                        .map(s -> s.getElectrolyzers()).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList()),
                                gridInput.getConstants())
                ));
        return broadFirstSearchAlgorithm.calculate();
    }

    public Double calculateHydrogen(GridInput gridInput) {
        calculateMaximumConsumption = new CalculateMaximumConsumption(
                new Data(
                        gridInput.getConstants(),
                        gridInput.getCosts(),
                        gridInput.getGrid().getStorages(),
                        calculateYearlyConsumption(gridInput.getGrid().getVehicles(),
                                gridInput.getConstants().getHydrogenTransportLoss()),
                        calculateElectrolyzers(gridInput.getGrid().getStorages().stream()
                                        .map(s -> s.getElectrolyzers()).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList()),
                                gridInput.getConstants())
                ));
        return calculateMaximumConsumption.calculate().getMaxConsumption();
    }

    private Map<Long, double[]> calculateElectrolyzers(List<Electrolyzer> input, GridConstants constants) {
        return input.stream().collect(Collectors.toMap(Electrolyzer::getId, e ->
                calculateSummaryEnergyProduction(e, constants)));
    }

    private double[] calculateSummaryEnergyProduction(Electrolyzer electrolyzer, GridConstants constants) {
        double[] production = new double[365 * 24];
        for (int i = 0; i < 365 * 24; i++) {
            double current = 0;
            for (EnergySource es : electrolyzer.getSources()) {
                current += es.getMaxPower() * getPowerMultiplier(i, es.getType(), constants)
                        * (1.0 - constants.getTransmissionLoss());
            }
            production[i] = current;
        }
        return production;
    }

    private double getPowerMultiplier(int hour, EnergySourceType type, GridConstants constants) {
        if (pvMultiplier == null) {
            try {
                Resource resource = new ClassPathResource("irradiance.txt");
                String pvString = new String(Files.readAllBytes(resource.getFile().toPath()));
                pvMultiplier = Arrays.stream(pvString.split(",")).map(s -> Double.valueOf(s)).toArray(Double[]::new);
                resource = new ClassPathResource("wind.txt");
                String windString = new String(Files.readAllBytes(resource.getFile().toPath()));
                windMultiplier = Arrays.stream(windString.split(",")).map(s -> Double.valueOf(s)).toArray(Double[]::new);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return 1;
            }
        }
        if (type == EnergySourceType.PV) {
            if (constants.getPvDailyProduction() != null && hour < constants.getPvDailyProduction().length)
                return constants.getPvDailyProduction()[hour];
            else
                return pvMultiplier[hour];
        }
        if (type == EnergySourceType.WIND) {
            if (constants.getWindDailyProduction() != null && hour < constants.getWindDailyProduction().length)
                return constants.getWindDailyProduction()[hour];
            else
                return windMultiplier[hour];
        }
        return 1.0;
    }

    public double[] calculateYearlyConsumption(List<Vehicle> vehicles, Double hydrogenTransportLoss) {
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
            for(int i = p.getDayFrom(); i <= p.getDayTo(); i++) {
                for (int j = p.getHourFrom(); j <= p.getHourTo(); j++) {
                    weekly[i * 7 + j] = true;
                }
            }
        }
        return weekly;
    }
}
