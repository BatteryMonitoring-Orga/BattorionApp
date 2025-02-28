package com.battery_level_alarm.monitoring.configuration_records;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public record GridBagConstraintsConfiguration(
        int gridx,
        int gridy,
        int gridwidth,
        int gridheight,
        double weightx,
        double weighty,
        int anchor,
        int fill,
        Insets insets,
        int ipadx,
        int ipady
){
    public GridBagConstraints toGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = this.gridx;
        gbc.gridy = this.gridy;
        gbc.gridwidth = this.gridwidth;
        gbc.gridheight = this.gridheight;
        gbc.weightx = this.weightx;
        gbc.weighty = this.weighty;
        gbc.anchor = this.anchor;
        gbc.fill = this.fill;
        gbc.insets = this.insets;
        gbc.ipadx = this.ipadx;
        gbc.ipady = this.ipady;
        return gbc;
    }
}