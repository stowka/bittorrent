package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import torrent.Torrent;
import torrent.peer.Peer;
import torrent.peer.PeerHandler;

/**
 * CLASSE DOWNLOAD : modelise le panel contenant les informations a propos de l'avancement d'un torrent
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
@SuppressWarnings("serial")
public class Download extends JPanel implements Runnable, ActionListener {
	private JProgressBar torrentProgress, pieceProgress;
	private JTextPane textTorrent;
	private JPanel duspeed;
	private JTextField downloadSpeed, uploadSpeed;
	private Torrent torrent;
	private List<Peer> peers;
	private double avancement;
	// private double uSpeed;
	private boolean loading, ended;
	private JToolBar toolBar;
	private JButton load, stop, open, openDir;
	private JSplitPane splitPane1, splitPane2;
	private JPanel torrentProgression;
	// private JList peerList;
	private JScrollPane peersPanel;
	private String s;
	private JTextArea peerList;
	private Speed threadSpeed;
	private JPanel caseOfPieces;

	private final int MAX_PEERS = 100;
	
	/**
	 * Constructeur du panel lorsqu'un torrent est ajoute
	 * @param bg : Color, background du panel
	 * @param torrent : Torrent, objet torrent associe au panel
	 */
	public Download(Color bg, Torrent torrent) {
		// EVERY DOWNLOAD (with this builder) IS CREATED WHEN A TORRENT IS ADDED
		this.toolBar = new JToolBar();
		this.caseOfPieces = new JPanel();
		this.caseOfPieces.setLayout(new GridLayout(1, 1));
		this.caseOfPieces.setOpaque(true);

		// CRETION DES BOUTONS LOAD / STOP
		ImageIcon loadIcon = new ImageIcon("data/icons/load.png");
		ImageIcon stopIcon = new ImageIcon("data/icons/stop.png");
		ImageIcon openFileIcon = new ImageIcon("data/icons/open.png");
		ImageIcon openDirIcon = new ImageIcon("data/icons/open-dir.png");
		
		this.load = new JButton(loadIcon);
		this.load.setBackground(bg);
		this.load.addActionListener(this);
		
		this.stop = new JButton(stopIcon);
		this.stop.setBackground(bg);
		this.stop.addActionListener(this);
		
		this.open = new JButton(openFileIcon);
		this.open.setBackground(bg);
		this.open.addActionListener(this);
		
		this.openDir = new JButton(openDirIcon);
		this.openDir.setBackground(bg);
		this.openDir.addActionListener(this);
		
		this.load.setEnabled(true);
		this.stop.setEnabled(false);
		this.open.setEnabled(false);
		this.openDir.setEnabled(true);
		// CREATION DES BOUTONS LOAD / STOP

		// CREATION DE LA TOOLBAR
		this.toolBar.setBackground(bg);
		this.toolBar.add(this.load);
		this.toolBar.add(this.stop);
		this.toolBar.add(this.open);
		this.toolBar.add(this.openDir);
		this.toolBar.setBackground(bg);
		this.toolBar.setFloatable(false);
		// CREATION DE LA TOOLBAR

		// INITIALISATION DES BOOLEANS
		this.loading = false;
		this.ended = false;
		// INITIALISATION DES BOOLEANS

		this.avancement = 0;
		this.torrent = torrent;
		this.peers = new ArrayList<Peer>();

		// CREATION DES TEXTFIELDS DE VITESSE DE CHARGEMENT (UP / DOWN)
		this.downloadSpeed = new JTextField("Download :  STOPPED");
		this.downloadSpeed.setBackground(bg);
		this.downloadSpeed.setEditable(false);
		this.downloadSpeed.setFocusable(false);

		this.uploadSpeed = new JTextField("Upload : " + getUploadSpeed()
				+ " byte/s");
		this.uploadSpeed.setBackground(bg);
		this.uploadSpeed.setEditable(false);
		this.uploadSpeed.setFocusable(false);

		this.duspeed = new JPanel();
		this.duspeed.setBackground(bg);
		this.duspeed.setLayout(new GridLayout(1, 2));
		this.duspeed.add(downloadSpeed);
		this.duspeed.add(uploadSpeed);

		this.threadSpeed = new Speed(this);
		this.threadSpeed.setLoading(true);
		this.threadSpeed.start();
		// CREATION DES TEXTFIELDS DE VITESSE DE CHARGEMENT (UP / DOWN)

		// PROGESSION
		this.torrentProgress = new JProgressBar();
		this.torrentProgress.setFocusable(false);
		this.pieceProgress = new JProgressBar();
		this.pieceProgress.setFocusable(false);

		this.avancement = torrent.getCompleteness();

		this.torrentProgress = new JProgressBar(0, 100);
		this.torrentProgress.setValue((int) avancement);
		this.torrentProgress.setBackground(bg);
		this.torrentProgress.setStringPainted(true);
		// PROGRESSION

		// PANEL CONTENANT L'AVANCEMENT DU TORRENT
		this.torrentProgression = new JPanel();
		this.torrentProgression.setLayout(new BorderLayout());
		this.torrentProgression.setBackground(new Color(255, 255, 255, 255));
		this.torrentProgression.add(toolBar, BorderLayout.NORTH);
		this.torrentProgression.add(torrentProgress, BorderLayout.CENTER);
		this.torrentProgression.add(duspeed, BorderLayout.SOUTH);
		// PANEL CONTENANT L'AVANCEMENT DU TORRENT

		// PANEL CONTENANT LA LISTE DES PAIRS (IP _ PORTS _ DL SPEED)
		this.s = "There is no peer connected";
		this.peerList = new JTextArea();
		this.peerList.setFocusable(false);
		this.peerList.setEditable(false);
		this.peerList.setBackground(Color.LIGHT_GRAY);
		this.peerList.setRows(5);
		this.peerList.setText(s);
		this.peersPanel = new JScrollPane(peerList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, peersPanel,
				caseOfPieces);
		this.splitPane2.setDividerLocation(525);
		this.splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				torrentProgression, splitPane2);
		this.splitPane1.setDividerLocation(85);
		this.setLayout(new GridLayout(1, 1));
		this.add(splitPane1);
		// PANEL CONTENANT LA LISTE DES PAIRS (IP _ PORTS _ DL SPEED)

		this.setButtonsEnabled();
	}

	/**
	 * initialise la mehode start pour l'interface runnable
	 */
	private void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * constructeur du panel au lancement du client
	 * @param bg, Color du panel
	 */
	public Download(Color bg) {
		// NO TORRENT
		this.setOpaque(false);
		this.textTorrent = new JTextPane();
		this.textTorrent.setText("No torrent is loaded");
		this.textTorrent.setOpaque(false);
		this.textTorrent.setEditable(false);
		this.textTorrent.setFocusable(false);

		this.setLayout(new GridLayout(10, 1));
		this.add(textTorrent);
	}

	/**
	 * 
	 * @return le nom du fichier en telechargement
	 */
	public String getTitle() {
		return this.torrent.getMetaInfo().getName();
	}

	/**
	 * 
	 * @return la progression du telechargement en %
	 */
	public double getAvancement() {
		return torrent.getCompleteness();
	}

	/**
	 * methode run du thread, actualise le panel (progression, vitesse, pieces)
	 */
	@Override
	public void run() {
		while (loading) {
			if (torrent.isComplete()) {
				ended = true;
				loading = false;
				this.torrent.getPieceManager().stopIt();
				stopLoading();
				threadSpeed.stopIt();
				JOptionPane.showMessageDialog(null, torrent.getMetaInfo()
						.getName(), "File downloaded",
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
								"data/icons/downloaded.png")); 
				this.setButtonsEnabled();
			}
			this.avancement = (int) getAvancement();
			showPeerList();
			this.downloadSpeed.setText("Download : "
					+ convertBytes(threadSpeed.getDownloadSpeed()) + " /s");
			this.torrentProgress.setValue((int) avancement);
		}
	}

	/**
	 * 
	 * @return
	 */
	public double getUploadSpeed() {
		return 0.0;
	}

	/**
	 * methode pour convertir des bytes en kilobytes, Megabytes ou Gigabytes
	 * @param bytes : les bytes a convertir
	 * @return le string resultant de la conversion
	 */
	public static String convertBytes(double bytes) {
		final int GIGA = 1073741824;
		final int MEGA = 1048576;
		final int KILO = 1024;
		if (bytes > GIGA) {
			return troncDouble((bytes / MEGA)) + " GB";
		} else if (bytes > MEGA) {
			return troncDouble((bytes / MEGA)) + " MB";
		} else if (bytes > KILO) {
			return troncDouble((bytes / KILO)) + " KiB";
		} else {
			return bytes + " Bytes";
		}
	}

	/**
	 * methode pour troncer un double a 2 decimales
	 * @param number : nombre a tronquer
	 * @return le nombre tronque
	 */
	public static double troncDouble(double number) {
		number *= 100;
		int numberInt = (int) number;
		return (double) ((double) numberInt / 100);
	}

	/**
	 * execute l'action de l'evenement d'un clic sur un des boutons
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(this.load)) { // START OU REPREND LE TELECHARGEMENT
			this.loading = true;
			this.torrent.massAnnounce(MAX_PEERS);
			for (PeerHandler ph : torrent.getPeerHandlers()) {
				ph.start();
				ph.setLoading(true);
			}
			if (!torrent.getPieceManager().isAlive()) {
				this.torrent.getPieceManager().start();
			}
			this.start();
		} else if (source.equals(this.stop)) { // STOP LE TELECHARGEMENT
			stopLoading();
		} else if (source.equals(this.open)) { // OUVRIR LE FICHIER TELECHARGE
			try {
				Desktop d = Desktop.getDesktop();
				d.open(new File(torrent.getSavingPath() + File.separator + torrent.getMetaInfo().getName()));
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		} else if (source.equals(this.openDir)) { // OUVRIR LE REPERTOIRE DE TELECHARGEMENT
			try {
				Desktop d = Desktop.getDesktop();
				d.open(new File(torrent.getSavingPath()));
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		this.setButtonsEnabled();
	}

	public boolean getEnded() {
		return ended;
	}

	/**
	 * Affiche la liste des pairs connectes a notre client
	 */
	public void showPeerList() {
		// ACTUALISER LA LISTE DES PAIRS CONNECTES
		this.peers.removeAll(peers);
		this.peers.addAll(torrent.getPeers());
		// ACTUALISER LA LISTE DES PAIRS CONNECTES
		s = "Peers connected :\n";
		for (Peer p : peers) {
			s += p.getIp().toString() + " : " + p.getPort() + "\n";
		}
		this.peerList.setText(s);
	}

	/**
	 * Set les boutons a un etat enable (true/false) suivant l'etat de telechargement du torrent
	 */
	public void setButtonsEnabled() {
		if (loading) {
			this.load.setEnabled(false);
			this.stop.setEnabled(true);
			this.open.setEnabled(false);
			this.openDir.setEnabled(true);
		} else {
			if (getEnded() || torrent.getCompleteness() == 100) {
				this.load.setEnabled(false);
				this.stop.setEnabled(false);
				this.open.setEnabled(true);
				this.openDir.setEnabled(true);
			} else {
				this.load.setEnabled(true);
				this.stop.setEnabled(false);
			}
		}
	}

	/**
	 * 
	 * @return l'etat du thread (true si le thread est alive)
	 */
	public boolean getLoading() {
		return loading;
	}

	/**
	 * Arrete les thread associes au torrent
	 */
	public void stopLoading() {
		this.loading = false;
		this.torrent.getPieceManager().setLoading(false);
		this.threadSpeed.setLoading(false);
		for (PeerHandler ph : torrent.getPeerHandlers()) {
			ph.stopIt();
		}
		this.torrent.getPeerHandlers().removeAll(torrent.getPeerHandlers());
	}

	/**
	 * 
	 * @return la taille du fichier telecharge
	 */
	public int getSizeFile() {
		return torrent.getMetaInfo().getLength();
	}

	/**
	 * 
	 * @return retourne le panel de pieces
	 */
	public JPanel getDesktop() {
		return caseOfPieces;
	}
}
