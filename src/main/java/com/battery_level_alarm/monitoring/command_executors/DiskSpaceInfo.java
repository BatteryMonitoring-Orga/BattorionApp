package com.battery_level_alarm.monitoring.command_executors;
import com.battery_level_alarm.monitoring.user_interface.ui_setup.statistics_container.PrepareDiskInfoGUI;

import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.displayProcessTrack;
import static com.battery_level_alarm.monitoring.notifications.messages.DisplayMessages.printErrorMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class DiskSpaceInfo {
	private static boolean isDestroyed = false;
	private static Thread timerThread;
	
	private static String filesNumber = "";
	private static String filesSize = "";
	private static String dirsNumber = "";
	private static String dirsSize = "";
	
	private static String[] files;
	private static String[] dirs;
	public static String[] track;
	
	public static String getFilesNumber() {
		return filesNumber;
	}
	
    public static String getFilesSize() {
		return filesSize;
	}
    
    public static String getDirNumber() {
		return dirsNumber;
	}
    
    public static String getDirSize() {
		return dirsSize;
	}

    private static Process getProcessForCleanTemp() throws IOException {
        String os = CallCommandLine.getOS();

        if (os.contains("win")) {
            String cmdPath = "C:\\Windows\\System32\\cmd.exe";
            return new ProcessBuilder(cmdPath, "/c", "del /q /f /s %TEMP%\\*").start();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            String bashPath = "/bin/bash";
            return new ProcessBuilder(bashPath, "-c", "rm -rf $HOME/.cache/*").start();
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    private static Process getProcessForDiskSpace() throws IOException {
        String os = CallCommandLine.getOS();

        if (os.contains("win")) {
            String cmdPath = "C:\\Windows\\System32\\cmd.exe";
            return new ProcessBuilder(cmdPath, "/c", "dir /s /a %TEMP%").start();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            String bashPath = "/bin/bash";
            return new ProcessBuilder(bashPath, "-c", "du -sh $HOME/.cache").start();
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }
	
    public static void cleanTempFiles() {
        try {
	        if(track == null) track = new String[5];
			else Arrays.fill(track, "");
        	long startTime = System.currentTimeMillis();
            Process process = getProcessForCleanTemp();
            setUnderTracking(process);
			
            try {
                process.waitFor();
	            if (process.exitValue() == 0) {
		            long endTime = System.currentTimeMillis();
		            long elapsedSeconds = (endTime - startTime) / 1000;
					track[0] = "Temporary files cleaned successfully in " + elapsedSeconds + " seconds.";
		            displayProcessTrack("Clean Temp", track, 400, 250);
					return;
	            }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("The thread was interrupted while waiting for the process.", e);
            }
            timerThread.interrupt();
            
            makeADecision();
            long endTime = System.currentTimeMillis();
            printTheResult(startTime, endTime);
        } catch (IOException | RuntimeException e) {
            printErrorMessage(e);
        }
    }
	
	private static void setUnderTracking(Process process) {
        timerThread = Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(5000);
                if (process.isAlive()) {
                    process.destroy();
                    isDestroyed = true;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                printErrorMessage(e);
            }
        });
	}
	
	private static void makeADecision() {
		PrepareDiskInfoGUI.isUnderTracking = false;
        if(isDestroyed) {
        	AlternativeDiskSpace.cleanTempFiles();
        }
	}
	
	private static void printTheResult(long startTime, long endTime) {
		long elapsedSeconds = (endTime - startTime) / 1000;
		track[3] = "Temporary files caused a delay, but the issue is resolved.";
		track[4] = "Process Time: " + elapsedSeconds + " second(s).";
		displayProcessTrack("Clean Temp", track, 500, 300);
	}
	
    public static void DiskSpace() {
        try {
            Process process = getProcessForDiskSpace();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            String totalFilesLine = null;
            String totalDirsLine = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.contains("File(s)")) {
                    totalFilesLine = line;
                }
                if (line.contains("Dir(s)")) {
                    totalDirsLine = line;
                }
            }
            reader.close();
            parseOutput(totalFilesLine, totalDirsLine);
        } catch (IOException e) {
            printErrorMessage(e);
        }
    }
    
    private static void parseOutput(String totalFilesLine, String totalDirsLine) {
    	if (totalFilesLine != null && totalDirsLine != null) {
    		exhumeNumberOfFiles(totalFilesLine, totalDirsLine);
    		exhumeSizeOfFiles();
        } else {
            System.out.println("Unable to retrieve disk space information.");
        }
    }
    
    private static void exhumeNumberOfFiles(String totalFilesLine, String totalDirsLine) {
    	String fileInfo = totalFilesLine.trim();
        String dirInfo = totalDirsLine.trim();
        files = fileInfo.split(" ", 3);
        dirs = dirInfo.split(" ", 3);
        filesNumber = files[0];
        dirsNumber = dirs[0];
    }
    
    private static void exhumeSizeOfFiles() {
    	String fileSize = files[2].trim();
        String dirSize = dirs[2].trim();
        files = fileSize.split(" ", 2);
        dirs = dirSize.split(" ", 2);
        filesSize = parseFileSize(files[0]);
        dirsSize = parseDirSize(dirs[0]);
    }
    
    private static String parseFileSize(String input) {
        try {
            long totalSize = Long.parseLong(input.replaceAll(",", "").trim());
            String formattedSize = formatBytes(totalSize);
            
            return String.format(formattedSize);
        } catch (NumberFormatException e) {
	        printErrorMessage(e);
            return "Unable to parse file information.";
        }
    }
    
    private static String parseDirSize(String input) {
        try {
            long freeSpace = Long.parseLong(input.replaceAll(",", "").trim());
            String formattedFreeSpace = formatBytes(freeSpace);
            
            return String.format(formattedFreeSpace);
        } catch (NumberFormatException e) {
	        printErrorMessage(e);
            return "Unable to parse directory information.";
        }
    }
    
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String[] suffixes = {"bytes", "KB", "MB", "GB", "TB"};
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), suffixes[exp]);
    }
}