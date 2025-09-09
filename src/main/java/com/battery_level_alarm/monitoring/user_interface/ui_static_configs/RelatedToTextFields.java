package com.battery_level_alarm.monitoring.user_interface.ui_static_configs;
import static com.battery_level_alarm.monitoring.system_automation.WakeUpPC.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getRow;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.buttonGroup;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.applyScrollConfigurationDetails;
import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RelatedToTextFields {
    public static JTextField addTextField(
            GridBagConstraints gbc, JPanel panel, String text,
            int width, int height, Border border, boolean isOpaque
    ) {
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

    public static void setPromptFeature(JTextField textField, String promptText, String[] DEVICE_STATUS_MESSAGES) {
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
    ) {
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
                    printErrorMessage(exception);
                    textField.setText(DefaultValue);
                } catch (AWTException ex) {
                    printErrorMessage(ex);
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    
    public static void setMouseListener(
            JTextField textField, Runnable action,
            Color originalColor, Color mouseEnteredColor,
            boolean isFocusable, boolean isEditable, boolean isEnabled
    ) {
        textField.setFocusable(isFocusable);
        textField.setEditable(isEditable);
        textField.setEnabled(isEnabled);
        
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                textField.setForeground(mouseEnteredColor);
                textField.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                textField.setForeground(originalColor);
                textField.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public static void setPopUpMenu(
            JTextField textField, JComponent[] components,
            Font font, boolean isLightWeightPopupEnabled,
            Consumer<Boolean> setRequestFocusInWindowConsumer,
            Supplier<Boolean> isRequestFocusInWindowConsumer
    ) {
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
    ) {
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
    ) {
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
    ) {
        field.setText(label);
        field.setFont(font);
        field.setEditable(isEditable);
        field.setEnabled(isEnabled);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JScrollPane scroll = new JScrollPane(field);
        applyScrollConfigurationDetails(scroll, configuration);
        return scroll;
    }
}