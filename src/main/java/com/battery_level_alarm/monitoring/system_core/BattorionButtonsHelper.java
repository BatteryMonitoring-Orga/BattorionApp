package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ButtonTexts.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.battery_emulator.BatteryIcon.mainSimulatorPanel;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveGeneralConfigurations;
import static com.battery_level_alarm.monitoring.system_core.BattorionPanelHelper.*;
import static com.battery_level_alarm.monitoring.battery_emulator.BatteryIcon.BatterySimulationStart;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.setButtonFontAndSize;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.createSettingsContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.mainTabbedPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.StatisticsContainerClass.createStatisticsContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.StatisticsContainerClass.statisticsMainTabbedPanel;
import com.battery_level_alarm.monitoring.graphics.BatteryLevelGraph;
import com.battery_level_alarm.monitoring.visual_effects.CallResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BattorionButtonsHelper {
    public static JButton createButton(
            String title, String toolTip,
            String imageIconPath, String iconName,
            ActionListener actionListener
    ){
        ImageIcon icon = CallResources.getImage(
                imageIconPath, iconName, new Dimension(20, 20), Image.SCALE_SMOOTH);
        JButton button = new JButton(title, icon);
        setButtonFontAndSize(
                button, new Font(Font.SERIF, Font.PLAIN + Font.BOLD, 14),
                WEST_PANEL_OPEN_WIDTH - 10, 70, SwingConstants.LEFT, SwingConstants.RIGHT);
        button.setToolTipText(toolTip);
        button.addActionListener(actionListener);
        return button;
    }

    static JButton createGraphButton(){
        BatteryLevelGraph.initialize();

        return createButton(
                GRAPH_PAINTER_TEXT, "Display a graph of battery level",
                BUTTON_ICONS_PATH, "graph", _ -> BatteryLevelGraph.display());
    }

    public static void setUpWestSideButton() {
        int width = WEST_PANEL_OPEN_WIDTH;
        if(westSideButton.getText().equals(WEST_SIDE_BUTTON_TEXT)){
            setButtonNamesEmpty();
            isWestSidePartAppear = false;
            width = WEST_PANEL_CLOSED_WIDTH;
        } else {
            returnButtonNames();
            isWestSidePartAppear = true;
        }
        refreshWestSide(width);
    }

    public static void refreshWestSide(int width){
        mainButtonsContainer.setPreferredSize(new Dimension(width, FRAME_HEIGHT));
        mainButtonsContainer.setMaximumSize(new Dimension(width, FRAME_HEIGHT));
        refreshMotherFrame();
        saveGeneralConfigurations();
    }

    private static void setButtonNamesEmpty(){
        westSideButton.setText("");
        dashboardButton.setText("");
        actionButton.setText("");
        settingsButton.setText("");
        statisticsButton.setText("");
        simulatorButton.setText("");
        graphPainter.setText("");
        reportButton.setText("");
        aboutButton.setText("");
    }

    private static void returnButtonNames(){
        westSideButton.setText(WEST_SIDE_BUTTON_TEXT);
        dashboardButton.setText(DASHBOARD_BUTTON_TEXT);
        settingsButton.setText(SETTINGS_BUTTON_TEXT);
        statisticsButton.setText(STATISTICS_BUTTON_TEXT);
        simulatorButton.setText(SIMULATOR_BUTTON_TEXT);
        graphPainter.setText(GRAPH_PAINTER_TEXT);
        reportButton.setText(REPORT_BUTTON_TEXT);
        aboutButton.setText(ABOUT_BUTTON_TEXT);

        if(isMonitorRunning){
            actionButton.setText(STOP_BUTTON_TEXT);
        } else {
            actionButton.setText(START_BUTTON_TEXT);
        }
    }

    public static void setUpDashboardPanel() {
        setVisibleFalse();
        DashboardPanel.setVisible(true);
        motherPanel.add(DashboardPanel, BorderLayout.CENTER);
    }

    public static void setUpSettingPanel() {
        createSettingsContainer();
        SettingsContainer = mainTabbedPanel;

        ifPanelsNullCreate();
        setVisibleFalse();
        SettingsContainer.setVisible(true);
        motherPanel.add(SettingsContainer, BorderLayout.CENTER);
    }

    public static void setUpStatisticsPanel() {
        createStatisticsContainer();
        StatisticsContainer = statisticsMainTabbedPanel;

        ifPanelsNullCreate();
        setVisibleFalse();
        StatisticsContainer.setVisible(true);
        motherPanel.add(StatisticsContainer, BorderLayout.CENTER);
    }

    public static void setUpSimulatorPanel() {
        BatterySimulationStart();
        SimulatorMainPanel = new JPanel(new BorderLayout());
        SimulatorMainPanel.add(new JScrollPane(mainSimulatorPanel), BorderLayout.CENTER);

        ifPanelsNullCreate();
        setVisibleFalse();
        SimulatorMainPanel.setVisible(true);
        motherPanel.add(SimulatorMainPanel, BorderLayout.CENTER);
    }

    public static void setButtonBackgroundColor(){
        westSideButton.setBackground(panelBackgroundColor);
        dashboardButton.setBackground(panelBackgroundColor);
        statisticsButton.setBackground(panelBackgroundColor);
        reportButton.setBackground(panelBackgroundColor);
        graphPainter.setBackground(panelBackgroundColor);
        simulatorButton.setBackground(panelBackgroundColor);
        actionButton.setBackground(panelBackgroundColor);
        aboutButton.setBackground(panelBackgroundColor);
        settingsButton.setBackground(panelBackgroundColor);
    }
}