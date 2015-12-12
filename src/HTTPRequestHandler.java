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

			//Generate an http response
			generateResponse();
			
<<<<<<< HEAD
			
=======
			if (this.response == null) {
				throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
			}
>>>>>>> origin/master

			//Send the response to client.
			Utils.writeOutputStream(this.connection.getOutputStream(), this.response.toString());

			if (shouldAttachFile()) {
				if (request.isChunked()){
					Utils.writeOutputStreamChunked(this.connection.getOutputStream(), this.getRequiredPath());
				}else{
					Utils.writeOutputStream(this.connection.getOutputStream(), this.response.fileContent);
				}
			}
			
			if (shouldAttachObject()) {
				Utils.writeOutputStream(this.connection.getOutputStream(), this.response.getAttachedObject());
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
			throw new ServerException(HTTPResponseCode.NOT_IMPLEMENTEED);
		}
	}

	private void handleTrace() {
		response = new HTTPResponse(HTTPResponseCode.OK, getConnectionVersion());
		String responseContent = request.originRequest;


		response.addHeader(Utils.HTTP_CONTENT_LENGTH_KEY, Integer.toString(responseContent.length()));
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
		byte[] fileContent = contentType.isImage() ? Utils.readImageFile(fullPath) :
			Utils.readFile(fullPath).getBytes();
		
		if (request.type == HTTPRequestType.POST) {
			HashMap<String, String> bodyObject = getBodyObject();
			this.response.attachHashMapObject(bodyObject);
		}
		

<<<<<<< HEAD
		if (!request.isChunked()){
			response.addHeader(Utils.HTTP_CONTENT_LENGTH_KEY, Integer.toString(fileContent.length));
		}else{
			response.addHeader(Utils.HTTP_TRANSFER_ENCODING, "chunked");	
		}
=======
		int contentLength = fileContent.length + response.getAttachedObject().length();
		response.addHeader(Utils.HTTP_CONTENT_LENGTH_KEY, Integer.toString(contentLength));
>>>>>>> origin/master
		response.addHeader(Utils.HTTP_CONTENT_TYPE_KEY, contentType.toString());
		response.attachFileContent(fileContent);
		
		
		
		

		String connectionString = getConnectionHeaderValue();
		response.addHeader(Utils.HTTP_CONNECTION_KEY, connectionString);
	}


<<<<<<< HEAD
	private void handlePost() {
		// TODO Auto-generated method stub

	}
=======
>>>>>>> origin/master

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
<<<<<<< HEAD

=======
	
	private HashMap<String, String> getBodyObject()  {
		return HTTPRequest.getParamsFromString(request.body);
	}

	private boolean shouldAttachFile() {
		return request != null && request.type != HTTPRequestType.HEAD;
	}
	
	private boolean shouldAttachObject() {
		return response != null && response.attachedObject != null;
	}
>>>>>>> origin/master
}

