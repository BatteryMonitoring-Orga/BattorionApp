package com.battery_level_alarm.monitoring.command;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;

import static com.battery_level_alarm.monitoring.effects.DisplayMessages.printErrorMessage;

public class CallCommandLine {
    public static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }
    
    private static ProcessBuilder getProcessBuilderForBatteryLevel(String os) throws UnsupportedOperationException {
        if (os.contains("win")) {
            return new ProcessBuilder("cmd", "/c", "WMIC Path Win32_Battery Get EstimatedChargeRemaining");
        } else if (os.contains("nix") || os.contains("nux")) {
            return new ProcessBuilder("cat", "/sys/class/power_supply/BAT0/capacity");
        } else if (os.contains("mac")) {
            return new ProcessBuilder("pmset", "-g", "batt");
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }
    
    private static ProcessBuilder getProcessBuilderForBatteryStatus(String os) throws UnsupportedOperationException {
        if (os.contains("win")) {
            return new ProcessBuilder("cmd", "/c", "WMIC Path Win32_Battery Get BatteryStatus");
        } else if (os.contains("nix") || os.contains("nux")) {
            return new ProcessBuilder("cat", "/sys/class/power_supply/BAT0/status");
        } else if (os.contains("mac")) {
            return new ProcessBuilder("pmset", "-g", "batt");
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }
    
    private static ProcessBuilder getProcessBuilderForBatteryReport(String os) throws UnsupportedOperationException {
        if (os.contains("win")) {
            String desktopPath = System.getProperty("user.home") + "\\Desktop\\battery-report.html";
            return new ProcessBuilder("cmd", "/c", "powercfg", "/batteryreport", "/output", desktopPath);
        } else if (os.contains("nix") || os.contains("nux")) {
            return new ProcessBuilder("cat", "/sys/class/power_supply/BAT0/status");
        } else if (os.contains("mac")) {
            return new ProcessBuilder("pmset", "-g", "batt");
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }
    
    private static int calculateVolumeValue(int percentage) {
        int maxVolume = 65535;
        return (percentage * maxVolume) / 100;
    }
    
    private static ProcessBuilder getProcessBuilderForVolume(String os, int percentage) throws UnsupportedOperationException {
        if (os.contains("win")) {
            String command = "nircmd.exe setsysvolume " + calculateVolumeValue(percentage);
            return new ProcessBuilder("cmd.exe", "/c", command);
        } else if (os.contains("nix") || os.contains("nux")) {
            String command = String.format("amixer sset 'Master' %d%%", percentage);
            return new ProcessBuilder("bash", "-c", command);
        } else if (os.contains("mac")) {
            String script = String.format("osascript -e \"set volume output volume %d\"", percentage);
            return new ProcessBuilder("bash", "-c", script);
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    private static ProcessBuilder getProcessBuilderForUnmute(String os) throws UnsupportedOperationException {
        if (os.contains("win")) {
            String command = "nircmd.exe mutesysvolume 0";
            return new ProcessBuilder("cmd.exe", "/c", command);
        } else if (os.contains("nix") || os.contains("nux")) {
            String command = "amixer set Master unmute";
            return new ProcessBuilder("bash", "-c", command);
        } else if (os.contains("mac")) {
            String script = "osascript -e \"set volume output muted false\"";
            return new ProcessBuilder("bash", "-c", script);
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    private static int parseBatteryLevel(String line) {
        try {
            return Integer.parseInt(line.replaceAll("[^0-9]", "").trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }
    
    private static boolean parseBatteryStatus(String line, boolean isMac) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }
        
        if (!isMac) {
            try {
            	int status = Integer.parseInt(line.replaceAll("[^0-9]", "").trim());
                return (status == 2);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return line.contains("charging");
        }
    }
    
    public static int getBatteryLevel() throws Exception {
        String os = getOS();
        ProcessBuilder processBuilder = getProcessBuilderForBatteryLevel(os);
        
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int batteryLevel = parseBatteryLevel(line);
                if (batteryLevel != -1) {
                    return batteryLevel;
                }
            }
        }
        throw new Exception("Unable to retrieve battery level");
    }
    
    public static boolean getBatteryStatus() throws Exception {
        String os = getOS();
        ProcessBuilder processBatteryStatus = getProcessBuilderForBatteryStatus(os);
        boolean isCharging;
        
        boolean isMac = os.contains("mac");
        Process statusProcess = processBatteryStatus.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(statusProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                isCharging = parseBatteryStatus(line, isMac);
                if (isCharging) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void setPCVolume(int percentage) {
    	try {
    		String os = getOS();
            ProcessBuilder processBuilder = getProcessBuilderForVolume(os, percentage);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("The thread was interrupted while waiting for the process.", e);
            }
        } catch (IOException | RuntimeException e) {
            printErrorMessage(e);
        }
    }

    public static void setSoundUnmute(){
        try {
            String os = getOS();
            ProcessBuilder processBuilder = getProcessBuilderForUnmute(os);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("The thread was interrupted while waiting for the process.", e);
            }
        } catch (IOException | RuntimeException e) {
            printErrorMessage(e);
        }
    }

    public static void batteryReport() {
        try {
            String os = getOS();
            ProcessBuilder processBuilder = getProcessBuilderForBatteryReport(os);
            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                	JOptionPane.showMessageDialog(null, line, "Battery Report", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("The thread was interrupted while waiting for the process.", e);
            }
        } catch (IOException | RuntimeException e) {
            printErrorMessage(e);
        }
    }
}