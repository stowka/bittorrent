package torrent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bencoding.BEValue;
import bencoding.InvalidBEncodingException;

import torrent.peer.Peer;
import torrent.peer.PeerAccepter;
import torrent.peer.PeerHandler;
import torrent.piece.Piece;
import torrent.piece.PieceManager;
import torrent.tracker.AnnounceInfo;
import torrent.tracker.TrackerInfo;

/**
 * CLASSE TORRENT : modelise un torrent provenant d'un fichier metainfo.
 * Consigne la liste de pairs que notre client peut connecter. Vérifie la
 * présence du fichier associé au torrent sur le disque dur. Permet d'écrire le
 * fichier téléchargé sur le disque dur.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 * 
 */
public class Torrent {
	private boolean filePresent, written, writingEnabled;
	private MetaInfo metainfo;
	private int port;
	private int numwant; // nombre de trackers - à remplacer par les infos de
							// l'URLAnnounce
	private byte[] infoHash;
	private List<Peer> peers;
	private List<PeerHandler> phList;
	private List<Piece> pieces;
	private List<TrackerInfo> trackerInfos;
	private byte[] peerId;
	private TrackerInfo trackerInfo;
	private AnnounceInfo announceInfo;
	private PieceManager pieceManager;
	private List<BEValue> announceList;
	private String savingPath;

	/**
	 * 
	 * @param cheminMetaInfo
	 *            : String contenant le chemin local du fichier metainfo.
	 * @param port
	 *            : Int contenant le port de connexion associe au torrent.
	 * @param file
	 *            : Objet File medelisant le fichier.
	 */
	public Torrent(String cheminMetaInfo, int port, File file) {
		this(cheminMetaInfo, port);
	}

	/**
	 * 
	 * @param cheminMetaInfo
	 *            : String contenant le chemin local du fichier metainfo.
	 * @param file
	 *            : Objet File medelisant le fichier.
	 */
	public Torrent(String cheminMetaInfo, File file) {
		this(cheminMetaInfo, 6881 + (int) (Math.random() * 30000), file);
	}
	
	/**
	 * 
	 * @param cheminMetaInfo : String, chemin du fichier .torrent
	 * @param savingPath : String, chemin d'enregistrement du téléchargement
	 */
	public Torrent(String cheminMetaInfo, String savingPath) {
		this(cheminMetaInfo, 6881 + (int) (Math.random() * 30000));
		this.savingPath = savingPath;
		this.filePresent = readFromFile();
		System.out.println(savingPath);
	}

	/**
	 * 
	 * @param cheminMetaInfo
	 *            : String contenant le chemin local du fichier metainfo.
	 */
	public Torrent(String cheminMetaInfo) {
		this(cheminMetaInfo, 6881 + (int) (Math.random() * 30000));
	}

	/**
	 * 
	 * @param cheminMetaInfo
	 *            : String contenant le chemin local du fichier metainfo.
	 * @param file
	 *            : Objet File medelisant le fichier.
	 * @param writingEnabled
	 *            : Boolean indiquant si le fichier est fini de téléchargé et
	 *            s'il est possible de l'ecrire sur le disque dur.
	 */
	public Torrent(String cheminMetaInfo, File file, boolean writingEnabled) {
		this(cheminMetaInfo, 6881 + (int) (Math.random() * 30000), file);
		this.writingEnabled = writingEnabled;
	}

	/**
	 * 
	 * @param cheminMetaInfo
	 *            : String contenant le chemin local du fichier metainfo.
	 * @param port
	 *            : Int contenant le port de connexion associe au torrent.
	 */
	public Torrent(String cheminMetaInfo, int port) {
		System.out.println("new torrent");
		this.savingPath = System.getProperty("user.home") + File.separator + "Downloads"
				+ File.separator;
		this.metainfo = new MetaInfo(cheminMetaInfo);
		this.announceList = metainfo.getAnnounceList();
		this.trackerInfos = new ArrayList<TrackerInfo>();
		this.written = false;
		this.port = port;
		this.peers = new ArrayList<Peer>();
		this.phList = new ArrayList<PeerHandler>();
		this.pieces = new ArrayList<Piece>();
		this.infoHash = metainfo.getInfoHash();
		this.writingEnabled = true;
		
		this.addTrackers(); // remplissage de trackerInfos
		
		byte[] hashes = metainfo.getHashes();
		for (int i = 0, j = 0; i < hashes.length; i += 20, j++) {
			byte[] sum = Arrays.copyOfRange(hashes, i, i + 20);
			if (i + 20 == hashes.length) {
				pieces.add(new Piece(j, metainfo.getLastPieceLength(), sum));
			} else {
				pieces.add(new Piece(j, metainfo.getPieceLength(), sum));
			}
		}
		this.filePresent = readFromFile();
		this.pieceManager = new PieceManager(this, filePresent);
	}

	/**
	 * Remplit la liste de trackers
	 */
	public void addTrackers() {
		try {
			if (announceList != null) {
				for (int i = 0; i < announceList.size(); i++) {
					if (announceList.get(i).getList().get(0).getString()
							.contains("udp://")) {
						announceList.remove(i);
					}
					trackerInfo = new TrackerInfo(announceList.get(i).getList()
							.get(0).getString(), infoHash, (40 * 32 * 1024),
							port);
					trackerInfos.add(trackerInfo);
				}
			} else {
				String announce = metainfo.getAnnounce();
				trackerInfo = new TrackerInfo(announce, infoHash,
						(40 * 32 * 1024), port);
				trackerInfos.add(trackerInfo);
			}
		} catch (InvalidBEncodingException e) {
			System.out.println("Erreur de bencoding");
		}
	}
	
	/**
	 * 
	 * @return : true si l'integrite de toutes les pieces est verifiee. false
	 *         des que l'une des pieces n'est pas correcte.
	 */
	public boolean isChecked() {
		for (Piece p : pieces) {
			if (!p.check()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param writingEnabled
	 *            : Boolean indiquant si le fichier est pret a etre ecrit sur le
	 *            disque dur.
	 */
	public void setWritingEnabled(boolean writingEnabled) {
		this.writingEnabled = writingEnabled;// fonctionne mais non adapté aux tests unitaires
	}

	/**
	 * Methode qui verifie si le fichier est deja ecrit sur le disque dur et
	 * s'il est integre.
	 * 
	 * 
	 * @return : true si le fichier existe deja et s'il est complet. false si le
	 *         fichier n'existe pas, ou si le fichier existe mais qu'il n'est
	 *         pas integre.
	 */
	public boolean readFromFile() {
		byte[] data;
		File fichier = new File(System.getProperty("user.home"), "Downloads"
				+ File.separator + metainfo.getName());
		if (!fichier.exists()) {
			return false;
		} else {
			written = true;
			FileInputStream fis = null;
			DataInputStream dis = null;
			Piece p = null;
			try {
				fis = new FileInputStream(fichier);
				dis = new DataInputStream(fis);
				for (int i = 0; i < pieces.size(); i++) {
					if (i == pieces.size() - 1) {
						data = new byte[metainfo.getLastPieceLength()];
					} else {
						data = new byte[metainfo.getPieceLength()];
					}
					p = pieces.get(i);
					dis.readFully(data);
					p.setData(data);
				}
			} catch (FileNotFoundException e) {
				System.out.println("Fichier introuvable");
			} catch (IOException e) {
				System.out.println("Erreur fichier");
			}
			return true;
		}
	}

	/**
	 * Methode qui ecrit le fichier telecharge une fois qu'il est complet et
	 * integre.
	 * 
	 * @param file
	 *            : Objet File medelisant le fichier.
	 * 
	 */
	public void writeToFile() {
		System.out.println("write to file");
		if (!writingEnabled) {
			return;
		}
		String chemin = savingPath;
		String name = metainfo.getName();
		System.out.println("chemin : " + chemin + "\nname : " + name);
		File fichier = new File(chemin + name);
		System.out.println("chemin réel: " + fichier.getPath());
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					fichier));
			for (int i = 0; i < pieces.size(); i++) {
				dos.write(pieces.get(i).getData());
			}
			written = true;
			dos.close();

		} catch (FileNotFoundException e) {
			System.out.println("Fichier introuvable");
			written = false;
			return;
		} catch (IOException e) {
			System.out.println("Erreur fichier");
			written = false;
			return;
		}
	}

	/**
	 * 
	 * Cree pour chaque tracker un objet TrackerInfo associe a un objet
	 * AnnounceInfo permettant d'envoyer la requete HTTPGet
	 * 
	 */
	public void massAnnounce(int maxPeers) {
		System.out.println("Torrent : massAnnounce");
		if (!this.isComplete()) {
			for (int i = 0; i < trackerInfos.size(); i++) {
				peerId = trackerInfo.getPeerId();
				announceInfo = trackerInfo.announce(numwant);
				peers.addAll(announceInfo.getPeers());
				for (int j = 0; j < peers.size() && j < maxPeers; j++) {
					this.phList.add(new PeerHandler(this, peers.get(i)));
				}
			}
		} else {
			new PeerAccepter(this);
		}
	}

	/**
	 * 
	 * @return pieces: la liste de pieces.
	 */
	public List<Piece> getPieces() {
		return pieces;
	}

	/**
	 * 
	 * @return metainfo: l'objet MetaInfo contenant toutes les caracteristiques
	 *         du torrent.
	 */
	public MetaInfo getMetaInfo() {
		return metainfo;
	}

	/**
	 * 
	 * @return trackersInfo: la liste des trackers.
	 */
	public List<TrackerInfo> getTrackerInfos() {
		return trackerInfos;
	}

	/**
	 * 
	 * @return pieceManager: le gestionnaire de pieces associe au torrent.
	 */
	public PieceManager getPieceManager() {
		return pieceManager;
	}

	/**
	 * 
	 * @return : true si le fichier est ecrit. false si le fichier n'est pas
	 *         ecrit.
	 */
	public boolean isWritten() {
		return written;
	}

	/**
	 * 
	 * @return : le "double" pourcentage de telechargement des pieces.
	 */
	public double getCompleteness() {
		double moyenne = 0.0;
		for (Piece p : pieces) {
			moyenne += p.getDownloadCompleteness();
		}
		moyenne = moyenne / pieces.size();
		return moyenne;
	}

	/**
	 * 
	 * @return : la taille de la liste de pieces (le nombre de pieces).
	 */
	public int getPieceCount() {
		return pieces.size();
	}

	/**
	 * 
	 * @return infoHash: le tableau de byte de la signature digitale du fichier
	 *         Metainfo produite par l'algorithme SHA1.
	 */
	public byte[] getInfoHash() {
		return infoHash;
	}

	/**
	 * 
	 * @return true si le téléchargement est fini (= 100%).
	 */
	public boolean isComplete() {
		if (this.getCompleteness() == 100) {
			if (!written) {
				this.writeToFile();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @return : le tableau de byte du l'id des pairs.
	 */
	public byte[] getPeerId() {
		return peerId;
	}

	/**
	 * 
	 * @return : int, le port de connexion.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 
	 * @param savingPath
	 *            , String chemin d'enregistrement du fichier tééléchargé.
	 */
	public void setSavingPath(String savingPath) {
		this.savingPath = savingPath;
	}

	/**
	 * 
	 * @return peers, le liste des pairs qui se connectent au torrent.
	 */
	public List<Peer> getPeers() {
		return peers;
	}
	
	/**
	 * 
	 * @return la liste de PeerHandler
	 */
	public List<PeerHandler> getPeerHandlers() {
		return phList;
	}
	
	/**
	 * 
	 * @return le chemin d'enregistrement des téléchargement.
	 */
	public String getSavingPath() {
		return savingPath;
	}
}