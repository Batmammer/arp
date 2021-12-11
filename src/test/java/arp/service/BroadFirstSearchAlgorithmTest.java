package arp.service;

import arp.dto.GridConstants;
import arp.dto.GridCosts;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Storage;
import arp.enums.EnergySourceType;
import arp.search.BroadFirstSearchAlgorithm;
import arp.search.State;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static arp.service.Utils.createTableOfValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BroadFirstSearchAlgorithmTest extends AbstractAlgorithmTest{

    @Test
    public void shouldAddPv() {
        // given
        double consumption = 1.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setWindCost(3.0d);
        gridCosts.setStoragePowerCost(10.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setStorageHydrogenCost(10.0d);

        Storage storage = new Storage();
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        String state = calculate(data);

        // then
        String expectedStateString = "7.0: PV, ELECTROLYZER";
        assertEquals(expectedStateString, state);
    }

    @Test
    public void shouldAddWind() {
        // given
        double consumption = 1.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(4.0d);
        gridCosts.setWindCost(3.0d);
        gridCosts.setStoragePowerCost(10.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setStorageHydrogenCost(10.0d);

        Storage storage = new Storage();
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        // when
        String state = calculate(data);

        // then
        String expectedStateString = "8.0: WIND, ELECTROLYZER";
        assertEquals(expectedStateString, state);
    }

    @Test
    public void shouldAddElectrolyzer() {
        // given
        double consumption = 1.0;

        GridConstants gridConstants = buildGridConstants();
        gridConstants.setElectrolyzerEfficiency(0.5d);

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setWindCost(3.0d);
        gridCosts.setStoragePowerCost(10.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setStorageHydrogenCost(10.0d);

        Storage storage = new Storage();
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        String state = calculate(data);

        // then
        String expectedStateString = "12.0: PV, ELECTROLYZER, ELECTROLYZER";
        assertEquals(expectedStateString, state);
    }

    @Test
    public void shouldAddMatchinPowerToVehiclesConsumption() {
        // given
        double consumption = 1.0;

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(4.0d);
        gridCosts.setWindCost(3.0d);
        gridCosts.setStoragePowerCost(10.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setStorageHydrogenCost(10.0d);

        GridConstants gridConstants = createGridConstants();
        gridConstants.getWindDailyProduction()[0] = 0;

        Storage storage = new Storage();
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        String state = calculate(data);

        // then
        String expectedStateString = "9.0: PV, ELECTROLYZER";
        assertEquals(expectedStateString, state);
    }

    @Test
    public void shouldPreferAccumulatorBeforeStorage() {
        // given
        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setWindCost(1000d);
        gridCosts.setPvCost(4.0d);
        gridCosts.setStoragePowerCost(2.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setStorageHydrogenCost(10.0d);

        Storage storage = new Storage();
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);

        gridConstants.setPvDailyProduction(createOneZeroTable());
        data.setVehiclesConsumption(createTableOfValue(1.0));

        // when
        String state = calculate(data);

        // then
        String expectedStateString = "15.0: PV, PV, ELECTROLYZER, ACCUMULATOR, ACCUMULATOR";
        assertEquals(expectedStateString, state);
    }

    @Test
    public void shouldPreferStorageBeforeAccumulator() {
        // given
        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setWindCost(1000d);
        gridCosts.setPvCost(4.0d);
        gridCosts.setStoragePowerCost(10.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setStorageHydrogenCost(2.0d);

        Storage storage = new Storage();
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);

        gridConstants.setPvDailyProduction(createOneZeroTable());
        data.setVehiclesConsumption(createTableOfValue(1.0));

        // when
        String state = calculate(data);

        // then
        String expectedStateString = "20.0: STORAGE, PV, PV, ELECTROLYZER";
        assertEquals(expectedStateString, state);
    }

    @Test
    public void shouldAddPvToFirstPathOfTwo() {
        // given
        double consumption = 4.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setWindCost(1000.0d);
        gridCosts.setStoragePowerCost(1000.0d);
        gridCosts.setStorageHydrogenCost(1000.0d);

        EnergySource s1 = new EnergySource();
        s1.setId(21l);
        s1.setDistance(0.0);
        s1.setMaxPower(1.0);
        s1.setType(EnergySourceType.PV);

        Electrolyzer e1 = buildElectrolyzerWithAccumulator();
        e1.setId(10l);
        e1.setEfficiency(1.0);
        e1.setMaxPower(2.0);
        e1.setSources(Lists.newArrayList(s1));

        EnergySource s2 = new EnergySource();
        s2.setId(22l);
        s2.setDistance(0.0);
        s2.setMaxPower(1.0);
        s2.setType(EnergySourceType.PV);

        EnergySource s3 = new EnergySource();
        s3.setId(23l);
        s3.setDistance(0.0);
        s3.setMaxPower(1.0);
        s3.setType(EnergySourceType.PV);

        Electrolyzer e2 = buildElectrolyzerWithAccumulator();
        e2.setId(11l);
        e2.setEfficiency(1.0);
        e2.setMaxPower(2.0);
        e2.setSources(Lists.newArrayList(s2, s3));

        Storage storage = new Storage();
        storage.setMaxCapacity(0.0);
        storage.setElectrolyzers(Lists.newArrayList(e1, e2));

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state = broadFirstSearchAlgorithm.calculate();

        // then
        String expectedStateString = "2.0: PV";
        assertEquals(expectedStateString, state.toString());
    }

    private GridConstants buildGridConstants() {
        GridConstants gridConstants = new GridConstants();
        gridConstants.setPvDailyProduction(createTableOfValue(1.0));
        gridConstants.setWindDailyProduction(createTableOfValue(1.0));
        gridConstants.setElectrolyzerEfficiency(1.0);
        return gridConstants;
    }

    private double[] createOneZeroTable() {
        double[] table = createTableOfValue(1.0d);
        for (int i = 0; i < table.length; i++) {
            if (i % 2 == 1) {
                table[i] = 0.0;
            }
        }
        return table;
    }


    private String calculate(Data data) {
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        return broadFirstSearchAlgorithm.calculate().toString();
    }

    private GridConstants createGridConstants() {
        GridConstants gridConstants = new GridConstants();
        gridConstants.setHydrogenTransportLoss(0.d);
        gridConstants.setStorageLoss(0.d);
        gridConstants.setTransmissionLoss(0.d);
        gridConstants.setPvDailyProduction(createTableOfValue(1.0));
        gridConstants.setWindDailyProduction(createTableOfValue(1.0));
        gridConstants.setElectrolyzerEfficiency(1.0);
        return gridConstants;
    }
}