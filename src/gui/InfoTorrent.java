package gui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import bencoding.BEValue;
import bencoding.InvalidBEncodingException;

import torrent.MetaInfo;

/**
 * CLASSE INFOTORRENT : modelise un panel affichant les information du fichier metainfo
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
@SuppressWarnings("serial")
public class InfoTorrent extends JPanel {
	private JTextArea textMetainfo;
	// private JTextArea textTitle;
	private Border border;
	private String author, comment, name, length;
	private long time;
	private String timeString, text;
	private Date timeDate;
	private List<String> trackers;
	private String pieces;

	/**
	 * Constructeur du panel
	 * @param bg : Color du background
	 */
	public InfoTorrent(Color bg) {
		this.setBackground(bg);
		this.trackers = new ArrayList<String>();
		this.border = BorderFactory.createRaisedBevelBorder();
		this.textMetainfo = new JTextArea("No torrent added");
		this.textMetainfo.setBackground(bg);
		this.textMetainfo.setForeground(Color.BLACK);
		this.textMetainfo.setFont(new Font("TimesRoman", Font.ITALIC, 13));
		this.textMetainfo.setBorder(border);
		this.textMetainfo.setEditable(false);
		this.textMetainfo.setFocusable(false);
		this.textMetainfo.setRows(44);
		this.textMetainfo.setColumns(40);
		this.add(textMetainfo);
	}

	/**
	 * Affiche les information d'un torrent passe en argument
	 * @param path : chemin du fichier metainfo
	 */
	public void addTorrent(String path) {
		MetaInfo metainfo = new MetaInfo(path);
		this.author = "Author :\n" + metainfo.getAuthor();
		this.time = metainfo.getTime();
		this.timeDate = new Date(this.time);
		this.timeString = "Creation date :\n" + this.timeDate.toString();
		this.comment = "\n\n" + metainfo.getComment();
		this.name = "Name :\n" + metainfo.getName();
		this.length = "Taille :\n"
				+ Download.convertBytes(metainfo.getLength());
		this.pieces = ((int) Math.ceil((double)metainfo.getLength() / metainfo.getPieceLength()))
				+ " pieces : "
				+ Download.convertBytes(metainfo.getPieceLength())
				+ "/piece ("
				+ Download.convertBytes(metainfo.getLastPieceLength())
				+ " for the last)";
		this.trackers.removeAll(trackers);
		if (metainfo.getAnnounceList() != null) {
			for (BEValue b : metainfo.getAnnounceList()) {
				try {
					this.trackers.add(b.getList().get(0).getString());
				} catch (InvalidBEncodingException e) {
					System.out.println("Invalid Bendcoding");
				}
			}
		} else {
			this.trackers.add(metainfo.getAnnounce());
		}
		this.text = this.name + "\n" + this.author + "\n" + this.timeString
				+ "\n" + this.length + "\n" + this.pieces + "\n" + this.comment
				+ "\n\nTrackers :\n";
		for (String s : this.trackers) {
			this.text += s + "\n";
		}
		this.textMetainfo.setText(text);
	}

}
