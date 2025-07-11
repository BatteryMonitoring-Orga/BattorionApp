package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab;
import static com.battery_level_alarm.monitoring.core_utilities.ComputerSettings.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.AS_SYSTEM;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.valueOf;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.monitorSystemTheme;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingActions.AppInterface.displayAppInterface;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingsTabHelper.createAutomationSection;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.applyTheme;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTab;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.settings_tab.SettingActions.*;

import com.battery_level_alarm.monitoring.core_utilities.UserChoices;
import com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager;
import com.battery_level_alarm.monitoring.tray_manager.modern_component.JavaFXSoundComboBox;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.main_executor.Monitor;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications.TrayAlerts;
import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.DashboardTab;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SoundItem;
import com.notifications.system_tray_notifications.basics.AlarmSounds;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SettingsTab {
	public static CheckBox enableSound = new CheckBox("Enable Notification Sound");
	public static CheckBox enableToast = new CheckBox("Enable Toast Notification");
	public static ComboBox<String> soundSelect = new ComboBox<>();
	public static Spinner<Integer> batteryMinLevel = new Spinner<>(10, 25, 25);
	public static Spinner<Integer> batteryMaxLevel = new Spinner<>(70, 95, 85);
	
	public static ComboBox<String> audioDeviceSelect = new ComboBox<>();
	public static Label currentDeviceLabel = new Label("Current Device:   ");
	public static TextField customDeviceField = new TextField();
	public static VBox notificationUI;
	private static String customDeviceFieldStyle;
	
	public static final String[] DEVICE_STATUS_MESSAGES_FOR_BACKGROUND_PROCESS = {
			"Added successfully", "Failed to add", "Device removed", "Removal failed", "Audio output set"
	};
	
	public static Tab createSettingsTab() {
		VBox content = new VBox(20,
				createToggleRunModeButton(),
				createAudioSettingsSection(),
				createAutomationSection(),
				createNotificationSection(),
				createThemeSection()
		);
		content.setPadding(insets);
		
		ScrollPane scrollPane = new ScrollPane(content);
		scrollPane.getStylesheets().add(Objects.requireNonNull(
				DashboardTab.class.getResource(STYLES_FILES_DIR_PATH + "/hide-scroll.css")
		).toExternalForm());
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		return createTab("Settings", scrollPane);
	}
	
	private static VBox createToggleRunModeButton() {
		Button toggleRunModeButton = new Button("Show Window");
		toggleRunModeButton.setMaxSize(110, 30);
		toggleRunModeButton.setPrefSize(110, 30);
		toggleRunModeButton.setMinSize(110, 30);
		toggleRunModeButton.setTooltip(new Tooltip("Restore the application window from background to foreground"));
		toggleRunModeButton.setOnAction(_ -> {
			if(isApplicationMode) {
				return;
			}
			displayAppInterface();
		});
		
		VBox runModeBox = new VBox(20,
				labeledNode("Display App Interface  ", toggleRunModeButton),
				createTabHeaderMode()
		);
		runModeBox.setPadding(new Insets(10));
		runModeBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		return runModeBox;
	}
	
	private static HBox createTabHeaderMode() {
		ComboBox<String> headerBox = new ComboBox<>();
		headerBox.getItems().addAll("Top", "Bottom");
		headerBox.setValue(prefs.get(TAB_HEADER_POSITION, "Bottom"));
		headerBox.setCursor(Cursor.HAND);
		headerBox.setMinWidth(112);
		headerBox.setPrefWidth(112);
		headerBox.setMaxWidth(112);
		headerBox.setOnAction(_ -> {
			String selected = headerBox.getValue();
			prefs.put(TAB_HEADER_POSITION, selected);
			setUpdateTabHeaderPosition(selected);
			setUpdateBoxPadding(selected);
			rebuildTabPanels();
			Monitor.isShouldUpdateTrayDashboard = true;
		});
		return new HBox(10, labeledNode("Tab Header Position\u2003", headerBox));
	}
	
	public static VBox createAudioSettingsSection() {
		currentDeviceLabel.setText("Current Device:   " + getCurrentAudioDevice());
		audioDeviceSelect.getItems().setAll(getAudioDevices());
		audioDeviceSelect.setValue(getCurrentAudioDevice());
		audioDeviceSelect.setCursor(Cursor.HAND);
		audioDeviceSelect.setPrefSize(120, 20);
		audioDeviceSelect.setStyle("-fx-font-size: 13px;");
		audioDeviceSelect.setOnAction(_ -> {
			String selected = audioDeviceSelect.getValue();
			setCurrentAudioDevice(selected);
			ConfigurationFilesManager.saveComputerSettings();
		});
		
		customDeviceField.setPrefHeight(25);
		customDeviceField.setPromptText("Enter the audio device name");
		customDeviceField.setStyle("-fx-font-size: 14px;");
		customDeviceFieldStyle = customDeviceField.getStyle();
		customDeviceField.focusedProperty().addListener((_, _, newVal) -> {
			if (newVal) {
				customDeviceField.setText("");
				customDeviceField.setStyle(customDeviceFieldStyle);
			}
		});
		
		VBox box = getBox();
		styleSection(box);
		return box;
	}
	
	private static @NotNull VBox getBox() {
		Hyperlink helpLink = new Hyperlink("How do I select the audio output?");
		helpLink.setStyle("-fx-font-size: 14px;");
		helpLink.setOnAction(_ -> TrayAlerts.howDoISelectTheAudioOutput());
		
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
		setAsDefaultAO.setOnAction(_ -> {
			RadioButtonsActions.setAsDefaultAOAction();
			Monitor.isShouldUpdateTrayDashboard = true;
		});
		return new HBox(10, useSelectedAO, addAudioDevice, deleteAudioDevice, setAsDefaultAO);
	}
	
	private static void setRadioButtonMouseListener(RadioButton radioButton, int index, String[] buttonNames) {
		radioButton.setOnMouseEntered(_ -> radioButton.setText(buttonNames[index]));
		radioButton.setOnMouseExited(_ -> radioButton.setText(""));
	}
	
	public static VBox createNotificationSection() {
		enableSound.setSelected(Boolean.parseBoolean(prefs.get(TRAY_NOTIFICATION_ENABLE, String.valueOf(true))));
		enableSound.setOnAction(_ -> {
			prefs.put(TRAY_NOTIFICATION_ENABLE, String.valueOf(enableSound.isSelected()));
			Monitor.changeFlag = true;
		});
		
		enableToast.setSelected(Boolean.parseBoolean(prefs.get(TOAST_NOTIFICATION_ENABLE, String.valueOf(true))));
		enableToast.setOnAction(_ -> {
			prefs.put(TOAST_NOTIFICATION_ENABLE, String.valueOf(enableToast.isSelected()));
			Monitor.changeFlag = true;
		});
		
		soundSelect.setCursor(Cursor.HAND);
		soundSelect.setVisibleRowCount(6);
		soundSelect.setValue(getNotificationSoundFileName());
		soundSelect.setOnAction(_ -> NotificationActions.updateNotificationSoundFileName(soundSelect.getValue()));
		if(soundSelect.getItems().isEmpty()) {
			soundSelect.getItems().addAll(AlarmSounds.getFullSoundSequence());
		}
		
		String[] soundNames = AlarmSounds.getFullSoundSequence();
		List<SoundItem> soundItems = Arrays.stream(soundNames).map(SoundItem::new).toList();
		notificationUI = JavaFXSoundComboBox.createNotificationSectionFX(soundItems);
		
		batteryMinLevel.getValueFactory().setValue(UserChoices.getMinimumLevel());
		batteryMinLevel.valueProperty().addListener((_, _, newVal) ->
				NotificationActions.updateMinimumLevel(newVal));
		
		batteryMaxLevel.getValueFactory().setValue(UserChoices.getMaximumLevel());
		batteryMaxLevel.valueProperty().addListener((_, _, newVal) ->
				NotificationActions.updateMaximumLevel(newVal));
		
		VBox section = new VBox(10,
				new Label("\uD83D\uDD14 Notifications"),
				enableSound,
				enableToast,
				notificationUI,
				labeledNode("Select Sound:  \u2003\u2003", soundSelect),
				labeledNode("Battery Min Level: ", batteryMinLevel),
				labeledNode("Battery Max Level:", batteryMaxLevel)
		);
		styleSection(section);
		return section;
	}
	
	private static VBox createThemeSection() {
		ComboBox<String> themeSelector = new ComboBox<>();
		List<String> themes = Arrays.stream(TrayTheme.SystemTheme.values()).map(Enum::name).toList();
		themeSelector.getItems().addAll(themes);
		
		String modeRaw = prefs.get(APP_THEME, AS_SYSTEM.toString());
		String mode = modeRaw.toUpperCase().replace(" ", "_");
		themeSelector.setValue(mode);
		
		themeSelector.setCursor(Cursor.HAND);
		themeSelector.setOnAction(_ -> {
			String selected = themeSelector.getValue();
			prefs.put(APP_THEME, valueOf(selected).toString());
			applyTheme(valueOf(selected));
			if(selected.equalsIgnoreCase(AS_SYSTEM.toString())) {
				monitorSystemTheme();
			}
		});
		
		VBox box = new VBox(10,
				new Label("\uD83C\uDFA8 Theme"),
				labeledNode("App Theme:   \u2003\u2003", themeSelector)
		);
		styleSection(box);
		return box;
	}
	
	static void styleSection(VBox box) {
		box.setPadding(new Insets(15));
		box.setStyle("-fx-border-color: lightgray; -fx-border-radius: 6; -fx-border-width: 1;");
	}
	
	public static HBox labeledNode(String labelText, Control control) {
		Label label = new Label(labelText);
		label.setMinHeight(Region.USE_PREF_SIZE);
		HBox box = new HBox(10, label, control);
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}
}
