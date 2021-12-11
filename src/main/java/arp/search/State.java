package arp.search;

import arp.dto.grid.Storage;
import arp.service.Data;
import arp.service.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class State implements Comparable<State> {

    public List<Storage> storages;
    public boolean good;
    public double minHourHydrogenLevel;
    public State previousState;
    public ActionType actionType;
    public double actionCost;
    public double totalCost;
    public Data data;
    private String toString;

    public State(List<Storage> storages, boolean good, double minHourHydrogenLevel, State previousState, ActionType actionType, double actionCost, double totalCost, Data data) {
        this.storages = storages;
        this.good = good;
        this.minHourHydrogenLevel = minHourHydrogenLevel;
        this.previousState = previousState;
        this.actionType = actionType;
        this.actionCost = actionCost;
        this.totalCost = totalCost;
        this.data = data;
        this.toString = null;
    }

    @Override
    public int compareTo(State o) {
        int result = Double.compare(totalCost, o.totalCost);
        if (result != 0)
            return result;
        return -Double.compare(minHourHydrogenLevel, o.minHourHydrogenLevel);
    }

    @Override
    public String toString() {
        if (toString != null) {
            return toString;
        }

        List<ActionType> actions = getActionTypesPath();

        toString = new StringBuilder()
                .append(Utils.roundDouble(totalCost))
                .append(": ")
                .append(actionsTypesToString(actions))
                .toString();

        return toString;
    }

    private String actionsTypesToString(List<ActionType> actions) {
        return actions.stream().sorted()
                .filter(a -> a != null)
                .map(a -> a.toString())
                .collect(Collectors.joining(", "));
    }

    private List<ActionType> getActionTypesPath() {
        List<ActionType> actions = new ArrayList<>();
        State state = this;
        while (state != null) {
            if (state.actionType != null) {
                actions.add(state.actionType);
            }
            state = state.previousState;
        }
        return actions;
    }
}
