package com.battery_level_alarm.monitoring.user_interface.ui_static_configs;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SoundItem;
import com.notifications.system_tray_notifications.influence.PlaySounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class SoundItemEditor extends AbstractCellEditor implements ComboBoxEditor {
    private SoundItem currentItem;
    private final JPanel panel;
    private final JLabel label;

    public SoundItemEditor() {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        label = new JLabel();
        label.setFont(ButtonsInComboBox.style);

        JButton playButton = new JButton("▶");
        playButton.addActionListener(e -> {
            if (currentItem != null) {
                PlaySounds.playSound(currentItem.name());
            }
        });

        panel.add(playButton);
        panel.add(label);
    }

    @Override
    public Component getEditorComponent() {
        return panel;
    }
    @Override
    public Object getItem() {
        return currentItem;
    }
    @Override
    public void setItem(Object item) {
        if (item instanceof SoundItem) {
            currentItem = (SoundItem) item;
            label.setText(currentItem.name());
        }
    }
    @Override
    public Object getCellEditorValue() {
        return currentItem;
    }

    @Override
    public void selectAll() {
    }
    @Override
    public void addActionListener(ActionListener l) {
    }
    @Override
    public void removeActionListener(ActionListener l) {
    }
}