package com.battery_level_alarm.monitoring.skeleton_constraints;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.battery_level_alarm.monitoring.feedback_system.UserDataFetcher.sendUserIdAndGetFilteredData;
import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.Keys.*;
import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.Keys.STATUS;
import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.STATUS.ACTIVE;
import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.updateUserData;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.BATTORION_WEBSITE;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.LAST_STATUS_VALIDATE;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_DATA_UPLOADED;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UserOnboarding.createAnalyticsMap;

public class ClientServerActions {
	private static final boolean isUnderDevelopment = true;
	
	static void clintToServerAction() {
		Thread.ofVirtual().start(() -> {
			BattorionCoreConstants.UserOnboarding.userSessionTracker();
			if(isInternetAvailable()) {
				String id = prefs.get(BattorionCoreConstants.PrefKeysIdentifiers.USER_IDENTIFIER, null);
				boolean isUploaded = prefs.getBoolean(USER_DATA_UPLOADED, false);
				try {
					if(isUnderDevelopment) {
						return;
					}
					
					if(!isUploaded) {
						Map<String, Object> data = BattorionCoreConstants.UserOnboarding.basicUserDataUpload();
						if(data == null || data.isEmpty()) return;
						updateUserData(id, data);
						prefs.putBoolean(USER_DATA_UPLOADED, true);
					} else {
						checkEveryThreeHours(id);
						Map<String, Object> updates = new HashMap<>();
						updates.put(VERSION, BattorionCoreConstants.AppInfo.APP_VERSION);
						updates.put(ANALYTICS, createAnalyticsMap(false));
						updateUserData(prefs.get(BattorionCoreConstants.PrefKeysIdentifiers.USER_IDENTIFIER, null), updates);
					}
				} catch (Exception e) {
					prefs.putBoolean(USER_DATA_UPLOADED, false);
					logger.severe("[EXCEPTION]: " + e.getMessage());
				}
			} else {
				checkDeviceActivation(new HashMap<>());
			}
		});
	}
	
	private static void checkEveryThreeHours(String id) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		Runnable task = () -> {
			try {
				Map<String, Object> dtatMap = sendUserIdAndGetFilteredData(id, List.of(
						STATUS,
						LICENSE_TYPE,
						LICENSE_EXPIRES_AT
				));
				
				prefs.put(LAST_STATUS_VALIDATE, dtatMap.get(STATUS).toString());
				checkDeviceActivation(dtatMap);
			} catch (Exception e) {
				logger.severe("[EXCEPTION]: " + e.getMessage());
			}
		};
		scheduler.scheduleAtFixedRate(task, 0, 3, TimeUnit.HOURS);
	}
	
	private static void checkDeviceActivation(Map<String, Object> dtatMap) {
		if ((dtatMap != null && !dtatMap.isEmpty() && !dtatMap.get(STATUS).equals(ACTIVE.name()))
				|| !prefs.get(LAST_STATUS_VALIDATE, ACTIVE.name()).equals(ACTIVE.name())) {
			showLicenseErrorMessageWithTimeout();
			Runtime.getRuntime().halt(0);
		}
	}
	
	public static void showLicenseErrorMessageWithTimeout() {
		JTextPane textPane = getTextPane();
		textPane.addHyperlinkListener(e -> {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				try {
					Desktop.getDesktop().browse(BATTORION_WEBSITE);
				} catch (Exception ex) {
					logger.severe("[EXCEPTION]: " + ex.getMessage());
				}
			}
		});
		
		JDialog dialog = new JDialog((Frame) null, "License Verification Failed", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLayout(new BorderLayout());
		dialog.add(textPane, BorderLayout.CENTER);
		dialog.setSize(400, 200);
		dialog.setLocationRelativeTo(null);
		
		new Timer(15000, _ -> {
			if (dialog.isShowing()) {
				dialog.dispose();
			}
		}).start();
		dialog.setVisible(true);
	}
	
	private static @NotNull JTextPane getTextPane() {
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setText(
				"<html><body style='font-family:sans-serif;'>"
						+ "<h3>⚠️ Access Denied</h3>"
						+ "<p>Your license is <b>inactive</b> or <b>unauthorized</b>.</p>"
						+ "<p>Please contact support if you believe this is a mistake.</p>"
						+ "<p>Visit our website: <a href='" + BATTORION_WEBSITE + "'>" + BATTORION_WEBSITE + "</a></p>"
						+ "</body></html>"
		);
		textPane.setEditable(false);
		textPane.setOpaque(false);
		return textPane;
	}
}
