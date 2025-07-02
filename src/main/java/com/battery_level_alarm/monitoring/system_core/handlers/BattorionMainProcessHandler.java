package com.battery_level_alarm.monitoring.system_core.handlers;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker.AudioDeviceThread;
import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.isCheckForUpdatesAutomatically;
import static com.battery_level_alarm.monitoring.core_utilities.UpdateSettings.isDownloadUpdatesAutomatically;
import static com.battery_level_alarm.monitoring.file_manager.RemoteVersionChecker.checkForVersionUpdates;
import static com.battery_level_alarm.monitoring.file_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.file_manager.EssentialToolsDownloader.isInternetAvailableFlag;
import static com.battery_level_alarm.monitoring.file_manager.RemoteVersionChecker.thereIsNewVersion;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.alternativeStage;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.scheduler;
import static com.battery_level_alarm.monitoring.graphics.executor.LocalScheduledExecutorService.changeTimer;
import static com.battery_level_alarm.monitoring.system_automation.WakeUpPC.wakeUpThreadInterruptRequest;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.setupDashboardPanel;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.releaseManager;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.cleanupAudioSettingsAfterAlert;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.isProcessesApplied;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientPreview.mainPreviewFrame;

import com.battery_level_alarm.monitoring.file_manager.AudioDeviceToolChecker;
import com.battery_level_alarm.monitoring.file_manager.EssentialToolsDownloader;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
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
				}
			} catch (Exception e) {
				logger.severe("[EXCEPTION]: " + e.getMessage());
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
					prefs.put("StartBattorionWith", String.valueOf(BattorionTrayUI.DepartureModes.START_WITH_TRAY));
					isApplicationMode = false;
				}
				
				mainMonitorInterruptRequest();
				AudioDeviceThread.interrupt();
				Platform.runLater(() -> {
					alternativeStage.hide();
					changeTimer.stop();
					scheduler.shutdown();
				});
				
				if(!Boolean.parseBoolean(prefs.get("wackUpPCAuto", String.valueOf(false)))) {
					wakeUpThreadInterruptRequest();
				} if(mainPreviewFrame != null) {
					mainPreviewFrame.dispose();
				} if(isProcessesApplied) {
					cleanupAudioSettingsAfterAlert();
				}
			});
		} catch (Exception exception) {
			printErrorMessage(exception);
		}
	}
	
	public static boolean showTrayModeConfirmationDialog() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("PSM Mode Information");
		alert.setHeaderText("You are now switching to Power Saver Mode (PSM)");
		
		Label contentLabel = getContentLabel();
		contentLabel.setWrapText(true);
		CheckBox confirmBox = new CheckBox("I have read and understood the information");
		VBox dialogContent = new VBox(10, contentLabel, confirmBox);
		dialogContent.setPadding(new Insets(10));
		alert.getDialogPane().setContent(dialogContent);
		
		ButtonType okButtonType = new ButtonType("Switch to PSM", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(okButtonType, cancelButtonType);
		
		Button okButton = (Button) alert.getDialogPane().lookupButton(okButtonType);
		okButton.setDisable(true);
		confirmBox.selectedProperty().addListener((_, _, isChecked) ->
				okButton.setDisable(!isChecked));
		
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == okButtonType) {
			prefs.put("IsFirstTimeRunningInBackground", "false");
			return true;
		} else {
			return false;
		}
	}
	
	private static @NotNull Label getContentLabel() {
		String content = """
		In Tray Mode:
		• Some alerts may not be available.
		• Notification efficiency is reduced compared to normal mode.
		• Value changes and updates may take slightly longer to reflect.
		
		However, this mode is designed to:
		✓ Greatly reduce CPU usage.
		✓ Minimize power consumption.
		✓ Run silently in the background.
		
		Note:
		The program already runs efficiently and consumes minimal resources.
		But with PSM Mode, power and CPU usage become almost zero —
		making it the most energy-saving mode available.
		""";
		return new Label(content);
	}
}