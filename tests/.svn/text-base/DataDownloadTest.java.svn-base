import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.After;

import test.Download;
import bencoding.InvalidBEncodingException;


public class DataDownloadTest {

	boolean finished = false;
	boolean success = false;

	Process trackerProcess = null;
	Process clientProcess = null;

	@Test
	public void testDataExchange() throws InterruptedException, InvalidBEncodingException, IOException {
		
		// Start the tracker
		//trackerProcess = jarStarter("data/tracker.jar");
		checkTrackerState();
		
		// Start uploading
		//clientProcess = jarStarter("data/client.jar");
		
		// Start downloading
		new Thread() {
			public void run() {
				try {
					Download download = new Download();
					download.run("data/LePetitPrince.torrent", true, true);
					while(true) {
						if(download.torrent.isComplete()) {
							success = true;
							finished = true;
						}
						Thread.sleep(100L);
					}
				} catch (Exception e) {
					throw new Error(e);
				}
			}
		}.start();
		
		while(!finished) {
			Thread.sleep(100L);
		}
		
		Assert.assertTrue(success);
	}

	@After
	public void destroyProcesses() {
		System.out.println("Exiting...");
		
		if (clientProcess != null) clientProcess.destroy();
		if (trackerProcess != null) trackerProcess.destroy();
	}
	
	public static Process jarStarter(String path) throws IOException {
		long trackerStart = System.nanoTime();
		Process p = Runtime.getRuntime().exec(new String [] {"java", "-jar", path});		
		
		long trackerStarted = System.nanoTime();
		System.out.println(path + " started in " + (((trackerStarted - trackerStart) / 10000) / 100.0) + " ms");

		return p;
	}

	public void checkTrackerState() throws InterruptedException {
		Socket testSocket = null;
		while (testSocket == null) {
			try {
				testSocket = new Socket(InetAddress.getLocalHost(), 6969);
			} catch (Exception e) {
				Thread.sleep(100L);
			}
		}
		assertTrue("Tracker started", testSocket.isBound());
	}
}
