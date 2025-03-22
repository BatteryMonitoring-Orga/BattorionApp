package com.battery_level_alarm.monitoring.graphics;
import static com.battery_level_alarm.monitoring.graphics.BatteryLevelGraph.series;
import static com.battery_level_alarm.monitoring.graphics.GraphsDefinitions.alterSeries;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.Scanner;

public class FileManager {
    static void saveDataAsCSV() {
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

    static void saveDataAsJSON() {
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

    static void loadDataFromCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            alterSeries.getData().clear();
            try (Scanner scanner = new Scanner(file)) {
                scanner.nextLine();
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    alterSeries.getData().add(new XYChart.Data<>(Double.parseDouble(parts[0]), Integer.parseInt(parts[1])));
                }
            } catch (IOException e) {
                printErrorMessage(e);
            }
        }
    }

    static void loadDataFromJSON(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            alterSeries.getData().clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }
                JSONArray jsonArray = new JSONArray(jsonContent.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    alterSeries.getData().add(new XYChart.Data<>(jsonObject.getDouble("time"), jsonObject.getInt("battery_level")));
                }
            } catch (IOException e) {
                printErrorMessage(e);
            }
        }
    }
}