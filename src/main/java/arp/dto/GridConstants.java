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
}
