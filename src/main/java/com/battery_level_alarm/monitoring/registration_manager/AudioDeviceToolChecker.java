package com.battery_level_alarm.monitoring.registration_manager;
import static com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager.saveGeneralConfigurations;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isAudioDeviceCmdletsInstalled;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class AudioDeviceToolChecker {
    private static Thread AudioDeviceToolThread;
    
    public static void startCheckingThread() {
        AudioDeviceToolThread = Thread.ofVirtual().start(AudioDeviceToolChecker::startToolChecking);
    }
    
    private static void startToolChecking() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            handleWindows();
        } else if (os.contains("mac")) {
            handleMacOS();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("linux")) {
            handleLinux();
        }
        
        try {
            AudioDeviceToolThread.interrupt();
        } catch (RuntimeException e) {
            printErrorMessage(e);
        }
    }
    
    private static void handleWindows() {
        if (isModuleNotInstalled()) {
            installWindowsModule();
            if (isModuleNotInstalled()) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Installation of 'AudioDeviceCmdlets' tool failed.\nPlease run the program as Administrator.",
                        "Support Center",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                isAudioDeviceCmdletsInstalled = true;
                saveGeneralConfigurations();
            }
        } else {
            isAudioDeviceCmdletsInstalled = true;
            saveGeneralConfigurations();
        }
    }
    
    private static boolean isModuleNotInstalled() {
        String command = "powershell -ExecutionPolicy Bypass -NoProfile -Command \"Try { Import-Module AudioDeviceCmdlets -ErrorAction Stop; Get-Module -ListAvailable | Where-Object { $_.Name -eq 'AudioDeviceCmdlets' } } Catch { Write-Host 'UNKNOWN_OUTPUT_DEVICE' }\"";
        String output = executeCommand(command).trim();
        return output.contains("UNKNOWN_OUTPUT_DEVICE");
    }
    
    private static void installWindowsModule() {
        String command = "powershell -Command \"Install-Module -Name AudioDeviceCmdlets -Scope CurrentUser -Force -ErrorAction Stop\"";
        executeCommand(command);
    }
    
    private static void handleMacOS() {
        String output = executeCommand("system_profiler SPAudioDataType");
        if (!output.contains("Output Source")) {
            String checkTool = executeCommand("which SwitchAudioSource");
            if (checkTool.trim().isEmpty()) {
                String brewCheck = executeCommand("which brew");
                if (brewCheck.trim().isEmpty()) {
                    executeCommand(
                            "/bin/bash -c \"$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
                    );
                }
                executeCommand("brew install switchaudio-osx");
            }
        }
    }
    
    private static void handleLinux() {
        if (executeCommand("which pactl").contains("pactl")) {
            executeCommand("pactl list short sinks");
        } else {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "PulseAudio is not installed. Attempting to install it now.\nPlease enter your password if prompted.",
                    "Support Center",
                    JOptionPane.INFORMATION_MESSAGE
            );
            executeCommand("sudo apt install pulseaudio-utils -y");
        }
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: \n" + line);
            }
            
            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                printErrorMessage(new Exception("Command execution timed out: " + command));
            }
        } catch (IOException | InterruptedException e) {
            printErrorMessage(e);
        }
        return output.toString();
    }
}