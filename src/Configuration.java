import java.io.File;
import java.util.HashMap;

public class Configuration {
	public final int port;
	public final String root;
	public final String defaultPage;
	public final int maxThreads;
	public final HashMap<HTTPResponseCode, String> errorPages;
	
	public Configuration(int port, String root, String defaultPage, int maxThreads,
			HashMap<HTTPResponseCode, String> errorPages) {
		this.port = port;
		this.root = root;
		this.defaultPage = defaultPage;
		this.maxThreads = maxThreads;
		this.errorPages = errorPages == null ? new HashMap<HTTPResponseCode, String>() : errorPages;
	}
	
	public String toString() {
		return String.format("(port:%d, root:%s, defaultPage:%s, maxThreads:%d)", port, root, defaultPage, maxThreads);
	}
	
	public boolean isValid() {
		
		if (this.port <= 0 || this.port > 56000) {
			return false;
		}
		
		File file = new File(this.root);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}
		
		if (!Utils.isValidFile(this.root + this.defaultPage)) {
			return false;
		}
		
		for (String errorFile : this.errorPages.values()) {
			System.out.println(errorFile);
			if (!Utils.isValidFile(getFullPathForFile(errorFile))) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isErrorFileExists(HTTPResponseCode code) {
		return this.errorPages.containsKey(code);
	}
	
	public String getFullPathForFile(String file) {
		return this.root + file;
	}
}
