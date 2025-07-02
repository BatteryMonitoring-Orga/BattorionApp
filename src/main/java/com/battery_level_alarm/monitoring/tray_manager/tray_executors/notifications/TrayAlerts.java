package com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.APP_VERSION;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.MAIN_FOLDER_NAME;
import com.battery_level_alarm.monitoring.battery_report.HTMLOpener;

import com.battery_level_alarm.monitoring.questionnaires.AppendixesQuestionnaire;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Cursor;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TrayAlerts {
	public static void showAboutDialog() {
		Map<String, Runnable> actionsMap = getStringRunnableMap();
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			VBox content = new VBox(10);
			content.setPadding(new Insets(10));
			content.setPrefWidth(600);
			content.setFillWidth(true);
			
			Label title = new Label("About Battorion Tray");
			title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
			Label version = new Label("Version: " + APP_VERSION);
			Label author = new Label("Author: Muath Hassoun");
			
			WebView description = getDescription();
			description.setMaxWidth(Double.MAX_VALUE);
			description.setStyle("""
				-fx-background-color: #f9f9f9;
				-fx-border-color: #ddd;
				-fx-border-radius: 4px;
				-fx-background-radius: 4px;
			""");
			
			ScrollPane scrollPane = getScrollPane(description, 580, 200);
			VBox.setVgrow(scrollPane, Priority.ALWAYS);
			
			Hyperlink arabicGuide = new Hyperlink("📘 Comprehensive guide in Arabic");
			arabicGuide.setOnAction(_ -> actionsMap.get("action:openComprehensiveBatteryGuideInArabic").run());
			Hyperlink englishGuide = new Hyperlink("📘 Comprehensive guide in English");
			englishGuide.setOnAction(_ -> actionsMap.get("action:openComprehensiveBatteryGuideInEnglish").run());
			
			content.getChildren().addAll(title, version, author, scrollPane, arabicGuide, englishGuide);
			createInstructionAlert("About Battorion", "Application Overview", content, null);
		});
	}
	
	private static @NotNull WebView getDescription() {
		WebView description = new WebView();
		description.setPrefHeight(300);
		description.setContextMenuEnabled(false);
		description.getEngine().loadContent(getTrayIntegrationText());
		return description;
	}
	
	private static @NotNull ScrollPane getScrollPane(WebView description, int viewPortWidth, int viewPortHeight) {
		VBox wrapper = new VBox(description);
		wrapper.setPadding(new Insets(5));
		wrapper.setFillWidth(true);
		wrapper.setPrefWidth(viewPortWidth);
		
		ScrollPane scrollPane = new ScrollPane(wrapper);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefViewportHeight(viewPortHeight);
		scrollPane.setPrefViewportWidth(viewPortWidth);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setStyle("""
		    -fx-background-color: transparent;
		    -fx-border-color: #cccccc;
		    -fx-border-radius: 6px;
		    -fx-background-radius: 6px;
		    -fx-padding: 4px;
		""");
		return scrollPane;
	}
	
	public static String getTrayIntegrationText() {
		return """
				<html>
						<body style="font-family: Serif, sans-serif; padding: 10px;">
								<p><b>Battorion Tray</b> is a lightweight background utility that keeps you informed about your battery status directly from the system tray.</p>
							\s
								<p>It operates silently without opening any main window and provides quick access to essential functions like:</p>
								<ul>
										<li>Enabling or disabling battery alerts.</li>
										<li>Viewing battery level and charging status.</li>
										<li>Accessing comprehensive battery guides.</li>
								</ul>
							\s
								<p><b>Features:</b></p>
								<ul>
										<li>High Battery Alert at 85% or above.</li>
										<li>Low Battery Alert at 25% or below.</li>
										<li>Works silently from the tray on Windows, Linux, and macOS.</li>
								</ul>
							\s
								<p>To access tray features:</p>
								<ul>
										<li>Right-click the Battorion icon in the system tray.</li>
										<li>Choose from the available menu options.</li>
								</ul>
							\s
								<p>Thank you for using <b>Battorion Tray</b>!</p>
						</body>
				</html>
				\t""";
	}
	
	private static @NotNull Map<String, Runnable> getStringRunnableMap() {
		Map<String, Runnable> actionsMap = new HashMap<>();
		actionsMap.put("action:openComprehensiveBatteryGuideInArabic",
				() -> HTMLOpener.open(System.getProperty("user.home") + MAIN_FOLDER_NAME + "/comprehensive_guide-main/Comprehensive Guide - Arabic.html")
		);
		actionsMap.put("action:openComprehensiveBatteryGuideInEnglish",
				() -> HTMLOpener.open(System.getProperty("user.home") + MAIN_FOLDER_NAME + "/comprehensive_guide-main/Comprehensive Guide - English.html")
		);
		return actionsMap;
	}
	
	public static void showTrayPinInstructionsFX() {
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			String os = System.getProperty("os.name").toLowerCase();
			VBox content = getTrayInstructionsVBox(os);
			String fallbackText = getFallbackText(os);
			createInstructionAlert("Tray Icon Pinning Guide", "How to Pin Battorion to System Tray", content, fallbackText);
		});
	}
	
	public static void howDoISelectTheAudioOutput() {
		VBox content = new VBox(10);
		content.setPadding(new Insets(10));
		content.setPrefWidth(600);
		content.setFillWidth(true);
		
		Label title = new Label("How do i select the audio output?");
		title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		
		WebView description = new WebView();
		description.setPrefHeight(300);
		description.setContextMenuEnabled(false);
		description.getEngine().loadContent(AppendixesQuestionnaire.aboutSelectAudioDevice());
		description.setMaxWidth(Double.MAX_VALUE);
		description.setStyle("""
				-fx-background-color: #f9f9f9;
				-fx-border-color: #ddd;
				-fx-border-radius: 4px;
				-fx-background-radius: 4px;
			""");
		
		ScrollPane scrollPane = getScrollPane(description, 700, 320);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		content.getChildren().addAll(title, scrollPane);
		createInstructionAlert("Select Audio Output Device", "Select Audio Output Device", content, null);
	}
	
	private static void createInstructionAlert(String title, String header, VBox content, String fallbackText) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setResizable(false);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		
		if (content != null) {
			alert.getDialogPane().setContent(content);
		} else {
			alert.setContentText(fallbackText);
		}
		
		for (ButtonType buttonType : alert.getButtonTypes()) {
			Button button = (Button) alert.getDialogPane().lookupButton(buttonType);
			button.setCursor(Cursor.HAND);
		}
		
		DialogPane pane = alert.getDialogPane();
		pane.getStylesheets().add(Objects.requireNonNull(TrayAlerts.class.getResource("/com/battery_level_alarm/monitoring/Tray/Styles/dialog-style.css")).toExternalForm());
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setResizable(false);
		stage.getIcons().add(new Image(Objects.requireNonNull(
				TrayAlerts.class.getResource("/com/battery_level_alarm/monitoring/Tray/Icons/info.png")).toExternalForm()));
		alert.showAndWait();
	}
	
	private static VBox getTrayInstructionsVBox(String os) {
		if (os.contains("win")) {
			URL arrowUrl = TrayAlerts.class.getResource("/com/battery_level_alarm/monitoring/Tray/Icons/up_arrow.png");
			HBox line1 = getLine1(arrowUrl);
			
			Label label2 = new Label("""
			2. Drag the Battorion icon and drop it into the visible tray area.

			3. To make the icon always visible, open 'Taskbar Settings':
			   - Right-click on the taskbar.
			   - Choose 'Taskbar settings'.
			   - Scroll to 'Notification area' > 'Select which icons appear on the taskbar'.
			   - Turn on Battorion from the list.
			""");
			
			VBox contentBox = new VBox(10, line1, label2);
			contentBox.setPadding(new Insets(10));
			return contentBox;
		}
		return null;
	}
	
	private static String getFallbackText(String os) {
		if (os.contains("mac")) {
			return """
				macOS does not support direct tray pinning like Windows.
				However, you can keep the app in the Dock by right-clicking the Dock icon
				and selecting "Options" > "Keep in Dock".
			""";
		} else if (os.contains("nux") || os.contains("nix")) {
			return """
				On Linux, tray behavior depends on your desktop environment.
				In most cases, you can configure the system tray visibility
				via your system settings or extensions.
			""";
		}
		return "Tray pinning instructions are not available for your OS.";
	}
	
	private static @NotNull HBox getLine1(URL arrowUrl) {
		HBox line1;
		if (arrowUrl != null) {
			ImageView arrowIcon = new ImageView(new Image(arrowUrl.toExternalForm()));
			arrowIcon.setFitWidth(16);
			arrowIcon.setFitHeight(16);
			Label text1 = new Label("1. Click the ");
			Label text2 = new Label(" arrow in the bottom-right system tray.");
			line1 = new HBox(5, text1, arrowIcon, text2);
			line1.setAlignment(Pos.CENTER_LEFT);
		} else {
			line1 = new HBox(5, new Label("1. Click the '△' arrow in the bottom-right system tray."));
		}
		return line1;
	}
}
