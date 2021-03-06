
import java.io.IOException;
import java.util.concurrent.Executor;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class Server {
	
	public static final int MAX_THREADS = 10;

	public static void main(String[] args) throws IOException  {
		
		//configuration parser.
		ConfigParser configParser = new ConfigParser(Utils.WORKING_DIR + Utils.FILE_SEPERATOR +
				Utils.CONFIG_INI);
		Configuration configuration = configParser.parse();
		
		//Check if configuration is valid.
		try {
			if (configuration == null || !configuration.isValid()) {
				System.out.println("Error in creating server: Please check the config.ini file");
				return;
			}
		} catch (SecurityException e) {
			System.out.println("Error in creating server: Please check the config.ini file");
			return;
		}			
		
		//Create and start the handlers pool.
		int maxThreads = configuration.maxThreads > MAX_THREADS ? MAX_THREADS : configuration.maxThreads;
		Executor executor = new ThreadPool(maxThreads); 
		
		//Create the Listener and start Listen to requests.
		HTTPListener listener = new HTTPListener(configuration, executor);
		
		try {
			listener.start();
		} catch (IOException | RejectedExecutionException e) {
			System.out.println("Error: Could not create Server");
		}
		
		
			
	}

}
