package arp.service;

import arp.dto.*;
import arp.dto.grid.*;
import arp.dto.util.WeeklyPeriod;
import arp.enums.EnergySourceType;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        return new GridInput(getGrid(), getConstants(), getCosts());
    }

    private Grid getGrid() {
        return new Grid(Collections.singletonList(
                new Vehicle(11L, 10L, getWeeklyWork(), 1.0, 0L)),
                Collections.singletonList(getStorage()));
    }

    private List<WeeklyPeriod> getWeeklyWork() {
        return Collections.singletonList(new WeeklyPeriod(0, 6, 0, 23));
    }

    private Storage getStorage() {
        return new Storage(21L, 1000.0, Collections.singletonList(getElectrolyzer()));
    }

    private Electrolyzer getElectrolyzer() {
        EnergySource energySource = new EnergySource();
        energySource.setId(31L);
        energySource.setType(EnergySourceType.WIND);
        energySource.setMaxPower(100d);
        energySource.setDistance(0d);

        Electrolyzer e = new Electrolyzer();
        e.setId(41L);
        e.setMaxPower(50d);
        e.setEfficiency(50d);
        e.setAccumulator(new Accumulator(100d));
        e.setSources(Lists.newArrayList(energySource));
        return e;
    }

    private GridConstants getConstants() {
        GridConstants gridConstants = new GridConstants();
        gridConstants.setHydrogenTransportLoss(0d);
        gridConstants.setStorageLoss(0d);
        gridConstants.setTransmissionLoss(0d);
        gridConstants.setWindDailyProduction(Utils.createTableOfValue(1d, 24));
        gridConstants.setPvDailyProduction(Utils.createTableOfValue(1d, 24));
        return gridConstants;
    }

    private GridCosts getCosts() {
        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(10d);
        gridCosts.setWindCost(10d);
        gridCosts.setElectrolyzerCost(20d);
        gridCosts.setStorageHydrogenCost(20d);
        gridCosts.setStoragePowerCost(20d);
        return gridCosts;
    }
}