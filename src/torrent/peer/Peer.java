package torrent.peer;

import java.net.InetAddress;

/**
 * CLASSE PEER : modelise un pair auquel le client va 
 * se connecter afin d'echanger des blocs de pieces de 
 * donnees dans le cadre du partage par bittorrent.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class Peer {
	private String id;
	private InetAddress ip;
	private int port;
	
	/**
	 * Constructeur de Peer, initialise les 3 variables le représentant. 2 envoyees en
	 * parametre et la id generee avec PeerIDGenerator.
	 * @param ip : InetAddress représente l'adresse IP du pair sous la forme d'un objet InetAdress
	 * @param port : int indique le port de connexion (par défaut 80)
	 */
	public Peer(InetAddress ip, int port) {
		this.setIp(ip);
		
		if (port != 0) {
			this.setPort(port);
		} else {
			this.setPort(80);
		}
		this.setId(PeerIDGenerator.generateID());
		
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
