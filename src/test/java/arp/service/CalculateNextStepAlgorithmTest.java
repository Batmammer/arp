package arp.service;

import arp.dto.Electrolyzer;
import arp.dto.GridConstants;
import arp.dto.Storage;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateNextStepAlgorithmTest {
    @Test
    public void xx() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] cosumption = {1.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, cosumption);

        Step step = initStep(electrolyzer, 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }


    private Step initStep(Electrolyzer electrolyzer, int hour, double storageState, double accumulatorState) {
        Step step = new Step();
        step.hour = hour;
        step.electorizersStates.put(electrolyzer, buildInitialState(accumulatorState));
        step.storageState = new StorageState(storageState);
        step.storageState.currentLevel = storageState;
        return step;
    }

    private ElectrolyzerState buildInitialState(double accumulatorState) {
        ElectrolyzerState state = new ElectrolyzerState();
        state.accumulatorCurrentLevel = accumulatorState;
        return state;
    }

    private Data buildData(Electrolyzer electrolyzer, double storageMaxCapacity, double[] cosumption) {
        GridConstants gridConstants = new GridConstants();
        gridConstants.hydrogenTransportLoss = 0d;
        gridConstants.storageLoss = 0d;
        gridConstants.transmissionLoss = 0d;

        Storage storage = new Storage();
        storage.maxCapacity = storageMaxCapacity;
        storage.electrolyzers = Lists.newArrayList(electrolyzer);

        Data data = new Data();
        data.gridConstants = gridConstants;
        data.summaryStorage = storage;
        data.vehiclesConsumption = cosumption;
        return data;
    }

}