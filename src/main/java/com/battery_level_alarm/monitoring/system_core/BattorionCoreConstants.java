package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.command_executors.HardwareInfoReader.*;
import static com.battery_level_alarm.monitoring.core_utilities.VersionReader.version;
import static com.battery_level_alarm.monitoring.feedback_system.UserDataFetcher.sendUserIdAndGetFilteredData;
import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.Keys.*;
import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.LICENSE.FREE_TRIAL;
import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.STATUS.ACTIVE;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.CONFIGURATIONS_MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.MAIN_FOLDER_NAME;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_DATA_UPLOADED;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.VERSION_FILE_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import java.io.File;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BattorionCoreConstants {
    public static class AppInfo {
        public static final String VERSION_FILE_NAME = "config.enc";
        public static final String APP_VERSION = version(getVersionFileParentPath() + "/" + VERSION_FILE_NAME);
        public static final String APP_NAME = "Battorion";
        public static final String APP_FRAME_TITLE = APP_NAME + " — Comprehensive Battery & System Management";
        public static final String TRAY_NOTIFICATION_NAME = APP_NAME + " — Running in Background";
        public static final String NULL_VALUE = "null";
        
        public static final URI BATTORION_WEBSITE = getBattorionWebsiteURI();
        private static URI getBattorionWebsiteURI() {
            try {
                return new URI("https://battorion-website.vercel.app/");
            } catch (URISyntaxException e) {
                logger.severe("[EXCEPTION]: " + e.getMessage());
                return null;
            }
        }
        
        public static String getVersionFileParentPath() {
            String path = Paths.BATTORION_MAIN_FOLDER_PATH + "/" + VERSION_FILE_NAME;
            if (new File(path).exists()) {
                return Paths.BATTORION_MAIN_FOLDER_PATH;
            } else {
                path = prefs.get(VERSION_FILE_PATH, ".") + "/" + VERSION_FILE_NAME;
                if (new File(path).exists()) {
                    return RoamingConfigClass.ROAMING_CONFIG_PATH;
                } else {
                    path = RoamingConfigClass.ROAMING_CONFIG_PATH + "/" + VERSION_FILE_NAME;
                    if (new File(path).exists()) {
                        return RoamingConfigClass.ROAMING_CONFIG_PATH;
                    } else {
                        return "./";
                    }
                }
            }
        }
    }

    public static class UI {
        public static final Font TEXT_FONT = new Font(Font.SERIF, Font.PLAIN, 14);
        public static final Color DARK_BLUE = new Color(0, 90, 145);
        public static final Color HYPERLINK_HOVER_COLOR = new Color(0, 134, 179);
        
        static void setUIManagerPanelColor(Color panelBackgroundColor) {
            UIManager.put("Panel.background", panelBackgroundColor);
            UIManager.put("ScrollPane.background", panelBackgroundColor);
            UIManager.put("ScrollBar.track", panelBackgroundColor.darker());
            UIManager.put("TabbedPane.background", panelBackgroundColor);
            UIManager.put("TabbedPane.selected", panelBackgroundColor.darker());
            UIManager.put("TabbedPane.unselectedBackground", panelBackgroundColor);
            UIManager.put("TextField.background", panelBackgroundColor);
            UIManager.put("TextArea.background", panelBackgroundColor);
            UIManager.put("ComboBox.background", panelBackgroundColor);
            UIManager.put("Table.background", panelBackgroundColor);
            UIManager.put("Table.gridColor", panelBackgroundColor.darker());
            UIManager.put("TableHeader.background", panelBackgroundColor.darker());
            UIManager.put("Spinner.background", panelBackgroundColor);
            UIManager.put("Label.background", panelBackgroundColor);
        }
    }
    
    public static class Paths {
        public static final String MAIN_FOLDER_NAME = "/Battorion";
        public static final String IMAGES_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Images/";
        public static final String ICONS_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Icons/";
        public static final String CSS_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Styles/";
        public static final String ASSETS_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Assets/";
        public static final String BUTTON_ICONS_PATH = "/com/battery_level_alarm/monitoring/mainButtonIcons/";
        public static final String REFRESH_ICON_PATH = "/com/battery_level_alarm/monitoring/Tray/Icons/";
        public static final String SUPPORT_VIDEOS_PATH = "/com/battery_level_alarm/monitoring/support-videos/";
        public static final String CHARGING_SOUND_PATH = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-start-2574.wav";
        public static final String DISCHARGING_SOUND_PATH = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-back-2575.wav";
        public static final String BATTERY_REPORT_PATH = CONFIGURATIONS_MAIN_FOLDER_PATH + "\\battery-report.html";
        
        public static final String BATTORION_MAIN_FOLDER_PATH = getBattorionMainFolderPath();
        private static String getBattorionMainFolderPath() {
            try {
                String path = Paths.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                return new File(path).getParent();
            } catch (Exception e) {
                logger.severe("[EXCEPTION]: " + e.getMessage());
                return null;
            }
        }
        
        public static String getCurrentExePath() {
            try {
                File codeSourceFile = new File(AppInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                File current = codeSourceFile.isDirectory() ? codeSourceFile : codeSourceFile.getParentFile();
                File root = null;
                while (current != null) {
                    if (current.getName().equalsIgnoreCase("Battorion")) {
                        root = current;
                        break;
                    }
                    current = current.getParentFile();
                }
                
                if (root != null) {
                    return new File(root, AppInfo.APP_NAME + ".exe").getAbsolutePath();
                } else {
                    return new File(System.getProperty("user.dir"), AppInfo.APP_NAME + ".exe").getAbsolutePath();
                }
            } catch (Exception e) {
                printErrorMessage(e);
                return new File(System.getProperty("user.dir"), AppInfo.APP_NAME + ".exe").getAbsolutePath();
            }
        }
    }
    
    public static class RoamingConfigClass {
        public static final String ROAMING_CONFIG_PATH = getRoamingConfigPath();
        private static String getRoamingConfigPath() {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                return System.getenv("APPDATA") + MAIN_FOLDER_NAME + "\\";
            } else if (os.contains("mac")) {
                return System.getProperty("user.home") + "/Library/Application Support" + MAIN_FOLDER_NAME + "/";
            } else {
                String xdg = System.getenv("XDG_CONFIG_HOME");
                if (xdg != null)
                    return xdg + MAIN_FOLDER_NAME + "/";
                else
                    return System.getProperty("user.home") + "/.config" + MAIN_FOLDER_NAME + "/";
            }
        }
        
        public static void moveFileToRoamingFolder(String sourceDir, String fileName) {
            try {
                Path sourcePath = java.nio.file.Paths.get(sourceDir, fileName);
                Path targetPath = java.nio.file.Paths.get(ROAMING_CONFIG_PATH, fileName);
                Files.createDirectories(java.nio.file.Paths.get(ROAMING_CONFIG_PATH));
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                logger.severe("[EXCEPTION]: " + e.getMessage());
            }
        }
    }

    public static class ChargingStatus {
        public static final String isIn_ChargingMode = "Charging";
        public static final String isIn_DisChargingMode = "Not Charging";
    }

    public static class PanelIdentifiers {
        public static final String IS_A_DASHBOARD_PANEL = "DashboardPanel";
        public static final String IS_A_SETTINGS_CONTAINER = "SettingsContainerPanel";
        public static final String IS_A_STATISTICS_CONTAINER = "StatisticsContainerPanel";
        public static final String IS_A_LIFE_REPORT_PANEL = "LifeReportPanel";
        public static final String IS_A_SIMULATOR_PANEL = "SimulatorPanel";
    }
    
    public static class ButtonTexts {
        public static final String WEST_SIDE_BUTTON_TEXT = " West Side";
        public static final String DASHBOARD_BUTTON_TEXT = " Dashboard";
        public static final String SETTINGS_BUTTON_TEXT = " Settings";
        public static final String FEEDBACK_BUTTON_TEXT = " Feedback";
        public static final String STATISTICS_BUTTON_TEXT = " Statistics";
        public static final String SIMULATOR_BUTTON_TEXT = " Simulator";
        public static final String GUIDE_BUTTON_TEXT = " Guide";
        public static final String REPORT_BUTTON_TEXT = " Life Report";
        public static final String ABOUT_BUTTON_TEXT = " About";
        public static final String GRAPH_PAINTER_TEXT = " Graph";
    }
    
    public static class PrefKeysIdentifiers {
        public static final String USER_IDENTIFIER = "BattorionUserIdentifier";
        public static final String NEW_BATTORION_USER = "NewBattorionUser";
        public static final String USER_GAVE_FEEDBACK = "UserGaveFeedback";
        public static final String START_BATTORION_WITH = "StartBattorionWith";
        public static final String NEW_RELEASE = "new-release";
        public static final String APP_THEME = "appTheme";
        public static final String START_ON_BOOT = "startOnBootControlled";
        public static final String TIME_RUNNING_IN_BACKGROUND = "IsFirstTimeRunningInBackground";
        public static final String SHOW_BATTERY_ICON = "showBatteryIcon";
        public static final String WAKE_UP_PC_AUTO = "wakeUpPCAuto";
        public static final String TAB_HEADER_POSITION = "tab_header_position";
        public static final String TRAY_NOTIFICATION_ENABLE = "trayNotificationEnable";
        public static final String TOAST_NOTIFICATION_ENABLE = "toastNotificationEnable";
        public static final String UPDATE_FREQUENCY = "UpdateFrequency";
        public static final String BATTORION_STARTED_AT = "DownloadedAtDate";
        public static final String USER_DATA_UPLOADED = "IsUserDataUploaded";
        public static final String LAST_STATUS_VALIDATE = "status";
        public static final String NEW_TRAY_TAB = "HasSeenNewTab";
        public static final String VERSION_FILE_PATH = "HasSeenNewTab";
    }
    
    public static class Dimensions {
        public static final int FRAME_WIDTH = 850;
        public static final int FRAME_HEIGHT = 580;
        public static final int WEST_PANEL_OPEN_WIDTH = 180;
        public static final int WEST_PANEL_CLOSED_WIDTH = 50;
    }

    public static class StateVariables {
        public static boolean progressBarInVerticalMode;
        public static boolean progressBarInFirstMode;
        public static boolean isWestSidePartAppear;
        public static boolean simulatorMode;
        public static boolean isDarkMode = false;
        public static boolean isAudioDeviceCmdletsInstalled;
    }

    public static class BatteryLevelHandlerConstants {
        public static final int ALERT_AFTER_SECONDS = 60;
        public static final String CRITICAL_BATTERY_STATUS = "Critical Battery Status";
        public static final String SPACE = "\u2003\u2003";
    }
    
    public static class BordersConfiguration {
        public static final int THICKNESS = 3;
        public static final int RADIUS = 15;
        public static final LayoutManager LAYOUT_MANAGER = new FlowLayout(FlowLayout.CENTER);
    }
    
    public static class UserIdentifier {
	    public static String getOrCreateUserId() {
            String existing = prefs.get(PrefKeysIdentifiers.USER_IDENTIFIER, null);
            if (existing != null) return existing;
            
            String newId = generateSecureId();
            prefs.put(PrefKeysIdentifiers.USER_IDENTIFIER, newId);
            return newId;
        }
        
        private static String generateSecureId() {
            return UUID.randomUUID().toString();
        }
    }
    
    public static class UserOnboarding {
        private static boolean isTodayCreated = false;
        
        public static void userSessionTracker() {
            String existing = prefs.get(PrefKeysIdentifiers.USER_IDENTIFIER, null);
            if (existing != null) return;
            
            isTodayCreated = true;
            prefs.put(PrefKeysIdentifiers.USER_IDENTIFIER, UserIdentifier.generateSecureId());
            prefs.put(PrefKeysIdentifiers.BATTORION_STARTED_AT, LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            prefs.putBoolean(PrefKeysIdentifiers.NEW_BATTORION_USER, true);
            prefs.putBoolean(PrefKeysIdentifiers.USER_GAVE_FEEDBACK, false);
        }
        
        public static Map<String, Object> basicUserDataUpload() {
            boolean isTrue = prefs.getBoolean(USER_DATA_UPLOADED, false);
            if (!isTodayCreated && isTrue) return null;
            isTodayCreated = false;
            String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            Map<String, Object> data = new HashMap<>();
            data.put(ID, prefs.get(PrefKeysIdentifiers.USER_IDENTIFIER, null));
            data.put(STATUS, ACTIVE.name());
            data.put(CREATED_AT, createdAt);
            data.put(VERSION, AppInfo.APP_VERSION);
            data.put(OS, getOSFullName());
            data.put(EMAIL, "");
            
            Map<String, Object> license = new HashMap<>();
            license.put(LICENSE_TYPE, FREE_TRIAL.name());
            license.put(LICENSE_EXPIRES_AT, LocalDate.now().plusDays(5).toString());
            license.put(LICENSE_ACTIVATED_AT, createdAt);
            license.put(LICENSE_MAX_DEVICES, 1);
            data.put(LICENSE, license);
            
            Map<String, Object> hardware = new HashMap<>();
            hardware.put(HARDWARE_HWID, getHWID());
            hardware.put(HARDWARE_CPU, getCPU());
            hardware.put(HARDWARE_RAM, getRAM());
            hardware.put(HARDWARE_DISK, getDisk());
            data.put(HARDWARE, hardware);
            data.put(ANALYTICS, createAnalyticsMap(true));
            return data;
        }
        
        public static Map<String, Object> createAnalyticsMap(boolean isAFirstTime) {
            int times = 1;
            if(!isAFirstTime) {
                try {
                    Map<String, Object> dtatMap = sendUserIdAndGetFilteredData(
                            prefs.get(PrefKeysIdentifiers.USER_IDENTIFIER, null), List.of(ANALYTICS_RUN_COUNT));
                    
                    if(!dtatMap.isEmpty() && isInternetAvailable()) {
                        times = (int) dtatMap.get(ANALYTICS_RUN_COUNT);
                        times++;
                    }
                } catch (Exception e) {
                    logger.severe("[EXCEPTION]: " + e.getMessage());
                }
            }
            
            Map<String, Object> analytics = new HashMap<>();
            analytics.put(ANALYTICS_LAST_ONLINE, LocalDate.now().toString());
            analytics.put(ANALYTICS_RUN_COUNT, times);
            analytics.put(ANALYTICS_VERSION_USED, AppInfo.APP_VERSION);
            return analytics;
        }
    }
}