package arp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Storage {
    public Double maxCapacity;
    public List<Electrolyzer> electrolyzers;
}
