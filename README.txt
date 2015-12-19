The Classes and Components of our web server:

1. Server.java:
	The main class of the web server. This class is in charge of the following:
	 1. getting the required configuration object using the configuration parser.
	 2. Creating the thread pool.
	 3. Start listen to http requests using the http listener.

2. Config.ini relevant components: 
	1. Config.ini files that should be saved under the working directory of the program
	2. ConfigParser - Class that read the config.ini, parse it, and create a configuration object.
	3. Configuration object that contains the relvant iformation regarding the server.

3. Multi theading components: 
	1. Sync queue - queue to put and pull request handler tasks.  The queue supported synchronization of multiple threads.
	2. Thread Pool - in charge of creating the required thread that will try getting new task from the queue and execute them. 

4. HttpListener.java - this class is in charge of listening to incoming request, create an instance of http request handler, and put it in the sync queue. 

5. HttpRequestHandler.java - this class is the main logic of the class. each thread from the thread pool has an instance of the class. 
	the class does the following: 
	1. Read the request.
	2. Parse request using the HTTP Request class. 
	3. if needed read the required files. 
	4. generates and sends the appropiate response.
	5. This class, also in charge of handling errors and exceptions using the ServerException class. 

6. HTTPRequest.java - this class is in charge of parsing the incoming request. 

7. HTTPResponse.java - this class is in charge of storing the required components for the response and provide helpers method for sending responses. 

8. Utils.java - this class contains usefull constants and helper static methods such as reading file, test is file is valid, read/write streams. ...

9. ServerException - every compnent that encounters error  should throw this exception with required response type such not found, internal ...
	Then the HttpRequestHandler, will catch this error and generate the required error response. 

10. Useful enums: 
	1. FileType.java - Contains all the supported file types. Also contains helper method to get the content type of a file and check if file is an image.
	2. HTTPRequestType.java - Class that contains all the suppoered http methods.
	3. HTTPResponseCode.java -  Class that contains all the supported response codes. 


