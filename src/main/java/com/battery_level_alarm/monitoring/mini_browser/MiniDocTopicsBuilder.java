package com.battery_level_alarm.monitoring.mini_browser;
import com.battery_level_alarm.monitoring.questionnaires.*;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.TrayAlerts;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.battery_level_alarm.monitoring.mini_browser.MiniDocExternalFilesLoader.loadMarkdownAsHtml;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.*;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.BATTERY_CALIBRATION_AR;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.BATTERY_CALIBRATION_EN;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.COMPREHENSIVE_BATTERY_GUIDE_AR;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.COMPREHENSIVE_BATTERY_GUIDE_EN;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.ENABLE_COMPUTER_NOTIFICATIONS;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.EXTERNAL_DOCS_HEADER;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.GUIDES_HEADER;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.NOTIFICATIONS_HEADER;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.NOTIFICATION_SOUND;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.OUTPUT_AUDIO_DEVICE;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.SETTINGS_QUESTIONNAIRE;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.STATISTICS_QUESTIONNAIRE;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.SYSTEM_SETTINGS_QUESTIONNAIRE;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.SYSTEM_TRAY_NOTIFICATION;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.TRAY_INTEGRATION;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;

public class MiniDocTopicsBuilder {
	public static final Map<String, Supplier<String>> TOPICS = new LinkedHashMap<>();
	public static final String RELEASE_NOTES_MD = "release_notes.md";
	public static final String LATEST_RELEASE_NOTES_MD = "latest_release_notes.md";
	
	private static String currentReleaseNote;
	public static String latestReleaseNote;
	
	private static String getCurrentReleaseMarkdownText() {
		return currentReleaseNote;
	}
	
	public static String getLatestReleaseMarkdownText() {
		return latestReleaseNote;
	}
	
	public static void buildTopicsMap() {
		TOPICS.put(GENERAL_HEADER, () -> "<h3>General</h3>");
		TOPICS.put(ABOUT_APP, StaticQuestionnaire::getAboutDispatchText);
		
		if(getCurrentReleaseMarkdownText() == null || getCurrentReleaseMarkdownText().isEmpty()) {
			currentReleaseNote = loadMarkdownAsHtml(RELEASE_NOTES_MD);
		}
		TOPICS.put(WHATS_NEW, MiniDocTopicsBuilder::getCurrentReleaseMarkdownText);
		
		TOPICS.put(GUIDES_HEADER, () -> "<h3>Guides</h3>");
		TOPICS.put(SETTINGS_QUESTIONNAIRE, SettingsQuestionnaire::getComprehensiveUserGuideHtml);
		TOPICS.put(SYSTEM_SETTINGS_QUESTIONNAIRE, ComputerSettingsQuestionnaire::getComputerSettingsGuide);
		TOPICS.put(STATISTICS_QUESTIONNAIRE, StaticQuestionnaire::getStatisticsContainerExplanation);
		TOPICS.put(GRAPH_QUESTIONNAIRE, GraphSettingsQuestionnaire::getGraphSettingsQuestionnaire);
		TOPICS.put(LIFE_REPORT_QUESTIONNAIRE, LifeReportQuestionnaire::getLifeReportQuestionnaire);
		
		TOPICS.put(NOTIFICATIONS_HEADER, () -> "<h3>Notifications</h3>");
		TOPICS.put(TRAY_INTEGRATION, TrayAlerts::getTrayIntegrationText);
		TOPICS.put(ENABLE_COMPUTER_NOTIFICATIONS, AppendixesQuestionnaire::aboutNotificationsIcon);
		TOPICS.put(SYSTEM_TRAY_NOTIFICATION, AppendixesQuestionnaire::aboutSystemTrayNotification);
		TOPICS.put(NOTIFICATION_SOUND, AppendixesQuestionnaire::aboutPlaySounds);
		TOPICS.put(OUTPUT_AUDIO_DEVICE, AppendixesQuestionnaire::aboutSelectAudioDevice);
		
		String basePath = System.getProperty("user.home") + MAIN_FOLDER_NAME + "/comprehensive_guide-main/";
		TOPICS.put(EXTERNAL_DOCS_HEADER, () -> "<h3>External</h3>");
		TOPICS.put(COMPREHENSIVE_BATTERY_GUIDE_AR, () -> "external::" + basePath + "Comprehensive Guide - Arabic.html");
		TOPICS.put(COMPREHENSIVE_BATTERY_GUIDE_EN, () -> "external::" + basePath + "Comprehensive Guide - English.html");
		TOPICS.put(BATTERY_CALIBRATION_AR, () -> "external::" + basePath + "BatteryCalibrationAndPerformanceAnalysisInArabic.html");
		TOPICS.put(BATTERY_CALIBRATION_EN, () -> "external::" + basePath + "BatteryCalibrationAndPerformanceAnalysisInEnglish.html");
		
		TOPICS.put(RECOMMENDATION_HEADER, () -> "<h3>Recommendations</h3>");
		TOPICS.put(RECOMMENDATION_SOFTWARE, () -> "external::" + HTML_PAGES_FOLDER_PATH + "recommendations.html");
//		TOPICS.put(END_OF_TITLES, () -> "<h3>End of Internal Sections</h3>");
	}
}
