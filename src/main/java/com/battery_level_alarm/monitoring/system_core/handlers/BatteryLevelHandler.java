package com.battery_level_alarm.monitoring.system_core.handlers;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.BatteryLevelHandlerConstants.*;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.DEFAULT_SECONDARY_SOUND_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.visual_effects.AlertSound;

import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class BatteryLevelHandler {
    private static int counter = 0;
    
    public static void handleHighBattery(JProgressBar batteryBar, JLabel alertLabel, Color batteryColor, String msg) {
        SwingUtilities.invokeLater(() -> {
            batteryBar.setForeground(batteryColor);
            alertLabel.setText(SPACE + msg);
        });
        
        if (UserChoices.isEnablePrimarySound()) {
            AlertSound.playSound(UserChoices.getPrimarySoundPath());
        } if (UserChoices.isEnableText()) {
            counter++;
            if (counter > ALERT_AFTER_SECONDS) {
                JOptionPane.showMessageDialog(null, "Battery is high for too long! Please unplug the charger immediately.",
                        CRITICAL_BATTERY_STATUS, JOptionPane.WARNING_MESSAGE);
                counter = 0;
            }
        } else {
            counter = 0;
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }
    }
    
    public static void handleLowBattery(JProgressBar batteryBar, JLabel alertLabel, Color batteryColor, String msg) {
        SwingUtilities.invokeLater(() -> {
            batteryBar.setForeground(batteryColor);
            alertLabel.setText(SPACE + msg);
        });
        
        if (UserChoices.isEnablePrimarySound()) {
            AlertSound.playSound(UserChoices.getPrimarySoundPath());
        } if (UserChoices.isEnableText()) {
            counter++;
            if (counter > ALERT_AFTER_SECONDS) {
                JOptionPane.showMessageDialog(null, "Battery is dangerously low! Please charge immediately.",
                        CRITICAL_BATTERY_STATUS, JOptionPane.WARNING_MESSAGE);
                counter = 0;
            }
        } else {
            counter = 0;
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }
    }
    
    public static void handleBatteryWarning(JProgressBar batteryBar, JLabel alertLabel, String alertText, Color color) {
        SwingUtilities.invokeLater(() -> {
            batteryBar.setForeground(color);
            alertLabel.setText(alertText);
        });
        
        if (UserChoices.isEnableSecondarySound()) {
            triggerAlert();
        }
        
        try {
            Thread.sleep(UserChoices.getRepeatIntervalBeforeRiskPhase() * 1000L);
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }
    }
    
    public static void handleNormalBattery(JProgressBar batteryBar, JLabel alertLabel, Color batteryColor) {
        SwingUtilities.invokeLater(() -> {
            batteryBar.setForeground(batteryColor);
            alertLabel.setText("");
        });
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }
        counter = 0;
    }
    
    private static void triggerAlert() {
        if (UserChoices.getSecondarySoundPath().equals(DEFAULT_SECONDARY_SOUND_PATH)) {
            java.awt.Toolkit.getDefaultToolkit().beep();
        } else {
            AlertSound.useDefaultDuration = true;
            AlertSound.playSound(UserChoices.getSecondarySoundPath());
            AlertSound.useDefaultDuration = false;
        }
    }
}