package com.battery_level_alarm.monitoring.basics;

public class UserChoices {
    private static String soundPath;
    private static int volumeLevel;
    private static int minimumLevel;
    private static int maximumLevel;
    private static int repeatIntervalGeneralMonitor;
    private static int repeatIntervalBeforeRiskPhase;
    private static int soundDuration;
    private static boolean isAutoMonitoring;
    private static boolean enablePrimarySound;
    private static boolean enableSecondarySound;
    private static boolean enableText;
    
    public static String getSoundPath() {
        return soundPath;
    }
    public static void setSoundPath(String soundPath) {
        UserChoices.soundPath = soundPath;
    }
    
    public static int getVolumeLevel() {
        return volumeLevel;
    }
    public static void setVolumeLevel(int volumeLevel) {
        UserChoices.volumeLevel = volumeLevel;
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
    
    public static int getRepeatIntervalForGeneralMonitor() {
        return repeatIntervalGeneralMonitor;
    }
    public static void setRepeatIntervalForGeneralMonitor(int repeatIntervalGeneralMonitor) {
        UserChoices.repeatIntervalGeneralMonitor = repeatIntervalGeneralMonitor;
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
    
    public static boolean isEnableText() {
        return enableText;
    }
    public static void setEnableText(boolean enableText) {
        UserChoices.enableText = enableText;
    }
}