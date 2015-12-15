
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;



public class Utils {

	//Static Patterns Strings.
	public static String CONFIG_PATTERN_STRING = "port=([0-9]*)root=(.*)defaultPage=(.*)maxThreads=([0-9]*)"
			+ "(bad-request=(.*?))?"
			+ "(not-found=(.*?))?"
			+ "(internal=(.*?))?"
			+ "(not-implemented=(.*?))?";
	public static String REQUEST_FIRSTLINE_PATTEN_STRING = "(.*?)/(.*)HTTP/(1[.][0-1])";
	public static String REQUEST_HEADERLINE_PATTERN_STRING = "(.*):(.*)";

	//Constants Symbols/Tokens
	public static final String CRLF = "\r\n";
	
	
	
	///saasas
	//Configuration and file constants
	public static final String FILE_SEPERATOR = File.separator;
	public static final String CONFIG_INI = "config.ini";
	public static final String WORKING_DIR = System.getProperty("user.dir");

	
	//HTTP protocol constants
	public static final String HTTP_TYPE_1_0 = "1.0";
	public static final String HTTP_TYPE_1_1 = "1.1";
	
	public static final String HTTP_CONNECTION_KEY = "connection";
	public static final String HTTP_CONNECTION_CLOSE = "closed";
	public static final String HTTP_CONNECTION_KEEP_ALIVE = "keep-alive";
	
	public static final String HTTP_CONTENT_LENGTH_KEY = "content-length";
	public static final String HTTP_CONTENT_TYPE_KEY = "content-type";
	
	public static final String HTTP_CONTENT_MESSAGE_TYPE = "message/http";
	public static final String HTTP_TRANSFER_ENCODING = "transfer-encoding";
	public static final String HTTP_CHUNKED_KEY = "chunked";
	public static final String HTTP_CHUNKED_KEY_YES = "yes";

	//chunked extension for handeling sending response in chunks
	public static final int CHUNKED_SIZE = 1024;
	
	



	public static String readFile(String file) throws ServerException{

		//Change this to dynamic config.ini	
		BufferedReader reader;
		try {

			Path configPath = Paths.get(file);
			reader = Files.newBufferedReader(configPath);
			StringBuilder builder = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				builder.append(line);
				line = reader.readLine();
			}	
			reader.close();
			
			return builder.toString();

		} catch (IOException | InvalidPathException | SecurityException ex) {
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		}
	}

	public static byte[] readImageFile(String file) throws ServerException {
		
		if (FileType.getTypeForFile(file) == FileType.ico) {
			try {
				return Files.readAllBytes(new File(file).toPath());
			} catch (Exception e) {
				throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
			}
			
		}
		
		BufferedImage originalImage;
		try {
			originalImage = ImageIO.read(new File(file));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( originalImage, FileType.getExtension(file),  baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();

			return imageInByte;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		}
	} 
	
	public static String readInputStream(InputStream in) throws ServerException  {

		BufferedReader inputStream = new BufferedReader(new InputStreamReader(in));
		StringBuilder builder = new StringBuilder();

		try {

			String line  = inputStream.readLine();
			while(line != null && !line.equals("")) {
				builder.append(line + CRLF);

				line = inputStream.readLine();
			}
			builder.append(CRLF);
			while(inputStream.ready()) {
				builder.append((char) inputStream.read());
			}
			
			
		} catch (IOException e) {
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR); 
		}	
		return builder.toString();
	}

	public static void writeOutputStream(OutputStream out, String content) throws ServerException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		try {
			writer.write(content);
			writer.flush();
			//writer.close();//remove
		} catch (IOException e) {
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		}

	}

	public static void writeOutputStream(OutputStream out, byte[] content) throws ServerException {
		try {
			out.write(content);
			out.flush();
		} catch (IOException e) {
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		}
	}
	
	public static  void writeOutputStreamChunked(OutputStream out, byte[] FileContent) throws ServerException {
		
		try {
			
			int startChunk = 0;
			while (startChunk <=  FileContent.length) {
				int length = startChunk + CHUNKED_SIZE <= FileContent.length ? 
						CHUNKED_SIZE : FileContent.length - startChunk;
				
				String firstLine = String.format("%s%s", Integer.toHexString(length), CRLF);
				out.write(firstLine.getBytes());
				out.write(FileContent, startChunk, length);
				out.write(CRLF.getBytes());
				out.flush();
				
				if (length == 0) {
					break;
				}
				
				startChunk += length;
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
		} 
	}
	
	public static boolean isValidFile(String filePath) {
		File file = new File(filePath);

		if (!file.exists() || file.isDirectory() || !file.canRead()){
			return false;
		}
		return true;
	}	
}