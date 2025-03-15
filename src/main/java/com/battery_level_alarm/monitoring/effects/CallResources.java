package com.battery_level_alarm.monitoring.effects;
import java.net.URL;
import javax.swing.ImageIcon;

import com.battery_level_alarm.monitoring.core.BattorionMain;

public class CallResources {
	public static ImageIcon getImage(String parentFolder, String imageName) {
		URL resource = BattorionMain.class.getResource( parentFolder + imageName + ".png");
        if (resource == null) {
            throw new IllegalArgumentException("File not found: " + parentFolder + imageName + ".png");
        }
        
        return new ImageIcon(resource);
	}

    public static ImageIcon getGif(String parentFolder, String gifName) {
        String path = parentFolder + gifName + ".gif";
        URL resource = BattorionMain.class.getResource(path);

        if (resource == null) {
            System.err.println("Error: File not found - " + path);
            throw new IllegalArgumentException("File not found: " + path);
        }

        return new ImageIcon(resource);
    }
}