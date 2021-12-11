package arp.service;

import arp.dto.GridConstants;
import arp.dto.GridCosts;
import arp.dto.grid.Storage;
import arp.search.BroadFirstSearchAlgorithm;
import arp.search.State;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static arp.service.Utils.createTableOfValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BroadFirstSearchAlgorithmTest {

    @Test
    public void shouldAddPv() {
        // given
        double consumption = 1.0;

        GridConstants gridConstants = createGridConstants();
        gridConstants.setPvCost(2.0d;
        gridConstants.setWindCost(3.0d;
        gridConstants.setStoragePowerCost(10.0d;
        gridConstants.setElectrolizerCost(5.0d;
        gridConstants.setStorageHydrogenCost(10.0d;

        Storage storage = new Storage();
        storage.maxCapacity = 0.0;

        Data data = new Data();
        data.gridConstants = gridConstants;
        data.summaryStorage = storage;
        data.vehiclesConsumption = createTableOfValue(consumption);

        // when
        double cost = calculate(data);

        // then
        double expectedCost = 7.0d;
        assertEquals(expectedCost, cost);
    }

    @Test
    public void shouldAddWind() {
        // given
        double consumption = 1.0;

        GridConstants gridConstants = createGridConstants();
        gridConstants.setPvCost(4.0d;
        gridConstants.setWindCost(3.0d;
        gridConstants.setStoragePowerCost(10.0d;
        gridConstants.setElectrolizerCost(5.0d;
        gridConstants.setStorageHydrogenCost(10.0d;

        Storage storage = new Storage();
        storage.maxCapacity = 0.0;

        Data data = new Data();
        data.gridConstants = gridConstants;
        data.summaryStorage = storage;
        data.vehiclesConsumption = createTableOfValue(consumption);

        // when
        double cost = calculate(data);

        // then
        double expectedCost = 8.0d;
        assertEquals(expectedCost, cost);
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
        data.setGridConstants(gridConstants);
        data.setSummaryStorage(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        double cost = calculate(data);

        // then
        double expectedCost = 9.0d;
        assertEquals(expectedCost, cost);
    }

    @Test
    public void shouldPreferAccumulatorBeforePower() {
        // given
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
        data.setGridConstants(gridCosts);
        data.setSummaryStorage(storage);

        gridCosts.setPvDailyProduction(createOneHalfTable());
        data.vehiclesConsumption = createOneTwoTable();

        // when
        double cost = calculate(data);

        // then
        double expectedCost = 9.0d;
        assertEquals(expectedCost, cost);
    }

    private double[] createOneHalfTable() {
        double[] table = createTableOfValue(1.0d);
        for (int i = 0; i < table.length; i++) {
            if (i % 2 == 1) {
                table[i] = 0.5;
            }
        }
        return table;
    }

    private double[] createOneTwoTable() {
        double[] table = createTableOfValue(2.0d);
        for (int i = 0; i < table.length; i++) {
            if (i % 2 == 0) {
                table[i] = 1;
            }
        }
        return table;
    }

    private double calculate(Data data) {
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state  = broadFirstSearchAlgorithm.calculate();
        System.out.println(state);
        return state.totalCost;
    }

    private GridConstants createGridConstants() {
        GridConstants gridConstants = new GridConstants();
        gridConstants.setHydrogenTransportLoss(0.d);
        gridConstants.setStorageLoss(0.d);
        gridConstants.setTransmissionLoss(0.d);
        gridConstants.setPvDailyProduction(createTableOfValue(1.0));
        gridConstants.setWindDailyProduction(createTableOfValue(1.0));
        gridConstants.setElectrolizerEfficiency(1.0);
        return gridConstants;
    }
}