package com.battery_level_alarm.monitoring.tray_manager.ui_setup;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.DepartureModes.START_WITH_APPLICATION;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.DepartureModes.START_WITH_TRAY;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.BattorionTrayUI.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.TrayTheme.applyTheme;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.UITabs.createBackButton;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.UITabs.createTab;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.SettingActions.*;

import com.battery_level_alarm.monitoring.core_utilities.StaticQuestionnaire;
import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.visual_effects.Brightness;
import com.notifications.system_tray_notifications.basics.AlarmSounds;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

public class SettingsTab {
	public static CheckBox enableSound = new CheckBox("Enable Notification Sound");
	public static ComboBox<String> soundSelect = new ComboBox<>();
	public static Spinner<Integer> batteryMinLevel = new Spinner<>(10, 25, 25);
	public static Spinner<Integer> batteryMaxLevel = new Spinner<>(70, 95, 85);
	
	public static ComboBox<String> audioDeviceSelect = new ComboBox<>();
	public static Label currentDeviceLabel = new Label("Current Device:   ");
	public static TextField customDeviceField = new TextField();
	
	public static final String[] DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS = {
			"Added successfully", "Failed to add", "Device removed", "Removal failed", "Audio output set"
	};
	
	static Tab createSettingsTab() {
		VBox content = new VBox(20,
				createBackButton(),
				createAudioSettingsSection(),
				createAutomationSection(),
				createNotificationSection(),
				createThemeSection()
		);
		content.setPadding(new Insets(20));
		
		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		return createTab("Settings", scrollPane);
	}
	
	public static VBox createAudioSettingsSection() {
		audioDeviceSelect.getItems().setAll(getAudioDevices());
		audioDeviceSelect.setValue(getCurrentAudioDevice());
		audioDeviceSelect.setPrefSize(120, 20);
		audioDeviceSelect.setStyle("-fx-font-size: 13px;");
		audioDeviceSelect.setOnAction(_ -> {
			String selected = audioDeviceSelect.getValue();
			currentDeviceLabel.setText("Current Device:   " + selected);
			setCurrentAudioDevice(selected);
			ConfigurationFilesManager.saveComputerSettings();
		});
		
		currentDeviceLabel.setText("Current Device:   " + getCurrentAudioDevice());
		customDeviceField.setPrefHeight(25);
		customDeviceField.setStyle("-fx-font-size: 14px;");
		VBox box = getBox();
		styleSection(box);
		return box;
	}
	
	private static @NotNull VBox getBox() {
		Hyperlink helpLink = new Hyperlink("How do I select the audio output?");
		helpLink.setOnAction(_ -> StaticQuestionnaire.aboutSoundSettingsGuide());
		
		HBox deviceSelector = labeledNode("Choose Device:", audioDeviceSelect);
		HBox radioGroupBox = getRadioGroupBox();
		return new VBox(10,
				new Label("\uD83C\uDFA7 Audio Output"),
				currentDeviceLabel,
				deviceSelector,
				new Label("Custom Device Name: "),
				customDeviceField,
				radioGroupBox,
				helpLink
		);
	}
	
	private static HBox getRadioGroupBox() {
		String[] names = { "Use the selected AO", "Add", "Delete", "Set it as AO"};
		RadioButton useSelectedAO = new RadioButton();
		RadioButton addAudioDevice = new RadioButton();
		RadioButton deleteAudioDevice = new RadioButton();
		RadioButton setAsDefaultAO = new RadioButton();
		
		useSelectedAO.setTooltip(new Tooltip("Use the selected audio output"));
		addAudioDevice.setTooltip(new Tooltip("Add a new audio output"));
		deleteAudioDevice.setTooltip(new Tooltip("Delete the selected audio output"));
		setAsDefaultAO.setTooltip(new Tooltip("Set it as the audio output"));
		
		setRadioButtonMouseListener(useSelectedAO, 0, names);
		setRadioButtonMouseListener(addAudioDevice, 1, names);
		setRadioButtonMouseListener(deleteAudioDevice, 2, names);
		setRadioButtonMouseListener(setAsDefaultAO, 3, names);
		
		ToggleGroup audioModeGroup = new ToggleGroup();
		useSelectedAO.setToggleGroup(audioModeGroup);
		addAudioDevice.setToggleGroup(audioModeGroup);
		deleteAudioDevice.setToggleGroup(audioModeGroup);
		setAsDefaultAO.setToggleGroup(audioModeGroup);
		
		useSelectedAO.setOnAction(_ -> customDeviceField.setText(getCurrentAudioDevice()));
		addAudioDevice.setOnAction(_ -> RadioButtonsActions.addAudioDeviceAction());
		deleteAudioDevice.setOnAction(_ -> RadioButtonsActions.deleteAudioDeviceAction());
		setAsDefaultAO.setOnAction(_ -> RadioButtonsActions.setAsDefaultAOAction());
		return new HBox(10, useSelectedAO, addAudioDevice, deleteAudioDevice, setAsDefaultAO);
	}
	
	private static void setRadioButtonMouseListener(RadioButton radioButton, int index, String[] buttonNames) {
		radioButton.setOnMouseEntered(_ -> radioButton.setText(buttonNames[index]));
		radioButton.setOnMouseExited(_ -> radioButton.setText(""));
	}
	
	private static VBox createAutomationSection() {
		String mode = prefs.get("StartBattorionWith", String.valueOf(START_WITH_APPLICATION));
		CheckBox startWithTray = new CheckBox("Start with Tray window");
		startWithTray.setSelected(mode.equals(String.valueOf(START_WITH_TRAY)));
		startWithTray.setTooltip(new Tooltip(
				"Tray Window: the small icon near the clock where the app runs in background.\n"
					+ "If enabled, the app will start hidden in the system tray."
		));
		startWithTray.selectedProperty().addListener((_, _, newValue) -> {
			String newMode = newValue ? String.valueOf(START_WITH_TRAY) : String.valueOf(START_WITH_APPLICATION);
			prefs.put("StartBattorionWith", newMode);
		});
		
		VBox box = new VBox(10,
				new Label("\uD83D\uDEE0 Automation"),
				startWithTray,
				new Label("PC - Brightness Level"),
				createSlider()
		);
		styleSection(box);
		return box;
	}
	
	private static Slider createSlider() {
		Slider slider = new Slider(0, 100, 50);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setBlockIncrement(2);
		Thread.ofVirtual().start(() -> {
			Brightness.BrightnessProcess(0, true);
			double current = Brightness.getCurrentBrightness();
			Platform.runLater(() -> {
				slider.setValue(current);
				slider.setMajorTickUnit(25);
			});
		});
		
		slider.valueChangingProperty().addListener((_, _, isChanging) -> {
			if (!isChanging) {
				Brightness.BrightnessProcess((int) slider.getValue(), false);
			}
		});
		slider.valueProperty().addListener((_, _, newVal) -> {
			if (!slider.isValueChanging()) {
				Brightness.BrightnessProcess((int) newVal.doubleValue(), false);
			}
		});
		return slider;
	}
	
	public static VBox createNotificationSection() {
		soundSelect.getItems().addAll(AlarmSounds.getFullSoundSequence());
		enableSound.setSelected(Boolean.parseBoolean(prefs.get("trayNotificationEnable", String.valueOf(true))));
		enableSound.setOnAction(_ -> prefs.put("trayNotificationEnable", String.valueOf(enableSound.isSelected())));
		
		soundSelect.setValue(getNotificationSoundFileName());
		soundSelect.setOnAction(_ -> NotificationActions.updateNotificationSoundFileName(
				soundSelect.getValue(), soundSelect.getSelectionModel().getSelectedIndex()));
		
		batteryMinLevel.getValueFactory().setValue(UserChoices.getMinimumLevel());
		batteryMinLevel.valueProperty().addListener((_, _, newVal) ->
				NotificationActions.updateMinimumLevel(newVal));
		
		batteryMaxLevel.getValueFactory().setValue(UserChoices.getMaximumLevel());
		batteryMaxLevel.valueProperty().addListener((_, _, newVal) ->
				NotificationActions.updateMaximumLevel(newVal));
		
		VBox section = new VBox(10,
				new Label("\uD83D\uDD14 Notifications"),
				enableSound,
				labeledNode("Select Sound:", soundSelect),
				labeledNode("Battery Min Level:", batteryMinLevel),
				labeledNode("Battery Max Level:", batteryMaxLevel)
		);
		styleSection(section);
		return section;
	}
	
	private static VBox createThemeSection() {
		ComboBox<String> themeSelector = new ComboBox<>();
		themeSelector.getItems().addAll("Light", "Dark", "As System");
		themeSelector.setValue(prefs.get("appTheme", "As System"));
		themeSelector.setOnAction(_ -> {
			String selected = themeSelector.getValue();
			prefs.put("appTheme", selected);
			applyTheme(selected);
		});
		
		VBox box = new VBox(10,
				new Label("\uD83C\uDFA8 Theme"),
				labeledNode("App Theme:", themeSelector)
		);
		styleSection(box);
		return box;
	}
	
	private static void styleSection(VBox box) {
		box.setPadding(new Insets(15));
		box.setStyle("-fx-border-color: lightgray; -fx-border-radius: 6; -fx-border-width: 1;");
	}
	
	private static HBox labeledNode(String labelText, Control control) {
		Label label = new Label(labelText);
		label.setMinHeight(Region.USE_PREF_SIZE);
		HBox box = new HBox(10, label, control);
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}
}
