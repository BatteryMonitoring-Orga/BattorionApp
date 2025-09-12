package com.battery_level_alarm.monitoring.server_side.email_verification;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;
import static com.battery_level_alarm.monitoring.server_side.email_verification.EmailVerificationFlowPanels.emailAddress;
import static com.battery_level_alarm.monitoring.system_core.Battorion.prefs;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.PrefKeysIdentifiers.USER_EMAIL_VERIFIED;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.UserIdentifier.getOrCreateUserId;

public class EmailVerification {
	public static boolean isEmailVerified() {
		if(EmailChecker.checkEmail(emailAddress)) {
			prefs.put(USER_EMAIL_VERIFIED, emailAddress);
			return true;
		}
		return false;
	}
	
	static boolean generateToken(String email) {
		try {
			String apiUrl = "https://battorion-ap-is.vercel.app/api/email_verification/generate-token";
			Map<String, String> payload = new HashMap<>();
			payload.put("email", email);
			payload.put("response", "Desktop");
			
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
			if (responseCode != 200) {
				return false;
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
		return true;
	}
	
	static boolean sendVerificationToken(String email) {
		try {
			String apiUrl = "https://battorion-ap-is.vercel.app/api/email_verification/send-verification-email";
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
			if (responseCode != 200) {
				return false;
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
		return true;
	}
}
