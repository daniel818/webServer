import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;


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
			request = new HTTPRequest(bufferContent);
			if (request == null) {
				return;
			}
			System.out.println(String.format("Received Request:\n%s", request.originRequest));

			//Generate an http response
			generateResponse();
			
			
			if (this.response == null) {
				throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
			}


			//Send the response to client.
			Utils.writeOutputStream(this.connection.getOutputStream(), this.response.toString());
			System.out.println(String.format("Sent Response:\n%s", this.response));
			

			if (shouldAttachFile()) {
				writeAttachedFile();
			}
			

		} catch (IOException e) {
			generateErrorResponse(HTTPResponseCode.INTERNAL_ERROR);
		} catch (ServerException e) {
			generateErrorResponse(e.code);
		} finally {		
			if (this.connection != null) {
				try {
					this.connection.close();
				} catch (IOException e) {
					System.out.println("Could not close socket.");
				}
			}
		}
	}

	private void generateResponse() throws IOException, ServerException {

		switch (this.request.type) {
		case GET:
		case HEAD:
		case POST: {
			handleGetHeadPost();
			break;
		}

		case TRACE: {
			handleTrace();
			break;
		}
		case NOT_SUPPORTED:
			System.out.println("not supported");
			throw new ServerException(HTTPResponseCode.NOT_IMPLEMENTEED);
		}
	}

	private void handleTrace() {
		response = new HTTPResponse(HTTPResponseCode.OK, getConnectionVersion());
		String responseContent = request.originRequest;
		
		if (request.isChunked()){
			response.addHeader(Utils.HTTP_TRANSFER_ENCODING, Utils.HTTP_CHUNKED_KEY);
		} else {
			response.addHeader(Utils.HTTP_CONTENT_LENGTH_KEY, Integer.toString(responseContent.length()));
		}
		
		response.addHeader(Utils.HTTP_CONTENT_TYPE_KEY, Utils.HTTP_CONTENT_MESSAGE_TYPE);
		response.attachFileContent(responseContent.getBytes());

		String connectionString = getConnectionHeaderValue();
		response.addHeader(Utils.HTTP_CONNECTION_KEY, connectionString);
	}


	private void handleGetHeadPost() throws ServerException {
		String fullPath = getRequiredPath();

		try {	
			if (!Utils.isValidFile(fullPath)) {
				throw new ServerException(HTTPResponseCode.NOT_FOUND);
			}
		} catch (SecurityException e) {
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		}

		response = new HTTPResponse(HTTPResponseCode.OK, getConnectionVersion());

		FileType contentType = FileType.getTypeForFile(fullPath);
		
		byte[] fileContent = null;
		if (contentType == FileType.html) {
			HashMap<String, String> bodyObject = getBodyObject();
			HtmlGenerator generator = new HtmlGenerator(bodyObject, fullPath);
			fileContent = generator.generate();
		} else {
			fileContent = contentType.isImage() ? Utils.readImageFile(fullPath) :
				Utils.readFile(fullPath).getBytes();
		}
		

		if (request.isChunked()){
			response.addHeader(Utils.HTTP_TRANSFER_ENCODING, Utils.HTTP_CHUNKED_KEY);
		}else{
			int contentLength = fileContent.length;
			response.addHeader(Utils.HTTP_CONTENT_LENGTH_KEY, Integer.toString(contentLength));	
		}

	
		response.addHeader(Utils.HTTP_CONTENT_TYPE_KEY, contentType.toString());
		response.attachFileContent(fileContent);
		

		String connectionString = getConnectionHeaderValue();
		response.addHeader(Utils.HTTP_CONNECTION_KEY, connectionString);
		
		
	}



	private void generateErrorResponse(HTTPResponseCode code) {
		String version = getConnectionVersion();
		response = new HTTPResponse(code, version);

		if (configuration.isErrorFileExists(code)) {
			String errorFile = configuration.errorPages.get(code);
			String errorFileFullPath = configuration.getFullPathForFile(errorFile);

			String fileContent = "";
			try {
				fileContent = Utils.readFile(errorFileFullPath);
			} catch (ServerException e) {
				//ignore the error in reading the file.
				System.out.println("Error in reading error file: " + errorFile);
			}

			FileType type = FileType.getTypeForFile(errorFile);
			response.addHeader(Utils.HTTP_CONTENT_LENGTH_KEY, Integer.toString(fileContent.length()));
			response.addHeader(Utils.HTTP_CONTENT_TYPE_KEY, type.toString());
			response.attachFileContent(fileContent.getBytes());
		}

		String connectionString = getConnectionHeaderValue();
		response.addHeader(Utils.HTTP_CONNECTION_KEY, connectionString);

		try {
			Utils.writeOutputStream(this.connection.getOutputStream(), response.toString());
			System.out.println(String.format("Sent Response:\n%s", this.response));

			if (response.fileContent != null) {
				Utils.writeOutputStream(connection.getOutputStream(), response.fileContent);
			}
		} catch (ServerException | IOException e) {
			//ignore error in sending the response.
			System.out.println("Error in sending error response");
		}
	}

	private String getConnectionHeaderValue() {
		return  request == null || request.shouldCloseConnection() ? 
				Utils.HTTP_CONNECTION_CLOSE : Utils.HTTP_CONNECTION_KEEP_ALIVE;
	}

	private String getConnectionVersion() {
		return this.request != null ? this.request.version : Utils.HTTP_TYPE_1_0;
	}

	private String getRequiredPath() throws ServerException {

		if (request == null) {
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		}

		return configuration.getFullPathForFile(request.path.isEmpty() ? configuration.defaultPage 
				: request.path);
	}

	
	private HashMap<String, String> getBodyObject()  {
		return HTTPRequest.getParamsFromString(request.body);
	}

	private boolean shouldAttachFile() {
		return request != null && request.type != HTTPRequestType.HEAD;
	}
	
	private void writeAttachedFile() throws ServerException, IOException {
		if (request.isChunked()) {
			Utils.writeOutputStreamChunked(this.connection.getOutputStream(), response.fileContent);
		}else{
			Utils.writeOutputStream(this.connection.getOutputStream(), response.fileContent);
		}
	}
	
}

