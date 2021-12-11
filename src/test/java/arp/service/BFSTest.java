package arp.service;

import arp.dto.Electrolyzer;
import arp.dto.EnergySource;
import arp.dto.GridConstants;
import arp.dto.Storage;
import arp.enums.EnergySourceType;
import arp.search.BroadFirstSearchAlgorithm;
import arp.search.State;

import java.util.List;

class BFSTest {

    public static void main(String[] args) {
        BroadFirstSearchAlgorithm broadFirstSearchAlgorithm;
        GridConstants gridConstants = new GridConstants(0.0, 0.0, 0.0);
        Storage storage = new Storage();
        storage.maxCapacity = 0.0;
        List<EnergySource> sources = List.of(new EnergySource(EnergySourceType.WIND, 0.0, 0.0), new EnergySource(EnergySourceType.PV, 0.0, 0.0));
        storage.electrolyzers = List.of(new Electrolyzer(sources, 0, 0.0, 1.0, 0.0, 0.0, Utils.createTableOfValue(0.0)));
        Data data = new Data(gridConstants, storage, Utils.createTableOfValue(0.9));
        broadFirstSearchAlgorithm = new BroadFirstSearchAlgorithm(data);
        State state = broadFirstSearchAlgorithm.calculate();
        System.out.println("FOUND SOLUTION: " + state);
    }
}