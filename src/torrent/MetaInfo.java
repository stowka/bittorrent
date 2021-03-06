package torrent;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import bencoding.BDecoder;
import bencoding.BEValue;

/**
 * CLASSE METAINFO : modelise les donnees metainfo caracterisant le fichier .torrent
 * Permet de renvoyer chacune des caracteristique par getters.
 * 
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
public class MetaInfo {
	private String author;
	private long time;
	private String comment;
	private String announce;
	private int length;
	private int pieceLength, lastPieceLength;
	private byte[] hashes;
	private byte[] infoHash;
	private String name;
	private List<BEValue> announceList;
	
	/**
	 * Ce constructeur decode le fichier metainfo et 
	 * stock chaque parametre (auteur, date de creation, taille etc.)
	 * dans des variables.
	 * 
	 * @param chemin :
	 * 				String contenant le chemin local du fichier metainfo
	 */
	public MetaInfo(String chemin) {
		try {
			//Torrent info
			BDecoder bdec = new BDecoder(new FileInputStream(chemin));
			BEValue dico = bdec.bdecodeMap();
			
			author = dico.getMap().get("created by").getString();
			time = dico.getMap().get("creation date").getLong();
			comment = dico.getMap().get("comment").getString();
			announce = dico.getMap().get("announce").getString();
			infoHash = bdec.getSpecialMapDigest();
			
			//Dico info
			BEValue dicoInfo = new BEValue(dico.getMap().get("info").getMap());
			pieceLength = dicoInfo.getMap().get("piece length").getInt();
			length = dicoInfo.getMap().get("length").getInt();
			hashes = dicoInfo.getMap().get("pieces").getBytes();
			name = dicoInfo.getMap().get("name").getString();
			
			lastPieceLength = (length % pieceLength);
			if (lastPieceLength == 0) {
				lastPieceLength = pieceLength;
			}
			if (dico.getMap().get("announce-list") != null) {
				announceList = dico.getMap().get("announce-list").getList();
				
			} else {
				announceList = null;
			}
		} catch (IOException e) {
			System.out.println("Erreur fichier");
		} catch (NullPointerException e) {
			System.out.println("metainfo pas aux normes");
		}
	}
	
	/**
	 * 
	 * @return author: le String contenant l'auteur du torrent.
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * 
	 * @return announceList: la liste de BEValue contenant la liste des trackers associes au torrent.
	 */
	public List<BEValue> getAnnounceList() {
		return announceList;
	}
	
	/**
	 * 
	 * @return time: le long de la date de creation du torrent.
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * 
	 * @return comment: le String contenant les commentaires a propos du torrent.
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * 
	 * @return announce: le String contenant l'adresse du tracker unique (s'il n'y en a qu'un seul).
	 */
	public String getAnnounce() {
		return announce;
	}
	
	/**
	 * 
	 * @return pieceLength: l'entier de la longueur des pieces (sauf de la derniere)
	 */
	public int getPieceLength() {
		return pieceLength;
	}
	
	/**
	 * 
	 * @return lastPieceLength: l'entier de la longueur de la derniere piece 
	 * 							(qui peut etre plus petite que les autres)
	 */
	public int getLastPieceLength() {
		return lastPieceLength;
	}
	
	/**
	 * 
	 * @return hashes: le tableau de byte modelisant les pieces.
	 */
	public byte[] getHashes() {
		return hashes;
	}
	
	/**
	 * 
	 * @return length: l'entier de la longueur totale du fichier associe au torrent.
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * 
	 * @return name: le String contenant le nom du fichier associe au torrent.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return infoHash: le tableau de byte de la signature digitale 
	 * 					 du fichier Metainfo produite par l'algorithme SHA1. 
	 * 					 Cette signature sur 20 bytes est url-encod�e 
	 */
	public byte[] getInfoHash() {
		return infoHash;
	}
}
