package com.battery_level_alarm.monitoring.skeleton_constraints;
import com.battery_level_alarm.monitoring.visual_effects.DisplayMessages;
import static com.battery_level_alarm.monitoring.system_core.Battorion.departure;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;

import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.Optional;

public class SingletonObject {
    private static final String LOCK_FILE_NAME = "bat_locker.lock";
    public static final String MAIN_FOLDER_PATH =
            System.getProperty("user.home") + MAIN_FOLDER_NAME;
    
    private static FileLock lock = null;
    private static FileChannel channel = null;
    private static File lockFile;
    
    public static void singletonMethod(String modeToUse, String[] args) {
        File lockDir = new File(MAIN_FOLDER_PATH);
        if (!lockDir.exists() && !lockDir.mkdir()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to create lock directory: " + lockDir.getAbsolutePath(),
                    "Directory Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
        
        lockFile = new File(lockDir, LOCK_FILE_NAME);
        try {
            FileOutputStream fos = new FileOutputStream(lockFile, false);
            channel = fos.getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                showIfAlreadyRunning();
                fos.close();
                return;
            }
            
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            String startTime = new Date().toString();
            String info = "User=" + MAIN_FOLDER_PATH + "\nPID=" + pid + "\nStarted=" + startTime + "\n";
            fos.write(info.getBytes());
            fos.flush();
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (lock != null) lock.release();
                    if (channel != null) channel.close();
                    if (lockFile.exists()) lockFile.delete();
                } catch (Exception e) {
                    DisplayMessages.printErrorMessage(e);
                }
            }));
            
            departure(modeToUse, args);
        } catch (IOException e) {
            logger.severe("Unexpected error in SingletonObject: " + e.getMessage());
            showIfAlreadyRunning();
        }
    }
    
    private static void showIfAlreadyRunning() {
        String pidFromFile = null;
        StringBuilder details = new StringBuilder();
        if (!lockFile.exists()) {
            showAndExit("Another instance may already be running.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(lockFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                details.append(line).append("\n");
                if (line.startsWith("PID=")) {
                    pidFromFile = line.substring(4).trim();
                }
            }
        } catch (IOException ioEx) {
            logger.warning("Unable to read lock file: " + ioEx.getMessage());
            details.append("Could not read lock file.\n");
        }
        
        boolean processAlive = false;
        if (pidFromFile != null) {
            try {
                long pid = Long.parseLong(pidFromFile);
                Optional<ProcessHandle> process = ProcessHandle.of(pid);
                processAlive = process.isPresent() && process.get().isAlive();
            } catch (NumberFormatException e) {
                logger.warning("Invalid PID in lock file: " + e.getMessage());
            }
        }
        
        if (processAlive) {
            showAndExit("Another instance is already running.\nPlease close it before starting a new one.\n\nDetails:\n" + details);
        } else {
            if (lockFile.exists() && !lockFile.delete()) {
                showAndExit("The application cannot start because of a lock file issue,\nwhich may indicate another instance is already running.\nPlease restart your computer or delete the lock file manually.");
            } else {
                showAndExit("It looks like the previous session didn’t close properly.\nPlease try again.");
            }
        }
    }
    
    private static void showAndExit(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Singleton Instance Error",
                JOptionPane.ERROR_MESSAGE
        );
        Runtime.getRuntime().halt(0);
    }
}