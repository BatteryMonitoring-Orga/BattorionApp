package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs;
import com.battery_level_alarm.monitoring.feedback_system.UserDataUploader;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import com.battery_level_alarm.monitoring.feedback_system.FeedbackSender;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.updateUserData;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.feedback_system.FeedbackPopup.showAlert;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.DashboardTab.handleScroll;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTab;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.insets;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class FeedbackTab {
	public static Tab createFeedbackTab() {
		Label titleLabel = new Label("Your Feedback Matters");
		titleLabel.setStyle("-fx-font-size: 20px;");
		VBox content = new VBox(20, titleLabel, createFeedbackFormBox());
		content.setPadding(insets);
		
		Pane viewport = new Pane(content);
		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(viewport.widthProperty());
		clip.heightProperty().bind(viewport.heightProperty());
		viewport.setClip(clip);
		viewport.setOnScroll(event -> handleScroll(event, content, viewport));
		return createTab("Feedback", viewport);
	}
	
	private static VBox createFeedbackFormBox() {
		final double MAX_WIDTH = 270;
		Label nameLabel = new Label("Name");
		TextField nameField = new TextField();
		nameField.setId("feedback-name");
		nameField.setPromptText("Enter your name");
		
		CheckBox rememberEmailCheckBox = new CheckBox("Remember my email for later use");
		rememberEmailCheckBox.setId("feedback-remember-email");
		
		Label emailLabel = new Label("Email");
		TextField emailField = new TextField();
		emailField.setPromptText("Enter your email address");
		emailField.setId("feedback-email");
		
		String savedEmail = BattorionCoreConstants.AppInfo.UserEmail;
		if (savedEmail != null && !savedEmail.isEmpty()) {
			emailField.setText(savedEmail);
			rememberEmailCheckBox.setVisible(false);
		}
		
		emailField.textProperty().addListener((_, _, newValue) -> {
			if (savedEmail != null && !savedEmail.isEmpty()) {
				if (newValue.equalsIgnoreCase(savedEmail)) {
					rememberEmailCheckBox.setVisible(false);
					rememberEmailCheckBox.setSelected(false);
				} else {
					rememberEmailCheckBox.setVisible(true);
					rememberEmailCheckBox.setSelected(false);
				}
			}
		});
		
		Label subjectLabel = new Label("Subject");
		TextField subjectField = new TextField();
		subjectField.setPromptText("Enter a short subject");
		subjectField.setId("feedback-subject");
		
		Label feedbackLabel = new Label("Feedback");
		TextArea feedbackArea = new TextArea();
		feedbackArea.setPromptText("Write your feedback here");
		feedbackArea.setWrapText(true);
		feedbackArea.setPrefRowCount(10);
		feedbackArea.setId("feedback-text");
		
		nameLabel.setStyle("-fx-font-size: 16px;");
		nameField.setStyle("-fx-font-size: 15px;");
		emailLabel.setStyle("-fx-font-size: 16px;");
		emailField.setStyle("-fx-font-size: 15px;");
		rememberEmailCheckBox.setStyle("-fx-font-size: 14px;");
		subjectLabel.setStyle("-fx-font-size: 16px;");
		subjectField.setStyle("-fx-font-size: 15px;");
		feedbackLabel.setStyle("-fx-font-size: 16px;");
		feedbackArea.setStyle("-fx-font-size: 15px;");
		
		nameField.setMaxWidth(MAX_WIDTH);
		nameField.setPrefWidth(MAX_WIDTH);
		emailField.setMaxWidth(MAX_WIDTH);
		emailField.setPrefWidth(MAX_WIDTH);
		subjectField.setMaxWidth(MAX_WIDTH);
		subjectField.setPrefWidth(MAX_WIDTH);
		feedbackArea.setMaxWidth(MAX_WIDTH);
		feedbackArea.setPrefWidth(MAX_WIDTH);
		rememberEmailCheckBox.setMaxWidth(MAX_WIDTH);
		rememberEmailCheckBox.setPrefWidth(MAX_WIDTH);
		HBox centeredButtonBox = getSubmitButtonBox(
				nameField, emailField, subjectField, feedbackArea, rememberEmailCheckBox
		);
		
		VBox formBox = new VBox(10,
				nameLabel, nameField,
				emailLabel, emailField,
				rememberEmailCheckBox,
				subjectLabel, subjectField,
				feedbackLabel, feedbackArea,
				centeredButtonBox
		);
		formBox.setPadding(new Insets(10));
		formBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		formBox.setMaxWidth(MAX_WIDTH + 20);
		formBox.setPrefWidth(MAX_WIDTH + 20);
		return formBox;
	}
	
	private static @NotNull HBox getSubmitButtonBox(
			TextField nameField,
			TextField emailField,
			TextField subjectField,
			TextArea feedbackArea,
			CheckBox rememberEmailCheckBox
	) {
		Button sendButton = new Button("Send");
		sendButton.setId("feedback-send-button");
		sendButton.setMaxWidth(100);
		sendButton.setPrefWidth(100);
		
		sendButton.setOnAction(_ -> {
			String name = nameField.getText().trim();
			String email = emailField.getText().trim();
			String subject = subjectField.getText().trim();
			String feedback = feedbackArea.getText().trim();
			
			if (name.isBlank() || email.isBlank() || subject.isBlank() || feedback.isBlank()) {
				showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
			} else {
				String userId = BattorionCoreConstants.UserIdentifier.getOrCreateUserId();
				if (rememberEmailCheckBox.isVisible() && rememberEmailCheckBox.isSelected()) {
					try {
						Map<String, Object> updates = new HashMap<>();
						updates.put(UserDataUploader.Keys.EMAIL, email);
						updateUserData(prefs.get(BattorionCoreConstants.PrefKeysIdentifiers.USER_IDENTIFIER, null), updates);
					} catch (Exception e) {
						printErrorMessage(e);
					}
				}
				
				nameField.setText("");
				emailField.setText("");
				subjectField.setText("");
				feedbackArea.setText("");
				rememberEmailCheckBox.setSelected(false);
				Thread.ofVirtual().start(() -> {
					if (isInternetAvailable()) {
						Thread.ofVirtual().start(() ->
								FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback));
					} else {
						Platform.runLater(() ->
								showAlert(Alert.AlertType.WARNING, "No internet connection. Please try again later."));
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