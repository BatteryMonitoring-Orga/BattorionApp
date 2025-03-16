package com.battery_level_alarm.monitoring.gui_interfaces_helper.computer_settings_gui_helper;
import static com.battery_level_alarm.monitoring.basics.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.core.BattorionMain.borderColor;
import static com.battery_level_alarm.monitoring.cybernate.WakeUpPC.setShiftInX_axis;
import static com.battery_level_alarm.monitoring.cybernate.WakeUpPC.setShiftInY_axis;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setColumn;
import static com.battery_level_alarm.monitoring.gui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToButtons.addToggleButton;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToLabels.addMouseListenerToLabel;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.addLabeledSpinner;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner.getSpinnerValue;
import static com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToTextFields.*;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY;
import static com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI.LABELS_FONT;
import static com.battery_level_alarm.monitoring.preparing_gui.DropDownList.*;

import com.battery_level_alarm.monitoring.basics.ComputerSettings;
import com.battery_level_alarm.monitoring.basics.DropDownListStaticQuestionnaires;
import com.battery_level_alarm.monitoring.configuration_records.ProgressBarValueUpdater;
import com.battery_level_alarm.monitoring.configuration_records.SpinnerConfig;
import com.battery_level_alarm.monitoring.effects.Brightness;
import com.battery_level_alarm.monitoring.gui_static_method_configurations.RelatedToSpinner;
import com.battery_level_alarm.monitoring.main_folder_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.preparing_gui.ComputerSettingsGUI;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import java.awt.*;

public class ComputerSettingsFourthPartialPanel {
    public static final boolean[] COMPUTER_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY = {
            ComputerSettings.isEnableSetBrightnessLevel()
    };
    public static final JSpinner[] COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_ARRAY = {
            new JSpinner()
    };

    public static JTextField sliderValueTextField;
    public static JProgressBar ProgressBar;
    public static boolean isEnableRequestFocusInWindow = true;

    public static JPanel prepareFourthPartialContainer(GridBagConstraints gbc){
        ProgressBar = prepareProgressBar(COMPUTER_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY, 6);
        return createPartialPanel(gbc);
    }

    private static JPanel createPartialPanel(GridBagConstraints gbc){
        JPanel mainPartialPanel = new JPanel(new BorderLayout());
        mainPartialPanel.setOpaque(false);
        JPanel partialPanelContent = new JPanel(new GridBagLayout());
        partialPanelContent.setOpaque(false);
        JPanel northPartialPanelContent = new JPanel(new GridBagLayout());
        northPartialPanelContent.setOpaque(false);

        int partialIndex = 0;
        setDimension(partialIndex, 0);
        Brightness.BrightnessProcess(0, true);
        JSlider screenBrightnessSlider = addLabeledSlider(
                gbc, northPartialPanelContent, "Adjust Screen Brightness",
                0, 100, Brightness.getCurrentBrightness(),
                20, 5, JSlider.HORIZONTAL,
                e -> {
                    JSlider source = (JSlider)e.getSource();
                    int newValue = source.getValue();
                    if (!source.getValueIsAdjusting()) {
                        Brightness.BrightnessProcess(newValue, false);
                        sliderValueTextField.setText(newValue + "");
                    }
                }
        );

        setDimension(0, 0);
        JPanel sliderTextPanel = new JPanel(new GridBagLayout());
        sliderTextPanel.setPreferredSize(new Dimension(40, 30));
        sliderValueTextField = addTextField(
                gbc, sliderTextPanel,
                Brightness.getCurrentBrightness() + "",
                18, 10,
                BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor)
                , false
        );
        setPopUpMenu(
                sliderValueTextField, new JComponent[]{
                        new JLabel(" Press 'Enter' to save the new brightness value ")
                }, new Font("Serif", Font.PLAIN, 12), false,
                ComputerSettingsFourthPartialPanel::setEnableRequestFocusInWindow,
                ComputerSettingsFourthPartialPanel::isEnableRequestFocusInWindow
        );
        setShiftInX_axis(0);
        setShiftInY_axis(50);
        setActionListener(
                sliderValueTextField, Brightness.getCurrentBrightness() + "", true,
                new Runnable[]{
                        () -> screenBrightnessSlider.setValue(Integer.parseInt(sliderValueTextField.getText())),
                        () -> Brightness.BrightnessProcess(Integer.parseInt(sliderValueTextField.getText()), false)
                }, ComputerSettingsFourthPartialPanel::setEnableRequestFocusInWindow
        );

        gbc.gridx = 2;
        gbc.gridy = partialIndex;
        northPartialPanelContent.add(sliderTextPanel, gbc);

        boolean isEnableSetBrightnessLevel = ComputerSettings.isEnableSetBrightnessLevel();
        String enableSetBrightnessLevel = isEnableSetBrightnessLevel? "On" : "Off";
        setDimension(partialIndex, 0);
        addLabel(gbc, partialPanelContent, "In risk phase:", DEFAULT_FONT);

        setDimension(++partialIndex, 0);
        addLabel(gbc, partialPanelContent, "Enable automatic brightness level:", DEFAULT_FONT);
        ProgressBarValueUpdater progressBarUpdater = new ProgressBarValueUpdater(
                ProgressBar,
                COMPUTER_SETTINGS_FOURTH_PARTIAL_TRUE_ARRAY,
                0,
                ComputerSettings::isEnableSetBrightnessLevel,
                COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_ARRAY
        );
        setColumn(1);
        addLabel(gbc, partialPanelContent, TWO_SPACE, LABELS_FONT);
        setColumn(2);
        addToggleButton(
                gbc, partialPanelContent, ComputerSettings::setEnableSetBrightnessLevel,
                ConfigurationFilesManager::saveComputerSettings, enableSetBrightnessLevel,
                60, 30, progressBarUpdater, true
        );

        SpinnerConfig brightnessConfig = new SpinnerConfig(
                "The brightness will be set to:",
                getBrightnessLevel(), 10, 0, 100, 1,
                ++partialIndex, 0, 80, 30,
                e -> {
                    int value = getSpinnerValue((JSpinner) e.getSource(), 0, 10);
                    setBrightnessLevel(value);
                    ConfigurationFilesManager.saveComputerSettings();
                }
        );
        COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_ARRAY[0] = RelatedToSpinner.createSpinner(brightnessConfig, false);
        COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_ARRAY[0].setEnabled(isEnableSetBrightnessLevel);
        addLabeledSpinner(
                gbc, partialPanelContent, brightnessConfig,
                COMPUTER_SETTINGS_FOURTH_PARTIAL_SPINNER_ARRAY[0],
                true
        );

        COMPUTER_SETTINGS_GUI_DROP_DOWN_LIST_PANELS_ARRAY[3] = partialPanelContent;
        JPanel partialPanelFooter = createPartialPanelFooter();
        partialPanelFooter.setOpaque(false);
        mainPartialPanel.add(northPartialPanelContent, BorderLayout.NORTH);
        mainPartialPanel.add(partialPanelContent, BorderLayout.CENTER);
        mainPartialPanel.add(partialPanelFooter, BorderLayout.SOUTH);
        return mainPartialPanel;
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

    private static void setEnableRequestFocusInWindow(boolean isEnableRequestFocusInWindow){
        ComputerSettingsFourthPartialPanel.isEnableRequestFocusInWindow = isEnableRequestFocusInWindow;
    }
    private static boolean isEnableRequestFocusInWindow(){
        return isEnableRequestFocusInWindow;
    }
}