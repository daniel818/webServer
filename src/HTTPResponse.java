import java.util.HashMap;


public class HTTPResponse {
	
	
	private HTTPResponseCode code;
	private HashMap<String, String> headears;
	private String version;
	public byte[] fileContent;
	public HashMap<String, String> attachedObject;
	
	
	public HTTPResponse(HTTPResponseCode code, String version) {
		this.code = code;
		this.version = version;
		this.headears = new HashMap<>();
		this.fileContent = null;
	}
	
	public void addHeader(String headerName, String headerValue) {		
		this.headears.put(headerName, headerValue);
	}
	
	public void attachFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	
	public void attachHashMapObject(HashMap<String, String> object) {
		this.attachedObject = object;
	}
	
	public String getAttachedObject() {
		if (attachedObject == null) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("<input type='hidden' name='hidden-object' value='");
		builder.append("{");
		
		Object[] keys = attachedObject.keySet().toArray();
		for (int i = 0; i < keys.length - 1; i++) {
			builder.append(String.format("\"%s\" : \"%s\",", keys[i], attachedObject.get(keys[i])));
		}
		
		
		Object lastKey = keys[keys.length - 1];
		builder.append(String.format("\"%s\" : \"%s\"", lastKey, attachedObject.get(lastKey)));
		
		builder.append("}'/>");
		return builder.toString();
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(String.format("HTTP/%s %s%s", this.version,
					this.code.toString(), Utils.CRLF));
		
		for (String headerName : this.headears.keySet()) {
			builder.append(String.format("%s : %s%s", headerName, this.headears.get(headerName), Utils.CRLF));
		}
		
		builder.append(Utils.CRLF);
		return builder.toString();
	}
	

}
