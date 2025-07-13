package com.battery_level_alarm.monitoring.feedback_system;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;

public class FeedbackPopup {
	public static void feedback(boolean isDarkTheme) {
		showFeedbackPopup(isDarkTheme);
	}
	
	private static void showFeedbackPopup(boolean isDarkTheme) {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.initStyle(StageStyle.DECORATED);
		popupStage.setTitle("Your Feedback Matters");
		
		String backgroundColor = isDarkTheme ? "rgba(255, 255, 255, 0.9)" : "rgba(0, 0, 0, 0.85)";
		String textColor = isDarkTheme ? "black" : "white";
		
		TextField nameField = new TextField();
		nameField.setPromptText("Your Name");
		
		TextField emailField = new TextField();
		emailField.setPromptText("Your Email");
		
		TextArea feedbackArea = new TextArea();
		feedbackArea.setPromptText("Write your feedback here...");
		feedbackArea.setWrapText(true);
		feedbackArea.setPrefRowCount(8);
		feedbackArea.setMaxHeight(Double.MAX_VALUE);
		
		ScrollPane scrollPane = new ScrollPane(feedbackArea);
		scrollPane.setFitToWidth(true);
		scrollPane.setPrefViewportHeight(120);
		scrollPane.setMaxHeight(150);
		scrollPane.setStyle("-fx-background-color: transparent;");
		
		Button submitBtn = new Button("Submit");
		Button closeBtn = new Button("Close");
		
		submitBtn.setDefaultButton(true);
		closeBtn.setCancelButton(true);
		
		closeBtn.setOnAction(_ -> popupStage.close());
		submitBtn.setOnAction(_ -> {
			String name = nameField.getText().trim();
			String email = emailField.getText().trim();
			String feedback = feedbackArea.getText().trim();
			
			if (name.isBlank() || email.isBlank() || feedback.isBlank()) {
				showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
			} else {
				popupStage.close();
				String userId = BattorionCoreConstants.UserIdentifier.getOrCreateUserId();
				Thread.ofVirtual().start(() -> {
					if (isInternetAvailable()) {
						Thread.ofVirtual().start(() -> FeedbackSender.sendFeedback(userId, name, email, feedback));
					} else {
						Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "No internet connection. Please try again later."));
					}
				});
			}
		});
		
		String buttonStyle = getButtonStyle(isDarkTheme);
		String buttonHoverStyle = String.format("""
		    -fx-background-color: %s;
		""", isDarkTheme ? "#00000033" : "#ffffff44");
		
		submitBtn.setStyle(buttonStyle);
		closeBtn.setStyle(buttonStyle);
		
		submitBtn.setOnMouseEntered(_ -> submitBtn.setStyle(buttonStyle + buttonHoverStyle));
		submitBtn.setOnMouseExited(_ -> submitBtn.setStyle(buttonStyle));
		closeBtn.setOnMouseEntered(_ -> closeBtn.setStyle(buttonStyle + buttonHoverStyle));
		closeBtn.setOnMouseExited(_ -> closeBtn.setStyle(buttonStyle));
		
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		VBox form = new VBox(10,
				styledLabel("Your Feedback Matters", textColor, 20),
				styledLabel("Name:", textColor, 15), nameField,
				styledLabel("Email:", textColor, 15), emailField,
				styledLabel("Feedback:", textColor, 15), scrollPane,
				new HBox(10, submitBtn, closeBtn)
		);
		((HBox) form.getChildren().getLast()).setAlignment(Pos.CENTER_RIGHT);
		form.setPadding(new Insets(20));
		form.setAlignment(Pos.CENTER_LEFT);
		form.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 10;");
		
		Scene popupScene = new Scene(form, 400, 500);
		popupScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
		Image icon = new Image(Objects.requireNonNull(FeedbackPopup.class.getResourceAsStream(ASSETS_FOLDER_PATH + "feedback.png")));
		
		popupStage.getIcons().add(icon);
		popupStage.initStyle(StageStyle.TRANSPARENT);
		popupStage.setScene(popupScene);
		popupStage.showAndWait();
	}
	
	private static @NotNull String getButtonStyle(boolean isDarkTheme) {
		String buttonBackground = isDarkTheme ? "#00000022" : "#ffffff22";
		String buttonTextColor = isDarkTheme ? "black" : "white";
		String buttonBorder = isDarkTheme ? "black" : "white";
		return String.format("""
		    -fx-background-color: %s;
		    -fx-text-fill: %s;
		    -fx-font-size: 14px;
		    -fx-padding: 8px 16px;
		    -fx-border-radius: 8px;
		    -fx-background-radius: 8px;
		    -fx-border-color: %s;
		    -fx-border-width: 1.5px;
		""", buttonBackground, buttonTextColor, buttonBorder);
	}
	
	private static Label styledLabel(String text, String color, int fontSize) {
		Label label = new Label(text);
		label.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size:" + fontSize + "px;");
		return label;
	}
	
	public static void showAlert(Alert.AlertType type, String message) {
		Alert alert = new Alert(type, message, ButtonType.OK);
		alert.setHeaderText(null);
		Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
		Image icon = new Image(Objects.requireNonNull(FeedbackPopup.class.getResourceAsStream(ASSETS_FOLDER_PATH + "feedback.png")));
		alertStage.getIcons().add(icon);
		alert.showAndWait();
	}
}