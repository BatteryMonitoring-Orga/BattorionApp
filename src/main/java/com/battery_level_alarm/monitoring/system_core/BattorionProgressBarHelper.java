package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.applyGradientBackground;

import javax.swing.*;
import java.awt.*;

public class BattorionProgressBarHelper {
    public static void setProgressBarMode(){
        batteryBar.setMinimum(0);
        batteryBar.setMaximum(100);

        if(progressBarInVerticalMode){
            configureProgressBarVertical();
        } else {
            configureProgressBarHorizontal();
        }
    }

    private static void configureProgressBarVertical() {
        batteryBar.setOrientation(JProgressBar.VERTICAL);
        Dimension size = new Dimension(85, 200);
        batteryBar.setPreferredSize(size);
        batteryBar.setMaximumSize(size);
        batteryBar.setMinimumSize(size);
    }

    private static void configureProgressBarHorizontal() {
        batteryBar.setOrientation(JProgressBar.HORIZONTAL);
        Dimension size = new Dimension(225, 85);
        batteryBar.setPreferredSize(size);
        batteryBar.setMaximumSize(size);
        batteryBar.setMinimumSize(size);
    }

    public static void setUpProgressPanel(boolean isFirstMode){
        if(progressBarInVerticalMode){
            progressPanelForVerticalMode(isFirstMode);
        } else {
            progressPanelForHorizontalMode(isFirstMode);
        }
    }

    private static void progressPanelForVerticalMode(boolean isFirstMode) {
        progressPanel.removeAll();
        progressPanel.setLayout(new GridBagLayout());
        progressPanel.setBackground(UIManager.getColor("Panel.background"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        JPanel batteryContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        batteryContainer.add(batteryBar);
        batteryContainer.setOpaque(false);

        JPanel labelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        labelContainer.add(ratioChargeLabel);
        labelContainer.setOpaque(false);

        if (isFirstMode) {
            progressPanel.add(labelContainer, gbc);
            gbc.gridx++;
            progressPanel.add(batteryContainer, gbc);
        } else {
            progressPanel.add(batteryContainer, gbc);
            gbc.gridx++;
            progressPanel.add(labelContainer, gbc);
        }

        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private static void progressPanelForHorizontalMode(boolean isFirstMode) {
        progressPanel.removeAll();
        progressPanel.setLayout(new GridBagLayout());
        progressPanel.setBackground(UIManager.getColor("Panel.background"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        JPanel batteryContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        batteryContainer.add(batteryBar);
        batteryContainer.setOpaque(false);

        JPanel labelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        labelContainer.add(ratioChargeLabel);
        labelContainer.setOpaque(false);

        if (isFirstMode) {
            progressPanel.add(batteryContainer, gbc);
            gbc.gridy++;
            progressPanel.add(labelContainer, gbc);
        } else {
            progressPanel.add(labelContainer, gbc);
            gbc.gridy++;
            progressPanel.add(batteryContainer, gbc);
        }
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    public static void setUpSafeModePanel(){
        safeModePanel = applyGradientBackground(safeModePanel, isDarkMode, true, 25, false);
        safeModePanel.setPreferredSize(new Dimension(150, 120));
        safeModePanel.setMaximumSize(new Dimension(150, 120));
        safeModePanel.setLayout(new BorderLayout());
    }
}