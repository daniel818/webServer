
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
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
	

	//public static final String HTTP_TRANSFER_ENCODING = "transfer-encoding";

	//parmas info file
	public static final String parmsInfo= "params_info.html";
	



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
		BufferedImage originalImage;
		try {
			originalImage = ImageIO.read(new File(file));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( originalImage, FileType.getExtension(file),  baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();

			return imageInByte;

		} catch (IOException e) {
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
	
//	public static void writeOutputStreamChunked(OutputStream out, String FilePath) throws ServerException {
//		File f = new File(FilePath);
//		
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
//		BufferedReader reader;
//		try {
//			reader = new BufferedReader(new FileReader(f));
//			char[] chunk = new char[1000];
//			int len = 0;
//			boolean firstChunk = true;
//			while((len = reader.read(chunk)) != -1){
//				if(firstChunk == true){
//					writer.write(CRLF);
//					firstChunk = false;
//				}
//				writer.write(Integer.toHexString(len) + "\r\n");
//				writer.write(chunk);
//				writer.write("\r\n");
//				writer.flush();
//			}
//			writer.write(Integer.toHexString(0)+"\r\n");
//			writer.flush();
//			writer.close();
//			reader.close();
//			
//		} catch (IOException e) {
//			throw new ServerException(HTTPResponseCode.INTERNAL_ERROR);
//		}
//
//	}
	public static boolean isValidFile(String filePath) {
		File file = new File(filePath);

		if (!file.exists() || file.isDirectory() || !file.canRead()){
			return false;
		}
		return true;
	}	
}