import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by xFrednet on 27.10.2016.
 */
public class Source {
	
	private String MAIN_PATH;
	private Workbook wb;
	
	public static void main(String[] args) {
		Source s = new Source();
		s.start(args);
	}
	
	private Source() {
			
		File me = new File("dir");
		MAIN_PATH = me.getAbsolutePath().replace("\\dir", "");
		
		System.out.println("Working directory: " + MAIN_PATH);
		
	}
	
	void start(String[] args) {
		List<File> files = new ArrayList<>();
		List<String> names = new ArrayList<>();
		Workbook wb = new HSSFWorkbook();
		
		System.out.println("Please enter the folders to search:");
		System.out.println("Enter \"start\" to search the given folders or to stop the program");
		
		int argIndex = 0;
		
		//selecting the folders/files to scan
		Scanner in = new Scanner(System.in);
		String line;
		boolean isNew;
		do {
			
			if (argIndex < args.length) {
				line = args[argIndex++];
			} else {
				line = in.nextLine();
			}
			
			if (Objects.equals(line, "start")) break;
			
			isNew = true;
			for (int i = 0; i < names.size(); i++) {
				if (line.equals(names.get(i))) {
					System.out.println("--" + line + " was already entered");
					isNew = false;
					break;
				}
			}
			if (!isNew)
				continue;
			
			File f = testFileName(line);
			if (f != null) {
				files.add(f);
				names.add(line);
			}
			
			
			
		} while(true);
		
		if (files.size() == 0) {
			System.out.println("No Valid files where entered. Terminating.");
			System.exit(0);
		}
		
		//counting the files
		System.out.println();
		FileStat totalStat = new FileStat();
		Sheet firstSheet = wb.createSheet("Total");
		Sheet sheet;
		for (int i = 0; i < files.size(); i++) {
			sheet = wb.createSheet(getShortPath(files.get(i)).replace('\\', '_'));
			totalStat.add(getFileStatistic(files.get(i), sheet, 0, 0));
		}
		totalStat.addToSheet(firstSheet, 0, 0, "Total", ":)");
		System.out.println();
		
		try {
			FileOutputStream fileOut = new FileOutputStream("results.xlsx");
			wb.write(fileOut);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private FileStat getFileStatistic(File file, Sheet sheet, int x, int y) {
		FileStat stat = new FileStat();
		
		if (!file.exists() || file.isHidden() || !file.canRead()) {
			System.out.println("--Unable to Test File: " + getShortPath(file));
			return stat;
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			FileStat childStat;
			
			for (int i = 0; i < files.length; i++) {
				childStat = getFileStatistic(files[i], sheet, x, y);
				y++;
				stat.add(childStat);
			}
			
			System.out.println("--Tested Directory:    " + getShortPath(file));
			stat.addToSheet(sheet, x, y, "Directory", getShortPath(file));
			return stat;
		} else {
			try {
				Scanner fIn = new Scanner(file);
				
				while (fIn.hasNextLine()) {
					stat.analyze(fIn.nextLine());
				}
				
				fIn.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("--Unable to open File: " + getShortPath(file));
				return stat;
			}
			
			System.out.println("--Tested File:         " + getShortPath(file) + " counted ");
			stat.addToSheet(sheet, x, y, "File", getShortPath(file));
			return stat;
		}
		
	}
	
	private File testFileName(String s) {
		
		File f;
		if (s.indexOf(':') == 2)
			f = new File(s);
		else
			f = new File(MAIN_PATH + "\\" + s);
		
		if (f.exists()) {
			if (f.isDirectory()) {
				System.out.println("--Selected folder is valid");
				return f;
			} else if (f.isFile() && f.canRead() && !f.isHidden()) {
				System.out.println("--Selected file is valid");
				return f;
			} else {
				System.out.println("--Selected folder is invalid (The given file is no directory)");
			}
		} else {
			System.out.println("--Selected folder is invalid (The given directory dose not exists)");
		}
		
		return null;
	}
	
	private String getShortPath(File file) {
		String s = file.getAbsolutePath();
		if (s.startsWith(MAIN_PATH)) {
			s = s.replace(MAIN_PATH, "..");
		}
		return s;
	}
	
}
