import java.io.IOException;
import java.net.Socket;


public class HTTPRequestHandler implements Runnable {

	private final Configuration configuration;
	private final Socket connection;
	private  HTTPRequest request;
	private HTTPResponse response;

	public HTTPRequestHandler(Socket connection, Configuration configuration)  {
		this.configuration = configuration;
		this.connection = connection;
	}


	public void run() {

		try {

			//Read the message from the client
			String bufferContent = Utils.readInputStream(this.connection.getInputStream());

			//Parse the message
			this.request = new HTTPRequest(bufferContent);

			//Generate an http response
			generateResponse();

			//Send the response to client.
			Utils.writeOutputStream(this.connection.getOutputStream(), this.response.toString());
			
			if (shouldAttachFile()) {
				Utils.writeOutputStream(this.connection.getOutputStream(), this.response.fileContent);
			}

		} catch (IOException e) {
			generateErrorResponse(HTTPResponseCode.INTERNAL_ERROR);
		} catch (ServerException e) {
			generateErrorResponse(e.code);
		}

		//Check if persistent or not. and handle it. 
		if (this.response.shouldCloseConnection()) {
			try {
				this.connection.close();
			
			} catch (IOException e) {
				//Ignoring error in closing socket.
				System.out.println("Could not close socket.");
			}
		}
	}

	private boolean shouldAttachFile() {
		return this.request.type  ==  HTTPRequestType.POST || 
				this.request.type == HTTPRequestType.GET;
	}

	private void generateResponse() throws IOException, ServerException {
		
		switch (this.request.type) {
		case GET:
		case HEAD: {
			handleGetHead();
		}
		case POST: {
			handlePost();
		}
		case TRACE: {
			handleTrace();
			break;
		}
		case NOT_SUPPORTED:
			throw new ServerException(HTTPResponseCode.NOT_IMPLEMENTEED);
		}
	}

	private void handleTrace() {
		// TODO Auto-generated method stub
		
	}


	private void handleGetHead() throws ServerException {
		String relativePath = this.request.path.equals("") ? this.configuration.defaultPage : this.request.path;
		String fullPath = this.configuration.getFullPathForFile(relativePath);
		
		try {	
			if (!Utils.isValidFile(fullPath)) {
				throw new ServerException(HTTPResponseCode.NOT_FOUND);
			}
		} catch (SecurityException e) {
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		}
		
		this.response = new HTTPResponse(HTTPResponseCode.OK, this.request.version);
		
		FileType contentType = FileType.getTypeForFile(fullPath);
		byte[] fileContent = contentType.isImage() ? Utils.readImageFile(fullPath) :
			Utils.readFile(fullPath).getBytes();

		this.response.addHeader("content-length", Integer.toString(fileContent.length));
		this.response.addHeader("content-type", contentType.toString());
		this.response.attachFileContent(fileContent);
		
		String connectionString = response.shouldCloseConnection() ? "closed" : "keep-alive";
		this.response.addHeader("connection", connectionString);
	}


	private void handlePost() {
		// TODO Auto-generated method stub
		
	}

	private void generateErrorResponse(HTTPResponseCode code) {
		System.out.println("generating error response with code: " + code);
		this.response = new HTTPResponse(code, this.request.version);
		
		if (this.configuration.isErrorFileExists(code)) {
			String errorFile = this.configuration.errorPages.get(code);
			String errorFileFullPath = this.configuration.getFullPathForFile(errorFile);
			
			String fileContent = "";
			try {
				fileContent = Utils.readFile(errorFileFullPath);
			} catch (ServerException e) {
				//ignore the error in reading the file.
				System.out.println("Error in reading error file: " + errorFile);
			}
			
			FileType type = FileType.getTypeForFile(errorFile);
			this.response.addHeader("content-length", Integer.toString(fileContent.length()));
			this.response.addHeader("content-type", type.toString());
			this.response.attachFileContent(fileContent.getBytes());
		}
		
		String connectionString = this.response.shouldCloseConnection() ? "closed" : "keep-alive";
		this.response.addHeader("connection", connectionString);
		
		try {
			Utils.writeOutputStream(this.connection.getOutputStream(), response.toString());
			
			if (this.response.fileContent != null) {
				Utils.writeOutputStream(this.connection.getOutputStream(), response.fileContent);
			}
		} catch (ServerException | IOException e) {
			//ignore error in sending the response.
			System.out.println("Error in sending error response");
		}
		
		
	}
}

