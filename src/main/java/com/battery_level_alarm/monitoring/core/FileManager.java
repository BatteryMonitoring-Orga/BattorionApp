package com.battery_level_alarm.monitoring.core;
import java.io.*;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import com.battery_level_alarm.monitoring.basics.UserChoices;

public class FileManager {
    private static final String CONFIG_FILE_PATH = "_settings.json";
    
    public static void saveSettings() {
        JSONObject json = new JSONObject();
        json.put("SoundPath: ", UserChoices.getSoundPath());
        json.put("SoundDuration: ", UserChoices.getSoundDuration());
        json.put("VolumeLevel: ", UserChoices.getVolumeLevel());
        json.put("MinimumLevel: ", UserChoices.getMinimumLevel());
        json.put("MaximumLevel: ", UserChoices.getMaximumLevel());
        json.put("RepeatIntervalForGeneralMonitor: ", UserChoices.getRepeatIntervalForGeneralMonitor());
        json.put("RepeatIntervalBeforeRiskPhase: ", UserChoices.getRepeatIntervalBeforeRiskPhase());
        json.put("AutomaticMonitoring: ", UserChoices.isAutoMonitoring());
        json.put("EnablePrimarySound: ", UserChoices.isEnablePrimarySound());
        json.put("EnableSecondarySound: ", UserChoices.isEnableSecondarySound());
        json.put("EnableText: ", UserChoices.isEnableText());
        
        try (FileWriter file = new FileWriter(CONFIG_FILE_PATH)) {
            file.write(json.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void loadSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE_PATH))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            JSONObject json = new JSONObject(jsonContent.toString());
            UserChoices.setSoundPath(json.optString("SoundPath: ", "/resources/BattIco/flash_flood_warning.wav"));
            UserChoices.setSoundDuration(json.optInt("SoundDuration: ", 5));
            UserChoices.setVolumeLevel(json.optInt("VolumeLevel: ", 35));
            UserChoices.setMinimumLevel(json.optInt("MinimumLevel: ", 20));
            UserChoices.setMaximumLevel(json.optInt("MaximumLevel: ", 85));
            UserChoices.setRepeatIntervalForGeneralMonitor(json.optInt("RepeatIntervalForGeneralMonitor: ", 1));
            UserChoices.setRepeatIntervalBeforeRiskPhase(json.optInt("RepeatIntervalBeforeRiskPhase: ", 30));
            UserChoices.setAutoMonitoring(json.optBoolean("AutomaticMonitoring: ", true));
            UserChoices.setEnablePrimarySound(json.optBoolean("EnablePrimarySound: ", true));
            UserChoices.setEnableSecondarySound(json.optBoolean("EnableSecondarySound: ", true));
            UserChoices.setEnableText(json.optBoolean("EnableText: ", true));
        } catch (IOException | JSONException e) {
        	JOptionPane.showMessageDialog(null, 
                    "Error loading settings. Using default values: \n" + e.getMessage(), 
                    "Error through loading data", 
                    JOptionPane.ERROR_MESSAGE);
            loadDefaultSettings();
            saveSettings();
        }
    }

    private static void loadDefaultSettings() {
        UserChoices.setSoundPath("/resources/BattIco/flash_flood_warning.wav");
        UserChoices.setSoundDuration(5);
        UserChoices.setVolumeLevel(35);
        UserChoices.setMinimumLevel(20);
        UserChoices.setMaximumLevel(85);
        UserChoices.setRepeatIntervalForGeneralMonitor(1);
        UserChoices.setRepeatIntervalBeforeRiskPhase(30);
        UserChoices.setAutoMonitoring(true);
        UserChoices.setEnablePrimarySound(true);
        UserChoices.setEnableSecondarySound(true);
        UserChoices.setEnableText(true);
    }
}