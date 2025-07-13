package com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.registration_manager.AutoStartManager.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import static com.battery_level_alarm.monitoring.system_core.Battorion.motherPanel;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.APP_NAME;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.getCurrentExePath;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.getRow;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.DropDownList.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;

import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.core_utilities.EffectDirection;
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

public class ComputerSettingsFirstPartialPanel {
    private static final boolean[] COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY = {
            isActivateTheAwakeningFeature(),
            isAutoStartEnabled(APP_NAME),
            isEnableSystemNotificationSound(),
            isEnableUnmuteVolumeAutomatically()
    };
    private static ComponentHierarchy hierarchy;
    public static Dimension partialPanelDimension;
    public static JProgressBar ProgressBar;

    public static JPanel prepareFirstPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY, 6);
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
                "Activate the awakening feature:",
                ComputerSettings::setActivateTheAwakeningFeature,
                ComputerSettings.isActivateTheAwakeningFeature()
        );
        partialIndex = addStartupToggleFeature(gbc, partialPanelContent, partialIndex, isAutoStartEnabled(APP_NAME));
        
        partialIndex = addToggleFeature(
                gbc, partialPanelContent, partialIndex,
                "Enable System Notification Sound:",
                ComputerSettings::setEnableSystemNotificationSound,
                ComputerSettings.isEnableSystemNotificationSound()
        );
        addToggleFeature(
                gbc, partialPanelContent, partialIndex,
                "Enable unmute volume automatically:",
                ComputerSettings::setEnableUnmuteVolumeAutomatically,
                ComputerSettings.isEnableUnmuteVolumeAutomatically()
        );
        
        decideTheSizeDimension();
        COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[0] = partialPanelContent;
        mainPartialPanel.add(partialPanelContent, BorderLayout.CENTER);
        return mainPartialPanel;
    }

    private static int addToggleFeature(
            GridBagConstraints gbc, JPanel panel, int index,
            String label, Consumer<Boolean> setter, boolean currentState
    ) {
        setDimension(index, 0);
        addLabel(gbc, panel, label, LABELS_FONT);
        CompoundUpdaterRecord compoundUpdaterRecord = getCompoundUpdaterRecord(hierarchy, index);
        ToggleButtonRecord toggleButtonRecord = new ToggleButtonRecord(
                setter,
                ConfigurationFilesManager::saveComputerSettings,
                currentState ? "On" : "Off",
                new Dimension(60, 30)
        );
        setColumn(1);
        addLabel(gbc, panel, TWO_SPACE + ONE_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(gbc, panel, toggleButtonRecord, compoundUpdaterRecord);
        return index + 1;
    }
    
    private static int addStartupToggleFeature(GridBagConstraints gbc, JPanel panel, int index, boolean currentState) {
        setDimension(index, 0);
        addLabel(gbc, panel, "Run the application at system startup:", LABELS_FONT);
        setColumn(1);
        addLabel(gbc, panel, TWO_SPACE + ONE_SPACE, LABELS_FONT);
        setColumn(2);
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        panel.add(createRunOnStartupButton(index, currentState), gbc);
        return index + 1;
    }
    
    private static JToggleButton createRunOnStartupButton(int index, boolean currentState) {
        CompoundUpdaterRecord compoundUpdaterRecord = getCompoundUpdaterRecord(hierarchy, index);
        JToggleButton toggleButton = new JToggleButton(currentState ? "On" : "Off");
        toggleButton.setPreferredSize(new Dimension(60, 30));
        toggleButton.setFont(toggleButtonsFont);
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.setSelected(currentState);
        toggleButton.setBackground(currentState ? new Color(72, 201, 176) : Color.DARK_GRAY);
        toggleButton.setForeground(currentState ? Color.BLACK : Color.WHITE);
        toggleButton.addActionListener(_ -> {
            boolean isSelected = toggleButton.isSelected();
            if(isSelected) {
                enableAutoStart(APP_NAME, getCurrentExePath());
            } else {
                disableAutoStart(APP_NAME);
            }
            
            toggleButton.setText(isSelected ? "On" : "Off");
            toggleButton.setBackground(isSelected ? new Color(72, 201, 176) : Color.DARK_GRAY);
            toggleButton.setForeground(isSelected ? Color.BLACK : Color.WHITE);
            updateProgressBars(compoundUpdaterRecord.progressBarValueUpdater());
        });
        return toggleButton;
    }

    private static CompoundUpdaterRecord getCompoundUpdaterRecord(ComponentHierarchy hierarchy, int index) {
        return switch (index) {
            case 0 -> getFirstCompoundUpdaterRecord(hierarchy);
            case 1 -> getSecondCompoundUpdaterRecord(hierarchy);
            case 2 -> getThirdCompoundUpdaterRecord(hierarchy);
            case 3 -> getFourthCompoundUpdaterRecord(hierarchy);
            default -> throw new IllegalArgumentException("Invalid index for CompoundUpdaterRecord");
        };
    }

    private static @NotNull CompoundUpdaterRecord getFirstCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                0,
                ComputerSettings::isActivateTheAwakeningFeature,
                new JComponent[]{}
        );
    }
    
    private static @NotNull CompoundUpdaterRecord getSecondCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                1,
                () -> isAutoStartEnabled(APP_NAME),
                new JComponent[]{}
        );
    }

    private static @NotNull CompoundUpdaterRecord getThirdCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                2,
                ComputerSettings::isEnableSystemNotificationSound,
                new JComponent[]{}
        );
    }

    private static @NotNull CompoundUpdaterRecord getFourthCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                3,
                ComputerSettings::isEnableUnmuteVolumeAutomatically,
                new JComponent[]{}
        );
    }

    private static @NotNull CompoundUpdaterRecord createCompoundUpdaterRecord(
            ComponentHierarchy hierarchy,
            int index,
            Callable<Boolean> conditionSupplier,
            JComponent[] components
    ) {
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY,
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
        partialPanelDimension = new Dimension(WIDTH, getFirstPCHeight());
    }
    public static int getFirstPCHeight() {
        return 220;
    }
}