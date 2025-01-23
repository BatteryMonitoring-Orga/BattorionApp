package com.battery_level_alarm.monitoring.effects;
import static com.battery_level_alarm.monitoring.command.AudioOutput.setSpeakerAsAnAudioOutput;
import static com.battery_level_alarm.monitoring.core.BatteryLevelAlarm.isFromCriticalAlert;
import static com.battery_level_alarm.monitoring.cybernate.AutoLogin.printErrorMessage;

import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.basics.PC_Details;
import com.battery_level_alarm.monitoring.command.CallCommandLine;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javax.sound.sampled.*;
import javax.swing.JOptionPane;

import java.io.*;
import java.net.URI;
import java.net.URL;

public class AlertSound {
    private static final String DEFAULT_SOUND = "/com/battery_level_alarm/monitoring/Sounds/flash_flood_warning.wav";
    private static Player player;
    private static Thread playThread;

    private static final int defaultSoundDuration = 1;
    public static boolean useDefaultDuration = false;

    public static void playSound(String filePath) {
        try {
            InputStream soundStream = getSoundStream(filePath);
            if (soundStream == null) {
                showErrorMessage("Sound file not found, using default sound.\nFile Path: '" + filePath + "'");
                soundStream = getSoundStream(DEFAULT_SOUND);
            }
            
            if (soundStream == null) {
                showErrorMessage("Default sound file not found.\nFile Path: '" + DEFAULT_SOUND + "'");
                return;
            }
            
            if (filePath.toLowerCase().endsWith(".mp3")) {
                playMP3(soundStream);
            } else if (filePath.toLowerCase().endsWith(".wav")) {
                playWAV(soundStream);
            } else {
                showErrorMessage("Unsupported file format. Using default sound.\n*Supported file formats: (wav, mp3)");
                playWAV(getSoundStream(DEFAULT_SOUND));
            }
        } catch (InterruptedException | UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            printErrorMessage(e);
        }
    }
    
    private static InputStream getSoundStream(String filePath) throws IOException {
        InputStream inputStream = null;
        File file = new File(filePath);
        if (file.exists()) {
            try (InputStream in_st = new FileInputStream(file)) {
                inputStream = in_st;
            } catch (Exception e) {
                printErrorMessage(e);
            }
        } else {
            inputStream = AlertSound.class.getResourceAsStream(filePath);
        }
        
        if (inputStream == null) {
            try {
            	URI uri = new URI(filePath);
                URL url = uri.toURL();
                try(InputStream ins = url.openStream()){
                    inputStream = ins;
                } catch (Exception e) {
                    printErrorMessage(e);
                }
            } catch (Exception ex) {
                printErrorMessage(ex);
            }
        }
        
        if (inputStream != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            inputStream.close();
            return new ByteArrayInputStream(buffer.toByteArray());
        }
        return null;
    }
    
    private static void playWAV(InputStream soundStream) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
    	CallCommandLine.setPCVolume(PC_Details.getVolumeLevel());
        if(PC_Details.isEnableExchangeToSpeakerAudioOutput() && isFromCriticalAlert){
            setSpeakerAsAnAudioOutput();
        }

    	AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundStream);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();

        if(useDefaultDuration){
            Thread.sleep(defaultSoundDuration * 1000L);
        } else{
            Thread.sleep(UserChoices.getSoundDuration() * 1000L);
        }
        clip.stop();
        clip.close();
    }
    
    private static void playMP3(InputStream soundStream) throws InterruptedException {
    	CallCommandLine.setPCVolume(PC_Details.getVolumeLevel());
        if(PC_Details.isEnableExchangeToSpeakerAudioOutput() && isFromCriticalAlert){
            setSpeakerAsAnAudioOutput();
        }

    	playThread = new Thread(() -> {
            try {
                player = new Player(soundStream);
                player.play();
            } catch (JavaLayerException e) {
                printErrorMessage(e);
            }
        });
        
        playThread.start();
        if(useDefaultDuration){
            Thread.sleep(defaultSoundDuration * 1000L);
        } else{
            Thread.sleep(UserChoices.getSoundDuration() * 1000L);
        }
        stopMP3();
    }
    
    public static void stopMP3() {
        if (player != null) {
            player.close();
        }
        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
        }
    }
    
    private static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}