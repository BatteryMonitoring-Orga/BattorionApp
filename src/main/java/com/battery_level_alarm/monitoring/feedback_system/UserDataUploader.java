package com.battery_level_alarm.monitoring.feedback_system;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UserDataUploader {
	private static final ObjectMapper mapper = new ObjectMapper();
	public enum LICENSE {
		FREE_TRIAL,
		ESSENTIAL,
		PROFESSIONAL,
		ENTERPRISE
	}
	
	public enum STATUS {
		ACTIVE,
		SUSPENDED,
		PENDING,
		DELETED
	}
	
	public static class Keys {
		public static final String ID = "id";
		public static final String STATUS = "status";
		public static final String CREATED_AT = "created_at";
		public static final String VERSION = "version";
		public static final String OS = "os";
		public static final String EMAIL = "email";
		
		public static final String LICENSE = "license";
		public static final String LICENSE_TYPE = "type";
		public static final String LICENSE_EXPIRES_AT = "expiresAt";
		public static final String LICENSE_ACTIVATED_AT = "activatedAt";
		public static final String LICENSE_MAX_DEVICES = "maxDevices";
		
		public static final String HARDWARE = "hardware";
		public static final String HARDWARE_HWID = "hwid";
		public static final String HARDWARE_CPU = "cpu";
		public static final String HARDWARE_RAM = "ram";
		public static final String HARDWARE_DISK = "disk";
		
		public static final String ANALYTICS = "analytics";
		public static final String ANALYTICS_LAST_ONLINE = "lastOnline";
		public static final String ANALYTICS_RUN_COUNT = "runCount";
		public static final String ANALYTICS_VERSION_USED = "versionUsed";
	}
	
	public static void updateUserData(String userId, Map<String, Object> updates) throws Exception {
		String apiUrl = "https://battorion-website.vercel.app/api/update-user";
		if (userId == null || userId.isEmpty()) {
			throw new IllegalArgumentException("userId is required");
		}
		updates.put(Keys.ID, userId);
		String json = mapper.writeValueAsString(updates);
		
		URI uri = URI.create(apiUrl);
		URL url = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setDoOutput(true);
		
		try (OutputStream os = conn.getOutputStream()) {
			os.write(json.getBytes(StandardCharsets.UTF_8));
		}
		
		int responseCode = conn.getResponseCode();
		InputStream inputStream = (responseCode >= 200 && responseCode < 300)
				? conn.getInputStream()
				: conn.getErrorStream();
		
		String responseBody = "";
		if (inputStream != null) {
			try (inputStream) {
				responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}
		} if (responseCode != 200) {
			throw new RuntimeException("Failed to update user data. HTTP code: " + responseCode + "\nResponse: " + responseBody);
		}
	}
}
