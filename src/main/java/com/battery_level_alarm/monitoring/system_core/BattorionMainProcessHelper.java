package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutputDeviceNameChecker.AudioDeviceThread;
import static com.battery_level_alarm.monitoring.graphics.BatteryLevelGraph.alternativeStage;
import static com.battery_level_alarm.monitoring.graphics.BatteryLevelGraph.scheduler;
import static com.battery_level_alarm.monitoring.graphics.LocalScheduledExecutorService.changeTimer;
import static com.battery_level_alarm.monitoring.system_automation.WakeUpPC.wakeUpThreadInterruptRequest;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.cleanupAudioSettingsAfterAlert;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.isProcessesApplied;
import static com.battery_level_alarm.monitoring.visual_effects.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientPreview.mainPreviewFrame;

import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import javafx.application.Platform;

public class BattorionMainProcessHelper {
	static void cleanup(boolean isToSwitching) {
		try {
			Thread.ofVirtual().start(() -> {
				if(isToSwitching) {
					prefs.put("StartBattorionWith", String.valueOf(BattorionTrayUI.DepartureModes.START_WITH_TRAY));
					isApplicationMode = false;
				}
				
				mainMonitorInterruptRequest();
				wakeUpThreadInterruptRequest();
				AudioDeviceThread.interrupt();
				Platform.runLater(() -> {
					alternativeStage.hide();
					changeTimer.stop();
					scheduler.shutdown();
				});
				
				if(mainPreviewFrame != null) {
					mainPreviewFrame.dispose();
				} if(isProcessesApplied) {
					cleanupAudioSettingsAfterAlert();
				}
			});
		} catch (Exception exception) {
			printErrorMessage(exception);
		}
	}
}
