package com.battery_level_alarm.monitoring.questionnaires;

public class SettingsQuestionnaire {
	public static String getComprehensiveUserGuideHtml() {
		return """
						<html>
						<body style='font-family: Serif, sans-serif; padding: 10px; color:#222;'>
		
						<h2 style='color:#003366;'>Comprehensive Settings Guide</h2>
		
						<p>Welcome to the official user guide for the Battery Monitoring Application. This guide covers all user interface panels, explaining the purpose of each setting and how to configure it for optimal battery health and user experience.</p>
		
						<hr>
		
						<h3>1. Settings Container Overview</h3>
						<p>The <b>Settings Container</b> is your centralized control hub. It groups all configuration panels into a single interface for easy navigation and management, including battery settings, sound alerts, notifications, themes, tray behavior, and updates.</p>
		
						<hr>
		
						<h3>2. Battery Monitoring Settings</h3>
						<p>This panel controls how the system monitors and reacts to your battery levels.</p>
						<ol>
										<li><b>Minimum Battery Level:</b>
														The battery percentage threshold that triggers a low battery alert.
														<i>Example:</i> Setting this to 20% means the system alerts you when battery reaches 20%.</li>
										<li><b>Maximum Battery Level:</b>
														The upper threshold to avoid overcharging. Alerts will notify you when battery exceeds this level.
														<i>Example:</i> Setting this to 85% prevents overcharge risks.</li>
										<li><b>Repeat Interval Before Risk Phase (seconds):</b>
														Determines how often the system checks the battery status during critical battery phases.
														<i>Example:</i> 30 seconds means the system rechecks and re-alerts every 30 seconds.</li>
										<li><b>Sound Duration (seconds):</b>
														Duration for how long an alert sound plays.
														<i>Example:</i> A 10-second duration ensures you hear the alert long enough to notice it.</li>
										<li><b>Enable Automatic Monitoring:</b>
														Toggles continuous background monitoring on or off.
														When enabled, battery level checks and alerts run automatically.</li>
						</ol>
		
						<hr>
		
						<h3>3. Sound Alert Settings</h3>
						<p>Customize how and when sound alerts are played:</p>
						<ol>
										<li><b>Enable Primary Sound Alerts:</b>
														Plays alert sounds when battery hits critical minimum or maximum levels.</li>
										<li><b>Enable Secondary Sound Alerts:</b>
														Plays periodic reminders before battery reaches critical thresholds to warn you in advance.</li>
										<li><b>Enable Charging/Discharging Sound:</b>
														Plays a sound effect when charger is connected or disconnected.</li>
										<li><b>Set Default Sound:</b>
														Resets sound alerts to the built-in default sound, overriding any custom selections.</li>
										<li><b>Sound File Path:</b>
														Displays the current audio file path used for alerts, allowing you to verify or change it.</li>
										<li><b>Choose Sound File:</b>
														Opens a file chooser to select a custom sound file for alert notifications.</li>
										<li><b>Test Alarm Sound:</b>
														Plays the currently selected sound so you can confirm it works and adjust volume as needed.</li>
						</ol>
		
						<hr>
		
						<h3>4. Notification Settings</h3>
						<p>Configure visual alerts and text notifications:</p>
						<ul>
										<li><b>Enable Text Alerts:</b>
														Show battery status updates and alerts as pop-up text messages on the screen.</li>
										<li><b>Customize Alert Text:</b>
														Modify the content and style of the text alerts for better clarity and personalization.</li>
						</ul>
		
						<hr>
		
						<h3>5. Tray Icon Settings</h3>
						<p>Manage how the application integrates with your system tray (notification area):</p>
						<ul>
									    <li><b>Start with Tray Window:</b>
									          Choose whether the app starts minimized to the tray instead of opening the main window. This behavior can be toggled via a checkbox.
									    </li>
									    <li><b>Tray Icon Preview:</b>
									          View a live preview of the system tray icon using the embedded preview panel. The preview reflects the current theme and settings in real-time.
									    </li>
									    <li><b>About Sections:</b>
									          Access additional information about tray settings and integration by clicking the "About Tray Panel" and "About Tray Integration" labels.
									    </li>
						</ul>
		
						<hr>
		
						<h3>6. Theme and Appearance Settings</h3>
						<p>Personalize the look and feel of the application interface:</p>
						<ol>
										<li><b>User Interface Theme Selection:</b>
														Select from predefined themes including Light, Dark, or Time-Based (auto switches depending on time of day).</li>
										<li><b>Dark Mode Gradient Background Style:</b>
														Choose your preferred gradient style and colors for dark mode backgrounds.</li>
										<li><b>Light Mode Gradient Background Style:</b>
														Choose gradient background styles for light mode.</li>
										<li><b>Apply Custom Gradient Background:</b>
														Enable this to use your own custom gradient colors instead of presets.</li>
										<li><b>Set Gradient Colors:</b>
														Use color pickers to select and save custom start and end colors for your gradient backgrounds.</li>
										<li><b>Preview Gradient:</b>
														Click preview to see how your gradient choices look before saving.</li>
						</ol>
		
						<hr>
		
						<h3>7. Update Settings</h3>
						<p>Ensure your application stays current with new features and fixes:</p>
						<ol>
									    <li><b>Check for Updates Automatically:</b><br>
									        Enable background checking for new versions regularly.
									    </li>
									    <li><b>Download Updates Automatically:</b><br>
									        Allow automatic downloading of new updates without manual intervention.
									    </li>
									    <li><b>Notify Before Installing:</b><br>
									        Receive a prompt before the update is applied.
									    </li>
									    <li><b>Auto-Restart After Update:</b><br>
									        Automatically restart the application after an update finishes.
									    </li>
									    <li><b>Version Information:</b><br>
									        View the current installed version and the latest available version.
									    </li>
									    <li><b>Manual Update Controls:</b>
									        <ul>
									            <li><b>Check for Updates Now:</b> Manually checks if a new version of the application is available.</li>
									            <li><b>View Release Notes:</b> Shows details of what's new in the latest version.</li>
									            <li><b>Download Update:</b> Manually downloads the new version of the application. The button text changes to "Downloading..." while in progress.</li>
									            <li><b>Rollback to Previous Version:</b> Restores the last installed version if issues occur. May require application restart.</li>
									        </ul>
									    </li>
						</ol>
		
						<hr>
		
						<h3>8. Sound Settings Guide (Cross-Platform)</h3>
						<p>Configuring sound settings can vary by operating system. Here's a brief guide:</p>
						<ul>
										<li><b>Windows:</b>
														Open Start Menu → Settings → System → Sound. Or press <kbd>Windows + I</kbd>.
														From there, choose output devices, adjust volume, and set advanced sound options.</li>
										<li><b>macOS:</b>
														Click Apple menu → System Preferences → Sound.
														Use Output tab to select speakers, headphones, or USB audio devices.</li>
										<li><b>Linux (Ubuntu example):</b>
														Open Settings → Sound.
														Adjust output device, volume, and other preferences.</li>
										<li><b>Shortcuts:</b>
														Most platforms provide quick access via sound icon in system tray/menu bar.</li>
						</ul>
		
						<hr>
		
						<h3>Additional Tips for Optimal Use</h3>
						<ul>
										<li>Set alert thresholds according to your battery’s health and usage habits for best results.</li>
										<li>Combine sound and text alerts to ensure you never miss important notifications.</li>
										<li>Use themes that suit your environment and reduce eye strain, especially in low light.</li>
										<li>Regularly update the application to enjoy latest features and security improvements.</li>
										<li>Test alert sounds after customization to confirm correct playback and volume.</li>
						</ul>
		
						<p>Thank you for using the Battery Monitoring Application! We hope this guide helps you configure the software to best fit your needs.</p>
		
						</body>
						</html>
				""";
	}
}
