package com.battery_level_alarm.monitoring.preparing_gui;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.core.BatteryLevelAlarm;
import com.battery_level_alarm.monitoring.core.FileManager;
import com.battery_level_alarm.monitoring.effects.AlertSound;

public class SettingsGUI {
    public static final Font DEFAULT_FONT = new Font("Serif", Font.BOLD + Font.ITALIC, 14);
    private static final String DEFAULT_SOUND_PATH = "/com/battery_level_alarm/monitoring/Sounds/flash_flood_warning.wav";
    private static String soundPath = DEFAULT_SOUND_PATH;
    private static boolean soundPlayed = false;
    private static int buttonWidth = 150;
    private static int buttonHeight = 30;

    private static JScrollPane CreatedGUI;
    public static JScrollPane getCreatedGUI() {
    	return CreatedGUI;
    }
    
    public static void createAndShowGUI() {
        JPanel settingsPanel = new JPanel();
    	settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        JPanel filePathPanel = new JPanel(new BorderLayout());

        JPanel firstPartPanel = new JPanel(new GridBagLayout());
        JPanel secondPartPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        int index = 0;
        
        addLabeledSpinner(gbc, firstPartPanel, "Minimum Battery Level:", UserChoices.getMinimumLevel(), 25, 10, 50, 1, index, 0,
            e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 10, 25);
                UserChoices.setMinimumLevel(value);
                FileManager.saveSettings();
            });

        addLabeledSpinner(gbc, firstPartPanel, "Maximum Battery Level:", UserChoices.getMaximumLevel(), 85, 80, 100, 1, ++index, 0,
        	e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 80, 85);
                UserChoices.setMaximumLevel(value);
                FileManager.saveSettings();
            });
        
        addLabel(gbc, firstPartPanel, "Repeat Interval: ", ++index, 0);
        addLabeledSpinner(gbc, firstPartPanel, "5 minutes before the risk phase (in Seconds):", UserChoices.getRepeatIntervalBeforeRiskPhase(), 1, 1, 60, 1, ++index, 0,
            e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 1, 1);
                UserChoices.setRepeatIntervalBeforeRiskPhase(value);
                FileManager.saveSettings();
            });
        
        addLabeledSpinner(gbc, firstPartPanel, "Sound Duration (in Seconds):", UserChoices.getSoundDuration(), 5, 1, 300, 1, ++index, 0,
            e -> {
                int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                UserChoices.setSoundDuration(value);
                FileManager.saveSettings();
            });

        addSeparator(gbc, firstPartPanel, 100, ++index, 0);
        returnGBC$ToDefault(gbc);
        addCheckbox(gbc, firstPartPanel, "Enable Automatic Monitoring", UserChoices.isAutoMonitoring(), ++index, 0,
        e -> {
        	UserChoices.setAutoMonitoring(((JCheckBox) e.getSource()).isSelected());
        	FileManager.saveSettings();
        });

        addCheckbox(gbc, firstPartPanel, "Enable Primary Sound Alerts", UserChoices.isEnablePrimarySound(), ++index, 0,
        e -> {
            UserChoices.setEnablePrimarySound(((JCheckBox) e.getSource()).isSelected());
            FileManager.saveSettings();
        });
        
        addCheckbox(gbc, firstPartPanel, "Enable Secondary Sound Alerts", UserChoices.isEnableSecondarySound(), ++index, 0,
        e -> {
            UserChoices.setEnableSecondarySound(((JCheckBox) e.getSource()).isSelected());
            FileManager.saveSettings();
        });

        addCheckbox(gbc, firstPartPanel, "Enable Charging/Discharging Sound", UserChoices.isEnableChargeAndDischargeSound(), ++index, 0,
                e -> {
                    UserChoices.setEnableChargeAndDischargeSound(((JCheckBox) e.getSource()).isSelected());
                    FileManager.saveSettings();
                });
        
        addCheckbox(gbc, firstPartPanel, "Enable Text Alerts", UserChoices.isEnableText(), ++index, 0,
        e -> {
            UserChoices.setEnableText(((JCheckBox) e.getSource()).isSelected());
            FileManager.saveSettings();
        });
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField pathField = new JTextField();
        southPanel.add(addTextInScroll(pathField, UserChoices.getSoundPath(), DEFAULT_FONT));
        
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
        addSeparator(gbc, secondPartPanel, 100, index, 0);
        returnGBC$ToDefault(gbc);
        addLabel(gbc, secondPartPanel, "Sound File Path: ", ++index, 0);
        addLabel(gbc, secondPartPanel, "\u2003", index, 1);
        setButtonDefaultSize();
        addButton(gbc, secondPartPanel, "Choose Sound File", index, 2,
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
    	
        addLabel(gbc, secondPartPanel, "Simulation of alarm sound: ", ++index, 0);
        addLabel(gbc, secondPartPanel, "\u2003", index, 1);
        setButtonDefaultSize();
        addButton(gbc, secondPartPanel, "  ⏯  ", index, 2,
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
        filePathPanel.add(southPanel, BorderLayout.SOUTH);
        
        settingsPanel.add(firstPartPanel);
        settingsPanel.add(filePathPanel);
        CreatedGUI = new JScrollPane(settingsPanel);
    }
    
    public static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }
    
    public static void addLabeledSpinner(GridBagConstraints gbc,JPanel panel, String label, int currentValue, int defaultValue, int min, int max, int step, int row, int column, ChangeListener listener) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(DEFAULT_FONT);
        gbc.gridy = row;
        gbc.gridx = column;
        panel.add(jLabel, gbc);
        
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentValue >= min ? currentValue : defaultValue, min, max, step));
        spinner.setFont(DEFAULT_FONT);
        spinner.setPreferredSize(new Dimension(80, 30));
        spinner.addChangeListener(listener);
        gbc.gridx = ++column;
        panel.add(spinner, gbc);
    }
    
    public static void addCheckbox(GridBagConstraints gbc, JPanel panel, String label, boolean isSelected, int row, int column, ActionListener listener) {
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.setFont(DEFAULT_FONT);
        checkBox.setSelected(isSelected);
        checkBox.addActionListener(listener);
        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(checkBox, gbc);
    }
    
    public static void addLabel(GridBagConstraints gbc, JPanel panel, String label, int row, int column) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(DEFAULT_FONT);
        gbc.gridy = row;
        gbc.gridx = column;
        panel.add(jLabel, gbc);
    }

    public static void setButtonSize(int buttonWidth, int buttonHeight){
        SettingsGUI.buttonWidth = buttonWidth;
        SettingsGUI.buttonHeight = buttonHeight;
    }

    public static void setButtonDefaultSize(){
        SettingsGUI.buttonWidth = 150;
        SettingsGUI.buttonHeight = 30;
    }

    public static JButton addButton(GridBagConstraints gbc, JPanel secondPartPanel, String label, int row, int column, ActionListener listener) {
        JButton button = new JButton(label);
        button.setFont(DEFAULT_FONT);
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.addActionListener(listener);
        gbc.gridx = column;
        gbc.gridy = row;
        secondPartPanel.add(button, gbc);
        return button;
    }
    
    private static JScrollPane addTextInScroll(JTextField field, String label, Font font) {
        field.setText(label);
        field.setFont(font);
        field.setEditable(false);
        field.setEnabled(false);
        
        JScrollPane scroll = new JScrollPane(field);
        scroll.setMaximumSize(new Dimension(390, 50));
        scroll.setPreferredSize(new Dimension(390, 50));
        return scroll;
    }
    
    public static int getSpinnerValue(JSpinner spinner, int minValue, int defaultValue) {
        int value = Integer.parseInt(spinner.getValue().toString());
        return (value >= minValue) ? value : defaultValue;
    }

    public static void addSeparator(GridBagConstraints gbc, JPanel panel, int height, int row, int column){
        JSeparator horizontalLine = new JSeparator();
        horizontalLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        horizontalLine.setForeground(Color.BLACK);
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(horizontalLine, gbc);
    }

    private static void returnGBC$ToDefault(GridBagConstraints gbc){
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
    }
}