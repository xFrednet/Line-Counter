import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by xFrednet on 27.10.2016.
 */
public class Source {
	
	private String MAIN_PATH;
	
	public static void main(String[] args) {
		Source s = new Source();
		s.start(args);
	}
	
	private Source() {

			File f = new File(System.getProperty("java.class.path"));
			File dir = f.getAbsoluteFile().getParentFile();
			MAIN_PATH = dir.toString();
		
			System.out.println("Working directory: " + MAIN_PATH);
		
	}
	
	void start(String[] args) {
		List<File> files = new ArrayList<>();
		List<String> names = new ArrayList<>();
		
		System.out.println("Please enter the folders to search:");
		System.out.println("Enter \"start\" to search the given folders or to stop the program");
		
		int argIndex = 0;
		
		Scanner in = new Scanner(System.in);
		String line;
		do {
			
			if (argIndex < args.length) {
				line = args[argIndex++];
			} else {
				line = in.nextLine();
			}
			
			if (Objects.equals(line, "start")) break;
			
			for (int i = 0; i < names.size(); i++) {
				if (line.equals(names.get(i))) {
					System.out.println("--" + line + " was already entered");
					continue;
				}
			}
			names.add(line);
			
			File f = testFolderName(line);
			
			if (f != null) 
				files.add(f);
			
		} while(true);
		
		if (files.size() == 0) {
			System.out.println("No Valid files where entered. Terminating.");
			System.exit(0);
		}
		
		System.out.println();
		int lineCount = 0;
		for (int i = 0; i < files.size(); i++) {
			lineCount += getLineCount(files.get(i));
		}
		System.out.println();
		
		System.out.println("Counted " + lineCount + " Lines");
	}
	
	private int getLineCount(File file) {
		int lineCount = 0;
		
		if (!file.exists() || file.isHidden() || !file.canRead()) {
			System.out.println("--Unable to Test File: " + getShortPath(file));
			return 0;
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			
			for (int i = 0; i < files.length; i++) {
				lineCount += getLineCount(files[i]);
			}
			
			System.out.println("--Tested Directory:    " + getShortPath(file) + " counted " + lineCount + " Lines");
			return lineCount;
		} else {
			try {
				Scanner fIn = new Scanner(file);
				
				while (fIn.hasNext()) {
					fIn.next();
					lineCount++;
				}
				
				fIn.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("--Unable to open File: " + getShortPath(file));
				return 0;
			}
			
			System.out.println("--Tested File:         " + getShortPath(file) + " counted " + lineCount + " Lines");
			return lineCount;
		}
		
	}
	
	private File testFolderName(String s) {
		File f = new File(MAIN_PATH + "\\" + s);
		
		if (f.exists()) {
			if (f.isDirectory()) {
				System.out.println("--Selected folder is valid");
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
