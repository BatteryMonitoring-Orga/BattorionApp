package com.battery_level_alarm.monitoring.system_core.helpers;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.pcVolumeSpinner;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.SettingsContainerClass.ICONS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.*;

import com.battery_level_alarm.monitoring.command_executors.CallCommandLine;
import com.battery_level_alarm.monitoring.command_executors.SoundVolumeReader;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedPanel;

import javax.swing.*;
import java.awt.*;

public class BattorionProgressBarHelper {
    public static void setProgressBarMode(){
        batteryBar.setMinimum(0);
        batteryBar.setMaximum(100);

        if(progressBarInVerticalMode){
            configureProgressBarVertical();
        } else {
            configureProgressBarHorizontal();
        }
    }

    private static void configureProgressBarVertical() {
        batteryBar.setOrientation(JProgressBar.VERTICAL);
        Dimension size = new Dimension(85, 200);
        batteryBar.setPreferredSize(size);
        batteryBar.setMaximumSize(size);
        batteryBar.setMinimumSize(size);
    }

    private static void configureProgressBarHorizontal() {
        batteryBar.setOrientation(JProgressBar.HORIZONTAL);
        Dimension size = new Dimension(225, 85);
        batteryBar.setPreferredSize(size);
        batteryBar.setMaximumSize(size);
        batteryBar.setMinimumSize(size);
    }

    public static void setUpProgressPanel(boolean isFirstMode){
        if(progressBarInVerticalMode){
            progressPanelForVerticalMode(isFirstMode);
        } else {
            progressPanelForHorizontalMode(isFirstMode);
        }
    }

    private static void progressPanelForVerticalMode(boolean isFirstMode) {
        progressPanel.removeAll();
        progressPanel.setLayout(new GridBagLayout());
        progressPanel.setBackground(UIManager.getColor("Panel.background"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        JPanel batteryContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        batteryContainer.add(batteryBar);
        batteryContainer.setOpaque(false);

        JPanel labelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        labelContainer.add(ratioChargeLabel);
        labelContainer.setOpaque(false);

        if (isFirstMode) {
            progressPanel.add(labelContainer, gbc);
            gbc.gridx++;
            progressPanel.add(batteryContainer, gbc);
        } else {
            progressPanel.add(batteryContainer, gbc);
            gbc.gridx++;
            progressPanel.add(labelContainer, gbc);
        }

        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private static void progressPanelForHorizontalMode(boolean isFirstMode) {
        progressPanel.removeAll();
        progressPanel.setLayout(new GridBagLayout());
        progressPanel.setBackground(UIManager.getColor("Panel.background"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(2, 2, 2, 2);

        JPanel batteryContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        batteryContainer.add(batteryBar);
        batteryContainer.setOpaque(false);

        JPanel labelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        labelContainer.add(ratioChargeLabel);
        labelContainer.setOpaque(false);

        if (isFirstMode) {
            progressPanel.add(batteryContainer, gbc);
            gbc.gridy++;
            progressPanel.add(labelContainer, gbc);
        } else {
            progressPanel.add(labelContainer, gbc);
            gbc.gridy++;
            progressPanel.add(batteryContainer, gbc);
        }
        progressPanel.revalidate();
        progressPanel.repaint();
    }
    
    public static JPanel setupDashboardControlPanel(){
        JPanel mainContainer = new JPanel(new GridLayout(3, 1));
        soundControlPanel = new JPanel(new BorderLayout());
        setupSoundControlPanel();
        soundControlPanel.setVisible(false);
        mainContainer.add(new JLabel(""));
        mainContainer.add(soundControlPanel);
        mainContainer.add(new JLabel(""));
        return mainContainer;
    }
    
    private static void setupSoundControlPanel(){
        JButton muteButton = BattorionButtonHelper.createButton(
                "Mute the alert sound", ICONS_FOLDER_PATH, "mute",
                _ -> {
                    CallCommandLine.setSoundUnmute(1);
                    soundControlPanel.setVisible(false);
                }
        );
        
        JButton soundButton = BattorionButtonHelper.createButton(
                "Adjust the alert sound volume", ICONS_FOLDER_PATH,
                "sound_level", null
        );
        soundButton.addActionListener(_ -> {
            JPopupMenu menu = createSoundMenu((int) SoundVolumeReader.getVolumeLevel());
            menu.show(soundButton, -83, soundButton.getHeight() + 5);
        });
        
        JButton stopSoundButton = BattorionButtonHelper.createButton(
                "Stop the alert sound", ICONS_FOLDER_PATH, "stop",
                _ -> {
                    stopWAV();
                    stopMP3();
                    cleanupAudioSettingsAfterAlert();
                    soundControlPanel.setVisible(false);
                }
        );
        
        RoundedPanel buttonContainer = new RoundedPanel(
                new GridLayout(1, 3), true,
                Color.BLUE, 15, 2, true
        );
        buttonContainer.add(muteButton);
        buttonContainer.add(soundButton);
        buttonContainer.add(stopSoundButton);
        
        soundControlPanel.add(new JLabel("   "), BorderLayout.WEST);
        soundControlPanel.add(buttonContainer, BorderLayout.CENTER);
        soundControlPanel.add(new JLabel("   "), BorderLayout.EAST);
    }
    
    public static JPopupMenu createSoundMenu(int soundLevel) {
        JPopupMenu soundMenu = new JPopupMenu();
        JSlider soundSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, soundLevel);
        JLabel soundLabel = new JLabel(soundLevel + " %");
        soundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        soundSlider.addChangeListener(e -> {
            JSlider source = (JSlider)e.getSource();
            int newValue = source.getValue();
            soundLabel.setText(newValue + " %");
            if (!source.getValueIsAdjusting()) {
                CallCommandLine.setPCVolume(newValue);
                if(pcVolumeSpinner != null){
                    pcVolumeSpinner.setValue(newValue);
                }
            }
        });
        
        soundMenu.add(soundLabel);
        soundMenu.add(Box.createVerticalStrut(5));
        soundMenu.add(soundSlider);
        return soundMenu;
    }
}