package arp.service;

import arp.dto.GridConstants;
import arp.dto.GridCosts;
import arp.dto.grid.Storage;
import arp.search.BroadFirstSearchAlgorithm;
import arp.search.State;
import org.junit.jupiter.api.Test;

import static arp.service.Utils.createTableOfValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BroadFirstSearchAlgorithmTest {

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
        String esxpectedStateString = "7.0: PV, STORAGE";
        assertEquals(esxpectedStateString, state);
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
        String esxpectedStateString = "8.0: WIND, STORAGE";
        assertEquals(esxpectedStateString, state);
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
        String esxpectedStateString = "9.0: PV, ELECTROLYZER";
        assertEquals(esxpectedStateString, state);
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
        String esxpectedStateString = "15.0: PV, PV, ELECTROLYZER, ACCUMULATOR, ACCUMULATOR";
        assertEquals(esxpectedStateString, state);
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
        String esxpectedStateString = "20.0: STORAGE, PV, PV, ELECTROLYZER";
        assertEquals(esxpectedStateString, state);
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

    private double[] createOneTwoTable() {
        double[] table = createTableOfValue(1.0d);
        for (int i = 0; i < table.length; i++) {
            if (i % 2 == 0) {
                table[i] = 1;
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