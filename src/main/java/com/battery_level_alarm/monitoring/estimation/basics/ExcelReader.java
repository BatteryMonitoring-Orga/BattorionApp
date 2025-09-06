package com.battery_level_alarm.monitoring.estimation.basics;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ExcelReader {
	public static void readLatestExcelFiles(String path, BiConsumer<Long, Integer> sampleConsumer) throws IOException {
		File fileOrFolder = new File(path);
		if (!fileOrFolder.exists()) {
			System.out.println("❌ Path does not exist: " + path);
			return;
		} if (fileOrFolder.isFile()) {
			readSingleFile(fileOrFolder, sampleConsumer);
			return;
		} if (!fileOrFolder.isDirectory()) {
			System.out.println("❌ Path is not a folder: " + path);
			return;
		}
		
		File[] files = fileOrFolder.listFiles();
		if (files == null || files.length == 0) {
			System.out.println("⚠ Folder is empty: " + path);
			return;
		}
		
		List<File> latestFiles = Arrays.stream(files)
				.filter(ExcelReader::isSupportedFile)
				.sorted(Comparator.comparingLong(File::lastModified).reversed())
				.limit(5)
				.toList();
		if (latestFiles.isEmpty()) {
			System.out.println("⚠ No supported files (.csv/.xls/.xlsx) found in: " + path);
			return;
		}
		for (File file : latestFiles) {
			readSingleFile(file, sampleConsumer);
		}
	}
	
	private static void readSingleFile(File file, BiConsumer<Long, Integer> sampleConsumer) throws IOException {
		String name = file.getName().toLowerCase();
		if (name.endsWith(".csv")) {
			readCsvFile(file, sampleConsumer);
		} else if (name.endsWith(".xlsx") || name.endsWith(".xls")) {
			readExcelFile(file, sampleConsumer);
		} else {
			System.out.println("⚠ Unsupported file type: " + file.getAbsolutePath());
		}
	}
	
	private static boolean isSupportedFile(File file) {
		if (!file.isFile()) return false;
		String n = file.getName().toLowerCase();
		return n.endsWith(".csv") || n.endsWith(".xlsx") || n.endsWith(".xls");
	}
	
	private static void readExcelFile(File file, BiConsumer<Long, Integer> sampleConsumer) throws IOException {
		try (FileInputStream fis = new FileInputStream(file);
		     Workbook workbook = file.getName().toLowerCase().endsWith(".xlsx")
				     ? new XSSFWorkbook(fis)
				     : new HSSFWorkbook(fis)) {
			
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue;
				Cell tsCell = row.getCell(0);
				Cell pctCell = row.getCell(1);
				if (tsCell == null || pctCell == null) continue;
				
				try {
					long timestamp = (long) (tsCell.getNumericCellValue() * 3600_000);
					int percent = (int) pctCell.getNumericCellValue();
					sampleConsumer.accept(timestamp, percent);
				} catch (Exception ignored) {}
			}
		}
	}
	
	private static void readCsvFile(File file, BiConsumer<Long, Integer> sampleConsumer) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first) { first = false; continue; }
				String[] parts = line.split(",");
				if (parts.length >= 2) {
					try {
						long timestamp = (long) (Double.parseDouble(parts[0].trim()) * 3600_000);
						int percent = Integer.parseInt(parts[1].trim());
						sampleConsumer.accept(timestamp, percent);
					} catch (NumberFormatException ignored) {}
				}
			}
		}
	}
}
