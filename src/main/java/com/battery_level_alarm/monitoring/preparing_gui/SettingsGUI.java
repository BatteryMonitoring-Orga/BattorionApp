package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToTextFields.addTextInScroll;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;

import com.battery_level_alarm.monitoring.core.BattorionPanelHelper;
import com.battery_level_alarm.monitoring.configuration_records.ScrollConfiguration;
import com.battery_level_alarm.monitoring.configuration_records.SpinnerConfig;
import com.battery_level_alarm.monitoring.basics.UserChoices;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.effects.AlertSound;
import com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SettingsGUI {
    private static final String DEFAULT_SOUND_PATH = "/com/battery_level_alarm/monitoring/Sounds/flash_flood_warning.wav";
    private static String soundPath = DEFAULT_SOUND_PATH;
    private static boolean soundPlayed = false;

    private static JScrollPane CreatedGUI;
    private static final ScrollConfiguration SCROLL_TEXT_FIELD_CONFIGURATION = new ScrollConfiguration(
            false,
            true,
            false,
            false,
            null,
            new Dimension(500, 50)
    );
    private static final ScrollConfiguration SCROLL_PANEL_CONFIGURATION = new ScrollConfiguration(
            false,
            true,
            true,
            false,
            null,
            new Dimension(550, 300)
    );

    public static JScrollPane getCreatedGUI() {
    	return CreatedGUI;
    }

    public static void createAndShowGUI() {
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);

        int index = 0;
        JPanel firstPartPanel = getFirstPartPanel(gbc, index);
        JPanel secondPartPanel = getSecondPartPanel(gbc, index);
        returnGBC$ToDefault(gbc);
        settingsPanel.add(firstPartPanel, gbc);
        gbc.gridy++;
        settingsPanel.add(secondPartPanel, gbc);

        CreatedGUI = new JScrollPane(settingsPanel);
        applyScrollConfigurationDetails(CreatedGUI, SCROLL_PANEL_CONFIGURATION);
    }

    private static JPanel getFirstPartPanel(GridBagConstraints gbc, int index){
        JPanel firstPartPanel = new JPanel(new GridBagLayout());
        SpinnerConfig minBatteryConfig = new SpinnerConfig(
                "Minimum Battery Level:",
                UserChoices.getMinimumLevel(), 25, 15, 30, 1,
                index, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 10, 25);
                    UserChoices.setMinimumLevel(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner minBatteryConfigSpinner = RelatedToSpinner.createSpinner(minBatteryConfig, false);
        addLabeledSpinner(gbc, firstPartPanel, minBatteryConfig, minBatteryConfigSpinner);

        SpinnerConfig maxBatteryConfig = new SpinnerConfig(
                "Maximum Battery Level:",
                UserChoices.getMaximumLevel(), 85, 80, 90, 1,
                ++index, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 80, 85);
                    UserChoices.setMaximumLevel(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner maxBatteryConfigSpinner = RelatedToSpinner.createSpinner(maxBatteryConfig, false);
        addLabeledSpinner(gbc, firstPartPanel, maxBatteryConfig, maxBatteryConfigSpinner);

        setDimension(++index, 0);
        addLabel(gbc, firstPartPanel, "Repeat Interval: ");
        SpinnerConfig repeatIntervalConfig = new SpinnerConfig(
                "5 minutes before the risk phase (in Seconds):",
                UserChoices.getRepeatIntervalBeforeRiskPhase(), 1, 1, 60, 1,
                ++index, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 1);
                    UserChoices.setRepeatIntervalBeforeRiskPhase(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner repeatIntervalConfigSpinner = RelatedToSpinner.createSpinner(repeatIntervalConfig, false);
        addLabeledSpinner(gbc, firstPartPanel, repeatIntervalConfig, repeatIntervalConfigSpinner);

        SpinnerConfig soundDurationConfig = new SpinnerConfig(
                "Sound Duration (in Seconds):",
                UserChoices.getSoundDuration(), 5, 1, 10, 1,
                ++index, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    UserChoices.setSoundDuration(value);
                    ConfigurationFilesManager.saveSettings();
                }
        );
        JSpinner soundDurationConfigSpinner = RelatedToSpinner.createSpinner(soundDurationConfig, false);
        addLabeledSpinner(gbc, firstPartPanel, soundDurationConfig, soundDurationConfigSpinner);

        setDimension(++index, 0);
        addSeparator(gbc, firstPartPanel, 150);
        returnGBC$ToDefault(gbc);
        String automatic = UserChoices.isAutoMonitoring()? "On":"Off";
        String primarySound = UserChoices.isEnablePrimarySound()? "On":"Off";
        String secondarySound = UserChoices.isEnableSecondarySound()? "On":"Off";
        String chargingSound = UserChoices.isEnableChargeAndDischargeSound()? "On":"Off";
        String textAlert = UserChoices.isEnableText()? "On":"Off";

        setDimension(++index, 0);
        addLabel(gbc, firstPartPanel, "Enable Automatic Monitoring:");
        setColumn(1);
        addToggleButton(
                gbc, firstPartPanel, UserChoices::setAutoMonitoring,
                ConfigurationFilesManager::saveSettings, automatic, 80, 30,
                null, false
        );
        setDimension(++index, 0);
        addLabel(gbc, firstPartPanel, "Enable Primary Sound Alerts:");
        setColumn(1);
        addToggleButton(
                gbc, firstPartPanel, UserChoices::setEnablePrimarySound,
                ConfigurationFilesManager::saveSettings, primarySound, 80, 30,
                null, false
        );
        setDimension(++index, 0);
        addLabel(gbc, firstPartPanel, "Enable Secondary Sound Alerts:");
        setColumn(1);
        addToggleButton(
                gbc, firstPartPanel, UserChoices::setEnableSecondarySound,
                ConfigurationFilesManager::saveSettings, secondarySound, 80, 30,
                null, false
        );
        setDimension(++index, 0);
        addLabel(gbc, firstPartPanel, "Enable Charging/Discharging Sound:");
        setColumn(1);
        addToggleButton(
                gbc, firstPartPanel, UserChoices::setEnableChargeAndDischargeSound,
                ConfigurationFilesManager::saveSettings, chargingSound, 80, 30,
                null, false
        );
        setDimension(++index, 0);
        addLabel(gbc, firstPartPanel, "Enable Text Alerts:");
        setColumn(1);
        addToggleButton(
                gbc, firstPartPanel, UserChoices::setEnableText,
                ConfigurationFilesManager::saveSettings, textAlert, 80, 30,
                null, false
        );
        return firstPartPanel;
    }

    private static JPanel getSecondPartPanel(GridBagConstraints gbc, int index){
        JPanel secondPartPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField pathField = new JTextField();
        southPanel.add(
                addTextInScroll(
                        pathField, UserChoices.getSoundPath(),
                        DEFAULT_FONT, false, false,
                        SCROLL_TEXT_FIELD_CONFIGURATION
                )
        );

        setDimension(index, 0);
        addSeparator(gbc, centerPanel, 150);
        returnGBC$ToDefault(gbc);
        boolean isSelected = DEFAULT_SOUND_PATH.equals(UserChoices.getSoundPath());
        setDimension(++index, 0);
        JCheckBox defaultSoundCheckBox = addCheckbox(
                gbc, centerPanel, "Select the default sound", isSelected,
                _ -> {
                    UserChoices.setSoundPath(DEFAULT_SOUND_PATH);
                    pathField.setText(DEFAULT_SOUND_PATH);
                    ConfigurationFilesManager.saveSettings();
                    BattorionPanelHelper.refreshSettingsPanel();
                });

        setRow(++index);
        addLabel(gbc, centerPanel, "Sound File Path: ");
        setColumn(1);
        addLabel(gbc, centerPanel, "\u2003");
        setDimension(index, 2);
        setButtonSize(200, 30);
        addButton(gbc, centerPanel, "Choose Sound",
                _ -> {
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
                        ConfigurationFilesManager.saveSettings();
                        BattorionPanelHelper.refreshSettingsPanel();
                    }
                });

        setDimension(++index, 0);
        addLabel(gbc, centerPanel, "Simulation of alarm sound: ");
        setColumn(1);
        addLabel(gbc, centerPanel, "\u2003");
        setButtonDefaultSize();
        setDimension(index, 2);
        addButton(gbc, centerPanel, "  ⏯  ",
                _ -> {
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

        setButtonDefaultSize();
        secondPartPanel.add(centerPanel, BorderLayout.CENTER);
        secondPartPanel.add(southPanel, BorderLayout.SOUTH);
        return secondPartPanel;
    }
}