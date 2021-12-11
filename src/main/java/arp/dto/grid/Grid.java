package arp.dto.grid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Grid {
    private List<Vehicle> vehicles;
    private List<Storage> storages;
}
