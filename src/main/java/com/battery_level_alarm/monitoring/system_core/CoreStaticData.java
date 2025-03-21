package com.battery_level_alarm.monitoring.system_core;
import javax.swing.*;
import java.awt.*;

public class CoreStaticData {
    public static final Font textFont = new Font(Font.SERIF, Font.PLAIN, 14);
    public static final String APP_VERSION = Battorion.class.getPackage().getImplementationVersion();
    public static final String IMAGES_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Images/";
    public static final String BUTTON_ICONS_PATH = "/com/battery_level_alarm/monitoring/mainButtonIcons/";
    public static final String isIn_ChargingMode = "Charging";
    public static final String isIn_DisChargingMode = "Not Charging";
    public static final String MAIN_FOLDER_NAME = "/Battorion";
    public static final Color DARK_BLUE = new Color(0, 90, 145);

    static final String APP_NAME = "Battorion";
    static final String ChargingSoundPath = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-start-2574.wav";
    static final String DischargingSoundPath = "/com/battery_level_alarm/monitoring/Sounds/mixkit-software-interface-back-2575.wav";
    static final String isA_DashboardPanel = "DashboardPanel";
    static final String isA_SettingsContainer = "SettingsContainerPanel";
    static final String isA_StatisticsContainer = "StatisticsContainerPanel";
    static final String isA_SimulatorPanel = "SimulatorPanel";

    static final String WEST_SIDE_BUTTON_TEXT = " West Side";
    static final String DASHBOARD_BUTTON_TEXT = " Dashboard";
    static final String START_BUTTON_TEXT = " Start";
    static final String STOP_BUTTON_TEXT = " Stop";
    static final String SETTINGS_BUTTON_TEXT = " Settings";
    static final String STATISTICS_BUTTON_TEXT = " Statistics";
    static final String SIMULATOR_BUTTON_TEXT = " Simulator";
    static final String REPORT_BUTTON_TEXT = " Report";
    static final String ABOUT_BUTTON_TEXT = " About";
    static final String GRAPH_PAINTER_TEXT = " Graph";

    public static final int frameWidth = 850;
    public static final int frameHeight = 560;
    public static final int westSidePanelWidthOpenMode = 180;
    public static final int westSidePanelWidthCloseMode = 50;

    static void setUIManagerPanelColor(Color panelBackgroundColor){
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