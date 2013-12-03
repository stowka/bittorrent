package torrent.peer;

import java.util.Random;

public class PeerIDGenerator {

	public static String generateID() {
		StringBuilder sb = new StringBuilder();
		
		
		// we used to usurpate uTorrent (UT) but it seems that it doesn't like that, and
		// even detects us as a FAKE. (Fail.)
		// So we're trying Azureus (AZ)
		sb.append("-XY");
		Random rand = new Random();
		for(int i = 0; i < 4; i++) {
			sb.append(rand.nextInt(10));
		}
		sb.append('-');
		for(int i = 0; i < 12; i++) {
			sb.append(rand.nextInt(10));
		}
		
		return sb.toString();
	}
	
}
