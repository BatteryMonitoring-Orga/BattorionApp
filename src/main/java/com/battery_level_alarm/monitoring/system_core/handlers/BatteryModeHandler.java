package com.battery_level_alarm.monitoring.system_core.handlers;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.ChargingStatus.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_automation.Timing.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;

import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.system_core.helpers.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.visual_effects.AlertSound;
import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import java.awt.*;

public class BatteryModeHandler {
    public static void exchangeBatteryMode(Color batteryColor){
        if(isCharging) {
            restoreBrightnessLevel();
            batteryBar.setForeground(Color.CYAN);
            status = isIn_ChargingMode;
            statusLabel.setText(ONE_SPACE + "Battery Status: " + status + " ");
        } else {
            restoreBrightnessLevel();
            batteryBar.setForeground(batteryColor);
            status = isIn_DisChargingMode;
            statusLabel.setText(ONE_SPACE + "Battery Status: " + status + " ");
        }
    }

    private static void restoreBrightnessLevel(){
        if(isWasInCriticalPhase && ComputerSettings.isAutomaticallyReduceAndRestoreBL()){
            Brightness.BrightnessProcess(Brightness.getCurrentBrightness(), false);
            isWasInCriticalPhase = false;
        } else if(isWasInCriticalPhase && ComputerSettings.isAutomaticallyRestoreBrightnessLevel()){
            int level = ComputerSettings.isAutomaticallyReduceBrightnessLevel()?
                    Brightness.getCurrentBrightness() : Brightness.getDefaultBrightness();
            Brightness.BrightnessProcess(level, false);
            isWasInCriticalPhase = false;
        }
    }

    private static void exchangeMode(String mode){
        calcSharpDifference(mode);

        if(lastMode.contains("Charging")){
            AlertSound.useDefaultDuration = true;
            if(lastMode.contains("Not")){
                if(UserChoices.isEnableChargeAndDischargeSound()){
                    AlertSound.playSound(CHARGING_SOUND_PATH);
                }
                calcSharpDifference(lastMode);
            } else{
                if(UserChoices.isEnableChargeAndDischargeSound()){
                    AlertSound.playSound(DISCHARGING_SOUND_PATH);
                }
                calcSharpDifference(lastMode);
            }
            doTheFollowingOperations();
        }
    }

    private static void doTheFollowingOperations(){
        howLongBatteryNeedToFullOrDump(status, "Start");
        if(!operationIsEnd){
            howLongBatteryNeedToFullOrDump(lastMode, "End");
        }
        clearObjectFromTheHistoryMap(status);
        AlertSound.useDefaultDuration = false;
    }

    public static void track(){
        if(!status.equals(lastMode)){
            exchangeMode(lastMode);
            lastMode = status;
            BattorionPanelHelper.refreshBatteryStatisticsPanel();
        }
    }
}