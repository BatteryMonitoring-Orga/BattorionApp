package com.battery_level_alarm.monitoring.feedback_system;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataFetcher {
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static Map<String, Object> sendUserIdAndGetFilteredData(String userId, List<String> keys) throws Exception {
		if (userId == null || userId.isEmpty()) {
			throw new IllegalArgumentException("userId is required");
		}
		
		String apiUrl = "https://battorion-website.vercel.app/api/get-user?id=" + URLEncoder.encode(userId, StandardCharsets.UTF_8);
		URI uri = URI.create(apiUrl);
		URL url = uri.toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		int code = conn.getResponseCode();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				(code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream(),
				StandardCharsets.UTF_8))) {
			
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			String jsonResponse = sb.toString();
			if (code != 200) {
				throw new Exception("Failed: HTTP error code: " + code + "\nResponse: " + jsonResponse);
			}
			
			Map<String, Object> responseMap = mapper.readValue(jsonResponse, new TypeReference<>() {});
			String status = (String) responseMap.get("status");
			if (!"success".equals(status)) {
				throw new Exception("API Error: " + responseMap.get("message"));
			}
			
			Object dataObj = responseMap.get("data");
			if (!(dataObj instanceof Map)) {
				throw new Exception("Invalid data format from API");
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) dataObj;
			return filterData(dataMap, keys);
		}
	}
	
	private static Map<String, Object> filterData(Map<String, Object> fullData, List<String> keys) {
		Map<String, Object> filtered = new HashMap<>();
		for (String key : fullData.keySet()) {
			Object value = fullData.get(key);
			
			if (key.equals(UserDataUploader.Keys.LICENSE) ||
					key.equals(UserDataUploader.Keys.HARDWARE) ||
					key.equals(UserDataUploader.Keys.ANALYTICS)) {
				Map<String, Object> nestedMap = null;
				if (value instanceof String strVal) {
					try {
						nestedMap = mapper.readValue(strVal, new TypeReference<>() {});
					} catch (Exception e) {
						continue;
					}
				} else if (value instanceof Map<?, ?> mapVal) {
					nestedMap = new HashMap<>();
					for (Map.Entry<?, ?> entry : mapVal.entrySet()) {
						if (entry.getKey() instanceof String) {
							nestedMap.put((String) entry.getKey(), entry.getValue());
						}
					}
				}
				
				if (nestedMap != null) {
					for (String subKey : keys) {
						if (nestedMap.containsKey(subKey)) {
							filtered.put(subKey, nestedMap.get(subKey));
						}
					}
				}
			} else if (keys.contains(key)) {
				filtered.put(key, value);
			}
		}
		return filtered;
	}
}