package com.battery_level_alarm.monitoring.preparing_gui;
import static com.battery_level_alarm.monitoring.basics.PC_Details.*;
import static com.battery_level_alarm.monitoring.basics.StaticQuestionnaire.*;
import static com.battery_level_alarm.monitoring.preparing_gui.SettingsGUI.*;
import com.battery_level_alarm.monitoring.basics.PC_Details;
import com.battery_level_alarm.monitoring.core.BatteryLevelAlarm;
import com.battery_level_alarm.monitoring.core.FileManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PC_DetailsGUI {
    private static JButton showButton;

    public static JPanel createPC$GUI(){
        JPanel pc$gui = new JPanel(new BorderLayout());
        JPanel firstPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();
        int index = 0;
        String switched = "";
        if(getActivateTheAwakeningFeature()){
            switched = "On";
        } else {
            switched = "Off";
        }

        addLabel(gbc, firstPanel, "Activate the awakening feature:", index, 0);
        addToggleButton(gbc, firstPanel, switched, index, 1, 120, 30);
        addCheckbox(gbc, firstPanel, "Enable Exchange To Speaker Audio Output", isEnableExchangeToSpeakerAudioOutput(), ++index, 0,
                e -> {
                    PC_Details.setEnableExchangeToSpeakerAudioOutput(((JCheckBox) e.getSource()).isSelected());
                    FileManager.savePC$Details();
                });
        addLabelWithMouseListener(gbc, firstPanel, "How do I select the audio output?</b></u></html>", ++index, 0);

        addLabel(gbc, firstPanel, "Your PC Password:", ++index, 0);
        JTextField textField = addTextField(gbc, firstPanel, true, index, 1);
        setButtonSize(195, 30);
        showButton = addButton(gbc, firstPanel, "Show", ++index, 1, new ActionListener() {
            boolean isHidden = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textField instanceof JPasswordField passwordField) {
                    if (isHidden) {
                        passwordField.setEchoChar((char) 0);
                        setShowButtonText("Hide");
                    } else {
                        passwordField.setEchoChar('*');
                        setShowButtonText("Show");
                    }
                    isHidden = !isHidden;
                }
            }
        });

        addLabeledSpinner(gbc, firstPanel, "Wake up the PC every (in Minutes):", getWakeUpEvery(), 5, 1, 10, 1, ++index, 0,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 1, 5);
                    setWakeUpEvery(value);
                    FileManager.savePC$Details();
                });
        addLabeledSpinner(gbc, firstPanel, "Set Volume Level (%):", PC_Details.getVolumeLevel(), 35, 20, 100, 1, ++index, 0,
                e -> {
                    int percentage = getSpinnerValue((JSpinner) e.getSource(), 20, 35);
                    PC_Details.setVolumeLevel(percentage);
                    FileManager.savePC$Details();
                });

        JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        setButtonDefaultSize();
        addButton(gbc, secondPanel, "About", 0, 0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aboutPC$DetailsDispatch();
            }
        });

        pc$gui.add(firstPanel, BorderLayout.CENTER);
        pc$gui.add(secondPanel, BorderLayout.SOUTH);
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

    private static void setShowButtonText(String title){
        showButton.setText(title);
    }

    private static JTextField addTextField(GridBagConstraints gbc, JPanel panel, boolean isPasswordField, int row, int column) {
        JTextField textField;
        if (isPasswordField) {
            JPasswordField passwordField = new JPasswordField(getSecretNumber(), 15);
            passwordField.setFont(BatteryLevelAlarm.textFont);
            addDocumentListenerForField(passwordField);
            textField = passwordField;
        } else {
            textField = new JTextField(getSecretNumber(), 15);
            textField.setFont(BatteryLevelAlarm.textFont);
            addDocumentListenerForField(textField);
        }

        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(textField, gbc);
        return textField;
    }

    private static void addDocumentListenerForField(JComponent field) {
        if (field instanceof JTextField textField) {
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    handleTextChange(textField);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    handleTextChange(textField);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    handleTextChange(textField);
                }

                private void handleTextChange(JTextField field) {
                    if (field instanceof JPasswordField passwordField) {
                        setSecretNumber(new String(passwordField.getPassword()));
                    } else {
                        setSecretNumber(field.getText());
                    }
                    FileManager.savePC$Details();
                }
            });
        } else {
            throw new IllegalArgumentException("Unsupported component type: " + field.getClass().getName());
        }
    }
}