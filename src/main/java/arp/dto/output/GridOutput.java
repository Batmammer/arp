package arp.dto.output;

import lombok.Data;

import java.util.List;

@Data
public class GridOutput {
    private List<VehicleOutput> vehicles;
    private List<StorageOutput> storages;
}
