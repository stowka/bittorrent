package torrent.tracker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import torrent.peer.Peer;
import torrent.peer.PeerIDGenerator;

import bencoding.BDecoder;
import bencoding.BEValue;
import http.HTTPGet;

/**
 * CLASSE TRACKERINFO : realise une requette HTTPGET grace a la liste des trackers
 * traite la reponse (liste des pairs) puis la stocke dans un ANNOUNCEINFO.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class TrackerInfo {
	private String info_hash;
	private String peer_id;
	private int left;
	private int compact;
	private int portLoc;
	private String event;
	private String trackerid;
	private HTTPGet query;
	private AnnounceInfo announceInfo;

	private byte[] reponse;

	/**
	 * Constructeur de trackerInfo, initialise les variables
	 * @param info_hash : byte[] signature digitale du fichier MetaInfo
	 * @param URLAnnounce : String permet de lancer une requete HTTPGET
	 * @param left : int nombre de bytes que le client doit encore telecharger
	 * @param portLoc : int port sur lequel le client va accepter la connexion
	 */
	public TrackerInfo(String URLAnnounce, byte[] info_hash, int left,
			int portLoc) {
		this.compact = 1;
		this.portLoc = portLoc;
		this.left = left;
		TorrentHash info_hashEncoded = new TorrentHash(info_hash);
		this.info_hash = info_hashEncoded.urlEncoded();
		this.peer_id = PeerIDGenerator.generateID();
		TorrentHash peer_idEncoded = new TorrentHash(peer_id);
		this.peer_id = peer_idEncoded.urlEncoded();

		this.event = "started";
		this.query = new HTTPGet(URLAnnounce);
	}

	/**
	 * Lance la requete HTTPGET et cree un objet AnnounceInfo pour stocker les donnees de la reponse
	 * @param numwant : int nombre maximal de pairs que le client souhaite connecter
	 */

	public AnnounceInfo announce(int numwant) {
		// Réponse de la requête HTTPGet sous la forme d'un tableau de byte
		reponse = query.get(info_hash, peer_id, compact, numwant, event,
				trackerid, left, portLoc);

		// Traitement de la réponse
		// BEValue decod = new BEValue(reponse);

		byte[] peerByte;
		byte[] ip = new byte[4];
		int interval, minInterval;
		String failureReason = "";
		int portInt = 0;
		List<Peer> peerList = new ArrayList<Peer>();
		Peer peer = null;
		BDecoder bdec = new BDecoder(new ByteArrayInputStream(reponse));
		announceInfo = null;

		try {
			BEValue dico = bdec.bdecodeMap();
			peerByte = dico.getMap().get("peers").getBytes();
			interval = dico.getMap().get("interval").getInt();
			if (dico.getMap().get("failure reason") == null) {
				System.out.println("No failure");
			} else {
				failureReason = dico.getMap().get("failure reason").getString();
			}
			for (int i = 0, l = 0; i < peerByte.length; i += 6, l++) {
				portInt = 0;
				// Stocker chaque adresse IP dans un byte array temp
				for (int j = 0; j < 4; j++) {
					ip[j] = peerByte[i + j];
				}
				// Stocker chaque port dans un byte array temp - transformer le
				// byte array en String
				//portInt = (((int)peerByte[i + 4]) << 8) & 0xFF00 + ((int)peerByte[i + 5] & 0xFF);
				portInt = ((int)peerByte[i + 4] & 0xff) << 8;
				portInt += peerByte[i + 5] & 0xff;
				try {
					// Conversion de l'IP en objet InetAddress
					InetAddress ipAddress = InetAddress.getByAddress(ip);
					peer = new Peer(ipAddress, portInt);
					// Ajout de l'IP et du port à la liste de Peer
					peerList.add(peer);

				} catch (UnknownHostException e) {
					System.out.println("TrackerInfo : Unknown host : " + e.toString());
				} catch (NullPointerException e) {
					System.out.println("TrackerInfo : Null Pointer exception : "
							+ e.toString());
					e.printStackTrace();
				}
			}

			try {
				minInterval = dico.getMap().get("min interval").getInt();
				announceInfo = new AnnounceInfo(peerList, interval, minInterval);
				announceInfo.setFailureReason(failureReason);
			} catch (NullPointerException e) { // Si minInterval n'existe pas,
												// création de l'objet
												// AnnounceInfo avec le 2nd
												// constructeur
				announceInfo = new AnnounceInfo(peerList, interval);
				announceInfo.setFailureReason(failureReason);
			}

		} catch (IOException e) {
			System.out.println("TrackerInfo : Erreur byte[] : " + e.toString());
			e.printStackTrace();
		}

		return announceInfo;
	}
	
	public byte[] getPeerId() {
		return new TorrentHash(peer_id).binaryHash();
	}
}
