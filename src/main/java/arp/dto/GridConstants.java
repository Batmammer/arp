package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridConstants {
    public Double hydrogenTransportLoss;
    public Double storageLoss;
    public Double transmissionLoss;
    public final Double PV_COST = 3.0;
    public final Double WIND_COST = 7.0;
    public final Double STORAGE_POWER_COST = 4.0;
    public final Double ELECTROLIZER_COST = 10.0;
    public final Double STORAGE_HYDROGEN_COST = 2.0; //0.176;

}
