package com.battery_level_alarm.monitoring.flow_chat;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.function.Consumer;

import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;

public class DynamicStepsFlow {
	private static Queue<ChatStep> chatFlow = new LinkedList<>();
	private static boolean isConversationFlowBuilt = false;
	
	public static void flow(Stage primaryStage, double xValue, double yValue) {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setWidth(1);
		primaryStage.setHeight(1);
		primaryStage.setOpacity(0.01);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.show();
		if(isConversationFlowBuilt) {
			showNextStep(primaryStage, xValue, yValue);
			isConversationFlowBuilt = false;
		} else {
			primaryStage.close();
		}
	}
	
	public static void buildConversationFlow(Queue<ChatStep> chatFlow) {
		DynamicStepsFlow.chatFlow = chatFlow;
		isConversationFlowBuilt = true;
	}
	
	private static void showNextStep(Stage stage, double xValue, double yValue) {
		if (chatFlow.isEmpty()) {
			stage.close();
			return;
		}
		
		ChatStep step = chatFlow.poll();
		if (step.isTextInput()) {
			showTextInputStep(stage, step.message(), step.textHandler(), xValue, yValue);
		} else if (step.options().isEmpty()) {
			showMessage(stage, step.message(), xValue, yValue);
		} else {
			showDynamicQuestion(stage, step.message(), xValue, yValue, step.options());
		}
	}
	
	private static void showDynamicQuestion(Stage owner, String question, double xValue, double yValue, List<ChatOption> options) {
		Label questionLabel = createStyledLabel(question);
		Popup questionPopup = createPopup(questionLabel, xValue, yValue);
		questionPopup.show(owner);
		
		questionLabel.applyCss();
		questionLabel.layout();
		
		double labelWidth = questionLabel.getLayoutBounds().getWidth();
		double labelHeight = questionLabel.getLayoutBounds().getHeight();
		List<Popup> optionPopups = new LinkedList<>();
		
		if (options.size() == 1) {
			ChatOption opt = options.getFirst();
			Button button = createStyledButton(opt.text(), 10);
			double btnX = xValue + labelWidth + 20;
			double btnY = yValue + labelHeight / 2 - 22;
			Popup buttonPopup = createPopup(button, btnX, btnY);
			buttonPopup.show(owner);
			optionPopups.add(buttonPopup);
			
			button.setOnAction(_ -> {
				questionPopup.hide();
				optionPopups.forEach(Popup::hide);
				opt.action().run();
				showNextStep(owner, xValue, yValue);
			});
		} else if (options.size() == 2) {
			ChatOption opt1 = options.get(0);
			ChatOption opt2 = options.get(1);
			Button btn1 = createStyledButton(opt1.text(), 10);
			Button btn2 = createStyledButton(opt2.text(), 10);
			
			double btn1X = xValue + labelWidth + 20;
			double btnY = yValue + labelHeight / 2 - 22;
			double btn2X = btn1X + opt1.text().length() * 22 + 10;
			
			Popup btn1Popup = createPopup(btn1, btn1X, btnY);
			Popup btn2Popup = createPopup(btn2, btn2X, btnY);
			
			btn1Popup.show(owner);
			btn2Popup.show(owner);
			
			optionPopups.add(btn1Popup);
			optionPopups.add(btn2Popup);
			
			btn1.setOnAction(_ -> {
				questionPopup.hide();
				optionPopups.forEach(Popup::hide);
				opt1.action().run();
				showNextStep(owner, xValue, yValue);
			});
			
			btn2.setOnAction(_ -> {
				questionPopup.hide();
				optionPopups.forEach(Popup::hide);
				opt2.action().run();
				showNextStep(owner, xValue, yValue);
			});
		} else {
			double btnX = xValue + labelWidth / 2 - 30;
			double btnY = yValue + labelHeight + 20;
			
			for (ChatOption opt : options) {
				Button button = createStyledButton(opt.text(), 10);
				Popup buttonPopup = createPopup(button, btnX, btnY);
				buttonPopup.show(owner);
				optionPopups.add(buttonPopup);
				
				button.setOnAction(_ -> {
					questionPopup.hide();
					optionPopups.forEach(Popup::hide);
					opt.action().run();
					showNextStep(owner, xValue, yValue);
				});
				btnY += 60;
			}
		}
	}
	
	private static void showTextInputStep(Stage owner, String message, Consumer<String> handler, double xValue, double yValue) {
		Stage inputStage = new Stage();
		inputStage.initStyle(StageStyle.TRANSPARENT);
		inputStage.initOwner(owner);
		inputStage.setAlwaysOnTop(true);
		
		Label label = createStyledLabel(message);
		label.setWrapText(true);
		
		TextField input = new TextField();
		input.setPromptText("Type your response...");
		input.setStyle("-fx-background-radius: 10px; -fx-font-size: 14px; -fx-padding: 6px 10px;");
		
		Button sendBtn = createStyledButton("Send", 10);
		sendBtn.setDefaultButton(true);
		sendBtn.setOnAction(_ -> {
			String response = input.getText().trim();
			if (!response.isEmpty()) {
				handler.accept(response);
			}
			inputStage.close();
			showNextStep(owner, xValue, yValue);
		});
		
		VBox root = new VBox(12, label, input, sendBtn);
		root.setAlignment(Pos.CENTER_LEFT);
		
		String bgColor = isDarkMode ? "rgba(255,255,255,0.85)" : "rgba(0,0,0,0.7)";
		String borderColor = isDarkMode ? "black" : "white";
		root.setStyle(
			"-fx-background-color: " + bgColor + ";" +
			"-fx-border-color: " + borderColor + ";" +
			"-fx-border-width: 2px;" +
			"-fx-background-radius: 30px;" +
			"-fx-border-radius: 30px;" +
			"-fx-padding: 22px;"
		);
		
		Scene scene = new Scene(root);
		scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
		inputStage.setScene(scene);
		inputStage.setX(xValue);
		inputStage.setY(yValue);
		inputStage.show();
		Platform.runLater(input::requestFocus);
	}
	
	private static void showMessage(Stage owner, String message, double xValue, double yValue) {
		Label messageLabel = createStyledLabel(message);
		Popup messagePopup = createPopup(messageLabel, xValue, yValue);
		messagePopup.show(owner);
		
		PauseTransition pause = new PauseTransition(Duration.seconds(3));
		pause.setOnFinished(_ -> {
			messagePopup.hide();
			showNextStep(owner, xValue, yValue);
		});
		pause.play();
	}
	
	private static Label createStyledLabel(String text) {
		String bgColor = isDarkMode ? "rgba(255,255,255,0.85)" : "rgba(0,0,0,0.7)";
		String textColor = isDarkMode ? "black" : "white";
		String borderColor = isDarkMode ? "black" : "white";
		
		Label label = new Label(text);
		label.setStyle(
				"-fx-font-size: 16px;" +
						"-fx-padding: 10px 20px;" +
						"-fx-border-color: " + borderColor + ";" +
						"-fx-border-width: 2px;" +
						"-fx-background-color: " + bgColor + ";" +
						"-fx-text-fill: " + textColor + ";" +
						"-fx-background-radius: 20px;" +
						"-fx-border-radius: 20px;"
		);
		return label;
	}
	
	private static Button createStyledButton(String text, double padding) {
		String bgColor = isDarkMode ? "rgba(255,255,255,0.85)" : "rgba(0,0,0,0.7)";
		String textColor = isDarkMode ? "black" : "white";
		String borderColor = isDarkMode ? "black" : "white";
		
		Button button = new Button(text);
		button.setCursor(Cursor.HAND);
		button.setStyle(
				"-fx-font-size: 14px;" +
						"-fx-padding: " + padding + "px " + (padding * 2) + "px;" +
						"-fx-border-color: " + borderColor + ";" +
						"-fx-border-width: 2px;" +
						"-fx-background-color: " + bgColor + ";" +
						"-fx-text-fill: " + textColor + ";" +
						"-fx-background-radius: 50;" +
						"-fx-border-radius: 50;"
		);
		return button;
	}
	
	private static Popup createPopup(javafx.scene.Node content, double x, double y) {
		Popup popup = new Popup();
		StackPane pane = new StackPane(content);
		pane.setAlignment(Pos.CENTER);
		popup.getContent().add(pane);
		popup.setX(x);
		popup.setY(y);
		return popup;
	}
}
