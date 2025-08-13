package com.battery_level_alarm.monitoring.user_interface.ui_helpers.app_settings_gui_helper;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import static com.battery_level_alarm.monitoring.system_core.Battorion.motherPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.AppSettingsGUI.APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.DropDownList.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.*;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;

import com.battery_level_alarm.monitoring.core_utilities.EffectDirection;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ComponentHierarchy;
import com.battery_level_alarm.monitoring.user_interface.ui_config.CompoundUpdaterRecord;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ToggleButtonRecord;
import com.battery_level_alarm.monitoring.registration_manager.ConfigurationFilesManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class AppSettingsFourthPartialPanel {
    private static final boolean[] APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY = {
            UserChoices.isEnableChargeAndDischargeSound(),
            UserChoices.isEnableText()
    };
    private static ComponentHierarchy hierarchy;
    public static Dimension partialPanelDimension;
    public static JProgressBar ProgressBar;

    public static JPanel prepareAppSettingsFourthPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc) {
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        JPanel partialPanelContent = new JPanel(new GridBagLayout());
        partialPanelContent.setOpaque(false);
        hierarchy = new ComponentHierarchy(
                null, 0, mainFrame, motherPanel, mainPartialPanel
        );

        int partialIndex = 0;
        partialIndex = addToggleFeature(
                gbc, partialPanelContent, partialIndex,
                "Enable Charging/Discharging Sound:",
                UserChoices::setEnableChargeAndDischargeSound,
                UserChoices.isEnableChargeAndDischargeSound()
        );

        addToggleFeature(
                gbc, partialPanelContent, partialIndex,
                "Enable Text Alerts:",
                UserChoices::setEnableText,
                UserChoices.isEnableText()
        );

        decideTheSizeDimension();
        APP_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[3] = partialPanelContent;
        mainPartialPanel.add(partialPanelContent, BorderLayout.CENTER);
        return mainPartialPanel;
    }

    private static int addToggleFeature(GridBagConstraints gbc, JPanel panel, int index,
                                        String label, Consumer<Boolean> setter, boolean currentState) {
        setDimension(index, 0);
        addLabel(gbc, panel, label, LABELS_FONT);
        CompoundUpdaterRecord compoundUpdaterRecord = getCompoundUpdaterRecord(hierarchy, index);
        ToggleButtonRecord toggleButtonRecord = new ToggleButtonRecord(
                setter,
                ConfigurationFilesManager::saveSettings,
                currentState ? "On" : "Off",
                new Dimension(80, 30)
        );

        setColumn(1);
        addLabel(gbc, panel, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(gbc, panel, toggleButtonRecord, compoundUpdaterRecord);
        return index + 1;
    }

    private static CompoundUpdaterRecord getCompoundUpdaterRecord(ComponentHierarchy hierarchy, int index) {
        return switch (index) {
            case 0 -> getFirstCompoundUpdaterRecord(hierarchy);
            case 1 -> getSecondCompoundUpdaterRecord(hierarchy);
            default -> throw new IllegalArgumentException("Invalid index for CompoundUpdaterRecord");
        };
    }

    private static @NotNull CompoundUpdaterRecord getFirstCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                0,
                UserChoices::isEnableChargeAndDischargeSound,
                new JComponent[]{}
        );
    }

    private static @NotNull CompoundUpdaterRecord getSecondCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                1,
                UserChoices::isEnableText,
                new JComponent[]{}
        );
    }

    private static @NotNull CompoundUpdaterRecord createCompoundUpdaterRecord(
            ComponentHierarchy hierarchy,
            int index,
            Callable<Boolean> conditionSupplier,
            JComponent[] components
    ){
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                APP_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY,
                index,
                conditionSupplier,
                components
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

    private static void decideTheSizeDimension(){
        partialPanelDimension = new Dimension(WIDTH, 85);
    }
}