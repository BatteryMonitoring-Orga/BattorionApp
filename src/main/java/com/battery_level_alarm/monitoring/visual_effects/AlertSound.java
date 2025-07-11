package com.battery_level_alarm.monitoring.visual_effects;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutput$CMD.setAudioOutputDevice;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.getDefaultSpeakerOutputDeviceName;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.activeAudioDeviceName;

import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.command_executors.CallCommandLine;
import com.battery_level_alarm.monitoring.command_executors.SoundVolumeReader;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javax.sound.sampled.*;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.URI;
import java.net.URL;

public class AlertSound {
    public static final String DEFAULT_PRIMARY_SOUND_PATH = "/com/battery_level_alarm/monitoring/Sounds/flash_flood_warning.wav";
    public static final String DEFAULT_SECONDARY_SOUND_PATH = "java.awt.Toolkit.getDefaultToolkit().beep()";
    private static Player player;
    private static Clip clip;
    private static Thread playThread;
    
    private static int volumeLevel = 0;
    private static final int defaultSoundDuration = 1;
    public static boolean useDefaultDuration = false;
    public static volatile boolean isProcessesApplied = false;
    
    public static void playSound(String filePath) {
        try {
            InputStream soundStream = getSoundStream(filePath);
            if (soundStream == null) {
                showErrorMessage("Sound file not found, using default sound.\nFile Path: '" + filePath + "'");
                soundStream = getSoundStream(DEFAULT_PRIMARY_SOUND_PATH);
            } if (soundStream == null) {
                showErrorMessage("Default sound file not found.\nFile Path: '" + DEFAULT_PRIMARY_SOUND_PATH + "'");
                return;
            }
            
            if(isFromCriticalAlert && soundControlPanel != null) {
                soundControlPanel.setVisible(true);
            } if (filePath.toLowerCase().endsWith(".mp3")) {
                playMP3(soundStream);
            } else if (filePath.toLowerCase().endsWith(".wav")) {
                playWAV(soundStream);
            } else {
                showErrorMessage("Unsupported file format. Using default sound.\n*Supported file formats: (wav, mp3)");
                playWAV(getSoundStream(DEFAULT_PRIMARY_SOUND_PATH));
            }
        } catch (IOException | LineUnavailableException | InterruptedException | UnsupportedAudioFileException e) {
            printErrorMessage(e);
        }
    }
    
    private static InputStream getSoundStream(String filePath) throws IOException {
        InputStream inputStream = null;
        File file = new File(filePath);
        if (file.exists()) {
            try (InputStream in_st = new FileInputStream(file)) {
                byte[] data = in_st.readAllBytes();
                inputStream = new ByteArrayInputStream(data);
            } catch (Exception e) {
                printErrorMessage(e);
            }
        } else {
            inputStream = AlertSound.class.getResourceAsStream(filePath);
        }

        if (inputStream == null) {
            inputStream = ifInputStreamNull(filePath);
        } if (inputStream != null) {
            return ifInputStreamNotNull(inputStream);
        }
        return null;
    }

    private static InputStream ifInputStreamNull(String filePath) {
        try {
            URI uri = new URI(filePath);
            URL url = uri.toURL();
            try(InputStream in_st = url.openStream()) {
                return in_st;
            } catch (Exception e) {
                printErrorMessage(e);
                return null;
            }
        } catch (Exception ex) {
            printErrorMessage(ex);
            return null;
        }
    }

    private static ByteArrayInputStream ifInputStreamNotNull(InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            inputStream.close();
            return new ByteArrayInputStream(buffer.toByteArray());
        } catch (IOException e) {
            printErrorMessage(e);
            return null;
        }
    }
    
    private static void playWAV(InputStream soundStream) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
        setupAudioSettingsBeforeAlert();
    	AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundStream);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();

        if(useDefaultDuration){
            Thread.sleep(defaultSoundDuration * 1000L);
        } else{
            Thread.sleep(UserChoices.getSoundDuration() * 1000L);
        }
        stopWAV();
        cleanupAudioSettingsAfterAlert();
    }
    
    public static void stopWAV() {
        if (clip != null && clip.isRunning() && clip.isOpen() && clip.isActive()) {
            clip.stop();
            clip.close();
        }
    }
    
    private static void playMP3(InputStream soundStream) throws InterruptedException {
        setupAudioSettingsBeforeAlert();
    	playThread = new Thread(() -> {
            try {
                player = new Player(soundStream);
                player.play();
            } catch (JavaLayerException e) {
                printErrorMessage(e);
            }
        });
        
        playThread.start();
        if(useDefaultDuration) {
            Thread.sleep(defaultSoundDuration * 1000L);
        } else {
            Thread.sleep(UserChoices.getSoundDuration() * 1000L);
        }
        stopMP3();
        cleanupAudioSettingsAfterAlert();
    }
    
    public static void stopMP3() {
        if (player != null) {
            player.close();
        } if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
        }
    }
    
    public static void setupAudioSettingsBeforeAlert() {
        isProcessesApplied = true;
        try {
            if (ComputerSettings.isRestoringSoundLevelAfterAlert()) {
                volumeLevel = (int) SoundVolumeReader.getVolumeLevel();
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }
        
        try {
            String deviceName = getDefaultSpeakerOutputDeviceName();
            if (ComputerSettings.isEnableExchangeToSpeakerAudioOutput() && isFromCriticalAlert) {
                setAudioOutputDevice(deviceName);
                activeAudioDeviceName.setText(deviceName);
                audioOutputDeviceDashTextField.setText(deviceName);
            }
            Thread.sleep(100);
        } catch (InterruptedException e) {
            printErrorMessage(e);
        }
        
        if(ComputerSettings.isEnablingSoundLevelChange()) {
            CallCommandLine.setPCVolume(ComputerSettings.getVolumeLevel());
        } if(ComputerSettings.isEnableUnmuteVolumeAutomatically()) {
            CallCommandLine.setSoundUnmute(0);
        }
    }

    public static void cleanupAudioSettingsAfterAlert() {
        try {
            if(ComputerSettings.isRestoringSoundLevelAfterAlert()) {
                CallCommandLine.setPCVolume(volumeLevel);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            logger.severe("[EXCEPTION]: " + e.getMessage());
            throw new RuntimeException(e);
        }
        
        if(ComputerSettings.isEnableExchangeToAudioOutputUsed() && isFromCriticalAlert) {
            String deviceName = ComputerSettings.getCurrentAudioDevice();
            setAudioOutputDevice(deviceName);
            activeAudioDeviceName.setText(deviceName);
            audioOutputDeviceDashTextField.setText(deviceName);
            if(soundControlPanel != null) {
                soundControlPanel.setVisible(false);
            }
        }
        isProcessesApplied = false;
    }

    private static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}