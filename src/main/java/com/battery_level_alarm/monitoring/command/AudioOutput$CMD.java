package com.battery_level_alarm.monitoring.command;
import static com.battery_level_alarm.monitoring.command.CallCommandLine.getOS;
import static com.battery_level_alarm.monitoring.effects.DisplayMessages.printErrorMessage;

import java.io.IOException;

public class AudioOutput$CMD {
    private static ProcessBuilder callCommandToSetAudioOutput(String os, String sourceName){
        if (sourceName == null || sourceName.isEmpty()) {
            throw new IllegalArgumentException("Source name cannot be null or empty");
        }

        if (os.contains("win")) {
            return new ProcessBuilder("cmd", "/c", "nircmd setdefaultsounddevice \"" + sourceName +"\"");
        } else if (os.contains("nix") || os.contains("nux")) {
            return new ProcessBuilder("pactl", "set-default-sink", sourceName);
        } else if (os.contains("mac")) {
            return new ProcessBuilder("SwitchAudioSource", "-s", sourceName);
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    public static void setSpeakerAsAnAudioOutput(){
        try {
            String os = getOS();
            String sourceName = "سماعات";
            ProcessBuilder processBuilder = null;
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