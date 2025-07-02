package com.battery_level_alarm.monitoring.questionnaires;

public class ComputerSettingsQuestionnaire {
	public static String getComputerSettingsGuide() {
		return """
					<html>
					<body style='font-family: Serif, sans-serif; padding: 10px;'>
					<h2 style='color:#003366;'>Computer Settings Overview</h2>
					<p>This panel provides a comprehensive interface to control automatic system behaviors and audio configurations.</p>

					<ol>
						<li><b>Auto Wake-Up Settings</b>
							<ul>
								<li><b>Wake the PC every (Minutes):</b><br>
												Define how frequently the system should wake itself automatically (default is every 5 minutes).</li>
							</ul>
						</li>

						<li><b>Audio Output Handling</b>
							<ul>
								<li><b>Exchange Output to Speaker:</b><br>
												When an alert triggers, the system switches audio output to speakers if enabled.</li>
								<li><b>Restore Output to Previous Device:</b><br>
												After an alert, the system will return to the previously used audio device if this option is enabled.</li>
							</ul>
						</li>

						<li><b>Audio Output Device Management</b>
							<ul>
								<li><b>Active Device Display:</b><br>
												Shows the currently active audio output device used by the system.</li>
								<li><b>Select Audio Output Device:</b><br>
												Choose your preferred audio device from a dropdown list of available devices.</li>
								<li><b>Acoustic Output Device Procedure Field:</b><br>
												A text field where you can enter a new device name to manage.</li>
								<li><b>Action Buttons:</b>
									<ul>
										<li><b>Use the selected AO:</b> Copy the current selected device name into the input field.</li>
										<li><b>Add:</b> Add a new audio output device to the configuration list.</li>
										<li><b>Delete:</b> Remove the selected audio device from the list.</li>
										<li><b>Set it as AO:</b> Apply the written device as the current output.</li>
									</ul>
								</li>
								<li><b><a href='openTab-aboutSelectAudioDevice'>How do I select the audio output?</a></b></li>
							</ul>
						</li>
						<br>
						<li><b>Sound Level Configuration</b>
							<ul>
								<li><b>Enable Sound Level Change:</b><br>
												Allows the app to automatically adjust the PC's sound level when needed.</li>
								<li><b>Restore Sound Level after Alert:</b><br>
												Resets sound level to its original state after an alert finishes.</li>
								<li><b>Set PC Volume Level (%):</b><br>
												Define the overall system volume level using a spinner or volume icon popup.</li>
								<li><b>Set Alert Volume Level (%):</b><br>
												Set the volume level specifically used during alert notifications.</li>
								<li><b><a href='openTab-openSystemTrayNotification'>Click here to open 'About Notification'</a></b></li>
							</ul>
						</li>
						<br>
						<li><b>Notification Sounds</b>
							<ul>
								<li><b>System Notification Sounds:</b><br>
												Choose a sound file to play during battery alerts or wake-up triggers.</li>
								<li><b><a href='openTab-openNotificationSound'>Click here to open 'About Notification Sounds'</a></b></li>
							</ul>
						</li>
					</ol>

					<p style='color:gray;'>All changes are saved automatically to ensure consistency across system restarts.</p>
					</body>
					</html>
				""";
	}
}
