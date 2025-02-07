package com.battery_level_alarm.monitoring.basics;

import java.util.ArrayList;
import java.util.List;

public class ComputerSettings {
    private static boolean activateTheAwakeningFeature;
    private static int wakeUpEvery;
    private static boolean enableExchangeToSpeakerAudioOutput;
    private static boolean enableExchangeToAudioOutputUsed;
    private static List<String> audioDevices = new ArrayList<>();
    private static String currentAudioDevice;
    private static int volumeLevel;
    private static String notificationSoundFileName;

    public static boolean isActivateTheAwakeningFeature(){
        return activateTheAwakeningFeature;
    }
    public static void setActivateTheAwakeningFeature(boolean activateTheAwakeningFeature){
        ComputerSettings.activateTheAwakeningFeature = activateTheAwakeningFeature;
    }

    public static int getWakeUpEvery(){
        return wakeUpEvery;
    }
    public static void setWakeUpEvery(int wakeUpEvery) {
        ComputerSettings.wakeUpEvery = wakeUpEvery;
    }

    public static boolean isEnableExchangeToSpeakerAudioOutput(){
        return enableExchangeToSpeakerAudioOutput;
    }
    public static void setEnableExchangeToSpeakerAudioOutput(boolean enableExchangeToSpeakerAudioOutput) {
        ComputerSettings.enableExchangeToSpeakerAudioOutput = enableExchangeToSpeakerAudioOutput;
    }

    public static boolean isEnableExchangeToAudioOutputUsed(){
        return enableExchangeToAudioOutputUsed;
    }
    public static void setEnableExchangeToAudioOutputUsed(boolean enableExchangeToAudioOutputUsed) {
        ComputerSettings.enableExchangeToAudioOutputUsed = enableExchangeToAudioOutputUsed;
    }

    public static List<String> getAudioDevices(){
        return audioDevices;
    }
    public static void setAudioDevices(List<String> audioDevices){
        ComputerSettings.audioDevices = audioDevices;
    }
    public static boolean setItemToAudioList(String device){
        if(!audioDevices.contains(device)){
            audioDevices.add(device);
            return true;
        }
        return false;
    }
    public static String getItemFromAudioList(String device){
        if(audioDevices.contains(device)){
            for(String item : audioDevices){
                if(item.equals(device)) return item;
            }
        }
        return null;
    }
    public static String getItemFromAudioListAtIndex(int index) {
        if (index >= 0 && index < audioDevices.size()) {
            return audioDevices.get(index);
        }
        return null;
    }
    public static boolean removeItemFromAudioList(String device){
        return audioDevices.remove(device);
    }
    public static boolean removeItemFromAudioListAtIndex(int index) {
        if (index >= 0 && index < audioDevices.size()) {
            audioDevices.remove(index);
            return true;
        }
        return false;
    }

    public static String getCurrentAudioDevice(){
        return currentAudioDevice;
    }
    public static void setCurrentAudioDevice(String currentAudioDevice){
        ComputerSettings.currentAudioDevice = currentAudioDevice;
    }

    public static int getVolumeLevel() {
        return volumeLevel;
    }
    public static void setVolumeLevel(int volumeLevel) {
        ComputerSettings.volumeLevel = volumeLevel;
    }

    public static String getNotificationSoundFileName(){
        return notificationSoundFileName;
    }
    public static void setNotificationSoundFileName(String notificationSoundFileName){
        ComputerSettings.notificationSoundFileName = notificationSoundFileName;
    }
}