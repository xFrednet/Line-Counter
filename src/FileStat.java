import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Created by xFrednet on 31.08.2017.
 */
public class FileStat {
	
	private static final char VALID_CHARS[] = {' ', '\t', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~'};
	private static final int CHAR_COUNT = VALID_CHARS.length;
	
	public int codeLines;
	public int commentLines;
	public int emptyLines;
	public int charCount[] = new int[CHAR_COUNT];
	
	FileStat() {
		//I think that this isn't necessary but I'm not sure.... I've programmed too much in C++ 
		codeLines = 0;
		commentLines = 0;
		emptyLines = 0;
		for (int i = 0; i < CHAR_COUNT; i++)
			charCount[i] = 0;
	}
	
	void analyze(String line) {
		if (line.trim().length() == 0) {
			emptyLines++;
			return;
		} else if (line.trim().startsWith("//")) {
			commentLines++;
		} else {
			codeLines++;
		}
		
		for (int i = 0; i < line.length(); i++) {
			addChar(line.charAt(i));
		}
	}
	void addChar(char c) {
		for (int i = 0; i < CHAR_COUNT; i++){
			if (c == VALID_CHARS[i]) {
				charCount[i]++;
				return;
			}
		}
	}
	
	void addToSheet(Sheet sheet, int x, int y, String type, String name) {
		if (y == 0) {
			int ix = x;
			Row infoRow = sheet.createRow(sheet.getLastRowNum());
			infoRow.createCell(ix++).setCellValue(type);
			infoRow.createCell(ix++).setCellValue("Total Lines");
			infoRow.createCell(ix++).setCellValue("Code Lines");
			infoRow.createCell(ix++).setCellValue("Command Lines");
			infoRow.createCell(ix++).setCellValue("Empty Lines");
			for (int i = 0; i < CHAR_COUNT; i++) {
				infoRow.createCell(ix++).setCellValue("\'" + VALID_CHARS[i] + "\'");
			}
		}
		
		Row dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
		dataRow.createCell(x++).setCellValue(name);
		dataRow.createCell(x++).setCellValue(codeLines + commentLines + emptyLines);
		dataRow.createCell(x++).setCellValue(codeLines);
		dataRow.createCell(x++).setCellValue(commentLines);
		dataRow.createCell(x++).setCellValue(emptyLines);
		for (int i = 0; i < CHAR_COUNT; i++) {
			dataRow.createCell(x++).setCellValue(charCount[i]);
		}
		
	}
	
	void add(FileStat other) {
		codeLines += other.codeLines;
		commentLines += other.commentLines;
		emptyLines += other.emptyLines;
		for (int i = 0; i < CHAR_COUNT; i++)
			charCount[i] += other.charCount[i];
	}
	
}
