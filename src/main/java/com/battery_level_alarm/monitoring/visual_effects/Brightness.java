package com.battery_level_alarm.monitoring.visual_effects;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Brightness {
    public static final int MAX_BRIGHTNESS = 100;
    public static final int MIN_BRIGHTNESS = 0;
    private static final int DEFAULT_BRIGHTNESS = 50;
    private static int CURRENT_BRIGHTNESS = 0;

    public static int getDefaultBrightness() {
        return DEFAULT_BRIGHTNESS;
    }

    public static int getCurrentBrightness() {
        return CURRENT_BRIGHTNESS;
    }
    public static void setCurrentBrightness(int brightness) {
        Brightness.CURRENT_BRIGHTNESS = brightness;
    }

    public static void BrightnessProcess(int brightness, boolean toGetBrightness){
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String brightnessOutput = executeProcess(getBrightnessProcess(os, toGetBrightness, brightness)).replaceAll("[^0-9]", "");
            if (!brightnessOutput.isEmpty()) {
                setCurrentBrightness(Integer.parseInt(brightnessOutput));
            }
        } catch (Exception e) {
            printErrorMessage(e);
        }
    }

    private static String executeProcess(ProcessBuilder processBuilder) throws IOException {
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString().trim();
    }

    private static ProcessBuilder getBrightnessProcess(String os, boolean getBrightness, int brightnessValue) {
        if (os.contains("win")) {
            if (getBrightness) {
                return new ProcessBuilder("powershell", "-Command", "(Get-CimInstance -Namespace root/WMI -ClassName WmiMonitorBrightness).CurrentBrightness");
            } else {
                return new ProcessBuilder("powershell", "-Command", "(Get-WmiObject -Namespace root/WMI -Class WmiMonitorBrightnessMethods).WmiSetBrightness(1," + brightnessValue + ")");
            }
        } else if (os.contains("nix") || os.contains("nux")) {
            if (getBrightness) {
                return new ProcessBuilder("cat", "/sys/class/backlight/intel_backlight/brightness");
            } else {
                return new ProcessBuilder("sh", "-c", "echo " + brightnessValue + " | sudo tee /sys/class/backlight/intel_backlight/brightness");
            }
        } else if (os.contains("mac")) {
            if (getBrightness) {
                return new ProcessBuilder("brightness", "-l");
            } else {
                return new ProcessBuilder("brightness", String.format("%.2f", brightnessValue / 100.0));
            }
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    public static JPopupMenu createBrightnessMenu(int brightnessValue) {
        JPopupMenu brightnessMenu = new JPopupMenu();
        JSlider brightnessSlider = new JSlider(JSlider.HORIZONTAL, MIN_BRIGHTNESS, MAX_BRIGHTNESS, brightnessValue);
        JLabel brightnessLabel = new JLabel(brightnessValue + " %");
        brightnessLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        brightnessSlider.addChangeListener(e -> {
            JSlider source = (JSlider)e.getSource();
            int newValue = source.getValue();
            brightnessLabel.setText(newValue + " %");
            if (!source.getValueIsAdjusting()) {
                BrightnessProcess(newValue, false);
            }
        });
        
        brightnessMenu.add(brightnessLabel);
        brightnessMenu.add(Box.createVerticalStrut(5));
        brightnessMenu.add(brightnessSlider);
        return brightnessMenu;
    }
}