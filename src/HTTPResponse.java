import java.util.HashMap;

public class HTTPResponse {
	
	
	private HTTPResponseCode code;
	private HashMap<String, String> headears;
	private String version;
	public byte[] fileContent;
	
	public HTTPResponse(HTTPResponseCode code, String version) {
		this.code = code;
		this.version = Utils.HTTP_TYPE_1_0;
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
		
		builder.append(String.format("HTTP/%s, %s%s", this.version,
					this.code.toString(), Utils.CRLF));
		
		for (String headerName : this.headears.keySet()) {
			builder.append(String.format("%s : %s%s", headerName, this.headears.get(headerName), Utils.CRLF));
		}
		
		builder.append(Utils.CRLF);
		
		return builder.toString();
	}
	
	public boolean shouldCloseConnection() {
		return this.version.equals(Utils.HTTP_TYPE_1_0);
	}
}
