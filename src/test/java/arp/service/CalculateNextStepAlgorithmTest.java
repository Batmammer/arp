package arp.service;

import arp.dto.Electrolyzer;
import arp.dto.GridConstants;
import arp.dto.Storage;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateNextStepAlgorithmTest {
    @Test
    public void shouldUseCurrentProductionFromElectrolizerWhenStorageInEmpty() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {1.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 100d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseStorageWhenProductionFromElectrolizerIsZero() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {1.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 0d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 0d, 0d);
        expectedStep.overflowPowerProduction = 1.0d;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseBothProductionFromElectrolizerAndStorageIsZero() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 1d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedProductionFromElectrolizer() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 1d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, -1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedStorage() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 0d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{0.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, -1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedMaxPowerAndOverproduction() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {4.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 2d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{98.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, -2d, 0d);
        expectedStep.overflowPowerProduction = 96;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shoudTreatMinusStorageAsZero() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 0d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, -1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 0d, 0d);
        expectedStep.overflowPowerProduction = 1.0;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void loadStorageToHalf() {
        // given
        double storageMaxCapacity = 2.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 100d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseEfficiency() {
        // given
        double storageMaxCapacity = 2.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 100d;
        electrolyzer.efficiency = 2.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 2d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 2d, 0d);
        expectedStep.overflowHydrogenProduction = 2.0;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseStorageLimitAndTestOverFlowHydrogenProduction() {
        // given
        double storageMaxCapacity = 2.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 100d;
        electrolyzer.efficiency = 3.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 2d, 0d);
        expectedStep.overflowHydrogenProduction = 1.0d;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseAccumulator() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {1.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 100d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 1.0d;
        electrolyzer.summaryEnergyProduction = new double[]{0.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 1d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseBothAccumulatorAndProduction() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 100d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 1.0d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 1d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedAculumulator() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 100d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 1.0d;
        electrolyzer.summaryEnergyProduction = new double[]{0.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 1d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, -1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedProduction() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 100d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.0d;
        electrolyzer.summaryEnergyProduction = new double[]{1.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, -1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void overflowPowerProduction() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 1d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.0d;
        electrolyzer.summaryEnergyProduction = new double[]{2.0};
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);

        Step step = initStep(electrolyzer, 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(electrolyzer, 1, 0d, 0d);
        expectedStep.overflowPowerProduction = 1;
        expectedStep.overflowHydrogenProduction = 1;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void comboProductions() {
        // given
        GridConstants gridConstants = new GridConstants();
        gridConstants.hydrogenTransportLoss = 0d;
        gridConstants.storageLoss = 0d;
        gridConstants.transmissionLoss = 0d;

        Electrolyzer e1 = new Electrolyzer();
        e1.maxPower = 2d;
        e1.efficiency = 1.0d;
        e1.accumulatorMaxSize = 0.0d;
        e1.summaryEnergyProduction = new double[]{10.0};

        Electrolyzer e2 = new Electrolyzer();
        e2.maxPower = 3d;
        e2.efficiency = 2.0d;
        e2.accumulatorMaxSize = 0.0d;
        e2.summaryEnergyProduction = new double[]{20.0};

        Storage storage = new Storage();
        storage.maxCapacity = 5.d;
        storage.electrolyzers = Lists.newArrayList(e1, e2);

        Data data = new Data();
        data.gridConstants = gridConstants;
        data.summaryStorage = storage;
        data.vehiclesConsumption = new double[]{11.0};

        Step step = new Step();
        step.hour = 0;
        step.electorizersStates.put(e1, buildInitialState(0));
        step.electorizersStates.put(e2, buildInitialState(0));
        step.storageState = new StorageState(5);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = new Step();
        expectedStep.hour = 1;
        expectedStep.electorizersStates.put(e1, buildInitialState(0));
        expectedStep.electorizersStates.put(e2, buildInitialState(0));
        expectedStep.storageState = new StorageState(2);
        expectedStep.overflowPowerProduction = 25;
        expectedStep.overflowHydrogenProduction = 0;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    private Step initStep(Electrolyzer electrolyzer, int hour, double storageState, double accumulatorState) {
        Step step = new Step();
        step.hour = hour;
        step.electorizersStates.put(electrolyzer, buildInitialState(accumulatorState));
        step.storageState = new StorageState(storageState);
        return step;
    }

    private ElectrolyzerState buildInitialState(double accumulatorState) {
        return new ElectrolyzerState(accumulatorState);
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