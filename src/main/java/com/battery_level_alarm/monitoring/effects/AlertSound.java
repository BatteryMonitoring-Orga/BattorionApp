package com.battery_level_alarm.monitoring.effects;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import javax.swing.JOptionPane;

import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.command.CallCommandLine;

import java.io.*;
import java.net.URI;
import java.net.URL;

public class AlertSound {
    private static final String DEFAULT_SOUND = "/com/battery_level_alarm/monitoring/BattIco/flash_flood_warning.wav";
    private static Player player;
    private static Thread playThread;
    
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static InputStream getSoundStream(String filePath) throws IOException {
        InputStream inputStream = null;
        File file = new File(filePath);
        if (file.exists()) {
            inputStream = new FileInputStream(file);
        } else {
            inputStream = AlertSound.class.getResourceAsStream(filePath);
        }
        
        if (inputStream == null) {
            try {
            	URI uri = new URI(filePath);
                URL url = uri.toURL();
                inputStream = url.openStream();
            } catch (Exception ignored) {
                // Not a valid URL
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
    	CallCommandLine.setPCVolume(UserChoices.getVolumeLevel());
    	
    	AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundStream);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
        
        Thread.sleep(UserChoices.getSoundDuration() * 1000);
        clip.stop();
        clip.close();
    }
    
    private static void playMP3(InputStream soundStream) throws InterruptedException {
    	CallCommandLine.setPCVolume(UserChoices.getVolumeLevel());
    	
    	playThread = new Thread(() -> {
            try {
                player = new Player(soundStream);
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        });
        
        playThread.start();
        Thread.sleep(UserChoices.getSoundDuration() * 10000);
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