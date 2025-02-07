package com.battery_level_alarm.monitoring.preparing_gui;
import com.notifications.system_tray_notifications.basics.AlarmSounds;

import static com.battery_level_alarm.monitoring.basics.StaticQuestionnaire.aboutBatteryStatisticsPanel;
import static com.battery_level_alarm.monitoring.core.BattorionMain.*;
import static com.battery_level_alarm.monitoring.preparing_gui.PrepareDiskInfoGUI.*;
import static com.battery_level_alarm.monitoring.cybernate.Timing.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BatteryStatisticsGUI {
    private static JPanel BatteryStatisticsPanel;

    public static JPanel getBatteryStatisticsPanel(){
        return BatteryStatisticsPanel;
    }

    public static void createGUI(AlarmSounds alarmSounds){
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

        JButton historyButton = new JButton("History");
        setButtonFontAndSize(historyButton, 80, 30);
        historyButton.addActionListener(e -> {
            JTabbedPane historyPanel = prepareHistoryPanel();
            JScrollPane historyScrollPane = new JScrollPane(historyPanel);
            historyScrollPane.setPreferredSize(new java.awt.Dimension(400, 200));
            JOptionPane.showMessageDialog(null, historyScrollPane, "📄 History", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton pcDetailsButton = new JButton("PC - Details");
        setButtonFontAndSize(pcDetailsButton, 120, 30);
        pcDetailsButton.addActionListener(e ->{
            JPanel pcDetailsPanel = PC_DetailsGUI.createPC$GUI(alarmSounds);
            JScrollPane historyScrollPane = new JScrollPane(pcDetailsPanel);
            historyScrollPane.setPreferredSize(new java.awt.Dimension(550, 280));
            JOptionPane.showMessageDialog(null, historyScrollPane, "PC Details Dialog", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton aboutButton = new JButton("About This Panel");
        setButtonFontAndSize(aboutButton, 160, 30);
        aboutButton.addActionListener(e ->{
            aboutBatteryStatisticsPanel();
        });

        JPanel packageButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setPreferredSize(new Dimension(360, 40));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(historyButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(pcDetailsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(aboutButton);
        packageButtons.add(buttonPanel);

        BatteryStatisticsPanel = new JPanel(new BorderLayout());
        BatteryStatisticsPanel.add(scrollPane, BorderLayout.CENTER);
        BatteryStatisticsPanel.add(packageButtons, BorderLayout.SOUTH);
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
        return getJTable(data, columns);
    }

    private static JTabbedPane prepareHistoryPanel(){
        JTabbedPane historyPanel = new JTabbedPane();
        JScrollPane chargingTabbedPanel = createChargingHistoryPanel();
        historyPanel.addTab("Ch-History", chargingTabbedPanel);

        JScrollPane dischargingTabbedPabel = createDisChargingHistoryPanel();
        historyPanel.addTab("Dis-History", dischargingTabbedPabel);
        return historyPanel;
    }

    private static JScrollPane createChargingHistoryPanel(){
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
        chTextArea.setEditable(false);
        return new JScrollPane(chTextArea);
    }

    private static JScrollPane createDisChargingHistoryPanel() {
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
        disTextArea.setEditable(false);
        return new JScrollPane(disTextArea);
    }

    private static JTable getJTable(String[][] data, String[] columns) {
        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        table.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
        return table;
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