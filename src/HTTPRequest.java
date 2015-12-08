import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTTPRequest {

	public HTTPRequestType type;
	public String path;
	public String version;
	private HashMap<String, String> headers;

	public HTTPRequest(String request)  throws ServerException{
		this.headers = new HashMap<>();
		parseRequest(request);
	}

	private void parseRequest(String str) throws ServerException{	

		String[] requestLines = str.split(Utils.CRLF);
		String firstLine = requestLines[0].replaceAll("\\s*", "");

		Pattern firstLinePattern = Pattern.compile(Utils.REQUEST_FIRSTLINE_PATTEN_STRING);
		Matcher matcher = firstLinePattern.matcher(firstLine);

		if (matcher.matches()) {
			
			this.type = HTTPRequestType.convertFromString(matcher.group(1));
			this.path = matcher.group(2);
			this.version = matcher.group(3);

		} else  {
			throw new ServerException(HTTPResponseCode.BAD_REQUEST);
		}
		

		Pattern headerlinePattern = Pattern.compile(Utils.REQUEST_HEADERLINE_PATTERN_STRING);
		for (int i = 1; i < requestLines.length; i++) {

			matcher = headerlinePattern.matcher(requestLines[i]);

			if (!matcher.matches()) {
				throw new ServerException(HTTPResponseCode.BAD_REQUEST); 
			} 
			this.headers.put(matcher.group(1), matcher.group(2));
		}
	}
}

