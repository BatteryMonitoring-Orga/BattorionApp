package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.core_utilities.VersionReader.version;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import java.io.File;
import javax.swing.*;
import java.awt.*;

public class BattorionCoreConstants {
    public static class AppInfo {
        public static final String APP_VERSION = version("config.enc");
        public static final String APP_NAME = "Battorion";
        public static final String TRAY_NOTIFICATION_NAME = "Battorion - In Background Process";
        
        public static String getCurrentExeDirectory() {
            try {
                File codeSourceFile = new File(AppInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                String path = codeSourceFile.getParent();
                if (path == null || path.isEmpty()) {
                    path = System.getProperty("user.dir");
                }
                return path;
            } catch (Exception e) {
                printErrorMessage(e);
                return System.getProperty("user.dir");
            }
        }
    }

    public static class UI {
        public static final Font TEXT_FONT = new Font(Font.SERIF, Font.PLAIN, 14);
        public static final Color DARK_BLUE = new Color(0, 90, 145);
        
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
        public static final String CSS_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Styles/";
        public static final String ASSETS_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Assets/";
        public static final String BUTTON_ICONS_PATH = "/com/battery_level_alarm/monitoring/mainButtonIcons/";
        public static final String REFRESH_ICON_PATH = "/com/battery_level_alarm/monitoring/Tray/Icons/";
        public static final String CHARGING_SOUND_PATH = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-start-2574.wav";
        public static final String DISCHARGING_SOUND_PATH = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-back-2575.wav";
    }

    public static class ChargingStatus {
        public static final String isIn_ChargingMode = "Charging";
        public static final String isIn_DisChargingMode = "Not Charging";
    }

    public static class PanelIdentifiers {
        public static final String isA_DashboardPanel = "DashboardPanel";
        public static final String isA_SettingsContainer = "SettingsContainerPanel";
        public static final String isA_StatisticsContainer = "StatisticsContainerPanel";
        public static final String isA_SimulatorPanel = "SimulatorPanel";
    }

    public static class ButtonTexts {
        public static final String WEST_SIDE_BUTTON_TEXT = " West Side";
        public static final String DASHBOARD_BUTTON_TEXT = " Dashboard";
        public static final String START_BUTTON_TEXT = " Start";
        public static final String STOP_BUTTON_TEXT = " Stop";
        public static final String SETTINGS_BUTTON_TEXT = " Settings";
        public static final String STATISTICS_BUTTON_TEXT = " Statistics";
        public static final String SIMULATOR_BUTTON_TEXT = " Simulator";
        public static final String GUIDE_BUTTON_TEXT = " Guide";
        public static final String REPORT_BUTTON_TEXT = " Life Report";
        public static final String ABOUT_BUTTON_TEXT = " About";
        public static final String GRAPH_PAINTER_TEXT = " Graph";
    }

    public static class Dimensions {
        public static final int FRAME_WIDTH = 850;
        public static final int FRAME_HEIGHT = 560;
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
}