package arp.service;

import arp.dto.Electrolyzer;
import org.junit.jupiter.api.Test;

import static arp.service.Utils.createTableOfValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateMaximumConsumptionTest extends AbstractAlgorithmTest {
    @Test
    public void shouldUseFullElectrolyzerMaxPowerEventIfPowerIsGreater() {
        // given
        double storageMaxCapacity = 0.0d;

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 2d;
        electrolyzer.efficiency = 3.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = createTableOfValue(3.0);
        Data data = buildData(electrolyzer, storageMaxCapacity, null);

        // when
        double value = new CalculateMaximumConsumption(data).calculate();

        // then
        double expectedValue = 6.0;
        assertEquals(expectedValue, value);
    }

    @Test
    public void shouldUseFullElectrolyzerMaxPower() {
        // given
        double storageMaxCapacity = 0.0d;
        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 2d;
        electrolyzer.efficiency = 3.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = createTableOfValue(2.0);
        Data data = buildData(electrolyzer, storageMaxCapacity, null);

        // when
        double value = new CalculateMaximumConsumption(data).calculate();

        // then
        double expectedValue = 6.0;
        assertEquals(expectedValue, value);
    }

    @Test
    public void shouldUseHalfElectrolyzerMaxPower() {
        // given
        double storageMaxCapacity = 0.0d;

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

    @Test
    public void shouldReturnZero() {
        // given
        double storageMaxCapacity = 0.0d;

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 0d;
        electrolyzer.efficiency = 3.0d;
        electrolyzer.accumulatorMaxSize = 0.d;
        electrolyzer.summaryEnergyProduction = createTableOfValue(1.0);
        Data data = buildData(electrolyzer, storageMaxCapacity, null);

        // when
        double value = new CalculateMaximumConsumption(data).calculate();

        // then
        double expectedValue = 0.0;
        assertEquals(expectedValue, value);
    }

    @Test
    public void shouldUseFullyAccumulator() {
        // given
        double storageMaxCapacity = 0.0d;

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 2.0d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 4.d;
        electrolyzer.summaryEnergyProduction = createTableOfValue(0.0);
        for (int i = 0; i < electrolyzer.summaryEnergyProduction.length; i++) {
            if (i % 3 == 0) {
                electrolyzer.summaryEnergyProduction[i] = 6;
            }
        }
        Data data = buildData(electrolyzer, storageMaxCapacity, null);

        // when
        double value = new CalculateMaximumConsumption(data).calculate();

        // then
        double expectedValue = 2.0;
        assertEquals(expectedValue, value);
    }

    @Test
    public void shouldUseAccumulatorButWithEnoughCapacity() {
        // given
        double storageMaxCapacity = 0.0d;

        Electrolyzer electrolyzer = new Electrolyzer();
        electrolyzer.maxPower = 2.0d;
        electrolyzer.efficiency = 1.0d;
        electrolyzer.accumulatorMaxSize = 3.0d;
        electrolyzer.summaryEnergyProduction = createTableOfValue(0.0);
        for (int i = 0; i < electrolyzer.summaryEnergyProduction.length; i++) {
            if (i % 3 == 0) {
                electrolyzer.summaryEnergyProduction[i] = 6;
            }
        }
        Data data = buildData(electrolyzer, storageMaxCapacity, null);

        // when
        double value = new CalculateMaximumConsumption(data).calculate();

        // then
        double expectedValue = 1.0;
        assertEquals(expectedValue, value);
    }
}