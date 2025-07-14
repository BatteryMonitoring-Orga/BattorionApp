package com.battery_level_alarm.monitoring.command_executors;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.HWDiskStore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import static com.battery_level_alarm.monitoring.visual_effects.messages.DisplayMessages.printErrorMessage;

public class HardwareInfoReader {
	private static final SystemInfo systemInfo = new SystemInfo();
	private static final HardwareAbstractionLayer hal = systemInfo.getHardware();
	
	public static String getOSFullName() {
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "systeminfo | findstr /B /C:\"OS Name\"");
			builder.redirectErrorStream(true);
			Process process = builder.start();
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("CP850")))) {
				String line = reader.readLine();
				if (line != null) {
					String osName = line.split(":", 2)[1].trim();
					osName = osName.replaceAll("[^A-Za-z0-9]", "");
					return osName;
				}
			}
		} catch (Exception e) {
			printErrorMessage(e);
		}
		return "Unknown OS";
	}
	
	public static String getCPU() {
		String cpu = runWMIC("wmic cpu get Name");
		if (isValid(cpu)) {
			return cpu;
		}
		CentralProcessor processor = hal.getProcessor();
		return processor.getProcessorIdentifier().getName();
	}
	
	public static String getHWID() {
		String hwid = runWMIC("wmic csproduct get UUID");
		if (isValid(hwid)) {
			return hwid;
		}
		return hal.getComputerSystem().getHardwareUUID();
	}
	
	public static String getRAM() {
		String ramStr = runWMIC("wmic ComputerSystem get TotalPhysicalMemory");
		if (isValid(ramStr)) {
			try {
				long bytes = Long.parseLong(ramStr.replaceAll("[^0-9]", ""));
				long gb = bytes / (1024 * 1024 * 1024);
				return gb + " GB";
			} catch (NumberFormatException e) {
				printErrorMessage(e);
			}
		}
		GlobalMemory memory = hal.getMemory();
		long gb = memory.getTotal() / (1024 * 1024 * 1024);
		return gb + " GB";
	}
	
	public static String getDisk() {
		String disk = runWMIC("wmic diskdrive get Model");
		if (isValid(disk)) {
			return disk;
		}
		List<HWDiskStore> disks = hal.getDiskStores();
		if (!disks.isEmpty()) {
			return disks.getFirst().getModel();
		}
		return "Unknown Disk";
	}
	
	private static String runWMIC(String command) {
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
			builder.redirectErrorStream(true);
			Process process = builder.start();
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				reader.readLine();
				String result = reader.readLine();
				if (result != null && !result.isBlank()) {
					return result.trim();
				}
			}
		} catch (Exception ignored) {
		}
		return null;
	}
	
	private static boolean isValid(String s) {
		return s != null && !s.isBlank() && !s.toLowerCase().contains("not found");
	}
}
