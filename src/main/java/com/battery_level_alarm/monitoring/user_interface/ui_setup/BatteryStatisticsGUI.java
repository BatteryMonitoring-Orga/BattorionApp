package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.launchAndOpenTopic;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.STATISTICS_QUESTIONNAIRE;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UI.TEXT_FONT;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ChargingStatus.*;
import static com.battery_level_alarm.monitoring.system_automation.Timing.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GUI_ComponentConstraints.setTableConstraints;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.setButtonFontAndSize;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BatteryStatisticsGUI {
    private static JPanel BatteryStatisticsPanel = new JPanel();
    public static JPanel getBatteryStatisticsPanel(){
        return BatteryStatisticsPanel;
    }

    public static void createGUI(){
        BatteryStatisticsPanel.setLayout(new BoxLayout(BatteryStatisticsPanel, BoxLayout.Y_AXIS));
        JTable table = getJTable();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(125);
        table.getColumnModel().getColumn(2).setPreferredWidth(125);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JButton aboutButton = new JButton("About This Panel");
        setButtonFontAndSize(
                aboutButton, new Font(Font.SERIF, Font.PLAIN + Font.BOLD, 14),
                160, 30, SwingConstants.CENTER, SwingConstants.CENTER);
        aboutButton.addActionListener(_ -> Thread.ofVirtual().start(() ->
                launchAndOpenTopic(STATISTICS_QUESTIONNAIRE, 0)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setPreferredSize(new Dimension(360, 40));
        buttonPanel.add(aboutButton);

        BatteryStatisticsPanel = new JPanel(new BorderLayout());
        BatteryStatisticsPanel.add(scrollPane, BorderLayout.CENTER);
        BatteryStatisticsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private static JTable getJTable() {
        String[][] data = {
                {"Status", isIn_ChargingMode, isIn_DisChargingMode},
                {"Sharping Difference", getChargingSharpDifference() + "", getDischargingSharpDifference() + ""},
                {"How Long Time", convertMillisecondsToShortTime(getHowLongBatteryNeedToFull())
                        , convertMillisecondsToShortTime(getHowLongBatteryNeedToDump())},
                {"Started at", getChargingStartAtLevel() + "", getDisChargingStartAtLevel() + ""},
                {"End at", getDisChargingStartAtLevel() + "", getChargingStartAtLevel() + ""}
        };

        String[] columns = {"\u2003Value\u2003", "\u2003" + isIn_ChargingMode + "\u2003", "\u2003" + isIn_DisChargingMode + "\u2003"};
        return setTableConstraints(data, columns);
    }

    static JScrollPane createChargingHistoryPanel(){
        ArrayList<Double> chHistory = getHistoryMap().get("Charging");
        HashMap<Double, Integer> frequencyMap = new HashMap<>();
        for (double value : chHistory) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }

        StringBuilder history = new StringBuilder("Charging History:\n");
        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            history.append("Value: ").append(entry.getKey())
                    .append(",  Number of screening times: ").append(entry.getValue())
                    .append("\n");
        }

        JTextArea chTextArea = new JTextArea(history.toString());
        chTextArea.setFont(TEXT_FONT);
        chTextArea.setEditable(false);
        return new JScrollPane(chTextArea);
    }

    static JScrollPane createDisChargingHistoryPanel() {
        ArrayList<Double> disHistory = getHistoryMap().get("Not Charging");
        HashMap<Double, Integer> frequencyMap = new HashMap<>();
        for (double value : disHistory) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }

        StringBuilder history = new StringBuilder("Discharging History:\n");
        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            history.append("Value: ").append(entry.getKey())
                    .append(",  Number of screening times: ").append(entry.getValue())
                    .append("\n");
        }

        JTextArea disTextArea = new JTextArea(history.toString());
        disTextArea.setFont(TEXT_FONT);
        disTextArea.setEditable(false);
        return new JScrollPane(disTextArea);
    }

    public static String convertMillisecondsToShortTime(long milliseconds) {
        long limit = 21600000;
        if (milliseconds <= 0 || milliseconds >= limit) {
            return "0s";
        }

        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours >= 1) {
            return String.format("%d:%d:%d", hours, minutes, seconds);
        } else if (minutes >= 1) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}