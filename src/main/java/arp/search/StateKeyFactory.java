package arp.search;

import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Storage;
import arp.service.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StateKeyFactory {
    private static int METHOD = 3;
//    static long sumMs = 0;

    public static String getKey(State state) {
        if (METHOD == 1) {
            return getKeyBasedOnType(state); // Szybka niepopprawna
        } else if (METHOD == 2) {
            return getKeyBasedOnTypeAndObject(state); // Poprawna ale długa
        } else {
//            long ms = System.nanoTime();
            return getKeyBasedOnStructure(state); // Szybka i mam nadzieję, że poprawna
//            long ms2 = System.nanoTime();
//            sumMs += ms2 - ms;
//            return key;

        }
    }

    public static String getKeyBasedOnStructure(State state) {
        return new StringBuilder()
                .append(Utils.roundDouble(state.getMetrics().getTotalCost()))
                .append(": ")
                .append(toString(state.getStorages()))
                .toString();
    }

    private static String toString(List<Storage> s) {
        return "[" + s.stream().map(e -> toString(e)).sorted().collect(Collectors.joining(",")) + "]";
    }

    private static String toString(Storage s) {
        return "[" + Utils.roundDouble(s.getMaxCapacity()) + "|" +
                (s.getElectrolyzers().stream().map(e -> toString(e)).sorted().collect(Collectors.joining(","))) + "]";
    }

    private static String toString(Electrolyzer e) {
        return "[" + Utils.roundDouble(e.getMaxPower()) + "|" +
                     Utils.roundDouble(e.getEfficiency()) + "|" +
                    Utils.roundDouble(e.getMinPower()) + "|" +
                    Utils.roundDouble(e.getEfficiency()) + "|" +
                    Utils.roundDouble(e.getAccumulator().getAccumulatorMaxSize())  + "|" +
                    (e.getSources().stream().map(p -> toString(p)).sorted().collect(Collectors.joining(","))) + "]";
    }

    private static String toString(EnergySource p) {
        return "[" + Utils.roundDouble(p.getMaxPower()) + "|" + Utils.roundDouble(p.getMaxPower()) + "|" + p.getType() + "]";
    }

    public static String getKeyBasedOnType(State state) {
        List<String> actions = getActionPath(state, a -> a.getType() +"");

        return new StringBuilder()
                .append(Utils.roundDouble(state.getMetrics().getTotalCost()))
                .append(": ")
                .append(actionsToString(actions))
                .toString();
    }

    public static String getKeyBasedOnTypeAndObject(State state) {
        List<String> actions = getActionPath(state, a -> a.getType() + "[" + a.getObjectId() + "]");

        return new StringBuilder()
                .append(Utils.roundDouble(state.getMetrics().getTotalCost()))
                .append(": ")
                .append(actionsToString(actions))
                .toString();
    }

    private static List<String> getActionPath(State state, Function<Action, String> function) {
        List<String> actions = new ArrayList<>();

        while (state != null) {
            if (state.getAction() != null) {
                actions.add(function.apply(state.getAction()));
            }
            state = state.getPreviousState();
        }
        return actions;
    }

    private static String actionsToString(List<String> actions) {
        return actions.stream()
                .filter(a -> a != null)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
