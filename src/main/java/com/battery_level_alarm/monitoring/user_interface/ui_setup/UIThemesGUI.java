package com.battery_level_alarm.monitoring.user_interface.ui_setup;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveGeneralConfigurations;
import static com.battery_level_alarm.monitoring.skeleton_constraints.RecordConfigurations.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.rebuild;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_constraints.GridBagConstraintsDetails.setDimension;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.*;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.OtherComponentsConfig.addLabeledColorPicker;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.RelatedToButtons.addButton;
import static com.battery_level_alarm.monitoring.user_interface.ui_static_configs.UIStaticObjects.Spaces.ONE_SPACE;
import static com.battery_level_alarm.monitoring.visual_effects.appearance.ThemesStatics.ThemeNames.getThemeNames;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientThemes.DARK_GRADIENTS;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.GradientThemes.LIGHT_GRADIENTS;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.getAppliedEndColor;
import static com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler.getAppliedStartColor;

import com.battery_level_alarm.monitoring.user_interface.ui_config.ScrollConfiguration;
import com.battery_level_alarm.monitoring.visual_effects.appearance.Appearance;
import com.battery_level_alarm.monitoring.visual_effects.gradient.GradientPreview;
import com.battery_level_alarm.monitoring.visual_effects.gradient.PanelStyler;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class UIThemesGUI {
    private static final ScrollConfiguration SCROLL_PANEL_CONFIGURATION = new ScrollConfiguration(
            false, true, true,
            false, null, new Dimension(600, 350)
    );

    private static JPanel UIThemeGUI;
    private static String selectedDarkTheme;
    private static String selectedLightTheme;
    public static boolean customizationGradientBackground;

    public static JPanel getUIThemeGUI() {
        return UIThemeGUI;
    }

    public static void createAndShowGUI() {
        JPanel uiThemePanel = new JPanel();
        uiThemePanel.setLayout(new BoxLayout(uiThemePanel, BoxLayout.Y_AXIS));
        GridBagConstraints gbc = createGridBagConstraints(GRID_BAG_CONSTRAINTS_CONFIGURATION);

        int index = 0;
        JPanel footerPanel = new JPanel(new GridLayout(1, 2));
        JPanel previewButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton previewButton = addButton(
                gbc, new JPanel(), "Preview Gradient",
                _ -> GradientPreview.newGradientPreview()
        );
        previewButtonPanel.add(previewButton);

        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox isCustomization = addCheckbox(
                gbc, new JPanel(), "Apply a custom gradient background",
                customizationGradientBackground,
                e -> {
                    JCheckBox source = (JCheckBox) e.getSource();
                    customizationGradientBackground = source.isSelected();
                    saveGeneralConfigurations();
                }
        );
        checkBoxPanel.add(isCustomization);
        footerPanel.add(checkBoxPanel);
        footerPanel.add(previewButtonPanel);

        JPanel firstPartPanel = createFirstPartPanel(gbc, index);
        JPanel secondPartPanel = createSecondPartPanel(gbc, index);
        returnGBC$ToDefault(gbc);
        uiThemePanel.add(firstPartPanel);
        uiThemePanel.add(secondPartPanel);
        JScrollPane uiThemeScroll = new JScrollPane(uiThemePanel);
        applyScrollConfigurationDetails(uiThemeScroll, SCROLL_PANEL_CONFIGURATION);

        UIThemeGUI = new JPanel(new BorderLayout());
        UIThemeGUI.setBorder(null);
        UIThemeGUI.setOpaque(false);
        UIThemeGUI.add(uiThemeScroll, BorderLayout.CENTER);
        UIThemeGUI.add(footerPanel, BorderLayout.SOUTH);
    }

    private static JPanel createFirstPartPanel(GridBagConstraints gbc, int index){
        JPanel partMainPanel = new JPanel(new GridBagLayout());
        setDimension(index, 0);
        addLabeledComboBox(
                gbc, partMainPanel, "User Interface Theme Selection", getThemeNames(), Appearance.getThemeName(), 4,
                e -> {
                    @SuppressWarnings("unchecked")
                    JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                    String selectedTheme = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
                    if(Appearance.getThemeName().equals(selectedTheme)){
                        return;
                    }

                    Appearance.setThemeName(selectedTheme);
                    saveGeneralConfigurations();
                    rebuild();
                }, 200, 30
        );

        setDimension(++index, 0);
        String[] darkThemes = DARK_GRADIENTS.keySet().toArray(new String[0]);
        addLabeledComboBox(
                gbc, partMainPanel, "Dark Mode Gradient Background Style", darkThemes,
                PanelStyler.getGradientBackgroundDarkModeName(), 4,
                e -> {
                    @SuppressWarnings("unchecked")
                    JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                    selectedDarkTheme = Objects.requireNonNull(comboBox.getSelectedItem()).toString();

                    Color[] colors = DARK_GRADIENTS.get(selectedDarkTheme);
                    GradientPreview.setStartPreviewColor(colors[0]);
                    GradientPreview.setEndPreviewColor(colors[1]);
                }, 200, 30
        );

        setDimension(++index, 0);
        String[] lightThemes = LIGHT_GRADIENTS.keySet().toArray(new String[0]);
        addLabeledComboBox(
                gbc, partMainPanel, "Light Mode Gradient Background Style", lightThemes,
                PanelStyler.getGradientBackgroundLightModeName(), 4,
                e -> {
                    @SuppressWarnings("unchecked")
                    JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                    selectedLightTheme = Objects.requireNonNull(comboBox.getSelectedItem()).toString();

                    Color[] colors = LIGHT_GRADIENTS.get(selectedLightTheme);
                    GradientPreview.setStartPreviewColor(colors[0]);
                    GradientPreview.setEndPreviewColor(colors[1]);
                }, 200, 30
        );

        setDimension(++index, 1);
        addButton(
                gbc, partMainPanel, "Save Selected Theme",
                _ -> {
                    PanelStyler.setGradientBackgroundDarkModeName(selectedDarkTheme);
                    PanelStyler.setGradientBackgroundLightModeName(selectedLightTheme);
                    saveGeneralConfigurations();
                    rebuild();
                }
        );
        return partMainPanel;
    }

    private static JPanel createSecondPartPanel(GridBagConstraints gbc, int index){
        JPanel partMainPanel = new JPanel(new GridBagLayout());
        setDimension(index, 0);
        addSeparator(gbc, partMainPanel, 150);
        returnGBC$ToDefault(gbc);

        setDimension(++index, 0);
        addLabeledColorPicker(
                gbc, partMainPanel,
                "Set Gradient Starting Color   " + ONE_SPACE,
                getAppliedStartColor(),
                GradientPreview::setStartPreviewColor,
                40, 30
        );

        setDimension(++index, 0);
        addLabeledColorPicker(
                gbc, partMainPanel,
                "Set Gradient Ending Color",
                getAppliedEndColor(),
                GradientPreview::setEndPreviewColor,
                40, 30
        );

        setDimension(++index, 1);
        JButton saveCustomColorButton = addButton(
                gbc, new JPanel(), "Save Custom Colors",
                _ -> {
                    saveGeneralConfigurations();
                    rebuild();
                }
        );
        saveCustomColorButton.setToolTipText(
                "Save your selected gradient colors.\nNote: This will only apply if \n'Apply a custom gradient background' is enabled."
        );
        gbc.gridx = getColumn();
        gbc.gridy = getRow();
        partMainPanel.add(saveCustomColorButton, gbc);
        return partMainPanel;
    }
}