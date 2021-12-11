package arp.service;

import arp.dto.GridConstants;
import org.assertj.core.util.Lists;

public class AbstractAlgorithmTest {
    protected Step initStep(Electrolyzer electrolyzer, int hour, double storageState, double accumulatorState) {
        Step step = new Step();
        step.hour = hour;
        step.electorizersStates.put(electrolyzer, buildInitialState(accumulatorState));
        step.storageStates = new StorageState(storageState);
        return step;
    }

    protected ElectrolyzerState buildInitialState(double accumulatorState) {
        return new ElectrolyzerState(accumulatorState);
    }

    protected Data buildData(Electrolyzer electrolyzer, double storageMaxCapacity, double[] consumption) {
        GridConstants gridConstants = new GridConstants();
        gridConstants.setHydrogenTransportLoss( = 0d;
        gridConstants.setStorageLoss( 0d;
        gridConstants.setTransmissionLoss(0d;

        Storage storage = new Storage();
        storage.maxCapacity = storageMaxCapacity;
        storage.electrolyzers = Lists.newArrayList(electrolyzer);

        Data data = new Data();
        data.gridConstants = gridConstants;
        data.summaryStorage = storage;
        data.vehiclesConsumption = consumption;
        return data;
    }
}
