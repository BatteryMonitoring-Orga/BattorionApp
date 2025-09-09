package com.battery_level_alarm.monitoring.battery_report;
import com.battery_level_alarm.monitoring.core_utilities.BatteryInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;

public class BatteryReportAnalyzer {
	public static void analyze(String reportFilePath) {
		try {
			extractBatteryInfo(reportFilePath);
			Document doc = Jsoup.parse(new File(reportFilePath), "UTF-8");
			
			long lastCapacity = extractLastCapacityFromHistory(doc);
			long lastRuntime = extractLastRuntimeEstimate(doc);
			BatteryInfo.setLastMeasuredCapacity(lastCapacity);
			BatteryInfo.setEstimatedRuntimeMinutes(lastRuntime);
		} catch (IOException e) {
			printErrorMessage(e);
		}
	}
	
	private static void extractBatteryInfo(String reportPath) {
		try {
			String html = Files.readString(Path.of(reportPath));
			long designedCapacity = extractCapacity(html, "Design Capacity");
			long fullChargeCapacity = extractCapacity(html, "Full Charge Capacity");
			double health = (designedCapacity > 0)
					? (fullChargeCapacity * 100.0) / designedCapacity
					: 0.0;
			
			BatteryInfo.setDesignedCapacity(designedCapacity);
			BatteryInfo.setFullChargeCapacity(fullChargeCapacity);
			BatteryInfo.setHealthPercentage(health);
		} catch (IOException e) {
			printErrorMessage(e);
		}
	}
	
	private static long extractCapacity(String html, String label) {
		try {
			Pattern pattern = Pattern.compile(
					"<td>\\s*<span[^>]*>\\s*" + Pattern.quote(label) + "\\s*</span>\\s*</td>\\s*<td[^>]*>\\s*([\\d,]+)\\s*mWh",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			);
			Matcher matcher = pattern.matcher(html);
			if (matcher.find()) {
				String valueStr = matcher.group(1).replace(",", "").trim();
				return Long.parseLong(valueStr);
			}
		} catch (Exception ignored) {
		}
		return 0;
	}
	
	private static Element getTableAfterHeader(Document doc, String headerText) {
		for (Element header : doc.select("h1, h2, h3")) {
			if (header.text().trim().equalsIgnoreCase(headerText.trim())) {
				Element sibling = header.nextElementSibling();
				while (sibling != null && !sibling.tagName().equals("table")) {
					sibling = sibling.nextElementSibling();
				}
				return sibling;
			}
		}
		return null;
	}
	
	private static long extractLastCapacityFromHistory(Document doc) {
		Element table = getTableAfterHeader(doc, "Battery capacity history");
		if (table != null) {
			Element lastRow = table.select("tr").last();
			if (lastRow != null) {
				for (Element td : lastRow.select("td")) {
					String text = td.text().replace(",", "").replace("mWh", "").trim();
					if (text.matches("\\d+")) {
						try {
							return Long.parseLong(text);
						} catch (NumberFormatException ignored) {}
					}
				}
			}
		}
		return 0;
	}
	
	private static long extractLastRuntimeEstimate(Document doc) {
		Element table = getTableAfterHeader(doc, "Battery life estimates");
		if (table != null) {
			Element lastRow = table.select("tr").last();
			if (lastRow != null) {
				for (Element td : lastRow.select("td")) {
					String time = td.text().trim();
					if (time.matches("\\d+:\\d+")) {
						try {
							String[] parts = time.split(":");
							int hours = Integer.parseInt(parts[0].trim());
							int minutes = Integer.parseInt(parts[1].trim());
							return hours * 60L + minutes;
						} catch (Exception ignored) {}
					}
				}
			}
		}
		return 0;
	}
}