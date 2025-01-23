package com.battery_level_alarm.monitoring.core;
import static com.battery_level_alarm.monitoring.core.BatteryLevelAlarm.*;
import static com.battery_level_alarm.monitoring.cybernate.Timing.*;

import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.effects.AlertSound;

import java.awt.*;

public class BatteryMode {
    public static void exchangeBatteryMode(){
        if(isCharging) {
            batteryBar.setForeground(Color.CYAN);
            status = isIn_ChargingMode;
            statusLabel.setText("Battery Status: " + status + " ");
        } else {
            batteryBar.setForeground(Color.GREEN);
            status = isIn_DisChargingMode;
            statusLabel.setText("Battery Status: " + status + " ");
        }
    }

    public static void track(){
        if(!status.equals(lastMode)){
            exchangeMode(lastMode);
            lastMode = status;
            refreshBatteryStatisticsPanel();
        }
    }

    private static void exchangeMode(String mode){
        isExchanged = true;
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
}