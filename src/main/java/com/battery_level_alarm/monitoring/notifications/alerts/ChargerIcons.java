package com.battery_level_alarm.monitoring.notifications.alerts;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ICONS_FOLDER_PATH;

public class ChargerIcons {
	private static final ScheduledExecutorService CLOSER = Executors.newSingleThreadScheduledExecutor();
	private static Stage IconStage;
	private static boolean isShowing = false;
	
	public static void showCircularImage(String imageName) {
		if(isShowing) hideIconsStage();
		IconStage = new Stage();
		IconStage.initStyle(StageStyle.TRANSPARENT);
		IconStage.getIcons().add(new Image(Objects.requireNonNull(ChargerIcons.class.getResourceAsStream(ICONS_FOLDER_PATH + imageName))));
		
		Image image = new Image(Objects.requireNonNull(ChargerIcons.class.getResource(ICONS_FOLDER_PATH + imageName)).toExternalForm());
		ImageView imageView = new ImageView(image);
		
		double radius = Math.min(image.getWidth(), image.getHeight()) / 2;
		Circle clip = new Circle(radius, radius, radius);
		imageView.setClip(clip);
		
		StackPane root = new StackPane(imageView);
		root.setStyle("-fx-background-color: transparent;");
		Scene scene = new Scene(root, image.getWidth(), image.getHeight());
		scene.setFill(null);
		IconStage.setScene(scene);
		IconStage.setAlwaysOnTop(true);
		IconStage.show();
		IconStage.toFront();
		isShowing = true;
		closeIconStage();
	}
	
	public static void hideIconsStage() {
		if(IconStage != null && IconStage.isShowing()) {
			IconStage.hide();
			IconStage.close();
			isShowing = false;
		}
	}
	
	private static void closeIconStage() {
		CLOSER.schedule(() -> Platform.runLater(() -> {
			if (IconStage != null && IconStage.isShowing()) {
				IconStage.close();
				isShowing = false;
			}
		}), 3, TimeUnit.SECONDS);
	}
}