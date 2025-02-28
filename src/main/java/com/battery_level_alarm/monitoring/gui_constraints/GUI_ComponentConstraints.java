package com.battery_level_alarm.monitoring.gui_constraints;

import javax.swing.*;
import java.awt.*;

public class GUI_ComponentConstraints {
    public static JTable setTableConstraints(String[][] data, String[] columns) {
        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        table.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 13));
        return table;
    }
}