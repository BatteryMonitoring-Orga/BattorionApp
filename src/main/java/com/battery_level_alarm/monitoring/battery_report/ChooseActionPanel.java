package com.battery_level_alarm.monitoring.battery_report;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.batteryReport;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.BATTERY_REPORT_PATH;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.LifeReportPanelUI.refreshReportPanel;

public class ChooseActionPanel {
    private static final Font FONT = new Font("Serif", Font.BOLD, 14);
    public static JButton reanalyzeButton;
    
    public static JPanel create() {
        File reportFile = new File(BATTERY_REPORT_PATH);
        String buttonText = reportFile.exists() ? "Re-create" : "Create";
        Dimension buttonSize = new Dimension(100, 30);
        
        JPanel wrapper = new JPanel(new GridBagLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Battery report actions:");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        JButton openButton = new JButton("Open");
        openButton.setFont(FONT);
        openButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openButton.setPreferredSize(buttonSize);
        openButton.setMaximumSize(buttonSize);
        openButton.setMinimumSize(buttonSize);
        openButton.setEnabled(reportFile.exists());
        openButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        JButton createButton = new JButton(buttonText);
        createButton.setFont(FONT);
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.setPreferredSize(buttonSize);
        createButton.setMaximumSize(buttonSize);
        createButton.setMinimumSize(buttonSize);
        createButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        reanalyzeButton = new JButton("Reanalyze");
        reanalyzeButton.setFont(FONT);
        reanalyzeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        reanalyzeButton.setPreferredSize(buttonSize);
        reanalyzeButton.setMaximumSize(buttonSize);
        reanalyzeButton.setMinimumSize(buttonSize);
        reanalyzeButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(openButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(createButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(reanalyzeButton);
        wrapper.add(panel, new GridBagConstraints());
        
        createButton.addActionListener(_ -> batteryReport());
        reanalyzeButton.addActionListener(_ -> Thread.ofVirtual().start(() -> refreshReportPanel(true)));
        openButton.addActionListener(_ -> {
            if (reportFile.exists()) {
                HTMLOpener.open(BATTERY_REPORT_PATH);
            } else {
                int confirm = JOptionPane.showConfirmDialog(
                        wrapper,
                        "Battery report not found.\nDo you want to create it?",
                        "File not found",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    batteryReport();
                }
            }
        });
        return wrapper;
    }
}