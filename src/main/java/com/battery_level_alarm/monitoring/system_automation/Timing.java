package com.battery_level_alarm.monitoring.system_automation;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.CoreStaticData.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Timing {
    private static final Map<String, ArrayList<Double>> history = new HashMap<>();
    private static int ChargingSharpDifference;
    private static int DischargingSharpDifference;
    private static long HowLongBatteryNeedToFull;
    private static long HowLongBatteryNeedToDump;

    private static long ChargingStartFrom;
    private static long DisChargingStartFrom;
    private static int ChargingStartAtLevel;
    private static int DisChargingStartAtLevel;

    public static void configurationHistoryMap(){
        history.put(isIn_ChargingMode, new ArrayList<>());
        history.put(isIn_DisChargingMode, new ArrayList<>());
    }

    public static Map<String, ArrayList<Double>> getHistoryMap(){
        return history;
    }

    public static void putNewItemInTheHistoryMap(String batteryMode, double value){
        ArrayList<Double> values = history.get(batteryMode);
        values.add(value);
        history.put(batteryMode, values);
    }

    public static void clearObjectFromTheHistoryMap(String batteryMode){
        history.put(batteryMode, new ArrayList<>());
    }

    public static int getChargingSharpDifference(){
        return ChargingSharpDifference;
    }

    public static int getDischargingSharpDifference(){
        return DischargingSharpDifference;
    }

    public static long getHowLongBatteryNeedToFull(){
        return HowLongBatteryNeedToFull;
    }

    public static long getHowLongBatteryNeedToDump(){
        return HowLongBatteryNeedToDump;
    }

    public static int getChargingStartAtLevel(){
        return ChargingStartAtLevel;
    }

    public static int getDisChargingStartAtLevel(){
        return DisChargingStartAtLevel;
    }

    public static void calcSharpDifference(String mode) {
        ArrayList<Double> Items = history.get(mode);
        if (Items == null || Items.size() < 2) {
            return;
        }

        int maxDifference = 0;
        for (int i = 0; i < Items.size() - 1; i++) {
            int difference = (int) Math.abs(Items.get(i + 1) - Items.get(i));
            if (difference > maxDifference) {
                maxDifference = difference;
            }
        }
        setSharpDifference(mode, maxDifference);
    }

    private static void setSharpDifference(String mode, int maxDifference){
        if(mode.equals(isIn_ChargingMode)){
            ChargingSharpDifference = maxDifference;
        } else if(mode.equals(isIn_DisChargingMode)){
            DischargingSharpDifference = maxDifference;
        }
    }

    public static void howLongBatteryNeedToFullOrDump(String mode, String state) {
        long time = System.currentTimeMillis();
        if (state.equals("Start")) {
            setStartedValue(mode, time);
        } else if (state.equals("End")) {
            long period = calculatePeriodTime(mode, time);
            setPeriodTime(mode, period);
        }
    }

    private static void setStartedValue(String mode, long time) {
        if (mode.equals(isIn_ChargingMode)) {
            ChargingStartFrom = time;
            ChargingStartAtLevel = batteryLevel;
        } else if (mode.equals(isIn_DisChargingMode)) {
            DisChargingStartFrom = time;
            DisChargingStartAtLevel = batteryLevel;
        }
    }

    private static long calculatePeriodTime(String mode, long time) {
        if (mode.equals(isIn_ChargingMode)) {
            return (time - ChargingStartFrom);
        } else if (mode.equals(isIn_DisChargingMode)) {
            return (time - DisChargingStartFrom);
        } else {
            return 0L;
        }
    }

    private static void setPeriodTime(String mode, long period) {
        if (mode.equals(isIn_ChargingMode)) {
            HowLongBatteryNeedToFull = period;
        } else if (mode.equals(isIn_DisChargingMode)) {
            HowLongBatteryNeedToDump = period;
        }
    }
}