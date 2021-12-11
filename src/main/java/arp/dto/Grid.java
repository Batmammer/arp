package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grid {
    public WatSide watSide;
    public HydrogenSide hydrogenSide;
}
