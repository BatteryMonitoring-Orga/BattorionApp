package com.battery_level_alarm.monitoring.feedback_system;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.updateUserData;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_EMAIL;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.website.Website.createFXWebsiteSendImagePageCaller;

public class FeedbackPopup {
	private static Label selectedFileLabel;
	private static boolean isFileSelected = false;
	private static final File[] selectedFile = {null};
	
	public static void feedback(boolean isDarkTheme) {
		showFeedbackPopup(isDarkTheme);
	}
	
	private static void showFeedbackPopup(boolean isDarkTheme) {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.initStyle(StageStyle.DECORATED);
		popupStage.setTitle("Your Feedback Matters");
		
		selectedFileLabel = new Label();
		selectedFileLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
		selectedFileLabel.setWrapText(true);
		selectedFileLabel.setMaxWidth(450);
		
		String backgroundColor = isDarkTheme ? "rgba(255, 255, 255, 0.9)" : "rgba(0, 0, 0, 0.85)";
		String textColor = isDarkTheme ? "black" : "white";
		
		TextField nameField = new TextField();
		TextField emailField = new TextField();
		TextField subjectField = new TextField();
		
		nameField.setPromptText("Your Name");
		emailField.setPromptText("Your Email");
		subjectField.setPromptText("Subject");
		
		CheckBox rememberEmailCheckBox = new CheckBox("Remember my email for later use");
		rememberEmailCheckBox.setStyle("-fx-text-fill: " + textColor + "; -fx-font-weight: bold; -fx-font-size: 14px;");
		
		String savedEmail = prefs.get(USER_EMAIL, "");
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
		
		HBox attachImageBox = createAttachImageBox(isDarkTheme);
		Button closeBtn = getCloseBtn(isDarkTheme, popupStage);
		Button submitBtn = createSubmitButton(
				popupStage, rememberEmailCheckBox, nameField, emailField,
				subjectField, feedbackArea, isDarkTheme
		);
		
		Label titleLabel = styledLabel("Your Feedback Matters", textColor, 22);
		Pane hyperlink = createFXWebsiteSendImagePageCaller(Pos.CENTER_LEFT);
		VBox headerBox = new VBox(titleLabel, hyperlink);
		
		VBox.setVgrow(scrollPane, Priority.ALWAYS);
		VBox form = new VBox(10,
				headerBox,
				styledLabel("Name:", textColor, 15), nameField,
				styledLabel("Email:", textColor, 15), emailField,
				rememberEmailCheckBox,
				styledLabel("Subject:", textColor, 15), subjectField,
				styledLabel("Feedback:", textColor, 15), scrollPane,
				selectedFileLabel,
				new HBox(10, attachImageBox, submitBtn, closeBtn)
		);
		((HBox) form.getChildren().getLast()).setAlignment(Pos.CENTER_RIGHT);
		form.setPadding(new Insets(20));
		form.setAlignment(Pos.CENTER_LEFT);
		form.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 10;");
		
		Scene popupScene = new Scene(form, 420, 600);
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
	
	private static @NotNull Button getCloseBtn(boolean isDarkTheme, Stage popupStage) {
		Button closeBtn = new Button("Close");
		closeBtn.setCancelButton(true);
		closeBtn.setOnAction(_ -> popupStage.close());
		
		String buttonStyle = getButtonStyle(isDarkTheme);
		String buttonHoverStyle = String.format("""
            -fx-background-color: %s;
        """, isDarkTheme ? "#00000033" : "#ffffff44");
		closeBtn.setStyle(buttonStyle);
		closeBtn.setOnMouseEntered(_ -> closeBtn.setStyle(buttonStyle + buttonHoverStyle));
		closeBtn.setOnMouseExited(_ -> closeBtn.setStyle(buttonStyle));
		return closeBtn;
	}
	
	private static Button createSubmitButton(
			Stage popupStage, CheckBox rememberEmailCheckBox,
			TextField nameField, TextField emailField,
			TextField subjectField, TextArea feedbackArea,
			boolean isDarkTheme
	) {
		Button submitBtn = new Button("Submit");
		submitBtn.setDefaultButton(true);
		submitBtn.setOnAction(_ -> {
			String name = nameField.getText().trim();
			String email = emailField.getText().trim();
			String subject = subjectField.getText().trim();
			String feedback = feedbackArea.getText().trim();
			
			if (name.isBlank() || email.isBlank() || subject.isBlank() || feedback.isBlank()) {
				showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
			} else {
				if (rememberEmailCheckBox.isVisible() && rememberEmailCheckBox.isSelected()) {
					try {
						Map<String, Object> updates = new HashMap<>();
						updates.put(UserDataUploader.Keys.EMAIL, email);
						updateUserData(prefs.get(BattorionCoreConstants.PrefKeysIdentifiers.USER_IDENTIFIER, null), updates);
					} catch (Exception e) {
						printErrorMessage(e);
					}
				}
				
				popupStage.close();
				String userId = BattorionCoreConstants.UserIdentifier.getOrCreateUserId();
				Thread.ofVirtual().start(() -> {
					if (isInternetAvailable()) {
						Thread.ofVirtual().start(() -> {
							if(isFileSelected) {
								FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback, selectedFile[0]);
								isFileSelected = false;
							} else {
								FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback, null);
							}
						});
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
		submitBtn.setOnMouseEntered(_ -> submitBtn.setStyle(buttonStyle + buttonHoverStyle));
		submitBtn.setOnMouseExited(_ -> submitBtn.setStyle(buttonStyle));
		return submitBtn;
	}
	
	private static @NotNull HBox createAttachImageBox(boolean isDarkTheme) {
		Button attachButton = new Button("Attach Image");
		attachButton.setTooltip(new Tooltip("Attach a screenshot or image (optional)"));
		attachButton.setId("feedback-attach-button");
		attachButton.setMaxWidth(150);
		attachButton.setPrefWidth(150);
		attachButton.setOnAction(_ -> {
			isFileSelected = false;
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select Image");
			fileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
			);
			File file = fileChooser.showOpenDialog(attachButton.getScene().getWindow());
			if (file != null) {
				FeedbackPopup.selectedFile[0] = file;
				isFileSelected = true;
				selectedFileLabel.setText("Selected: " + file.getName());
			}
		});
		
		String buttonStyle = getButtonStyle(isDarkTheme);
		String buttonHoverStyle = String.format("""
            -fx-background-color: %s;
        """, isDarkTheme ? "#00000033" : "#ffffff44");
		
		attachButton.setStyle(buttonStyle);
		attachButton.setOnMouseEntered(_ -> attachButton.setStyle(buttonStyle + buttonHoverStyle));
		attachButton.setOnMouseExited(_ -> attachButton.setStyle(buttonStyle));
		
		HBox attachBox = new HBox(attachButton);
		attachBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
		return attachBox;
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