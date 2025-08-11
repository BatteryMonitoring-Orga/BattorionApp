package com.battery_level_alarm.monitoring.questionnaires;

public class LifeReportQuestionnaire {
	public static String getLifeReportQuestionnaire() {
		return """
				<html>
					<head>
						<meta charset="UTF-8">
						<title>Battery & Hardware Information Guide</title>
					</head>
					<body style="font-family: Arial, sans-serif; padding: 20px; line-height: 1.6; color: #333; background-color: #f9f9f9;">
						<h1 style="color:#003366; border-bottom: 2px solid #ccc; padding-bottom: 5px;">
							Device Battery & Hardware Monitoring Guide
						</h1>
						<section>
							<h2 style="color:#004080;">Battery Report Management Overview</h2>
							<p>This section helps you understand and manage your computer's battery health. It offers easy-to-use buttons to perform key actions, even if you're not familiar with battery terminology.</p>
							<ul>
								<li><b>Open:</b> Opens the latest battery health report (if it exists) in your default browser for review.</li>
								<li><b>Create/Re-create:</b> Generates a new system battery report. Use this if no report exists, or you want the most recent battery status.</li>
								<li><b>Reanalyze:</b> Re-processes the current report to extract and refresh battery data shown in the app. Use this after changes or new data is available.</li>
							</ul>
							<p style="color:gray;">This tool simplifies battery report handling by combining all actions—open, generate, and refresh—into one user-friendly panel.</p>
						</section>
						<hr/>
						<section>
							<h2 style="color:#004080;">Battery Information</h2>
							<p>The following metrics provide insights into your battery’s current state and long-term health:</p>
							<ul>
								<li><b>Designed Capacity:</b> Original energy storage capacity when the battery was new (in mWh).</li>
								<li><b>Full Charge Capacity:</b> Maximum energy the battery can hold now (in mWh).</li>
								<li><b>Battery Health:</b> Percentage of original capacity still available (e.g., 80%).</li>
								<li><b>Battery Name:</b> Manufacturer/model of the battery.</li>
								<li><b>Chemistry:</b> Battery material type (e.g., Li-ion).</li>
								<li><b>Charge Level:</b> Current battery percentage (e.g., 60%).</li>
								<li><b>Charging:</b> Indicates whether the battery is currently charging.</li>
								<li><b>Estimated Remaining Time:</b> Time left before the battery runs out.</li>
								<li><b>Current Capacity:</b> Current charge in mWh.</li>
								<li><b>Max Capacity:</b> Best possible charge capacity now.</li>
								<li><b>Battery Wear:</b> Amount of degradation the battery has suffered.</li>
								<li><b>Cycle Count:</b> Number of full charge/discharge cycles completed.</li>
								<li><b>Power Usage Rate:</b> Battery usage or charging rate in mW.</li>
								<li><b>Voltage:</b> Current output voltage in volts.</li>
								<li><b>Amperage:</b> Current flow in milliamps.</li>
							</ul>
						</section>
						<hr/>
						<section>
							<h2 style="color:#004080;">Hardware Monitoring Metrics</h2>
							<p>These metrics are collected from system sensors to monitor temperature and power usage:</p>
							<ul>
								<li><b>CPU Package Temperature:</b> Overall CPU chip temperature.</li>
								<li><b>CPU Core #X Temperature:</b> Temperature of individual CPU cores.</li>
								<li><b>Distance to TjMax:</b> Temperature difference from the CPU's max safe limit.</li>
								<li><b>GPU Core Temperature:</b> Current temperature of the GPU.</li>
								<li><b>GPU Hot Spot Temperature:</b> Hottest measured point on the GPU.</li>
								<li><b>Power - GPU:</b> GPU power consumption in watts.</li>
								<li><b>Power - Discharge Rate:</b> Battery drain rate when on battery.</li>
								<li><b>Power - Charge Rate:</b> Battery charge rate when charging.</li>
								<li><b>Degradation:</b> Loss of battery’s original capacity over time.</li>
								<li><b>Available Spare:</b> Remaining spare capacity before battery replacement is needed.</li>
							</ul>
						</section>
					</body>
				</html>
				""";
	}
}