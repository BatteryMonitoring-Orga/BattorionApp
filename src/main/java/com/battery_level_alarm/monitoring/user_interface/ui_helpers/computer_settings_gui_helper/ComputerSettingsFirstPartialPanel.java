package com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.mainFrame;
import static com.battery_level_alarm.monitoring.system_core.Battorion.motherPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.ComputerSettingsGUI.COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.DropDownList.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addMouseListenerToLabel;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;

import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.core_utilities.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.core_utilities.EffectDirection;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ComponentHierarchy;
import com.battery_level_alarm.monitoring.user_interface.ui_config.CompoundUpdaterRecord;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.user_interface.ui_config.ToggleButtonRecord;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.ComputerSettingsGUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class ComputerSettingsFirstPartialPanel {
    private static final boolean[] COMPUTER_SETTINGS_FIRST_PARTIAL_TRUE_ARRAY = {
            isActivateTheAwakeningFeature(),
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
                " Activate the awakening feature:",
                ComputerSettings::setActivateTheAwakeningFeature,
                ComputerSettings.isActivateTheAwakeningFeature()
        );

        partialIndex = addToggleFeature(
                gbc, partialPanelContent, partialIndex,
                " Enable System Notification Sound:",
                ComputerSettings::setEnableSystemNotificationSound,
                ComputerSettings.isEnableSystemNotificationSound()
        );

        addToggleFeature(
                gbc, partialPanelContent, partialIndex,
                " Enable unmute volume automatically:",
                ComputerSettings::setEnableUnmuteVolumeAutomatically,
                ComputerSettings.isEnableUnmuteVolumeAutomatically()
        );

        decideTheSizeDimension();
        COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[0] = partialPanelContent;
        JPanel partialPanelFooter = createPartialPanelFooter();
        partialPanelFooter.setOpaque(false);
        mainPartialPanel.add(partialPanelContent, BorderLayout.CENTER);
        mainPartialPanel.add(partialPanelFooter, BorderLayout.SOUTH);
        return mainPartialPanel;
    }

    private static int addToggleFeature(GridBagConstraints gbc, JPanel panel, int index,
                                        String label, Consumer<Boolean> setter, boolean currentState) {
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
        addLabel(gbc, panel, FOUR_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(gbc, panel, toggleButtonRecord, compoundUpdaterRecord);
        return index + 1;
    }

    private static CompoundUpdaterRecord getCompoundUpdaterRecord(ComponentHierarchy hierarchy, int index) {
        return switch (index) {
            case 0 -> getFirstCompoundUpdaterRecord(hierarchy);
            case 1 -> getSecondCompoundUpdaterRecord(hierarchy);
            case 2 -> getThirdCompoundUpdaterRecord(hierarchy);
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
                ComputerSettings::isEnableSystemNotificationSound,
                new JComponent[]{}
        );
    }

    private static @NotNull CompoundUpdaterRecord getThirdCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                2,
                ComputerSettings::isEnableUnmuteVolumeAutomatically,
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

    private static JPanel createPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + ONE_SPACE);
        about.setFont(ComputerSettingsGUI.TITLE_LISTS_FONT);
        addMouseListenerToLabel(
                about,
                Color.LIGHT_GRAY,
                () -> displayPopUpMenu(
                        about,
                        createFooterPopupMenu(DropDownListStaticQuestionnaires::getFirstPartialQuestionnaires)
                )
        );

        JPanel aboutLabelPackage = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        aboutLabelPackage.add(about);
        JPanel aboutPanel = new JPanel(new BorderLayout());
        //aboutPanel.add(new JLabel(TWO_SPACE), BorderLayout.NORTH);
        aboutPanel.add(aboutLabelPackage, BorderLayout.CENTER);
        return aboutPanel;
    }

    private static void decideTheSizeDimension(){
        partialPanelDimension = new Dimension(WIDTH, 190);
    }
}