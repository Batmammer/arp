package arp.service;

import arp.dto.Electrolyzer;

import static arp.service.Utils.createTableOfValue;

public class CalculateMaximumConsumption {
    private double epsilon = 0.01;
    private final Data data;

    public CalculateMaximumConsumption(Data data) {
        this.data = data;
    }

    public double calculate() {
        double min = 0;
        double max = getMax();

        YearResult result = calculate(max);
        if (result.isGood()) {
            return max;
        }

        while (min < max - epsilon) {
            double mid = (max + min) / 2;
            System.out.println("[" + min + ", " + max + " = " + mid + "]");

            result = calculate(mid);

            if (result.isGood()) {
                min = mid;
            } else {
                max = mid;
            }
        }

        return min;
    }

    private YearResult calculate(double value) {
        Data midData = cloneDataWithConsumption(value);
        return new CalculateYearAlgorithm(midData).calculate();
    }

    private double getMax() {
        double max = 0;
        for (Electrolyzer e : data.summaryStorage.electrolyzers) {
            max += e.maxPower * e.efficiency;
        }
        return max;
    }

    private Data cloneDataWithConsumption(double consumption) {
        Data newData = new Data();
        newData.gridConstants = data.gridConstants;
        newData.summaryStorage = data.summaryStorage;
        newData.vehiclesConsumption = createTableOfValue(consumption);
        return newData;
    }



}
