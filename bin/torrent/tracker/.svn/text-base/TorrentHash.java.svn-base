package torrent.tracker;

import http.BinaryURLDecoder;
import http.BinaryURLEncoder;

import java.util.Arrays;

/**
 * Represents the 20-byte hash of a torrent.
 * 
 * We need to wrap the byte array in a class in order to have
 * equals() and hashCode() implemented properly, so that we can
 * use it in a HashMap.
 *
 * @author Ismail Amrani
 * @author Bruno Didot
 * @author Amos Wenger
 *
 */
public class TorrentHash {

	private byte[] data;
	private String urlEncoded;
	
	public TorrentHash(byte[] data) {
		assert(data.length == 20);
		this.data = data;
		this.urlEncoded = BinaryURLEncoder.encode(data);
	}
	
	public TorrentHash(String urlEncodedHash) {
		this.data = BinaryURLDecoder.decode(urlEncodedHash);
		this.urlEncoded = urlEncodedHash;
		assert(data.length == 20);
	}
	
	public String urlEncoded() {
		return this.urlEncoded;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof TorrentHash)) {
			return false;
		}
		
		TorrentHash th = (TorrentHash) o;
		return Arrays.equals(th.data, data);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(data);
	}

	public byte[] binaryHash() {
		return data;
	}
	
}
