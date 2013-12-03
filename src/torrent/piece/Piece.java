package torrent.piece;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.lang.IllegalArgumentException;

import torrent.messages.Request;
import torrent.peer.PeerHandler;

/**
 * CLASSE Piece modelise une piece de donnees
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class Piece {
	private byte[] data;
	private int size;
	private byte[] sum;
	private int index;
	private boolean[] complete;
	private boolean checked;
	private HashMap<Request, List<PeerHandler>> list;
	private int lastBlockSize;
	private int nbBlock;

	public final static int BLOCK_SIZE = 1 << 14;

	/**
	 * CONSTRUCTEUR DE LA PIECE
	 * 
	 * @param index
	 *            : int index de la pice
	 * @param size
	 *            : taille du tableau data (donnŽes)
	 * @param sum
	 *            : somme de controle arithmetique (SHA-1)
	 */
	public Piece(int index, int size, byte[] sum) {
		this.setIndex(index);
		this.size = size;
		this.nbBlock = (int) Math.ceil((double) size / BLOCK_SIZE);
		this.sum = sum;
		this.data = null;
		this.complete = new boolean[nbBlock];
		this.list = new HashMap<Request, List<PeerHandler>>();
		this.checked = false;
		this.lastBlockSize = (int) (size - (nbBlock - 1) * BLOCK_SIZE);
	}

	/**
	 * Calcule le pourcentage de telechargement de la piece
	 * @return int : downloadCompleteness
	 */
	public int getDownloadCompleteness() {
		int nbBlocksDownloaded = 0;
		for (int i = 0; i < complete.length; i++) {
			if (complete[i]) {
				nbBlocksDownloaded++;
			}
		}
		return (100 * nbBlocksDownloaded) / nbBlock;
	}

	/**
	 * retourne data[] si quelquechose a ete telecharge sinon retourne null
	 * @return byte[] data
	 */
	public byte[] getData() {
		for (int i = 0; i < complete.length; i++) {
			if (complete[i]) {
				return data;
			}
		}
		return null;
	}

	/**
	 * Methode pour changer les donnees stockees dans data et de le remplacer
	 * par de nouvelles envoyees en parametre.
	 * 
	 * @param data
	 *            : byte[] tableau de donnees pour une piece
	 */
	public void setData(byte[] data) {
		this.data = data.clone();
		for (int i = 0; i < complete.length; i++) {
			complete[i] = true;
		}
	}

	/**
	 * METHODE POUR REMPLIR LE TABLEAU DE BYTES AVEC LES BLOCKS LANCE LA METHODE
	 * CHECK POUR TESTER LE REMPLISSAGE
	 * initialise data ici pour economiser de la memoire.
	 * 
	 * @param begin
	 *            : int index du block reu
	 * @param block
	 *            : byte[] block de donnees reu
	 */
	public void feed(int begin, byte[] block) {
		if (data == null) {
			data = new byte[size];
		}
		System.out.println("piece index : " + index + " block : " + begin
				+ " block.length : " + block.length);
		if (begin < 0 || begin > size || block.length > BLOCK_SIZE) {
			throw new IllegalArgumentException();
		}
		//feed le dernier block
		if (begin == nbBlock - 1) {
			for (int i = 0; i < block.length; i++) {
				data[begin + i] = block[i];
			}
			complete[begin / BLOCK_SIZE] = true;
		} else if (block.length != BLOCK_SIZE) {
			throw new IllegalArgumentException();
		} else {
			// feed un block normal
			for (int i = 0; i < block.length; i++) {
				data[begin + i] = block[i];
			}
			complete[begin / BLOCK_SIZE] = true;
		}
		if (getDownloadCompleteness() == 100) {
			check();
			System.out.println("pice 100 % tŽlŽchargŽe");
		}
		
		//supprime les requetes pendantes sur ce block dans tous les PeerHandler
		if (this.isComplete()) {
			Request req = null;
			List<PeerHandler> listPh = null;
			Set<Request> requests = list.keySet();
			
			for (Request r : requests) {
				if (r.getBegin() == begin) {
					req = r;
					listPh = list.get(r);
					break;
				}
			}
			
			if (listPh == null) {
				listPh = new ArrayList<PeerHandler>();
			}
			
			for (int i = 0; i < listPh.size(); i++) {
				listPh.get(i).getQueueMessage().remove(req);
				
				if (getChecked()) {
				listPh.get(i).getPieceManager().getPiecesOfInterest().remove(this);
				}
			}
		}
		
		
		
	}

	/**
	 * METHODE DE VERIFICATION FIN DE DOWNLOAD
	 * 
	 * @return renvoie true si le telechargement est fini : c'est a dire si la
	 *         methode getDownloadCompleteness renvoie 100.
	 */
	public boolean isComplete() {
		return (getDownloadCompleteness() == 100);
	}

	/**
	 * METHODE DE VERIFICATION DE LA SOMME DE CONTROLE verifie d'abord que le
	 * telechargement est complet puis passe un boolean a true si les sommes de
	 * controle sont egales.
	 * 
	 * @return renvoie true si la somme de controle est juste (l'integritŽ est
	 *         conservŽe)
	 */
	public boolean check() {
		if (checked) {
			
			return true;
		}
		if (isComplete()) {
			try {
				byte[] hash = MessageDigest.getInstance("SHA").digest(data);
				if (MessageDigest.isEqual(hash, sum)) {
					checked = true;
					return true;
				} else {
					for (int i = 0; i < complete.length; i++) {
						data[i] = 0;
						complete[i] = false;
					}
					return false;
				}
			} catch (NoSuchAlgorithmException e) {
				throw new Error("No SHA support in this VM.");
			}
		}

		return false;
	}

	/**
	 * permet au PieceManager de choisir un block dans la piece tel que
	 * le block soit celui avec le moins de requetes possibles.
	 * @return int : begin du block
	 */
	public int BlockSelection() {
		int nb = Integer.MAX_VALUE;
		int block = 0;

		Set<Request> listRequest = list.keySet();

		for (int i = 0; i < nbBlock; i++) {
			for (Request req : listRequest) {
				if (req.getBegin() == i) {
					if (nb > list.get(req).size()) {
						block = i * BLOCK_SIZE;
						nb = list.get(req).size();
					}
				} else if (complete[i] == false) {
					return i * BLOCK_SIZE;
				}
			}
		}
		return block;
	}

	public int getLength() {
		return size;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public boolean getChecked() {
		return checked;
	}

	public int getBlockSize() {
		return BLOCK_SIZE;
	}

	public int getLastBlockSize() {
		return lastBlockSize;
	}

	public HashMap<Request, List<PeerHandler>> getList() {
		return list;
	}

	public int getNbBlock() {
		return nbBlock;
	}
	
	public byte[] getSum() {
		return sum;
	}

}
