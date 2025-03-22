package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PanelIdentifiers.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.DARK_BLUE;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.BatteryStatisticsGUI;
import static com.battery_level_alarm.monitoring.system_core.BattorionButtonsHelper.setButtonBackgroundColor;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.mainTabbedPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.pcSettingPanel;
import static com.battery_level_alarm.monitoring.visual_effects.PanelStyler.applyGradientBackground;

import javax.swing.*;
import java.awt.*;

public class BattorionPanelHelper {
    static void createHBoxPanel(){
        HBoxPanel = new JPanel();
        HBoxPanel = applyGradientBackground(
                HBoxPanel, isDarkMode
        );
        HBoxPanel.setLayout(new BoxLayout(HBoxPanel, BoxLayout.X_AXIS));
        HBoxPanel.add(mainButtonsContainer);
        HBoxPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        HBoxPanel.add(motherPanel);
    }

    public static void refreshSettingsPanel() {
        setVisibleFalse();
        settingsButton.doClick();
    }

    public static void refreshBatteryStatisticsPanel() {
        BatteryStatisticsGUI.createGUI();
    }

    public static void refreshTopAssistantPartialPanelsShadow(Color color){
        dropShadowBorder.setShadowColor(color);
        firstTopAssistantPartialPanel.setBorder(dropShadowBorder);
        secondTopAssistantPartialPanel.setBorder(dropShadowBorder);
        thirdTopAssistantPartialPanel.setBorder(dropShadowBorder);

        topAssistantPartialPanelsContainer.repaint();
        topAssistantPartialPanelsContainer.revalidate();
    }

    public static void ifPanelsNullCreate() {
        if(SettingsContainer == null) {
            SettingsContainer = new JTabbedPane();
            SettingsContainer.setVisible(false);
        } if(StatisticsContainer == null) {
            StatisticsContainer = new JTabbedPane();
            StatisticsContainer.setVisible(false);
        } if(SimulatorMainPanel == null){
            SimulatorMainPanel = new JPanel();
            SimulatorMainPanel.setVisible(false);
        }
    }

    public static void setVisibleFalse() {
        DashboardPanel.setVisible(false);
        SettingsContainer.setVisible(false);
        StatisticsContainer.setVisible(false);
        SimulatorMainPanel.setVisible(false);
    }

    public static void setVisibleTrue(String isA) {
        if (DashboardPanel != null && isA.equals(isA_DashboardPanel)) {
            DashboardPanel.setVisible(true);
        } else if (SettingsContainer != null && isA.equals(isA_SettingsContainer)) {
            settingsButton.doClick();
        } else if (StatisticsContainer != null && isA.equals(isA_StatisticsContainer)) {
            statisticsButton.doClick();
        } else if (SimulatorMainPanel != null && isA.equals(isA_SimulatorPanel)){
            simulatorButton.doClick();
        } else {
            assert DashboardPanel != null;
            DashboardPanel.setVisible(true);
        }
    }

    static void audioLabelMouseAction(){
        setVisibleFalse();
        setButtonBackgroundColor();
        settingsButton.setBackground(DARK_BLUE);
        setVisibleTrue(isA_SettingsContainer);
        mainTabbedPanel.setSelectedIndex(1);
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = pcSettingPanel.getVerticalScrollBar();
            verticalBar.setValue(250);
        });
    }

    public static String whatIsVisible() {
        if (DashboardPanel.isVisible()) {
            return isA_DashboardPanel;
        } else if (SettingsContainer.isVisible()) {
            return isA_SettingsContainer;
        } else if (StatisticsContainer.isVisible()) {
            return isA_StatisticsContainer;
        } else if (SimulatorMainPanel.isVisible()) {
            return isA_SimulatorPanel;
        } else {
            return "No panel is visible";
        }
    }
}