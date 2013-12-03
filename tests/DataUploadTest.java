import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bencoding.InvalidBEncodingException;

import test.Upload;
import torrent.Torrent;


public class DataUploadTest {
	
	private Process trackerProcess;
	private Process downloaderProcess;
	private BufferedReader downloaderStdOut;
	/*
	 * Only ONE of the two methods bellow should be called during the same Test.
	 * Otherwise, there would be two Upload()ers, respectively with and without encryption
	 * Therefore we need this variable
	 * 
	 */
	private boolean testWithEncryption = false;
	
	@Before
	public void setup() throws InvalidBEncodingException, FileNotFoundException, IOException, InterruptedException {
		System.out.println("[DataUploadTest] Launching the tracker and the client");
		
		// Start the tracker
		trackerProcess = TestUtils.jarStarter(new String[]{"tracker.jar"});
		TestUtils.waitForServerSocket(6969);

		Torrent petitPrinceUp = new Torrent("data/LePetitPrince.torrent", new File("data/st_exupery_le_petit_prince.pdf"));
		Thread.sleep(200L);
		assertTrue("File did load properly", petitPrinceUp.isComplete());

		//new Upload().run(petitPrinceUp, true, testWithEncryption);
		
		Thread.sleep(1000L);
		
		// The uploader should be started by now, lets run the download !
		downloaderProcess = TestUtils.jarStarter(new String[]{"downloader.jar", testWithEncryption ? "--enableEncryption" : "--disableEncryption"});
		downloaderStdOut = new BufferedReader(new InputStreamReader(downloaderProcess.getInputStream()));
	}

	@Test
	public void testUpload() throws IOException {
		if (testWithEncryption)
			Assert.assertTrue(TestUtils.isEncryptedAndCompleted(downloaderStdOut));
		else
			Assert.assertTrue(TestUtils.isClearAndCompleted(downloaderStdOut));
	}
	
	@After
	public void destroyProcesses() {
		System.out.println("removing remaining processes...");
		
		if (trackerProcess != null)    trackerProcess.destroy();
		if (downloaderProcess != null) downloaderProcess.destroy();
	}

}
