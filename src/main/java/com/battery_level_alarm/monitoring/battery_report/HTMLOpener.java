package com.battery_level_alarm.monitoring.battery_report;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

import java.awt.Desktop;
import java.io.File;

public class HTMLOpener {
    public static void open(String filePath){
        try {
            File reportFile = new File(filePath);
            for (int i = 0; i < 5; i++) {
                if (reportFile.exists()) {
                    break;
                }
                Thread.sleep(1000);
            }

            if (reportFile.exists()) {
                Desktop.getDesktop().browse(reportFile.toURI());
            }
        } catch (Exception e) {
            printErrorMessage(e);
        }
    }
}