package com.battery_level_alarm.monitoring.website;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import static com.battery_level_alarm.monitoring.system_core.Battorion.logger;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.BATTORION_WEBSITE;
import static com.battery_level_alarm.monitoring.system_core.BattorionCoreConstants.AppInfo.BATTORION_WEBSITE_SEND_IMAGE_PAGE;
import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class Website {
	public static void websiteCaller() {
		try {
			if (BATTORION_WEBSITE != null) {
				Desktop.getDesktop().browse(BATTORION_WEBSITE);
			}
		} catch (Exception ex) {
			try {
				StringSelection selection = new StringSelection(BATTORION_WEBSITE.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, null);
				logger.info("Website URL copied to clipboard.");
			} catch (Exception clipboardEx) {
				printErrorMessage(clipboardEx);
			}
		}
	}
	
	public static Pane createFXWebsiteCaller(Pos pos) {
		HBox container = new HBox();
		container.setSpacing(10);
		container.setAlignment(pos);
		
		Hyperlink link = new Hyperlink("Visit Battorion Website");
		link.setCursor(javafx.scene.Cursor.HAND);
		link.setStyle("-fx-text-fill: #2196f3; -fx-underline: true; -fx-font-size: 14px");
		
		boolean canBrowse = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
		if (!canBrowse) {
			logger.warning("Desktop browsing is not supported on this platform.");
			link.setDisable(true);
			link.setText("Website unavailable on this platform");
		} else {
			link.setOnAction(_ -> {
				try {
					if (BATTORION_WEBSITE != null) {
						Desktop.getDesktop().browse(BATTORION_WEBSITE);
					}
				} catch (Exception ex) {
					try {
						Toolkit.getDefaultToolkit()
								.getSystemClipboard()
								.setContents(new java.awt.datatransfer.StringSelection(BATTORION_WEBSITE.toString()), null);
						logger.info("Website URL copied to clipboard.");
					} catch (Exception clipboardEx) {
						printErrorMessage(clipboardEx);
					}
				}
			});
		}
		container.getChildren().add(link);
		return container;
	}
	
	public static Pane createFXWebsiteSendImagePageCaller(Pos pos) {
		HBox container = new HBox();
		container.setAlignment(pos);
		
		Hyperlink link = new Hyperlink("Upload Image via Website");
		link.setCursor(javafx.scene.Cursor.HAND);
		link.setStyle("-fx-text-fill: #2196f3; -fx-underline: true; -fx-font-size: 15px");
		
		boolean canBrowse = Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
		if (!canBrowse) {
			logger.warning("Desktop browsing is not supported on this platform.");
			link.setDisable(true);
			link.setText("Website unavailable on this platform");
		} else {
			link.setOnAction(_ -> {
				try {
					if (BATTORION_WEBSITE_SEND_IMAGE_PAGE != null) {
						Desktop.getDesktop().browse(BATTORION_WEBSITE_SEND_IMAGE_PAGE);
					}
				} catch (Exception ex) {
					try {
						Toolkit.getDefaultToolkit()
								.getSystemClipboard()
								.setContents(new java.awt.datatransfer.StringSelection(BATTORION_WEBSITE_SEND_IMAGE_PAGE.toString()), null);
						logger.info("Website Send Image Page URL copied to clipboard.");
					} catch (Exception clipboardEx) {
						printErrorMessage(clipboardEx);
					}
				}
			});
		}
		container.getChildren().add(link);
		return container;
	}
}