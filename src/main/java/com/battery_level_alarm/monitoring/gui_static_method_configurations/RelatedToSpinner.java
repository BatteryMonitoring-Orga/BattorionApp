package com.battery_level_alarm.monitoring.gui_static_method_configurations;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.DEFAULT_FONT;
import com.battery_level_alarm.monitoring.configuration_records.SpinnerConfig;

import javax.swing.*;
import java.awt.*;

public class RelatedToSpinner {
    public static void addLabeledSpinner(GridBagConstraints gbc, JPanel panel, SpinnerConfig config, JSpinner spinner) {
        JLabel jLabel = new JLabel(config.label());
        jLabel.setFont(DEFAULT_FONT);
        gbc.gridy = config.row();
        gbc.gridx = config.column();
        panel.add(jLabel, gbc);

        spinner.setFont(DEFAULT_FONT);
        spinner.setPreferredSize(new Dimension(config.width(), config.height()));
        if (config.listener() != null) {
            spinner.addChangeListener(config.listener());
        }
        gbc.gridx = config.column() + 1;
        panel.add(spinner, gbc);
    }

    public static int getSpinnerValue(JSpinner spinner, int minValue, int defaultValue) {
        int value = Integer.parseInt(spinner.getValue().toString());
        return (value >= minValue) ? value : defaultValue;
    }

    public static JSpinner createSpinner(SpinnerConfig config, boolean unableToWrite) {
        SpinnerNumberModel model = new SpinnerNumberModel(
                config.currentValue(), config.min(), config.max(), config.step()
        );

        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(config.width(), config.height()));
        if(unableToWrite){
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) editor).getTextField().setEditable(false);
                ((JSpinner.DefaultEditor) editor).getTextField().setFocusable(false);
            }
        }

        if (config.listener() != null) {
            spinner.addChangeListener(config.listener());
        }
        return spinner;
    }
}