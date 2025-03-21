package com.battery_level_alarm.monitoring.user_interface.ui_constraints;
import com.battery_level_alarm.monitoring.user_interface.ui_config.GridBagConstraintsConfiguration;
import com.battery_level_alarm.monitoring.user_interface.ui_config.InsetsRecord;

import java.awt.*;

public class GridBagConstraintsDetails {
    private static int row = 0;
    private static int column = 0;

    public static GridBagConstraints createGridBagConstraints(GridBagConstraintsConfiguration configuration) {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = configuration.gridx();
        gbc.gridy = configuration.gridy();
        gbc.gridwidth = configuration.gridwidth();
        gbc.gridheight = configuration.gridheight();
        gbc.weightx = configuration.weightx();
        gbc.weighty = configuration.weighty();
        gbc.fill = configuration.fill();
        gbc.anchor = configuration.anchor();
        gbc.ipadx = configuration.ipadx();
        gbc.ipady = configuration.ipady();
        gbc.insets = configuration.insets();
        return gbc;
    }

    public static void setGridBagConstraintsInsets(GridBagConstraints gbc, InsetsRecord insets, boolean isSingleElement){
        if(isSingleElement){
            gbc.insets = new Insets(insets.top(), insets.left(), insets.bottom(), insets.right());
        } else {
            gbc.insets = new Insets(10, 10, 10, 10);
        }
    }

    public static void returnGBC$ToDefault(GridBagConstraints gbc){
        setRowToDefault();
        setColumnToDefault();
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        setGridBagConstraintsInsets(gbc, null, false);
    }

    public static void setDimension(int row, int column){
        GridBagConstraintsDetails.row = row;
        GridBagConstraintsDetails.column = column;
    }

    public static int getRow(){
        return row;
    }
    public static void setRow(int row){
        GridBagConstraintsDetails.row = row;
    }
    public static void setRowToDefault(){
        GridBagConstraintsDetails.row = 0;
    }

    public static int getColumn(){
        return column;
    }
    public static void setColumn(int column){
        GridBagConstraintsDetails.column = column;
    }
    public static void setColumnToDefault(){
        GridBagConstraintsDetails.column = 0;
    }
}