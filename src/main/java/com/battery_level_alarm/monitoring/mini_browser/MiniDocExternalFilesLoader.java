package com.battery_level_alarm.monitoring.mini_browser;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.ensureReleaseNotesExists;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.installCurrentReleaseNotesFile;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopicsBuilder.RELEASE_NOTES_MD;
import static com.battery_level_alarm.monitoring.skeleton_constraints.SingletonObject.CONFIGURATIONS_MAIN_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

public class MiniDocExternalFilesLoader {
	public static String loadMarkdownAsHtml(String filename) {
		try {
			String markdownText;
			if (RELEASE_NOTES_MD.equalsIgnoreCase(filename) && ensureReleaseNotesExists()) {
				markdownText = readMarkdownFile(new File(CONFIGURATIONS_MAIN_FOLDER_PATH, RELEASE_NOTES_MD));
				return markdownText;
			}
			
			String executionDir = System.getProperty("user.dir");
			File file = new File(executionDir, filename);
			if (!file.exists()) {
				if(isInternetAvailable()) {
					installCurrentReleaseNotesFile();
					String path = CONFIGURATIONS_MAIN_FOLDER_PATH + "/" + RELEASE_NOTES_MD;
					markdownText = readMarkdownFile(new File(path));
				} else {
					markdownText = "<h2 style='color:red;'>File not found: " + file.getAbsolutePath() + "</h2>";
				}
				return markdownText;
			}
			return readMarkdownFile(file);
		} catch (Exception e) {
			printErrorMessage(e);
			return "<h2 style='color:red;'>Error loading markdown: " + filename + "<br>" + e.getMessage() + "</h2>";
		}
	}
	
	private static String readMarkdownFile(File file) {
		try {
			String markdown = Files.readString(file.toPath(), StandardCharsets.UTF_8);
			Parser parser = Parser.builder().build();
			HtmlRenderer renderer = HtmlRenderer.builder().build();
			Node document = parser.parse(markdown);
			return renderer.render(document);
		} catch (Exception e) {
			printErrorMessage(e);
		}
		return "<h2 style='color:red;'>File not found: " + file.getAbsolutePath() + "</h2>";
	}
	
	static String loadHtmlFromFile(String filename) {
		try (InputStream is = new java.io.FileInputStream(filename)) {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			return "<h2 style='color:red;'>Error loading: " + filename + "</h2>";
		}
	}
}
