package com.battery_level_alarm.monitoring.battery_report;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HTMLOpener {
    public static void open(String filePath) {
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
    
    public static String readHtmlAsText(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return formatError("File not found", filePath);
        } if (!filePath.toLowerCase().endsWith(".html") && !filePath.toLowerCase().endsWith(".htm")) {
            return formatError("Unsupported file type", filePath);
        } if (!file.isFile() || !file.canRead()) {
            return formatError("Cannot read the file", filePath);
        }
        
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if (!content.toLowerCase().contains("<html")) {
                return formatError("Invalid HTML content", filePath);
            }
            if (!content.toLowerCase().contains("<meta charset")) {
                content = content.replaceFirst("(?i)<head>", "<head>\n<meta charset=\"UTF-8\">");
            }
            
            content = content.replaceAll("[^\\p{IsArabic}\\p{IsLatin}\\p{N}\\p{P}\\p{Z}\\p{Cc}\\p{Cf}\\p{M}\\p{Zs}\\s<>=\"'/\\\\:.;-]", "");
            return content;
        } catch (IOException e) {
            printErrorMessage(e);
            return formatError("Error reading file", filePath);
        }
    }
    
    private static String formatError(String reason, String filePath) {
        return "<h2 style='color:red;'>⚠ " + reason + ": <br><code>" + filePath + "</code></h2>";
    }
}