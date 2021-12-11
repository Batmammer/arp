package arp.service;

import arp.dto.Electrolyzer;

import java.util.Arrays;

import static arp.service.Consts.HOURS_OF_YEAR;

public class CalculateMaximumConsumption {
    private double epsilon = 0.01;
    private final Data data;

    public CalculateMaximumConsumption(Data data) {
        this.data = data;
    }

    public double calculate() {
        double min = 0;
        double max = getMax();

        while (min < max - epsilon) {
            double mid = (max - min) / 2;

            Data midData = cloneDataWithConsumption(mid);
            YearResult result = new CalculateYearAlgorithm(midData).calculate();

            if (result.isGood()) {
                min = mid;
            } else {
                max = mid;
            }
        }

        return min;
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

    private double[] createTableOfValue(double value) {
        double[] table = new double[HOURS_OF_YEAR];
        Arrays.fill(table, value);
        return table;
    }

}
