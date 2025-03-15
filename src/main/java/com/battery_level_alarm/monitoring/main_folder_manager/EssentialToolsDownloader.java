package com.battery_level_alarm.monitoring.main_folder_manager;
import static com.battery_level_alarm.monitoring.effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EssentialToolsDownloader {
    private static final String[][] REPOSITORIES = {
            {"NirCMD-main", "https://github.com/CMD-Helper-Battorion/NirCMD/archive/refs/heads/main.zip"},
            {"svcl-x64-main", "https://github.com/CMD-Helper-Battorion/svcl-x64/archive/refs/heads/main.zip"},
            {"comprehensive_guide-main", "https://github.com/CMD-Helper-Battorion/comprehensive_guide/archive/refs/heads/main.zip"}
    };

    public static void Downloader(BiConsumer<Long, Long> progressCallback, boolean isForceDownload) {
        if (!isInternetAvailable()) {
            JOptionPane.showMessageDialog(
                    null,
                    "❌ No internet connection! \n" +
                            "The application requires an internet connection to download essential tools.",
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean isFileExist = false;
        boolean isAllFilesExist = true;
        for (String[] repo : REPOSITORIES) {
            String repoName = repo[0];
            String repoZipUrl = repo[1];
            String zipPath = MAIN_FOLDER_PATH + File.separator + repoName + ".zip";
            String repoPath = MAIN_FOLDER_PATH + File.separator + repoName;
            isAllFilesExist = false;

            if (new File(repoPath).exists()) {
                isAllFilesExist = true;
                isFileExist = true;
                continue;
            }

            try {
                downloadFile(repoZipUrl, zipPath, progressCallback);
                unzip(zipPath);
                Files.deleteIfExists(Paths.get(zipPath));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "❌ Error downloading essential tool: " + repoName + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if(!isForceDownload){
            if(isFileExist && !isAllFilesExist){
                JOptionPane.showMessageDialog(null, "✔ Some files already exist. Not all tools were downloaded.", "System", JOptionPane.INFORMATION_MESSAGE);
            } else if(isAllFilesExist){
                JOptionPane.showMessageDialog(null, "✔ All essential tools are already installed.", "System", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private static boolean isInternetAvailable() {
        try {
            URI uri = new URI("http://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection.getResponseCode() == 200;
        } catch (IOException | URISyntaxException e) {
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

    private static void unzip(String zipFilePath){
        File dir = new File(com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH);
        if (!dir.exists()) dir.mkdirs();

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                File filePath = new File(com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.MAIN_FOLDER_PATH, entry.getName());
                if (entry.isDirectory()) {
                    filePath.mkdirs();
                } else {
                    filePath.getParentFile().mkdirs();
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = zipIn.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (IOException e) {
            printErrorMessage(e);
        }
    }
}