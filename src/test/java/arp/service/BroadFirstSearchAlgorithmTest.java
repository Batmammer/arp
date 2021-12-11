package arp.service;

import arp.dto.GridConstants;
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
        gridConstants.pvCost = 2.0d;
        gridConstants.windCost = 3.0d;
        gridConstants.storagePowerCost = 10.0d;
        gridConstants.electrolizerCost = 5.0d;
        gridConstants.storageHydrogenCost = 10.0d;

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 1.0d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 0.0d;
        electrolyzer.summaryEnergyProduction = createTableOfValue(0.0);

        Storage storage = new Storage();
        storage.maxCapacity = 0.0;
        storage.electrolyzers = Lists.newArrayList(electrolyzer);

        Data data = new Data();
        data.gridConstants = gridConstants;
        data.summaryStorage = storage;
        data.vehiclesConsumption = createTableOfValue(consumption);

        // when
        double cost = calculate(data);

        // then
        double expectedCost = 2.0;
        assertEquals(expectedCost, cost);
    }

    private double calculate(Data data) {
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state  = broadFirstSearchAlgorithm.calculate();
        return state.totalCost;
    }

    private GridConstants createGridConstants() {
        GridConstants gridConstants = new GridConstants();
        gridConstants.hydrogenTransportLoss = 0.d;
        gridConstants.storageLoss = 0.d;
        gridConstants.transmissionLoss = 0.d;
        gridConstants.pvDailyProduction = createTableOfValue(1.0);
        gridConstants.windDailyProduction = createTableOfValue(1.0);
        return gridConstants;
    }
}