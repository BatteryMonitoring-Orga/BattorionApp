package com.battery_level_alarm.monitoring.server_side.email_verification;
import com.battery_level_alarm.monitoring.server_side.user_data.UserDataUploader;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.server_side.email_verification.EmailVerification.*;
import static com.battery_level_alarm.monitoring.server_side.user_data.UserDataUploader.updateUserData;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ICONS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_EMAIL;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_EMAIL_VERIFIED;

public class EmailVerificationFlowPanels {
	public static ScrollPane mainEmailVerificationFlow;
	private static StackPane emailScene;
	private static StackPane verifyScene;
	private static StackPane failScene;
	private static TextField emailField;
	
	public enum VerificationStatus { PENDING, VERIFIED, UNVERIFIED }
	private static final String errorColor = "#d93025";
	private static final String successColor = "#188038";
	private static String backgroundColor;
	private static String textColor;
	private static String fieldBackground;
	private static String fieldBorder;
	private static String secondaryText;
	
	private static Runnable runnable;
	private static String receivedEmailAddress;
	public static String emailAddress;
	
	public static void prepareBasicData(String emailAddress, Runnable runnable) {
		receivedEmailAddress = emailAddress;
		EmailVerificationFlowPanels.runnable = runnable;
	}
	
	public static ScrollPane createVerificationFlow(boolean isDarkTheme) {
		setThemeColors(isDarkTheme);
		mainEmailVerificationFlow = new ScrollPane();
		mainEmailVerificationFlow.setId("main-email-verification-flow");
		mainEmailVerificationFlow.setPrefSize(580, 400);
		mainEmailVerificationFlow.setFitToWidth(true);
		
		emailScene = new StackPane(getEmailLayout(isDarkTheme));
		verifyScene = new StackPane(getVerifyLayout(isDarkTheme));
		failScene = new StackPane(getFailLayout(isDarkTheme));
		
		emailScene.setId("email-scene");
		verifyScene.setId("verify-scene");
		failScene.setId("fail-scene");
		
		mainEmailVerificationFlow.setContent(emailScene);
		styleScroll(mainEmailVerificationFlow, isDarkTheme);
		return mainEmailVerificationFlow;
	}
	
	private static void setThemeColors(boolean isDarkTheme) {
		backgroundColor = isDarkTheme ? "#000000" : "#ffffff";
		textColor = isDarkTheme ? "#ffffff" : "#000000";
		fieldBackground = isDarkTheme ? "#111111" : "#fdfdfd";
		fieldBorder = isDarkTheme ? "#444444" : "#cccccc";
		secondaryText = isDarkTheme ? "#aaaaaa" : "#5f6368";
	}
	
	private static @NotNull VBox getEmailLayout(boolean isDarkTheme) {
		Label emailLabel = styledLabel("Verify your email address", textColor, 18, true);
		emailLabel.setId("email-label");
		Label instructions = styledLabel("Please enter your email address to receive a verification code.", secondaryText, 14, false);
		instructions.setId("instructions-label");
		
		VBox emailLayout = getEmailLayout(emailLabel, instructions, isDarkTheme);
		emailLayout.setId("email-layout");
		emailLayout.setAlignment(Pos.CENTER);
		emailLayout.setPadding(new Insets(30));
		emailLayout.setStyle("-fx-background-color:" + backgroundColor + ";");
		return emailLayout;
	}
	
	private static @NotNull VBox getEmailLayout(Label emailLabel, Label instructions, boolean isDarkTheme) {
		emailField = styledTextField(fieldBackground, fieldBorder, textColor);
		emailField.setId("email-field");
		emailField.setText(receivedEmailAddress);
		emailField.setMaxSize(350, 35);
		emailField.setPrefSize(350, 35);
		emailField.setMinSize(350, 35);
		emailField.setPromptText("you@example.com");
		emailField.setStyle(
			"-fx-font-family:Serif;" +
			"-fx-font-size: 16px;" +
			"-fx-background-color:" + fieldBackground + ";" +
			"-fx-text-fill:" + textColor + ";" +
			"-fx-border-color:" + fieldBorder + ";" +
			"-fx-prompt-text-fill:" + textColor + ";"
		);
		
		Button sendCodeButton = styledButton("Send Verification Email", isDarkTheme);
		sendCodeButton.setId("send-code-button");
		sendCodeButton.setDefaultButton(true);
		sendCodeButton.setOnAction(_ -> Thread.ofVirtual().start(() -> {
			boolean isGenerated = generateToken(emailField.getText());
			if (isGenerated) {
				boolean isSent = sendVerificationToken(emailField.getText());
				if (isSent) {
					emailAddress = emailField.getText();
					Platform.runLater(() -> mainEmailVerificationFlow.setContent(verifyScene));
				} else Platform.runLater(() -> mainEmailVerificationFlow.setContent(failScene));
			} else mainEmailVerificationFlow.setContent(failScene);
		}));
		return new VBox(12, emailLabel, instructions, emailField, sendCodeButton);
	}
	
	private static @NotNull VBox getVerifyLayout(boolean isDarkTheme) {
		Image gmailIcon = new Image(Objects.requireNonNull(
				EmailVerificationFlowPanels.class.getResourceAsStream(ICONS_FOLDER_PATH + "gmail.png")),
				64, 64, true, true);
		ImageView gmailView = new ImageView(gmailIcon);
		gmailView.setId("gmail-icon");
		
		Label verifyLabel = styledLabel("Verify Your Email", textColor, 18, true);
		verifyLabel.setId("verify-label");
		Label emailLabel = styledLabel(emailField.getText(), textColor, 16, true);
		emailLabel.setId("email-label");
		
		Label instructions = styledLabel("We’ve sent a verification link to your email address.", secondaryText, 14, false);
		instructions.setId("verify-instructions");
		instructions.setWrapText(true);
		
		Label noteLabel = styledLabel("Note: The first email may take longer to arrive.", secondaryText, 14, false);
		noteLabel.setId("note-label");
		
		Label afterVerification = styledLabel("Open the link in your email, then click the button (Verify) below to continue.", secondaryText, 14, false);
		afterVerification.setId("after-verification-label");
		
		VBox labelsBox = new VBox(5, instructions, noteLabel, afterVerification);
		labelsBox.setAlignment(Pos.CENTER);
		
		VBox v = getTokenLayout(verifyLabel, emailLabel, gmailView, labelsBox, isDarkTheme);
		v.setId("verify-layout");
		v.setStyle("-fx-background-color:" + backgroundColor + ";");
		return v;
	}
	
	private static @NotNull VBox getTokenLayout(Label verifyLabel, Label emailLabel, ImageView gmailView, VBox labelsBox, boolean isDarkTheme) {
		ProgressIndicator loading = new ProgressIndicator();
		loading.setId("loading-indicator");
		loading.setPrefSize(60, 60);
		
		Button verifyButton = styledButton("Verify", isDarkTheme);
		verifyButton.setId("verify-button");
		verifyButton.setOnAction(_ -> {
			loading.setVisible(true);
			boolean verified = isEmailVerified();
			if (verified) {
				loading.setVisible(false);
				verifyLabel.setText("Your email has been verified successfully!");
				verifyLabel.setTextFill(Color.web(successColor));
				
				Thread.ofVirtual().start(() -> {
					prefs.put(USER_EMAIL, emailAddress);
					prefs.put(USER_EMAIL_VERIFIED, emailAddress);
					try {
						Map<String, Object> updates = new HashMap<>();
						updates.put(UserDataUploader.Keys.EMAIL, emailAddress);
						updateUserData(prefs.get(BattorionCoreConstants.PrefKeysIdentifiers.USER_IDENTIFIER, null), updates);
					} catch (Exception e) {
						printErrorMessage(e);
					}
				});
				Platform.runLater(() -> runnable.run());
			} else {
				verifyLabel.setText("Verification failed. Please try again.");
				verifyLabel.setTextFill(Color.web(errorColor));
			}
		});
		
		Button resendButton = styledButton("Resend Verification Email", isDarkTheme);
		resendButton.setId("resend-button");
		resendButton.setOnAction(_ -> Thread.ofVirtual().start(() -> {
			boolean isGenerated = generateToken(emailAddress);
			if (isGenerated) {
				boolean isSent = sendVerificationToken(emailAddress);
				if (isSent) Platform.runLater(() -> mainEmailVerificationFlow.setContent(verifyScene));
				else Platform.runLater(() -> mainEmailVerificationFlow.setContent(failScene));
			} else mainEmailVerificationFlow.setContent(failScene);
		}));
		
		HBox buttonsBox = new HBox(10, verifyButton, resendButton);
		buttonsBox.setAlignment(Pos.CENTER);
		
		Region spacer1 = new Region();
		spacer1.setMinHeight(15);
		Region spacer2 = new Region();
		spacer2.setMinHeight(25);
		
		VBox tokenLayout = new VBox(10, gmailView, verifyLabel, emailLabel, labelsBox, spacer1, loading, spacer2, buttonsBox);
		tokenLayout.setId("token-layout");
		tokenLayout.setAlignment(Pos.CENTER);
		tokenLayout.setPadding(new Insets(20));
		return tokenLayout;
	}
	
	private static @NotNull VBox getFailLayout(boolean isDarkTheme) {
		Label failLabel = styledLabel("Something went wrong", errorColor, 18, true);
		failLabel.setId("fail-label");
		Label instructions = styledLabel("We couldn't send the verification email. Please try again later.", secondaryText, 14, false);
		instructions.setId("fail-instructions");
		instructions.setWrapText(true);
		
		Button backButton = styledButton("Back to Email Entry", isDarkTheme);
		backButton.setId("back-button");
		backButton.setOnAction(_ -> Platform.runLater(() -> mainEmailVerificationFlow.setContent(emailScene)));
		
		VBox failLayout = new VBox(12, failLabel, instructions, backButton);
		failLayout.setId("fail-layout");
		failLayout.setAlignment(Pos.CENTER);
		failLayout.setPadding(new Insets(30));
		failLayout.setStyle("-fx-background-color:" + backgroundColor + ";");
		return failLayout;
	}
	
	private static Label styledLabel(String text, String color, int fontSize, boolean bold) {
		Label label = new Label(text);
		label.setFont(Font.font("Serif", bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize));
		label.setTextFill(Color.web(color));
		return label;
	}
	
	private static TextField styledTextField(String background, String border, String textColor) {
		TextField field = new TextField();
		field.setPrefWidth(300);
		field.setStyle("-fx-font-family:Serif; -fx-background-color:" + background + "; -fx-text-fill:" + textColor + "; -fx-border-color:" + border + "; -fx-prompt-text-fill:" + textColor + ";");
		return field;
	}
	
	private static Button styledButton(String text, boolean isDarkTheme) {
		Button btn = new Button(text);
		String style = getButtonStyle(isDarkTheme);
		String hover = isDarkTheme ? "-fx-background-color:#666666;" : "-fx-background-color:#c0c0c0;";
		btn.setStyle(style);
		btn.setOnMouseEntered(_ -> btn.setStyle(hover + "-fx-text-fill:white; -fx-font-size:14px; -fx-font-family:Serif; -fx-padding:6px 12px; -fx-border-radius:6px; -fx-background-radius:6px;"));
		btn.setOnMouseExited(_ -> btn.setStyle(style));
		return btn;
	}
	
	private static String getButtonStyle(boolean isDarkTheme) {
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
	
	private static void styleScroll(ScrollPane scrollPane, boolean isDarkTheme) {
		String bg = isDarkTheme ? "#000000" : "#ffffff";
		String thumb = isDarkTheme ? "#555555" : "#cccccc";
		String track = isDarkTheme ? "#222222" : "#eeeeee";
		scrollPane.setStyle(
			"-fx-background:" + bg + ";" +
			"-fx-control-inner-background:" + bg + ";" +
			"-fx-background-color:" + bg + ";"
		);
		Platform.runLater(() -> {
			scrollPane.lookupAll(".scroll-bar").forEach(node -> {
				if (node instanceof ScrollBar sb) sb.setStyle("-fx-background-color:" + track + ";");
			});
			scrollPane.lookupAll(".thumb").forEach(node ->
				node.setStyle("-fx-background-color:" + thumb + "; -fx-background-radius:6px;")
			);
		});
	}
}