package com.battery_level_alarm.monitoring.gui_static_method_configurations;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.getColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.getRow;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.updateProgressBars;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.configuration_records.SoundItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class RelatedToButtons {
    public static final Font toggleButtonsFont = new Font("Serif", Font.BOLD, 15);
    private static int buttonWidth = 150;
    private static int buttonHeight = 30;

    public static ButtonGroup buttonGroup;
    public static JSpinner editSpinner;

    public static ButtonGroup getGroupOfButtons(
            GridBagConstraints gbc, JPanel panel,
            String[] buttonNames, ActionListener[] actions,
            int numberOfButtons
    ){
        JPanel groupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup group = new ButtonGroup();
        for(int i=0; i < numberOfButtons; i++){
            JRadioButton radioButton = new JRadioButton(buttonNames[i]);
            radioButton.setFont(textFieldFont);
            radioButton.addActionListener(actions[i]);
            group.add(radioButton);
            groupPanel.add(radioButton);
        }

        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(groupPanel, gbc);
        return group;
    }

    public static void addButton(GridBagConstraints gbc, JPanel secondPartPanel, String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.setFont(DEFAULT_FONT);
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.addActionListener(listener);
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        secondPartPanel.add(button, gbc);
    }

    public static void setButtonFontAndSize(JButton button, int width, int height) {
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
    }

    public static void setButtonSize(int buttonWidth, int buttonHeight){
        RelatedToButtons.buttonWidth = buttonWidth;
        RelatedToButtons.buttonHeight = buttonHeight;
    }

    public static void setButtonDefaultSize(){
        RelatedToButtons.buttonWidth = 150;
        RelatedToButtons.buttonHeight = 30;
    }

    public static void addButtonMixWithComboBox(GridBagConstraints gbc, JPanel panel, String text){
        JLabel label = new JLabel(text);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(label, gbc);

        JComboBox<SoundItem> comboBox = ButtonsInComboBox.createModernComboBox();
        gbc.gridx = getColumn() + 1;
        panel.add(comboBox, gbc);
    }

    public static void addToggleButton(
            GridBagConstraints gbc, JPanel panel, Consumer<Boolean> stateChangeHandler,
            Runnable saveAction, String value, int width, int height,
            ProgressBarValueUpdater progressBarValueUpdater, boolean isFromDropDownList
    ){
        JToggleButton toggleButton = new JToggleButton(value);
        toggleButton.setPreferredSize(new Dimension(width, height));
        toggleButton.setFont(toggleButtonsFont);
        toggleButton.setSelected(value.equals("On"));
        setColor(toggleButton, stateChangeHandler);
        toggleButton.addActionListener(_ -> {
            setColor(toggleButton, stateChangeHandler);
            saveAction.run();
            if(isFromDropDownList){
                updateProgressBars(progressBarValueUpdater);
                for(JSpinner spinner : progressBarValueUpdater.spinner()){
                    spinner.setEnabled(toggleButton.isSelected());
                }
            }
        });

        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(toggleButton, gbc);
    }

    private static void setColor(JToggleButton toggleButton, Consumer<Boolean> stateChangeHandler){
        boolean isOn = toggleButton.isSelected();
        toggleButton.setText(isOn ? "On" : "Off");
        toggleButton.setBackground(isOn ? new Color(72, 201, 176) : Color.DARK_GRAY);
        toggleButton.setForeground(isOn ? Color.BLACK : Color.WHITE);
        stateChangeHandler.accept(isOn);
    }
}