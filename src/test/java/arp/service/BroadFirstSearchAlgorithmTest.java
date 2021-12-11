package arp.service;

import arp.dto.GridConstants;
import arp.dto.GridCosts;
import arp.dto.grid.Electrolyzer;
import arp.dto.grid.EnergySource;
import arp.dto.grid.Storage;
import arp.enums.EnergySourceType;
import arp.search.BroadFirstSearchAlgorithm;
import arp.search.State;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static arp.service.Utils.createTableOfValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BroadFirstSearchAlgorithmTest extends AbstractAlgorithmTest{

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
        storage.setId(1l);
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        String state = calculate(data);

        // then
        String expectedStateString = "7.0: ELECTROLYZER, PV";
        assertEquals(expectedStateString, state);
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
        storage.setId(1l);
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
        String expectedStateString = "8.0: ELECTROLYZER, WIND";
        assertEquals(expectedStateString, state);
    }

    @Test
    public void shouldAddElectrolyzer() {
        // given
        double consumption = 1.0;

        GridConstants gridConstants = buildGridConstants();
        gridConstants.setElectrolyzerEfficiency(0.5d);

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setWindCost(3.0d);
        gridCosts.setStoragePowerCost(10.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setStorageHydrogenCost(10.0d);

        Storage storage = new Storage();
        storage.setId(1l);
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        String state = calculate(data);

        // then
        String expectedStateString = "12.0: ELECTROLYZER, ELECTROLYZER, PV";
        assertEquals(expectedStateString, state);
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
        storage.setId(1l);
        storage.setMaxCapacity(0.0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.getStorages().add(storage);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        String state = calculate(data);

        // then
        String expectedStateString = "9.0: ELECTROLYZER, PV";
        assertEquals(expectedStateString, state);
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
        storage.setId(1l);
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
        String expectedStateString = "15.0: ACCUMULATOR, ACCUMULATOR, ELECTROLYZER, PV, PV";
        assertEquals(expectedStateString, state);
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
        storage.setId(1l);
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
        String expectedStateString = "20.0: ELECTROLYZER, PV, PV, STORAGE";
        assertEquals(expectedStateString, state);
    }

    @Test
    public void shouldNotAddAnything() {
        // given
        double consumption = 4.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setWindCost(1000.0d);
        gridCosts.setStoragePowerCost(1000.0d);
        gridCosts.setStorageHydrogenCost(1000.0d);

        List<Storage> storages = buildFullTreeOfStorages(2, 1, 2);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.setStorages(storages);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state = broadFirstSearchAlgorithm.calculate();

        // then
        String expectedStateString = "0.0: ";
        assertEquals(expectedStateString, state.toString());
    }

    @Test
    public void shouldAddPvToFirstPathOfTwo() {
        // given
        double consumption = 4.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setWindCost(1000.0d);
        gridCosts.setStoragePowerCost(1000.0d);
        gridCosts.setStorageHydrogenCost(1000.0d);

        List<Storage> storages = buildFullTreeOfStorages(2, 1, 2);
        storages.get(0).getElectrolyzers().get(0).getSources().remove(0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.setStorages(storages);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state = broadFirstSearchAlgorithm.calculate();

        // then
        String expectedStateString = "2.0: PV";
        assertEquals(expectedStateString, state.toString());
    }

    @Test
    public void shouldAddPvToSecondPathOfTwo() {
        // given
        double consumption = 4.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setWindCost(1000.0d);
        gridCosts.setStoragePowerCost(1000.0d);
        gridCosts.setStorageHydrogenCost(1000.0d);

        List<Storage> storages = buildFullTreeOfStorages(2, 1, 2);
        storages.get(1).getElectrolyzers().get(0).getSources().remove(0);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.setStorages(storages);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state = broadFirstSearchAlgorithm.calculate();

        // then
        String expectedStateString = "2.0: PV";
        assertEquals(expectedStateString, state.toString());
    }

    @Test
    public void shouldAddPvToTwoOfEightPaths() {
        // given
        double consumption = 8.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setWindCost(1000.0d);
        gridCosts.setStoragePowerCost(1000.0d);
        gridCosts.setStorageHydrogenCost(1000.0d);

        List<Storage> storages = buildFullTreeOfStorages(2, 2, 2);
        storages.get(0).getElectrolyzers().get(0).getSources().remove(0);
        storages.get(1).getElectrolyzers().get(1).getSources().remove(1);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.setStorages(storages);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state = broadFirstSearchAlgorithm.calculate();

        // then
        String expectedStateString = "4.0: PV, PV";
        assertEquals(expectedStateString, state.toString());
    }

    @Test
    public void shouldAddElectrolyzerToFirstPathOfFour() {
        // given
        double consumption = 4.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setWindCost(1000.0d);
        gridCosts.setStoragePowerCost(1000.0d);
        gridCosts.setStorageHydrogenCost(1000.0d);

        List<Storage> storages = buildFullTreeOfStorages(2, 2, 1);
        storages.get(0).getElectrolyzers().get(0).setEfficiency(0.5);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.setStorages(storages);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state = broadFirstSearchAlgorithm.calculate();

        // then
        String expectedStateString = "5.0: ELECTROLYZER";
        assertEquals(expectedStateString, state.toString());
    }

    @Test
    public void bigTest666add7Pvs() {
        // given
        double consumption = 216.0;

        GridConstants gridConstants = buildGridConstants();

        GridCosts gridCosts = new GridCosts();
        gridCosts.setPvCost(2.0d);
        gridCosts.setElectrolyzerCost(5.0d);
        gridCosts.setWindCost(1000.0d);
        gridCosts.setStoragePowerCost(1000.0d);
        gridCosts.setStorageHydrogenCost(1000.0d);

        List<Storage> storages = buildFullTreeOfStorages(6, 6, 6);
        storages.get(0).getElectrolyzers().get(0).getSources().remove(5);
        storages.get(0).getElectrolyzers().get(0).getSources().remove(4);
        storages.get(0).getElectrolyzers().get(0).getSources().remove(3);
        storages.get(0).getElectrolyzers().get(0).getSources().remove(2);
        storages.get(0).getElectrolyzers().get(0).getSources().remove(1);
        storages.get(0).getElectrolyzers().get(0).getSources().remove(0);
        storages.get(0).getElectrolyzers().get(1).getSources().remove(5);

        Data data = new Data();
        data.setGridCosts(gridCosts);
        data.setGridConstants(gridConstants);
        data.setStorages(storages);
        data.setVehiclesConsumption(createTableOfValue(consumption));

        // when
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state = broadFirstSearchAlgorithm.calculate();

        // then
        String expectedStateString = "14.0: PV, PV, PV, PV, PV, PV, PV";
        assertEquals(expectedStateString, state.toString());
    }

    private List<Storage> buildFullTreeOfStorages(int storagesAmount, int electrolizersPerStorage, int powersPerElectorizer) {
        List<Storage> storages = new ArrayList<>();

        long sIdx = 100;
        long eIdx = 200;
        long pIdx = 300;

        for (int s = 0; s < storagesAmount; s++) {
            List<Electrolyzer> electrolyzers = new ArrayList<>();
            for (int e = 0; e < electrolizersPerStorage; e++) {
                List<EnergySource> powers = new ArrayList<>();
                for (int p = 0; p < powersPerElectorizer; p++) {
                    EnergySource power = new EnergySource();
                    power.setId(pIdx++);
                    power.setDistance(0.0);
                    power.setMaxPower(1.0);
                    power.setType(EnergySourceType.PV);
                    powers.add(power);
                }

                Electrolyzer electrolyzer = buildElectrolyzerWithAccumulator();
                electrolyzer.setId(eIdx++);
                electrolyzer.setEfficiency(1.0);
                electrolyzer.setMaxPower(powersPerElectorizer);
                electrolyzer.setSources(powers);
                electrolyzers.add(electrolyzer);
            }

            Storage storage = new Storage();
            storage.setId(sIdx++);
            storage.setMaxCapacity(0.0);
            storage.setElectrolyzers(electrolyzers);
            storages.add(storage);
        }
        return storages;
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