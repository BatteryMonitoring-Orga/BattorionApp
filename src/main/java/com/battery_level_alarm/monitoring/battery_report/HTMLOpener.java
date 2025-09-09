package com.battery_level_alarm.monitoring.battery_report;
import javafx.scene.web.WebEngine;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
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
    
    public static void safeLoad(WebEngine engine, String path) {
        File file = new File(path);
        if (file.exists()) {
            engine.load(file.toURI().toString());
        } else {
            URL resource = HTMLOpener.class.getResource(path);
            if (resource == null) {
                resource = HTMLOpener.class.getResource("/" + path);
            }
            if (resource != null) {
                engine.load(resource.toExternalForm());
                engine.locationProperty().addListener((_, _, newLocation) -> {
                    if (newLocation != null && (newLocation.startsWith("http://") || newLocation.startsWith("https://"))) {
                        try {
                            java.awt.Desktop.getDesktop().browse(new URI(newLocation));
                        } catch (Exception e) {
                            printErrorMessage(e);
                        }
                        engine.getHistory().go(-1);
                    }
                });
            } else {
                engine.loadContent("<h2 style='color:red;'>⚠ File not found: <br><code>" + path + "</code></h2>");
            }
        }
    }
    
    public static String readHtmlAsText(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile() && file.canRead()) {
            if (!filePath.toLowerCase().endsWith(".html") && !filePath.toLowerCase().endsWith(".htm")) {
                return formatError("Unsupported file type", filePath);
            }
            
            try {
                String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                return cleanAndFixHtml(content, filePath);
            } catch (IOException e) {
                printErrorMessage(e);
                return formatError("Error reading file", filePath);
            }
        }
        
        try (InputStream is = HTMLOpener.class.getResourceAsStream(filePath)) {
            if (is == null) {
                return formatError("File not found", filePath);
            }
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return cleanAndFixHtml(content, filePath);
        } catch (IOException e) {
            printErrorMessage(e);
            return formatError("Error reading resource", filePath);
        }
    }
    
    private static String cleanAndFixHtml(String content, String sourcePath) {
        if (!content.toLowerCase().contains("<html")) {
            return formatError("Invalid HTML content", sourcePath);
        } if (!content.toLowerCase().contains("<meta charset")) {
            content = content.replaceFirst("(?i)<head>", "<head>\n<meta charset=\"UTF-8\">");
        }
        content = content.replaceAll("[^\\p{IsArabic}\\p{IsLatin}\\p{N}\\p{P}\\p{Z}\\p{Cc}\\p{Cf}\\p{M}\\p{Zs}\\s<>=\"'/\\\\:.;-]", "");
        return content;
    }
    
    private static String formatError(String reason, String filePath) {
        return "<h2 style='color:red;'>⚠ " + reason + ": <br><code>" + filePath + "</code></h2>";
    }
}