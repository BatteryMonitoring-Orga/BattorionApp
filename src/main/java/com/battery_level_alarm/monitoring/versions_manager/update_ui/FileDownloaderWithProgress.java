package com.battery_level_alarm.monitoring.versions_manager.update_ui;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class FileDownloaderWithProgress {
	private static ProgressBar downloadProgressBar;
	private static boolean isDownloading = false;
	
	public FileDownloaderWithProgress(ProgressBar downloadProgressBar) {
		FileDownloaderWithProgress.downloadProgressBar = downloadProgressBar;
	}
	
	static ProgressBar getDownloadProgressBar() {
		return downloadProgressBar;
	}
	
	static boolean isDownloading() {
		return isDownloading;
	}
	
	public boolean downloadToFile(String fileURL, File saveTo) {
		try {
			isDownloading = true;
			Thread.sleep(200);
			URI url = new URI(fileURL);
			HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
			int totalSize = connection.getContentLength();
			if (totalSize <= 0) {
				logger.warning("⚠️ Couldn't determine file size for: " + fileURL);
			}
			
			try (InputStream input = connection.getInputStream();
			     FileOutputStream output = new FileOutputStream(saveTo)) {
				byte[] buffer = new byte[4096];
				int bytesRead;
				long downloaded = 0;
				
				while ((bytesRead = input.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
					downloaded += bytesRead;
					if (totalSize > 0) {
						final double progress = (double) downloaded / totalSize;
						Platform.runLater(() -> downloadProgressBar.setProgress(progress));
					}
				}
			}
			
			Platform.runLater(() -> downloadProgressBar.setProgress(1.0));
			connection.disconnect();
			isDownloading = false;
			return true;
		} catch (Exception e) {
			printErrorMessage(e);
			Platform.runLater(() -> {
				downloadProgressBar.setStyle("-fx-accent: red;");
				downloadProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
			});
			return false;
		}
	}
}