package com.battery_level_alarm.monitoring.tray_manager.modern_component;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.*;
import static com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme.SystemTheme.*;

import com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related.TrayTheme;
import com.notifications.system_tray_notifications.influence.PlaySounds;
import com.battery_level_alarm.monitoring.user_interface.ui_config.SoundItem;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.util.List;
import java.util.Objects;

public class JavaFXSoundComboBox {
	private static final Font STYLE = Font.font("Serif", 14);
	private static final String DARK_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/try-notifications-dark-style.css";
	private static final String GRAY_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/try-notifications-gray-style.css";
	private static final String CREAM_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/try-notifications-cream-style.css";
	private static final String LIGHT_THEME_FILE_PATH = "/com/battery_level_alarm/monitoring/Tray/Styles/try-notifications-light-style.css";
	
	public static HBox createSoundSelector(List<SoundItem> soundItems) {
		ComboBox<SoundItem> comboBox = new ComboBox<>(FXCollections.observableArrayList(soundItems));
		comboBox.getStyleClass().add("try-notification-combo");
		comboBox.setMinWidth(150);
		comboBox.setPrefWidth(150);
		comboBox.setMaxWidth(150);
		comboBox.setPromptText("Select Sound");
		comboBox.setCursor(Cursor.HAND);
		
		Label label = new Label();
		label.setFont(STYLE);
		comboBox.valueProperty().addListener((_, _, newVal) -> {
			if (newVal != null) {
				label.setText(newVal.name());
			}
		});
		
		Button playButton = new Button("▶");
		playButton.getStyleClass().add("try-notification-button");
		playButton.setOnAction(_ -> {
			SoundItem selected = comboBox.getValue();
			if (selected != null) {
				PlaySounds.playSound(selected.name());
			}
		});
		
		HBox hBox = new HBox(10, playButton, comboBox);
		hBox.setPadding(new Insets(5));
		return hBox;
	}
	
	public static VBox createNotificationSectionFX(List<SoundItem> soundItems) {
		Label title = new Label("🔔 Try Notification Sounds");
		title.setFont(Font.font("Serif", 16));
		
		HBox soundSelector = createSoundSelector(soundItems);
		soundSelector.getStyleClass().add("try-notification-hbox");
		
		VBox section = new VBox(10, title, soundSelector);
		section.setPadding(new Insets(15));
		section.getStyleClass().add("try-notification-vbox");
		
		String modeRaw = prefs.get("appTheme", AS_SYSTEM.toString());
		String mode = modeRaw.toUpperCase().replace(" ", "_");
		setVBoxThemeMode(section, SystemTheme.valueOf(mode));
		return section;
	}
	
	public static void setVBoxThemeMode(VBox section, SystemTheme mode) {
		switch (mode) {
			case DARK -> section.getStylesheets().add(Objects.requireNonNull(JavaFXSoundComboBox.class.getResource(DARK_THEME_FILE_PATH)).toExternalForm());
			case LIGHT -> section.getStylesheets().add(Objects.requireNonNull(JavaFXSoundComboBox.class.getResource(LIGHT_THEME_FILE_PATH)).toExternalForm());
			case GRAY -> section.getStylesheets().add(Objects.requireNonNull(JavaFXSoundComboBox.class.getResource(GRAY_THEME_FILE_PATH)).toExternalForm());
			case CREAM -> section.getStylesheets().add(Objects.requireNonNull(JavaFXSoundComboBox.class.getResource(CREAM_THEME_FILE_PATH)).toExternalForm());
			default -> {
				TrayTheme.SystemTheme theme = System.getProperty("os.name").toLowerCase().contains("mac") ? getMacTheme() : getSystemTheme();
				String systemMode = theme == DARK ? DARK_THEME_FILE_PATH : LIGHT_THEME_FILE_PATH;
				section.getStylesheets().add(Objects.requireNonNull(JavaFXSoundComboBox.class.getResource(systemMode)).toExternalForm());
			}
		}
	}
}