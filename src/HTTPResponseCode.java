
public enum HTTPResponseCode {
		OK,
		NOT_FOUND,
		NOT_IMPLEMENTEED,
		BAD_REQUEST,
		INTERNAL_ERROR;
		
		public String toString() {	
			switch(this) {		
			case OK:
				return "200 OK";
			case NOT_FOUND: 
				return "404 Not Found";
			case NOT_IMPLEMENTEED:
				return "501 Not Implemented";
			case INTERNAL_ERROR:
			default:
				return "500 Internal Server Error";
			}
		}
}
	