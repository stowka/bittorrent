package gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * CLASSE DOTTORRENTFILTER : modelise un filtre de fichiers *.torrent pour un JFileChooser
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
public class DotTorrentFilter extends FileFilter {
	private String extension, description;
	
	/**
	 * Constructeur du filtre
	 */
	public DotTorrentFilter() {
		this.extension = ".torrent";
		this.description = "fichier metainfo";
	}
	
	/**
	 * Choisir le fichier
	 */
	@Override
	public boolean accept(File file) {
		return (file.isDirectory() || file.getName().endsWith(this.extension));
	}

	/**
	 * recupere la description de l'extension
	 */
	@Override
	public String getDescription() {
		return (this.extension + " - " + this.description);
	}
}
