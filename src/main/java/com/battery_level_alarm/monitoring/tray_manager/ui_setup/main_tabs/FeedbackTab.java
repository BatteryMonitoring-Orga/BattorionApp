package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import com.battery_level_alarm.monitoring.feedback_system.FeedbackSender;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jetbrains.annotations.NotNull;

import static com.battery_level_alarm.monitoring.file_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.feedback_system.FeedbackPopup.showAlert;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.insets;

public class FeedbackTab {
	public static Tab createFeedbackTab() {
		Label titleLabel = new Label("Your Feedback Matters");
		titleLabel.setStyle("-fx-font-size: 20px;");
		
		VBox content = new VBox(20, titleLabel, createFeedbackFormBox());
		content.setPadding(insets);
		return UITabs.createTab("Feedback", content);
	}
	
	private static VBox createFeedbackFormBox() {
		final double MAX_WIDTH = 270;
		Label nameLabel = new Label("Name");
		TextField nameField = new TextField();
		nameField.setId("feedback-name");
		nameField.setPromptText("Enter your name");
		nameField.setMaxWidth(MAX_WIDTH);
		nameField.setPrefWidth(MAX_WIDTH);
		nameField.setMinWidth(MAX_WIDTH);
		
		Label emailLabel = new Label("Email");
		TextField emailField = new TextField();
		emailField.setPromptText("Enter your email address");
		emailField.setId("feedback-email");
		emailField.setMaxWidth(MAX_WIDTH);
		emailField.setPrefWidth(MAX_WIDTH);
		emailField.setMinWidth(MAX_WIDTH);
		
		Label feedbackLabel = new Label("Feedback");
		TextArea feedbackArea = new TextArea();
		feedbackArea.setPromptText("Write your feedback here");
		feedbackArea.setWrapText(true);
		feedbackArea.setPrefRowCount(10);
		feedbackArea.setId("feedback-text");
		feedbackArea.setMaxWidth(MAX_WIDTH);
		feedbackArea.setPrefWidth(MAX_WIDTH);
		feedbackArea.setMinWidth(MAX_WIDTH);
		
		nameLabel.setStyle("-fx-font-size: 16px;");
		nameField.setStyle("-fx-font-size: 15px;");
		emailLabel.setStyle("-fx-font-size: 16px;");
		emailField.setStyle("-fx-font-size: 15px;");
		feedbackLabel.setStyle("-fx-font-size: 16px;");
		feedbackArea.setStyle("-fx-font-size: 15px;");
		
		HBox centeredButtonBox = getSubmitButtonBox(nameField, emailField, feedbackArea);
		VBox formBox = new VBox(10,
				nameLabel, nameField,
				emailLabel, emailField,
				feedbackLabel, feedbackArea,
				centeredButtonBox
		);
		formBox.setPadding(new Insets(10));
		formBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		formBox.setMaxWidth(MAX_WIDTH + 20);
		formBox.setPrefWidth(MAX_WIDTH + 20);
		formBox.setMinWidth(MAX_WIDTH + 20);
		return formBox;
	}
	
	private static @NotNull HBox getSubmitButtonBox(TextField nameField, TextField emailField, TextArea feedbackArea) {
		Button sendButton = new Button("Send");
		sendButton.setId("feedback-send-button");
		sendButton.setMaxWidth(100);
		sendButton.setPrefWidth(100);
		sendButton.setMinWidth(100);
		sendButton.setOnAction(_ -> {
			String name = nameField.getText().trim();
			String email = emailField.getText().trim();
			String feedback = feedbackArea.getText().trim();
			
			if (name.isBlank() || email.isBlank() || feedback.isBlank()) {
				showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
			} else {
				String userId = BattorionCoreConstants.UserIdentifier.getOrCreateUserId();
				nameField.setText("");
				emailField.setText("");
				feedbackArea.setText("");
				Thread.ofVirtual().start(() -> {
					if (isInternetAvailable()) {
						Thread.ofVirtual().start(() -> FeedbackSender.sendFeedback(userId, name, email, feedback));
					} else {
						Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "No internet connection. Please try again later."));
					}
				});
			}
		});
		
		HBox centeredButtonBox = new HBox(sendButton);
		centeredButtonBox.setPadding(new Insets(5, 0, 0, 0));
		centeredButtonBox.setAlignment(javafx.geometry.Pos.CENTER);
		return centeredButtonBox;
	}
}