package arp.service;

import arp.dto.grid.Electrolyzer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static arp.service.Utils.createTableOfValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateMaximumConsumptionTest extends AbstractAlgorithmTest {
    @Test
    public void shouldUseFullElectrolyzerMaxPowerEventIfPowerIsGreater() {
        // given
        double storageMaxCapacity = 0.0d;

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(2d);
        electrolyzer.setEfficiency(3.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        electrolyzer.setSummaryEnergyProduction(createTableOfValue(3.0));
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
        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(2d);
        electrolyzer.setEfficiency(3.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        electrolyzer.setSummaryEnergyProduction(createTableOfValue(2.0));
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

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(2d);
        electrolyzer.setEfficiency(3.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        electrolyzer.setSummaryEnergyProduction(createTableOfValue(1.0));
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

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(0d);
        electrolyzer.setEfficiency(3.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(0.d);
        electrolyzer.setSummaryEnergyProduction(createTableOfValue(1.0));
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

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(2.0d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(4.d);
        double[] tableOfValue = createTableOfValue(0.0);
        for (int i = 0; i < tableOfValue.length; i++) {
            if (i % 3 == 0) {
                tableOfValue[i] = 6;
            }
        }
        electrolyzer.setSummaryEnergyProduction(tableOfValue);

        Data data = buildData(electrolyzer, storageMaxCapacity, null);

        // when
        double value = new CalculateMaximumConsumption(data).calculate();

        // then
        double expectedValue = 2.0;
        assertEquals(expectedValue, value);
    }

    @Disabled("Jeszcze nie gotowa implementacja")
    @Test
    public void shouldUseAccumulatorButWithEnoughCapacity() {
        // given
        double storageMaxCapacity = 0.0d;

        Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
        electrolyzer.setMaxPower(2.0d);
        electrolyzer.setEfficiency(1.0d);
        electrolyzer.getAccumulator().setAccumulatorMaxSize(3.0d);
        double[] tableOfValue = createTableOfValue(0.0);
        for (int i = 0; i < tableOfValue.length; i++) {
            if (i % 3 == 0) {
                tableOfValue[i] = 6;
            }
        }
        electrolyzer.setSummaryEnergyProduction(tableOfValue);
        Data data = buildData(electrolyzer, storageMaxCapacity, null);

        // when
        double value = new CalculateMaximumConsumption(data).calculate();

        // then
        double expectedValue = 1.5;
        assertEquals(expectedValue, value);
    }
}