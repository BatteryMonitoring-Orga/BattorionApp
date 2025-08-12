package com.battery_level_alarm.monitoring.registration_manager;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.RoamingConfigClass.ROAMING_CONFIG_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EssentialToolsDownloader {
    public static final String EXPORT_HARDWARE_MONITOR_FILE = "export-hardware-monitor.ps1";
    public static boolean isInternetAvailableFlag;
    private static final String[][] REPOSITORIES = {
            {"NirCMD-main", "https://github.com/CMD-Helper-Battorion/NirCMD/archive/refs/heads/main.zip"},
            {"svcl-x64-main", "https://github.com/CMD-Helper-Battorion/svcl-x64/archive/refs/heads/main.zip"},
            {"SoundVolumeView-main", "https://github.com/CMD-Helper-Battorion/SoundVolumeView/archive/refs/heads/main.zip"},
            {"LibreHardwareMonitor-net472", "https://github.com/LibreHardwareMonitor/LibreHardwareMonitor/releases/download/v0.9.4/LibreHardwareMonitor-net472.zip"},
            {"comprehensive_guide-main", "https://github.com/CMD-Helper-Battorion/comprehensive_guide/archive/refs/heads/main.zip"},
            {EXPORT_HARDWARE_MONITOR_FILE, "https://github.com/CMD-Helper-Battorion/comprehensive_guide/releases/download/EHM-PS1/export-hardware-monitor.ps1"}
    };
    
    public static void Downloader(BiConsumer<Long, Long> progressCallback, boolean isForceDownload) {
        isInternetAvailableFlag = isInternetAvailable();
        if (!isInternetAvailableFlag) return;
        
        boolean isFileExist = false;
        boolean isAllFilesExist = true;
        for (String[] repo : REPOSITORIES) {
            String repoName = repo[0];
            String repoUrl = repo[1];
            String fileName = repoUrl.substring(repoUrl.lastIndexOf('/') + 1);
            boolean isZip = fileName.endsWith(".zip");
            
            if (isZip) {
                String zipPath = ROAMING_CONFIG_PATH + File.separator + fileName;
                String repoPath = ROAMING_CONFIG_PATH + File.separator + repoName;
                if (new File(repoPath).exists()) {
                    isFileExist = true;
                    continue;
                } try {
                    isAllFilesExist = false;
                    downloadFile(repoUrl, zipPath, progressCallback);
                    unzipWithConditionalFlatten(zipPath, repoName);
                    Files.deleteIfExists(Paths.get(zipPath));
                } catch (Exception e) {
                    printErrorMessage(e);
                }
            } else {
                String savePath = ROAMING_CONFIG_PATH + File.separator + fileName;
                if (new File(savePath).exists()) {
                    isFileExist = true;
                    continue;
                } try {
                    downloadFile(repoUrl, savePath, progressCallback);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "❌ Error downloading essential tool: " + repoName + ": " + e.getMessage(), "Support Center", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        if (!isForceDownload) {
            if (isFileExist && !isAllFilesExist) {
                JOptionPane.showMessageDialog(null, "✔ Some files already exist. Not all tools were downloaded.", "Support Center", JOptionPane.INFORMATION_MESSAGE);
            } else if (isAllFilesExist) {
                JOptionPane.showMessageDialog(null, "✔ All essential tools are already installed.", "Support Center", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static boolean isInternetAvailable() {
        try {
            URI uri = new URI("http://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection.getResponseCode() == 200;
        } catch (IOException | URISyntaxException e) {
            printErrorMessage(e);
            return false;
        }
    }

    private static void downloadFile(String fileURL, String savePath, BiConsumer<Long, Long> progressCallback) {
        try {
            URI uri = new URI(fileURL);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            long totalSize = connection.getContentLengthLong();
            long downloadedSize = 0;

            try (InputStream in = connection.getInputStream(); FileOutputStream out = new FileOutputStream(savePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    downloadedSize += bytesRead;
                    progressCallback.accept(downloadedSize, totalSize);
                }
            }
        } catch (IOException | URISyntaxException e) {
            printErrorMessage(e);
        }
    }
    
    private static void unzipWithConditionalFlatten(String zipFilePath, String repoName) throws IOException {
        File targetDir = new File(ROAMING_CONFIG_PATH, repoName);
        if (targetDir.exists()) {
            try (var paths = Files.walk(targetDir.toPath())) {
                paths.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        printErrorMessage(e);
                    }
                });
            } catch (IOException e) {
                printErrorMessage(e);
            }
        } if (!targetDir.mkdirs() && !targetDir.exists()) {
            throw new IOException("Failed to create target directory: " + targetDir);
        }
        
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                File filePath = new File(targetDir, entry.getName());
                if (entry.isDirectory()) {
                    if (!filePath.mkdirs() && !filePath.exists()) {
                        throw new IOException("Failed to create directory: " + filePath);
                    }
                } else {
                    if (!filePath.getParentFile().mkdirs() && !filePath.getParentFile().exists()) {
                        throw new IOException("Failed to create directory: " + filePath.getParentFile());
                    } try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = zipIn.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipIn.closeEntry();
            }
        } catch (IOException e) {
            printErrorMessage(e);
        }
        
        while (true) {
            File[] items = targetDir.listFiles();
            if (items == null || items.length != 1 || !items[0].isDirectory()) break;
            File onlyDir = items[0];
            try (var walk = Files.walk(onlyDir.toPath())) {
                walk.forEach(source -> {
                    try {
                        Path destination = targetDir.toPath().resolve(onlyDir.toPath().relativize(source));
                        if (Files.isDirectory(source)) {
                            if (!Files.exists(destination)) Files.createDirectories(destination);
                        } else {
                            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        printErrorMessage(e);
                    }
                });
            }
            try (var walk = Files.walk(onlyDir.toPath())) {
                walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        printErrorMessage(e);
                    }
                });
            }
        }
    }
}