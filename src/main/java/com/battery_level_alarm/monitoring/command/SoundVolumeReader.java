package com.battery_level_alarm.monitoring.command;
import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.effects.DisplayMessages;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class SoundVolumeReader {
    private static double mainValue = 0.0;
    private static final String SVCL_PATH = MAIN_FOLDER_PATH + "/svcl-x64-main/svcl-x64";

    private static void returnVolumeLevel() {
        Thread thread = new Thread(() -> {
            try {
                String exePath = Paths.get(SVCL_PATH, "svcl.exe").normalize().toAbsolutePath().toString();
                System.out.println(exePath);
                if (!new File(exePath).exists()) {
                    System.out.println("Error: Executable file not found!");
                    return;
                }

                ProcessBuilder pb = new ProcessBuilder(exePath, "/Stdout", "/GetPercent", "DefaultRenderDevice");
                pb.redirectErrorStream(true);
                Process process = pb.start();

                double volume = -1;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    int counter = 0;

                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        line = line.trim();
                        if (line.matches("\\d+(\\.\\d+)?")) {
                            volume = Double.parseDouble(line);
                            if (counter == 0) {
                                mainValue = volume;
                                counter++;
                            }
                        }
                    }
                }
                process.waitFor();

                if (volume == -1) {
                    mainValue = ComputerSettings.getVolumeLevel();
                }
            } catch (IOException | InterruptedException e) {
                DisplayMessages.printErrorMessage(e);
            }
        });
        thread.start();
        try {
            thread.join(); // Wait for the thread to finish
        } catch (InterruptedException e) {
            DisplayMessages.printErrorMessage(e);
        }
    }

    public static double getVolumeLevel(){
        returnVolumeLevel();
        return mainValue;
    }
}