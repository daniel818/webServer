
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigParser {
	
	private final String fileName;
	
	public ConfigParser(String fileName) {
		this.fileName = fileName;
	}
	
	public Configuration parse() {
		
		Configuration configuration = null;
		
		try {
			
			//Read all content of file.
			String stringData = Utils.readFile(this.fileName);
			
			//Parse content to configuration object.
			configuration =  parseStringData(stringData);
			
		} catch (ServerException e) {
			return null;
		}
		
		return configuration;	
	}

	
	private Configuration parseStringData(String stringData) {
		if (stringData == null) {
			return null;
		}
		String parsedString = stringData.replaceAll("\\s*", "").replaceAll(":", "=");
		Pattern pattern = Pattern.compile(Utils.CONFIG_PATTERN_STRING);
		Matcher matcher = pattern.matcher(parsedString);
		
		if (!matcher.matches()) {
			return null;
		}
		
		int port = Integer.parseInt(matcher.group(1));
		String root = matcher.group(2);
		String defaultPage = matcher.group(3);
		int maxThreads = Integer.parseInt(matcher.group(4));
		HashMap<HTTPResponseCode, String> errorPages = new HashMap<>(); 
		
		if (matcher.group(5) != null && !matcher.group(6).isEmpty()) {
			errorPages.put(HTTPResponseCode.BAD_REQUEST, matcher.group(6));
		}
		
		if (matcher.group(7) != null && !matcher.group(8).isEmpty()) {
			errorPages.put(HTTPResponseCode.NOT_FOUND, matcher.group(8));
		}
		
		if (matcher.group(9) != null && !matcher.group(10).isEmpty()) {
			errorPages.put(HTTPResponseCode.INTERNAL_ERROR, matcher.group(10));
		}
		
		if (matcher.group(11) != null && !matcher.group(12).isEmpty()) {
			errorPages.put(HTTPResponseCode.NOT_IMPLEMENTEED, matcher.group(12));
		}
		
		
		return new Configuration(port, root, defaultPage, maxThreads, errorPages);
	}
	
	
}


