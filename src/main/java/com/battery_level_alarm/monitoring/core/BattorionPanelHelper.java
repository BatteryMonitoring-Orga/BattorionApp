package com.battery_level_alarm.monitoring.core;
import com.battery_level_alarm.monitoring.command.DiskSpaceInfo;
import com.battery_level_alarm.monitoring.preparing_gui.BatteryStatisticsGUI;
import com.battery_level_alarm.monitoring.preparing_gui.PrepareDiskInfoGUI;

import static com.battery_level_alarm.monitoring.core.BattorionMain.*;
import javax.swing.*;
import java.awt.*;

public class BattorionPanelHelper {
    public static void refreshSettingsPanel() {
        settingsButton.setText("Settings");
        setVisibleFalse();
        settingsButton.doClick();
    }

    public static void refreshComputerSettingsPanel() {
        pcSettingsButton.setText("PC - Settings");
        setVisibleFalse();
        pcSettingsButton.doClick();
    }

    public static void refreshDiskInfoPanel(String isA, boolean fromDiskPanel) {
        DiskSpaceInfo.DiskSpace();
        PrepareDiskInfoGUI.createGUI();

        diskButton.setText("Disk Info.");
        setVisibleFalse();
        if(fromDiskPanel) {
            diskButton.doClick();
            return;
        }
        setVisibleTrue(isA);
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
        if(SettingScrollPanel == null) {
            SettingScrollPanel = new JScrollPane();
            SettingScrollPanel.setVisible(false);
        } if(pcSettingScrollPanel == null) {
            pcSettingScrollPanel = new JScrollPane();
            pcSettingScrollPanel.setVisible(false);
        } if(DiskInfoPanel == null) {
            DiskInfoPanel = new JPanel();
            DiskInfoPanel.setVisible(false);
        } if(BatteryStatisticsPanel == null) {
            BatteryStatisticsPanel = new JPanel();
            BatteryStatisticsPanel.setVisible(false);
        }
    }

    public static void setVisibleFalse() {
        DashboardPanel.setVisible(false);
        SettingScrollPanel.setVisible(false);
        pcSettingScrollPanel.setVisible(false);
        DiskInfoPanel.setVisible(false);
        BatteryStatisticsPanel.setVisible(false);
    }

    public static void setVisibleTrue(String isA) {
        if (DashboardPanel != null && isA.equals(isA_DashboardPanel)) {
            DashboardPanel.setVisible(true);
        } else if (SettingScrollPanel != null && isA.equals(isA_SettingScrollPanel)) {
            settingsButton.setText("Settings");
            settingsButton.doClick();
        } else if (pcSettingScrollPanel != null && isA.equals(isA_PC_SettingScrollPanel)) {
            pcSettingsButton.setText("PC - Settings");
            pcSettingsButton.doClick();
        } else if (DiskInfoPanel != null && isA.equals(isA_DiskInfoPanel)) {
            diskButton.setText("Disk Info.");
            diskButton.doClick();
        } else if (BatteryStatisticsPanel != null && isA.equals(isA_BatteryStatisticsPanel)) {
            batteryStatisticsButton.setText("Statistics");
            batteryStatisticsButton.doClick();
        } else {
            assert DashboardPanel != null;
            DashboardPanel.setVisible(true);
        }
    }

    public static String whatIsVisible() {
        if (DashboardPanel.isVisible()) {
            return isA_DashboardPanel;
        } else if (SettingScrollPanel.isVisible()) {
            return isA_SettingScrollPanel;
        } else if (pcSettingScrollPanel.isVisible()) {
            return isA_PC_SettingScrollPanel;
        } else if (DiskInfoPanel.isVisible()) {
            return isA_DiskInfoPanel;
        } else if (BatteryStatisticsPanel.isVisible()) {
            return isA_BatteryStatisticsPanel;
        } else {
            return "No panel is visible";
        }
    }
}