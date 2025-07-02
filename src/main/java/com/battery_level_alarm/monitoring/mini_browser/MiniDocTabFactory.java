package com.battery_level_alarm.monitoring.mini_browser;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;

import static com.battery_level_alarm.monitoring.battery_report.HTMLOpener.readHtmlAsText;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.tabPane;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocHtmlFix.fixRelativePaths;
import static com.battery_level_alarm.monitoring.questionnaires.AppendixesQuestionnaire.appendicesActionTitle;
import static com.battery_level_alarm.monitoring.questionnaires.AppendixesQuestionnaire.getStringAppendicesMap;
import static com.battery_level_alarm.monitoring.questionnaires.StaticQuestionnaire.aboutActionTitle;
import static com.battery_level_alarm.monitoring.questionnaires.StaticQuestionnaire.getStringRunnableMap;

public class MiniDocTabFactory {
	static Tab createTab(String title, String htmlContent) {
		WebView webView = new WebView();
		webView.setId("web-view");
		WebEngine engine = getWebEngine(htmlContent, webView);
		engine.documentProperty().addListener((_, _, newDoc) -> {
			if (newDoc != null) {
				engine.executeScript(
						"document.addEventListener('click', function(e) {" +
								"  let target = e.target;" +
								"  while(target && target.tagName !== 'A') {" +
								"    target = target.parentElement;" +
								"  }" +
								"  if(target && target.href) {" +
								"    if(target.href.startsWith('action:')) {" +
								"      e.preventDefault();" +
								"      alert(target.href);" +
								"    } else if (target.href.startsWith('openTab-')) {" +
								"      e.preventDefault();" +
								"      alert(target.href);" +
								"    }" +
								"  }" +
								"});"
				);
			}
		});
		
		BorderPane wrapper = new BorderPane(webView);
		wrapper.setId("tab-wrapper");
		Tab tab = new Tab(title);
		tab.setId("tab-" + title.replaceAll("\\s+", "-").toLowerCase());
		tab.setContent(wrapper);
		return tab;
	}
	
	private static @NotNull WebEngine getWebEngine(String htmlContent, WebView webView) {
		WebEngine engine = webView.getEngine();
		engine.loadContent(htmlContent, "text/html");
		engine.setOnAlert(event -> {
			String data = event.getData();
			if (data.startsWith("openTab-")) {
				Platform.setImplicitExit(false);
				Platform.runLater(() -> {
					Tab newTab;
					if(aboutActionTitle.containsKey(data)) {
						String realPath = getStringRunnableMap().get(data);
						String content = readHtmlAsText(realPath);
						String fixedHtml = fixRelativePaths(content, realPath);
						newTab = createTab(aboutActionTitle.get(data), fixedHtml);
					} else {
						newTab = createTab(appendicesActionTitle.get(data), getStringAppendicesMap().get(data));
					}
					tabPane.getTabs().add(newTab);
					tabPane.getSelectionModel().select(newTab);
				});
			}
		});
		return engine;
	}
}
