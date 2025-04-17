package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.*;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.*;
import static com.battery_level_alarm.monitoring.command_executors.AudioOutput$CMD.setAudioOutputDevice;
import static com.battery_level_alarm.monitoring.system_core.Battorion.audioOutputDeviceDashTextField;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.DropDownList.prepareListsContainer;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.*;

import com.battery_level_alarm.monitoring.core_utilities.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.command_executors.CallCommandLine;
import com.battery_level_alarm.monitoring.command_executors.SoundVolumeReader;
import com.battery_level_alarm.monitoring.user_interface.ui_config.*;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ComputerSettingsGUI {
    public static final Font TITLE_LISTS_FONT = new Font("Serif", Font.BOLD, 15);
    public static final Font LABELS_FONT = new Font("Serif", Font.BOLD, 15);
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
    private static final ScrollConfiguration PC_SET_SCROLL_CONFIGURATION = new ScrollConfiguration(
            false,
            true,
            true,
            false,
            null,
            new Dimension(600, 350)
    );
    public static final JPanel[] COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY = {
            new JPanel(), new JPanel(), new JPanel(), new JPanel()
    };

    public static JPanel dropDownListsContainer = new JPanel();
    public static DropDownListsContainerRecord containerOfSingleRecords;
    public static final JTextField activeAudioDeviceName = new JTextField();
    public static JSpinner pcVolumeSpinner;

    public static JScrollPane createComputerSettingsGUI(AlarmSounds alarmSounds){
        JPanel computerSettingsGui = new JPanel();
        computerSettingsGui.setLayout(new BoxLayout(computerSettingsGui, BoxLayout.Y_AXIS));
        GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);
        JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        setButtonDefaultSize();
        setDimension(0, 0);

        Map<String, Runnable> actionsMap = new HashMap<>();
        actionsMap.put("action:openSystemTrayNotification", StaticQuestionnaire::aboutSystemTrayNotification);
        actionsMap.put("action:openNotificationSound", StaticQuestionnaire::aboutPlaySounds);
        addLabelWithMouseListener(
                gbc, firstPanel, "About", new Color(0, 134, 179),
                () -> StaticQuestionnaire.aboutEditorPanelDispatch(
                        "About Computer Settings Panel",
                        StaticQuestionnaire.getComputerSettingsEditorText(),
                        actionsMap, 500, 300
                ), DEFAULT_FONT
        );

        createComputerSettingsDropDownListConfigurations(gbc);
        containerOfSingleRecords = new DropDownListsContainerRecord(
                "   Do these procedures automatically:",
                TITLE_LISTS_FONT,
                computerSettingsContainerListShadow,
                COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY,
                new SingleDropDownListRecord[]{
                        COMPUTER_SETTINGS_FIRST_DDL,
                        COMPUTER_SETTINGS_SECOND_DDL,
                        COMPUTER_SETTINGS_THIRD_DDL,
                        COMPUTER_SETTINGS_FOURTH_DDL,
                }
        );
        dropDownListsContainer = prepareListsContainer(containerOfSingleRecords);

        int index = 0;
        JPanel thirdPanel = new JPanel(new GridBagLayout());
        setDimension(++index, 0);
        addSeparator(gbc, thirdPanel, 100);
        returnGBC$ToDefault(gbc);
        setDimension(++index, 0);
        addLabel(gbc, thirdPanel, "Active audio output device:", DEFAULT_FONT);
        activeAudioDeviceName.setBackground(UIManager.getColor("TextField.background"));
        activeAudioDeviceName.setForeground(UIManager.getColor("TextField.foreground"));
        JScrollPane textInScroll = addTextInScroll(
                activeAudioDeviceName, getCurrentAudioDevice(), textFieldFont,
                false, false, SCROLL_TEXT_FIELD_CONFIGURATION
        );
        gbc.gridx = getColumn() + 1;
        thirdPanel.add(textInScroll, gbc);

        setDimension(++index, 0);
        JComboBox<String> audioDevicesComboBox = addLabeledComboBox(
                gbc, thirdPanel, "Select the audio device used", getAudioDevices().toArray(new String[0]),
                getCurrentAudioDevice(), 4, e -> {
                    if (e.getSource() instanceof JComboBox<?>) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                        String selectedSound = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
                        setCurrentAudioDevice(selectedSound);
                        activeAudioDeviceName.setText(selectedSound);
                        audioOutputDeviceDashTextField.setText(selectedSound);
                        ConfigurationFilesManager.saveComputerSettings();
                    }
                }, 160, 30);

        setDimension(++index, 0);
        addLabel(gbc, thirdPanel, "Acoustic Output Device Procedure Field:", DEFAULT_FONT);
        setColumn(1);
        JTextField audioDevicesActionField = addTextField(
                gbc, thirdPanel, promptText, 150, 38,
                UIManager.getBorder("TextField.border")
                , false
        );
        setPromptFeature(audioDevicesActionField, promptText, DEVICE_STATUS_MESSAGES);
        setDocumentListener(audioDevicesActionField, audioDevicesComboBox, promptText, DEVICE_STATUS_MESSAGES);
        setDimension(++index, 1);
        buttonGroup = getGroupOfButtons(
                gbc, thirdPanel, getButtonNames(), getButtonToolTip(),
                getButtonActions(audioDevicesActionField, audioDevicesComboBox),
                getButtonNames().length
        );
        setDimension(++index, 0);
        addLabelWithMouseListener(
                gbc, thirdPanel, "How do I select the audio output?",
                new Color(0, 134, 179), StaticQuestionnaire::aboutSoundSettingsGuide, DEFAULT_FONT
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
        addLabeledSpinner(gbc, thirdPanel, wakeUpConfig, wakeUpConfigSpinner, false);

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
        pcVolumeSpinner = RelatedToSpinner.createSpinner(pcVolumeConfig, true);
        addLabeledSpinner(gbc, thirdPanel, pcVolumeConfig, pcVolumeSpinner, false);

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
        addLabeledSpinner(gbc, thirdPanel, volumeConfig, volumeSpinner, false);
        setDimension(++index, 0);
        addButtonMixWithComboBox(gbc, thirdPanel, "System Notification Sounds:");
        setRow(++index);
        addLabeledComboBox(gbc, thirdPanel, "Pick Your Notification Sound", getAlarmsArray(), getNotificationSoundFileName(), 5,
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
        computerSettingsGui.add(dropDownListsContainer);
        computerSettingsGui.add(thirdPanel);
        JScrollPane scrollPaneContainer = new JScrollPane(computerSettingsGui);
        applyScrollConfigurationDetails(scrollPaneContainer, PC_SET_SCROLL_CONFIGURATION);
        return scrollPaneContainer;
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
        return new String[]{"Use the selected AO", "Add", "Delete", "Set it as AO"};
    }
    private static String[] getButtonToolTip() {
        return new String[]{
                "Use the selected audio output",
                "Add a new audio output",
                "Delete the selected audio output",
                "Set it as the audio output"
        };
    }
    private static ActionListener[] getButtonActions(
            JTextField audioDevicesActionField,
            JComboBox<String> audioDevicesComboBox
    ){
        return new ActionListener[]{
                _ -> audioDevicesActionField.setText(getCurrentAudioDevice()),
                _ -> {
                    boolean isAdded = ComputerSettings.addItemToAudioList(audioDevicesActionField.getText());
                    if(isAdded){
                        ConfigurationFilesManager.saveComputerSettings();
                        audioDevicesComboBox.addItem(audioDevicesActionField.getText());
                        audioDevicesActionField.setText(DEVICE_STATUS_MESSAGES[0]);
                        audioDevicesActionField.setForeground(GREEN_COLOR);
                    } else {
                        audioDevicesActionField.setText(DEVICE_STATUS_MESSAGES[1]);
                        audioDevicesActionField.setForeground(Color.RED);
                    }
                }, _ -> {
                    boolean isDeleted = ComputerSettings.removeItemFromAudioList(audioDevicesActionField.getText());
                    if (isDeleted) {
                        ConfigurationFilesManager.saveComputerSettings();
                        audioDevicesComboBox.removeItem(audioDevicesActionField.getText());
                        audioDevicesActionField.setText(DEVICE_STATUS_MESSAGES[2]);
                        audioDevicesActionField.setForeground(GREEN_COLOR);
                    } else {
                        audioDevicesActionField.setText(DEVICE_STATUS_MESSAGES[3]);
                        audioDevicesActionField.setForeground(Color.RED);
                    }
                }, _ -> {
                    setAudioOutputDevice(audioDevicesActionField.getText());
                    activeAudioDeviceName.setText(audioDevicesActionField.getText());
                    audioOutputDeviceDashTextField.setText(audioDevicesActionField.getText());
                    audioDevicesActionField.setText(DEVICE_STATUS_MESSAGES[4]);
                    audioDevicesActionField.setForeground(GREEN_COLOR);
                }
        };
    }
}