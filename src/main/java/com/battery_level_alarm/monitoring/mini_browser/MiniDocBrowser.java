package com.battery_level_alarm.monitoring.mini_browser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import static com.battery_level_alarm.monitoring.system_core.Battorion.isMiniBrowserLaunched;
import static com.battery_level_alarm.monitoring.battery_report.HTMLOpener.readHtmlAsText;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocEmptyMessage.createEmptyMessagePane;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocEmptyMessage.emptyMessagePane;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocHtmlFix.fixRelativePaths;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTabFactory.createTab;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopicsBuilder.TOPICS;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopicsBuilder.buildTopicsMap;
import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.*;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.StateVariables.isDarkMode;

public class MiniDocBrowser extends Application {
	private static final CountDownLatch latch = new CountDownLatch(1);
	static final TabPane tabPane = new TabPane();
	private static Stage primaryStage;
	
	private static boolean isDisplayed = false;
	private static boolean isDarkTheme = false;
	private static boolean waitingForStart = true;
	private static boolean isStageCreated = false;
	
	@Override
	public void start(Stage stage) {
		createPrimaryStage(stage);
	}
	
	private static void createPrimaryStage(Stage stage) {
		isMiniBrowserLaunched = true;
		isDisplayed = true;
		waitingForStart = false;
		isStageCreated = true;
		
		createEmptyMessagePane();
		configureTabPane();
		ListView<String> topicList = createTopicList();
		
		VBox sideBar = createSideBar(topicList);
		StackPane contentWrapper = createContentWrapper();
		SplitPane mainContent = createMainContent(stage, sideBar, contentWrapper);
		Scene scene = createScene(mainContent);
		configureStage(stage, scene);
		
		primaryStage = stage;
		stage.show();
		latch.countDown();
	}
	
	private static void configureTabPane() {
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		tabPane.getTabs().addListener((javafx.collections.ListChangeListener<Tab>) _ ->
				emptyMessagePane.setVisible(tabPane.getTabs().isEmpty()));
		tabPane.setId("tab-pane");
	}
	
	private static ListView<String> createTopicList() {
		ListView<String> topicList = new ListView<>();
		topicList.setId("topic-list");
		topicList.getItems().addAll(TOPICS.keySet());
		VBox.setVgrow(topicList, Priority.ALWAYS);
		topicList.setCellFactory(_ -> new ListCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else if (item.startsWith("─")) {
					setText(item);
					setStyle("-fx-font-weight: bold; -fx-background-color: #eeeeee; -fx-text-fill: #555;");
				} else {
					setText(item);
					setStyle("");
				}
			}
		});
		topicList.setOnMouseClicked(_ -> handleTopicSelection(topicList));
		return topicList;
	}
	
	private static void handleTopicSelection(ListView<String> topicList) {
		String selected = topicList.getSelectionModel().getSelectedItem();
		if (selected == null || selected.startsWith("─")) return;
		for (Tab tab : tabPane.getTabs()) {
			if (selected.equals(tab.getText())) {
				tabPane.getSelectionModel().select(tab);
				return;
			}
		}
		
		Tab newTab;
		String content = TOPICS.get(selected).get();
		if(content == null) {
			buildTopicsMap();
			content = TOPICS.get(selected).get();
		} if (content.startsWith("external::")) {
			String realPath = content.substring("external::".length());
			String htmlContent = readHtmlAsText(realPath);
			String fixedHtml = fixRelativePaths(htmlContent, realPath);
			newTab = createTab(selected, fixedHtml);
		} else {
			newTab = createTab(selected, content);
		}
		tabPane.getTabs().add(newTab);
		tabPane.getSelectionModel().select(newTab);
	}
	
	private static VBox createSideBar(ListView<String> topicList) {
		Label titleLabel = new Label("\uD83D\uDCD6 Topics");
		titleLabel.setId("topic-title");
		
		VBox sideBar = new VBox(titleLabel, topicList);
		sideBar.setPadding(new Insets(10));
		sideBar.setSpacing(5);
		sideBar.setId("sidebar");
		sideBar.setMinWidth(200);
		sideBar.setMaxWidth(400);
		return sideBar;
	}
	
	private static StackPane createContentWrapper() {
		StackPane contentWrapper = new StackPane(tabPane, emptyMessagePane);
		emptyMessagePane.setVisible(tabPane.getTabs().isEmpty());
		return contentWrapper;
	}
	
	private static SplitPane createMainContent(Stage stage, VBox sideBar, StackPane contentWrapper) {
		SplitPane mainContent = new SplitPane();
		mainContent.setId("main-split-pane");
		mainContent.getItems().addAll(sideBar, contentWrapper);
		mainContent.setDividerPositions(0.25);
		mainContent.getDividers().getFirst().positionProperty().addListener((_, _, newVal) -> {
			double minPos = 200.0 / stage.getWidth();
			double maxPos = 400.0 / stage.getWidth();
			if (newVal.doubleValue() < minPos) mainContent.setDividerPositions(minPos);
			else if (newVal.doubleValue() > maxPos) mainContent.setDividerPositions(maxPos);
		});
		return mainContent;
	}
	
	private static Scene createScene(SplitPane mainContent) {
		Scene scene = new Scene(mainContent, 1050, 650);
		isDarkTheme = isDarkMode;
		String cssFile = isDarkMode ? "browser-dark.css" : "browser-light.css";
		scene.getStylesheets().add(Objects.requireNonNull(MiniDocBrowser.class.getResource(CSS_FOLDER_PATH + cssFile)).toExternalForm());
		return scene;
	}
	
	private static void configureStage(Stage stage, Scene scene) {
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setOnCloseRequest(_ -> isDisplayed = false);
		stage.setTitle("Battorion Internal Browser");
		stage.getIcons().add(
				new Image(Objects.requireNonNull(MiniDocBrowser.class.getResource(
						ASSETS_FOLDER_PATH + "guide.png"
				)).toExternalForm())
		);
	}
	
	public static void main_browser(String[] args) {
		if (isDisplayed) {
			return;
		} if (TOPICS.isEmpty()) {
			buildTopicsMap();
		} if (!isMiniBrowserLaunched) {
			launch(args);
		} else {
			if (isDarkTheme == isDarkMode) {
				Platform.runLater(() -> {
					if (primaryStage != null) {
						primaryStage.show();
					}
				});
			} else if(!isStageCreated) {
				Platform.runLater(() -> createPrimaryStage(new Stage()));
			} else {
				reloadUI();
			}
		}
	}
	
	private static void reloadUI() {
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			if (primaryStage != null) {
				MiniDocBrowser browser = new MiniDocBrowser();
				try {
					browser.start(primaryStage);
				} catch (Exception e) {
					logger.severe("[EXCEPTION]: " + e.getMessage());
				}
			}
		});
	}
	
	public static void launchAndOpenTopic(@NotNull String topicTitle, int scroll) {
		if (isDisplayed) {
			return;
		} if (TOPICS.isEmpty()) {
			buildTopicsMap();
		} if(!isStageCreated && isMiniBrowserLaunched) {
			Platform.runLater(() -> createPrimaryStage(new Stage()));
		} if (!isMiniBrowserLaunched) {
			new Thread(() -> Application.launch(MiniDocBrowser.class)).start();
			while (waitingForStart) {
				try {
					latch.await();
				} catch (InterruptedException e) {
					logger.severe("[EXCEPTION]: " + e.getMessage());
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
		
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			primaryStage.show();
			tabPane.getTabs().clear();
			Supplier<String> supplier = TOPICS.get(topicTitle);
			if (supplier == null) {
				logger.severe("[EXCEPTION]: Topic not found: " + topicTitle);
				return;
			}
			
			String content = supplier.get();
			Tab newTab;
			if (content.startsWith("external::")) {
				String realPath = content.substring("external::".length());
				String htmlContent = readHtmlAsText(realPath);
				String fixedHtml = fixRelativePaths(htmlContent, realPath);
				newTab = createTab(topicTitle, fixedHtml);
			} else {
				newTab = createTab(topicTitle, content);
			}
			tabPane.getTabs().add(newTab);
			tabPane.getSelectionModel().select(newTab);
			
			BorderPane wrapper = (BorderPane) newTab.getContent();
			WebView webView = (WebView) wrapper.getCenter();
			WebEngine engine = webView.getEngine();
			if (engine.getDocument() != null) {
				engine.executeScript("window.scrollTo(0, " + scroll + ");");
			} else {
				engine.documentProperty().addListener((_, _, newDoc) -> {
					if (newDoc != null) {
						engine.executeScript("window.scrollTo(0, " + scroll + ");");
					}
				});
			}
		});
	}
}