package torrent.piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import torrent.Torrent;
import torrent.messages.Request;
import torrent.peer.PeerHandler;

/**
 * CLASSE PIECEMANAGER : modelise une gestionnaire de piece qui sera cree par
 * l'objet Torrent afin de gerer le priorite de telechargement de chaque piece
 * que l'on a besoin de telecharger.
 * 
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 * 
 */
public class PieceManager extends Thread {
	private Torrent torrent;
	private final int N = 100;
	private List<Piece> piecesOfInterest;
	private ArrayList<Piece> allPieces;
	private double avancement;
	private boolean filePresent, complete, loading, stop;

	/**
	 * 
	 * @param allPieces
	 *            : Liste de toutes les pieces du fichier.
	 * @param filePresent
	 *            : Boolean indiquant si le fichier est deja present
	 *            (telecharge, verifie).
	 */
	public PieceManager(Torrent torrent, boolean filePresent) {
		this.torrent = torrent;
		this.loading = false;
		this.stop = false;
		this.filePresent = filePresent;
		this.piecesOfInterest = new ArrayList<Piece>();
		this.allPieces = new ArrayList<Piece>();
		this.allPieces.addAll(this.torrent.getPieces());
		this.complete = false;
		this.avancement = 0.0;
	}

	/**
	 * processus qui lance updatePriorities (MàJ des piecesofInterest) toutes
	 * les 3 secondes.
	 * 
	 */
	public void run() {
		if (torrent.isComplete()) {
			System.out.println("PieceManager : Telechargement deja fini");
			loading = false;
			stopIt();
		} else {
			loading = true;
		}
		while (!stop) {
			while (loading) {
				if (torrent.isComplete()) {
					System.out.println("PieceManager : Telechargement fini");
					loading = false;
					stopIt();
				} else {
					updatePriorities();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.out.println("Interrupted");
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return piecesOfInterest: la liste des pieces qui ne sont pas
	 *         telechargees que le gestionnaire de pieces met a jour
	 *         regulierement en generant aleatoirement certaines d'entre elles.
	 */
	public List<Piece> updatePriorities() {
		if (filePresent) {
			piecesOfInterest.removeAll(piecesOfInterest);
			System.out.println("PieceManager : updatePriorities : Téléchargement deja fini");
		} else if (allPieces.size() != 0) {
			Collections.shuffle(allPieces);
			if (piecesOfInterest.size() != 0) {
				piecesOfInterest.removeAll(piecesOfInterest);
			}
			for (int i = 0; i < allPieces.size() && i < N; i++) {
				piecesOfInterest.add(allPieces.get(i));
			}
			System.out
					.println("PieceManager : updatePriorities : allPieces.size : "
							+ allPieces.size());
			System.out
					.println("PieceManager : updatePriorities : piecesOfInterestsize : "
							+ piecesOfInterest.size());
			if (torrent.isComplete()) {
				System.out
						.println("PieceManager : updatePriorities : Telechargement fini");
				complete = true;
				avancement = 100;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			if (torrent.isComplete()) {
				System.out
						.println("PieceManager : updatePriorities : Telechargement fini");
				complete = true;
				avancement = 100;
			}
		}
		return piecesOfInterest;
	}

	/**
	 * 
	 * @return complete: boolean indiquant si le fichier est complet (true).
	 */
	public boolean getComplete() {
		return complete;
	}

	/**
	 * 
	 * @return avancement: le "double" indiquant l'avancement du telechargement.
	 */
	public double getAvancement() {
		return avancement;
	}

	/**
	 * etablit la liste des pieces communes à notre client et le pair puis lance choicePiece()
	 * pour obtenir la pièce avec le moins de requêtes.
	 * @param list : liste des index auxquel le pair possede des pieces.
	 * @return Piece : la piece choisie pour être téléchargée
	 */
	public Piece receivePiece(List<Integer> list) {
		List<Piece> listCommonPieces = new ArrayList<Piece>();
		System.out
				.println("PieceManager : receivePiece : piecesOfInterest size : "
						+ piecesOfInterest.size());
		for (int i = 0; i < piecesOfInterest.size(); i++) {
			for (int j = 0; j < list.size(); j++) {
				if (piecesOfInterest.get(i).getIndex() == list.get(j)) {
					listCommonPieces.add(piecesOfInterest.get(i));
				}
			}
		}
		try {
			Piece p = choicePiece(listCommonPieces);
			return p;
		} catch (NullPointerException e) {
			return null;
		}
		
	}

	/**
	 * Choisit la piece avec le moins de requêtes
	 * @param commonPieces listes communes entre notre client et le pair
	 * @return piece choisie pour receivePiece()
	 */
	public Piece choicePiece(List<Piece> commonPieces) {
		Set<Request> listRequest;
		int sumOfPh = 0;
		int nb = Integer.MAX_VALUE;
		Piece interestPiece = null;

		for (Piece p : commonPieces) {
			listRequest = p.getList().keySet();
			if (listRequest.size() != 0) {
				for (Request req : listRequest) {
					List<PeerHandler> ph = p.getList().get(req);
					sumOfPh += ph.size();
				}

				sumOfPh = sumOfPh / p.getNbBlock();

				if (sumOfPh < nb) {
					nb = sumOfPh;
					interestPiece = p;
				}
				sumOfPh = 0;
			} else {
				return p;
			}
		}
		return interestPiece;
	}

	/**
	 * 
	 * @return allPieces.size : taille de allPieces
	 */
	public int getAllPiecesSize() {
		return allPieces.size();
	}

	/**
	 * 
	 * @return piecesOfInterest : liste des pieces que l'on veut télécharger
	 */
	public List<Piece> getPiecesOfInterest() {
		return piecesOfInterest;
	}

	/**
	 * 
	 * @param loading : boolean pour mettre en pause/ reprendre
	 */
	public void setLoading(boolean loading) {
		this.loading = loading;
		if (loading) {
			this.start();
		}
	}
	
	/**
	 * arrête tout le processus
	 */
	public void stopIt() {
		this.loading = false;
		this.stop = true;
	}
}
