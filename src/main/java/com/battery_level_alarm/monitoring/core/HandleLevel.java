package com.battery_level_alarm.monitoring.core;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.effects.AlertSound;

public class HandleLevel {
	private static int counter = 0;
	
    public static void handleHighBattery(JProgressBar batteryBar, JLabel alertLabel) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
        	batteryBar.setForeground(Color.DARK_GRAY);
        	alertLabel.setText("Battery is high! Please unplug the charger.");
        });
        if(UserChoices.isEnablePrimarySound()) {
        	AlertSound.playSound(UserChoices.getSoundPath());
        }
        
        counter++;
        if((counter > 5) && UserChoices.isEnableText()) {
            JOptionPane.showMessageDialog(null, "Battery is high for too long! Please unplug the charger immediately.", 
                    "Critical Battery Status", JOptionPane.WARNING_MESSAGE);
        }
        Thread.sleep(1000);
    }
    
    public static void handleLowBattery(JProgressBar batteryBar, JLabel alertLabel) throws InterruptedException {
        SwingUtilities.invokeLater(() -> {
        	batteryBar.setForeground(Color.RED);
        	alertLabel.setText("Battery is low! Please charge.");
        });
        if(UserChoices.isEnablePrimarySound()) {
        	AlertSound.playSound(UserChoices.getSoundPath());
        }
        
        counter++;
        if((counter > 5) && UserChoices.isEnableText()) {
            JOptionPane.showMessageDialog(null, "Battery is dangerously low! Please charge immediately.", 
                    "Critical Battery Status", JOptionPane.WARNING_MESSAGE);
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
    
    public static void handleNormalBattery(JLabel alertLabel) throws InterruptedException {
        SwingUtilities.invokeLater(() -> alertLabel.setText(""));
        Thread.sleep(500);
        counter = 0;
    }
    
    private static void triggerAlert() {
        java.awt.Toolkit.getDefaultToolkit().beep(); // Play alert sound
    }
}