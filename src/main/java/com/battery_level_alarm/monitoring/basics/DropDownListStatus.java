package com.battery_level_alarm.monitoring.basics;

public class DropDownListStatus {
    private static boolean isFirstEnabled;
    private static boolean isSecondEnabled;
    private static boolean isThirdEnabled;

    public static boolean isFirstEnabled() {
        return isFirstEnabled;
    }
    public static void setFirstEnabled(boolean isFirstEnabled) {
        DropDownListStatus.isFirstEnabled = isFirstEnabled;
    }

    public static boolean isSecondEnabled() {
        return isSecondEnabled;
    }
    public static void setSecondEnabled(boolean isSecondEnabled) {
        DropDownListStatus.isSecondEnabled = isSecondEnabled;
    }

    public static boolean isThirdEnabled() {
        return isThirdEnabled;
    }
    public static void setThirdEnabled(boolean isThirdEnabled) {
        DropDownListStatus.isThirdEnabled = isThirdEnabled;
    }
}