package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.TEXT_FONT;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SettingsContainerClass {
    public static final String ICONS_FOLDER_PATH = "/com/battery_level_alarm/monitoring/Icons/";
    private static final String TOOLTIP_APP_SETTINGS = "Application settings configuration";
    private static final String TOOLTIP_PC_SETTINGS = "Computer settings configuration";
    private static final String TOOLTIP_UPDATES_SETTINGS = "Configuration related to an Updates";
    private static final String TOOLTIP_GRAPHICS_SETTINGS = "Configurations related to a Graphics";

    private static final ImageIcon appSettingsTabIcon = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "850768.png")))
    );
    private static final ImageIcon pcSettingsTabIcon = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "18237560.png")))
    );
    private static final ImageIcon updatesSettingsTabIcon = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "2879431.png")))
    );
    private static final ImageIcon graphicSettingsTabIcon = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "5136523.png")))
    );

    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    public static JTabbedPane mainTabbedPanel;
    public static JScrollPane appSettingPanel;
    public static JScrollPane pcSettingPanel;
    public static JScrollPane updatesSettingsPanel;
    public static JScrollPane graphicSettingsPanel;

    public static void createSettingsContainer() {
        mainTabbedPanel = new JTabbedPane();
        mainTabbedPanel.setFont(TEXT_FONT);

        AppSettingsGUI.createAndShowGUI();
        appSettingPanel = AppSettingsGUI.getCreatedGUI();
        mainTabbedPanel.addTab(
                "Application Settings",
                appSettingsTabIcon,
                appSettingPanel,
                TOOLTIP_APP_SETTINGS);

        pcSettingPanel = ComputerSettingsGUI.createComputerSettingsGUI(alarmSounds);
        mainTabbedPanel.addTab(
                "PC Settings",
                pcSettingsTabIcon,
                pcSettingPanel,
                TOOLTIP_PC_SETTINGS);

        updatesSettingsPanel = new JScrollPane();
        mainTabbedPanel.addTab(
                "Updates",
                updatesSettingsTabIcon,
                updatesSettingsPanel,
                TOOLTIP_UPDATES_SETTINGS);

        graphicSettingsPanel = new JScrollPane();
        mainTabbedPanel.addTab(
                "Graphic",
                graphicSettingsTabIcon,
                graphicSettingsPanel,
                TOOLTIP_GRAPHICS_SETTINGS);
    }

    private static ImageIcon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static void refreshAppSettingsTab() {
        if (mainTabbedPanel == null) return;
        AppSettingsGUI.createAndShowGUI();
        appSettingPanel = AppSettingsGUI.getCreatedGUI();

        int index = mainTabbedPanel.indexOfTab("Application Settings");
        if (index != -1) {
            mainTabbedPanel.setComponentAt(index, appSettingPanel);
        }

        mainTabbedPanel.revalidate();
        mainTabbedPanel.repaint();
    }

    public static void refreshPCSettingsTab() {
        if (mainTabbedPanel == null) return;
        pcSettingPanel = ComputerSettingsGUI.createComputerSettingsGUI(alarmSounds);

        int index = mainTabbedPanel.indexOfTab("PC Settings");
        if (index != -1) {
            mainTabbedPanel.setComponentAt(index, pcSettingPanel);
        }

        mainTabbedPanel.revalidate();
        mainTabbedPanel.repaint();
    }
}