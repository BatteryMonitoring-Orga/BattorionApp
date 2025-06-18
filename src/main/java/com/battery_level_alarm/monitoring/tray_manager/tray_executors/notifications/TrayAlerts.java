package com.battery_level_alarm.monitoring.tray_manager.tray_executors.notifications;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.APP_VERSION;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.MAIN_FOLDER_NAME;
import com.battery_level_alarm.monitoring.battery_report.HTMLOpener;

import javafx.scene.layout.Priority;
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
			
			Label description = getDescription();
			description.setPadding(new Insets(5));
			description.setWrapText(true);
			description.setMaxWidth(Double.MAX_VALUE);
			description.setStyle("""
				-fx-background-color: #f9f9f9;
				-fx-border-color: #ddd;
				-fx-border-radius: 4px;
				-fx-background-radius: 4px;
			""");
			
			ScrollPane scrollPane = getScrollPane(description);
			VBox.setVgrow(scrollPane, Priority.ALWAYS);
			
			Hyperlink arabicGuide = new Hyperlink("📘 Comprehensive guide in Arabic");
			arabicGuide.setOnAction(_ -> actionsMap.get("action:openComprehensiveBatteryGuideInArabic").run());
			Hyperlink englishGuide = new Hyperlink("📘 Comprehensive guide in English");
			englishGuide.setOnAction(_ -> actionsMap.get("action:openComprehensiveBatteryGuideInEnglish").run());
			
			content.getChildren().addAll(title, version, author, scrollPane, arabicGuide, englishGuide);
			createInstructionAlert("About Battorion", "Application Overview", content, null);
		});
	}
	
	private static @NotNull ScrollPane getScrollPane(Label description) {
		VBox wrapper = new VBox(description);
		wrapper.setPadding(new Insets(5));
		wrapper.setFillWidth(true);
		wrapper.setPrefWidth(600);
		
		ScrollPane scrollPane = new ScrollPane(wrapper);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefViewportHeight(200);
		scrollPane.setPrefViewportWidth(600);
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
	
	private static @NotNull Label getDescription() {
		Label description = new Label("""
			Battorion Tray is a lightweight background utility that keeps you informed about
			your battery status directly from the system tray.
		\t
			It operates silently without opening any main window and provides quick access to
			essential functions like:
			- Enabling or disabling battery alerts.
			- Viewing battery level and charging status.
			- Accessing comprehensive battery guides.
		\t
			Features:
			- High Battery Alert at 85% or above.
			- Low Battery Alert at 25% or below.
			- Works silently from the tray on Windows, Linux, and macOS.
		\t
			To access tray features:
			- Right-click the Battorion icon in the system tray.
			- Choose from the available menu options.
		\t
			Thank you for using Battorion Tray!
		\t""");
		description.setWrapText(true);
		return description;
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
