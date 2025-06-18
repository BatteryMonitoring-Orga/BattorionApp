package com.battery_level_alarm.monitoring.skeleton_constraints;
import com.battery_level_alarm.monitoring.visual_effects.DisplayMessages;
import static com.battery_level_alarm.monitoring.system_core.Battorion.departure;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;

import javax.swing.*;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Optional;

public class SingletonObject {
    private static final String LOCK_FILE_NAME = "bat_locker.lock";
    public static final String MAIN_FOLDER_PATH =
            System.getProperty("user.home") + MAIN_FOLDER_NAME;
    
    private static FileLock lock = null;
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
        
        try (RandomAccessFile raf = new RandomAccessFile(lockFile, "rw");
             FileChannel fileChannel = raf.getChannel()) {
            
            lock = fileChannel.tryLock();
            
            if (lock == null) {
                String pidFromFile = null;
                StringBuilder details = new StringBuilder();
                
                try (BufferedReader reader = new BufferedReader(new FileReader(lockFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        details.append(line).append("\n");
                        if (line.startsWith("PID=")) {
                            pidFromFile = line.substring(4).trim();
                        }
                    }
                } catch (IOException ioEx) {
                    logger.severe("[EXCEPTION]: " + ioEx.getMessage());
                    details.append("Unable to read details: ").append(ioEx.getMessage()).append("\n");
                }
                
                boolean processAlive = false;
                if (pidFromFile != null) {
                    try {
                        long pid = Long.parseLong(pidFromFile);
                        Optional<ProcessHandle> process = ProcessHandle.of(pid);
                        processAlive = process.isPresent() && process.get().isAlive();
                    } catch (NumberFormatException e) {
                        logger.severe("Invalid PID in lock file: " + e.getMessage());
                    }
                }
                
                if (processAlive) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Another instance is already running.\n\nDetails:\n" + details,
                            "System Is Running",
                            JOptionPane.ERROR_MESSAGE
                    );
                    System.exit(0);
                } else {
                    if (!lockFile.delete()) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Old lock file exists and can't be deleted.",
                                "Lock Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        System.exit(0);
                    }
                }
            } else {
                String user = System.getProperty("user.name") + MAIN_FOLDER_NAME;
                String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
                String startTime = new Date().toString();
                String info = "User=" + user + "\nPID=" + pid + "\nStarted=" + startTime + "\n";
                raf.setLength(0);
                raf.writeBytes(info);
                
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        if (lock != null) lock.release();
	                    fileChannel.close();
                        if (lockFile.exists() && !lockFile.delete()) {
                            DisplayMessages.printErrorMessage(new IOException("Failed to delete lock file"));
                        }
                    } catch (Exception e) {
                        DisplayMessages.printErrorMessage(e);
                    }
                }));
                
                departure(modeToUse, args);
            }
        } catch (IOException e) {
            logger.severe("Unexpected error in SingletonObject: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Unexpected Error: " + e.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}