package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.isEnableExchangeToAudioOutputUsed;
import static com.battery_level_alarm.monitoring.basics.StaticQuestionnaire.*;
import static com.battery_level_alarm.monitoring.command.AudioOutput$CMD.setSpeakerAsAnAudioOutput;
import static com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI.*;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.buttons_in_combo_box.ButtonsInComboBox;
import com.battery_level_alarm.monitoring.buttons_in_combo_box.SoundItem;
import com.battery_level_alarm.monitoring.core.FileManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class ComputerSettingsGUI {
    private static final Font toggleButtonsFont = new Font("Serif", Font.BOLD, 15);
    private static final Font textFieldFont = new Font("Serif", Font.PLAIN, 14);
    private static final Color GREEN_COLOR = new Color(0, 150, 0);
    private static final String promptText = "Enter device name";
    private static final String[] DEVICE_STATUS_MESSAGES = {
            "Added successfully",
            "Failed to add",
            "Device removed",
            "Removal failed",
            "Audio output set"
    };
    private static ButtonGroup buttonGroup;

    public static JPanel createComputerSettingsGUI(AlarmSounds alarmSounds){
        JPanel computerSettingsGui = new JPanel(new BorderLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setButtonDefaultSize();
        addButton(gbc, firstPanel, "About", 0, 0, e -> aboutComputerSettingsDispatch());

        int index = 0;
        JPanel secondPanel = new JPanel(new GridBagLayout());
        String switched = isActivateTheAwakeningFeature()? "On":"Off";
        String toSpeaker = isEnableExchangeToSpeakerAudioOutput()? "On":"Off";
        String toUsed = isEnableExchangeToAudioOutputUsed()? "On":"Off";

        addLabel(gbc, secondPanel, "Do these procedures automatically:", index, 0);
        addLabel(gbc, secondPanel, "Activate the awakening feature:", ++index, 0);
        addToggleButton(gbc, secondPanel, ComputerSettings::setActivateTheAwakeningFeature, FileManager::saveComputerSettings, switched, index, 1, 120, 30);
        addLabel(gbc, secondPanel, "Exchange to speaker audio output:", ++index, 0);
        addToggleButton(gbc, secondPanel, ComputerSettings::setEnableExchangeToSpeakerAudioOutput, FileManager::saveComputerSettings, toSpeaker, index, 1, 120, 30);
        addLabel(gbc, secondPanel, "Exchange to audio output used:", ++index, 0);
        addToggleButton(gbc, secondPanel, ComputerSettings::setEnableExchangeToAudioOutputUsed, FileManager::saveComputerSettings, toUsed, index, 1, 120, 30);

        addSeparator(gbc, secondPanel, 100, ++index, 0);
        returnGBC$ToDefault(gbc);
        JComboBox<String> audioDevicesComboBox = addComboBox(
                gbc, secondPanel, "Select the audio device used", getAudioDevices().toArray(new String[0]),
                getCurrentAudioDevice(), 4, ++index, 0, e -> {
                    if (e.getSource() instanceof JComboBox<?>) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                        String selectedSound = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
                        setCurrentAudioDevice(selectedSound);
                        FileManager.saveComputerSettings();
                    }
                });

        addLabel(gbc, secondPanel, "Audio output device name:", ++index, 0);
        JTextField audioDeviceName = addTextField(gbc, secondPanel, promptText, index, 1);
        setPromptFeature(audioDeviceName, promptText, DEVICE_STATUS_MESSAGES);
        setDocumentListener(audioDeviceName, audioDevicesComboBox, promptText, DEVICE_STATUS_MESSAGES);
        buttonGroup = getGroupOfButtons(
                gbc, secondPanel, getButtonNames(),
                getButtonActions(audioDeviceName, audioDevicesComboBox),
                getButtonNames().length, ++index, 1
        );
        addLabelWithMouseListener(gbc, secondPanel, "How do I select the audio output?", ++index, 0);

        addSeparator(gbc, secondPanel, 100, ++index, 0);
        returnGBC$ToDefault(gbc);
        addLabeledSpinner(gbc, secondPanel, "Wake up the PC every (in Minutes):", getWakeUpEvery(), 5, 1, 10, 1, ++index, 0,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    setWakeUpEvery(value);
                    FileManager.saveComputerSettings();
                });
        addLabeledSpinner(gbc, secondPanel, "Set Volume Level (%):", ComputerSettings.getVolumeLevel(), 35, 20, 100, 1, ++index, 0,
                e -> {
                    int percentage = getSpinnerValue((JSpinner) e.getSource(), 20, 35);
                    ComputerSettings.setVolumeLevel(percentage);
                    FileManager.saveComputerSettings();
                });

        addButtonMixWithComboBox(gbc, secondPanel, "System Notification Sounds:", ++index, 0);
        addComboBox(gbc, secondPanel, "Pick Your Notification Sound", getAlarmsArray(), getNotificationSoundFileName(), 5, ++index, 0,
                e -> {
                    if (e.getSource() instanceof JComboBox<?>) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                        String selectedSound = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
                        setNotificationSoundFileName(selectedSound);
                        alarmSounds.setSoundSequenceNumber(comboBox.getSelectedIndex() + 1);
                        FileManager.saveComputerSettings();
                    }
                });

        computerSettingsGui.add(firstPanel, BorderLayout.NORTH);
        computerSettingsGui.add(secondPanel, BorderLayout.CENTER);
        return computerSettingsGui;
    }

    public static void addToggleButton(
            GridBagConstraints gbc, JPanel panel, Consumer<Boolean> stateChangeHandler,
            Runnable saveAction, String value, int row, int column, int width, int height
    ){
        JToggleButton toggleButton = new JToggleButton(value);
        toggleButton.setPreferredSize(new Dimension(width, height));
        toggleButton.setFont(toggleButtonsFont);
        toggleButton.setSelected(value.equals("On"));
        setColor(toggleButton, stateChangeHandler);
        toggleButton.addActionListener(e -> {
            setColor(toggleButton, stateChangeHandler);
            saveAction.run();
        });

        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(toggleButton, gbc);
    }

    private static void setColor(JToggleButton toggleButton, Consumer<Boolean> stateChangeHandler){
        boolean isOn = toggleButton.isSelected();
        toggleButton.setText(isOn ? "On" : "Off");
        toggleButton.setBackground(isOn ? new Color(72, 201, 176) : Color.DARK_GRAY);
        toggleButton.setForeground(isOn ? Color.BLACK : Color.WHITE);
        stateChangeHandler.accept(isOn);
    }

    private static void addLabelWithMouseListener(GridBagConstraints gbc, JPanel panel, String text, int row, int column){
        JLabel label = new JLabel(text);
        label.setText("<html><u><b>" + label.getText() + "</b></u></html>");
        label.setFont(DEFAULT_FONT);
        addMouseListenerToLabel(label);
        gbc.gridy = row;
        gbc.gridx = column;
        panel.add(label, gbc);
    }

    private static void addMouseListenerToLabel(JLabel label) {
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                aboutSoundSettingsGuide();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                label.setForeground(Color.CYAN);
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                label.setForeground(UIManager.getColor("Label.Foreground"));
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private static void addButtonMixWithComboBox(GridBagConstraints gbc, JPanel panel, String text, int row, int column){
        JLabel label = new JLabel(text);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(label, gbc);

        JComboBox<SoundItem> comboBox = ButtonsInComboBox.createModernComboBox();
        gbc.gridx = ++column;
        gbc.gridy = row;
        panel.add(comboBox, gbc);
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

    private static JComboBox<String> addComboBox(
            GridBagConstraints gbc, JPanel panel, String text,
            String[] dataArray, String selectedItem,
            int maximumRowCount, int row, int column,
            ItemListener listener
    ){
        JLabel label = new JLabel(text);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(label, gbc);

        JComboBox<String> comboBox = new JComboBox<>(dataArray);
        comboBox.setFont(DEFAULT_FONT);
        comboBox.setMaximumRowCount(maximumRowCount);
        comboBox.setSelectedItem(selectedItem);
        comboBox.addItemListener(listener);
        gbc.gridx = ++column;
        panel.add(comboBox, gbc);
        return comboBox;
    }

    private static JTextField addTextField(GridBagConstraints gbc, JPanel panel, String text, int row, int column){
        JTextField textField = new JTextField(text, 13);
        textField.setFont(textFieldFont);
        textField.setForeground(Color.GRAY);
        gbc.gridy = row;
        gbc.gridx = column;
        panel.add(textField, gbc);
        return textField;
    }

    private static void setPromptFeature(JTextField textField, String promptText, String[] DEVICE_STATUS_MESSAGES){
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (
                        textField.getText().equals(promptText) ||
                        Arrays.stream(DEVICE_STATUS_MESSAGES).anyMatch(
                                msg -> msg.equals(textField.getText())
                        )
                ){
                    textField.setText("");
                    textField.setForeground(UIManager.getColor("TextField.Foreground"));
                    buttonGroup.clearSelection();
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(promptText);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    private static void setDocumentListener(
            JTextField textField, JComboBox<String> comboBox,
            String promptText, String[] DEVICE_STATUS_MESSAGES
    ){
        JPopupMenu popupMenu = new JPopupMenu();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                expectedText(
                        popupMenu, textField, comboBox,
                        promptText, DEVICE_STATUS_MESSAGES
                );
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                expectedText(
                        popupMenu, textField, comboBox,
                        promptText, DEVICE_STATUS_MESSAGES
                );
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                expectedText(
                        popupMenu, textField, comboBox,
                        promptText, DEVICE_STATUS_MESSAGES
                );
            }


        });
    }

    private static void expectedText(
            JPopupMenu popupMenu, JTextField textField, JComboBox<String> comboBox,
            String promptText, String[] DEVICE_STATUS_MESSAGES
    ){
        popupMenu.setVisible(false);
        boolean hasMatches = false;

        if(
                textField.getText().equals(promptText)
                        || Arrays.stream(DEVICE_STATUS_MESSAGES).anyMatch(
                        msg -> msg.equals(textField.getText())
                ) || textField.getText().isEmpty()
        ){
            return;
        }

        popupMenu.removeAll();
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            String item = comboBox.getItemAt(i);
            if (item.contains(textField.getText())) {
                JMenuItem expectedTextItem = new JMenuItem(item);
                int finalI = i;
                expectedTextItem.addActionListener(
                        e -> textField.setText(comboBox.getItemAt(finalI))
                );
                popupMenu.add(expectedTextItem);
                hasMatches = true;
            }
        }

        if (hasMatches) {
            popupMenu.show(textField, 0, textField.getHeight());
            textField.requestFocusInWindow();
        }
    }

    private static ButtonGroup getGroupOfButtons(
            GridBagConstraints gbc, JPanel panel,
            String[] buttonNames, ActionListener[] actions,
            int numberOfButtons, int row, int column
    ){
        JPanel groupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup group = new ButtonGroup();
        for(int i=0; i < numberOfButtons; i++){
            JRadioButton radioButton = new JRadioButton(buttonNames[i]);
            radioButton.addActionListener(actions[i]);
            group.add(radioButton);
            groupPanel.add(radioButton);
        }

        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(groupPanel, gbc);
        return group;
    }

    private static String[] getButtonNames(){
        return new String[]{"Add", "Delete", "Set as audio output"};
    }

    private static ActionListener[] getButtonActions(JTextField audioDeviceName, JComboBox<String> audioDevicesComboBox){
        return new ActionListener[]{
                e -> {
                    boolean isAdded = ComputerSettings.setItemToAudioList(audioDeviceName.getText());
                    if(isAdded){
                        FileManager.saveComputerSettings();
                        audioDevicesComboBox.addItem(audioDeviceName.getText());
                        audioDeviceName.setText(DEVICE_STATUS_MESSAGES[0]);
                        audioDeviceName.setForeground(GREEN_COLOR);
                    } else {
                        audioDeviceName.setText(DEVICE_STATUS_MESSAGES[1]);
                        audioDeviceName.setForeground(Color.RED);
                    }
                },
                e -> {
                    boolean isDeleted = ComputerSettings.removeItemFromAudioList(audioDeviceName.getText());
                    if (isDeleted) {
                        FileManager.saveComputerSettings();
                        audioDevicesComboBox.removeItem(audioDeviceName.getText());
                        audioDeviceName.setText(DEVICE_STATUS_MESSAGES[2]);
                        audioDeviceName.setForeground(GREEN_COLOR);
                    } else {
                        audioDeviceName.setText(DEVICE_STATUS_MESSAGES[3]);
                        audioDeviceName.setForeground(Color.RED);
                    }
                },
                e -> {
                    setSpeakerAsAnAudioOutput(audioDeviceName.getText());
                    audioDeviceName.setText(DEVICE_STATUS_MESSAGES[4]);
                    audioDeviceName.setForeground(GREEN_COLOR);
                }
        };
    }
}