package com.battery_level_alarm.monitoring.graphics.records;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.series;
import static com.battery_level_alarm.monitoring.graphics.base.GraphsDefinitions.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.FRAME_HEIGHT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.FRAME_WIDTH;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.Scanner;

public class GraphicRecordsManager {
    private static int loadCounter = 0;
    public static void saveDataAsCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Time, Battery Level");
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    writer.println(data.getXValue() + "," + data.getYValue());
                }
            } catch (IOException e) {
                printErrorMessage(e);
            }
        }
    }
    
    public static void saveDataAsJSON() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                JSONArray jsonArray = new JSONArray();
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("time", data.getXValue());
                    jsonObject.put("battery_level", data.getYValue());
                    jsonArray.put(jsonObject);
                }
                writer.write(jsonArray.toString(4));
            } catch (IOException e) {
                printErrorMessage(e);
            }
        }
    }
    
    public static void saveDataAsCSV(String path) {
        try {
            File file = prepareUniqueFile(path);
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Time, Battery Level");
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    writer.println(data.getXValue() + "," + data.getYValue());
                }
            }
        } catch (IOException e) {
            printErrorMessage(e);
        }
    }
    
    public static void saveDataAsJSON(String path) {
        try {
            File file = prepareUniqueFile(path);
            try (FileWriter writer = new FileWriter(file)) {
                JSONArray jsonArray = new JSONArray();
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("time", data.getXValue());
                    jsonObject.put("battery_level", data.getYValue());
                    jsonArray.put(jsonObject);
                }
                writer.write(jsonArray.toString(4));
            }
        } catch (IOException e) {
            printErrorMessage(e);
        }
    }
    
    private static File prepareUniqueFile(String path) throws IOException {
        File file = new File(path);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("تعذر إنشاء المجلد: " + parentDir.getAbsolutePath());
            }
        }
        
        if (file.exists()) {
            String name = file.getName();
            String baseName;
            String extension = "";
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                baseName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            } else {
                baseName = name;
            }
            
            int counter = 1;
            while (file.exists()) {
                String newName = baseName + "_" + counter + extension;
                file = new File(parentDir, newName);
                counter++;
            }
        }
        
        return file;
    }
    
    public static void loadDataFromCSV(Stage stage) {
        loadCounter = 0;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            resetChartSpecifications();
            try (Scanner scanner = new Scanner(file)) {
                scanner.nextLine();
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    double range = Double.parseDouble(parts[0]);
                    alterSeries.getData().add(new XYChart.Data<>(range, Integer.parseInt(parts[1])));

                    if(loadCounter == INCREASE_WIDTH_EACH_TIMES){
                        NumberAxis xAxis = (NumberAxis) alterLineChart.getXAxis();
                        xAxis.setUpperBound(range + 5);
                        alterLineChart.setMinWidth(alterLineChart.getMinWidth() + WIDTH_INCREASE_VALUE);
                        loadCounter = 0;
                    } else {
                        loadCounter++;
                    }
                }
            } catch (IOException e) {
                printErrorMessage(e);
            }
        }
    }
    
    public static void loadDataFromJSON(Stage stage) {
        loadCounter = 0;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            resetChartSpecifications();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }
                JSONArray jsonArray = new JSONArray(jsonContent.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    double range = jsonObject.getDouble("time");
                    alterSeries.getData().add(new XYChart.Data<>(range, jsonObject.getInt("battery_level")));

                    if(loadCounter == INCREASE_WIDTH_EACH_TIMES){
                        NumberAxis xAxis = (NumberAxis) alterLineChart.getXAxis();
                        xAxis.setUpperBound(range + 5);
                        alterLineChart.setMinWidth(alterLineChart.getMinWidth() + WIDTH_INCREASE_VALUE);
                        loadCounter = 0;
                    } else {
                        loadCounter++;
                    }
                }
            } catch (IOException e) {
                printErrorMessage(e);
            }
        }
    }

    private static void resetChartSpecifications(){
        alterSeries.getData().clear();
        alterLineChart.setMinWidth(FRAME_WIDTH);
        alterLineChart.setMinHeight(FRAME_HEIGHT);
        NumberAxis xAxis = (NumberAxis) alterLineChart.getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(15);
        NumberAxis yAxis = (NumberAxis) alterLineChart.getYAxis();
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
    }
}