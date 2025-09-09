package com.battery_level_alarm.monitoring.system_core.handlers;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ChargingStatus.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_automation.Timing.*;
import static com.battery_level_alarm.monitoring.system_core.top_assist.TopAssistPanel.isSilentMode;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;
import static com.battery_level_alarm.monitoring.notifications.alerts.ChargerIcons.showCircularImage;

import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.notifications.alerts.AlertSound;
import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import com.battery_level_alarm.monitoring.notifications.alerts.ChargerIcons;
import javafx.application.Platform;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BatteryModeHandler {
    public static void exchangeBatteryMode(Color batteryColor) {
        if(isCharging) {
            restoreBrightnessLevel();
            batteryBar.setForeground(Color.CYAN);
            status = IS_IN_CHARGING_MODE;
            statusLabel.setText(ONE_SPACE + "Battery Status: (" + batteryLevel + "%), " + status + " ");
        } else {
            restoreBrightnessLevel();
            batteryBar.setForeground(batteryColor);
            status = IS_IN_DIS_CHARGING_MODE;
            statusLabel.setText(ONE_SPACE + "Battery Status: (" + batteryLevel + "%), " + status + " ");
        }
    }

    private static void restoreBrightnessLevel() {
        if(isWasInCriticalPhase && ComputerSettings.isAutomaticallyReduceAndRestoreBL()) {
            Brightness.BrightnessProcess(Brightness.getCurrentBrightness(), false);
            isWasInCriticalPhase = false;
        } else if(isWasInCriticalPhase && ComputerSettings.isAutomaticallyRestoreBrightnessLevel()) {
            int level = ComputerSettings.isAutomaticallyReduceBrightnessLevel()?
                    Brightness.getCurrentBrightness() : Brightness.getDefaultBrightness();
            Brightness.BrightnessProcess(level, false);
            isWasInCriticalPhase = false;
        }
    }
    
    public static void track() {
        if(!status.equals(lastMode) && !isProgramCurrentlyStarted) {
            exchangeMode(lastMode);
            lastMode = status;
            BattorionPanelHelper.refreshBatteryStatisticsPanel();
            Thread.ofVirtual().start(() -> {
                Platform.runLater(() -> showCircularImage(status.equalsIgnoreCase(IS_IN_DIS_CHARGING_MODE)? DIS_CHARGING_MODE_ICON_NAME : CHARGING_MODE_ICON_NAME));
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    Platform.runLater(ChargerIcons::hideIconsStage);
                    scheduler.shutdown();
                }, 2, TimeUnit.SECONDS);
            });
        } else {
            isProgramCurrentlyStarted = false;
        }
    }
    
    private static void exchangeMode(String mode) {
        calcSharpDifference(mode);
        if(lastMode.contains("Charging")) {
            if(!isSilentMode) {
                AlertSound.useDefaultDuration = true;
                if(lastMode.contains("Not") && UserChoices.isEnableChargeAndDischargeSound()){
                    AlertSound.playSound(CHARGING_SOUND_PATH);
                } else if(UserChoices.isEnableChargeAndDischargeSound()) {
                    AlertSound.playSound(DISCHARGING_SOUND_PATH);
                }
            }
            doTheFollowingOperations();
            calcSharpDifference(lastMode);
        }
    }

    private static void doTheFollowingOperations() {
        howLongBatteryNeedToFullOrDump(status, "Start");
        if(!operationIsEnd) {
            howLongBatteryNeedToFullOrDump(lastMode, "End");
        }
        clearObjectFromTheHistoryMap(status);
        AlertSound.useDefaultDuration = false;
    }
}