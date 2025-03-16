package com.battery_level_alarm.monitoring.gui_static_method_configurations;
import static com.battery_level_alarm.monitoring.cybernate.WakeUpPC.*;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.getColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.getRow;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.buttonGroup;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.textFieldFont;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.applyScrollConfigurationDetails;
import com.battery_level_alarm.monitoring.configuration_records.ScrollConfiguration;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RelatedToTextFields {
    public static JTextField addTextField(
            GridBagConstraints gbc, JPanel panel, String text,
            int width, int height, Border border, boolean isOpaque
    ){
        JTextField textField = new JTextField(text);
        textField.setPreferredSize(new Dimension(width, height));
        textField.setFont(textFieldFont);
        textField.setForeground(Color.GRAY);
        textField.setBorder(border);
        textField.setOpaque(isOpaque);
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

    public static void setActionListener(
            JTextField textField, String DefaultValue, boolean isMouseMovementEnabled,
            Runnable[] action, Consumer<Boolean> setRequestFocusInWindowConsumer
    ){
        final Point[] mousePosition = new Point[1];
        textField.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    for (Runnable runnable : action) {
                        runnable.run();
                    }
                    setRequestFocusInWindowConsumer.accept(false);
                    textField.getParent().requestFocusInWindow();
                    if(isMouseMovementEnabled){
                        mousePosition[0] = getMousePosition();
                        doRobotAction(new java.awt.Robot(), mousePosition[0], true, getShiftInY_axis(), getShiftInX_axis());
                    }
                } catch (NumberFormatException exception){
                    textField.setText(DefaultValue);
                } catch (AWTException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public static void setPopUpMenu(
            JTextField textField, JComponent[] components,
            Font font, boolean isLightWeightPopupEnabled,
            Consumer<Boolean> setRequestFocusInWindowConsumer,
            Supplier<Boolean> isRequestFocusInWindowConsumer
    ){
        JPopupMenu popupMenu = new JPopupMenu();
        for (JComponent component : components) {
            if (component instanceof JLabel label) {
                label.setFont(font);
                popupMenu.add(label);
            }
        }
        popupMenu.setInvoker(textField);
        popupMenu.setLightWeightPopupEnabled(isLightWeightPopupEnabled);

        textField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                popupMenu.show(textField, 0, textField.getHeight());
                if(isRequestFocusInWindowConsumer.get()){
                    textField.requestFocusInWindow();
                    setRequestFocusInWindowConsumer.accept(true);
                } else {
                    textField.getParent().requestFocusInWindow();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                popupMenu.show(textField, 0, textField.getHeight());
                setRequestFocusInWindowConsumer.accept(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setRequestFocusInWindowConsumer.accept(false);
                popupMenu.setVisible(false);
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