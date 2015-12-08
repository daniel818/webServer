import java.util.HashMap;

public class HTTPResponse {
	
	
	private HTTPResponseCode code;
	private HashMap<String, String> headears;
	public byte[] fileContent;
	
	public HTTPResponse(HTTPResponseCode code) {
		this.code = code;
		this.headears = new HashMap<>();
		this.fileContent = null;;
	}
	
	public void addHeader(String headerName, String headerValue) {		
		this.headears.put(headerName, headerValue);
	}
	
	public void attachFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(String.format("HTTP/%s, %s%s", Utils.HTTP_TYPE,
					this.code.toString(), Utils.CRLF));
		
		for (String headerName : this.headears.keySet()) {
			builder.append(String.format("%s : %s%s", headerName, this.headears.get(headerName), Utils.CRLF));
		}
		
		builder.append(Utils.CRLF);
		
		return builder.toString();
	}
}
