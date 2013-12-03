package http;

/**
 * Binary URL-decoding utilities for SHA-1 hashes of torrents
 * 
 * @author Bruno Didot
 * @author Ismail Amrani
 * @author Amos Wenger (ndd@rylliog.cz)
 */
public class BinaryURLDecoder {

	/** Hide the constructor - it's an utility class */
	private BinaryURLDecoder() {}

	/**
	 * Do a binary URL-encoding of a SHA-1 hash - used in requests to torrent
	 * trackers.
	 * @param input a URL-encoded String representation of the hash
	 * @return input An array of (usually 20) bytes composing the SHA-1 hash 
	 */
	public static byte[] decode(String input) {
		byte[] result = new byte[20];

		int j = 0;
		for(int i = 0; i < result.length; i++) {
			char c = input.charAt(j); 
			if(c == '%') {
				j++;
				// if it's %-prefixed then it's an hexadecimal value with two digits
				int value = Integer.parseInt(input.substring(j, j + 2), 16);
				assert(value < Byte.MAX_VALUE && value > Byte.MIN_VALUE);
				result[i] = (byte) value;
				j += 2;
			} else {
				// otherwise it's just the char we read
				// although we make sure what was given to us is according to the standard
				assert( ('a' <= c && c <= 'z') ||
			            ('A' <= c && c <= 'Z') ||
			            c == '.' || c == '-' || c == '_' || c == '~');
				result[i] = (byte) c;
				j++;
			}
		}
		
		assert(j == input.length());
		return result;
	}
	
}
