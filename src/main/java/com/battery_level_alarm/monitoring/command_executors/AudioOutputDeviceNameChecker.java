package com.battery_level_alarm.monitoring.command_executors;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.addItemToAudioList;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.getAudioDevices;
import static com.battery_level_alarm.monitoring.system_core.Battorion.audioOutputDeviceDashTextField;
import static com.battery_level_alarm.monitoring.system_core.Battorion.isMonitorRunning;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isAudioDeviceCmdletsInstalled;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.activeAudioDeviceName;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AudioOutputDeviceNameChecker {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static Thread AudioDeviceThread;
    public static String lastOutputDevice = "";
    
    public static void threadStart() {
        if(AudioDeviceThread != null){
            return;
        }
        AudioDeviceThread = Thread.ofVirtual().start(AudioOutputDeviceNameChecker::startDeviceChecking);
    }

    private static void startDeviceChecking() {
        scheduler.scheduleAtFixedRate(() -> {
            if (isMonitorRunning) {
                doExecutionSingleton();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void doExecutionSingleton() {
        if (isAudioDeviceCmdletsInstalled) {
            String returnedOutput = getCurrentAudioOutputDevice();
            String currentDevice = checkDevicesList(returnedOutput);
            String currentDeviceFullName = getDeviceFullName(returnedOutput);
            if (!currentDevice.isEmpty() && !currentDevice.equals(lastOutputDevice)) {
                lastOutputDevice = currentDevice;
                activeAudioDeviceName.setText(lastOutputDevice);
                audioOutputDeviceDashTextField.setText(currentDeviceFullName);
            } else if (!lastOutputDevice.equals(currentDeviceFullName)){
                audioOutputDeviceDashTextField.setText(currentDeviceFullName);
            }
        }
    }
    
    public static String[] getAudioOutputDevice() {
        if(isAudioDeviceCmdletsInstalled) {
            String returnedOutput = getCurrentAudioOutputDevice();
            String currentDevice = checkDevicesList(returnedOutput);
            String currentDeviceFullName = getDeviceFullName(returnedOutput);
            return new String[] {
                    currentDevice,
                    currentDeviceFullName
            };
        }
        return new String[]{};
    }
    
    private static String getCurrentAudioOutputDevice() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return executeCommand("powershell -command \"chcp 65001; (Get-AudioDevice -Playback).Name\"");
        } else if (os.contains("mac")) {
            return executeCommand("SwitchAudioSource -c");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("linux")) {
            return executeCommand("pactl get-default-sink");
        }
        return "";
    }

    private static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder processBuilder;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder = new ProcessBuilder("powershell", "-command", command);
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                printErrorMessage(new Exception("Command execution timed out: " + command));
            }
        } catch (IOException | InterruptedException e) {
            printErrorMessage(e);
        }
        return output.toString().trim();
    }

    private static String checkDevicesList(String currentDevice){
        for(String entry : getAudioDevices()){
            if(currentDevice.contains(entry)){
                return entry;
            }
        }
        addItemToAudioList(currentDevice);
        return currentDevice;
    }
    
    private static String getDeviceFullName(String currentDevice){
        String[] lines = currentDevice.split("\\R");
        for (String line : lines) {
            for (String entry : getAudioDevices()) {
                if (line.contains(entry)) {
                    return line.trim();
                }
            }
        }
        return currentDevice;
    }
}