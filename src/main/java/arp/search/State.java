package arp.search;

import arp.service.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class State implements Comparable<State> {

    public Storage storage;
    public boolean good;
    public double minHourHydrogenLevel;
    public State previousState;
    public ActionType actionType;
    public double actionCost;
    public double totalCost;
    private String toString;

    public State(Storage storage, boolean good, double minHourHydrogenLevel, State previousState, ActionType actionType, double actionCost, double totalCost) {
        this.storage = storage;
        this.good = good;
        this.minHourHydrogenLevel = minHourHydrogenLevel;
        this.previousState = previousState;
        this.actionType = actionType;
        this.actionCost = actionCost;
        this.totalCost = totalCost;
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
        StringBuilder result = new StringBuilder().append(Utils.roundDouble(totalCost)).append(": ");
        List<ActionType> actions = new ArrayList<>();
        State state = this;
        while (state != null) {
            if (state.actionType != null)
                actions.add(state.actionType);
            state = state.previousState;
        }
        Collections.sort(actions);
        for (ActionType actionType : actions)
            if (actionType != null) {
                result.append(actionType).append(", ");
            }
        toString = result.toString();
        return toString;
    }
}
