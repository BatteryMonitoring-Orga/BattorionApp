package com.battery_level_alarm.monitoring.system_core;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionMainProcessHelper.cleanup;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor.backgroundProcessMonitoring;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.AS_SYSTEM;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.ComputerSettingsGUI.pcVolumeSpinner;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.ICONS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.UIThemesGUI.customizationGradientBackground;
import static com.battery_level_alarm.monitoring.visual_effects.AlertSound.*;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientThemes.*;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.*;

import com.battery_level_alarm.monitoring.command_executors.CallCommandLine;
import com.battery_level_alarm.monitoring.command_executors.SoundVolumeReader;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedButton;
import com.battery_level_alarm.monitoring.visual_effects.gradient.RoundedPanel;
import org.jetbrains.annotations.NotNull;

import javafx.application.Platform;
import javafx.stage.Stage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    
    public static void setupSaverModePanel() {
        saverModePanel = new JPanel();
        saverModePanel = applyGradientBackground(saverModePanel, isDarkMode, true, 25, false);
        saverModePanel.setPreferredSize(new Dimension(180, 140));
        saverModePanel.setMaximumSize(new Dimension(180, 140));
        saverModePanel.setLayout(new BoxLayout(saverModePanel, BoxLayout.Y_AXIS));
        saverModePanel.setOpaque(false);
        
        JLabel infoLabel = createSaverModePanelLabel();
        JButton toggleButton = createSaverModePanelButton();
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        saverModePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        saverModePanel.add(infoLabel);
        saverModePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        saverModePanel.add(toggleButton);
    }
    
    private static JLabel createSaverModePanelLabel() {
        JLabel label = new JLabel("Power Saver Mode", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 14));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setOpaque(false);
        
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextArea textArea = getTextArea();
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(320, 160));
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                scrollPane.getViewport().setOpaque(false);
                scrollPane.setOpaque(false);
                
                JOptionPane.showMessageDialog(
                        saverModePanel,
                        scrollPane,
                        "Power Saver Mode Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            
            private static @NotNull JTextArea getTextArea() {
                JTextArea textArea = new JTextArea("""
                    In Power Saver Mode, the application will minimize to the system tray\s
                    and continue running silently in the background.
        
                    This mode is ideal for battery monitoring without keeping the main window open,\s
                    helping to reduce resource usage and power consumption.
        
                    Use the button below to activate or deactivate Power Saver Mode.
                """);
                
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setEditable(false);
                textArea.setFocusable(false);
                textArea.setOpaque(false);
                textArea.setFont(new Font("Serif", Font.PLAIN, 14));
                return textArea;
            }
        });
        return label;
    }
    
    private static JButton createSaverModePanelButton() {
        Color backgroundColor;
        if(!customizationGradientBackground) {
            if(isDarkMode) {
                String dark = getGradientBackgroundDarkModeName();
                backgroundColor = DARK_GRADIENTS.get(dark)[0];
            } else {
                String light = getGradientBackgroundLightModeName();
                backgroundColor = LIGHT_GRADIENTS.get(light)[0];
            }
        } else {
            backgroundColor = getStartCustomColor();
        }
	    return getButton(backgroundColor);
    }
    
    private static @NotNull JButton getButton(Color backgroundColor) {
        Dimension dimension = new Dimension(140, 30);
        JButton button = new RoundedButton("Run in Background", dimension, 1.5f, 30);
        button.setFont(new Font("Serif", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(backgroundColor);
        button.setPreferredSize(dimension);
        button.setMinimumSize(dimension);
        button.setMaximumSize(dimension);
        
        button.addActionListener(_ -> {
            boolean isFirstTime = Boolean.parseBoolean(prefs.get("IsFirstTimeRunningInBackground", String.valueOf(true)));
            if (isFirstTime) {
                final boolean[] result = {false};
                try {
                    java.util.concurrent.FutureTask<Boolean> future = new java.util.concurrent.FutureTask<>(BattorionMainProcessHelper::showTrayModeConfirmationDialog);
                    Platform.runLater(future);
                    result[0] = future.get();
                } catch (Exception ex) {
                    logger.severe("[EXCEPTION]: " + ex.getMessage());
                    return;
                } if (!result[0]) {
                    return;
                }
            }
            
            mainFrame.dispose();
            cleanup(true);
            Platform.setImplicitExit(false);
            Platform.runLater(() -> {
                new BattorionTrayUI().start(new Stage());
                backgroundProcessMonitoring(prefs.get("appTheme", String.valueOf(AS_SYSTEM)));
            });
        });
        return button;
    }
    
    public static JPanel setupDashboardControlPanel(){
        JPanel mainContainer = new JPanel(new GridLayout(3, 1));
        soundControlPanel = new JPanel(new BorderLayout());
        setupSoundControlPanel();
        soundControlPanel.setVisible(false);
        mainContainer.add(soundControlPanel);
        mainContainer.add(new JLabel(""));
        mainContainer.add(new JLabel(""));
        return mainContainer;
    }
    
    private static void setupSoundControlPanel(){
        JButton muteButton = BattorionButtonsHelper.createButton(
                "Mute the alert sound", ICONS_FOLDER_PATH, "mute",
                _ -> {
                    CallCommandLine.setSoundUnmute(1);
                    soundControlPanel.setVisible(false);
                }
        );
        
        JButton soundButton = BattorionButtonsHelper.createButton(
                "Adjust the alert sound volume", ICONS_FOLDER_PATH,
                "sound_level", null
        );
        soundButton.addActionListener(_ -> {
            JPopupMenu menu = createSoundMenu((int) SoundVolumeReader.getVolumeLevel());
            menu.show(soundButton, -83, soundButton.getHeight() + 5);
        });
        
        JButton stopSoundButton = BattorionButtonsHelper.createButton(
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