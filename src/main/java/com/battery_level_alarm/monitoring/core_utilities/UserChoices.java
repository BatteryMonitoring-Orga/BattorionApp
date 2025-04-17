package com.battery_level_alarm.monitoring.core_utilities;

public class UserChoices {
    private static String primarySoundPath;
    private static String secondarySoundPath;
    private static int minimumLevel;
    private static int maximumLevel;
    private static int alertBeforeRiskPhaseBy;
    private static int repeatIntervalBeforeRiskPhase;
    private static int soundDuration;
    private static boolean isAutoMonitoring;
    private static boolean enablePrimarySound;
    private static boolean enableSecondarySound;
    private static boolean enableChargeAndDischargeSound;
    private static boolean enableText;
    
    public static String getPrimarySoundPath() {
        return primarySoundPath;
    }
    public static void setPrimarySoundPath(String primarySoundPath) {
        UserChoices.primarySoundPath = primarySoundPath;
    }
    
    public static String getSecondarySoundPath() {
        return secondarySoundPath;
    }
    public static void setSecondarySoundPath(String secondarySoundPath) {
        UserChoices.secondarySoundPath = secondarySoundPath;
    }
    
    public static int getMinimumLevel() {
        return minimumLevel;
    }
    public static void setMinimumLevel(int minimumLevel) {
        UserChoices.minimumLevel = minimumLevel;
    }
    
    public static int getMaximumLevel() {
        return maximumLevel;
    }
    public static void setMaximumLevel(int maximumLevel) {
        UserChoices.maximumLevel = maximumLevel;
    }

    public static int getAlertBeforeRiskPhaseBy() {
        return alertBeforeRiskPhaseBy;
    }
    public static void setAlertBeforeRiskPhaseBy(int alertBeforeRiskPhaseBy) {
        UserChoices.alertBeforeRiskPhaseBy = alertBeforeRiskPhaseBy;
    }

    public static int getRepeatIntervalBeforeRiskPhase() {
        return repeatIntervalBeforeRiskPhase;
    }
    public static void setRepeatIntervalBeforeRiskPhase(int repeatIntervalBeforeRiskPhase) {
        UserChoices.repeatIntervalBeforeRiskPhase = repeatIntervalBeforeRiskPhase;
    }
    
    public static int getSoundDuration() {
        return soundDuration;
    }
    public static void setSoundDuration(int soundDuration) {
        UserChoices.soundDuration = soundDuration;
    }
    
    public static boolean isAutoMonitoring() {
        return isAutoMonitoring;
    }
    public static void setAutoMonitoring(boolean isAutoMonitoring) {
        UserChoices.isAutoMonitoring = isAutoMonitoring;
    }
    
    public static boolean isEnablePrimarySound() {
        return enablePrimarySound;
    }
    public static void setEnablePrimarySound(boolean enableSound) {
        UserChoices.enablePrimarySound = enableSound;
    }
    
    public static boolean isEnableSecondarySound() {
        return enableSecondarySound;
    }
    public static void setEnableSecondarySound(boolean enableSound) {
        UserChoices.enableSecondarySound = enableSound;
    }

    public static boolean isEnableChargeAndDischargeSound() {
        return enableChargeAndDischargeSound;
    }
    public static void setEnableChargeAndDischargeSound(boolean enableSound) {
        UserChoices.enableChargeAndDischargeSound = enableSound;
    }

    public static boolean isEnableText() {
        return enableText;
    }
    public static void setEnableText(boolean enableText) {
        UserChoices.enableText = enableText;
    }
}