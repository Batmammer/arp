package arp.service;

import java.util.Arrays;

public class Utils {
    public static final int HOURS_OF_YEAR = 24 * 365;

    public static double[] createTableOfValue(double value) {
        double[] table = new double[HOURS_OF_YEAR];
        Arrays.fill(table, value);
        return table;
    }

    public static String roundDouble(double d) {
        return "" + Math.round(1000 * d) / 1000.0;
    }

}
