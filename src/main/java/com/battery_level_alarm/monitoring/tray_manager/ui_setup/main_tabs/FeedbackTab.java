package com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs;
import com.battery_level_alarm.monitoring.server_side.email_verification.EmailChecker;
import com.battery_level_alarm.monitoring.server_side.feedback.FeedbackPanel;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import com.battery_level_alarm.monitoring.server_side.feedback.FeedbackSender;
import com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.showNodeInStage;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.server_side.email_verification.EmailVerificationFlowPanels.*;
import static com.battery_level_alarm.monitoring.server_side.feedback.FeedbackPanel.showAlert;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ICONS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_EMAIL;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.DashboardTab.handleScroll;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_tabs.UITabs.createTab;
import static com.battery_level_alarm.monitoring.tray_manager.ui_setup.main_ui.BattorionTrayUI.insets;
import static com.battery_level_alarm.monitoring.website.Website.createFXWebsiteSendImagePageCaller;

public class FeedbackTab {
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static Stage stage;
	
	private static VBox emailBox;
	private static TextField emailField;
	private static Label verifiedLabel;
	private static Label selectedFileLabel;
	
	private static volatile VerificationStatus emailStatus = VerificationStatus.PENDING;
	private static boolean isFileSelected = false;
	
	public static Tab createFeedbackTab() {
		emailStatus = VerificationStatus.PENDING;
		Label titleLabel = new Label("Your Feedback Matters");
		titleLabel.setStyle("-fx-font-size: 22px;");
		Pane hyperlink = createFXWebsiteSendImagePageCaller(Pos.CENTER_LEFT);
		VBox headerBox = new VBox(titleLabel, hyperlink);
		VBox content = new VBox(20, headerBox, createFeedbackFormBox());
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
		final File[] selectedFile = {null};
		
		Label nameLabel = new Label("Name");
		TextField nameField = new TextField();
		nameField.setId("feedback-name");
		nameField.setPromptText("Enter your name");
		
		Label emailLabel = new Label("Email");
		verifiedTextField();
		
		String savedEmail = prefs.get(USER_EMAIL, "");
		if (savedEmail != null && !savedEmail.isEmpty()) {
			emailField.setText(savedEmail);
			emailStatus = VerificationStatus.PENDING;
			Thread.ofVirtual().start(() -> {
				boolean verified = false;
				try {
					verified = EmailChecker.checkEmail(savedEmail);
				} catch (Exception e) {
					printErrorMessage(e);
				}
				emailStatus = verified ? VerificationStatus.VERIFIED : VerificationStatus.UNVERIFIED;
			});
		}
		
		emailField.textProperty().addListener((_, _, newValue) ->
				localEmailVerified(newValue)
		);
		
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
		
		HBox centeredButtonBox = getSubmitButtonBox(nameField, emailField, subjectField, feedbackArea, selectedFile);
		HBox attachImageButtonBox = createAttachImageBox(selectedFile);
		HBox buttonsBox = new HBox(attachImageButtonBox, centeredButtonBox);
		buttonsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
		
		VBox formBox = new VBox(10,
				nameLabel, nameField,
				emailLabel, emailBox,
				subjectLabel, subjectField,
				feedbackLabel, feedbackArea,
				buttonsBox, selectedFileLabel
		);
		formBox.setPadding(new Insets(10));
		formBox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 8;");
		formBox.setMaxWidth(MAX_WIDTH + 20);
		formBox.setPrefWidth(MAX_WIDTH + 20);
		startStatusRefresher();
		return formBox;
	}
	
	private static void localEmailVerified(String email) {
		Thread.ofVirtual().start(() -> {
			if (email == null || email.trim().isEmpty()) {
				emailStatus = VerificationStatus.UNVERIFIED;
				return;
			}
			
			emailStatus = VerificationStatus.PENDING;
			boolean verified = false;
			try {
				verified = EmailChecker.checkEmail(email.trim());
			} catch (Exception e) {
				printErrorMessage(e);
			}
			emailStatus = verified ? VerificationStatus.VERIFIED : VerificationStatus.UNVERIFIED;
		});
	}
	
	private static void startStatusRefresher() {
		executor.scheduleAtFixedRate(() -> Platform.runLater(() -> {
			if (verifiedLabel == null) return;
			ImageView verifiedIcon;
			switch (emailStatus) {
				case VERIFIED -> {
					Image icon = new Image(Objects.requireNonNull(
							FeedbackPanel.class.getResourceAsStream(ICONS_FOLDER_PATH + "verified.png")
					));
					verifiedIcon = new ImageView(icon);
					verifiedIcon.setFitWidth(20);
					verifiedIcon.setFitHeight(20);
					verifiedLabel.setText("Verified!");
					verifiedLabel.setGraphic(verifiedIcon);
					verifiedLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 13;");
				}
				case UNVERIFIED -> {
					Image icon = new Image(Objects.requireNonNull(
							FeedbackPanel.class.getResourceAsStream(ICONS_FOLDER_PATH + "unverified.png")
					));
					verifiedIcon = new ImageView(icon);
					verifiedIcon.setFitWidth(20);
					verifiedIcon.setFitHeight(20);
					verifiedLabel.setText("Unverified!");
					verifiedLabel.setGraphic(verifiedIcon);
					verifiedLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 13;");
				}
				default -> {
					verifiedLabel.setText("Verifying, please wait...");
					verifiedLabel.setGraphic(null);
					verifiedLabel.setStyle("-fx-text-fill: " + (isDarkMode ? "white" : "black") + "; -fx-font-size: 13;");
				}
			}
		}), 0, 3, TimeUnit.SECONDS);
	}
	
	private static void verifiedTextField() {
		emailField = new TextField();
		emailField.setPrefWidth(300);
		emailField.setId("feedback-email");
		
		verifiedLabel = new Label();
		verifiedLabel.setContentDisplay(ContentDisplay.RIGHT);
		verifiedLabel.setGraphicTextGap(6);
		
		HBox box = new HBox(verifiedLabel);
		box.setAlignment(Pos.CENTER_RIGHT);
		
		emailBox = new VBox(2);
		emailBox.setAlignment(Pos.CENTER_LEFT);
		emailBox.getChildren().addAll(emailField, box);
	}
	
	private static @NotNull HBox getSubmitButtonBox(
			TextField nameField, TextField emailField, TextField subjectField,
			TextArea feedbackArea, File[] selectedFile
	) {
		Button sendButton = new Button("Send");
		sendButton.setId("feedback-send-button");
		sendButton.setMaxWidth(100);
		sendButton.setPrefWidth(100);
		sendButton.setOnAction(_ -> {
			if (emailStatus != VerificationStatus.VERIFIED) {
				prepareBasicData(emailField.getText().trim(), () -> {
					stage.close();
					localEmailVerified(emailAddress);
				});
				stage = showNodeInStage(createVerificationFlow(false), true, BattorionTrayUI.ICONS_FOLDER_PATH + "verification.png");
			} else {
				String name = nameField.getText().trim();
				String email = emailField.getText().trim();
				String subject = subjectField.getText().trim();
				String feedback = feedbackArea.getText().trim();
				
				if (name.isBlank() || email.isBlank() || subject.isBlank() || feedback.isBlank()) {
					showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
				} else {
					String userId = BattorionCoreConstants.UserIdentifier.getOrCreateUserId();
					nameField.setText("");
					subjectField.setText("");
					feedbackArea.setText("");
					selectedFileLabel.setText("");
					Thread.ofVirtual().start(() -> {
						if (isInternetAvailable()) {
							Thread.ofVirtual().start(() -> {
								if(isFileSelected) {
									FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback, selectedFile[0]);
									isFileSelected = false;
								} else FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback, null);
							});
						} else Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "No internet connection. Please try again later."));
					});
				}
			}
		});
		
		HBox centeredButtonBox = new HBox(sendButton);
		centeredButtonBox.setPadding(new Insets(5, 0, 0, 5));
		centeredButtonBox.setAlignment(javafx.geometry.Pos.CENTER);
		return centeredButtonBox;
	}
	
	private static @NotNull HBox createAttachImageBox(File[] selectedFile) {
		selectedFileLabel = new Label();
		selectedFileLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
		selectedFileLabel.setWrapText(true);
		selectedFileLabel.setMaxWidth(250);
		
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
				selectedFile[0] = file;
				isFileSelected = true;
				selectedFileLabel.setText("Selected: " + file.getName());
			}
		});
		
		HBox attachBox = new HBox(attachButton);
		attachBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
		attachBox.setPadding(new Insets(5, 5, 0, 0));
		return attachBox;
	}
}