package com.battery_level_alarm.monitoring.user_interface.ui_static_configs;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getRow;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.DEFAULT_FONT;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class OtherComponentsConfig {
    public static JSlider addLabeledSlider(
            GridBagConstraints gbc, JPanel panel, String label,
            int minValue, int maxValue, int initialValue,
            int majorTickSpacing, int minorTickSpacing,
            int orientation, ChangeListener listener
    ){
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(DEFAULT_FONT);
        gbc.gridy = getRow();
        gbc.gridx = getColumn();
        panel.add(jLabel, gbc);

        JSlider slider = new JSlider(minValue, maxValue, initialValue);
        slider.setMajorTickSpacing(majorTickSpacing);
        slider.setMinorTickSpacing(minorTickSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setOrientation(orientation);
        slider.addChangeListener(listener);
        gbc.gridx = getColumn() + 1;
        panel.add(slider, gbc);
        return slider;
    }

    public static JComboBox<String> addLabeledComboBox(
            GridBagConstraints gbc, JPanel panel, String text,
            String[] dataArray, String selectedItem, int maximumRowCount,
            ItemListener listener, int width, int height
    ){
        JLabel label = new JLabel(text);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(label, gbc);

        JComboBox<String> comboBox = new JComboBox<>(dataArray);
        comboBox.setFont(DEFAULT_FONT);
        comboBox.setMaximumRowCount(maximumRowCount);
        comboBox.setSelectedItem(selectedItem);
        comboBox.addItemListener(listener);
        comboBox.setMaximumSize(new Dimension(width, height));
        comboBox.setPreferredSize(new Dimension(width, height));
        gbc.gridx = getColumn() + 1;
        panel.add(comboBox, gbc);
        return comboBox;
    }

    public static JButton addLabeledColorPicker(
            GridBagConstraints gbc, JPanel panel, String labelText,
            Color initialColor, Consumer<Color> onColorSelected, int width, int height
    ) {
        JLabel label = new JLabel(labelText);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(label, gbc);

        JButton colorButton = new JButton();
        colorButton.setBackground(initialColor);
        colorButton.setPreferredSize(new Dimension(width, height));
        colorButton.setMaximumSize(new Dimension(width, height));
        colorButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorButton.addActionListener(_ -> {
            Color chosenColor = JColorChooser.showDialog(null, "Choose Color", colorButton.getBackground());
            if (chosenColor != null) {
                colorButton.setBackground(chosenColor);
                onColorSelected.accept(chosenColor);
            }
        });
        gbc.gridx = getColumn() + 1;
        panel.add(colorButton, gbc);
        return colorButton;
    }

    public static JCheckBox addCheckbox(
            GridBagConstraints gbc, JPanel panel, String label,
            boolean isSelected, ActionListener listener
    ){
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.setFont(DEFAULT_FONT);
        checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkBox.setSelected(isSelected);
        checkBox.addActionListener(listener);
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(checkBox, gbc);
        return checkBox;
    }

    public static void addSeparator(GridBagConstraints gbc, JPanel panel, int height){
        JSeparator horizontalLine = new JSeparator();
        horizontalLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        horizontalLine.setForeground(Appearance.getBorderColor());
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(horizontalLine, gbc);
    }

    public static void applyScrollConfigurationDetails(JScrollPane scroll, ScrollConfiguration configuration){
        scroll.setBorder(configuration.scrollBorder());
        scroll.setFocusable(configuration.isFocusable());
        scroll.setOpaque(configuration.isOpaque());
        scroll.setEnabled(configuration.isEnabled());
        scroll.setMaximumSize(configuration.scrollSize());
        scroll.setPreferredSize(configuration.scrollSize());
        scroll.addMouseWheelListener(InputEvent::consume);
    }
}