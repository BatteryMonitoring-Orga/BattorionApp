package com.battery_level_alarm.monitoring.gui_static_method_configurations;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.getColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.getRow;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.buttonGroup;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.textFieldFont;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.applyScrollConfigurationDetails;
import com.battery_level_alarm.monitoring.configuration_records.ScrollConfiguration;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;

public class RelatedToTextFields {
    public static JTextField addTextField(GridBagConstraints gbc, JPanel panel, String text){
        JTextField textField = new JTextField(text, 13);
        textField.setFont(textFieldFont);
        textField.setForeground(Color.GRAY);
        gbc.gridy = getRow();
        gbc.gridx = getColumn();
        panel.add(textField, gbc);
        return textField;
    }

    public static void setPromptFeature(JTextField textField, String promptText, String[] DEVICE_STATUS_MESSAGES){
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

    public static void setDocumentListener(
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

    public static void expectedText(
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
                        _ -> textField.setText(comboBox.getItemAt(finalI))
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

    public static JScrollPane addTextInScroll(
            JTextField field, String label, Font font,
            boolean isEditable, boolean isEnabled, ScrollConfiguration configuration
    ){
        field.setText(label);
        field.setFont(font);
        field.setEditable(isEditable);
        field.setEnabled(isEnabled);

        JScrollPane scroll = new JScrollPane(field);
        applyScrollConfigurationDetails(scroll, configuration);
        return scroll;
    }
}