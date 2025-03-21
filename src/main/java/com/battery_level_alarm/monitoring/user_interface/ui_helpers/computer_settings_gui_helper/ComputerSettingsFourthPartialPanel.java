package com.battery_level_alarm.monitoring.user_interface.ui_helpers.computer_settings_gui_helper;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.ComputerSettingsGUI.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.DropDownList.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToLabels.addMouseListenerToLabel;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner.addLabeledSpinner;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner.getSpinnerValue;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToTextFields.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass.pcSettingPanel;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.WIDTH;

import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.core_utilities.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.core_utilities.EffectDirection;
import com.battery_level_alarm.monitoring.user_interface.ui_config.*;
import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToSpinner;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.SettingsContainerClass;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.concurrent.Callable;

public class ComputerSettingsFourthPartialPanel {
    private static final boolean[] COMPUTER_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY = {
            ComputerSettings.isAutomaticallyReduceAndRestoreBL(),
            ComputerSettings.isAutomaticallyRestoreBrightnessLevel(),
            ComputerSettings.isAutomaticallyReduceBrightnessLevel()
    };
    private static final JPanel[] COMPUTER_SETTINGS_FOURTH_PARTIAL_PANELS_ARRAY = {
            new JPanel(),
            new JPanel()
    };
    private static final JPanel[] COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_PANEL_ARRAY = {
            new JPanel()
    };
    private static final JToggleButton[] COMPUTER_SETTINGS_FOURTH_PARTIAL_TOGGLE_BUTTONS_ARRAY = {
            new JToggleButton(), new JToggleButton(), new JToggleButton()
    };

    public static JTextField sliderValueTextField;
    public static JProgressBar ProgressBar;
    private static ComponentHierarchy hierarchy;
    public static Dimension partialPanelDimension;
    public static boolean isEnableRequestFocusInWindow = true;
    private static boolean thereIsInvisiblePartFlag = false;

    public static JPanel prepareFourthPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(COMPUTER_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc) {
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        hierarchy = new ComponentHierarchy(
                SettingsContainerClass::refreshPCSettingsTab,
                0, mainFrame,
                motherPanel, pcSettingPanel
        );

        setDimension(0, 0);
        JPanel northPartialPanelContent = createNorthPartialPanelContent(gbc);
        JPanel partialPanelContent = createPartialPanelContent(gbc);
        JPanel partialPanelFooter = createPartialPanelFooter();
        decideTheSizeDimension();

        northPartialPanelContent.setOpaque(false);
        partialPanelContent.setOpaque(false);
        partialPanelFooter.setOpaque(false);

        mainPartialPanel.add(northPartialPanelContent, BorderLayout.NORTH);
        mainPartialPanel.add(partialPanelContent, BorderLayout.CENTER);
        mainPartialPanel.add(partialPanelFooter, BorderLayout.SOUTH);
        return mainPartialPanel;
    }

    private static JPanel createNorthPartialPanelContent(GridBagConstraints gbc) {
        JPanel northPartialPanelContent = new JPanel(new GridBagLayout());
        northPartialPanelContent.setOpaque(false);

        Brightness.BrightnessProcess(0, true);
        JSlider screenBrightnessSlider = createScreenBrightnessSlider(gbc, northPartialPanelContent);
        JPanel sliderTextPanel = createSliderTextPanel(gbc, screenBrightnessSlider);

        gbc.gridx = 2;
        gbc.gridy = 0;
        northPartialPanelContent.add(sliderTextPanel, gbc);
        return northPartialPanelContent;
    }

    private static JSlider createScreenBrightnessSlider(GridBagConstraints gbc, JPanel parentPanel) {
        return addLabeledSlider(
                gbc, parentPanel, "Adjust Screen Brightness",
                0, 100, Brightness.getCurrentBrightness(),
                20, 5, JSlider.HORIZONTAL,
                e -> {
                    JSlider source = (JSlider) e.getSource();
                    if (!source.getValueIsAdjusting()) {
                        int newValue = source.getValue();
                        Brightness.BrightnessProcess(newValue, false);
                        sliderValueTextField.setText(String.valueOf(newValue));
                    }
                }
        );
    }

    private static JPanel createSliderTextPanel(
            GridBagConstraints gbc, JSlider slider
    ){
        JPanel sliderTextPanel = new JPanel(new GridBagLayout());
        sliderTextPanel.setPreferredSize(new Dimension(40, 30));
        setDimension(0, 0);

        sliderValueTextField = addTextField(
                gbc, sliderTextPanel,
                String.valueOf(Brightness.getCurrentBrightness()),
                18, 10,
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
                false
        );

        setPopUpMenu(sliderValueTextField,
                new JComponent[]{
                        new JLabel(" Press 'Enter' to save the new brightness value ")
                }, new Font("Serif", Font.PLAIN, 12), false,
                ComputerSettingsFourthPartialPanel::setEnableRequestFocusInWindow,
                ComputerSettingsFourthPartialPanel::isEnableRequestFocusInWindow);

        setActionListener(sliderValueTextField, Brightness.getCurrentBrightness() + "", true,
                new Runnable[]{
                        () -> slider.setValue(Integer.parseInt(sliderValueTextField.getText())),
                        () -> Brightness.BrightnessProcess(Integer.parseInt(sliderValueTextField.getText()), false)
                }, ComputerSettingsFourthPartialPanel::setEnableRequestFocusInWindow);
        return sliderTextPanel;
    }

    private static JPanel createPartialPanelContent(GridBagConstraints gbc) {
        JPanel partialPanelContent = new JPanel(new GridBagLayout());
        Insets inset = gbc.insets;
        gbc.insets = new Insets(0, 0, 0, 0);
        int partialIndex = 4;
        createBrightnessSpinner(gbc, partialIndex);

        setDimension(0, 0);
        JPanel firstPanel = createFirstPanel(gbc, hierarchy);
        setDimension(0, 0);
        COMPUTER_SETTINGS_FOURTH_PARTIAL_PANELS_ARRAY[0] = createSecondPanel(gbc, hierarchy);
        COMPUTER_SETTINGS_FOURTH_PARTIAL_PANELS_ARRAY[0].setVisible(!ComputerSettings.isAutomaticallyReduceAndRestoreBL());
        setDimension(0, 0);
        COMPUTER_SETTINGS_FOURTH_PARTIAL_PANELS_ARRAY[1] = createThirdPanel(gbc, hierarchy);
        COMPUTER_SETTINGS_FOURTH_PARTIAL_PANELS_ARRAY[1].setVisible(!ComputerSettings.isAutomaticallyReduceAndRestoreBL());

        JPanel labelPanelContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel labelPanel = new JPanel(new GridBagLayout());
        addLabel(gbc, labelPanel, "In risk phase:", DEFAULT_FONT);
        labelPanelContainer.add(labelPanel);

        setDimension(0, 0);
        JPanel comboBoxPanel = new JPanel(new GridBagLayout());
        createBrightnessComboBox(gbc, comboBoxPanel);

        gbc.insets = inset;
        gbc.gridx = 0;
        gbc.gridy = 0;
        partialPanelContent.add(comboBoxPanel, gbc);
        gbc.gridy++;
        partialPanelContent.add(firstPanel, gbc);
        gbc.gridy++;
        partialPanelContent.add(COMPUTER_SETTINGS_FOURTH_PARTIAL_PANELS_ARRAY[0], gbc);
        gbc.gridy++;
        partialPanelContent.add(labelPanelContainer, gbc);
        gbc.gridy++;
        partialPanelContent.add(COMPUTER_SETTINGS_FOURTH_PARTIAL_PANELS_ARRAY[1], gbc);
        gbc.gridy++;
        partialPanelContent.add(COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_PANEL_ARRAY[0], gbc);
        COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[3] = partialPanelContent;
        return partialPanelContent;
    }

    private static void createBrightnessComboBox(GridBagConstraints gbc, JPanel panel) {
        String[] brightnessOptions = {
                "High & Low Phases",
                "High Phase Only",
                "Low Phase Only",
                "Do Not Adjust"
        };
        String selectedItem = brightnessOptions[getBrightnessControlOption()];

        addComboBox(
                gbc, panel, "When to control brightness automatically:" + ONE_SPACE,
                brightnessOptions, selectedItem, 3,
                e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        String selected = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();
                        assert selected != null;
                        int mode = switch (selected) {
                            case "High & Low Phases" -> 0;
                            case "High Phase Only" -> 1;
                            case "Low Phase Only" -> 2;
                            case "Do Not Adjust" -> 3;
                            default -> -1;
                        };
                        setBrightnessControlOption(mode);
                        ConfigurationFilesManager.saveComputerSettings();

                        if(mode == 3){
                            for(JToggleButton entry : COMPUTER_SETTINGS_FOURTH_PARTIAL_TOGGLE_BUTTONS_ARRAY){
                                if(entry.isSelected()){
                                    entry.doClick();
                                }
                            }
                        }
                    }
                }, 150, 30
        );
    }

    private static JPanel createFirstPanel(GridBagConstraints gbc, ComponentHierarchy hierarchy) {
        JPanel firstPanel = new JPanel(new GridBagLayout());
        addLabel(gbc, firstPanel, "Automatically reduce and restore BL:", DEFAULT_FONT);
        CompoundUpdaterRecord firstCompoundUpdaterRecord = getFirstCompoundUpdaterRecord(hierarchy);
        ToggleButtonRecord firstToggleButtonRecord = new ToggleButtonRecord(
                ComputerSettings::setAutomaticallyReduceAndRestoreBL,
                ConfigurationFilesManager::saveComputerSettings,
                ComputerSettings.isAutomaticallyReduceAndRestoreBL() ? "On" : "Off",
                new Dimension(60, 30)
        );

        setColumn(1);
        addLabel(gbc, firstPanel, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        COMPUTER_SETTINGS_FOURTH_PARTIAL_TOGGLE_BUTTONS_ARRAY[0] = addToggleButton(
                gbc, firstPanel, firstToggleButtonRecord, firstCompoundUpdaterRecord
        );
        return firstPanel;
    }

    private static JPanel createSecondPanel(GridBagConstraints gbc, ComponentHierarchy hierarchy) {
        JPanel secondPanel = new JPanel(new GridBagLayout());
        addLabel(gbc, secondPanel, "Automatically restore brightness level (BL):", DEFAULT_FONT);

        CompoundUpdaterRecord secondCompoundUpdaterRecord = getSecondCompoundUpdaterRecord(hierarchy);
        ToggleButtonRecord secondToggleButtonRecord = new ToggleButtonRecord(
                ComputerSettings::setAutomaticallyRestoreBrightnessLevel,
                ConfigurationFilesManager::saveComputerSettings,
                ComputerSettings.isAutomaticallyRestoreBrightnessLevel() ? "On" : "Off",
                new Dimension(60, 30)
        );

        setColumn(1);
        addLabel(gbc, secondPanel, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        COMPUTER_SETTINGS_FOURTH_PARTIAL_TOGGLE_BUTTONS_ARRAY[1] = addToggleButton(
                gbc, secondPanel, secondToggleButtonRecord, secondCompoundUpdaterRecord
        );
        return secondPanel;
    }

    private static JPanel createThirdPanel(GridBagConstraints gbc, ComponentHierarchy hierarchy) {
        JPanel thirdPanel = new JPanel(new GridBagLayout());
        addLabel(gbc, thirdPanel, "Automatically reduce brightness level (BL):", DEFAULT_FONT);

        CompoundUpdaterRecord thirdCompoundUpdaterRecord = getThirdCompoundUpdaterRecord(hierarchy);
        ToggleButtonRecord thirdToggleButtonRecord = new ToggleButtonRecord(
                ComputerSettings::setAutomaticallyReduceBrightnessLevel,
                ConfigurationFilesManager::saveComputerSettings,
                ComputerSettings.isAutomaticallyReduceBrightnessLevel() ? "On" : "Off",
                new Dimension(60, 30)
        );

        setColumn(1);
        addLabel(gbc, thirdPanel, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        COMPUTER_SETTINGS_FOURTH_PARTIAL_TOGGLE_BUTTONS_ARRAY[2] = addToggleButton(
                gbc, thirdPanel, thirdToggleButtonRecord, thirdCompoundUpdaterRecord
        );
        return thirdPanel;
    }

    private static @NotNull CompoundUpdaterRecord getFirstCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                0,
                ComputerSettings::isAutomaticallyReduceAndRestoreBL,
                COMPUTER_SETTINGS_FOURTH_PARTIAL_PANELS_ARRAY,
                EffectDirection.REVERSE
        );
    }

    private static @NotNull CompoundUpdaterRecord getSecondCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                1,
                ComputerSettings::isAutomaticallyRestoreBrightnessLevel,
                new JComponent[]{},
                EffectDirection.NONE
        );
    }

    private static @NotNull CompoundUpdaterRecord getThirdCompoundUpdaterRecord(ComponentHierarchy hierarchy) {
        return createCompoundUpdaterRecord(
                hierarchy,
                2,
                ComputerSettings::isAutomaticallyReduceBrightnessLevel,
                COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_PANEL_ARRAY,
                EffectDirection.FORWARD
        );
    }

    private static @NotNull CompoundUpdaterRecord createCompoundUpdaterRecord(
            ComponentHierarchy hierarchy,
            int index,
            Callable<Boolean> conditionSupplier,
            JComponent[] components,
            EffectDirection effectDirection
    ) {
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                COMPUTER_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY,
                index,
                conditionSupplier,
                components
        );
        return new CompoundUpdaterRecord(
                ComputerSettingsFourthPartialPanel::setThereIsInvisiblePartFlag,
                ComputerSettingsFourthPartialPanel::isThereIsInvisiblePartFlag,
                ComputerSettingsFourthPartialPanel::setSpinnerVisibility,
                progressBarUpdater, effectDirection,
                hierarchy, null,
                true, true
        );
    }

    private static void createBrightnessSpinner(GridBagConstraints gbc, int partialIndex){
        SpinnerConfig brightnessConfig = new SpinnerConfig(
                "The brightness will be set to:  " + TEN_SPACE + ONE_SPACE,
                getBrightnessLevel(), 10, 0, 100, 1,
                partialIndex, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 0, 10);
                    setBrightnessLevel(value);
                    ConfigurationFilesManager.saveComputerSettings();
                }
        );
        JSpinner brightnessRiskSpinner = RelatedToSpinner.createSpinner(brightnessConfig, false);
        brightnessRiskSpinner.setEnabled(
                ComputerSettings.isAutomaticallyReduceBrightnessLevel() ||
                ComputerSettings.isAutomaticallyReduceAndRestoreBL());

        thereIsInvisiblePartFlag = !ComputerSettings.isAutomaticallyReduceBrightnessLevel();
        COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_PANEL_ARRAY[0] = new JPanel(new GridBagLayout());
        COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_PANEL_ARRAY[0].setVisible(
                !thereIsInvisiblePartFlag || ComputerSettings.isAutomaticallyReduceAndRestoreBL());
        addLabeledSpinner(
                gbc, COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_PANEL_ARRAY[0],
                brightnessConfig, brightnessRiskSpinner, true
        );
    }

    private static JPanel createPartialPanelFooter(){
        JLabel about = new JLabel("▶ What do these options mean?" + ONE_SPACE);
        about.setFont(TITLE_LISTS_FONT);
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

    private static void setEnableRequestFocusInWindow(boolean isEnableRequestFocusInWindow){
        ComputerSettingsFourthPartialPanel.isEnableRequestFocusInWindow = isEnableRequestFocusInWindow;
    }
    private static boolean isEnableRequestFocusInWindow(){
        return isEnableRequestFocusInWindow;
    }

    private static void setThereIsInvisiblePartFlag(boolean thereIsInvisiblePartFlag){
        ComputerSettingsFourthPartialPanel.thereIsInvisiblePartFlag = thereIsInvisiblePartFlag;
    }
    private static boolean isThereIsInvisiblePartFlag(){
        return thereIsInvisiblePartFlag;
    }

    private static void setSpinnerVisibility(){
        COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_PANEL_ARRAY[0].setVisible(true);
    }

    private static void decideTheSizeDimension(){
        if(ComputerSettings.isAutomaticallyReduceAndRestoreBL()){
            partialPanelDimension = new Dimension(WIDTH, 310);
        } else {
            if(thereIsInvisiblePartFlag){
                partialPanelDimension = new Dimension(WIDTH, 360);
            } else {
                partialPanelDimension = new Dimension(WIDTH, 400);
            }
        }
    }
}