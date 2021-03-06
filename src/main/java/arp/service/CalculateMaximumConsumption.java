package arp.service;

import arp.dto.grid.Electrolyzer;
import arp.dto.grid.Storage;

import static arp.service.Utils.createTableOfValue;

public class CalculateMaximumConsumption {
    private double epsilon = 0.01;
    private final Data data;

    public CalculateMaximumConsumption(Data data) {
        this.data = data;
    }

    public MaxConsumptionYearResult calculate() {
        double min = 0;
        double max = getMax();

        YearResult result = calculate(max);
        if (result.isGood()) {
            return new MaxConsumptionYearResult(result, Utils.standardRound(max));
        }

        while (min < max - epsilon) {
            double mid = (max + min) / 2;

            result = calculate(mid);

            if (result.isGood()) {
                min = mid;
            } else {
                max = mid;
            }
        }

        return new MaxConsumptionYearResult(result, Utils.standardRound(min));
    }

    private YearResult calculate(double value) {
        Data midData = cloneDataWithConsumption(value);
        return new CalculateYearAlgorithm(midData).calculate();
    }

    private double getMax() {
        double max = 0;
        for (Storage storage : data.getStorages()) {
            for (Electrolyzer e : storage.getElectrolyzers()) {
                max += e.getMaxPower() * e.getEfficiency();
            }
        }
        return max;
    }

    private Data cloneDataWithConsumption(double consumption) {
        Data newData = data.clone(false);
        newData.setVehiclesConsumption(createTableOfValue(consumption));
        return newData;
    }


}
