package com.battery_level_alarm.monitoring.basics;

public class DropDownListStatus {
    private static boolean isCS_First_DDL_Enabled;
    private static boolean isCS_Second_DDL_Enabled;
    private static boolean isCS_Third_DDL_Enabled;
    private static boolean isCS_Fourth_DDL_Enabled;

    private static boolean isAS_First_DDL_Enabled;
    private static boolean isAS_Second_DDL_Enabled;
    private static boolean isAS_Third_DDL_Enabled;
    private static boolean isAS_Fourth_DDL_Enabled;

    public static boolean isCS_FirstDropDownListEnabled() {
        return isCS_First_DDL_Enabled;
    }
    public static void setCS_FirstDropDownListEnabled(boolean isFirstEnabled) {
        DropDownListStatus.isCS_First_DDL_Enabled = isFirstEnabled;
    }

    public static boolean isCS_SecondDropDownListEnabled() {
        return isCS_Second_DDL_Enabled;
    }
    public static void setCS_SecondDropDownListEnabled(boolean isSecondEnabled) {
        DropDownListStatus.isCS_Second_DDL_Enabled = isSecondEnabled;
    }

    public static boolean isCS_ThirdDropDownListEnabled() {
        return isCS_Third_DDL_Enabled;
    }
    public static void setCS_ThirdDropDownListEnabled(boolean isThirdEnabled) {
        DropDownListStatus.isCS_Third_DDL_Enabled = isThirdEnabled;
    }

    public static boolean isCS_FourthDropDownListEnabled() {
        return isCS_Fourth_DDL_Enabled;
    }
    public static void setCS_FourthDropDownListEnabled(boolean isFourthEnabled) {
        DropDownListStatus.isCS_Fourth_DDL_Enabled = isFourthEnabled;
    }

    public static boolean isAppSettingsFirstDropDownListEnabled() {
        return isAS_First_DDL_Enabled;
    }
    public static void setAppSettingsFirstDropDownListEnabled(boolean isFirstEnabled) {
        DropDownListStatus.isAS_First_DDL_Enabled = isFirstEnabled;
    }

    public static boolean isAppSettingsSecondDropDownListEnabled() {
        return isAS_Second_DDL_Enabled;
    }
    public static void setAppSettingsSecondDropDownListEnabled(boolean isSecondEnabled) {
        DropDownListStatus.isAS_Second_DDL_Enabled = isSecondEnabled;
    }

    public static boolean isAppSettingsThirdDropDownListEnabled() {
        return isAS_Third_DDL_Enabled;
    }
    public static void setAppSettingsThirdDropDownListEnabled(boolean isThirdEnabled) {
        DropDownListStatus.isAS_Third_DDL_Enabled = isThirdEnabled;
    }

    public static boolean isAppSettingsFourthDropDownListEnabled() {
        return isAS_Fourth_DDL_Enabled;
    }
    public static void setAppSettingsFourthDropDownListEnabled(boolean isFourthEnabled) {
        DropDownListStatus.isAS_Fourth_DDL_Enabled = isFourthEnabled;
    }
}