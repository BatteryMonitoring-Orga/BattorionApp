package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.addToggleButton;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.core.BattorionMain;
import com.battery_level_alarm.monitoring.core.FileManager;
import com.battery_level_alarm.monitoring.effects.AlertSound;
import com.battery_level_alarm.monitoring.effects.Appearance;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SettingsGUI {
    public static final Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 14);
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
        GridBagConstraints gbc = createGridBagConstraints();

        int index = 0;
        JPanel firstPartPanel = getFirstPartPanel(gbc, index);
        JPanel secondPartPanel = getSecondPartPanel(gbc, index);
        settingsPanel.add(firstPartPanel);
        settingsPanel.add(secondPartPanel);
        CreatedGUI = new JScrollPane(settingsPanel);
    }
    
    public static GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private static JPanel getFirstPartPanel(GridBagConstraints gbc, int index){
        JPanel firstPartPanel = new JPanel(new GridBagLayout());
        addLabeledSpinner(gbc, firstPartPanel, "Minimum Battery Level:", UserChoices.getMinimumLevel(), 25, 15, 30, 1, index, 0,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 10, 25);
                    UserChoices.setMinimumLevel(value);
                    FileManager.saveSettings();
                });

        addLabeledSpinner(gbc, firstPartPanel, "Maximum Battery Level:", UserChoices.getMaximumLevel(), 85, 80, 90, 1, ++index, 0,
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

        addLabeledSpinner(gbc, firstPartPanel, "Sound Duration (in Seconds):", UserChoices.getSoundDuration(), 5, 1, 10, 1, ++index, 0,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    UserChoices.setSoundDuration(value);
                    FileManager.saveSettings();
                });

        addSeparator(gbc, firstPartPanel, 100, ++index, 0);
        returnGBC$ToDefault(gbc);
        String automatic = UserChoices.isAutoMonitoring()? "On":"Off";
        String primarySound = UserChoices.isEnablePrimarySound()? "On":"Off";
        String secondarySound = UserChoices.isEnableSecondarySound()? "On":"Off";
        String chargingSound = UserChoices.isEnableChargeAndDischargeSound()? "On":"Off";
        String textAlert = UserChoices.isEnableText()? "On":"Off";

        addLabel(gbc, firstPartPanel, "Enable Automatic Monitoring:", ++index, 0);
        addToggleButton(gbc, firstPartPanel, UserChoices::setAutoMonitoring, FileManager::saveSettings, automatic, index, 1, 80, 30);
        addLabel(gbc, firstPartPanel, "Enable Primary Sound Alerts:", ++index, 0);
        addToggleButton(gbc, firstPartPanel, UserChoices::setEnablePrimarySound, FileManager::saveSettings, primarySound, index, 1, 80, 30);
        addLabel(gbc, firstPartPanel, "Enable Secondary Sound Alerts:", ++index, 0);
        addToggleButton(gbc, firstPartPanel, UserChoices::setEnableSecondarySound, FileManager::saveSettings, secondarySound, index, 1, 80, 30);
        addLabel(gbc, firstPartPanel, "Enable Charging/Discharging Sound:", ++index, 0);
        addToggleButton(gbc, firstPartPanel, UserChoices::setEnableChargeAndDischargeSound, FileManager::saveSettings, chargingSound, index, 1, 80, 30);
        addLabel(gbc, firstPartPanel, "Enable Text Alerts:", ++index, 0);
        addToggleButton(gbc, firstPartPanel, UserChoices::setEnableText, FileManager::saveSettings, textAlert, index, 1, 80, 30);
        return firstPartPanel;
    }

    private static JPanel getSecondPartPanel(GridBagConstraints gbc, int index){
        JPanel secondPartPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField pathField = new JTextField();
        southPanel.add(addTextInScroll(pathField, UserChoices.getSoundPath(), DEFAULT_FONT));

        addSeparator(gbc, centerPanel, 100, index, 0);
        returnGBC$ToDefault(gbc);
        boolean isSelected = DEFAULT_SOUND_PATH.equals(UserChoices.getSoundPath());
        JCheckBox defaultSoundCheckBox = addCheckbox(
                gbc, centerPanel, "Select the default sound",
                isSelected, ++index, 0,
                e -> {
                    UserChoices.setSoundPath(DEFAULT_SOUND_PATH);
                    pathField.setText(DEFAULT_SOUND_PATH);
                    FileManager.saveSettings();
                    BattorionMain.refreshSettingsPanel();
                });

        addLabel(gbc, centerPanel, "Sound File Path: ", ++index, 0);
        addLabel(gbc, centerPanel, "\u2003", index, 1);
        setButtonDefaultSize();
        addButton(gbc, centerPanel, "Choose Sound", index, 2,
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
                        BattorionMain.refreshSettingsPanel();
                    }
                });

        addLabel(gbc, centerPanel, "Simulation of alarm sound: ", ++index, 0);
        addLabel(gbc, centerPanel, "\u2003", index, 1);
        setButtonDefaultSize();
        addButton(gbc, centerPanel, "  ⏯  ", index, 2,
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

        secondPartPanel.add(centerPanel, BorderLayout.CENTER);
        secondPartPanel.add(southPanel, BorderLayout.SOUTH);
        return secondPartPanel;
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
    
    public static JCheckBox addCheckbox(
            GridBagConstraints gbc, JPanel panel, String label,
            boolean isSelected, int row, int column, ActionListener listener
    ){
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.setFont(DEFAULT_FONT);
        checkBox.setSelected(isSelected);
        checkBox.addActionListener(listener);
        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(checkBox, gbc);
        return checkBox;
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

    public static void addButton(GridBagConstraints gbc, JPanel secondPartPanel, String label, int row, int column, ActionListener listener) {
        JButton button = new JButton(label);
        button.setFont(DEFAULT_FONT);
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.addActionListener(listener);
        gbc.gridx = column;
        gbc.gridy = row;
        secondPartPanel.add(button, gbc);
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
        horizontalLine.setForeground(Appearance.getBorderColor());
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(horizontalLine, gbc);
    }

    public static void returnGBC$ToDefault(GridBagConstraints gbc){
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
    }
}