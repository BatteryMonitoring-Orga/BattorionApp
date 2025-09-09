package com.battery_level_alarm.monitoring.registration_manager;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.*;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.setSaveAfterNumOfRecords;
import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.*;
import static com.battery_level_alarm.monitoring.graphics.base.ChartType.LINE;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.DEFAULT_DARK_GRADIENT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.DEFAULT_LIGHT_GRADIENT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.setBrightnessControlOption;
import static com.battery_level_alarm.monitoring.core_utilities.DropDownListStatus.*;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.CONFIGURATIONS_MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.top_assist.TopAssistPanel.isSilentMode;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.UIThemesGUI.customizationGradientBackground;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.*;

import com.battery_level_alarm.monitoring.command_executors.SoundDevicesNamesFinder;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;
import com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigurationFilesManager {
    private static final String CONFIG_SUB_FOLDER = "/z-config";
    private static final String CONFIG_PANEL_MODE_FILE = "./_general.cfg";
    private static final String CONFIG_DROP_DOWN_LIST_FILE = "./_dropdown-list.cfg";
    private static final String CONFIG_SETTINGS_FILE_PATH = "./_settings.cfg";
    private static final String PC_SETTINGS_FILE_PATH = "./_pc-settings.cfg";
    private static final String UPDATE_SETTINGS_FILE_PATH = "./_update-settings.cfg";
    private static final String GRAPH_SETTINGS_FILE_PATH = "./_painter-settings.cfg";
    public static final String UNKNOWN_OUTPUT_DEVICE = "Unknown (S) E404";
    
    private static File getMainFolderPath(String configFileName) {
        File folderDir = new File(CONFIGURATIONS_MAIN_FOLDER_PATH + CONFIG_SUB_FOLDER);
        if (!folderDir.exists()) {
            boolean isCreated = folderDir.mkdirs();
            if (!isCreated) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to create the main directory: " + folderDir.getAbsolutePath(),
                        "Directory Creation Error",
                        JOptionPane.ERROR_MESSAGE
                );
                Runtime.getRuntime().halt(0);
            }
        }
        return new File(folderDir, configFileName);
    }

    public static void saveGeneralConfigurations() {
        JSONObject json = createGeneralConfigurationsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(CONFIG_PANEL_MODE_FILE).getAbsolutePath()
        )) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save panel mode");
        }
    }

    private static JSONObject createGeneralConfigurationsJson() {
        JSONObject json = new JSONObject();
        json.put("progress bar first mode", progressBarInVerticalMode);
        json.put("progress bar second mode", progressBarInFirstMode);
        json.put("west side mode", isWestSidePartAppear);
        json.put("silent mode is active", isSilentMode);
        json.put("audio device cmdlets installed", isAudioDeviceCmdletsInstalled);
        json.put("theme mode", Appearance.getThemeName());
        json.put("layout mode id", getLayoutModeID());
        json.put("gradient background dark mode", PanelStyler.getGradientBackgroundDarkModeName());
        json.put("gradient background light mode", PanelStyler.getGradientBackgroundLightModeName());
        json.put("customization gradient background", customizationGradientBackground);
        
        Color startColor = getStartCustomColor();
        String colorStr = startColor.getRed() + "," + startColor.getGreen() + "," + startColor.getBlue();
        json.put("custom start color", colorStr);
        
        Color endColor = getEndCustomColor();
        String colorEnd = endColor.getRed() + "," + endColor.getGreen() + "," + endColor.getBlue();
        json.put("custom end color", colorEnd);
        return json;
    }

    public static void loadGeneralConfigurations() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(CONFIG_PANEL_MODE_FILE).getAbsolutePath())
        )) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadGeneralConfigurationsJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, e.getMessage());
            loadDefaultGeneralConfigurations();
            setStartCustomColor(new Color(5, 56, 89));
            setEndCustomColor(new Color(0, 67, 8));
            saveGeneralConfigurations();
        }
    }

    private static void loadGeneralConfigurationsJson(StringBuilder jsonContent) {
        JSONObject json = new JSONObject(jsonContent.toString());
        progressBarInVerticalMode = json.optBoolean("progress bar first mode", false);
        progressBarInFirstMode = json.optBoolean("progress bar second mode", true);
        isWestSidePartAppear = json.optBoolean("west side mode", true);
        isSilentMode = json.optBoolean("silent mode is active", false);
        isAudioDeviceCmdletsInstalled = json.optBoolean("audio device cmdlets installed", false);
        customizationGradientBackground = json.optBoolean("customization gradient background", false);
        Appearance.setThemeName(json.optString("theme mode", "Light"));
        setLayoutModeID(json.optInt("layout mode id", 0));
        PanelStyler.setGradientBackgroundDarkModeName(json.optString("gradient background dark mode", DEFAULT_DARK_GRADIENT));
        PanelStyler.setGradientBackgroundLightModeName(json.optString("gradient background light mode", DEFAULT_LIGHT_GRADIENT));
        loadCustomGradientBackgroundColor(json);
    }

    private static void loadCustomGradientBackgroundColor(JSONObject json) {
        try {
            String color = json.getString("custom start color");
            String[] rgb = color.split(",");
            if (rgb.length == 3) {
                Color startColor = new Color(
                        Integer.parseInt(rgb[0].trim()),
                        Integer.parseInt(rgb[1].trim()),
                        Integer.parseInt(rgb[2].trim())
                );
                setStartCustomColor(startColor);
            } else {
                setStartCustomColor(new Color(5, 56, 89));
                saveGeneralConfigurations();
            }
        } catch (Exception e) {
            printErrorMessage(e, e.getMessage());
            setStartCustomColor(new Color(5, 56, 89));
            saveGeneralConfigurations();
        }

        try {
            String color = json.getString("custom end color");
            String[] rgb = color.split(",");
            if (rgb.length == 3) {
                Color endColor = new Color(
                        Integer.parseInt(rgb[0].trim()),
                        Integer.parseInt(rgb[1].trim()),
                        Integer.parseInt(rgb[2].trim())
                );
                setEndCustomColor(endColor);
            } else {
                setEndCustomColor(new Color(0, 67, 8));
                saveGeneralConfigurations();
            }
        } catch (Exception e) {
            printErrorMessage(e, e.getMessage());
            setEndCustomColor(new Color(0, 67, 8));
            saveGeneralConfigurations();
        }
    }

    private static void loadDefaultGeneralConfigurations() {
        progressBarInVerticalMode = false;
        isWestSidePartAppear = true;
        isSilentMode = false;
        isAudioDeviceCmdletsInstalled = false;
        customizationGradientBackground = false;
        progressBarInFirstMode = true;
        Appearance.setThemeName("Light");
        setLayoutModeID(0);
        PanelStyler.setGradientBackgroundDarkModeName(DEFAULT_DARK_GRADIENT);
        PanelStyler.setGradientBackgroundLightModeName(DEFAULT_LIGHT_GRADIENT);
    }

    public static void saveDropDownListConfigurations() {
        JSONObject json = createDropDownListConfigurationsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(CONFIG_DROP_DOWN_LIST_FILE).getAbsolutePath()
        )) {
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
        json.put("fourth drop down list 'CS'", isCS_FourthDropDownListEnabled());
        json.put("first drop down list 'AS'", isAppSettingsFirstDropDownListEnabled());
        json.put("second drop down list 'AS'", isAppSettingsSecondDropDownListEnabled());
        json.put("third drop down list 'AS'", isAppSettingsThirdDropDownListEnabled());
        json.put("fourth drop down list 'AS'", isAppSettingsFourthDropDownListEnabled());
        return json;
    }

    public static void loadDropDownListConfigurations() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(CONFIG_DROP_DOWN_LIST_FILE).getAbsolutePath())
        )) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadDropDownListConfigurationsJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, e.getMessage());
            loadDefaultDropDownListConfigurations();
            saveDropDownListConfigurations();
        }
    }

    private static void loadDropDownListConfigurationsJson(StringBuilder jsonContent) {
        JSONObject json = new JSONObject(jsonContent.toString());
        setCS_FirstDropDownListEnabled(json.optBoolean("first drop down list 'CS'", true));
        setCS_SecondDropDownListEnabled(json.optBoolean("second drop down list 'CS'", false));
        setCS_ThirdDropDownListEnabled(json.optBoolean("third drop down list 'CS'", false));
        setCS_FourthDropDownListEnabled(json.optBoolean("fourth drop down list 'CS'", false));
        setAppSettingsFirstDropDownListEnabled(json.optBoolean("first drop down list 'AS'", true));
        setAppSettingsSecondDropDownListEnabled(json.optBoolean("second drop down list 'AS'", false));
        setAppSettingsThirdDropDownListEnabled(json.optBoolean("third drop down list 'AS'", false));
        setAppSettingsFourthDropDownListEnabled(json.optBoolean("fourth drop down list 'AS'", false));
    }

    private static void loadDefaultDropDownListConfigurations() {
        setCS_FirstDropDownListEnabled(true);
        setCS_SecondDropDownListEnabled(false);
        setCS_ThirdDropDownListEnabled(false);
        setCS_FourthDropDownListEnabled(false);
        setAppSettingsFirstDropDownListEnabled(true);
        setAppSettingsSecondDropDownListEnabled(false);
        setAppSettingsThirdDropDownListEnabled(false);
        setAppSettingsFourthDropDownListEnabled(false);
    }

    public static void saveSettings() {
        JSONObject json = createSettingsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(CONFIG_SETTINGS_FILE_PATH).getAbsolutePath()
        )) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save settings");
        }
    }

    private static JSONObject createSettingsJson() {
        JSONObject json = new JSONObject();
        json.put("Primary Sound Path", UserChoices.getPrimarySoundPath());
        json.put("Primary Sound Duration", UserChoices.getSoundDuration());
        json.put("Secondary Sound Path", UserChoices.getSecondarySoundPath());
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
        )) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadSettingsFromJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, e.getMessage());
            loadDefaultSettings();
            saveSettings();
        }
    }

    private static void loadSettingsFromJson(StringBuilder jsonContent) {
        JSONObject json = new JSONObject(jsonContent.toString());
        UserChoices.setPrimarySoundPath(json.optString("Primary Sound Path", "/com/battery_level_alarm/monitoring/alert-sounds/flash_flood_warning.wav"));
        UserChoices.setSoundDuration(json.optInt("Primary Sound Duration", 5));
        UserChoices.setSecondarySoundPath(json.optString("Secondary Sound Path", "java.awt.Toolkit.getDefaultToolkit().beep()"));
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
        UserChoices.setPrimarySoundPath("/com/battery_level_alarm/monitoring/alert-sounds/flash_flood_warning.wav");
        UserChoices.setSecondarySoundPath("java.awt.Toolkit.getDefaultToolkit().beep()");
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

    public static void saveComputerSettings() {
        JSONObject json = createComputerSettingsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(PC_SETTINGS_FILE_PATH).getAbsolutePath()
        )) {
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
        json.put("Automatic set and reset brightness level", isAutomaticallyReduceAndRestoreBL());
        json.put("Automatic set brightness level", isAutomaticallyReduceBrightnessLevel());
        json.put("Automatically restore brightness level", isAutomaticallyRestoreBrightnessLevel());
        json.put("Default Speaker Output Device Name", getDefaultSpeakerOutputDeviceName());
        json.put("Current audio device", getCurrentAudioDevice());
        json.put("Audio devices", getAudioDevicesList());
        json.put("Volume Level", getVolumeLevel());
        json.put("Brightness level", getBrightnessLevel());
        json.put("Brightness Control Option", getBrightnessControlOption());
        json.put("Notification Sound File Name", getNotificationSoundFileName());
        return json;
    }

    public static void loadComputerSettings() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(PC_SETTINGS_FILE_PATH).getAbsolutePath())
        )) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadComputerSettingsFromJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, e.getMessage());
            loadDefaultComputerSettings();
            saveComputerSettings();
        }
    }

    private static void loadComputerSettingsFromJson(StringBuilder jsonContent) {
        JSONObject json = new JSONObject(jsonContent.toString());
        setActivateTheAwakeningFeature(json.optBoolean("Activate the awakening feature", false));
        setEnableSystemNotificationSound(json.optBoolean("Enable System Notification Sound", true));
        setEnableUnmuteVolumeAutomatically(json.optBoolean("Enable unmute volume automatically", true));
        setEnableExchangeToSpeakerAudioOutput(json.optBoolean("Switch audio output to Speakers", true));
        setEnableExchangeToAudioOutputUsed(json.optBoolean("Switch audio output to the Used device", true));
        setEnablingSoundLevelChange(json.optBoolean("Enabling sound level change", true));
        setRestoringSoundLevelAfterAlert(json.optBoolean("Restoring sound level after alert", true));
        setAutomaticallyReduceAndRestoreBL(json.optBoolean("Automatic set and reset brightness level", false));
        setAutomaticallyReduceBrightnessLevel(json.optBoolean("Automatic set brightness level", true));
        setAutomaticallyRestoreBrightnessLevel(json.optBoolean("Automatically restore brightness level", true));
        setWakeUpEvery(json.optInt("Wake up the PC every (in Minutes)", 2));
        setBrightnessControlOption(json.optInt("Brightness Control Option", 2));
        setVolumeLevel(json.optInt("Volume Level", 35));
        setBrightnessLevel(json.optInt("Brightness level", 60));
        setDefaultSpeakerOutputDeviceName(json.optString("Default Speaker Output Device Name", UNKNOWN_OUTPUT_DEVICE));
        setCurrentAudioDevice(json.optString("Current audio device", getDefaultSpeakerOutputDeviceName()));
        loadAudioDevicesList(json);
        setNotificationSoundFileName(json.optString("Notification Sound File Name", "Alarm01.wav"));
    }

    private static void loadAudioDevicesList(JSONObject json) {
        JSONArray audioDevicesArray = json.optJSONArray("Audio devices");
        List<String> audioDevicesList = new ArrayList<>();
        if (audioDevicesArray != null) {
            for (int i = 0; i < audioDevicesArray.length(); i++) {
                audioDevicesList.add(audioDevicesArray.optString(i, ""));
            }
        }
        setAudioDevicesList(audioDevicesList);
    }

    private static void loadDefaultComputerSettings() {
        setActivateTheAwakeningFeature(false);
        setEnableSystemNotificationSound(true);
        setEnableUnmuteVolumeAutomatically(true);
        setEnableExchangeToSpeakerAudioOutput(true);
        setEnableExchangeToAudioOutputUsed(true);
        setEnablingSoundLevelChange(true);
        setRestoringSoundLevelAfterAlert(true);
        setAutomaticallyReduceAndRestoreBL(false);
        setAutomaticallyReduceBrightnessLevel(true);
        setAutomaticallyRestoreBrightnessLevel(true);
        setWakeUpEvery(2);
        setVolumeLevel(35);
        setBrightnessLevel(60);
        setBrightnessControlOption(2);
        setAudioDevicesList(new ArrayList<>());
        setNotificationSoundFileName("Alarm01.wav");
        initializeDefaultSpeakerDevice();
    }
    
    private static void initializeDefaultSpeakerDevice() {
        try {
            String defaultSpeakerOutputDeviceName = SoundDevicesNamesFinder.findFirstDefaultValidRenderDevice();
            setDefaultSpeakerOutputDeviceName(defaultSpeakerOutputDeviceName);
            setCurrentAudioDevice(defaultSpeakerOutputDeviceName);
        } catch (Exception e) {
            printErrorMessage(e, e.getMessage());
            setDefaultSpeakerOutputDeviceName(UNKNOWN_OUTPUT_DEVICE);
            setCurrentAudioDevice(UNKNOWN_OUTPUT_DEVICE);
        }
    }
    
    public static void saveUpdateVersionConfigurations() {
        JSONObject json = createUpdateVersionConfigurationsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(UPDATE_SETTINGS_FILE_PATH).getAbsolutePath()
        )) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save update settings");
        }
    }
    
    private static JSONObject createUpdateVersionConfigurationsJson() {
        JSONObject json = new JSONObject();
        json.put("check for updates automatically", isCheckForUpdatesAutomatically());
        json.put("download updates automatically", isDownloadUpdatesAutomatically());
        json.put("notify before installing", isNotifyBeforeInstalling());
        json.put("previous version", getPreviousVersion());
        return json;
    }
    
    public static void loadUpdateVersionConfigurations() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(UPDATE_SETTINGS_FILE_PATH).getAbsolutePath())
        )) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadUpdateVersionConfigurationsJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, e.getMessage());
            loadDefaultUpdateVersionConfigurations();
            saveUpdateVersionConfigurations();
        }
    }
    
    private static void loadUpdateVersionConfigurationsJson(StringBuilder jsonContent) {
        JSONObject json = new JSONObject(jsonContent.toString());
        setCheckForUpdatesAutomatically(json.optBoolean("check for updates automatically", true));
        setDownloadUpdatesAutomatically(json.optBoolean("download updates automatically", false));
        setNotifyBeforeInstalling(json.optBoolean("notify before installing", false));
        setPreviousVersion(json.optString("previous version", "0.0.0"));
    }
    
    private static void loadDefaultUpdateVersionConfigurations() {
        setCheckForUpdatesAutomatically(true);
        setDownloadUpdatesAutomatically(false);
        setNotifyBeforeInstalling(false);
        setPreviousVersion("0.0.0");
    }
    
    public static void saveGraphConfigurations() {
        JSONObject json = createGraphConfigurationsJson();
        try (FileWriter file = new FileWriter(
                getMainFolderPath(GRAPH_SETTINGS_FILE_PATH).getAbsolutePath()
        )) {
            file.write(json.toString(4));
        } catch (IOException e) {
            printErrorMessage(e, "Failed to save graph settings");
        }
    }
    
    private static JSONObject createGraphConfigurationsJson() {
        JSONObject json = new JSONObject();
        json.put("graph theme", getPainterTheme());
        json.put("sketch color", getSketchColor().toString());
        json.put("background color", getBackgroundColor().toString());
        json.put("axis color", getAxisColor().toString());
        json.put("chart type", getChartType());
        json.put("show points on graph", isShowDataPoints());
        json.put("show values on hover", isShowValuesOnHover());
        json.put("show grid lines", isShowGridLines());
        json.put("show x axis labels", isShowXAxisLabels());
        json.put("show y axis labels", isShowYAxisLabels());
        json.put("enable auto update", isAutoUpdate());
        json.put("enable zoom", isZoomEnabled());
        json.put("minimum zoom level", getZoomMin());
        json.put("maximum zoom level", getZoomMax());
        json.put("alert threshold percent", getAlertThreshold());
        json.put("alert color", getAlertColor().toString());
        json.put("default save format", getSaveFormat());
        json.put("enable auto save", isAutoSave());
        json.put("auto-save every (records)", getSaveAfterNumOfRecords());
        return json;
    }
    
    public static void loadGraphConfigurations() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(getMainFolderPath(GRAPH_SETTINGS_FILE_PATH).getAbsolutePath())
        )) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            loadGraphConfigurationsJson(jsonContent);
        } catch (IOException | JSONException e) {
            printErrorMessage(e, e.getMessage());
            loadDefaultGraphConfigurations();
            saveGraphConfigurations();
        }
    }
    
    private static void loadGraphConfigurationsJson(StringBuilder jsonContent) {
        JSONObject json = new JSONObject(jsonContent.toString());
        setPainterTheme(json.optString("graph theme", GRAPH_THEME.FlatIntellijLightLaf.getDisplayName()));
        setSketchColor(javafx.scene.paint.Color.web(json.optString("sketch color", "#f3622d")));
        setBackgroundColor(javafx.scene.paint.Color.web(json.optString("background color", "WHITE")));
        setAxisColor(javafx.scene.paint.Color.web(json.optString("axis color", "BLACK")));
        setChartType(json.optString("chart type", LINE.name()));
        setShowDataPoints(json.optBoolean("show points on graph", true));
        setShowValuesOnHover(json.optBoolean("show values on hover", true));
        setShowGridLines(json.optBoolean("show grid lines", false));
        setShowXAxisLabels(json.optBoolean("show x axis labels", true));
        setShowYAxisLabels(json.optBoolean("show y axis labels", true));
        setAutoUpdate(json.optBoolean("enable auto update", true));
        setZoomEnabled(json.optBoolean("enable zoom", true));
        setZoomMin(json.optDouble("minimum zoom level", 0.5));
        setZoomMax(json.optDouble("maximum zoom level", 2.0));
        setAlertThreshold(json.optInt("alert threshold percent", 20));
        setAlertColor(javafx.scene.paint.Color.web(json.optString("alert color", "RED")));
        setSaveFormat(json.optString("default save format", "CSV"));
        setAutoSave(json.optBoolean("enable auto save", true));
        setSaveAfterNumOfRecords(json.optInt("auto-save every (records)", 250));
    }
    
    public static void loadDefaultGraphConfigurations() {
        setPainterTheme(GRAPH_THEME.FlatIntellijLightLaf.getDisplayName());
        setSketchColor(javafx.scene.paint.Color.web("#f3622d"));
        setBackgroundColor(javafx.scene.paint.Color.WHITE);
        setAxisColor(javafx.scene.paint.Color.BLACK);
        setChartType(LINE.name());
        setShowDataPoints(true);
        setShowValuesOnHover(true);
        setShowGridLines(false);
        setShowXAxisLabels(true);
        setShowYAxisLabels(true);
        setAutoUpdate(true);
        setZoomEnabled(true);
        setZoomMin(0.5);
        setZoomMax(2.0);
        setAlertThreshold(20);
        setAlertColor(javafx.scene.paint.Color.RED);
        setSaveFormat("CSV");
        setAutoSave(true);
        setSaveAfterNumOfRecords(250);
    }
    
    private static void printErrorMessage(Throwable e, String loggerText) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        logger.severe("[EXCEPTION]: " + loggerText + ": " + e.getMessage());
    }
}