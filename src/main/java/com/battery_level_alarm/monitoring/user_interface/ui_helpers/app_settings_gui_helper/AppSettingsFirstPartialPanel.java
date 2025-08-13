package com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import static com.battery_level_alarm.monitoring.system_core.Battorion.motherPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.DropDownList.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Fonts.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.AppSettingsGUI.APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;

import com.battery_level_alarm.monitoring.core_utilities.EffectDirection;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.user_interface.ui_config.*;
import com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner;
import com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class AppSettingsFirstPartialPanel {
    private static final boolean[] APP_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY = { UserChoices.isAutoMonitoring() };
    private static ComponentHierarchy hierarchy;
    public static Dimension partialPanelDimension;
    public static JProgressBar ProgressBar;

    public static JPanel prepareAppSettingsFirstPartialContainer(GridBagConstraints gbc) {
        ProgressBar = prepareProgressBar(APP_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        hierarchy = new ComponentHierarchy(
                null, 0, mainFrame, motherPanel, mainPanel
        );

        addMonitoringToggle(gbc, contentPanel);
        addBatteryLevelSpinners(gbc, contentPanel);

        decideTheSizeDimension();
        APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[0] = contentPanel;
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private static void addMonitoringToggle(GridBagConstraints gbc, JPanel panel) {
        setDimension(0, 0);
        addLabel(gbc, panel, "Enable Automatic Monitoring:", DEFAULT_FONT);
        CompoundUpdaterRecord updaterRecord = getCompoundUpdaterRecord(hierarchy);
        ToggleButtonRecord toggleButton = createToggleButtonRecord();

        setColumn(1);
        addLabel(gbc, panel, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(gbc, panel, toggleButton, updaterRecord);
    }

    private static ToggleButtonRecord createToggleButtonRecord() {
        return new ToggleButtonRecord(
                UserChoices::setAutoMonitoring,
                ConfigurationFilesManager::saveSettings,
                UserChoices.isAutoMonitoring() ? "On" : "Off",
                new Dimension(80, 30)
        );
    }

    private static @NotNull CompoundUpdaterRecord getCompoundUpdaterRecord(
            ComponentHierarchy hierarchy
    ){
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY,
                0,
                UserChoices::isAutoMonitoring,
                new JSpinner[]{}
        );
        return new CompoundUpdaterRecord(
                null, null, null,
                progressBarUpdater,
                EffectDirection.NONE,
                hierarchy,
                null,
                true,
                false
        );
    }

    private static void addBatteryLevelSpinners(GridBagConstraints gbc, JPanel panel) {
        setColumn(0);
        SpinnerConfig minBatteryConfig = createSpinnerConfig(
                "Minimum Battery Level:",
                UserChoices.getMinimumLevel(), 25, 15, 30, 1, e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 10, 25);
                    UserChoices.setMinimumLevel(value);
                    ConfigurationFilesManager.saveSettings();
                });
        JSpinner minSpinner = RelatedToSpinner.createSpinner(minBatteryConfig, false);
        addLabeledSpinner(gbc, panel, minBatteryConfig, minSpinner, true);

        setColumn(0);
        SpinnerConfig maxBatteryConfig = createSpinnerConfig(
                "Maximum Battery Level:",
                UserChoices.getMaximumLevel(), 85, 80, 90, 2, e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 80, 85);
                    UserChoices.setMaximumLevel(value);
                    ConfigurationFilesManager.saveSettings();
                });
        JSpinner maxSpinner = RelatedToSpinner.createSpinner(maxBatteryConfig, false);
        addLabeledSpinner(gbc, panel, maxBatteryConfig, maxSpinner, true);
    }

    private static SpinnerConfig createSpinnerConfig(
            String label, int current, int def,
            int min, int max, int row,
            ChangeListener listener
    ){
        return new SpinnerConfig(
                label,
                current, def,
                min, max,
                1, row, 0,
                80, 30,
                listener
        );
    }

    private static void decideTheSizeDimension(){
        partialPanelDimension = new Dimension(WIDTH, 130);
    }
}