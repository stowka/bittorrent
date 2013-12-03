package http;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import torrent.tracker.TorrentHash;

/**
 * Classe pour envoyer une requete HTTP GET
 * a partir du torrent dont les informations
 * sont recueillies puis envoyees par TrackerInfo.
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 */
public class HTTPGet {
	private String adresse;
	private String chemin;
	private int port;
	private URL URLAnnounce;

	/**
	 * Constructeur de HTTPGet.
	 * L'adresse URL fournie par le torrent est
	 * importee dans la classe ce qui permet d'initialiser
	 * port, adresse et chemin.
	 * @param URLAnnounce String contenant la
	 * concaténation de : adresse + chemin + port du torrent.
	 * 
	 * 
	 * port : (int) port par lequel on peut se connecter
	 * en utilisant le socket.
	 * 
	 * adresse : (String) adresse internet toujours pour se
	 * connecter à travers un socket.
	 * 
	 * chemin : (String) sert à lancer la requête HTTP-GET
	 * afin de la diriger.
	 */
	public HTTPGet(String URLAnnounce) {
		try {
			this.URLAnnounce = new URL(URLAnnounce);
		} catch (MalformedURLException e) {
			System.out.println("Mauvaise URL");
		}
		this.port = this.URLAnnounce.getPort();
		this.adresse = this.URLAnnounce.getHost();
		this.chemin = this.URLAnnounce.getFile();
	}

	/**
	 * La methode get() permet d'envoyer une requete
	 * HTTP-Get : elle reçoit les parametres necessaires
	 * de TrackerInfo.
	 * La réponse de la requete (tableau de bytes) est
	 * envoyee directement a TrackerInfo qui va la decrypter.
	 * @param info_hash : String signature digitale du fichier Metainfo produite par l'algorithme SHA1
	 * @param peer_id : String identifiant client unique
	 * @param compact : int egal à 1 ou 0
	 * @param numwant : nombre voulu de pairs; si le nombre 
	 * n'est pas defini comme il faut, il est à 50 par defaut.
	 * @param event : String "started" dans la 1ere requete
	 * @param trackerid : String identifiant envoye par le tracker
	 * @param left : int le nombres de bytes que le client doit encore telecharger
	 * @param potLoc : int numero de port sur lequel le client accepte les connexions
	 * 
	 * @return la séquence de byte de la réponse de la requête GET
	 */
	public byte[] get(String info_hash, String peer_id, int compact, int numwant, String event, String trackerid,int left, int portLoc) {
		InputStream inst = null;
		System.out.println(adresse);
		ByteArrayOutputStream outs = null;
		Socket socket;
		BufferedWriter os = null;
		if (numwant <= 0) {
			numwant = 50;
		}
		byte[] seq = null;
		try {
			socket = new Socket(adresse, port);
			os = new BufferedWriter(
					new OutputStreamWriter(
							(socket.getOutputStream())));
			
			inst = socket.getInputStream();
			outs = new ByteArrayOutputStream();
			//Chaine de caractère de la requête
			String query = "GET " + chemin + 
							"?event=" + event + 
							"&info_hash=" + info_hash + 
							"&peer_id=" + peer_id + 
							"&compact=" + compact + 
							"&numwant=" + numwant + 
							"&trackerid=" + trackerid + 
							"&port=" + portLoc +
							"&left=" + left +
							" HTTP/1.0\n\r\n\r\n";
			
			//Affichage de la requête (info sur le bon déroulement du programme)
			System.out.println("Requête : \n" + query);
	
			TorrentHash queryEncoded = new TorrentHash(query);
			os.write(queryEncoded.urlEncoded());
			os.flush();
			//On ignore l'en-tête HTTP et les deux
			//sauts de ligne
			boolean finHeader = false;
			while (!finHeader) {
				if(inst.read() == '\n') {
					if(inst.read() == '\r') {
						if(inst.read() == '\n') {
							finHeader = true;
						}
					}
				}
			}
			
			//Lire le reste 
			int read;
			while ((read = inst.read()) != -1) {
				outs.write(read);
			}
			seq = outs.toByteArray();
			
			String s = new String(seq);
			System.out.println(s);
			outs.close();
			inst.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Erreur fichier : " + e.toString());
			e.printStackTrace();
		}
		return seq;
	}
}
