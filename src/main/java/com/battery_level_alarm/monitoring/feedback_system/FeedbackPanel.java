package com.battery_level_alarm.monitoring.feedback_system;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.website.Website.createFXWebsiteSendImagePageCaller;

public class FeedbackPanel {
	private static Label selectedFileLabel;
	private static boolean isFileSelected = false;
	private static final File[] selectedFile = {null};
	
	public static ScrollPane feedback(boolean isDarkTheme) {
		return createFeedbackPane(isDarkTheme);
	}
	
	private static ScrollPane createFeedbackPane(boolean isDarkTheme) {
		String backgroundColor = isDarkTheme ? "#000000" : "#ffffff";
		String textColor = isDarkTheme ? "#ffffff" : "#000000";
		String fieldBackground = isDarkTheme ? "#111111" : "#fdfdfd";
		String fieldBorder = isDarkTheme ? "#444444" : "#cccccc";
		
		selectedFileLabel = new Label();
		selectedFileLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:" + textColor + "; -fx-font-family: Serif; -fx-background-color:" + backgroundColor + ";");
		selectedFileLabel.setWrapText(true);
		selectedFileLabel.setMaxWidth(400);
		
		TextField nameField = styledTextField(fieldBackground, fieldBorder, textColor);
		TextField emailField = styledTextField(fieldBackground, fieldBorder, textColor);
		TextField subjectField = styledTextField(fieldBackground, fieldBorder, textColor);
		
		nameField.setPromptText("Your Name");
		emailField.setPromptText("Your Email");
		subjectField.setPromptText("Subject");
		
		CheckBox rememberEmailCheckBox = new CheckBox("Remember my email for later use");
		rememberEmailCheckBox.setStyle("-fx-text-fill:" + textColor + "; -fx-font-weight:bold; -fx-font-size:14px; -fx-font-family:Serif; -fx-background-color:" + backgroundColor + ";");
		
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
		feedbackArea.setPrefRowCount(5);
		feedbackArea.setPrefWidth(400);
		feedbackArea.setStyle("-fx-font-family:Serif; -fx-background-color:" + fieldBackground + "; -fx-text-fill:" + textColor + "; -fx-border-color:" + fieldBorder + "; -fx-prompt-text-fill:" + textColor + ";");
		feedbackArea.setBackground(Background.fill(Color.web(fieldBackground)));
		
		HBox attachImageBox = createAttachImageBox(isDarkTheme);
		Button submitBtn = createSubmitButton(rememberEmailCheckBox, nameField, emailField, subjectField, feedbackArea, isDarkTheme);
		
		Label titleLabel = styledLabel("Your Feedback Matters", textColor, 20, backgroundColor);
		Pane hyperlink = createFXWebsiteSendImagePageCaller(Pos.CENTER_LEFT);
		hyperlink.setStyle("-fx-background-color:" + backgroundColor + ";");
		VBox headerBox = new VBox(titleLabel, hyperlink);
		headerBox.setSpacing(5);
		headerBox.setStyle("-fx-background-color:" + backgroundColor + ";");
		
		HBox buttonsBox = new HBox(10, attachImageBox, submitBtn);
		buttonsBox.setAlignment(Pos.CENTER_RIGHT);
		buttonsBox.setStyle("-fx-background-color:" + backgroundColor + ";");
		
		VBox form = new VBox(8,
				headerBox,
				styledLabel("Name:", textColor, 15, backgroundColor), nameField,
				styledLabel("Email:", textColor, 15, backgroundColor), emailField,
				rememberEmailCheckBox,
				styledLabel("Subject:", textColor, 15, backgroundColor), subjectField,
				styledLabel("Feedback:", textColor, 15, backgroundColor), feedbackArea,
				selectedFileLabel,
				buttonsBox
		);
		form.setPadding(new Insets(15));
		form.setAlignment(Pos.TOP_LEFT);
		form.setStyle("-fx-background-color:" + backgroundColor + "; -fx-font-size:14px; -fx-background-radius:10; -fx-font-family:Serif;");
		form.setMinWidth(450);
		form.setPrefWidth(450);
		form.setMaxWidth(450);
		
		StackPane centeredWrapper = new StackPane(form);
		centeredWrapper.setAlignment(Pos.CENTER);
		
		ScrollPane root = new ScrollPane(centeredWrapper);
		root.setPrefSize(580, 380);
		root.setFitToWidth(true);
		styleScrollPane(root, isDarkTheme);
		return root;
	}
	
	private static void styleScrollPane(ScrollPane scrollPane, boolean isDarkTheme) {
		String backgroundColor = isDarkTheme ? "#000000" : "#ffffff";
		String thumbColor = isDarkTheme ? "#555555" : "#cccccc";
		String trackColor = isDarkTheme ? "#222222" : "#eeeeee";
		
		scrollPane.setStyle(
				"-fx-background:" + backgroundColor + ";" +
				"-fx-control-inner-background:" + backgroundColor + ";" +
				"-fx-background-color:" + backgroundColor + ";"
		);
		
		Platform.runLater(() -> {
			scrollPane.lookupAll(".scroll-bar").forEach(node -> {
				if (node instanceof ScrollBar sb) {
					sb.setStyle("-fx-background-color:" + trackColor + ";");
				}
			});
			
			scrollPane.lookupAll(".thumb").forEach(node ->
					node.setStyle(
							"-fx-background-color:" + thumbColor + ";" +
							"-fx-background-radius: 6px;"
					)
			);
		});
	}
	
	private static @NotNull TextField styledTextField(String background, String border, String textColor) {
		TextField field = new TextField();
		field.setPrefWidth(300);
		field.setStyle("-fx-font-family:Serif; -fx-background-color:" + background + "; -fx-text-fill:" + textColor + "; -fx-border-color:" + border + "; -fx-prompt-text-fill:" + textColor + ";");
		return field;
	}
	
	private static @NotNull String getButtonStyle(boolean isDarkTheme) {
		String buttonBackground = isDarkTheme ? "#444444" : "#e0e0e0";
		String buttonTextColor = isDarkTheme ? "#ffffff" : "#000000";
		return String.format("""
            -fx-background-color:%s;
            -fx-text-fill:%s;
            -fx-font-size:14px;
            -fx-font-family:Serif;
            -fx-padding:6px 12px;
            -fx-border-radius:6px;
            -fx-background-radius:6px;
        """, buttonBackground, buttonTextColor);
	}
	
	private static Label styledLabel(String text, String color, int fontSize, String background) {
		Label label = new Label(text);
		label.setStyle("-fx-text-fill:" + color + "; -fx-font-weight:bold; -fx-font-size:" + fontSize + "px; -fx-font-family:Serif; -fx-background-color:" + background + ";");
		return label;
	}
	
	private static Button createSubmitButton(CheckBox rememberEmailCheckBox, TextField nameField, TextField emailField, TextField subjectField, TextArea feedbackArea, boolean isDarkTheme) {
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
				
				String userId = BattorionCoreConstants.UserIdentifier.getOrCreateUserId();
				Thread.ofVirtual().start(() -> {
					if (isInternetAvailable()) {
						Thread.ofVirtual().start(() -> {
							if (isFileSelected) {
								FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback, selectedFile[0]);
								isFileSelected = false;
							} else {
								FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback, null);
							}
							
							Platform.runLater(() -> {
								nameField.setText("");
								emailField.setText("");
								subjectField.setText("");
								feedbackArea.setText("");
								selectedFileLabel.setText("");
							});
						});
					} else {
						Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "No internet connection. Please try again later."));
					}
				});
			}
		});
		
		String buttonStyle = getButtonStyle(isDarkTheme);
		String buttonHoverStyle = isDarkTheme ? "-fx-background-color:#666666;" : "-fx-background-color:#c0c0c0;";
		submitBtn.setStyle(buttonStyle);
		submitBtn.setOnMouseEntered(_ -> submitBtn.setStyle(buttonHoverStyle + "-fx-text-fill:white; -fx-font-size:14px; -fx-font-family:Serif; -fx-padding:6px 12px; -fx-border-radius:6px; -fx-background-radius:6px;"));
		submitBtn.setOnMouseExited(_ -> submitBtn.setStyle(buttonStyle));
		return submitBtn;
	}
	
	private static @NotNull HBox createAttachImageBox(boolean isDarkTheme) {
		Button attachButton = new Button("Attach Image");
		attachButton.setTooltip(new Tooltip("Attach a screenshot or image (optional)"));
		attachButton.setId("feedback-attach-button");
		attachButton.setPrefWidth(120);
		attachButton.setStyle(getButtonStyle(isDarkTheme));
		attachButton.setOnAction(_ -> {
			isFileSelected = false;
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select Image");
			fileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
			);
			File file = fileChooser.showOpenDialog(attachButton.getScene().getWindow());
			if (file != null) {
				FeedbackPanel.selectedFile[0] = file;
				isFileSelected = true;
				selectedFileLabel.setText("Selected: " + file.getName());
			}
		});
		
		String buttonHoverStyle = isDarkTheme ? "-fx-background-color:#666666;" : "-fx-background-color:#c0c0c0;";
		attachButton.setOnMouseEntered(_ -> attachButton.setStyle(buttonHoverStyle + "-fx-text-fill:white; -fx-font-size:14px; -fx-font-family:Serif; -fx-padding:6px 12px; -fx-border-radius:6px; -fx-background-radius:6px;"));
		attachButton.setOnMouseExited(_ -> attachButton.setStyle(getButtonStyle(isDarkTheme)));
		
		HBox attachBox = new HBox(attachButton);
		attachBox.setAlignment(Pos.CENTER_LEFT);
		attachBox.setStyle("-fx-background-color:" + (isDarkTheme ? "#000000" : "#ffffff") + ";");
		return attachBox;
	}
	
	public static void showAlert(Alert.AlertType type, String message) {
		Alert alert = new Alert(type, message, ButtonType.OK);
		alert.setHeaderText(null);
		Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
		Image icon = new Image(Objects.requireNonNull(FeedbackPanel.class.getResourceAsStream(ASSETS_FOLDER_PATH + "feedback.png")));
		alertStage.getIcons().add(icon);
		alert.showAndWait();
	}
}