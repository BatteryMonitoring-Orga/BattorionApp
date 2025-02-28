package com.battery_level_alarm.monitoring.gui_static_method_configurations;
import com.battery_level_alarm.monitoring.configuration_records.SoundItem;
import com.notifications.system_tray_notifications.basics.AlarmSounds;
import javax.swing.*;
import java.awt.*;

public class ButtonsInComboBox {
    public static final Font style = new Font (Font.SERIF, Font.BOLD, 14);

    public static JComboBox<SoundItem> createModernComboBox(){
        AlarmSounds alarmSounds_Object = new AlarmSounds(1);
        JComboBox<SoundItem> comboBox = new JComboBox<>();
        comboBox.setMaximumRowCount(4);
        for(int i = 1; i < 11; i++){
            alarmSounds_Object.setSoundSequenceNumber(i);
            comboBox.addItem(new SoundItem(alarmSounds_Object.getSoundFileName()));
        }

        comboBox.setRenderer(new SoundItemRenderer());
        comboBox.setEditor(new SoundItemEditor());
        comboBox.setEditable(true);
        return comboBox;
    }
}