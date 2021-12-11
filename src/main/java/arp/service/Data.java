package arp.service;

import arp.dto.GridConstants;
import arp.dto.Storage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Data {
    public GridConstants gridConstants;
    public Storage summaryStorage;
    public double[] vehiclesConsumption; // godzina w roku
}
