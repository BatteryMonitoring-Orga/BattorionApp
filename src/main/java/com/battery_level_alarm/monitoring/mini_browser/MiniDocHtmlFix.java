package com.battery_level_alarm.monitoring.mini_browser;
import java.io.File;

public class MiniDocHtmlFix {
	static String fixRelativePaths(String htmlContent, String htmlFilePath) {
		File htmlFile = new File(htmlFilePath);
		File parentDir = htmlFile.getParentFile();
		htmlContent = fixPaths(htmlContent, "(?i)(<link[^>]+href=[\"'])(?!https?:|file:|/)([^\"']+)([\"'])", parentDir);
		htmlContent = fixPaths(htmlContent, "(?i)(<a[^>]+href=[\"'])(?!https?:|file:|/)([^\"']+)([\"'])", parentDir);
		htmlContent = fixPaths(htmlContent, "(?i)(<img[^>]+src=[\"'])(?!https?:|file:|/)([^\"']+)([\"'])", parentDir);
		return htmlContent;
	}
	
	static String fixPaths(String html, String regex, File parentDir) {
		StringBuilder result = new StringBuilder();
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
		java.util.regex.Matcher matcher = pattern.matcher(html);
		int lastEnd = 0;
		while (matcher.find()) {
			result.append(html, lastEnd, matcher.start());
			String before = matcher.group(1);
			String relative = matcher.group(2);
			String after = matcher.group(3);
			File fullPath = new File(parentDir, relative);
			String absoluteUri = fullPath.toURI().toString();
			result.append(before).append(absoluteUri).append(after);
			lastEnd = matcher.end();
		}
		result.append(html.substring(lastEnd));
		return result.toString();
	}
}
