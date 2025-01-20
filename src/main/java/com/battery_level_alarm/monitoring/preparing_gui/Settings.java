package com.battery_level_alarm.monitoring.preparing_gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.core.BatteryLevelAlarm;
import com.battery_level_alarm.monitoring.core.FileManager;
import com.battery_level_alarm.monitoring.effects.AlertSound;

public class Settings {
    private static final Font DEFAULT_FONT = new Font("Serif", Font.BOLD + Font.ITALIC, 14);
    private static final String DEFAULT_SOUND_PATH = "/com/battery_level_alarm/monitoring/BattIco/flash_flood_warning.wav";
    private static String soundPath = DEFAULT_SOUND_PATH;
    private static boolean soundPlayed = false;
    
    private static JScrollPane CreatedGUI;
    private static JPanel firstPartPanel;
    private static JPanel secondPartPanel;
    private static GridBagConstraints gbc;
    
    public static JScrollPane getCreatedGUI() {
    	return CreatedGUI;
    }
    
    public static void createAndShowGUI() {
        JPanel settingsPanel = new JPanel();
    	settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        JPanel filePathPanel = new JPanel(new BorderLayout());

        firstPartPanel = new JPanel(new GridBagLayout());
        secondPartPanel = new JPanel(new GridBagLayout());
        gbc = createGridBagConstraints();
        int index = 0;
        
        addLabeledSpinner(firstPartPanel, "Set Volume Level (%):", UserChoices.getVolumeLevel(), 35, 20, 100, 1, index, 0,
        	e -> {
        		int percentage = getSpinnerValue((JSpinner) e.getSource(), 20, 35);
        	    UserChoices.setVolumeLevel(percentage);
        	    FileManager.saveSettings();
        	});
        
        addLabeledSpinner(firstPartPanel, "Minimum Battery Level:", UserChoices.getMinimumLevel(), 25, 10, 50, 1, ++index, 0,
            e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 10, 25);
                UserChoices.setMinimumLevel(value);
                FileManager.saveSettings();
            });

        addLabeledSpinner(firstPartPanel, "Maximum Battery Level:", UserChoices.getMaximumLevel(), 85, 80, 100, 1, ++index, 0,
        	e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 80, 85);
                UserChoices.setMaximumLevel(value);
                FileManager.saveSettings();
            });
        
        addLabel(firstPartPanel, "Repeat Interval: ", ++index, 0);
        addLabeledSpinner(firstPartPanel, "For General Monitoring (in minutes):", UserChoices.getRepeatIntervalForGeneralMonitor(), 1, 1, 20, 1, ++index, 0,
            e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 1, 1);
                UserChoices.setRepeatIntervalForGeneralMonitor(value);
                FileManager.saveSettings();
            });
        
        addLabeledSpinner(firstPartPanel, "5 minutes before the risk phase (in Seconds):", UserChoices.getRepeatIntervalBeforeRiskPhase(), 1, 1, 60, 1, ++index, 0,
            e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 1, 1);
                UserChoices.setRepeatIntervalBeforeRiskPhase(value);
                FileManager.saveSettings();
            });
        
        addLabeledSpinner(firstPartPanel, "Sound Duration (in seconds):", UserChoices.getSoundDuration(), 5, 1, 300, 1, ++index, 0,
            e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                UserChoices.setSoundDuration(value);
                FileManager.saveSettings();
            });
        
        addCheckbox("Enable Automatic Monitoring", UserChoices.isAutoMonitoring(), ++index, 0,
        e -> {
        	UserChoices.setAutoMonitoring(((JCheckBox) e.getSource()).isSelected());
        	FileManager.saveSettings();
        });
        
        addCheckbox("Enable Primary Sound Alerts", UserChoices.isEnablePrimarySound(), ++index, 0,
        e -> {
            UserChoices.setEnablePrimarySound(((JCheckBox) e.getSource()).isSelected());
            FileManager.saveSettings();
        });
        
        addCheckbox("Enable Secondary Sound Alerts", UserChoices.isEnableSecondarySound(), ++index, 0,
        e -> {
            UserChoices.setEnableSecondarySound(((JCheckBox) e.getSource()).isSelected());
            FileManager.saveSettings();
        });
        
        addCheckbox("Enable Text Alerts", UserChoices.isEnableText(), ++index, 0,
        e -> {
            UserChoices.setEnableText(((JCheckBox) e.getSource()).isSelected());
            FileManager.saveSettings();
        });
        
        JPanel sounthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField pathField = new JTextField();
        sounthPanel.add(addTextInScroll(pathField, UserChoices.getSoundPath(), DEFAULT_FONT));
        
        JCheckBox defaultSoundCheckBox = new JCheckBox("Set the default sound.");
        defaultSoundCheckBox.setFont(DEFAULT_FONT);
        defaultSoundCheckBox.setSelected((DEFAULT_SOUND_PATH.equals(UserChoices.getSoundPath())));
        defaultSoundCheckBox.addActionListener(e -> {
            UserChoices.setSoundPath(DEFAULT_SOUND_PATH);
            pathField.setText(DEFAULT_SOUND_PATH);
            FileManager.saveSettings();
            BatteryLevelAlarm.refreshSettingsPanel();
        });
        gbc.gridx = 0;
        gbc.gridy = ++index;
        firstPartPanel.add(defaultSoundCheckBox, gbc);
        
        index = 0;
        addLabel(secondPartPanel, "Sound File Path: ", index, 0);
        addLabel(secondPartPanel, "\u2003", index, 1);
        addButton("Choose Sound File", index, 2, 
        e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Audio Files", "wav", "mp3"));
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                soundPath = fileChooser.getSelectedFile().getAbsolutePath();
                if(DEFAULT_SOUND_PATH.equals(soundPath)) {
                	defaultSoundCheckBox.setSelected(true);
                	pathField.setText(DEFAULT_SOUND_PATH);
                } else {
                	defaultSoundCheckBox.setSelected(false);
                	pathField.setText(soundPath);
                }
                
                UserChoices.setSoundPath(soundPath);
                FileManager.saveSettings();
                BatteryLevelAlarm.refreshSettingsPanel();
            }
        });
    	
        addLabel(secondPartPanel, "Simulation of alarm sound: ", ++index, 0);
        addLabel(secondPartPanel, "\u2003", index, 1);
        addButton("  ⏯  ", index, 2, 
        e -> {
        	if (soundPlayed) {
                return;
            }
            
        	soundPlayed = true;
            Thread playThread = new Thread(() -> {
            	AlertSound.playSound(UserChoices.getSoundPath());
                soundPlayed = false;
            });
            playThread.start();
        });
        
        filePathPanel.add(secondPartPanel, BorderLayout.CENTER);
        filePathPanel.add(sounthPanel, BorderLayout.SOUTH);
        
        settingsPanel.add(firstPartPanel);
        settingsPanel.add(filePathPanel);
        CreatedGUI = new JScrollPane(settingsPanel);
    }
    
    private static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }
    
    private static void addLabeledSpinner(JPanel panel, String label, int currentValue, int defaultValue, int min, int max, int step, int row, int culomn, ChangeListener listener) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(DEFAULT_FONT);
        gbc.gridy = row;
        gbc.gridx = culomn;
        panel.add(jLabel, gbc);
        
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentValue >= min ? currentValue : defaultValue, min, max, step));
        spinner.setFont(DEFAULT_FONT);
        spinner.setPreferredSize(new Dimension(80, 30));
        spinner.addChangeListener(listener);
        gbc.gridx = ++culomn;
        panel.add(spinner, gbc);
    }
    
    private static void addCheckbox(String label, boolean isSelected, int row, int culomn, ActionListener listener) {
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.setFont(DEFAULT_FONT);
        checkBox.setSelected(isSelected);
        checkBox.addActionListener(listener);
        gbc.gridx = culomn;
        gbc.gridy = row;
        firstPartPanel.add(checkBox, gbc);
    }
    
    private static void addLabel(JPanel panel, String label, int row, int culomn) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(DEFAULT_FONT);
        gbc.gridy = row;
        gbc.gridx = culomn;
        panel.add(jLabel, gbc);
    }
    
    private static void addButton(String label, int row, int culomn, ActionListener listener) {
        JButton button = new JButton(label);
        button.setFont(DEFAULT_FONT);
        button.setMaximumSize(new Dimension(150, 30));
        button.setPreferredSize(new Dimension(150, 30));
        button.addActionListener(listener);
        gbc.gridx = culomn;
        gbc.gridy = row;
        secondPartPanel.add(button, gbc);
    }
    
    private static JScrollPane addTextInScroll(JTextField field, String label, Font font) {
        field = new JTextField(label);
        field.setFont(font);
        field.setEditable(false);
        field.setEnabled(false);
        
        JScrollPane scroll = new JScrollPane(field);
        scroll.setMaximumSize(new Dimension(390, 50));
        scroll.setPreferredSize(new Dimension(390, 50));
        return scroll;
    }
    
    private static int getSpinnerValue(JSpinner spinner, int minValue, int defaultValue) {
        int value = Integer.parseInt(spinner.getValue().toString());
        return (value >= minValue) ? value : defaultValue;
    }
}