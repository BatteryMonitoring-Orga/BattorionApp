package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.TEXT_FONT;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.BatteryStatisticsGUI.createChargingHistoryPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.BatteryStatisticsGUI.createDisChargingHistoryPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.ICONS_FOLDER_PATH;
import com.battery_level_alarm.monitoring.command_executors.DiskSpaceInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class StatisticsContainerClass {
    private static final String TOOLTIP_DISK_INFO_SETTINGS = "Application settings configuration";
    private static final String TOOLTIP_BATTERY_STATISTICS_SETTINGS = "Computer settings configuration";
    private static final ImageIcon diskInfoTabIcon = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "5134759.png")))
    );
    private static final ImageIcon batteryStatisticsTabIcon = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "12196400.png")))
    );
    private static final ImageIcon historyTabIcon = resizeIcon(
            new ImageIcon(Objects.requireNonNull(SettingsContainerClass.class.getResource(ICONS_FOLDER_PATH + "1979179.png")))
    );
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    public static JTabbedPane statisticsMainTabbedPanel;
    public static JPanel diskInfoPanel;
    public static JPanel batteryStatisticsPanel;
    public static JScrollPane historyPanel;

    public static void createStatisticsContainer() {
        statisticsMainTabbedPanel = new JTabbedPane();
        statisticsMainTabbedPanel.setFont(TEXT_FONT);

        diskInfoPanel = PrepareDiskInfoGUI.getDiskInfoPanel();
        statisticsMainTabbedPanel.addTab(
                "Disk Information",
                diskInfoTabIcon,
                diskInfoPanel,
                TOOLTIP_DISK_INFO_SETTINGS);

        batteryStatisticsPanel = BatteryStatisticsGUI.getBatteryStatisticsPanel();
        statisticsMainTabbedPanel.addTab(
                "Battery Statistics",
                batteryStatisticsTabIcon,
                batteryStatisticsPanel,
                TOOLTIP_BATTERY_STATISTICS_SETTINGS);

        historyPanel = new JScrollPane(prepareHistoryPanel());
        statisticsMainTabbedPanel.addTab(
                "History",
                historyTabIcon,
                historyPanel,
                TOOLTIP_BATTERY_STATISTICS_SETTINGS);
    }

    private static ImageIcon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static void refreshDiskInfoTab() {
        if(statisticsMainTabbedPanel == null){
            return;
        }

        DiskSpaceInfo.DiskSpace();
        PrepareDiskInfoGUI.createGUI();
        diskInfoPanel = PrepareDiskInfoGUI.getDiskInfoPanel();
        int index = statisticsMainTabbedPanel.indexOfTab("Disk Information");
        if (index != -1) {
            statisticsMainTabbedPanel.setComponentAt(index, diskInfoPanel);
        }
        statisticsMainTabbedPanel.revalidate();
        statisticsMainTabbedPanel.repaint();
    }

    private static JTabbedPane prepareHistoryPanel(){
        JTabbedPane historyPanel = new JTabbedPane();
        historyPanel.setFont(TEXT_FONT);

        JScrollPane chargingTabbedPanel = createChargingHistoryPanel();
        historyPanel.addTab("Ch-History", chargingTabbedPanel);
        JScrollPane dischargingTabbedPanel = createDisChargingHistoryPanel();
        historyPanel.addTab("Dis-History", dischargingTabbedPanel);
        return historyPanel;
    }
}