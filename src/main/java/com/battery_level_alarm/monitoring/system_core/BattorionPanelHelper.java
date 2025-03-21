package com.battery_level_alarm.monitoring.system_core;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.BatteryStatisticsGUI;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.CoreStaticData.*;
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
        settingsButton.setText("Settings");
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
            settingsButton.setText("Settings");
            settingsButton.doClick();
        } else if (StatisticsContainer != null && isA.equals(isA_StatisticsContainer)) {
            statisticsButton.setText("Statistics");
            statisticsButton.doClick();
        } else if (SimulatorMainPanel != null && isA.equals(isA_SimulatorPanel)){
            simulatorButton.setText("Simulator");
            simulatorButton.doClick();
        } else {
            assert DashboardPanel != null;
            DashboardPanel.setVisible(true);
        }
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