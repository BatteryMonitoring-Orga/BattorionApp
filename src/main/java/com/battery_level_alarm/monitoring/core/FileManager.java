package com.battery_level_alarm.monitoring.core;
import static com.battery_level_alarm.monitoring.battery_simulation.BatteryIcon.simulatorMode;
import static com.battery_level_alarm.monitoring.core.BatteryLevelAlarm.progressBarInVerticalMode;
import com.battery_level_alarm.monitoring.basics.PC_Details;
import com.battery_level_alarm.monitoring.basics.UserChoices;

import java.io.*;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

public class FileManager {
    private static final String CONFIG_PANEL_MODE_FILE = "_panel-mode.cfg";
    private static final String CONFIG_SETTINGS_FILE_PATH = "_settings.cfg";
    private static final String PC$DETAILS_FILE_PATH = "_PC$Details.cfg";
    private static final Logger logger = Logger.getLogger(FileManager.class.getName());

    public static void saveBatteryConfigurationModes(){
        JSONObject json = createBatteryConfigurationModesJson();
        try (FileWriter file = new FileWriter(CONFIG_PANEL_MODE_FILE)) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save panel mode");
        }
    }

    private static JSONObject createBatteryConfigurationModesJson() {
        JSONObject json = new JSONObject();
        json.put("panel mode", progressBarInVerticalMode);
        json.put("battery simulator", simulatorMode);
        return json;
    }

    public static void loadBatteryConfigurationModes(){
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_PANEL_MODE_FILE))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadBatteryConfigurationModesJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, "Failed to load panel mode");
            loadDefaultBatteryConfigurationModes();
            saveBatteryConfigurationModes();
        }
    }

    private static void loadBatteryConfigurationModesJson(StringBuilder jsonContent){
        JSONObject json = new JSONObject(jsonContent.toString());
        progressBarInVerticalMode = json.optBoolean("panel mode", false);
        simulatorMode = json.optBoolean("battery simulator", false);
    }

    private static void loadDefaultBatteryConfigurationModes() {
        progressBarInVerticalMode = false;
        simulatorMode = false;
    }

    public static void saveSettings() {
        JSONObject json = createSettingsJson();
        try (FileWriter file = new FileWriter(CONFIG_SETTINGS_FILE_PATH)) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save settings");
        }
    }

    private static JSONObject createSettingsJson() {
        JSONObject json = new JSONObject();
        json.put("Sound Path", UserChoices.getSoundPath());
        json.put("Sound Duration", UserChoices.getSoundDuration());
        json.put("Minimum Level", UserChoices.getMinimumLevel());
        json.put("Maximum Level", UserChoices.getMaximumLevel());
        json.put("Repeat Interval Before Risk Phase", UserChoices.getRepeatIntervalBeforeRiskPhase());
        json.put("Automatic Monitoring", UserChoices.isAutoMonitoring());
        json.put("Enable Primary Sound", UserChoices.isEnablePrimarySound());
        json.put("Enable Secondary Sound", UserChoices.isEnableSecondarySound());
        json.put("Enable Charging/Discharging Sound", UserChoices.isEnableChargeAndDischargeSound());
        json.put("Enable Text", UserChoices.isEnableText());
        return json;
    }
    
    public static void loadSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_SETTINGS_FILE_PATH))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadSettingsFromJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, "Failed to load settings");
            loadDefaultSettings();
            saveSettings();
        }
    }

    private static void loadSettingsFromJson(StringBuilder jsonContent){
        JSONObject json = new JSONObject(jsonContent.toString());
        UserChoices.setSoundPath(json.optString("Sound Path", "/com/battery_level_alarm/monitoring/Sounds/flash_flood_warning.wav"));
        UserChoices.setSoundDuration(json.optInt("Sound Duration", 5));
        UserChoices.setMinimumLevel(json.optInt("Minimum Level", 25));
        UserChoices.setMaximumLevel(json.optInt("Maximum Level", 85));
        UserChoices.setRepeatIntervalBeforeRiskPhase(json.optInt("Repeat Interval Before Risk Phase", 30));
        UserChoices.setAutoMonitoring(json.optBoolean("Automatic Monitoring", true));
        UserChoices.setEnablePrimarySound(json.optBoolean("Enable Primary Sound", true));
        UserChoices.setEnableSecondarySound(json.optBoolean("Enable Secondary Sound", true));
        UserChoices.setEnableChargeAndDischargeSound(json.optBoolean("Enable Charging/Discharging Sound", true));
        UserChoices.setEnableText(json.optBoolean("Enable Text", true));
    }

    private static void loadDefaultSettings() {
        UserChoices.setSoundPath("/com/battery_level_alarm/monitoring/Sounds/flash_flood_warning.wav");
        UserChoices.setSoundDuration(5);
        UserChoices.setMinimumLevel(25);
        UserChoices.setMaximumLevel(85);
        UserChoices.setRepeatIntervalBeforeRiskPhase(30);
        UserChoices.setAutoMonitoring(true);
        UserChoices.setEnablePrimarySound(true);
        UserChoices.setEnableSecondarySound(true);
        UserChoices.setEnableChargeAndDischargeSound(true);
        UserChoices.setEnableText(true);
    }

    public static void savePC$Details(){
        JSONObject json = createPC$DetailsJson();
        try (FileWriter file = new FileWriter(PC$DETAILS_FILE_PATH)) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save pc details");
        }
    }

    private static JSONObject createPC$DetailsJson() {
        JSONObject json = new JSONObject();
        json.put("Activate the awakening feature", PC_Details.getActivateTheAwakeningFeature());
        json.put("Wake up the PC every (in Minutes)", PC_Details.getWakeUpEvery());
        json.put("Switch audio output to Speakers", PC_Details.isEnableExchangeToSpeakerAudioOutput());
        json.put("Volume Level", PC_Details.getVolumeLevel());
        json.put("Notification Sound File Name", PC_Details.getNotificationSoundFileName());
        return json;
    }

    public static void loadPC$Details(){
        try (BufferedReader reader = new BufferedReader(new FileReader(PC$DETAILS_FILE_PATH))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadPC$DetailsFromJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, "Failed to load pc details");
            loadDefaultPC$Details();
            savePC$Details();
        }
    }

    private static void loadPC$DetailsFromJson(StringBuilder jsonContent){
        JSONObject json = new JSONObject(jsonContent.toString());
        PC_Details.setActivateTheAwakeningFeature(json.optBoolean("Activate the awakening feature", false));
        PC_Details.setWakeUpEvery(json.optInt("Wake up the PC every (in Minutes)", 2));
        PC_Details.setEnableExchangeToSpeakerAudioOutput(json.optBoolean("Switch audio output to Speakers", true));
        PC_Details.setVolumeLevel(json.optInt("Volume Level", 35));
        PC_Details.setNotificationSoundFileName(json.optString("Notification Sound File Name", "Alarm01.wav"));
    }

    public static void loadDefaultPC$Details(){
        PC_Details.setActivateTheAwakeningFeature(false);
        PC_Details.setWakeUpEvery(2);
        PC_Details.setEnableExchangeToSpeakerAudioOutput(true);
        PC_Details.setVolumeLevel(35);
        PC_Details.setNotificationSoundFileName("Alarm01.wav");
    }

    private static void printErrorMessage(Throwable e, String loggerText){
        logger.severe(loggerText + ": " + e.getMessage());
        JOptionPane.showMessageDialog(
                null,
                "Error: " + e.getClass().getName() + "\nMessage: " + e.getMessage(),
                "Battery Level Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}