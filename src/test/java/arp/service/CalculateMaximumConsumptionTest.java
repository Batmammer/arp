package arp.service;

import arp.dto.Electrolyzer;
import arp.dto.GridConstants;
import arp.dto.Storage;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculateMaximumConsumptionTest extends AbstractAlgorithmTest {
    @Test
    public void shouldUseEfficiency() {
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

        // when
        new CalculateMaximumConsumption(data);
        // then
    }
}