package com.battery_level_alarm.monitoring.notifications.alerts;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ICONS_FOLDER_PATH;

public class ChargerIcons {
	private static Stage IconStage;
	public static void showCircularImage(String imageName) {
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
	}
	
	public static void hideIconsStage() {
		if(IconStage != null && IconStage.isShowing()) {
			IconStage.hide();
		}
	}
}