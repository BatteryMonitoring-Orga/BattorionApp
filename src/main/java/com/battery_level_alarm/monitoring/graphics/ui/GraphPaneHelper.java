package com.battery_level_alarm.monitoring.graphics.ui;
import static com.battery_level_alarm.monitoring.core_utilities.GraphSettings.*;
import static com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph.*;
import static com.battery_level_alarm.monitoring.graphics.ui.GraphButtonsHelper.createButtonsPanel;
import static com.battery_level_alarm.monitoring.graphics.ui.GraphButtonsHelper.createLoadGraphPanel;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Dimensions.*;
import static com.battery_level_alarm.monitoring.visual_effects.ColorUtils.toCssCompatibleColor;

import com.battery_level_alarm.monitoring.core_utilities.GraphSettings;
import com.battery_level_alarm.monitoring.graphics.base.BatteryLevelGraph;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.Objects;

public class GraphPaneHelper {
    public static Scene createMainScene(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createStaticGraphicTab(primaryStage));
        borderPane.setTop(createButtonsPanel(borderPane));
        borderPane.setBottom(createBottomPanel());

        Scene scene = new Scene(borderPane, FRAME_WIDTH, FRAME_HEIGHT);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        BatteryLevelGraph.class.getResource(
                                GraphSettings.GRAPH_THEME.fromDisplayName(getPainterTheme()).getCssPath())
                ).toExternalForm()
        );
        return scene;
    }

    static TabPane createStaticGraphicTab(Stage primaryStage){
        TabPane tabPane = new TabPane();
        tabPane.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                newScene.getRoot().lookupAll(".tab").forEach(tabHeader -> {
                    tabHeader.setOnMouseEntered(_ -> tabHeader.setCursor(Cursor.HAND));
                    tabHeader.setOnMouseExited(_ -> tabHeader.setCursor(Cursor.DEFAULT));
                });
            }
        });

        Tab currentGraphTab = createTab(
                "Current Situation Graph", mainGraphScrolls[0]);
        Tab loadGraphTab = createTab(
                "Load Saved Graph", createLoadGraphPanel(primaryStage));
        tabPane.getTabs().addAll(currentGraphTab, loadGraphTab);
        return tabPane;
    }

    private static Tab createTab(String title, Node content){
        Tab tab = new Tab(title, content);
        tab.setClosable(false);
        return tab;
    }
    
    static HBox createBottomPanel() {
        HBox bottomPanel = new HBox();
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER);
        
        batteryStatusLabel = new Label("Battery Status: Normal");
        batteryStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-family: Serif; -fx-font-style: italic; -fx-text-fill: " + toCssCompatibleColor(getAxisColor()) + ";");
        bottomPanel.getChildren().add(batteryStatusLabel);
        return bottomPanel;
    }
}