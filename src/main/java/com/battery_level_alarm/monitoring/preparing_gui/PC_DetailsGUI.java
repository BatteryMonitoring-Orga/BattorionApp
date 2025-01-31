package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.basics.PC_Details.*;
import static com.battery_level_alarm.monitoring.basics.StaticQuestionnaire.*;
import static com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI.*;

import com.battery_level_alarm.monitoring.basics.PC_Details;
import com.battery_level_alarm.monitoring.buttons_in_combo_box.ButtonsInComboBox;
import com.battery_level_alarm.monitoring.buttons_in_combo_box.SoundItem;
import com.battery_level_alarm.monitoring.core.FileManager;
import com.notifications.system_tray_notifications.basics.AlarmSounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Objects;

public class PC_DetailsGUI {
    public static JPanel createPC$GUI(AlarmSounds alarmSounds){
        JPanel pc$gui = new JPanel(new BorderLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setButtonDefaultSize();
        addButton(gbc, firstPanel, "About", 0, 0, e -> aboutPC$DetailsDispatch());

        JPanel secondPanel = new JPanel(new GridBagLayout());
        int index = 0;
        String switched = "";
        if(getActivateTheAwakeningFeature()){
            switched = "On";
        } else {
            switched = "Off";
        }

        addLabel(gbc, secondPanel, "Activate the awakening feature:", index, 0);
        addToggleButton(gbc, secondPanel, switched, index, 1, 120, 30);
        addCheckbox(gbc, secondPanel, "Enable Exchange To Speaker Audio Output", isEnableExchangeToSpeakerAudioOutput(), ++index, 0,
                e -> {
                    PC_Details.setEnableExchangeToSpeakerAudioOutput(((JCheckBox) e.getSource()).isSelected());
                    FileManager.savePC$Details();
                });
        addLabelWithMouseListener(gbc, secondPanel, "How do I select the audio output?</b></u></html>", ++index, 0);

        addLabeledSpinner(gbc, secondPanel, "Wake up the PC every (in Minutes):", getWakeUpEvery(), 5, 1, 10, 1, ++index, 0,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    setWakeUpEvery(value);
                    FileManager.savePC$Details();
                });
        addLabeledSpinner(gbc, secondPanel, "Set Volume Level (%):", PC_Details.getVolumeLevel(), 35, 20, 100, 1, ++index, 0,
                e -> {
                    int percentage = getSpinnerValue((JSpinner) e.getSource(), 20, 35);
                    PC_Details.setVolumeLevel(percentage);
                    FileManager.savePC$Details();
                });

        addButtonMixWithComboBox(gbc, secondPanel, "System Notification Sounds:", ++index, 0);
        addComboBox(gbc, secondPanel, "Pick Your Notification Sound", getAlarmsArray(), getNotificationSoundFileName(), ++index, 0,
                e -> {
                    if (e.getSource() instanceof JComboBox<?>) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                        String selectedSound = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
                        setNotificationSoundFileName(selectedSound);
                        alarmSounds.setSoundSequenceNumber(comboBox.getSelectedIndex() + 1);
                        FileManager.savePC$Details();
                    }
                });

        pc$gui.add(firstPanel, BorderLayout.NORTH);
        pc$gui.add(secondPanel, BorderLayout.CENTER);
        return pc$gui;
    }

    private static void addToggleButton(GridBagConstraints gbc, JPanel panel, String value, int row, int column, int width, int height){
        JToggleButton toggleButton = new JToggleButton(value);
        toggleButton.setPreferredSize(new Dimension(width, height));
        toggleButton.setFont(new Font("Serif", Font.BOLD, 14));
        toggleButton.setSelected(value.equals("On"));
        setColor(toggleButton);

        toggleButton.addActionListener(e -> {
            setColor(toggleButton);
            FileManager.savePC$Details();
        });

        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(toggleButton, gbc);
    }

    private static void setColor(JToggleButton toggleButton){
        if (toggleButton.isSelected()) {
            toggleButton.setText("On");
            toggleButton.setBackground(new Color(72, 201, 176));
            toggleButton.setForeground(Color.BLACK);
            setActivateTheAwakeningFeature(true);
        } else {
            toggleButton.setText("Off");
            toggleButton.setBackground(Color.DARK_GRAY);
            toggleButton.setForeground(Color.WHITE);
            setActivateTheAwakeningFeature(false);
        }
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
                label.setForeground(java.awt.Color.BLUE);
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                label.setForeground(java.awt.Color.BLACK);
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
        ArrayList<String> data = new ArrayList<String>();
        AlarmSounds alarmSounds_Object = new AlarmSounds(1);

        for(int i = 1; i < 11; i++){
            alarmSounds_Object.setSoundSequenceNumber(i);
            data.add(alarmSounds_Object.getSoundFileName());
        }
        return data.toArray(new String[0]);
    }

    private static void addComboBox(GridBagConstraints gbc, JPanel panel, String text, String[] dataArray, String selectedItem, int row, int column, ItemListener listener){
        JLabel label = new JLabel(text);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(label, gbc);

        JComboBox<String> comboBox = new JComboBox<>(dataArray);
        comboBox.setFont(DEFAULT_FONT);
        comboBox.setSelectedItem(selectedItem);
        comboBox.addItemListener(listener);
        gbc.gridx = ++column;
        panel.add(comboBox, gbc);
    }
}