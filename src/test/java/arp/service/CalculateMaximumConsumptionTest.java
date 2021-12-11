package arp.service;

import arp.dto.Electrolyzer;
import org.junit.jupiter.api.Test;

import static arp.service.Utils.createTableOfValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateMaximumConsumptionTest extends AbstractAlgorithmTest {
    @Test
    public void shouldUseEfficiency() {
        // given
        double storageMaxCapacity = 1.0d;

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 2d;
        electrolyzer.efficiency = 3.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = createTableOfValue(1.0);
        Data data = buildData(electrolyzer, storageMaxCapacity, null);

        // when
        double value = new CalculateMaximumConsumption(data).calculate();

        // then
        double expectedValue = 3.0;
        assertEquals(expectedValue, value);
    }
}