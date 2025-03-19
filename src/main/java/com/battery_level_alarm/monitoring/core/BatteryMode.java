package com.battery_level_alarm.monitoring.core;
import static com.battery_level_alarm.monitoring.core.BattorionMain.*;
import static com.battery_level_alarm.monitoring.cybernate.Timing.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.ONE_SPACE;

import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.effects.AlertSound;
import com.battery_level_alarm.monitoring.effects.Brightness;

import java.awt.*;

public class BatteryMode {
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
                    AlertSound.playSound(ChargingSoundPath);
                }
                calcSharpDifference(lastMode);
            } else{
                if(UserChoices.isEnableChargeAndDischargeSound()){
                    AlertSound.playSound(DischargingSoundPath);
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