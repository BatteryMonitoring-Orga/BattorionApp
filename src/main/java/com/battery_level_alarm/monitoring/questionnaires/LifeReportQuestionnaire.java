package com.battery_level_alarm.monitoring.questionnaires;

public class LifeReportQuestionnaire {
	public static String getLifeReportQuestionnaire() {
		return """
				<html>
				<body style='font-family: Serif, sans-serif; padding: 10px;'>
				<h2 style='color:#003366;'>Battery Report Management Overview</h2>
				<p>This section helps you understand and manage your computer's battery health. It offers easy-to-use buttons to perform key actions, even if you're not familiar with battery terminology.</p>
	
				<ul>
				    <li><b>Open:</b> Opens the latest battery health report (if it exists) in your default browser for review.</li>
				    <br>
				    <li><b>Create/Re-create:</b> Generates a new system battery report. Use this if no report exists, or you want the most recent battery status.</li>
				    <br>
				    <li><b>Reanalyze:</b> Re-processes the current report to extract and refresh battery data shown in the app. Use this after changes or new data is available.</li>
				</ul>
				<p style='color:gray;'>This tool simplifies battery report handling by combining all actions—open, generate, and refresh—into one user-friendly panel.</p>
				<hr/>
				
				<h3 style='color:#003366;'>Battery Information Display Guide</h3>
				<p>The app shows detailed battery statistics to help you understand your device's battery condition. Here’s what each label means:</p>
				<ul>
				    <li><b>Designed Capacity:</b> The amount of energy the battery was originally built to store (in mWh). Higher values mean longer battery life when new.</li>
				    <br>
				    <li><b>Full Charge Capacity:</b> The current maximum energy the battery can hold after wear and use (in mWh).</li>
				    <br>
				    <li><b>Battery Health:</b> Shows how healthy the battery is, as a percentage. For example, 80% means the battery holds 80% of its original charge capacity.</li>
				    <br>
				    <li><b>Battery Name:</b> The name or model of your battery as provided by your device's hardware.</li>
				    <br>
				    <li><b>Chemistry:</b> The type of battery material used, such as Lithium-Ion (Li-ion).</li>
				    <br>
				    <li><b>Charge Level:</b> How full your battery is right now, shown as a percentage (e.g., 60%).</li>
				    <br>
				    <li><b>Charging:</b> Indicates whether your laptop is currently charging (Yes or No).</li>
				    <br>
				    <li><b>Estimated Remaining Time:</b> An estimate of how long the battery will last until it needs to be charged again.</li>
				    <br>
				    <li><b>Current Capacity:</b> The battery’s current energy amount based on the charge level (in mWh).</li>
				    <br>
				    <li><b>Max Capacity:</b> The best energy capacity the battery can reach now when fully charged.</li>
				    <br>
				    <li><b>Battery Wear:</b> Shows how much the battery has degraded. Higher wear means shorter battery life.</li>
				    <br>
				    <li><b>Cycle Count:</b> The number of full charge-discharge cycles the battery has gone through. Batteries degrade after many cycles.</li>
				    <br>
				    <li><b>Power Usage Rate:</b> The speed at which the battery is being used or charged (in mW), along with the direction (charging or discharging).</li>
				    <br>
				    <li><b>Voltage:</b> The current voltage being output by the battery (in volts).</li>
				    <br>
				    <li><b>Amperage:</b> The current flowing in or out of the battery (in milliamps).</li>
				    <br>
				    <li><b>Warning:</b> Shown if the battery seems miscalibrated (for example, if the max capacity is higher than designed capacity).</li>
				</ul>
				</body>
				</html>
				""";
	}
}