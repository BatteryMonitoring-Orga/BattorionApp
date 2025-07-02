package com.battery_level_alarm.monitoring.mini_browser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

public class MiniDocExternalFilesLoader {
	static String loadMarkdownAsHtml(String filename) {
		try (InputStream is = new java.io.FileInputStream(filename)) {
			String markdown = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			Parser parser = Parser.builder().build();
			HtmlRenderer renderer = HtmlRenderer.builder().build();
			Node document = parser.parse(markdown);
			return renderer.render(document);
		} catch (Exception e) {
			return "<h2 style='color:red;'>Error loading markdown: " + filename + "</h2>";
		}
	}
	
	static String loadHtmlFromFile(String filename) {
		try (InputStream is = new java.io.FileInputStream(filename)) {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			return "<h2 style='color:red;'>Error loading: " + filename + "</h2>";
		}
	}
}
