package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.BatteryLevelHandlerConstants.*;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.visual_effects.AlertSound;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class BatteryLevelHandler {
	private static int counter = 0;
    public static void handleHighBattery(JProgressBar batteryBar, JLabel alertLabel, Color batteryColor, String msg) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
        	batteryBar.setForeground(batteryColor);
        	alertLabel.setText(SPACE + msg);
        });
        if(UserChoices.isEnablePrimarySound()) {
        	AlertSound.playSound(UserChoices.getSoundPath());
        }

        if(UserChoices.isEnableText()){
            counter++;
            if((counter > ALERT_AFTER_SECONDS)) {
                JOptionPane.showMessageDialog(null, "Battery is high for too long! Please unplug the charger immediately.",
                        CRITICAL_BATTERY_STATUS, JOptionPane.WARNING_MESSAGE);
                counter = 0;
            }
        } else {
            counter = 0;
        }
        Thread.sleep(1000);
    }
    
    public static void handleLowBattery(JProgressBar batteryBar, JLabel alertLabel, Color batteryColor, String msg) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
        	batteryBar.setForeground(batteryColor);
        	alertLabel.setText(SPACE + msg);
        });
        if(UserChoices.isEnablePrimarySound()) {
        	AlertSound.playSound(UserChoices.getSoundPath());
        }

        if(UserChoices.isEnableText()){
            counter++;
            if(UserChoices.isEnableText() && (counter > ALERT_AFTER_SECONDS)) {
                JOptionPane.showMessageDialog(null, "Battery is dangerously low! Please charge immediately.",
                        CRITICAL_BATTERY_STATUS, JOptionPane.WARNING_MESSAGE);
                counter = 0;
            }
        } else {
            counter = 0;
        }
        Thread.sleep(1000);
    }
    
    public static void handleBatteryWarning(JProgressBar batteryBar, JLabel alertLabel, String alertText, Color color) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
        	batteryBar.setForeground(color);
        	alertLabel.setText(alertText);
        });
        
        if(UserChoices.isEnableSecondarySound()) {
            triggerAlert();
        }
        Thread.sleep(UserChoices.getRepeatIntervalBeforeRiskPhase() * 1000L);
    }
    
    public static void handleNormalBattery(JProgressBar batteryBar, JLabel alertLabel, Color batteryColor) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
            batteryBar.setForeground(batteryColor);
            alertLabel.setText("");
        });
        Thread.sleep(500);
        counter = 0;
    }
    
    private static void triggerAlert() {
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}