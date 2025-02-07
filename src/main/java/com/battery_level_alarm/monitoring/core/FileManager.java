package com.battery_level_alarm.monitoring.core;
import static com.battery_level_alarm.monitoring.core.BattorionMain.progressBarInVerticalMode;
import static com.battery_level_alarm.monitoring.core.BattorionMain.simulatorMode;
import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.UserChoices;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import com.battery_level_alarm.monitoring.effects.Appearance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileManager {
    private static final String CONFIG_PANEL_MODE_FILE = "_general.cfg";
    private static final String CONFIG_SETTINGS_FILE_PATH = "_settings.cfg";
    private static final String PC_SETTINGS_FILE_PATH = "_pc-settings.cfg";
    private static final Logger logger = Logger.getLogger(FileManager.class.getName());

    public static void saveGeneralConfigurations(){
        JSONObject json = createGeneralConfigurationsJson();
        try (FileWriter file = new FileWriter(CONFIG_PANEL_MODE_FILE)) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save panel mode");
        }
    }

    private static JSONObject createGeneralConfigurationsJson() {
        JSONObject json = new JSONObject();
        json.put("panel mode", progressBarInVerticalMode);
        json.put("battery simulator", simulatorMode);
        json.put("theme mode", Appearance.getThemeName());
        return json;
    }

    public static void loadGeneralConfigurations(){
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_PANEL_MODE_FILE))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadGeneralConfigurationsJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, "Failed to load panel mode");
            loadDefaultGeneralConfigurations();
            saveGeneralConfigurations();
        }
    }

    private static void loadGeneralConfigurationsJson(StringBuilder jsonContent){
        JSONObject json = new JSONObject(jsonContent.toString());
        progressBarInVerticalMode = json.optBoolean("panel mode", false);
        simulatorMode = json.optBoolean("battery simulator", false);
        Appearance.setThemeName(json.optString("theme mode", "Light"));
    }

    private static void loadDefaultGeneralConfigurations() {
        progressBarInVerticalMode = false;
        simulatorMode = false;
        Appearance.setThemeName("Light");
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

    public static void saveComputerSettings(){
        JSONObject json = createComputerSettingsJson();
        try (FileWriter file = new FileWriter(PC_SETTINGS_FILE_PATH)) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save pc details");
        }
    }

    private static JSONObject createComputerSettingsJson() {
        JSONObject json = new JSONObject();
        json.put("Activate the awakening feature", ComputerSettings.isActivateTheAwakeningFeature());
        json.put("Wake up the PC every (in Minutes)", ComputerSettings.getWakeUpEvery());
        json.put("Switch audio output to Speakers", ComputerSettings.isEnableExchangeToSpeakerAudioOutput());
        json.put("Switch audio output to the Used device", ComputerSettings.isEnableExchangeToAudioOutputUsed());
        json.put("Current audio device", ComputerSettings.getCurrentAudioDevice());
        json.put("Audio devices", ComputerSettings.getAudioDevices());
        json.put("Volume Level", ComputerSettings.getVolumeLevel());
        json.put("Notification Sound File Name", ComputerSettings.getNotificationSoundFileName());
        return json;
    }

    public static void loadComputerSettings(){
        try (BufferedReader reader = new BufferedReader(new FileReader(PC_SETTINGS_FILE_PATH))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadComputerSettingsFromJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, "Failed to load pc details");
            loadDefaultComputerSettings();
            saveComputerSettings();
        }
    }

    private static void loadComputerSettingsFromJson(StringBuilder jsonContent){
        JSONObject json = new JSONObject(jsonContent.toString());
        ComputerSettings.setActivateTheAwakeningFeature(json.optBoolean("Activate the awakening feature", false));
        ComputerSettings.setWakeUpEvery(json.optInt("Wake up the PC every (in Minutes)", 2));
        ComputerSettings.setEnableExchangeToSpeakerAudioOutput(json.optBoolean("Switch audio output to Speakers", true));
        ComputerSettings.setEnableExchangeToAudioOutputUsed(json.optBoolean("Switch audio output to the Used device", true));
        ComputerSettings.setCurrentAudioDevice(json.optString("Current audio device", "سماعات"));
        loadAudioDevicesList(json);
        ComputerSettings.setVolumeLevel(json.optInt("Volume Level", 35));
        ComputerSettings.setNotificationSoundFileName(json.optString("Notification Sound File Name", "Alarm01.wav"));
    }

    private static void loadAudioDevicesList(JSONObject json){
        JSONArray audioDevicesArray = json.optJSONArray("Audio devices");
        List<String> audioDevicesList = new ArrayList<>();
        if (audioDevicesArray != null) {
            for (int i = 0; i < audioDevicesArray.length(); i++) {
                audioDevicesList.add(audioDevicesArray.optString(i, ""));
            }
        }
        ComputerSettings.setAudioDevices(audioDevicesList);
    }

    public static void loadDefaultComputerSettings(){
        ComputerSettings.setActivateTheAwakeningFeature(false);
        ComputerSettings.setWakeUpEvery(2);
        ComputerSettings.setEnableExchangeToSpeakerAudioOutput(true);
        ComputerSettings.setEnableExchangeToAudioOutputUsed(true);
        ComputerSettings.setCurrentAudioDevice("سماعات");
        ComputerSettings.setAudioDevices(new ArrayList<>());
        ComputerSettings.setVolumeLevel(35);
        ComputerSettings.setNotificationSoundFileName("Alarm01.wav");
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