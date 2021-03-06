package arp.service;

import arp.dto.GridConstants;
import arp.dto.GridInput;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Storage;
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

@Service
public class GridService {

    private static boolean TEST = true;

    private CalculateYearAlgorithm calculateYearAlgorithm;
    private BroadFirstSearchAlgorithm broadFirstSearchAlgorithm;
    private CalculateMaximumConsumption calculateMaximumConsumption;
    private static double pvMultiplier[] = null;
    private static double windMultiplier[] = null;

    public GridService() {
        if (pvMultiplier == null) {
            try {
                if (TEST) {
                    Resource resource = new ClassPathResource("irradiance.txt");
                    String pvString = new String(Files.readAllBytes(resource.getFile().toPath()));
                    pvMultiplier = Arrays.stream(pvString.split(",")).mapToDouble(s -> 1.0).toArray();
                    resource = new ClassPathResource("wind.txt");
                    String windString = new String(Files.readAllBytes(resource.getFile().toPath()));
                    windMultiplier = Arrays.stream(windString.split(",")).mapToDouble(s -> 1.0).toArray();
                } else {
                    Resource resource = new ClassPathResource("irradiance.txt");
                    String pvString = new String(Files.readAllBytes(resource.getFile().toPath()));
                    pvMultiplier = Arrays.stream(pvString.split(",")).mapToDouble(s -> Double.valueOf(s)).toArray();
                    resource = new ClassPathResource("wind.txt");
                    String windString = new String(Files.readAllBytes(resource.getFile().toPath()));
                    windMultiplier = Arrays.stream(windString.split(",")).mapToDouble(s -> Double.valueOf(s)).toArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public YearResult runSimulation(GridInput gridInput) {
        Data data = getDataAndInit(gridInput);
        calculateYearAlgorithm = new CalculateYearAlgorithm(data);
        return calculateYearAlgorithm.calculate();
    }

    public State calculateCapex(GridInput gridInput) {
        Data data = getDataAndInit(gridInput);
        broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        return broadFirstSearchAlgorithm.calculate();
    }

    public MaxConsumptionYearResult calculateHydrogen(GridInput gridInput) {
        Data data = getDataAndInit(gridInput);
        calculateMaximumConsumption = new CalculateMaximumConsumption(data);
        return calculateMaximumConsumption.calculate();
    }

    private Data getDataAndInit(GridInput gridInput) {
        Data data = new Data(
                gridInput.getConstants(),
                gridInput.getCosts(),
                gridInput.getGrid().getStorages(),
                calculateYearlyConsumption(gridInput.getGrid().getVehicles(),
                        gridInput.getConstants().getHydrogenTransportLoss()),
                pvMultiplier,
                windMultiplier
        );
        recalculateElectrolyzers(gridInput.getGrid().getStorages(), data);
        return data;
    }

    private void recalculateElectrolyzers(List<Storage> storages, Data data) {
        for (Storage storage : storages)
            for (Electrolyzer electrolyzer : storage.getElectrolyzers())
                electrolyzer.recalculateSummaryEnergyProduction(data);
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
        if (type == EnergySourceType.PV) {
            if (constants.getPvDailyProduction() != null)
                return constants.getPvDailyProduction()[hour % constants.getPvDailyProduction().length];
            else
                return pvMultiplier[hour];
        }
        if (type == EnergySourceType.WIND) {
            if (constants.getWindDailyProduction() != null)
                return constants.getWindDailyProduction()[hour % constants.getWindDailyProduction().length];
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
            for (int i = p.getDayFrom(); i <= p.getDayTo(); i++) {
                for (int j = p.getHourFrom(); j <= p.getHourTo(); j++) {
                    weekly[i * 7 + j] = true;
                }
            }
        }
        return weekly;
    }
}
