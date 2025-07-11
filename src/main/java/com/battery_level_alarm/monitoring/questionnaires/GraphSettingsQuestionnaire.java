package com.battery_level_alarm.monitoring.questionnaires;

public class GraphSettingsQuestionnaire {
	public static String getGraphSettingsQuestionnaire() {
		return """
				<html>
				<body style='font-family: Serif, sans-serif; padding: 10px;'>
				<h2 style='color:#003366;'>Battery Graph Settings Overview</h2>
				<p>This interface enables users to customize how battery data is displayed, monitored, and saved within the application. It is organized into several key sections:</p>

				<ol>
					<li><b>Appearance Settings</b>
						<ul>
							<li><b>Theme:</b><br>
											Choose from a variety of visual themes for the graph and interface appearance.</li>
							<li><b>Line Color:</b><br>
											Customize the color of the battery level line in the graph.</li>
							<li><b>Background Color:</b><br>
											Set the background color of the graph area.</li>
							<li><b>Axis Color:</b><br>
											Select a custom color for the X and Y axis lines.</li>
						</ul>
					</li>
					<br>
					<li><b>Chart Settings</b>
						<ul>
							<li><b>Chart Type:</b><br>
											Switch between Line Chart, or Area Chart for battery data visualization.</li>
							<li><b>Graph Details:</b>
								<ul>
									<li><b>Show Data Points:</b> Display dots at each data entry.</li>
									<li><b>Show Values on Hover:</b> Show tooltip values when hovering over the graph.</li>
									<li><b>Show Grid Lines:</b> Toggle the visibility of background grid lines.</li>
								</ul>
							</li>
							<li><b>Axis Labels:</b>
								<ul>
									<li><b>Show X Axis Labels:</b> Display labels along the horizontal axis.</li>
									<li><b>Show Y Axis Labels:</b> Display labels along the vertical axis.</li>
								</ul>
							</li>
						</ul>
					</li>
					<br>
					<li><b>Time & Zoom Settings</b>
						<ul>
							<li><b>Enable Auto Update:</b><br>
											Automatically refreshes the graph to show new incoming battery data.</li>
							<li><b>Enable Zoom:</b><br>
											Allows dynamic zooming into specific graph regions.</li>
							<li><b>Min Zoom / Max Zoom:</b><br>
											Adjust the minimum and maximum zoom levels using sliders.</li>
						</ul>
					</li>
					<br>
					<li><b>Battery Alerts</b>
						<ul>
							<li><b>Alert Threshold (%):</b><br>
											Set the battery percentage that triggers a visual alert.</li>
							<li><b>Alert Color:</b><br>
											Choose the color of the alert indicator displayed on the graph.</li>
						</ul>
					</li>
					<br>
					<li><b>Save & Load Options</b>
						<ul>
							<li><b>Default Save Format:</b><br>
											Choose the format (CSV or JSON) used when saving graph data.</li>
							<li><b>Enable Auto Save:</b><br>
											Automatically saves recorded battery data without manual intervention.</li>
							<li><b>Save After (Records):</b><br>
				       	                    Define the number of battery data records to collect before triggering an automatic save.</li>
						</ul>
					</li>
				</ol>
				<p style='color:gray;'>Click <b>'Save Changes'</b> to apply all modified settings. Preferences are retained for future sessions.</p>
				</body>
				</html>
				""";
	}
}