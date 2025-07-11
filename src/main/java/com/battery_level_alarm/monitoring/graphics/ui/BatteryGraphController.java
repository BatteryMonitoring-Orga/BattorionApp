package com.battery_level_alarm.monitoring.graphics.ui;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.*;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.loadDefaultGraphConfigurations;
import static com.battery_level_alarm.monitoring.file_manager.ConfigurationFilesManager.saveGraphConfigurations;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.reloadGraphUI;
import static com.battery_level_alarm.monitoring.graphics.base.ChartType.*;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocBrowser.launchAndOpenTopic;
import static com.battery_level_alarm.monitoring.mini_browser.MiniDocTopics.GRAPH_QUESTIONNAIRE;
import static com.battery_level_alarm.monitoring.system_core.Battorion.DashboardPanel;
import static com.battery_level_alarm.monitoring.user_interface.ui_setup.settings_container.SettingsContainerClass.refreshGraphSettingsTab;
import static com.battery_level_alarm.monitoring.versions_manager.ReleaseManager.showFXAlert;

import com.battery_level_alarm.monitoring.core_utilities.GraphSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

public class BatteryGraphController {
	private static final int SHARED_WIDTH = 150;
	
	public static BorderPane buildGraphController() {
		java.awt.Color awtColor = DashboardPanel.getBackground();
		String cssColor = toCssColor(awtColor);
		String backgroundStyle = "-fx-background-color: " + cssColor + " !important;";
		ScrollPane appearanceContent = new ScrollPane(createTitledVBox("appearance-section",
				"Appearance Settings", backgroundStyle, true,
				createLabeledCombo("theme-combo", "Painter Theme:", getPainterTheme(), GraphSettings::setPainterTheme,
						Arrays.stream(GRAPH_THEME.values()).map(GRAPH_THEME::getDisplayName).toArray(String[]::new)),
				createColorSection("line-color", "Sketch Color:", getSketchColor(), GraphSettings::setSketchColor),
				createColorSection("bg-color", "Background Color:", getBackgroundColor(), GraphSettings::setBackgroundColor),
				createColorSection("axis-color", "Axis Color:", getAxisColor(), GraphSettings::setAxisColor)
		));
		apparentContentSetup(appearanceContent, backgroundStyle);
		
		ScrollPane chartContent = getChartContent(backgroundStyle);
		VBox timeContent = getTimeContent(backgroundStyle);
		VBox alertContent = getAlertSection(backgroundStyle);
		VBox saveContent = getSaveSection(backgroundStyle);
		
		TabPane tabPane = buildTabPane(backgroundStyle, appearanceContent, chartContent, timeContent, alertContent, saveContent);
		HBox bottomBox = buildBottomBox(backgroundStyle);
		HBox guideBox = buildGuideBox(backgroundStyle);
		
		BorderPane container = new BorderPane();
		container.setTop(guideBox);
		container.setCenter(tabPane);
		container.setBottom(bottomBox);
		container.setId("graph-controller-root");
		container.setStyle(backgroundStyle);
		return container;
	}
	
	private static void apparentContentSetup(ScrollPane appearanceContent, String backgroundStyle) {
		appearanceContent.setFitToWidth(true);
		appearanceContent.setFitToHeight(true);
		appearanceContent.setStyle(backgroundStyle);
		appearanceContent.setId("chart-scroll");
	}
	
	private static @NotNull VBox getTimeContent(String backgroundStyle) {
		CheckBox enableAutoUpdate = new CheckBox("Enable Auto Update");
		enableAutoUpdate.setId("auto-update");
		enableAutoUpdate.setSelected(isAutoUpdate());
		enableAutoUpdate.setOnAction(_ -> setAutoUpdate(enableAutoUpdate.isSelected()));
		
		CheckBox enableZoom = new CheckBox("Enable Zoom");
		enableZoom.setId("enable-zoom");
		enableZoom.setSelected(isZoomEnabled());
		enableZoom.setOnAction(_ -> setZoomEnabled(enableZoom.isSelected()));
		
		Slider zoomMin = buildSlider("zoom-min", 0.5, 1.0, getZoomMin(), 0.05,
				0.1, 4, val -> setZoomMin(val.doubleValue())
		);
		Slider zoomMax = buildSlider("zoom-max", 1.0, 5.0, getZoomMax(), 0.2,
				1.0, 4, val -> setZoomMax(val.doubleValue())
		);
		return createTitledVBox("time-section", "Time & Zoom Settings", backgroundStyle, false,
				enableAutoUpdate, new Separator(), enableZoom,
				createLabeledNode("Min Zoom:", false, zoomMin),
				createLabeledNode("Max Zoom:",false, zoomMax)
		);
	}
	
	private static @NotNull ScrollPane getChartContent(String backgroundStyle) {
		VBox chartTypeBox = getChartTypeBox();
		CheckBox showDataPoints = createCheckBox("show-points", "Show Data Points", isShowDataPoints(), GraphSettings::setShowDataPoints);
		CheckBox showValuesOnHover = createCheckBox("hover-values", "Show Values on Hover", isShowValuesOnHover(), GraphSettings::setShowValuesOnHover);
		CheckBox showGridLines = createCheckBox("show-grid", "Show Grid Lines", isShowGridLines(), GraphSettings::setShowGridLines);
		CheckBox showXAxisLabels = createCheckBox("x-axis-labels", "Show X Axis Labels", isShowXAxisLabels(), GraphSettings::setShowXAxisLabels);
		CheckBox showYAxisLabels = createCheckBox("y-axis-labels", "Show Y Axis Labels", isShowYAxisLabels(), GraphSettings::setShowYAxisLabels);
		
		VBox chartBox = createTitledVBox("chart-section", "Chart Settings", backgroundStyle, false,
				new Label("Chart Type:"), chartTypeBox, new Separator(),
				new Label("Graph Details:"), showDataPoints, showValuesOnHover, showGridLines,
				new Separator(), new Label("Axis Labels:"), showXAxisLabels, showYAxisLabels
		);
		
		ScrollPane scrollPane = new ScrollPane(chartBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setId("chart-scroll");
		scrollPane.setStyle(backgroundStyle);
		return scrollPane;
	}
	
	private static @NotNull VBox getChartTypeBox() {
		ToggleGroup chartGroup = new ToggleGroup();
		RadioButton lineChart = new RadioButton("Line Chart");
		RadioButton areaChart = new RadioButton("Area Chart");
		
		lineChart.setToggleGroup(chartGroup);
		areaChart.setToggleGroup(chartGroup);
		for (Toggle toggle : chartGroup.getToggles()) {
			RadioButton rb = (RadioButton) toggle;
			String name;
			if(getChartType().equalsIgnoreCase(AREA.name())) {
				name = "Area Chart";
			} else {
				name = "Line Chart";
			} if (rb.getText().equals(name)) {
				chartGroup.selectToggle(rb);
				break;
			}
		}
		
		chartGroup.selectedToggleProperty().addListener((_, _, newToggle) -> {
			if (newToggle instanceof RadioButton rb) {
				if (rb.getText().equals("Area Chart")) {
					setChartType(AREA.name());
				} else {
					setChartType(LINE.name());
				}
			}
		});
		
		VBox chartTypeBox = new VBox(5, lineChart, areaChart);
		chartTypeBox.setId("chart-types");
		chartTypeBox.setPadding(new Insets(5));
		return chartTypeBox;
	}
	
	private static VBox getAlertSection(String backgroundStyle) {
		Slider alertThresholdSlider = buildSlider("alert-threshold", 0, 30, getAlertThreshold(),
				1, 5, 4, val -> setAlertThreshold(val.intValue())
		);
		
		ColorPicker alertColorPicker = new ColorPicker();
		alertColorPicker.setId("alert-color");
		alertColorPicker.setValue(getAlertColor());
		alertColorPicker.setOnAction(_ -> setAlertColor(alertColorPicker.getValue()));
		
		return createTitledVBox("alert-section", "Battery Alerts", backgroundStyle, false,
				createLabeledNode("Alert Threshold (%)", true, alertThresholdSlider),
				createLabeledNode("Alert Color", false, alertColorPicker)
		);
	}
	
	private static VBox getSaveSection(String backgroundStyle) {
		ComboBox<String> formatCombo = new ComboBox<>();
		formatCombo.getItems().addAll("CSV", "JSON");
		formatCombo.setId("save-format");
		formatCombo.setValue(getSaveFormat());
		formatCombo.setOnAction(_ -> setSaveFormat(formatCombo.getValue()));
		
		CheckBox autoSaveCheck = new CheckBox("Enable Auto Save");
		autoSaveCheck.setId("auto-save");
		autoSaveCheck.setSelected(isAutoSave());
		autoSaveCheck.setOnAction(_ -> setAutoSave(autoSaveCheck.isSelected()));
		
		Slider saveAfterNumOfRecords = buildSlider("save-threshold", 100, 1000, getSaveAfterNumOfRecords(),
				10, 100, 4, val -> setSaveAfterNumOfRecords(val.intValue())
		);
		
		return createTitledVBox("save-section", "Save & Load Options", backgroundStyle, false,
				createLabeledNode("Auto Save Format:", false, formatCombo),
				autoSaveCheck,
				createLabeledNode("Save After (Records):", true, saveAfterNumOfRecords)
		);
	}
	
	private static TabPane buildTabPane(String style, Node... contents) {
		TabPane tabPane = new TabPane();
		tabPane.setSide(Side.LEFT);
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabPane.setPrefSize(570, 278);
		tabPane.setId("graph-tabs");
		tabPane.setStyle(style);
		
		String[] titles = {"Appearance", "Chart", "Time", "Alerts", "Save/Load"};
		for (int i = 0; i < titles.length; i++)
			tabPane.getTabs().add(new Tab(titles[i], contents[i]));
		
		return tabPane;
	}
	
	private static HBox buildBottomBox(String backgroundStyle) {
		Button resetToDefault = new Button("Restore Defaults");
		resetToDefault.setId("reset-button");
		resetToDefault.setCursor(Cursor.HAND);
		resetToDefault.setMinSize(150, 30);
		resetToDefault.setPrefSize(150, 30);
		resetToDefault.setMaxSize(150, 30);
		resetToDefault.setOnAction(_ -> {
			loadDefaultGraphConfigurations();
			saveGraphConfigurations();
			reloadGraphUI(true);
			refreshGraphSettingsTab();
		});
		
		Button saveChanges = new Button("Save Changes");
		saveChanges.setId("save-button");
		saveChanges.setCursor(Cursor.HAND);
		saveChanges.setMinSize(150, 30);
		saveChanges.setPrefSize(150, 30);
		saveChanges.setMaxSize(150, 30);
		saveChanges.setOnAction(_ -> {
			saveGraphConfigurations();
			showFXAlert("Settings Saved", 330, 80,
					"Your graph configurations have been saved successfully.",
					Collections.emptyMap(), ButtonType.OK);
			reloadGraphUI(true);
		});
		
		HBox bottomBox = new HBox(resetToDefault, saveChanges);
		bottomBox.setAlignment(Pos.CENTER_RIGHT);
		bottomBox.setPadding(new Insets(5));
		bottomBox.setSpacing(8);
		bottomBox.setStyle(backgroundStyle);
		return bottomBox;
	}
	
	private static HBox buildGuideBox(String backgroundStyle) {
		Label guideLabel = new Label("About Graph Settings Panel");
		guideLabel.setId("link-label");
		guideLabel.setOnMouseClicked(_ -> Thread.ofVirtual().start(() -> launchAndOpenTopic(GRAPH_QUESTIONNAIRE, 0)));
		
		HBox guideBox = new HBox(guideLabel);
		guideBox.setAlignment(Pos.CENTER_RIGHT);
		guideBox.setPadding(new Insets(5));
		guideBox.setStyle(backgroundStyle);
		return guideBox;
	}
	
	private static Slider buildSlider(
			String id, double min, double max, double initial, double blockIncrement,
			double majorTickUnit, int minorTickCount, Consumer<Number> listener
	) {
		Slider slider = new Slider(min, max, initial);
		slider.setId(id);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setBlockIncrement(blockIncrement);
		slider.setMajorTickUnit(majorTickUnit);
		slider.setMinorTickCount(minorTickCount);
		slider.valueProperty().addListener((_, _, newVal) -> listener.accept(newVal));
		
		Tooltip tooltip = new Tooltip(String.valueOf((int) initial));
		Tooltip.install(slider, tooltip);
		slider.setOnMouseMoved(_ -> tooltip.setText(String.format("%.0f", slider.getValue())));
		return slider;
	}
	
	private static CheckBox createCheckBox(String id, String label, boolean selected, Consumer<Boolean> consumer) {
		CheckBox checkBox = new CheckBox(label);
		checkBox.setId(id);
		checkBox.setSelected(selected);
		checkBox.setOnAction(_ -> consumer.accept(checkBox.isSelected()));
		return checkBox;
	}
	
	private static HBox createLabeledNode(String label, boolean newLine, Node node) {
		Label lbl = new Label(label);
		lbl.setId(label.toLowerCase().replace(" ", "-") + "-label");
		HBox hBox;
		if(newLine) {
			VBox innerBox = new VBox(5, lbl, node);
			if (node instanceof Slider) {
				VBox.setVgrow(node, Priority.ALWAYS);
				node.maxWidth(Double.MAX_VALUE);
			}
			
			hBox = new HBox(innerBox);
			HBox.setHgrow(innerBox, Priority.ALWAYS);
		} else {
			hBox = new HBox(10, lbl, node);
		}
		
		hBox.setAlignment(Pos.CENTER_LEFT);
		if (node instanceof Slider) {
			HBox.setHgrow(node, Priority.ALWAYS);
			((Slider) node).setMaxWidth(Double.MAX_VALUE);
		}
		return hBox;
	}
	
	private static HBox createLabeledCombo(String comboId, String label, String initialValue,
	                                       Consumer<String> consumer, String... items) {
		Label lbl = new Label(label);
		lbl.setId(comboId + "-label");
		ComboBox<String> comboBox = new ComboBox<>();
		comboBox.getItems().addAll(items);
		comboBox.setId(comboId);
		comboBox.setValue(initialValue);
		comboBox.setOnAction(_ -> consumer.accept(comboBox.getValue()));
		comboBox.setPrefWidth(SHARED_WIDTH);
		return new HBox(10, lbl, comboBox);
	}
	
	private static HBox createColorSection(String pickerId, String label, javafx.scene.paint.Color initialColor,
	                                       Consumer<javafx.scene.paint.Color> consumer) {
		Label lbl = new Label(label);
		lbl.setId(pickerId + "-label");
		ColorPicker picker = new ColorPicker(initialColor);
		picker.setId(pickerId);
		picker.setOnAction(_ -> consumer.accept(picker.getValue()));
		picker.setPrefWidth(SHARED_WIDTH);
		return new HBox(10, lbl, picker);
	}
	
	private static VBox createTitledVBox(String styleId, String title, String style, boolean splitInColumns, Node... nodes) {
		Label titleLabel = new Label(title);
		titleLabel.setFont(Font.font("Arial", 15));
		titleLabel.setId(styleId + "-title");
		
		VBox box = new VBox(10);
		box.setStyle(style);
		box.setPadding(new Insets(12));
		box.setId(styleId);
		box.getChildren().add(titleLabel);
		
		if(splitInColumns) {
			GridPane grid = new GridPane();
			grid.setHgap(15);
			grid.setVgap(10);
			grid.setAlignment(Pos.TOP_LEFT);
			
			int row = 0;
			for (Node node : nodes) {
				if (node instanceof HBox hBox && hBox.getChildren().size() == 2 &&
						hBox.getChildren().get(0) instanceof Label) {
					Node label = hBox.getChildren().get(0);
					Node control = hBox.getChildren().get(1);
					grid.add(label, 0, row);
					grid.add(control, 1, row);
				} else {
					grid.add(node, 0, row, 2, 1);
				}
				row++;
			}
			box.getChildren().add(grid);
		} else {
			box.getChildren().addAll(nodes);
		}
		return box;
	}
	
	public static String toCssColor(java.awt.Color awtColor) {
		return String.format("#%02x%02x%02x", awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
	}
}