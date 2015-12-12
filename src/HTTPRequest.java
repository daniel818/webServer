import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTTPRequest {
	
	public final String originRequest;
	public HTTPRequestType type;
	public String path;
	public String version;
	public HashMap<String, String> UrlParams;
	private HashMap<String, String> headers;
	public String body;

	public HTTPRequest(String request)  throws ServerException{
		originRequest = request;
		headers = new HashMap<>();
		UrlParams = new HashMap<>();
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
System.out.println(this.path);
		} else  {
			throw new ServerException(HTTPResponseCode.BAD_REQUEST);
		}
		

		Pattern headerlinePattern = Pattern.compile(Utils.REQUEST_HEADERLINE_PATTERN_STRING);
		int i = 1;
		while (i < requestLines.length && !requestLines[i].isEmpty()) {
			
			matcher = headerlinePattern.matcher(requestLines[i]);

			if (!matcher.matches()) {
				throw new ServerException(HTTPResponseCode.BAD_REQUEST); 
			} 
			this.headers.put(matcher.group(1).toLowerCase().trim(),
					matcher.group(2).trim());
			i++;
		}
		
		
		if (this.type == HTTPRequestType.POST) {
			parseBody(i, requestLines);
		}
		
		if (this.type == HTTPRequestType.GET || this.type == HTTPRequestType.HEAD) {
			parseURLParams();
		}
		if(this.path.equals("index.html")){
			this.headers.put("chunked", "yes");
			System.out.println("fuck");
		}
	}
	
	private void parseURLParams() throws ServerException {
		
		String[] Urlparts = this.path.split("\\?");
		this.path = Urlparts[0];
		
		if (Urlparts.length > 1) {
			UrlParams = getParamsFromString(Urlparts[1]);
			if (UrlParams == null) {
				throw new ServerException(HTTPResponseCode.BAD_REQUEST);
			}
		}
	}

	private void parseBody(int startIndex, String[] requestLines) throws ServerException {
		StringBuilder builder = new StringBuilder();
		while (startIndex < requestLines.length) {
			builder.append(requestLines[startIndex]);
			startIndex++;
		}
		
		this.body = builder.toString();
		
		String contentLength = headers.get(Utils.HTTP_CONTENT_LENGTH_KEY);
		if (contentLength == null) {
			throw new ServerException(HTTPResponseCode.BAD_REQUEST);
		}
		
		try {
			if (Integer.parseInt(contentLength) != body.length()) {
				System.out.println(String.format("content-length:%s, body length: %s",
						Integer.parseInt(contentLength), body.length()));
				throw new ServerException(HTTPResponseCode.BAD_REQUEST);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new ServerException(HTTPResponseCode.BAD_REQUEST);
		}
	}

	public boolean shouldCloseConnection() {

		String connectionValue = this.headers.get(Utils.HTTP_CONNECTION_KEY);
		if (this.version.equals(Utils.HTTP_TYPE_1_0)) {
			return !Utils.HTTP_CONNECTION_KEEP_ALIVE.equals(connectionValue);
		} else {
			return Utils.HTTP_CONNECTION_CLOSE.equals(connectionValue);
		}
	} 
	
<<<<<<< HEAD
	public boolean isChunked(){
		
		if (this.headers.containsKey("chunked")){
			if (this.headers.get("chunked").equals("yes")) {
				return true;	
			}
		}
		return false;
=======
	public static HashMap<String, String> getParamsFromString(String str) {
		HashMap<String, String> params = new HashMap<>();
		String[] paramsParts = str.split("&");
		for (int  i = 0; i < paramsParts.length; i++) {
			String[] keyValue = paramsParts[i].split("=");
			if (keyValue.length != 2) {
				return null;
			}
			
			params.put(keyValue[0], keyValue[1]);
		}
		
		return params;
>>>>>>> origin/master
	}
}


