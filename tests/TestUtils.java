import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TestUtils {
	
	public static void waitForServerSocket(int port) throws InterruptedException {
		Socket testSocket = null;
		while (testSocket == null) {
			try {
				testSocket = new Socket(InetAddress.getLocalHost(), port);
			} catch (Exception e) {
				Thread.sleep(100L);
			}
		}
		
		assertTrue("A ServerSocket is listening on port " + port, testSocket.isBound());
		
		try {
			testSocket.close();
		} catch (IOException io) {
			System.out.println("You ran out of luck for today");
		}
	}

	public static Process jarStarter(String[] args) throws IOException {
		long trackerStart = System.nanoTime();
		
		String[] execute = new String[args.length + 2];
		
		execute[0] = "java";
		execute[1] = "-jar";
		
		System.arraycopy(args, 0, execute, 2, args.length);
		
		Process p = new ProcessBuilder(Arrays.asList(execute)).start();
		
		long trackerStarted = System.nanoTime();
		System.out.println("[TestUtils] " + execute[2] + " started in " + (((trackerStarted - trackerStart) / 10000) / 100.0) + " ms");

		return p;
	}

	public static boolean containsAll(BufferedReader bR, List<String> strings) throws IOException {
		String line = null;
		
		while((line = bR.readLine()) != null && ! strings.isEmpty()) {
			Iterator<String> iter = strings.iterator();
			
			while (iter.hasNext()) {
				String s = iter.next();
				
				if (line.contains(s)) {
					iter.remove();
					break; // k, we did find what we were looking for, now using the so much feared break instruction !!
				}
			}
		}

		if (! strings.isEmpty()) {
			System.out.println("[DataUploadTest][WARNING] some string(s) did not appear :");
			
			for (String s : strings) {
				System.out.println(s);
			}
		}
		
		return strings.isEmpty();
	}
	
	public static boolean contains(BufferedReader bR, String string) throws IOException {
		String line = null;
		
		while ((line = bR.readLine()) != null && ! line.contains(string));
		
		return line != null;
	}
	
	public static boolean isEncrypted(BufferedReader bR) throws IOException {
		return TestUtils.contains(bR, "[PeerHandler] enabling encryption");
	}
	
	public static boolean isClear(BufferedReader bR) throws IOException {
		return TestUtils.contains(bR, "One of the two of us does not support encryption");
	}
	
	public static boolean isEncryptedAndCompleted(BufferedReader bR) throws IOException {
		List<String> strings = new ArrayList<String>();
		strings.add("[PeerHandler] enabling encryption");
		strings.add("Download completed");
		
		return TestUtils.containsAll(bR, strings);
	}

	public static boolean isClearAndCompleted(BufferedReader bR) throws IOException {
		List<String> strings = new ArrayList<String>();
		strings.add("One of the two of us does not support encryption");
		strings.add("Download completed");
		
		return TestUtils.containsAll(bR, strings);
	}
	
}
