package com.battery_level_alarm.monitoring.main_folder_manager;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.basics.DropDownListStatus.*;
import static com.battery_level_alarm.monitoring.core.BattorionMain.progressBarInVerticalMode;
import static com.battery_level_alarm.monitoring.core.BattorionMain.simulatorMode;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.effects.Appearance;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigurationFilesManager {
    private static final String CONFIG_PANEL_MODE_FILE = "./_general.cfg";
    private static final String CONFIG_DROP_DOWN_LIST_FILE = "./_dropdown-list.cfg";
    private static final String CONFIG_SETTINGS_FILE_PATH = "./_settings.cfg";
    private static final String PC_SETTINGS_FILE_PATH = "./_pc-settings.cfg";
    private static final Logger logger = Logger.getLogger(ConfigurationFilesManager.class.getName());

    private static File getMainFolderPath(String configFileName){
        File folderDir = new File(MAIN_FOLDER_PATH);
        if (!folderDir.exists()) {
            folderDir.mkdir();
        }

        return new File(folderDir, configFileName);
    }

    public static void saveGeneralConfigurations(){
        JSONObject json = createGeneralConfigurationsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(CONFIG_PANEL_MODE_FILE).getAbsolutePath()
        )){
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
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(CONFIG_PANEL_MODE_FILE).getAbsolutePath())
        )){
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

    public static void saveDropDownListConfigurations(){
        JSONObject json = createDropDownListConfigurationsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(CONFIG_DROP_DOWN_LIST_FILE).getAbsolutePath()
        )){
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save drop down list states");
        }
    }

    private static JSONObject createDropDownListConfigurationsJson() {
        JSONObject json = new JSONObject();
        json.put("first drop down list 'CS'", isCS_FirstDropDownListEnabled());
        json.put("second drop down list 'CS'", isCS_SecondDropDownListEnabled());
        json.put("third drop down list 'CS'", isCS_ThirdDropDownListEnabled());
        return json;
    }

    public static void loadDropDownListConfigurations(){
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(CONFIG_DROP_DOWN_LIST_FILE).getAbsolutePath())
        )){
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadDropDownListConfigurationsJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, "Failed to load drop down list mode");
            loadDefaultDropDownListConfigurations();
            saveDropDownListConfigurations();
        }
    }

    private static void loadDropDownListConfigurationsJson(StringBuilder jsonContent){
        JSONObject json = new JSONObject(jsonContent.toString());
        setCS_FirstDropDownListEnabled(json.optBoolean("first drop down list 'CS'", true));
        setCS_SecondDropDownListEnabled(json.optBoolean("second drop down list 'CS'", false));
        setCS_ThirdDropDownListEnabled(json.optBoolean("third drop down list 'CS'", false));
    }

    private static void loadDefaultDropDownListConfigurations() {
        setCS_FirstDropDownListEnabled(true);
        setCS_SecondDropDownListEnabled(false);
        setCS_ThirdDropDownListEnabled(false);
    }

    public static void saveSettings() {
        JSONObject json = createSettingsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(CONFIG_SETTINGS_FILE_PATH).getAbsolutePath()
        )){
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
        json.put("Run secondary alarm before", UserChoices.getAlertBeforeRiskPhaseBy());
        json.put("Repeat Interval Before Risk Phase", UserChoices.getRepeatIntervalBeforeRiskPhase());
        json.put("Automatic Monitoring", UserChoices.isAutoMonitoring());
        json.put("Enable Primary Sound", UserChoices.isEnablePrimarySound());
        json.put("Enable Secondary Sound", UserChoices.isEnableSecondarySound());
        json.put("Enable Charging/Discharging Sound", UserChoices.isEnableChargeAndDischargeSound());
        json.put("Enable Text", UserChoices.isEnableText());
        return json;
    }
    
    public static void loadSettings() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(CONFIG_SETTINGS_FILE_PATH).getAbsolutePath())
        )){
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
        UserChoices.setAlertBeforeRiskPhaseBy(json.optInt("Run secondary alarm before", 5));
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
        UserChoices.setAlertBeforeRiskPhaseBy(5);
        UserChoices.setRepeatIntervalBeforeRiskPhase(30);
        UserChoices.setAutoMonitoring(true);
        UserChoices.setEnablePrimarySound(true);
        UserChoices.setEnableSecondarySound(true);
        UserChoices.setEnableChargeAndDischargeSound(true);
        UserChoices.setEnableText(true);
    }

    public static void saveComputerSettings(){
        JSONObject json = createComputerSettingsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(PC_SETTINGS_FILE_PATH).getAbsolutePath()
        )){
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save pc details");
        }
    }

    private static JSONObject createComputerSettingsJson() {
        JSONObject json = new JSONObject();
        json.put("Activate the awakening feature", isActivateTheAwakeningFeature());
        json.put("Enable System Notification Sound", isEnableSystemNotificationSound());
        json.put("Wake up the PC every (in Minutes)", getWakeUpEvery());
        json.put("Switch audio output to Speakers", isEnableExchangeToSpeakerAudioOutput());
        json.put("Switch audio output to the Used device", isEnableExchangeToAudioOutputUsed());
        json.put("Enabling sound level change", isEnablingSoundLevelChange());
        json.put("Restoring sound level after alert", isRestoringSoundLevelAfterAlert());
        json.put("Enable unmute volume automatically", isEnableUnmuteVolumeAutomatically());
        json.put("Current audio device", getCurrentAudioDevice());
        json.put("Audio devices", getAudioDevices());
        json.put("Volume Level", getVolumeLevel());
        json.put("Notification Sound File Name", getNotificationSoundFileName());
        return json;
    }

    public static void loadComputerSettings(){
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(PC_SETTINGS_FILE_PATH).getAbsolutePath())
        )){
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
        setActivateTheAwakeningFeature(json.optBoolean("Activate the awakening feature", false));
        setEnableSystemNotificationSound(json.optBoolean("Enable System Notification Sound", true));
        setEnableUnmuteVolumeAutomatically(json.optBoolean("Enable unmute volume automatically", true));
        setEnableExchangeToSpeakerAudioOutput(json.optBoolean("Switch audio output to Speakers", true));
        setEnableExchangeToAudioOutputUsed(json.optBoolean("Switch audio output to the Used device", true));
        setEnablingSoundLevelChange(json.optBoolean("Enabling sound level change", true));
        setRestoringSoundLevelAfterAlert(json.optBoolean("Restoring sound level after alert", true));
        setWakeUpEvery(json.optInt("Wake up the PC every (in Minutes)", 2));
        setVolumeLevel(json.optInt("Volume Level", 35));
        setCurrentAudioDevice(json.optString("Current audio device", "سماعات"));
        loadAudioDevicesList(json);
        setNotificationSoundFileName(json.optString("Notification Sound File Name", "Alarm01.wav"));
    }

    private static void loadAudioDevicesList(JSONObject json){
        JSONArray audioDevicesArray = json.optJSONArray("Audio devices");
        List<String> audioDevicesList = new ArrayList<>();
        if (audioDevicesArray != null) {
            for (int i = 0; i < audioDevicesArray.length(); i++) {
                audioDevicesList.add(audioDevicesArray.optString(i, ""));
            }
        }
        setAudioDevices(audioDevicesList);
    }

    public static void loadDefaultComputerSettings(){
        setActivateTheAwakeningFeature(false);
        setEnableSystemNotificationSound(true);
        setEnableUnmuteVolumeAutomatically(true);
        setEnableExchangeToSpeakerAudioOutput(true);
        setEnableExchangeToAudioOutputUsed(true);
        setEnablingSoundLevelChange(true);
        setRestoringSoundLevelAfterAlert(true);
        setWakeUpEvery(2);
        setVolumeLevel(35);
        setCurrentAudioDevice("سماعات");
        setAudioDevices(new ArrayList<>());
        setNotificationSoundFileName("Alarm01.wav");
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