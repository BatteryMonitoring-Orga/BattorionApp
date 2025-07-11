package com.battery_level_alarm.monitoring.feedback_system;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

import static com.battery_level_alarm.monitoring.feedback_system.FeedbackSender.logException;
//        fetchJsonFile("settings.json");
//        fetchJsonFile("instructions.json");
public class FileFetcher {
	public static void fetchJsonFile(String fileName) {
		SwingUtilities.invokeLater(() -> {
			try {
				String url = "https://battorion-website.vercel.app/api/get-file?name=" + fileName;
				HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
				conn.setRequestMethod("GET");
				
				int responseCode = conn.getResponseCode();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						(responseCode == 200) ? conn.getInputStream() : conn.getErrorStream()
				));
				StringBuilder responseBuilder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line);
				}
				reader.close();
				
				String json = responseBuilder.toString();
				ObjectMapper mapper = new ObjectMapper();
				
				if (responseCode == 200) {
					Map<String, Object> result = mapper.readValue(json, new TypeReference<>() {});
				} else {
					Map<String, Object> error = mapper.readValue(json, new TypeReference<>() {});
				}
			} catch (Exception e) {
				logException(e);
			}
		});
	}
}
