package arp.service;

import arp.dto.GridConstants;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.Storage;
import arp.exception.BusinessException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculateNextStepAlgorithmTest extends AbstractAlgorithmTest {
    @Test
    public void shouldUseCurrentProductionFromElectrolizerWhenStorageInEmpty() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {1.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseStorageWhenProductionFromElectrolizerIsZero() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {1.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(0d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0d);
        expectedStep.overflowPowerProduction = 1.0d;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseBothProductionFromElectrolizerAndStorageIsZero() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(1d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedProductionFromElectrolizer() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(1d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, -1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedStorage() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(0d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{0.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, -1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedMaxPowerAndOverproduction() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {4.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(2d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{98.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, -2d, 0d);
        expectedStep.overflowPowerProduction = 96;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shoudTreatMinusStorageAsZero() {
        // given
        double storageMaxCapacity = 1.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(0d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, -1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0d);
        expectedStep.overflowPowerProduction = 1.0;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void loadStorageToHalf() {
        // given
        double storageMaxCapacity = 2.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseEfficiency() {
        // given
        double storageMaxCapacity = 2.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(2.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(electrolyzer.getId(), new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 2d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 2d, 0d);
        expectedStep.overflowHydrogenProduction = 2.0;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseStorageLimitAndTestOverFlowHydrogenProduction() {
        // given
        double storageMaxCapacity = 2.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(3.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 2d, 0d);
        expectedStep.overflowHydrogenProduction = 1.0d;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseAccumulator() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {1.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(1.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{0.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 1d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseBothAccumulatorAndProduction() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(1.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 1d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shouldUseRightIndex() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {2.0, 3.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{5.0, 1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 2, -2d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedAculumulator() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(1.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{0.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 1d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, -1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void exceedProduction() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{1.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, -1d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void overflowPowerProduction() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(1d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{2.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0d);
        expectedStep.overflowPowerProduction = 1;
        expectedStep.overflowHydrogenProduction = 1;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void useStorageLoss() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {2.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{1.0});
        data.gridConstants.setStorageLoss( 0.2 * 24);

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 1d, 0d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, -0.2d, 0d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void shoudThrowExceptionWhenRichMinPower() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(100d);
        electrolyzer.setMinPower(1d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{0.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 0d);

        // when
        boolean wasException = false;
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        try {
            algorithm.calculate(step);
        } catch (BusinessException ex) {
            wasException = true;
        }

        // then
        assertTrue(wasException);
    }

    @Disabled("Jeszcze nie gotowa implementacja")
    @Test
    public void shouldNotUseAccumulatorWhenThereisNoConsumption() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(1d);
        electrolyzer.setMinPower(0d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(1.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{0.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 1d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 1d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Disabled("Jeszcze nie gotowa implementacja")
    @Test
    public void shouldUseAccumulatorWhenThereisNoConsumptionButIsMinimumPower() {
        // given
        double storageMaxCapacity = 0.0d;
        double[] consumption = {0.0};

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(1d);
        electrolyzer.setMinPower(0.2d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(1.0d);
        Data data = buildData(electrolyzer, storageMaxCapacity, consumption);
        data.getSummaryEnergyProduction().put(1l, new double[]{0.0});

        Storage storage = data.getStorages().get(0);
        Step step = initStep(storage, electrolyzer.getAccumulator(), 0, 0d, 1d);

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = initStep(storage, electrolyzer.getAccumulator(), 1, 0d, 0.8d);
        assertEquals(expectedStep.toString(), resultStep.toString());
    }

    @Test
    public void comboProductions() {
        // given
        GridConstants gridConstants = new GridConstants();
        gridConstants.setHydrogenTransportLoss( 0d);
        gridConstants.setStorageLoss( 0d);
        gridConstants.setTransmissionLoss(0d);

        Electrolyzer e1 = buildElectrolyzerWithAccumulator();
        e1.setMaxPower(2d);
        e1.setEfficiency(1.0d);
        e1.getAccumulator().setAccumulatorMaxSize(0.0d);

        Electrolyzer e2 = buildElectrolyzerWithAccumulator();
        e2.setId(2l);
        e2.setMaxPower(3d);
        e2.setEfficiency(2.0d);
        e2.getAccumulator().setAccumulatorMaxSize(0.0d);

        Storage storage = new Storage();
        storage.setMaxCapacity(5.d);
        storage.setElectrolyzers(Lists.newArrayList(e1, e2));

        Data data = new Data();
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(new double[]{11.0});
        data.getSummaryEnergyProduction().put(e1.getId(), new double[]{10.0});
        data.getSummaryEnergyProduction().put(e2.getId(), new double[]{20.0});

        Step step = new Step();
        step.hour = 0;
        step.acumulatorsStates.put(e1.getAccumulator(), buildInitialState(0));
        step.acumulatorsStates.put(e2.getAccumulator(), buildInitialState(0));
        step.storageStates.put(storage, new StorageState(5));

        // when
        CalculateNextStepAlgorithm algorithm = new CalculateNextStepAlgorithm(data);
        Step resultStep = algorithm.calculate(step);

        // then
        Step expectedStep = new Step();
        expectedStep.hour = 1;
        expectedStep.acumulatorsStates.put(e1.getAccumulator(), buildInitialState(0));
        expectedStep.acumulatorsStates.put(e2.getAccumulator(), buildInitialState(0));
        expectedStep.storageStates.put(storage, new StorageState(2));
        expectedStep.overflowPowerProduction = 25;
        expectedStep.overflowHydrogenProduction = 0;
        assertEquals(expectedStep.toString(), resultStep.toString());
    }



}