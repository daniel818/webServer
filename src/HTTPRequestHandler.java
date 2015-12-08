import java.io.IOException;
import java.net.Socket;


public class HTTPRequestHandler implements Runnable {

	private final Configuration configuration;
	private final Socket connection;
	private  HTTPRequest request;

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
			HTTPResponse response = generateResponse();

			//Send the response to client.
			Utils.writeOutputStream(this.connection.getOutputStream(), response.toString());
			
			if (shouldAttachFile()) {
				Utils.writeOutputStream(this.connection.getOutputStream(), response.fileContent);
			}

		} catch (IOException e) {
			generateErrorResponse(HTTPResponseCode.INTERNAL_ERROR);
		} catch (ServerException e) {
			generateErrorResponse(e.code);
		}

		//Check if persistent or not. and handle it. 
		if (shouldClose()) {
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


	private boolean shouldClose() {
		return true;
	}


	private HTTPResponse generateResponse() throws IOException, ServerException {
		
		if (this.request.type == HTTPRequestType.NOT_SUPPORTED) {
			throw new ServerException(HTTPResponseCode.NOT_IMPLEMENTEED);
		}
		
		
		String relativePath = this.request.path.equals("") ? this.configuration.defaultPage : this.request.path;
		String fullPath = this.configuration.getFullPathForFile(relativePath);
		
		try {	
			if (!Utils.isValidFile(fullPath)) {
				throw new ServerException(HTTPResponseCode.NOT_FOUND);
			}
		} catch (SecurityException e) {
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		}
		

		HTTPResponse response = new HTTPResponse(HTTPResponseCode.OK);
		//response.addHeader("connection", "closed");


		FileType contentType = FileType.getTypeForFile(fullPath);
		
		byte[] fileContent = contentType.isImage() ? Utils.readImageFile(fullPath) :
			Utils.readFile(fullPath).getBytes();

		response.addHeader("content-length", Integer.toString(fileContent.length));
		response.addHeader("content-type", contentType.toString());
		response.attachFileContent(fileContent);

		return response;
	}

	private void handleTrace() {
		// TODO Auto-generated method stub
		
	}


	private void handleHead() {
		// TODO Auto-generated method stub
		
	}


	private void handlePost() {
		// TODO Auto-generated method stub
		
	}


	private void handleGet() {
		// TODO Auto-generated method stub
		System.out.println("generate get reponse");
		
	}


	private void generateErrorResponse(HTTPResponseCode code) {
		HTTPResponse response = new HTTPResponse(code);
		
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
			response.addHeader("content-length", Integer.toString(fileContent.length()));
			response.addHeader("content-type", type.toString());
			response.attachFileContent(fileContent.getBytes());
		}
		
		String connectionString = shouldClose() ? "closed" : "keep-alive";
		response.addHeader("connection", connectionString);
		
		try {
			Utils.writeOutputStream(this.connection.getOutputStream(), response.toString());
			
			if (response.fileContent != null) {
				Utils.writeOutputStream(this.connection.getOutputStream(), response.fileContent);
			}
		} catch (ServerException | IOException e) {
			//ignore error in sending the response.
			System.out.println("Error in sending error response");
		}
		
		
	}
}

