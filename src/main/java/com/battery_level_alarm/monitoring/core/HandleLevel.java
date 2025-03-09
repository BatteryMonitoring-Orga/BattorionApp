package com.battery_level_alarm.monitoring.core;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.effects.AlertSound;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class HandleLevel {
	private static int counter = 0;
    private static final int ALERT_AFTER_SECONDS = 120;
    private static final String CRITICAL_BATTERY_STATUS = "Critical Battery Status";
    public static final String SPACE = "\u2003\u2003";
	
    public static void handleHighBattery(JProgressBar batteryBar, JLabel alertLabel, Color batteryColor, String msg) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
        	batteryBar.setForeground(batteryColor);
        	alertLabel.setText(SPACE + msg);
        });
        if(UserChoices.isEnablePrimarySound()) {
        	AlertSound.playSound(UserChoices.getSoundPath());
        }
        
        counter++;
        if((counter > ALERT_AFTER_SECONDS) && UserChoices.isEnableText()) {
            JOptionPane.showMessageDialog(null, "Battery is high for too long! Please unplug the charger immediately.",
                    CRITICAL_BATTERY_STATUS, JOptionPane.WARNING_MESSAGE);
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
        
        counter++;
        if((counter > ALERT_AFTER_SECONDS) && UserChoices.isEnableText()) {
            JOptionPane.showMessageDialog(null, "Battery is dangerously low! Please charge immediately.",
                    CRITICAL_BATTERY_STATUS, JOptionPane.WARNING_MESSAGE);
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