package com.battery_level_alarm.monitoring.flow_chat;
import com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.Battorion.refreshMasterFrame;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.NEW_BATTORION_USER;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_GAVE_FEEDBACK;
import static com.battery_level_alarm.monitoring.flow_chat.DynamicStepsFlow.flow;
import static com.battery_level_alarm.monitoring.system_core.helpers.BattorionButtonHelper.setupFeedbackPanel;
import static com.battery_level_alarm.monitoring.system_core.helpers.MainButtons.westSideButton;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class CallStepsFlow {
	private static final Queue<ChatStep> chatFlow = new LinkedList<>();
	private static final LocalDateTime sessionStartTime = LocalDateTime.now();
	public static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	public static ScheduledFuture<?> scheduledTask;
	private static final boolean isSkipped = true;
	
	public static void handleUserFlows() {
		if(isSkipped) {
			return;
		} if (prefs.getBoolean(NEW_BATTORION_USER, true)) {
			scheduledTask = scheduler.scheduleAtFixedRate(() -> {
				try {
					if (shouldAskAboutFont()) {
						callFontStepsFlow();
					} if (!prefs.getBoolean(NEW_BATTORION_USER, true)) {
						scheduledTask.cancel(false);
						scheduler.shutdown();
					}
				} catch (Exception e) {
					printErrorMessage(e);
				}
			}, 0, 60, TimeUnit.MINUTES);
		} if (!prefs.getBoolean(USER_GAVE_FEEDBACK, false) && shouldAskForFeedback()) {
			callFeedbackStepsFlow();
		}
	}
	
	private static boolean shouldAskAboutFont() {
		boolean newUser = prefs.getBoolean(NEW_BATTORION_USER, true);
		if (!newUser) return false;
		
		long minutesSinceLaunch = ChronoUnit.MINUTES.between(sessionStartTime, LocalDateTime.now());
		return minutesSinceLaunch >= 60;
	}
	
	private static boolean shouldAskForFeedback() {
		String startStr = prefs.get(BattorionCoreConstants.PrefKeysIdentifiers.BATTORION_STARTED_AT, null);
		boolean alreadyGaveFeedback = prefs.getBoolean(USER_GAVE_FEEDBACK, false);
		if (startStr == null || alreadyGaveFeedback) return false;
		
		LocalDate startDate = LocalDate.parse(startStr);
		long daysSinceStart = ChronoUnit.DAYS.between(startDate, LocalDate.now());
		return daysSinceStart >= 10;
	}
	
	private static void callFontStepsFlow() {
		Platform.runLater(() -> {
			Stage fontFlow = new Stage();
			chatFlow.add(new ChatStep("Is the font style in Battorion suitable for you?", List.of(
					new ChatOption("Yes", () -> {
						prefs.putBoolean(NEW_BATTORION_USER, false);
						chatFlow.add(new ChatStep("Awesome! The font looks great 🎉", List.of(), false, null));
					}),
					new ChatOption("No", () -> chatFlow.add(new ChatStep(
							"Do you need to adjust the font size?\nPress Switch to change.",
							List.of(new ChatOption("Switch", () -> {
								System.out.println("Open font settings here.");
								prefs.putBoolean(NEW_BATTORION_USER, false);
								fontFlow.close();
							})), false, null
					)))
			), false, null));
			
			DynamicStepsFlow.buildConversationFlow(chatFlow);
			Point locationOnScreen = westSideButton.getLocationOnScreen();
			double x = locationOnScreen.getX();
			double y = locationOnScreen.getY();
			flow(fontFlow, x, y);
		});
	}
	
	public static void callFeedbackStepsFlow() {
		Platform.runLater(() -> {
			Stage fontFlow = new Stage();
			chatFlow.add(new ChatStep("How was your experience?",
				List.of(
					new ChatOption("Good", () -> chatFlow.add(new ChatStep("Would you like to leave feedback?",
						List.of(
							new ChatOption("Yes", () -> chatFlow.add(new ChatStep("We’d love to hear from you — just tap the \"Feedback\" to share your thoughts!",
									List.of(new ChatOption("Feedback", () -> {
										setupFeedbackPanel();
										refreshMasterFrame();
									})),
									false, null
							))),
							new ChatOption("No", () -> chatFlow.add(new ChatStep("Thanks for your time!", List.of(), false, null)))
						), false, null
					))),
					new ChatOption("Bad", () -> chatFlow.add(new ChatStep("Would you like to leave feedback?",
						List.of(
							new ChatOption("Yes", () -> chatFlow.add(new ChatStep("Please let us know what went wrong.\nJust tap the \"Feedback\" to share your issue!",
									List.of(new ChatOption("Feedback", () -> {
										setupFeedbackPanel();
										refreshMasterFrame();
									})),
									false, null
							))),
							new ChatOption("No", () -> chatFlow.add(new ChatStep("Sorry to hear that. Thanks anyway!", List.of(), false, null)))
						), false, null
					)))
				), false, null
			));
			
			DynamicStepsFlow.buildConversationFlow(chatFlow);
			Point locationOnScreen = westSideButton.getLocationOnScreen();
			double x = locationOnScreen.getX();
			double y = locationOnScreen.getY();
			flow(fontFlow, x, y);
		});
	}
}
