package com.battery_level_alarm.monitoring.flow_chat;
import com.battery_level_alarm.monitoring.feedback_system.UserDataUploader;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;
import java.util.*;
import java.util.List;

import static com.battery_level_alarm.monitoring.feedback_system.UserDataUploader.updateUserData;
import static com.battery_level_alarm.monitoring.flow_chat.DynamicStepsFlow.flow;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.westSideButton;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class FeedbackFlowChat {
	public static void callSmartFeedbackStepsFlow() {
		Platform.runLater(() -> {
			Stage feedbackFlowStage = new Stage();
			Queue<ChatStep> chatFlow = new LinkedList<>();
			
			chatFlow.add(new ChatStep(
					"👋 Hey there! We'd love to hear your thoughts. Did you recently use our app?",
					List.of(
							new ChatOption("Yes", () -> chatFlow.add(new ChatStep(
									"😊 How would you rate your overall experience?",
									List.of(
											new ChatOption("⭐ Excellent", () -> showPositiveFeedbackSteps(chatFlow)),
											new ChatOption("⭐ Average", () -> showNegativeFeedbackSteps(chatFlow)),
											new ChatOption("⭐ Poor", () -> showNegativeFeedbackSteps(chatFlow))
									),
									false, null
							))),
							new ChatOption("Not yet", () -> chatFlow.add(new ChatStep(
									"No worries! Feel free to come back when you've had a chance to explore it.",
									List.of(),
									false, null
							)))
					),
					false, null
			));
			
			DynamicStepsFlow.buildConversationFlow(chatFlow);
			Point locationOnScreen = westSideButton.getLocationOnScreen();
			double x = locationOnScreen.getX();
			double y = locationOnScreen.getY();
			flow(feedbackFlowStage, x, y);
		});
	}
	
	private static void showPositiveFeedbackSteps(Queue<ChatStep> chatFlow) {
		chatFlow.add(new ChatStep(
				"Great! What did you like most about the app?",
				List.of(),
				true, response -> System.out.println("Positive Highlight: " + response)
		));
		
		chatFlow.add(new ChatStep(
				"Would you like to suggest anything we could improve?",
				List.of(
						new ChatOption("Yes", () -> chatFlow.add(new ChatStep(
								"Please share your suggestions:",
								List.of(),
								true, suggestion -> System.out.println("Suggestion: " + suggestion)
						))),
						new ChatOption("No", () -> chatFlow.add(new ChatStep(
								"Alright! 😊",
								List.of(),
								false, null
						)))
				),
				false, null
		));
		
		appendFinalSteps(chatFlow);
	}
	
	private static void showNegativeFeedbackSteps(Queue<ChatStep> chatFlow) {
		chatFlow.add(new ChatStep(
				"We're sorry to hear that 😥 What went wrong?",
				List.of(),
				true, response -> System.out.println("Issue reported: " + response)
		));
		
		chatFlow.add(new ChatStep(
				"Do you have any suggestions for improvement?",
				List.of(
						new ChatOption("Yes", () -> chatFlow.add(new ChatStep(
								"Please share your suggestions:",
								List.of(),
								true, suggestion -> System.out.println("Suggestion: " + suggestion)
						))),
						new ChatOption("No", () -> chatFlow.add(new ChatStep(
								"Thanks for your honesty.",
								List.of(),
								false, null
						)))
				),
				false, null
		));
		
		appendFinalSteps(chatFlow);
	}
	
	private static void appendFinalSteps(Queue<ChatStep> chatFlow) {
		chatFlow.add(new ChatStep(
				"Would you like to rate the app performance?",
				List.of(
						new ChatOption("Sure", () -> chatFlow.add(new ChatStep(
								"How would you rate performance/speed?",
								List.of(
										new ChatOption("🚀 Very Fast", () -> {}),
										new ChatOption("⚡ Fast", () -> {}),
										new ChatOption("⏱ Average", () -> {}),
										new ChatOption("🐢 Slow", () -> {}),
										new ChatOption("🐌 Very Slow", () -> {})
								),
								false, null
						))),
						new ChatOption("Skip", () -> {})
				),
				false, null
		));
		
		chatFlow.add(new ChatStep(
				"Would you like us to contact you if we implement your suggestion?",
				List.of(
						new ChatOption("Yes", () -> chatFlow.add(new ChatStep(
								"Please enter your email:",
								List.of(),
								true, email -> {
									try {
										Map<String, Object> updates = new HashMap<>();
										updates.put(UserDataUploader.Keys.EMAIL, email);
										updateUserData(prefs.get(BattorionCoreConstants.PrefKeysIdentifiers.USER_IDENTIFIER, null), updates);
									} catch (Exception e) {
										printErrorMessage(e);
									}
						}))),
						new ChatOption("No", () -> {})
				),
				false, null
		));
		
		chatFlow.add(new ChatStep(
				"🎉 You're awesome! Thanks for helping us improve! Have a great day 🌟",
				List.of(),
				false, null
		));
	}
}
