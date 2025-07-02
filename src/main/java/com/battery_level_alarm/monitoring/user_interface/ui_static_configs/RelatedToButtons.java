package com.battery_level_alarm.monitoring.user_interface.ui_static_configs;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getRow;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.DropDownList.updateProgressBars;
import com.battery_level_alarm.monitoring.core_utilities.EffectDirection;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SoundItem;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ToggleButtonRecord;
import com.battery_level_alarm.monitoring.user_interface.ui_config.CompoundUpdaterRecord;
import com.battery_level_alarm.monitoring.system_automation.Updater;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.function.Consumer;

public class RelatedToButtons {
    public static final Font toggleButtonsFont = new Font("Serif", Font.BOLD, 15);
    private static int buttonWidth = 150;
    private static int buttonHeight = 30;
    public static ButtonGroup buttonGroup;

    public static ButtonGroup getGroupOfButtons(
            GridBagConstraints gbc, JPanel panel,
            String[] buttonNames,
            String[] buttonsToolTip,
            ActionListener[] actions,
            int numberOfButtons
    ){
        JPanel groupPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup group = new ButtonGroup();
        for(int i=0; i < numberOfButtons; i++){
            JRadioButton radioButton = new JRadioButton();
            radioButton.setFont(textFieldFont);
            radioButton.setToolTipText(buttonsToolTip[i]);
            radioButton.addActionListener(actions[i]);
            setRadioButtonMouseListener(radioButton, i, buttonNames);
            group.add(radioButton);
            groupPanel.add(radioButton);
        }

        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(groupPanel, gbc);
        return group;
    }

    private static @NotNull JRadioButton getClearButton(ButtonGroup group) {
        JRadioButton clearButton = new JRadioButton();
        clearButton.setFont(textFieldFont);
        clearButton.setToolTipText(
                """
                        This button allows you to clear the selection
                        of all radio buttons in the group.
                        (it will not deselect itself)"""
        );
        clearButton.addActionListener(_ -> clearSelection(group));
        return clearButton;
    }

    private static void setRadioButtonMouseListener(
            JRadioButton radioButton, int index,
            String[] buttonNames
    ){
        radioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                radioButton.setText(buttonNames[index]);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                radioButton.setText("");
            }
        });
    }

    private static void clearSelection(ButtonGroup group) {
        Enumeration<AbstractButton> buttons = group.getElements();
        while (buttons.hasMoreElements()) {
            buttons.nextElement().setSelected(false);
        }
    }

    public static JButton addButton(GridBagConstraints gbc, JPanel secondPartPanel, String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.setFont(DEFAULT_FONT);
        button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);

        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        secondPartPanel.add(button, gbc);
        return button;
    }

    public static void setButtonFontAndSize(
            JButton button, Font font, int width, int height,
            int setHorizontalAlignment, int setHorizontalTextPosition
    ){
        button.setPreferredSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setFont(font);
        button.setHorizontalAlignment(setHorizontalAlignment);
        button.setHorizontalTextPosition(setHorizontalTextPosition);
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

    public static JToggleButton addToggleButton(
            GridBagConstraints gbc, JPanel panel,
            ToggleButtonRecord record,
            CompoundUpdaterRecord updaterRecord
    ){
        JToggleButton toggleButton = new JToggleButton(record.value());
        toggleButton.setPreferredSize(record.dimension());
        toggleButton.setFont(toggleButtonsFont);
        toggleButton.setSelected(record.value().equals("On"));
        setColor(toggleButton, record.stateChangeHandler());
        setToggleButtonAction(toggleButton, record, updaterRecord);
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(toggleButton, gbc);
        return toggleButton;
    }

    private static void setToggleButtonAction(
            JToggleButton toggleButton,
            ToggleButtonRecord record,
            CompoundUpdaterRecord updaterRecord
    ){
        toggleButton.addActionListener(_ -> {
            setColor(toggleButton, record.stateChangeHandler());
            record.saveAction().run();

            if (!updaterRecord.isFromDropDownList()) return;
            updateProgressBars(updaterRecord.progressBarValueUpdater());

            boolean isSelected = toggleButton.isSelected();
            boolean forward = updaterRecord.effectDirection().equals(EffectDirection.FORWARD);
            boolean reverse = updaterRecord.effectDirection().equals(EffectDirection.REVERSE);
            for (JComponent component : updaterRecord.progressBarValueUpdater().components()) {
                component.setVisible(forward ? isSelected : reverse ? !isSelected : component.isVisible());
            }

            if (updaterRecord.isAbleToUseConsumers()) {
                if (forward && updaterRecord.setFlagConsumer() != null) {
                    updaterRecord.setFlagConsumer().accept(!isSelected);
                } else if (reverse && updaterRecord.Supplier() != null && updaterRecord.Supplier().get()) {
                    updaterRecord.Action().run();
                }
            }
            Updater.update(updaterRecord.hierarchy());
        });
    }

    private static void setColor(JToggleButton toggleButton, Consumer<Boolean> stateChangeHandler){
        boolean isOn = toggleButton.isSelected();
        toggleButton.setText(isOn ? "On" : "Off");
        toggleButton.setBackground(isOn ? new Color(72, 201, 176) : Color.DARK_GRAY);
        toggleButton.setForeground(isOn ? Color.BLACK : Color.WHITE);
        stateChangeHandler.accept(isOn);
    }
}