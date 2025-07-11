package com.battery_level_alarm.monitoring.flow_chat;
import java.util.List;
import java.util.function.Consumer;

public record ChatStep(
		String message,
		List<ChatOption> options,
		boolean isTextInput,
		Consumer<String> textHandler
) { }