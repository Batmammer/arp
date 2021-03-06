package arp.service;

import arp.dto.GridConstants;
import arp.dto.grid.Accumulator;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.Storage;
import org.assertj.core.util.Lists;

public class AbstractAlgorithmTest {
    protected Step initStep(Storage storage, Accumulator accumulator, int hour, double storageState, double accumulatorState) {
        Step step = new Step();
        step.setHour(hour);
        step.getAccumulatorsStates().put(accumulator, buildInitialState(accumulatorState));
        step.getStorageStates().put(storage, new StorageState(storageState));
        return step;
    }

    protected AccumulatorState buildInitialState(double accumulatorState) {
        return new AccumulatorState(accumulatorState);
    }

    protected Data buildData(Electrolyzer electrolyzer, double storageMaxCapacity, double[] consumption) {
        GridConstants gridConstants = new GridConstants();
        gridConstants.setHydrogenTransportLoss(0d);
        gridConstants.setStorageLoss( 0d);
        gridConstants.setTransmissionLoss(0d);

        Storage storage = new Storage();
        storage.setId(1l);
        storage.setMaxCapacity(storageMaxCapacity);
        storage.setElectrolyzers(Lists.newArrayList(electrolyzer));

        Data data = new Data();
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(consumption);
        return data;
    }

    protected Electrolyzer buildElectrolyzerWithAccumulator() {
        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.setId(1l);
        electrolyzer.setAccumulator(new Accumulator());
        return electrolyzer;
    }


}
