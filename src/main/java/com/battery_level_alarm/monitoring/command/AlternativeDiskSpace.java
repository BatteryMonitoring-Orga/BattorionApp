package com.battery_level_alarm.monitoring.command;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import javax.swing.JOptionPane;
import java.util.concurrent.atomic.AtomicLong;

public class AlternativeDiskSpace {
    private static String filesNumber = "";
    private static String filesSize = "";
    private static String dirsNumber = "";
    private static String dirsSize = "";

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
    
    public static void cleanTempFiles() {
        String tempDir = System.getenv("TEMP");
        if (tempDir == null) {
            JOptionPane.showMessageDialog(null, "TEMP directory not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Path tempPath = Paths.get(tempDir);
            AtomicLong deletedFiles = new AtomicLong();
            AtomicLong failedFiles = new AtomicLong();
            
            Files.walkFileTree(tempPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        Files.delete(file);
                        deletedFiles.incrementAndGet();
                    } catch (IOException e) {
                        failedFiles.incrementAndGet();
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    try {
                        Files.delete(dir);
                    } catch (IOException e) {
                    	failedFiles.incrementAndGet();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            
            JOptionPane.showMessageDialog(null, 
                "Temporary files cleaned successfully.\nFiles deleted: " + deletedFiles.get() + 
                "\nFiles failed to delete: " + failedFiles.get(), 
                "Clean Temp", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred during the cleaning process.\n" + e.getMessage(), "Clean Temp", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void DiskSpace() {
        String tempDir = System.getenv("TEMP");
        if (tempDir == null) {
            System.out.println("TEMP directory not found.");
            return;
        }
        
        try {
            Path tempPath = Paths.get(tempDir);
            AtomicLong totalSize = new AtomicLong();
            AtomicLong totalFiles = new AtomicLong();
            
            Files.walkFileTree(tempPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    totalSize.addAndGet(attrs.size());
                    totalFiles.incrementAndGet();
                    return FileVisitResult.CONTINUE;
                }
            });
            
            filesNumber = String.valueOf(totalFiles.get());
            filesSize = formatBytes(totalSize.get());
            dirsNumber = "N/A";
            dirsSize = "N/A";
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error: " + e.getClass().getName() + "\nMessage: " + e.getMessage(),
                    "Battery Level Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String[] suffixes = {"bytes", "KB", "MB", "GB", "TB"};
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), suffixes[exp]);
    }
}