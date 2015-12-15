import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlGenerator {
	public static final String PATTERN_STRING = "[@][{](.*?)[}]";
	public static final String OFF = "";
	
	private final HashMap<String, String> data;
	private final String filePath;
	
	public HtmlGenerator(HashMap<String, String> data, String filePath) {
		this.data = data;
		this.filePath = filePath;
	}
	
	public byte[] generate() throws ServerException {
		String fileContent = readFile();
		Pattern pattern  = Pattern.compile(PATTERN_STRING);
		Matcher matcher = pattern.matcher(fileContent);
		
		
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			String replace = data.get(matcher.group(1));
			replace = replace == null ? OFF : replace;
			matcher.appendReplacement(stringBuffer, replace);
		}
		
		matcher.appendTail(stringBuffer);
		
		
		return stringBuffer.toString().getBytes();
	}
	
	private String readFile() throws ServerException {
		return Utils.readFile(filePath);
	}
	
	
	
}
