package com.battery_level_alarm.monitoring.user_interface.ui_static_configs;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getRow;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.DEFAULT_FONT;

import javax.swing.*;
import java.awt.*;

public class RelatedToLabels {
    public static JLabel addLabel(
            GridBagConstraints gbc, JPanel panel,
            String label, Font font
    ){
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(font);
        //jLabel.setToolTipText(prompt);
        gbc.gridy = getRow();
        gbc.gridx = getColumn();
        panel.add(jLabel, gbc);
        return jLabel;
    }

    public static void addLabelWithMouseListener(
            GridBagConstraints gbc, JPanel panel, String text,
            Color highlightedColor, Runnable action
    ){
        JLabel label = new JLabel(text);
        label.setText("<html><u><b>" + label.getText() + "</b></u></html>");
        label.setFont(DEFAULT_FONT);
        addMouseListenerToLabel(label, highlightedColor, action);
        gbc.gridy = getRow();
        gbc.gridx = getColumn();
        panel.add(label, gbc);
    }

    public static void addMouseListenerToLabel(JLabel label, Color enteredColor, Runnable action) {
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                label.setForeground(enteredColor);
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                label.setForeground(UIManager.getColor("Label.Foreground"));
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
}