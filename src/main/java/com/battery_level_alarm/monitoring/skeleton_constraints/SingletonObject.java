package com.battery_level_alarm.monitoring.skeleton_constraints;
import com.battery_level_alarm.monitoring.visual_effects.DisplayMessages;
import static com.battery_level_alarm.monitoring.system_core.CoreStaticData.MAIN_FOLDER_NAME;
import static com.battery_level_alarm.monitoring.system_core.Battorion.build;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class SingletonObject {
    private static final String LOCK_FILE_NAME = "bat_locker.lock";
    public static final String MAIN_FOLDER_PATH =
            System.getProperty("user.home") + MAIN_FOLDER_NAME;

    private static FileLock lock = null;
    private static FileChannel channel = null;
    private static File lockFile;

    public static void singletonMethod() {
        File lockDir = new File(MAIN_FOLDER_PATH);
        if (!lockDir.exists()) {
            lockDir.mkdir();
        }

        lockFile = new File(lockDir, LOCK_FILE_NAME);
        try {
            if (lockFile.exists()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Another instance is already running.",
                        "System Is Running",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            FileOutputStream fos = new FileOutputStream(lockFile);
            channel = fos.getChannel();
            lock = channel.tryLock();

            if (lock == null) {
                JOptionPane.showMessageDialog(
                        null,
                        "Another instance is already running.",
                        "System Is Running",
                        JOptionPane.ERROR_MESSAGE
                );
                fos.close();
                System.exit(0);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (lock != null) {
                        lock.release();
                    }
                    if (channel != null) {
                        channel.close();
                    }
                    if (lockFile.exists()) {
                        lockFile.delete();
                    }
                } catch (Exception e) {
                    DisplayMessages.printErrorMessage(e);
                }
            }));

            build();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error: " + e.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}