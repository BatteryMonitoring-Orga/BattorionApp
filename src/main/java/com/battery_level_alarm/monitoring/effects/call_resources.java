package com.battery_level_alarm.monitoring.effects;
import java.net.URL;

import javax.swing.ImageIcon;

import com.battery_level_alarm.monitoring.core.BatteryLevelAlarm;

public class call_resources {
	public static ImageIcon getImage(String imageName) {
		URL resource = BatteryLevelAlarm.class.getResource("/com/battery_level_alarm/monitoring/BattIco/" + imageName + ".png");
        if (resource == null) {
            throw new IllegalArgumentException("File not found: /com/battery_level_alarm/monitoring/BattIco/" + imageName + ".png");
        }
        
        return new ImageIcon(resource);
	}

    public static ImageIcon getGif(String gifName) {
        String path = "/com/battery_level_alarm/monitoring/BattIco/" + gifName + ".gif";
        URL resource = BatteryLevelAlarm.class.getResource(path);

        if (resource == null) {
            System.err.println("Error: File not found - " + path);
            throw new IllegalArgumentException("File not found: " + path);
        }

        return new ImageIcon(resource);
    }
}