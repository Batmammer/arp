package arp.dto.output;

import lombok.Data;

import java.util.List;

@Data
public class StorageOutput {
    private Double maxCapacity;
    private List<ElectrolyzerOutput> electrolyzers;
}
