package com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ICONS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.TEXT_FONT;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SettingsContainerClass {
    private static final String TOOLTIP_APP_SETTINGS = "Manage general application preferences and behavior.";
    private static final String TOOLTIP_PC_SETTINGS = "Adjust settings related to your computer and hardware.";
    private static final String TOOLTIP_GRAPHICS_SETTINGS = "Configure graphics options and visual performance.";
    private static final String TOOLTIP_TRAY_SETTINGS = "Configure how the application appears and behaves in the system tray area.";
    private static final String TOOLTIP_UPDATES_SETTINGS = "Control update preferences and version management.";
    private static final String TOOLTIP_THEMES_SETTINGS = "Manage visual themes and interface customization options.";
    
    private static final ImageIcon APP_SETTINGS_TAB_ICON = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "850768.png")))
    );
    private static final ImageIcon PC_SETTINGS_TAB_ICON = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "18237560.png")))
    );
    private static final ImageIcon GRAPHIC_SETTINGS_TAB_ICON = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "5136523.png")))
    );
    private static final ImageIcon TRAY_SETTINGS_TAB_ICON = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "battorion_background.png")))
    );
    private static final ImageIcon UPDATES_SETTINGS_TAB_ICON = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "2879431.png")))
    );
    private static final ImageIcon UI_THEMES_SETTINGS_TAB_ICON = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "themes.png")))
    );
    
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    public static JTabbedPane mainTabbedPanel;
    public static JScrollPane appSettingPanel;
    public static JScrollPane pcSettingPanel;
    public static JScrollPane graphicSettingsPanel;
    public static JScrollPane traySettingPanel;
    public static JScrollPane updatesSettingsPanel;
    public static JPanel uiThemesSettingsPanel;

    public static void createSettingsContainer() {
        mainTabbedPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        mainTabbedPanel.setBackground(DashboardPanel.getBackground());
        mainTabbedPanel.setFont(TEXT_FONT);
        
        AppSettingsGUI.createAndShowGUI();
        appSettingPanel = AppSettingsGUI.getApplicationSettingsGUI();
        mainTabbedPanel.addTab(
                "Application",
                APP_SETTINGS_TAB_ICON,
                appSettingPanel,
                TOOLTIP_APP_SETTINGS);

        pcSettingPanel = ComputerSettingsGUI.createComputerSettingsGUI(alarmSounds);
        mainTabbedPanel.addTab(
                "System",
                PC_SETTINGS_TAB_ICON,
                pcSettingPanel,
                TOOLTIP_PC_SETTINGS);
        
        graphicSettingsPanel = GraphSettingsGUI.createGraphSettingsGUI();
        graphicSettingsPanel.setBackground(DashboardPanel.getBackground());
        mainTabbedPanel.addTab(
                "Graphic",
                GRAPHIC_SETTINGS_TAB_ICON,
                graphicSettingsPanel,
                TOOLTIP_GRAPHICS_SETTINGS);
        
        traySettingPanel = TraySettingsGUI.createTraySettingsGUI();
        mainTabbedPanel.addTab(
                "Tray",
                TRAY_SETTINGS_TAB_ICON,
                traySettingPanel,
                TOOLTIP_TRAY_SETTINGS);
        
        updatesSettingsPanel = UpdateSettingsGUI.createUpdateSettingsGUI();
        mainTabbedPanel.addTab(
                "Updates",
                UPDATES_SETTINGS_TAB_ICON,
                updatesSettingsPanel,
                TOOLTIP_UPDATES_SETTINGS);
        
        UIThemesGUI.createAndShowGUI();
        uiThemesSettingsPanel = UIThemesGUI.getUIThemeGUI();
        mainTabbedPanel.addTab(
                "Themes",
                UI_THEMES_SETTINGS_TAB_ICON,
                uiThemesSettingsPanel,
                TOOLTIP_THEMES_SETTINGS);
    }

    private static ImageIcon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static void refreshAppSettingsTab() {
        if (mainTabbedPanel == null) return;
        AppSettingsGUI.createAndShowGUI();
        appSettingPanel = AppSettingsGUI.getApplicationSettingsGUI();

        int index = mainTabbedPanel.indexOfTab("Application");
        if (index != -1) {
            mainTabbedPanel.setComponentAt(index, appSettingPanel);
        }

        mainTabbedPanel.revalidate();
        mainTabbedPanel.repaint();
    }

    public static void refreshPCSettingsTab() {
        if (mainTabbedPanel == null) return;
        pcSettingPanel = ComputerSettingsGUI.createComputerSettingsGUI(alarmSounds);

        int index = mainTabbedPanel.indexOfTab("System");
        if (index != -1) {
            mainTabbedPanel.setComponentAt(index, pcSettingPanel);
        }

        mainTabbedPanel.revalidate();
        mainTabbedPanel.repaint();
    }
    
    public static void refreshGraphSettingsTab() {
        if (mainTabbedPanel == null) return;
        graphicSettingsPanel = GraphSettingsGUI.createGraphSettingsGUI();
        int index = mainTabbedPanel.indexOfTab("Graphic");
        if (index != -1) {
            mainTabbedPanel.setComponentAt(index, graphicSettingsPanel);
        }
        
        mainTabbedPanel.revalidate();
        mainTabbedPanel.repaint();
    }
}