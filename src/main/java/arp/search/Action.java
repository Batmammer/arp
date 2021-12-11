package arp.search;

import lombok.Data;

@Data
public class Action {
    private ActionType type;
    private double actionCost;
    private long objectId;
}
