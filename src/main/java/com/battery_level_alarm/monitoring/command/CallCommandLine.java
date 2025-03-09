package com.battery_level_alarm.monitoring.command;
import static com.battery_level_alarm.monitoring.effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;

public class CallCommandLine {
    public static final String NIR_CMD_PATH = MAIN_FOLDER_PATH + "/NirCMD-main/NirCMD";
    public static final String RESOURCES_PATH = System.getProperty("user.home") + "\\Battorion\\battery-report.html";
    public static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }
    
    private static ProcessBuilder getProcessBuilderForBatteryLevel(String os) throws UnsupportedOperationException {
        if (os.contains("win")) {
            return new ProcessBuilder("C:\\Windows\\System32\\wbem\\WMIC.exe", "Path", "Win32_Battery", "Get", "EstimatedChargeRemaining");
        } else if (os.contains("nix") || os.contains("nux")) {
            return new ProcessBuilder("/bin/cat", "/sys/class/power_supply/BAT0/capacity");
        } else if (os.contains("mac")) {
            return new ProcessBuilder("/usr/bin/pmset", "-g", "batt");
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    private static ProcessBuilder getProcessBuilderForBatteryStatus(String os) throws UnsupportedOperationException {
        if (os.contains("win")) {
            return new ProcessBuilder("C:\\Windows\\System32\\wbem\\WMIC.exe", "Path", "Win32_Battery", "Get", "BatteryStatus");
        } else if (os.contains("nix") || os.contains("nux")) {
            return new ProcessBuilder("/bin/cat", "/sys/class/power_supply/BAT0/status");
        } else if (os.contains("mac")) {
            return new ProcessBuilder("/usr/bin/pmset", "-g", "batt");
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    private static ProcessBuilder getProcessBuilderForBatteryReport(String os) throws UnsupportedOperationException {
        if (os.contains("win")) {
            return new ProcessBuilder("C:\\Windows\\System32\\cmd.exe", "/c", "powercfg", "/batteryreport", "/output", RESOURCES_PATH);
        } else if (os.contains("nix") || os.contains("nux")) {
            return new ProcessBuilder("/bin/cat", "/sys/class/power_supply/BAT0/status");
        } else if (os.contains("mac")) {
            return new ProcessBuilder("/usr/bin/pmset", "-g", "batt");
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    private static ProcessBuilder getProcessBuilderForVolume(String os, int percentage) throws UnsupportedOperationException {
        if (os.toLowerCase().contains("win")) {
            String nircmdPath = NIR_CMD_PATH + "/nircmd.exe";
            if (!new java.io.File(nircmdPath).exists()) {
                throw new UnsupportedOperationException("File not found: " + nircmdPath);
            }
            String command = nircmdPath + " setsysvolume " + calculateVolumeValue(percentage);
            return new ProcessBuilder("cmd.exe", "/c", command);
        } else if (os.toLowerCase().contains("nix") || os.toLowerCase().contains("nux")) {
            String amixerPath = "/usr/bin/amixer";
            if (!new java.io.File(amixerPath).exists()) {
                throw new UnsupportedOperationException("Command not found: " + amixerPath);
            }
            String command = String.format("%s sset 'Master' %d%%", amixerPath, percentage);
            return new ProcessBuilder("/bin/bash", "-c", command);
        } else if (os.toLowerCase().contains("mac")) {
            String osascriptPath = "/usr/bin/osascript";
            if (!new java.io.File(osascriptPath).exists()) {
                throw new UnsupportedOperationException("Command not found: " + osascriptPath);
            }
            String script = String.format("%s -e \"set volume output volume %d\"", osascriptPath, percentage);
            return new ProcessBuilder("/bin/bash", "-c", script);
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    private static int calculateVolumeValue(int percentage) {
        return (int) (65535 * (percentage / 100.0));
    }

    private static ProcessBuilder getProcessBuilderForUnmute(String os) throws UnsupportedOperationException {
        if (os.toLowerCase().contains("win")) {
            String nircmdPath = NIR_CMD_PATH + "/nircmd.exe";
            if (!new java.io.File(nircmdPath).exists()) {
                throw new UnsupportedOperationException("File not found: " + nircmdPath);
            }
            String command = nircmdPath + " mutesysvolume 0";
            return new ProcessBuilder("cmd.exe", "/c", command);
        } else if (os.toLowerCase().contains("nix") || os.toLowerCase().contains("nux")) {
            String amixerPath = "/usr/bin/amixer";
            if (!new java.io.File(amixerPath).exists()) {
                throw new UnsupportedOperationException("Command not found: " + amixerPath);
            }
            String command = "amixer set Master unmute";
            return new ProcessBuilder("bash", "-c", command);
        } else if (os.toLowerCase().contains("mac")) {
            String osascriptPath = "/usr/bin/osascript";
            if (!new java.io.File(osascriptPath).exists()) {
                throw new UnsupportedOperationException("Command not found: " + osascriptPath);
            }
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
            
            /*
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                	JOptionPane.showMessageDialog(null, line, "Battery Report", JOptionPane.INFORMATION_MESSAGE);
                }
            }
             */

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