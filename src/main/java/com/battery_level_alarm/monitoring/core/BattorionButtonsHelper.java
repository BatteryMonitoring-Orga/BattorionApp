package com.battery_level_alarm.monitoring.core;
import com.battery_level_alarm.monitoring.preparing_gui.BatteryStatisticsGUI;
import com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI;
import com.battery_level_alarm.monitoring.preparing_gui.PrepareDiskInfoGUI;
import com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI;

import static com.battery_level_alarm.monitoring.core.BattorionMain.*;
import static com.battery_level_alarm.monitoring.core.BattorionPanelHelper.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.setButtonFontAndSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BattorionButtonsHelper {
    public static JButton createButton(
            String title, String toolTip, ActionListener actionListener
    ){
        JButton button = new JButton(title);
        setButtonFontAndSize(button, 120, 70);
        button.setToolTipText(toolTip);
        button.addActionListener(actionListener);
        return button;
    }

    public static void reviewButtonsName() {
        if(settingsButton.getText().equals("Dashboard")) {
            settingsButton.setText("Settings");
        } else if(pcSettingsButton.getText().equals("Dashboard")) {
            pcSettingsButton.setText("PC - Settings");
        } else if(diskButton.getText().equals("Dashboard")) {
            diskButton.setText("Disk Info.");
        } else if(batteryStatisticsButton.getText().equals("Dashboard")){
            batteryStatisticsButton.setText("Statistics");
        }
    }

    public static void setUpDashboardPanel() {
        settingsButton.setText("Settings");
        pcSettingsButton.setText("PC - Settings");
        diskButton.setText("Disk Info.");
        batteryStatisticsButton.setText("Statistics");
        settingsButton.setToolTipText("Go To Settings Page");

        setVisibleFalse();
        DashboardPanel.setVisible(true);
        motherPanel.add(DashboardPanel, BorderLayout.CENTER);
        mainFrame.setSize(700, 400);
    }

    public static void setUpSettingScrollPanel() {
        reviewButtonsName();
        SettingsGUI.createAndShowGUI();
        SettingScrollPanel = SettingsGUI.getCreatedGUI();
        settingsButton.setText("Dashboard");
        settingsButton.setToolTipText("Go To Dashboard Page");

        ifPanelsNullCreate();
        setVisibleFalse();
        SettingScrollPanel.setVisible(true);
        aboutSettingsButton.setVisible(true);
        motherPanel.add(SettingScrollPanel, BorderLayout.CENTER);
        mainFrame.setSize(700, 400);
    }

    public static void setUpPCSettingsScrollPanel() {
        reviewButtonsName();
        pcSettingsButton.setText("Dashboard");
        pcSettingScrollPanel = new JScrollPane(
                ComputerSettingsGUI.createComputerSettingsGUI(alarmSounds)
        );
        pcSettingScrollPanel.setBorder(null);
        pcSettingScrollPanel.setOpaque(false);
        pcSettingScrollPanel.setFocusable(false);

        ifPanelsNullCreate();
        setVisibleFalse();
        pcSettingScrollPanel.setVisible(true);
        motherPanel.add(pcSettingScrollPanel, BorderLayout.CENTER);
        mainFrame.setSize(700, 400);
    }

    public static void setUpDiskInfoPanel() {
        reviewButtonsName();
        DiskInfoPanel = PrepareDiskInfoGUI.getDiskInfoPanel();
        diskButton.setText("Dashboard");

        ifPanelsNullCreate();
        setVisibleFalse();
        DiskInfoPanel.setVisible(true);
        motherPanel.add(DiskInfoPanel, BorderLayout.CENTER);
        mainFrame.setSize(700, 400);
    }

    public static void setUpBatteryStatisticsPanel() {
        reviewButtonsName();
        BatteryStatisticsPanel = BatteryStatisticsGUI.getBatteryStatisticsPanel();
        batteryStatisticsButton.setText("Dashboard");

        ifPanelsNullCreate();
        setVisibleFalse();
        BatteryStatisticsPanel.setVisible(true);
        motherPanel.add(BatteryStatisticsPanel, BorderLayout.CENTER);
        mainFrame.setSize(700, 400);
    }
}