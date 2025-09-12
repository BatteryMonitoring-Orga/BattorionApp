package com.battery_level_alarm.monitoring.server_side.feedback;
import com.battery_level_alarm.monitoring.server_side.email_verification.EmailChecker;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.registration_manager.EssentialToolsDownloader.isInternetAvailable;
import static com.battery_level_alarm.monitoring.server_side.email_verification.EmailVerificationFlowPanels.*;
import static com.battery_level_alarm.monitoring.system_core.Battorion.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ICONS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_EMAIL;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_EMAIL_VERIFIED;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.dashboardButton;
import static com.battery_level_alarm.monitoring.website.Website.createFXWebsiteSendImagePageCaller;

public class FeedbackPanel {
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static Scene scene;
	private static JFXPanel jfxPanel;
	public static StackPane feedbackStackPane;
	private static VBox emailBox;
	private static TextField emailField;
	private static Label verifiedLabel;
	private static Label selectedFileLabel;
	
	private static volatile VerificationStatus emailStatus = VerificationStatus.PENDING;
	private static boolean isFileSelected = false;
	private static final File[] selectedFile = {null};
	
	public static void createFeedbackPanel() {
		FeedbackMainPanel = new JPanel();
		jfxPanel = new JFXPanel();
		jfxPanel.setBackground(isDarkMode ? java.awt.Color.BLACK : java.awt.Color.WHITE);
		
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			ScrollPane fxContent = feedback(isDarkMode);
			fxContent.setBackground(Background.EMPTY);
			
			scene = new Scene(fxContent);
			scene.setFill(javafx.scene.paint.Color.web(isDarkMode ? "black" : "white"));
			SwingUtilities.invokeLater(() -> {
				jfxPanel.setScene(scene);
				jfxPanel.repaint();
				jfxPanel.requestFocusInWindow();
				
				FeedbackMainPanel.add(jfxPanel);
				FeedbackMainPanel.repaint();
				FeedbackMainPanel.revalidate();
			});
		});
	}
	
	private static ScrollPane feedback(boolean isDarkTheme) {
		String savedEmail = prefs.get(USER_EMAIL, "");
		boolean storedMatches = prefs.get(USER_EMAIL_VERIFIED, "").equalsIgnoreCase(savedEmail);
		if (!storedMatches) {
			boolean verified = false;
			if (savedEmail != null && !savedEmail.isEmpty()) {
				try {
					verified = EmailChecker.checkEmail(savedEmail);
				} catch (Exception e) {
					printErrorMessage(e);
				}
			} if (!verified) {
				prepareBasicData(savedEmail, () -> mainEmailVerificationFlow.setContent(createFeedbackPane(isDarkTheme)));
				return createVerificationFlow(isDarkTheme);
			}
		}
		
		feedbackStackPane = createFeedbackPane(isDarkTheme);
		ScrollPane root = new ScrollPane(feedbackStackPane);
		root.setPrefSize(580, 400);
		root.setFitToWidth(true);
		styleScrollPane(root, isDarkTheme);
		return root;
	}
	
	public static StackPane createFeedbackPane(boolean isDarkTheme) {
		emailStatus = VerificationStatus.PENDING;
		String backgroundColor = isDarkTheme ? "#000000" : "#ffffff";
		String textColor = isDarkTheme ? "#ffffff" : "#000000";
		String fieldBackground = isDarkTheme ? "#111111" : "#fdfdfd";
		String fieldBorder = isDarkTheme ? "#444444" : "#cccccc";
		
		selectedFileLabel = new Label();
		selectedFileLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:" + textColor + "; -fx-font-family: Serif; -fx-background-color:" + backgroundColor + ";");
		selectedFileLabel.setWrapText(true);
		selectedFileLabel.setMaxWidth(400);
		
		TextField nameField = styledTextField(fieldBackground, fieldBorder, textColor);
		TextField subjectField = styledTextField(fieldBackground, fieldBorder, textColor);
		verifiedTextField(fieldBackground, fieldBorder, textColor);
		
		nameField.setPromptText("Your Name");
		emailField.setPromptText("Your Email");
		subjectField.setPromptText("Subject");
		
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
				Thread.ofVirtual().start(() -> {
					if (newValue == null || newValue.trim().isEmpty()) {
						emailStatus = VerificationStatus.UNVERIFIED;
						return;
					}
					
					emailStatus = VerificationStatus.PENDING;
					boolean verified = false;
					try {
						verified = EmailChecker.checkEmail(newValue.trim());
					} catch (Exception e) {
						printErrorMessage(e);
					}
					emailStatus = verified ? VerificationStatus.VERIFIED : VerificationStatus.UNVERIFIED;
				})
		);
		
		TextArea feedbackArea = new TextArea();
		feedbackArea.setPromptText("Write your feedback here...");
		feedbackArea.setWrapText(true);
		feedbackArea.setPrefRowCount(5);
		feedbackArea.setPrefWidth(400);
		feedbackArea.setStyle("-fx-font-family:Serif; -fx-background-color:" + fieldBackground + "; -fx-text-fill:" + textColor + "; -fx-border-color:" + fieldBorder + "; -fx-prompt-text-fill:" + textColor + ";");
		feedbackArea.setBackground(Background.fill(Color.web(fieldBackground)));
		
		HBox attachImageBox = createAttachImageBox(isDarkTheme);
		Button submitBtn = createSubmitButton(nameField, emailField, subjectField, feedbackArea, isDarkTheme);
		
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
				styledLabel("Email:", textColor, 15, backgroundColor), emailBox,
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
		startStatusRefresher();
		return centeredWrapper;
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
	
	private static void verifiedTextField(String background, String border, String textColor) {
		emailField = new TextField();
		emailField.setPrefWidth(300);
		emailField.setStyle(
				"-fx-font-family:Serif; -fx-background-color:" + background +
				"; -fx-text-fill:" + textColor +
				"; -fx-border-color:" + border +
				"; -fx-prompt-text-fill:" + textColor + ";"
		);
		
		verifiedLabel = new Label();
		verifiedLabel.setContentDisplay(ContentDisplay.RIGHT);
		verifiedLabel.setGraphicTextGap(6);
		
		HBox box = new HBox(verifiedLabel);
		box.setAlignment(Pos.CENTER_RIGHT);
		
		Label spaceLabel = new Label("\u2003");
		emailBox = new VBox(2);
		emailBox.setAlignment(Pos.CENTER_LEFT);
		emailBox.getChildren().addAll(emailField, box, spaceLabel);
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
	
	private static Button createSubmitButton(TextField nameField, TextField emailField, TextField subjectField, TextArea feedbackArea, boolean isDarkTheme) {
		Button submitBtn = new Button("Submit");
		submitBtn.setDefaultButton(true);
		submitBtn.setOnAction(_ -> {
			if (emailStatus != VerificationStatus.VERIFIED) {
				Platform.runLater(() -> {
					prepareBasicData(emailField.getText().trim(), () -> mainEmailVerificationFlow.setContent(feedbackStackPane));
					scene.setRoot(createVerificationFlow(isDarkTheme));
					SwingUtilities.invokeLater(() -> {
						jfxPanel.repaint();
						jfxPanel.requestFocusInWindow();
						FeedbackMainPanel.repaint();
						FeedbackMainPanel.revalidate();
					});
				});
			} else {
				String name = nameField.getText().trim();
				String email = emailField.getText().trim();
				String subject = subjectField.getText().trim();
				String feedback = feedbackArea.getText().trim();
				
				if (name.isBlank() || email.isBlank() || subject.isBlank() || feedback.isBlank()) {
					showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
				} else {
					String userId = BattorionCoreConstants.UserIdentifier.getOrCreateUserId();
					Thread.ofVirtual().start(() -> {
						if (isInternetAvailable()) {
							Thread.ofVirtual().start(() -> {
								if (isFileSelected) {
									FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback, selectedFile[0]);
									isFileSelected = false;
								} else FeedbackSender.sendFeedback(userId, name, email, subject + "\n\n" + feedback, null);
								
								Platform.runLater(() -> {
									nameField.setText("");
									subjectField.setText("");
									feedbackArea.setText("");
									selectedFileLabel.setText("");
								});
								dashboardButton.doClick();
							});
						} else {
							Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "No internet connection. Please try again later."));
						}
					});
				}
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