package arp.search;

import arp.dto.grid.Accumulator;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Storage;
import arp.service.CalculateYearAlgorithm;
import arp.service.Data;
import arp.service.Utils;
import arp.service.YearResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@lombok.Data
public class State implements Comparable<State>, Cloneable {
    private State previousState;
    private Action action;
    private Metrics metrics;
    private String key;

    private List<Storage> storages;

    public State(Data data) {
        this.previousState = null;
        this.action = null;
        this.metrics = calculateMetrics(data);
        this.storages = data.getStorages();
        updateKey();
    }

    @Override
    public int compareTo(State o) {
        return this.metrics.compareTo(o.getMetrics());
    }

    @Override
    public String toString() {
        if (key != null) {
            return key;
        } else {
            return updateKey();
        }
    }

    public String updateKey() {
        key = StateKeyFactory.getKey(this);
        return key;
    }

    public State buildNextState(Action action) {
        State state = this.clone();
        state.previousState = this;
        state.action = action;

        Metrics metrics = new Metrics();
        metrics.setGood(false);
        metrics.setTotalCost(this.metrics.getTotalCost() + (action != null ? action.getActionCost(): 0));

        state.metrics = metrics;
        state.key = null;

        return state;
    }

    public void updateMetrics(Data data) {
        Data clonedData = data.clone(false);
        clonedData.setStorages(storages);
        this.metrics = calculateMetrics(clonedData);
        this.key = updateKey();
    }

    private Metrics calculateMetrics(Data data) {
        CalculateYearAlgorithm calculateYearAlgorithm = new CalculateYearAlgorithm(data);
        YearResult yearResult = calculateYearAlgorithm.calculate();
        Metrics metrics = new Metrics();
        metrics.setGood(yearResult.isGood());
        metrics.setMinHourHydrogenLevel(yearResult.getMinHourHydrogenLevel());
        metrics.setTotalCost(getPreviousTotalCosts() + (action != null ? action.getActionCost(): 0));
        metrics.setTotalHydrogenWasted(yearResult.getTotalHydrogenWasted());
        metrics.setYearResult(yearResult);
        return metrics;
    }

    private double getPreviousTotalCosts() {
        return previousState != null ? previousState.getMetrics().getTotalCost() : 0.0;
    }

    @Override
    protected State clone() {
        try {
            State state = (State) super.clone();
            state.storages = storages.stream().map(s -> s.clone()).collect(Collectors.toList());
            return state;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Storage findStorageById(Long id) {
        for (Storage s: storages) {
            if (s.getId() != null && s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public Electrolyzer findElectrolyzerById(Long id) {
        for (Storage s: storages) {
            for (Electrolyzer e: s.getElectrolyzers()) {
                if (e.getId() != null && e.getId().equals(id)) {
                    return e;
                }
            }
        }
        return null;
    }

    public Accumulator findAccumulatorById(Long id) {
        for (Storage s: storages) {
            for (Electrolyzer e: s.getElectrolyzers()) {
                if (e.getId() != null && e.getId().equals(id)) {
                    return e.getAccumulator();
                }
            }
        }
        return null;
    }

    public EnergySource findEnergySourceById(Long id) {
        for (Storage s: storages) {
            for (Electrolyzer e: s.getElectrolyzers()) {
                for (EnergySource p: e.getSources()) {
                    if (p.getId() != null && p.getId().equals(id)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }



    public long nextStorageId() {
        long maxId = 0;
        for (Storage s: storages) {
            if (s.getId() != null) {
                maxId = Math.max(maxId, s.getId());
            }
        }
        return maxId+1;
    }

    public long nextElectrolyzerId() {
        long maxId = 0;
        for (Storage s: storages) {
            for (Electrolyzer e: s.getElectrolyzers()) {
                if (e.getId() != null) {
                    maxId = Math.max(maxId, e.getId());
                }
            }
        }
        return maxId+1;
    }

    public long nextEnergySourceId() {
        long maxId = 0;

        for (Storage s: storages) {
            for (Electrolyzer e: s.getElectrolyzers()) {
                for (EnergySource p: e.getSources()) {
                    if (p.getId() != null) {
                        maxId = Math.max(maxId, p.getId());
                    }
                }
            }
        }
        return maxId+1;
    }

}
