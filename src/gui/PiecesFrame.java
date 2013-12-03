package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import torrent.Torrent;
import torrent.piece.Piece;

/**
 * CLASSE PIECEFRAME : modelise un panel ou chaque piece du torrent est representee par une couleur
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
@SuppressWarnings("serial")
public class PiecesFrame extends JPanel implements Runnable {
	private int piecesNb, piecesByCase;
	private List<Piece> pieces;
	private boolean loading;
	private Torrent t;
	
	/**
	 * Constructeur du panel
	 * @param t : fichier torrent
	 */
	public PiecesFrame(Torrent t) {
		this.t = t;
		this.pieces = new ArrayList<Piece>();
		this.pieces.addAll(t.getPieces());
		this.piecesNb = pieces.size();
		System.out.println("PIECES : " + piecesNb);
		this.setLayout(new GridLayout(1, piecesNb));
		
		this.setVisible(true);
		this.setOpaque(true);
		this.setBackground(Color.darkGray);
		update();
		validate();
		this.loading = true;
		this.start();
	}

	/**
	 * run du thread met a jour les couleurs des pieces
	 */
	@Override
	public void run() {
		while(loading) {
			this.removeAll();
			updateCases();
			validate();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (t.getCompleteness() == 100) {
				loading = false;
				this.removeAll();
				updateCases();
				validate();
			}
		}
	}
	
	/**
	 * methode qui met a jour la couleur de chaque piece
	 */
	public void update() {
		if (piecesNb > 100) {
			piecesByCase = (int) Math.ceil((double)piecesNb / 100);
			piecesNb = (int) Math.ceil((double)piecesNb / piecesByCase);
		} else {
			piecesByCase = 1;
		}
		updateCases();
	}
	
	public void updateCases() {
		for (int i = 0; i < piecesNb; i++) {
			Piece[] piecesCase = new Piece[piecesByCase];
			for (int j = 0; j < piecesByCase; j++) {
				piecesCase[j] = pieces.get(i);
			}
			this.add(new Case(piecesCase));
		}
	}
	
	/**
	 * initialise le start de l'interface runnable
	 */
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
}
