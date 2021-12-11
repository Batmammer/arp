package arp.controller;

import arp.dto.GridInput;
import arp.dto.GridResult;
import arp.dto.ValidationResult;
import arp.search.State;
import arp.service.GridService;
import arp.service.MaxConsumptionYearResult;
import arp.service.Step;
import arp.service.YearResult;
import io.swagger.v3.oas.annotations.Operation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GridController {
    private final GridService gridService;

    public GridController(GridService gridService) {
        this.gridService = gridService;
    }

    @Operation(summary = "Validate grid given as an input")
    @PostMapping("/validateGrid")
    public ValidationResult validateGrid(@RequestBody GridInput gridInput) {
        YearResult yearResult = gridService.runSimulation(gridInput);
        ValidationResult validationResult = new ValidationResult();
        validationResult.setGrid(gridInput.getGrid());
        validationResult.setIsValid(yearResult.isGood());
        double[] vehicleConsumption = gridService.calculateYearlyConsumption(gridInput.getGrid().getVehicles(),
                gridInput.getConstants().getHydrogenTransportLoss());
        validationResult.setMaxVehicleConsumption(Arrays.stream(vehicleConsumption).max().getAsDouble());
        List<Double> electricityProduction = new ArrayList<>();
        List<Double> hydrogenProduction = new ArrayList<>();
        List<Double> hydrogenLevel = new ArrayList<>();
        for (Step s : yearResult.getSteps()) {
            hydrogenProduction.add(s.getHydrogenProduction());
            electricityProduction.add(s.getElectricityProduction());
            hydrogenLevel.add(s.getStorageStates().values().stream().map(ss ->
                    ss.getCurrentLevel()).collect(Collectors.summingDouble(Double::doubleValue)));
        }
        validationResult.setMinHydrogenProduction(hydrogenProduction.stream().min(Double::compareTo).get());
        validationResult.setResMaxPower(electricityProduction.stream().max(Double::compareTo).get());
        validationResult.setResAnnualCapacity(electricityProduction.stream()
                .collect(Collectors.summingDouble(Double::doubleValue)));
        validationResult.setHydrogenProduction(hydrogenProduction);
        validationResult.setElectricityProduction(electricityProduction);
        validationResult.setErrors(yearResult.getErrors());
        validationResult.setWarnings(yearResult.getWarnings());
        validationResult.setHydrogenLevel(hydrogenLevel);
        if (validationResult.getIsValid())
            generateCharts(electricityProduction, hydrogenProduction);
        return validationResult;
    }

    @Operation(summary = "Calculate minimal hydrogen production during year")
    @PostMapping("/hydrogenProduction")
    public ValidationResult hydrogenProduction(@RequestBody GridInput gridInput) {
        MaxConsumptionYearResult minHydrogenProduction = gridService.calculateHydrogen(gridInput);
        YearResult yearResult = minHydrogenProduction.getYearResult();
        ValidationResult validationResult = new ValidationResult();
        validationResult.setGrid(gridInput.getGrid());
        validationResult.setIsValid(yearResult.isGood());
        double[] vehicleConsumption = gridService.calculateYearlyConsumption(gridInput.getGrid().getVehicles(),
                gridInput.getConstants().getHydrogenTransportLoss());
        validationResult.setMaxVehicleConsumption(Arrays.stream(vehicleConsumption).max().getAsDouble());
        List<Double> electricityProduction = new ArrayList<>();
        List<Double> hydrogenProduction = new ArrayList<>();
        List<Double> hydrogenLevel = new ArrayList<>();
        for (Step s : yearResult.getSteps()) {
            hydrogenProduction.add(s.getHydrogenProduction());
            electricityProduction.add(s.getElectricityProduction());
            hydrogenLevel.add(s.getStorageStates().values().stream().map(ss ->
                    ss.getCurrentLevel()).collect(Collectors.summingDouble(Double::doubleValue)));
        }
        validationResult.setMinHydrogenProduction(minHydrogenProduction.getMaxConsumption());
        validationResult.setResMaxPower(electricityProduction.stream().max(Double::compareTo).get());
        validationResult.setResAnnualCapacity(electricityProduction.stream()
                .collect(Collectors.summingDouble(Double::doubleValue)));
        validationResult.setHydrogenProduction(hydrogenProduction);
        validationResult.setElectricityProduction(electricityProduction);
        validationResult.setErrors(yearResult.getErrors());
        validationResult.setWarnings(yearResult.getWarnings());
        validationResult.setHydrogenLevel(hydrogenLevel);
        if (validationResult.getIsValid())
            generateCharts(electricityProduction, hydrogenProduction);
        return validationResult;
    }

    @Operation(summary = "Calculate minimal CAPEX (grid investment cost)")
    @PostMapping("/minCapex")
    public GridResult minCapex(@RequestBody GridInput gridInput) {
        State state = gridService.calculateCapex(gridInput);
        GridResult gridResult = new GridResult();
        gridResult.setGrid(gridInput.getGrid());
//        if (validationResult.getIsValid())
//            generateCharts(electricityProduction, hydrogenProduction);
        return gridResult;
    }

    private void generateCharts(List<Double> electricityProduction, List<Double> hydrogenProduction) {
        var electricityDataset = new DefaultXYDataset();
        var hydrogenDataset = new DefaultXYDataset();
        int compressedChartSize = electricityProduction.size() / 4;

        double[] electricityArray = new double[compressedChartSize];
        double[] hydrogenArray = new double[compressedChartSize];

        for (int i = 0; i < compressedChartSize; i++) {
            var elecricityPoint = electricityProduction.get(i) + electricityProduction.get(i + 1) + electricityProduction.get(i + 2) + electricityProduction.get(i + 3);
            var hydrogenPoint = hydrogenProduction.get(i) + hydrogenProduction.get(i + 1) + hydrogenProduction.get(i + 2) + hydrogenProduction.get(i + 3);
            electricityArray[i] = elecricityPoint / 4;
            hydrogenArray[i] = hydrogenPoint / 4;
        }

        double[][] electroOutArray = new double[2][];
        double[][] hydroOutArray = new double[2][];
        electroOutArray[0] = new double[compressedChartSize];
        electroOutArray[1] = electricityArray;
        hydroOutArray[0] = new double[compressedChartSize];
        hydroOutArray[1] = hydrogenArray;

        for (int i = 0; i < compressedChartSize; i++) {
            electroOutArray[0][i] = i;
        }

        electricityDataset.addSeries("key", electroOutArray);
        hydrogenDataset.addSeries("key", hydroOutArray);

        JFreeChart electricityChart = ChartFactory.createScatterPlot("electricity", "x", "y", electricityDataset, PlotOrientation.VERTICAL, false, false, false);
        JFreeChart hydrogenChart = ChartFactory.createScatterPlot("hydrogen", "x", "y", hydrogenDataset, PlotOrientation.VERTICAL, false, false, false);
        XYLineAndShapeRenderer rndr = (XYLineAndShapeRenderer) ((XYPlot) electricityChart.getPlot()).getRenderer();
        rndr.setSeriesShape(0, new Ellipse2D.Double(0, 0, 0.5, 0.5));
        rndr.setBaseLinesVisible(true);
        ((XYPlot) hydrogenChart.getPlot()).setRenderer(rndr);
        try {
            ChartUtilities.saveChartAsPNG(new File("electricityChart.png"), electricityChart, 1500, 1000);
            ChartUtilities.saveChartAsPNG(new File("hydrogenChart.png"), hydrogenChart, 1500, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
