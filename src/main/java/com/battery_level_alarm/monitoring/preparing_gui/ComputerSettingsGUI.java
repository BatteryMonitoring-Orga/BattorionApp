package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.*;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.command.AudioOutput$CMD.setSpeakerAsAnAudioOutput;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToTextFields.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.prepareCheckLists;

import com.battery_level_alarm.monitoring.configuration_records.*;
import com.battery_level_alarm.monitoring.basics.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.command.CallCommandLine;
import com.battery_level_alarm.monitoring.command.SoundVolumeReader;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner;

import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;

public class ComputerSettingsGUI {
    private static final Color GREEN_COLOR = new Color(0, 150, 0);
    private static final String promptText = "Enter device name";
    private static final String[] DEVICE_STATUS_MESSAGES = {
            "Added successfully",
            "Failed to add",
            "Device removed",
            "Removal failed",
            "Audio output set"
    };
    private static final ScrollConfiguration SCROLL_TEXT_FIELD_CONFIGURATION = new ScrollConfiguration(
            false,
            true,
            false,
            false,
            null,
            new Dimension(120, 40)
    );
    public static final JTextField outputDeviceName = new JTextField();

    public static JPanel createComputerSettingsGUI(AlarmSounds alarmSounds){
        JPanel computerSettingsGui = new JPanel();
        computerSettingsGui.setLayout(new BoxLayout(computerSettingsGui, BoxLayout.Y_AXIS));
        GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);
        JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        setButtonDefaultSize();
        setDimension(0, 0);
        addLabelWithMouseListener(
                gbc, firstPanel, "About", Color.PINK,
                StaticQuestionnaire::aboutComputerSettingsDispatch
        );

        DropDownList.borderForegroundColor = UIManager.getColor("Label.foreground");
        JPanel secondPanel = prepareCheckLists(gbc);

        int index = 0;
        JPanel thirdPanel = new JPanel(new GridBagLayout());
        setDimension(++index, 0);
        addSeparator(gbc, thirdPanel, 100);
        returnGBC$ToDefault(gbc);
        setDimension(++index, 0);
        addLabel(gbc, thirdPanel, "Active audio output device:", DEFAULT_FONT);
        outputDeviceName.setBackground(UIManager.getColor("TextField.background"));
        outputDeviceName.setForeground(UIManager.getColor("TextField.foreground"));
        JScrollPane textInScroll = addTextInScroll(
                outputDeviceName, getCurrentAudioDevice(), textFieldFont,
                false, false, SCROLL_TEXT_FIELD_CONFIGURATION
        );
        gbc.gridx = getColumn() + 1;
        thirdPanel.add(textInScroll, gbc);

        setDimension(++index, 0);
        JComboBox<String> audioDevicesComboBox = addComboBox(
                gbc, thirdPanel, "Select the audio device used", getAudioDevices().toArray(new String[0]),
                getCurrentAudioDevice(), 4, e -> {
                    if (e.getSource() instanceof JComboBox<?>) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                        String selectedSound = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
                        setCurrentAudioDevice(selectedSound);
                        ConfigurationFilesManager.saveComputerSettings();
                    }
                }, 160, 30);

        setDimension(++index, 0);
        addLabel(gbc, thirdPanel, "Audio output device name:", DEFAULT_FONT);
        setColumn(1);
        JTextField audioDeviceName = addTextField(gbc, thirdPanel, promptText);
        setPromptFeature(audioDeviceName, promptText, DEVICE_STATUS_MESSAGES);
        setDocumentListener(audioDeviceName, audioDevicesComboBox, promptText, DEVICE_STATUS_MESSAGES);
        setDimension(++index, 1);
        buttonGroup = getGroupOfButtons(
                gbc, thirdPanel, getButtonNames(),
                getButtonActions(audioDeviceName, audioDevicesComboBox),
                getButtonNames().length
        );
        setDimension(++index, 0);
        addLabelWithMouseListener(
                gbc, thirdPanel, "How do I select the audio output?",
                Color.CYAN, StaticQuestionnaire::aboutSoundSettingsGuide
        );

        setDimension(++index, 0);
        addSeparator(gbc, thirdPanel, 100);
        returnGBC$ToDefault(gbc);
        SpinnerConfig wakeUpConfig = new SpinnerConfig(
                "Wake up the PC every (in Minutes):",
                getWakeUpEvery(), 5, 1, 10, 1,
                ++index, 0, 180, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    setWakeUpEvery(value);
                    ConfigurationFilesManager.saveComputerSettings();
                }
        );
        JSpinner wakeUpConfigSpinner = RelatedToSpinner.createSpinner(wakeUpConfig, false);
        addLabeledSpinner(gbc, thirdPanel, wakeUpConfig, wakeUpConfigSpinner);

        int initialValue = (int) SoundVolumeReader.getVolumeLevel();
        int stepSize = (initialValue % 2 == 0) ? 2 : 1;
        setGridBagConstraintsInsets(gbc, new InsetsRecord(10, 10, 0, 10), true);
        SpinnerConfig pcVolumeConfig = new SpinnerConfig(
                "Set PC Volume Level (%):", initialValue,
                35, 0, 100, stepSize, ++index, 0, 180, 30,
                e -> {
                    int percentage = getSpinnerValue((JSpinner) e.getSource(), 20, 35);
                    CallCommandLine.setPCVolume(percentage);
                }
        );
        JSpinner pcVolumeSpinner = RelatedToSpinner.createSpinner(pcVolumeConfig, true);
        addLabeledSpinner(gbc, thirdPanel, pcVolumeConfig, pcVolumeSpinner);

        setGridBagConstraintsInsets(gbc, new InsetsRecord(0, 10, 10, 10), true);
        setDimension(++index, 1);
        addLabel(gbc, thirdPanel, "  Use the spinner buttons only", DEFAULT_FONT);
        setGridBagConstraintsInsets(gbc, null, false);

        SpinnerConfig volumeConfig = new SpinnerConfig(
                "Set Alert Volume Level (%):", ComputerSettings.getVolumeLevel(),
                35, 20, 100, 1, ++index, 0, 180, 30,
                e -> {
                    int percentage = getSpinnerValue((JSpinner) e.getSource(), 20, 35);
                    ComputerSettings.setVolumeLevel(percentage);
                    ConfigurationFilesManager.saveComputerSettings();
                }
        );
        JSpinner volumeSpinner = RelatedToSpinner.createSpinner(volumeConfig, false);
        addLabeledSpinner(gbc, thirdPanel, volumeConfig, volumeSpinner);
        setDimension(++index, 0);
        addButtonMixWithComboBox(gbc, thirdPanel, "System Notification Sounds:");
        setRow(++index);
        addComboBox(gbc, thirdPanel, "Pick Your Notification Sound", getAlarmsArray(), getNotificationSoundFileName(), 5,
                e -> {
                    if (e.getSource() instanceof JComboBox<?>) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                        String selectedSound = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
                        setNotificationSoundFileName(selectedSound);
                        alarmSounds.setSoundSequenceNumber(comboBox.getSelectedIndex() + 1);
                        ConfigurationFilesManager.saveComputerSettings();
                    }
                }, 180, 40);

        computerSettingsGui.add(firstPanel);
        computerSettingsGui.add(secondPanel);
        computerSettingsGui.add(thirdPanel);
        return computerSettingsGui;
    }

    private static String[] getAlarmsArray(){
        ArrayList<String> data = new ArrayList<>();
        AlarmSounds alarmSounds_Object = new AlarmSounds(1);

        for(int i = 1; i < 11; i++){
            alarmSounds_Object.setSoundSequenceNumber(i);
            data.add(alarmSounds_Object.getSoundFileName());
        }
        return data.toArray(new String[0]);
    }

    private static String[] getButtonNames(){
        return new String[]{"Add", "Delete", "Set as audio output"};
    }
    private static ActionListener[] getButtonActions(JTextField audioDeviceName, JComboBox<String> audioDevicesComboBox){
        return new ActionListener[]{
                _ -> {
                    boolean isAdded = ComputerSettings.setItemToAudioList(audioDeviceName.getText());
                    if(isAdded){
                        ConfigurationFilesManager.saveComputerSettings();
                        audioDevicesComboBox.addItem(audioDeviceName.getText());
                        audioDeviceName.setText(DEVICE_STATUS_MESSAGES[0]);
                        audioDeviceName.setForeground(GREEN_COLOR);
                    } else {
                        audioDeviceName.setText(DEVICE_STATUS_MESSAGES[1]);
                        audioDeviceName.setForeground(Color.RED);
                    }
                },
                _ -> {
                    boolean isDeleted = ComputerSettings.removeItemFromAudioList(audioDeviceName.getText());
                    if (isDeleted) {
                        ConfigurationFilesManager.saveComputerSettings();
                        audioDevicesComboBox.removeItem(audioDeviceName.getText());
                        audioDeviceName.setText(DEVICE_STATUS_MESSAGES[2]);
                        audioDeviceName.setForeground(GREEN_COLOR);
                    } else {
                        audioDeviceName.setText(DEVICE_STATUS_MESSAGES[3]);
                        audioDeviceName.setForeground(Color.RED);
                    }
                },
                _ -> {
                    setSpeakerAsAnAudioOutput(audioDeviceName.getText());
                    outputDeviceName.setText(audioDeviceName.getText());
                    audioDeviceName.setText(DEVICE_STATUS_MESSAGES[4]);
                    audioDeviceName.setForeground(GREEN_COLOR);
                }
        };
    }
}