package com.battery_level_alarm.monitoring.tray_manager.tray_executors.tray_related;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;

import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.ASSETS_FOLDER_PATH;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.Paths.SUPPORT_VIDEOS_PATH;

public class TrayUsageTutorial {
	private static final LinkedHashMap<String, String> VIDEO_SEQUENCE = new LinkedHashMap<>();
	private static final HashMap<String, String> VIDEO_DESCRIPTIONS = new HashMap<>();
	private static final ArrayList<MediaPlayer> MEDIA_PLAYERS = new ArrayList<>();
	private static Label descriptionLabel;
	private static int currentIndex = 0;
	
	static {
		// Sort by entry
		VIDEO_SEQUENCE.put("tray_move_icon.mp4", SUPPORT_VIDEOS_PATH + "tray_move_icon.mp4");
		VIDEO_SEQUENCE.put("tray_how_to_use.mp4", SUPPORT_VIDEOS_PATH + "tray_how_to_use.mp4");
		
		VIDEO_DESCRIPTIONS.put("tray_move_icon.mp4",
				"""
				This video demonstrates how to move the system tray icon to a new position on the taskbar.
				It also explains the difference between hidden and visible tray icons,
				and how to ensure the icon is always accessible by adjusting your operating system settings.
				""");
		VIDEO_DESCRIPTIONS.put("tray_how_to_use.mp4",
				"""
				This video provides a quick tutorial on how to use the system tray features of the application,
				including minimizing to tray, accessing settings from the tray icon,
				and restoring the main window with a single click.
				""");
	}
	
	public static void showTrayUsageTutorial(Stage primaryStage) {
		for (String videoName : VIDEO_SEQUENCE.keySet()) {
			String path = VIDEO_SEQUENCE.get(videoName);
			MediaPlayer mp = new MediaPlayer(new Media(
					Objects.requireNonNull(TrayUsageTutorial.class.getResource(path)).toExternalForm()));
			MEDIA_PLAYERS.add(mp);
		}
		
		MediaView mediaView = createMediaView(MEDIA_PLAYERS.getFirst());
		VBox rightBox = createVideoSection(mediaView);
		VBox descriptionBox = createDescriptionSection();
		StackPane bottomPane = createBottomPane(primaryStage);
		
		BorderPane root = new BorderPane();
		root.setRight(rightBox);
		root.setLeft(descriptionBox);
		root.setBottom(bottomPane);
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #005c97, #363795);");
		
		Scene scene = new Scene(root, 950, 650);
		scene.setFill(Color.TRANSPARENT);
		Rectangle clip = new Rectangle(950, 650);
		clip.setArcWidth(40);
		clip.setArcHeight(40);
		root.setClip(clip);
		
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.getIcons().add(new Image(
				Objects.requireNonNull(TrayUsageTutorial.class.getResourceAsStream(ASSETS_FOLDER_PATH + "video_guide.png"))));
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setTitle("How to Use the Tray Feature");
		primaryStage.show();
		primaryStage.toFront();
		
		String firstVideoName = new ArrayList<>(VIDEO_SEQUENCE.keySet()).getFirst();
		descriptionLabel.setText(VIDEO_DESCRIPTIONS.get(firstVideoName));
		MEDIA_PLAYERS.getFirst().play();
		MEDIA_PLAYERS.getFirst().setOnEndOfMedia(() -> playNextVideo(mediaView, primaryStage));
	}
	
	private static MediaView createMediaView(MediaPlayer player) {
		MediaView mediaView = new MediaView(player);
		mediaView.setPreserveRatio(true);
		mediaView.setFitWidth(600);
		mediaView.setFitHeight(500);
		return mediaView;
	}
	
	private static VBox createVideoSection(MediaView mediaView) {
		StackPane videoContainer = new StackPane(mediaView);
		videoContainer.setPrefWidth(600);
		videoContainer.setPrefHeight(500);
		videoContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #005c97, #363795); -fx-padding: 10px;");
		
		VBox rightBox = new VBox(videoContainer);
		rightBox.setAlignment(Pos.CENTER);
		rightBox.setSpacing(10);
		rightBox.setStyle("-fx-background-color: linear-gradient(to bottom, #005c97, #363795);");
		return rightBox;
	}
	
	private static VBox createDescriptionSection() {
		ImageView iconView = new ImageView(new Image(
				Objects.requireNonNull(TrayUsageTutorial.class.getResourceAsStream(ASSETS_FOLDER_PATH + "video_guide.png"))));
		iconView.setFitWidth(200);
		iconView.setFitHeight(200);
		StackPane iconWrapper = new StackPane(iconView);
		iconWrapper.setPadding(new Insets(30, 10, 20, 5));
		
		Label titleLabel = new Label("How to Use the Tray Feature");
		titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
		titleLabel.setPadding(new Insets(0, 10, 10, 15));
		
		descriptionLabel = new Label();
		descriptionLabel.setWrapText(true);
		descriptionLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-padding: 5 15 15 15;");
		
		ScrollPane scrollPane = new ScrollPane(descriptionLabel);
		scrollPane.setPrefViewportHeight(250);
		scrollPane.setStyle("""
            -fx-background: transparent;
            -fx-background-color: transparent;
        """);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setFitToWidth(true);
		
		VBox descriptionBox = new VBox(iconWrapper, titleLabel, scrollPane);
		descriptionBox.setPrefWidth(300);
		descriptionBox.setAlignment(Pos.TOP_LEFT);
		descriptionBox.setStyle("-fx-background-color: linear-gradient(to bottom, #005c97, #363795);");
		return descriptionBox;
	}
	
	private static StackPane createBottomPane(Stage stage) {
		Label skipLink = new Label("Skip");
		skipLink.setStyle("-fx-text-fill: #00e5ff; -fx-underline: true; -fx-font-size: 14px; -fx-cursor: hand;");
		skipLink.setPadding(new Insets(10));
		skipLink.setOnMouseClicked(_ -> {
			MEDIA_PLAYERS.forEach(MediaPlayer::stop);
			stage.close();
		});
		
		StackPane bottomPane = new StackPane(skipLink);
		bottomPane.setAlignment(Pos.CENTER);
		bottomPane.setPadding(new Insets(10));
		bottomPane.setStyle("-fx-background-color: linear-gradient(to bottom, #005c97, #363795);");
		return bottomPane;
	}
	
	private static void playNextVideo(MediaView view, Stage stage) {
		currentIndex++;
		if (currentIndex >= MEDIA_PLAYERS.size()) {
			stage.close();
			return;
		}
		
		MediaPlayer nextPlayer = MEDIA_PLAYERS.get(currentIndex);
		view.setMediaPlayer(nextPlayer);
		String videoName = new ArrayList<>(VIDEO_SEQUENCE.keySet()).get(currentIndex);
		descriptionLabel.setText(VIDEO_DESCRIPTIONS.get(videoName));
		
		if (nextPlayer.getStatus() == MediaPlayer.Status.READY) {
			nextPlayer.play();
		} else {
			nextPlayer.setOnReady(nextPlayer::play);
		}
		nextPlayer.setOnEndOfMedia(() -> playNextVideo(view, stage));
	}
}