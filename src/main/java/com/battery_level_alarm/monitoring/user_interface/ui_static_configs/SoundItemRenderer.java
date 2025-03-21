package com.battery_level_alarm.monitoring.user_interface.ui_static_configs;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SoundItem;

import javax.swing.*;
import java.awt.*;

class SoundItemRenderer extends JPanel implements ListCellRenderer<SoundItem> {
    private JLabel label;
    private JButton playButton;

    public SoundItemRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        label = new JLabel();
        label.setFont(ButtonsInComboBox.style);
        playButton = new JButton("▶");

        add(playButton);
        add(label);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends SoundItem> list,
            SoundItem value, int index,
            boolean isSelected,
            boolean cellHasFocus
    ){
        if (value != null) {
            label.setText(value.name());
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}