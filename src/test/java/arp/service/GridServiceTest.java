package arp.service;

import arp.dto.*;
import arp.enums.EnergySourceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GridServiceTest {

    GridService gridService = new GridService();

    @Test
    void runSimpleSimulation() {
        //given
        InputGrid inputGrid = getInputGrid();

        //when
        YearResult result = gridService.runSimulation(inputGrid);

        //then
        assertEquals(result.good, true);
    }

    private InputGrid getInputGrid() {
        return new InputGrid(getGrid(), getConstants());
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
        Electrolyzer e = new Electrolyzer();
        e.setMaxPower(100d);
        e.setMinPower(10d);
        e.setEfficiency(50);
        e.setAccumulatorMaxSize(100d);
        e.setSources(Collections.singletonList(new EnergySource(EnergySourceType.WIND, 100d, 0d)));
        return e;
    }

    private GridConstants getConstants() {
        return new GridConstants(0.0, 0.0, 0.0);
    }
}