public enum  FileType 
{

	txt("txt"), //Text file
	css("css"), // CSS file
	js("javascript"), // JavaScript file
	jpg("jpg"),// Image file
	jpeg("jpg"),
	png("png"),
	bmp("bmp"),
	gif("gif"),
	html("html"),
	ico("x-icon"),
	xml("xml"),
	unknown("octet-stream");

	private final String value;

	private FileType(String value) {
		this.value = value;
	}

	public static FileType getTypeForFile(String file) {
		int indexOfLastPoint = file.lastIndexOf('.');
		String extension = file.substring(indexOfLastPoint + 1);
		
		switch (extension) {
		case "txt":
			return txt;
		case "xml":
			return xml;
		case "css":
			return css;
		case "jpg":
			return jpg;
		case "jpeg":
			return jpeg;
		case "bmp":
			return bmp;
		case "png":
			return png;
		case "gif":
			return gif;
		case "ico":
			return ico;
		case "html":
			return html;
		case "js":
			return js;
		default : 
			return unknown;
				
		}
	}


	public String toString() {
		switch (this) {
		case txt:
		case xml:
		case css:
		case html:
			return String.format("text/%s", this.value);
		case jpg:
		case jpeg:
		case png:
		case gif:
		case bmp:
		case ico:
			return String.format("image/%s", this.value);
		default:
			return String.format("application/%s", this.value);
		}
		
	}
	
	public boolean isImage() {
		
		switch (this) {
		case jpg:
		case jpeg:
		case png:
		case gif:
		case bmp:
		case ico:
			return true;
		default:
			return false;
		}
		
	}
}