package com.battery_level_alarm.monitoring.server_side.email_verification;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UserIdentifier.getOrCreateUserId;

public class EmailChecker {
	public static boolean checkEmail(String email) {
		try {
			String apiUrl = "https://battorion-ap-is.vercel.app/api/email_verification/check-verification";
			Map<String, String> payload = new HashMap<>();
			payload.put("email", email);
			payload.put("id", getOrCreateUserId());
			
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(payload);
			
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
			if (responseMap.containsKey("verified")) {
				int verified = (int) responseMap.getOrDefault("verified", 0);
				return verified == 1;
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
		return false;
	}
}
