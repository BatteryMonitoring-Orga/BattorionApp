package com.battery_level_alarm.monitoring.tray_manager.modern_component;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

/**
 * A lightweight custom Tooltip with a fast appearance for JavaFX components.
 */

/*
Button myButton = new Button("Hover me!");
FastTooltip.attach(myButton, "Battery is OK", Duration.millis(150));
 */
public class FastTooltip {
	public static void attach(Node node, String message, Duration delay) {
		Popup popup = new Popup();
		Text text = new Text(message);
		text.setStyle("-fx-background-color: lightyellow; -fx-padding: 5px; -fx-border-color: gray; -fx-border-width: 1px;");
		popup.getContent().add(text);
		popup.setAutoHide(true);
		popup.setHideOnEscape(true);
		
		PauseTransition showDelay = new PauseTransition(delay);
		
		node.addEventHandler(MouseEvent.MOUSE_ENTERED, _ -> {
			showDelay.setOnFinished(_ -> {
				Point2D point = node.localToScreen(node.getBoundsInLocal().getMaxX() / 2, node.getBoundsInLocal().getMaxY());
				popup.show(node, point.getX(), point.getY());
			});
			showDelay.playFromStart();
		});
		
		node.addEventHandler(MouseEvent.MOUSE_EXITED, _ -> {
			showDelay.stop();
			popup.hide();
		});
	}
}