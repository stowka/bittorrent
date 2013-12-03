package gui;

import java.awt.Color;

import javax.swing.JPanel;

import torrent.piece.Piece;

/**
 * CLASSE CASE : modelise une piece sous forme de JPanel coloree selon l'etat
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 * 
 */

@SuppressWarnings("serial")
public class Case extends JPanel {
	private Piece[] pieces;

	/**
	 * Constructeur
	 * 
	 * @param pieces
	 *            : liste des pieces du Torrent en cours de telechargement
	 */
	

	public Case(Piece[] pieces) {
		this.setOpaque(true);
		this.pieces = new Piece[pieces.length];
		for (int i = 0; i < pieces.length; i++) {
			this.pieces[i] = pieces[i];
		}
		setColorCase();
	}

	public void setColorCase() {
		setBackground(Color.red);
		for (int i = 0; i < pieces.length; i++) {
			if (pieces[i].getChecked()) {
				setBackground(Color.blue);
				return;
			} else if (pieces[i].getDownloadCompleteness() > 0 && pieces[i].getDownloadCompleteness() < 100) {
				setBackground(Color.yellow);
				return;
			}
		}
	}
}