package com.battery_level_alarm.monitoring.basics;

public class PC_Details {
    private static boolean activateTheAwakeningFeature;
    private static int wakeUpEvery;
    private static boolean enableExchangeToSpeakerAudioOutput;
    private static int volumeLevel;

    public static boolean getActivateTheAwakeningFeature(){
        return activateTheAwakeningFeature;
    }
    public static void setActivateTheAwakeningFeature(boolean activateTheAwakeningFeature){
        PC_Details.activateTheAwakeningFeature = activateTheAwakeningFeature;
    }

    public static int getWakeUpEvery(){
        return wakeUpEvery;
    }
    public static void setWakeUpEvery(int wakeUpEvery) {
        PC_Details.wakeUpEvery = wakeUpEvery;
    }

    public static boolean isEnableExchangeToSpeakerAudioOutput(){
        return enableExchangeToSpeakerAudioOutput;
    }
    public static void setEnableExchangeToSpeakerAudioOutput(boolean enableExchangeToSpeakerAudioOutput) {
        PC_Details.enableExchangeToSpeakerAudioOutput = enableExchangeToSpeakerAudioOutput;
    }

    public static int getVolumeLevel() {
        return volumeLevel;
    }
    public static void setVolumeLevel(int volumeLevel) {
        PC_Details.volumeLevel = volumeLevel;
    }
}