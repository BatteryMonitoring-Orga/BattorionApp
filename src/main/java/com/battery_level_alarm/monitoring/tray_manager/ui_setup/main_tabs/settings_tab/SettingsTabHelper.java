package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.START_BATTORION_WITH;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.labeledNode;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTab.styleSection;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.DepartureModes.START_WITH_APPLICATION;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.DepartureModes.START_WITH_TRAY;
import com.battery_level_alarm.monitoring.core_utilities.ComputerSettings;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;

import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;
import java.util.function.Supplier;

class SettingsTabHelper {
	private static TextField brightnessLevelField;
	private static double brightnessLevelValue;
	
	static VBox createAutomationSection() {
		String mode = prefs.get(START_BATTORION_WITH, String.valueOf(START_WITH_APPLICATION));
		CheckBox startWithTray = new CheckBox("Start with Tray window");
		startWithTray.setSelected(mode.equals(String.valueOf(START_WITH_TRAY)));
		startWithTray.setTooltip(new Tooltip("""
			Tray Window: the small icon near the clock where the app runs in background.
			If enabled, the app will start hidden in the system tray.
			"""));
		
		startWithTray.selectedProperty().addListener((_, _, newValue) ->
			prefs.put(START_BATTORION_WITH, newValue ? String.valueOf(START_WITH_TRAY) : String.valueOf(START_WITH_APPLICATION)));
		
		VBox box = new VBox(10,
				new Label("\uD83D\uDEE0 Automation"),
				startWithTray,
				createSettingCheckbox(
						"Switch audio to speaker",
						"Force audio output to switch to speaker when alert occurs.",
						ComputerSettings::isEnableExchangeToSpeakerAudioOutput,
						ComputerSettings::setEnableExchangeToSpeakerAudioOutput
				),
				createSettingCheckbox(
						"Switch audio to last used device",
						"Switch back audio output to the previously used device after the alert.",
						ComputerSettings::isEnableExchangeToAudioOutputUsed,
						ComputerSettings::setEnableExchangeToAudioOutputUsed
				),
				createSettingCheckbox(
						"Allow volume change",
						"Allow the app to change system volume during alerts.",
						ComputerSettings::isEnablingSoundLevelChange,
						ComputerSettings::setEnablingSoundLevelChange
				),
				createSettingCheckbox(
						"Restore volume after alert",
						"Automatically restore volume level after the battery alert is over.",
						ComputerSettings::isRestoringSoundLevelAfterAlert,
						ComputerSettings::setRestoringSoundLevelAfterAlert
				),
				createSettingCheckbox(
						"Unmute sound automatically",
						"Unmute the system volume when an alert needs sound.",
						ComputerSettings::isEnableUnmuteVolumeAutomatically,
						ComputerSettings::setEnableUnmuteVolumeAutomatically
				),
				createBrightnessBox(),
				createSlider()
		);
		styleSection(box);
		return box;
	}
	
	private static CheckBox createSettingCheckbox(
			String text, String tooltip, Supplier<Boolean> getter, Consumer<Boolean> setter
	) {
		CheckBox checkBox = new CheckBox(text);
		checkBox.setSelected(getter.get());
		checkBox.setTooltip(new Tooltip(tooltip));
		checkBox.selectedProperty().addListener((_, _, newValue) -> {
			setter.accept(newValue);
			ConfigurationFilesManager.saveComputerSettings();
		});
		return checkBox;
	}
	
	private static HBox createBrightnessBox() {
		brightnessLevelField = new TextField();
		brightnessLevelField.setEditable(false);
		brightnessLevelField.setMinSize(25, 30);
		brightnessLevelField.setPrefSize(25, 30);
		brightnessLevelField.setMaxSize(25, 30);
		Thread.ofVirtual().start(() -> {
			Brightness.BrightnessProcess(0, true);
			Platform.setImplicitExit(false);
			brightnessLevelValue = Brightness.getCurrentBrightness();
			Platform.runLater(() -> brightnessLevelField.setText(brightnessLevelValue + ""));
		});
		
		return new HBox(10, labeledNode("PC - Brightness Level: ", brightnessLevelField));
	}
	
	private static Slider createSlider() {
		Slider slider = new Slider(0, 100, 50);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setBlockIncrement(2);
		slider.setCursor(Cursor.HAND);
		Thread.ofVirtual().start(() -> {
			Brightness.BrightnessProcess(0, true);
			brightnessLevelValue = Brightness.getCurrentBrightness();
			Platform.setImplicitExit(false);
			Platform.runLater(() -> {
				slider.setValue(brightnessLevelValue);
				slider.setMajorTickUnit(25);
			});
		});
		
		slider.valueChangingProperty().addListener((_, _, isChanging) -> {
			if (!isChanging) {
				Brightness.BrightnessProcess((int) slider.getValue(), false);
				brightnessLevelField.setText((int) slider.getValue() + "");
			}
		});
		slider.valueProperty().addListener((_, _, newVal) -> {
			if (!slider.isValueChanging()) {
				Brightness.BrightnessProcess((int) newVal.doubleValue(), false);
				brightnessLevelField.setText((int) slider.getValue() + "");
			}
		});
		return slider;
	}
}