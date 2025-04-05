package com.battery_level_alarm.monitoring.user_interface.ui_static_configs;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.*;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SpinnerConfig;

import javax.swing.*;
import java.awt.*;

public class RelatedToSpinner {
    public static void addLabeledSpinner(
            GridBagConstraints gbc, JPanel panel, SpinnerConfig config,
            JSpinner spinner, boolean isAllowToAddSpace
    ){
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
        gbc.gridx = isAllowToAddSpace ? config.column() + 2 : config.column() + 1;
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
            setSpinnerTextFieldEditable(spinner, false);
        }

        if (config.listener() != null) {
            spinner.addChangeListener(config.listener());
        }
        return spinner;
    }

    public static void setSpinnerTextFieldEditable(JSpinner spinner, boolean editable){
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setEditable(editable);
            ((JSpinner.DefaultEditor) editor).getTextField().setFocusable(editable);
        }
    }

    public static void invisibleSpinnerTextField(JSpinner spinner, boolean showTextField){
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setVisible(showTextField);
        }
    }
}