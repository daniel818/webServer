

public enum HTTPRequestType {
		GET, POST, TRACE, HEAD, NOT_SUPPORTED;
		
		public static HTTPRequestType convertFromString(String str) {
			
			System.out.println(str);
			
			switch (str.toUpperCase()) {
			case "GET":
				return GET;
			case "POST":
				return POST;
			case "TRACE":
				return HEAD;
			case "HEAD":
				return TRACE;
			default:
				return NOT_SUPPORTED;
			}
		}
	}
	