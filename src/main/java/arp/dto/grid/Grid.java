package arp.dto.grid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Grid {
    @Schema(description = "Vehicles (types) list")
    private List<Vehicle> vehicles;
    @Schema(description = "Storages list")
    private List<Storage> storages;
}
