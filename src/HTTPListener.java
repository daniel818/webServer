import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
//import java.util.concurrent.ExecutorService;



public class HTTPListener {

	private final Executor executor;
	private final Configuration configuration;

	public HTTPListener(Configuration configuration, Executor executor) {
		this.executor = executor;
		this.configuration = configuration;
	}

	public void start() throws IOException {

		int port = configuration.port;

		// Establish the listen socket.
		@SuppressWarnings("resource")
		ServerSocket socket = new ServerSocket(port);

		System.out.println("start listeneing on port: " + port);

		// Process HTTP service requests in an infinite loop.
		while (true) {
			
			// Listen for a TCP connection request.
			Socket connection = socket.accept();

			// Construct an object to process the HTTP request message.
			HTTPRequestHandler requestHandler = new HTTPRequestHandler(connection, configuration);
			
			System.out.println("Submiting new request");
			this.executor.execute(requestHandler);
		}
	}
}
