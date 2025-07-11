package com.battery_level_alarm.monitoring.core_utilities;
import java.util.ArrayList;
import java.util.List;

public class ComputerSettings {
    private static boolean activateTheAwakeningFeature;
    private static boolean enableSystemNotificationSound;
    private static boolean enableUnmuteVolumeAutomatically;
    private static boolean enableExchangeToSpeakerAudioOutput;
    private static boolean enableExchangeToAudioOutputUsed;
    private static boolean enablingSoundLevelChange;
    private static boolean restoringSoundLevelAfterAlert;

    private static boolean automaticallyReduceAndRestoreBL;
    private static boolean automaticallyReduceBrightnessLevel;
    private static boolean automaticallyRestoreBrightnessLevel;
    private static List<String> audioDevices = new ArrayList<>();
    private static String notificationSoundFileName;
    private static String defaultSpeakerOutputDeviceName;
    private static String currentAudioDevice;
    private static int brightnessControlOption;
    private static int brightnessLevel;
    private static int wakeUpEvery;
    private static int volumeLevel;

    public static boolean isActivateTheAwakeningFeature() {
        return activateTheAwakeningFeature;
    }
    public static void setActivateTheAwakeningFeature(boolean activateTheAwakeningFeature) {
        ComputerSettings.activateTheAwakeningFeature = activateTheAwakeningFeature;
    }

    public static boolean isEnableSystemNotificationSound() {
        return enableSystemNotificationSound;
    }
    public static void setEnableSystemNotificationSound(boolean enableSystemNotificationSound) {
        ComputerSettings.enableSystemNotificationSound = enableSystemNotificationSound;
    }

    public static boolean isEnableExchangeToSpeakerAudioOutput() {
        return enableExchangeToSpeakerAudioOutput;
    }
    public static void setEnableExchangeToSpeakerAudioOutput(boolean enableExchangeToSpeakerAudioOutput) {
        ComputerSettings.enableExchangeToSpeakerAudioOutput = enableExchangeToSpeakerAudioOutput;
    }

    public static boolean isEnableExchangeToAudioOutputUsed() {
        return enableExchangeToAudioOutputUsed;
    }
    public static void setEnableExchangeToAudioOutputUsed(boolean enableExchangeToAudioOutputUsed) {
        ComputerSettings.enableExchangeToAudioOutputUsed = enableExchangeToAudioOutputUsed;
    }

    public static boolean isEnablingSoundLevelChange() {
        return enablingSoundLevelChange;
    }
    public static void setEnablingSoundLevelChange(boolean enablingSoundLevelChange) {
        ComputerSettings.enablingSoundLevelChange = enablingSoundLevelChange;
    }

    public static boolean isRestoringSoundLevelAfterAlert() {
        return restoringSoundLevelAfterAlert;
    }
    public static void setRestoringSoundLevelAfterAlert(boolean restoringSoundLevelAfterAlert) {
        ComputerSettings.restoringSoundLevelAfterAlert = restoringSoundLevelAfterAlert;
    }

    public static boolean isEnableUnmuteVolumeAutomatically() {
        return enableUnmuteVolumeAutomatically;
    }
    public static void setEnableUnmuteVolumeAutomatically(boolean enableUnmuteVolumeAutomatically) {
        ComputerSettings.enableUnmuteVolumeAutomatically = enableUnmuteVolumeAutomatically;
    }

    public static boolean isAutomaticallyReduceAndRestoreBL() {
        return automaticallyReduceAndRestoreBL;
    }
    public static void setAutomaticallyReduceAndRestoreBL(boolean automaticallyRestoreBrightnessLevel) {
        ComputerSettings.automaticallyReduceAndRestoreBL = automaticallyRestoreBrightnessLevel;
    }

    public static boolean isAutomaticallyReduceBrightnessLevel() {
        return automaticallyReduceBrightnessLevel;
    }
    public static void setAutomaticallyReduceBrightnessLevel(boolean automaticallyReduceBrightnessLevel) {
        ComputerSettings.automaticallyReduceBrightnessLevel = automaticallyReduceBrightnessLevel;
    }

    public static boolean isAutomaticallyRestoreBrightnessLevel() {
        return automaticallyRestoreBrightnessLevel;
    }
    public static void setAutomaticallyRestoreBrightnessLevel(boolean automaticallyRestoreBrightnessLevel) {
        ComputerSettings.automaticallyRestoreBrightnessLevel = automaticallyRestoreBrightnessLevel;
    }

    public static List<String> getAudioDevices() {
        return audioDevices;
    }
    public static void setAudioDevices(List<String> audioDevices) {
        ComputerSettings.audioDevices = audioDevices;
    }
    public static boolean addItemToAudioList(String device) {
        if(!audioDevices.contains(device)){
            audioDevices.add(device);
            return true;
        }
        return false;
    }
    public static String getItemFromAudioList(String device) {
        if(audioDevices.contains(device)) {
            for(String item : audioDevices) {
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
    public static boolean removeItemFromAudioList(String device) {
        return audioDevices.remove(device);
    }
    public static boolean removeItemFromAudioListAtIndex(int index) {
        if (index >= 0 && index < audioDevices.size()) {
            audioDevices.remove(index);
            return true;
        }
        return false;
    }
    
    public static String getDefaultSpeakerOutputDeviceName() {
        return defaultSpeakerOutputDeviceName;
    }
    
    public static void setDefaultSpeakerOutputDeviceName(String defaultSpeakerOutputDeviceName) {
        ComputerSettings.defaultSpeakerOutputDeviceName = defaultSpeakerOutputDeviceName;
        if(currentAudioDevice == null) {
            currentAudioDevice = defaultSpeakerOutputDeviceName;
        }
    }
    
    public static String getCurrentAudioDevice() {
        return currentAudioDevice;
    }
    public static void setCurrentAudioDevice(String currentAudioDevice) {
        ComputerSettings.currentAudioDevice = currentAudioDevice;
    }

    public static String getNotificationSoundFileName() {
        return notificationSoundFileName;
    }
    public static void setNotificationSoundFileName(String notificationSoundFileName) {
        ComputerSettings.notificationSoundFileName = notificationSoundFileName;
    }

    public static int getBrightnessControlOption() {
        return brightnessControlOption;
    }
    public static void setBrightnessControlOption(int option) {
        ComputerSettings.brightnessControlOption = option;
    }

    public static int getBrightnessLevel() {
        return brightnessLevel;
    }
    public static void setBrightnessLevel(int brightnessLevel) {
        ComputerSettings.brightnessLevel = brightnessLevel;
    }

    public static int getVolumeLevel() {
        return volumeLevel;
    }
    public static void setVolumeLevel(int volumeLevel) {
        ComputerSettings.volumeLevel = volumeLevel;
    }

    public static int getWakeUpEvery() {
        return wakeUpEvery;
    }
    public static void setWakeUpEvery(int wakeUpEvery) {
        ComputerSettings.wakeUpEvery = wakeUpEvery;
    }
}