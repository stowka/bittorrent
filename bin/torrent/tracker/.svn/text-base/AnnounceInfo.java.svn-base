package torrent.tracker;

import java.util.List;

import torrent.peer.Peer;

/**
 * AnnounceInfo est une classe dont le but est de stocker
 * les informations de la reponse à la requête HTTP-GET
 * du Torrent. La classe TrackerInfo lui envoie ces
 * parametres.
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 */
public class AnnounceInfo {
	private int interval;
	private int minInterval;
	private String failureReason;
	private List<Peer> peers;
	

	/**
	 * 1er constructeur AnnounceInfo si présence du paramètre
	 * min interval.
	 * @param peerList : représente la liste de pairs du torrent.
	 * @param interval : (int) intervalle à respecter entre
	 * 					 2 demandes aux pairs.
	 * @param minInterval : (int) intervalle minimum à respecter entre 
	 * 						2 demandes aux pairs.
	 */

	public AnnounceInfo(List<Peer> peerList, int interval, int minInterval) {
		this.interval = interval;
		this.minInterval = minInterval;
		this.peers = peerList;
	}
	

	/**
	 * 2eme constructeur AnnounceInfo si absence de min interval.
	 * On initialise donc minInterval à 0 : pas d'intervalle minimum.
	 * @param peerList : représente la liste de pairs du torrent auquel
	 * 					 le client va se connecter..
	 * @param interval : (int) intervalle à respecter entre
	 * 					 2 requetes du  client au tracker.
	 */

	public AnnounceInfo(List<Peer> peerList, int interval) {
		this.interval = interval;
		this.peers = peerList;
		this.minInterval = 0;
	}
	
	/**
	 * Donne une valeur a failureReason
	 * @param failureReason : String raison d'un echec
	 */
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	/**
	 * On envoie l'intervalle entre deux demandes.
	 * @return interval : int
	 */
	public int getInterval() {
		return interval;
	}

	
	/**
	 * Si minInterval est absent on l'a initilisé à 0
	 * l'entier retourné ne correpond à rien concretement.
	 * @return minInterval : int
	 */

	public int getMinInterval() {
		return minInterval;
	}

	
	/**
	 * On envoie la raison d'un échec.
	 * @return failureReason : String
	 */


	public String getFailureReason() {
		return failureReason;
	}


	public List<Peer> getPeers() {
		return peers;
	}
}
