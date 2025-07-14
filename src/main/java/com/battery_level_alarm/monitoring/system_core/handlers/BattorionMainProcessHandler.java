package com.battery_level_alarm.monitoring.system_core.handlers;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker.AudioDeviceThread;
import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.isCheckForUpdatesAutomatically;
import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.isDownloadUpdatesAutomatically;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.checkForVersionUpdates;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailableFlag;
import static com.battery_level_alarm.monitoring.registration_manager.RemoteVersionChecker.thereIsNewVersion;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.alternativeStage;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.scheduler;
import static com.battery_level_alarm.monitoring.graphics.executor.LocalScheduledExecutorService.changeTimer;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocExternalFilesLoader.loadMarkdownAsHtml;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopicsBuilder.RELEASE_NOTES_MD;
import static com.battery_level_alarm.monitoring.system_automation.WakeUpPC.wakeUpThreadInterruptRequest;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.START_BATTORION_WITH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.WAKE_UP_PC_AUTO;
import static com.battery_level_alarm.monitoring.system_core.InitializeMainPanels.initializeDashboard;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.setupDashboardPanel;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.releaseManager;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.cleanupAudioSettingsAfterAlert;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.isProcessesApplied;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientPreview.mainPreviewFrame;

import com.battery_level_alarm.monitoring.registration_manager.AudioDeviceToolChecker;
import com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader;
import com.battery_level_alarm.monitoring.flow_chat.CallStepsFlow;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BattorionMainProcessHandler {
	private static final ScheduledExecutorService VERSION_UPDATE_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
	public static boolean isWaitingForInternet = true;
	
	public static void prepareStartup() {
		Thread.ofVirtual().start(() -> {
			try {
				AudioDeviceToolChecker.startCheckingThread();
				EssentialToolsDownloader.Downloader((_, _) -> {}, true);
				isInternetAvailableFlag = isInternetAvailable();
				
				if(isCheckForUpdatesAutomatically()) {
					if (isInternetAvailableFlag) {
						mainUpdateProcess();
					} else {
						logger.info("[INFO]: Internet connection unavailable.");
						isWaitingForInternet = true;
						VERSION_UPDATE_EXECUTOR.schedule(() -> {
							if (isInternetAvailable()) {
								VERSION_UPDATE_EXECUTOR.shutdown();
								mainUpdateProcess();
							}
						}, 30, TimeUnit.MINUTES);
					}
				} if(isApplicationMode) {
					loadMarkdownAsHtml(RELEASE_NOTES_MD);
				}
			} catch (Exception e) {
				printErrorMessage(e);
				Thread.currentThread().interrupt();
			}
		});
	}
	
	private static void mainUpdateProcess() {
		checkForVersionUpdates();
		isWaitingForInternet = false;
		if(isDownloadUpdatesAutomatically() && isInternetAvailable()) {
			releaseManager();
		} else {
			updateUIBasedOnVersion();
		}
	}
	
	public static void updateUIBasedOnVersion() {
		if(!isApplicationMode && thereIsNewVersion) {
			Platform.runLater(BattorionTrayUI::rebuildTabPanels);
		} else if(thereIsNewVersion) {
			initializeDashboard(true);
			setupDashboardPanel();
		}
	}
	
	public static void cleanup(boolean isToSwitching) {
		try {
			Thread.ofVirtual().start(() -> {
				if(isToSwitching) {
					prefs.put(START_BATTORION_WITH, String.valueOf(BattorionTrayUI.DepartureModes.START_WITH_TRAY));
					isApplicationMode = false;
				}
				
				mainMonitorInterruptRequest();
				AudioDeviceThread.interrupt();
				Platform.runLater(() -> {
					alternativeStage.hide();
					changeTimer.stop();
					scheduler.shutdown();
				});
				
				if(!Boolean.parseBoolean(prefs.get(WAKE_UP_PC_AUTO, String.valueOf(false)))) {
					wakeUpThreadInterruptRequest();
				} if(mainPreviewFrame != null) {
					mainPreviewFrame.dispose();
				} if(isProcessesApplied) {
					cleanupAudioSettingsAfterAlert();
				} try {
					if(CallStepsFlow.scheduledTask != null) {
						CallStepsFlow.scheduledTask.cancel(false);
					}
					CallStepsFlow.scheduler.shutdown();
				} catch (Exception e) {
					printErrorMessage(e);
				}
			});
		} catch (Exception exception) {
			printErrorMessage(exception);
		}
	}
}