package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ButtonTexts.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.battery_emulator.BatteryIcon.mainSimulatorPanel;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveGeneralConfigurations;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.DARK_BLUE;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BattorionButtonsHelper {
    public static JButton createButton(
            String title, String toolTip,
            String imageIconPath, String iconName,
            ActionListener actionListener
    ){
        ImageIcon icon = null;
        if(iconName != null){
            icon = CallResources.getImage(
                    imageIconPath, iconName, new Dimension(20, 20), Image.SCALE_SMOOTH);
        }
        
        JButton button = new JButton(title, icon);
        setButtonFontAndSize(
                button, new Font(Font.SERIF, Font.PLAIN + Font.BOLD, 14),
                WEST_PANEL_OPEN_WIDTH - 10, 70, SwingConstants.LEFT, SwingConstants.RIGHT);
        button.setToolTipText(toolTip);
        button.addActionListener(actionListener);
        return button;
    }
    
    public static JButton createButton(
            String toolTip, String imageIconPath, String iconName,
            ActionListener actionListener
    ){
        if(iconName == null){
            return new JButton();
        }
        ImageIcon icon = CallResources.getImage(
                imageIconPath, iconName, new Dimension(20, 20), Image.SCALE_SMOOTH);
        
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(30, 30));
        button.setToolTipText(toolTip);
        hyalineButton(button, false);
        button.addActionListener(actionListener);
        return button;
    }
    
    static void hyalineButton(JButton button, boolean isColoredAble, boolean... values) {
        if (values.length == 0) {
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
        } else {
            button.setOpaque(values[0]);
            if (values.length >= 2) {
                button.setContentAreaFilled(values[1]);
            } if (values.length >= 3) {
                button.setBorderPainted(values[2]);
            }
        }
        
        boolean isBorderPainted = values.length < 3 || values[2];
        addHandMouseListener(button, isColoredAble, isBorderPainted);
    }
    
    private static void addHandMouseListener(JButton button, boolean isColoredAble, boolean isBorderPainted){
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setBorderPainted(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                button.setBorderPainted(isBorderPainted);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isColoredAble){
                    setButtonBackgroundColor();
                    button.setBackground(DARK_BLUE);
                }
            }
        });
    }

    static JButton createGraphButton(){
        BatteryLevelGraph.initialize();
        return createButton(
                GRAPH_PAINTER_TEXT, "Display a graph of battery level",
                BUTTON_ICONS_PATH, "graph", _ -> BatteryLevelGraph.display());
    }

    public static void setupWestSideButton() {
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

    public static void setupDashboardPanel() {
        setVisibleFalse();
        DashboardPanel.setVisible(true);
        motherPanel.add(DashboardPanel, BorderLayout.CENTER);
    }

    public static void setupSettingPanel() {
        createSettingsContainer();
        SettingsContainer = mainTabbedPanel;

        ifPanelsNullCreate();
        setVisibleFalse();
        SettingsContainer.setVisible(true);
        motherPanel.add(SettingsContainer, BorderLayout.CENTER);
    }

    public static void setupStatisticsPanel() {
        createStatisticsContainer();
        StatisticsContainer = statisticsMainTabbedPanel;

        ifPanelsNullCreate();
        setVisibleFalse();
        StatisticsContainer.setVisible(true);
        motherPanel.add(StatisticsContainer, BorderLayout.CENTER);
    }

    public static void setupSimulatorPanel() {
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