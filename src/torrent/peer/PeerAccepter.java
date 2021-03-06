package torrent.peer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import settings.PeerSettings;
import torrent.Torrent;

/**
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
public class PeerAccepter extends Thread {
	ServerSocket s;
	Torrent torrent;
	PeerSettings ps;
	
	
	/**
	 * Constructeur de PeerAccepter, d�marre le processus qui fait du client
	 * un serveur socket sur lequel se connecter.
	 */
	public PeerAccepter(Torrent torrent, PeerSettings ps) {
		this.torrent = torrent;
		this.ps = ps;
		this.start();
	}
	
	public PeerAccepter(Torrent torrent) {
		this.torrent = torrent;
		System.out.println("new PeerAccepter");
		this.start();
	}
	
	/**
	 * methode run d'un Thread : cr�e le serveur socket.
	 */
	public void run() {
		try {
			s = new ServerSocket(0);
		}
		catch (IOException e) {
			System.out.println("IOException");
		}
		
		System.out.println(s.getLocalPort());
		
		
		while(true) {
			Socket serviceSocket;
			try {
				serviceSocket = s.accept();
				PrintStream output = new PrintStream(serviceSocket.getOutputStream());
				new PeerHandler(torrent, serviceSocket);
				output.println("SERVER");
				
				serviceSocket.close();
			} catch (IOException e) {
				System.out.println("IOException");
			}
		}
		
	}
}
