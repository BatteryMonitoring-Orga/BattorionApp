package com.battery_level_alarm.monitoring.feedback_system;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_GAVE_FEEDBACK;
import static com.battery_level_alarm.monitoring.feedback_system.FeedbackPanel.showAlert;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class FeedbackSender {
	public static void sendFeedback(String userId, String name, String email, String feedback, File image) {
		try {
			String apiUrl = "https://battorion-website.vercel.app/api/feedback";
			Map<String, String> feedbackMap = new HashMap<>();
			feedbackMap.put("UserID", userId);
			feedbackMap.put("UserName", name);
			feedbackMap.put("UserEmail", email);
			feedbackMap.put("UserFeedback", feedback);
			if(image != null) {
				try (InputStream is = new FileInputStream(image)) {
					byte[] bytes = is.readAllBytes();
					String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
					feedbackMap.put("SupportImage", base64);
				}
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(feedbackMap);
			
			HttpURLConnection conn = (HttpURLConnection) URI.create(apiUrl).toURL().openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			try (OutputStream os = conn.getOutputStream()) {
				os.write(json.getBytes(StandardCharsets.UTF_8));
			}
			
			int responseCode = conn.getResponseCode();
			StringBuilder responseBuilder = new StringBuilder();
			InputStream inputStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line);
				}
			}
			
			String jsonResponse = responseBuilder.toString();
			Map<String, Object> responseMap = mapper.readValue(jsonResponse, new TypeReference<>() {});
			String status = (String) responseMap.getOrDefault("status", "error");
			String message = (String) responseMap.getOrDefault("message", "Unknown response from server");
			
			if ("success".equals(status)) {
				prefs.putBoolean(USER_GAVE_FEEDBACK, true);
				Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, message));
			} else if("warning".equals(status)) {
				Platform.runLater(() -> showAlert(Alert.AlertType.WARNING,
						"Failed to send feedback. HTTP Code: " + responseCode + "\n" + message));
			} else {
				Platform.runLater(() -> showAlert(Alert.AlertType.ERROR,
						"Failed to send feedback. HTTP Code: " + responseCode + "\n" + message));
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}
}