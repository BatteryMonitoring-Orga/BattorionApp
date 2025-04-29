package com.battery_level_alarm.monitoring.command_executors;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.NIR_CMD_PATH;
import static com.battery_level_alarm.monitoring.command_executors.CallCommandLine.getOS;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import java.io.IOException;

public class AudioOutput$CMD {
    private static ProcessBuilder callCommandToSetAudioOutput(String os, String sourceName){
        if (sourceName == null || sourceName.isEmpty()) {
            throw new IllegalArgumentException("Source name cannot be null or empty");
        }
        
        if (os.contains("win")) {
            String nircmdPath = NIR_CMD_PATH + "/nircmd.exe";
            if (!new java.io.File(nircmdPath).exists()) {
                throw new UnsupportedOperationException("File not found: " + nircmdPath);
            }
            String command = nircmdPath + " setdefaultsounddevice \"" + sourceName + "\"";
            return new ProcessBuilder("cmd", "/c", command);
        } else if (os.contains("nix") || os.contains("nux")) {
            String command = "pactl set-default-sink " + sourceName;
            if (!new java.io.File("/usr/bin/pactl").exists()) {
                throw new UnsupportedOperationException("pactl command not found");
            }
            return new ProcessBuilder("bash", "-c", command);
        } else if (os.contains("mac")) {
            String command = "SwitchAudioSource -s " + sourceName;
            if (!new java.io.File("/usr/local/bin/SwitchAudioSource").exists()) {
                throw new UnsupportedOperationException("SwitchAudioSource command not found");
            }
            return new ProcessBuilder("bash", "-c", command);
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    public static void setAudioOutputDevice(String sourceName){
        try {
            String os = getOS();
            ProcessBuilder processBuilder;
            try{
                processBuilder = callCommandToSetAudioOutput(os, sourceName);
            } catch (IllegalArgumentException e) {
                printErrorMessage(e);
                return;
            }

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
}