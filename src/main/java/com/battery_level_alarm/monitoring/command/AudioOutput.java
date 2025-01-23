package com.battery_level_alarm.monitoring.command;
import static com.battery_level_alarm.monitoring.command.CallCommandLine.getOS;
import static com.battery_level_alarm.monitoring.cybernate.AutoLogin.printErrorMessage;

import java.io.IOException;

public class AudioOutput {
    private static ProcessBuilder callCommandToSetAudioOutput(String os, String sourceName){
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
            ProcessBuilder processBuilder = callCommandToSetAudioOutput(os, sourceName);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            printErrorMessage(e);
        }
    }
}