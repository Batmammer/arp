package arp.service;

import arp.dto.*;
import arp.enums.EnergySourceType;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GridServiceTest {

    GridService gridService = new GridService();

    @Test
    void runSimpleSimulation() {
        //given
        GridInput gridInput = getInputGrid();

        //when
        YearResult result = gridService.runSimulation(gridInput);

        //then
        assertEquals(result.isGood(), true);
    }

    private GridInput getInputGrid() {
        return new GridInput(getGrid(), getConstants());
    }

    private Grid getGrid() {
        return new Grid(getWatSide(), getHydrogenSide());
    }

    private HydrogenSide getHydrogenSide() {
        return new HydrogenSide(
                Collections.singletonList(
                        new Vehicle("Truck", 100L, getWeeklyWork(), 1.0, 0L)));
    }

    private boolean[] getWeeklyWork() {
        boolean weeklyWork[] = new boolean[24 * 7];
        Arrays.fill(weeklyWork, true);
        return weeklyWork;
    }

    private WatSide getWatSide() {
        return new WatSide(getStorage());
    }

    private Storage getStorage() {
        return new Storage(1000.0, Collections.singletonList(getElectrolyzer()));
    }

    private Electrolyzer getElectrolyzer() {
        EnergySource energySource = new EnergySource();
        energySource.type = EnergySourceType.WIND;
        energySource.maxPower = 100d;
        energySource.distance = 0d;

        Electrolyzer e = new Electrolyzer();
        e.maxPower = 10d;
        e.minPower = 1d;
        e.efficiency = 10;
        e.accumulatorMaxSize = 100d;
        e.sources = Lists.newArrayList(energySource);
        return e;
    }

    private GridConstants getConstants() {
        GridConstants gridConstants = new GridConstants();
        gridConstants.hydrogenTransportLoss = 0.d;
        gridConstants.storageLoss = 0.d;
        gridConstants.transmissionLoss = 0.d;
        return gridConstants;
    }
}