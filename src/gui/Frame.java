package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import settings.PeerSettings;
import torrent.Torrent;

/**
 * CLASSE FRAME : modelise la fenetre de la gui de notre client, contient
 * plusieurs panels
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 * 
 */
@SuppressWarnings("serial")
public class Frame extends JFrame implements WindowListener, KeyListener,
		MouseListener, FocusListener {
	private JMenuBar menuBar;
	private JMenu file, tools, window, client;
	private JMenuItem open, close, changeSavingPath, credits, pieces, blue,
			red, yellow, infoClient;
	private JCheckBox encrypt;
	private Download download;
	private InfoTorrent infoTorrent;
	private JPanel panel;
	private JTabbedPane tabs;
	private boolean encryption, piecesShowed;
	private Torrent torrent;
	private List<Torrent> torrentList;
	private List<String> pathList;
	private List<Download> downloads;
	private List<PiecesFrame> piecesPanels;
	private String savingPath;
	private Color colorMenu;
	private Color bg;
	private int selected;
	private JPopupMenu popup;
	private JMenuItem cancel;

	/**
	 * Constructeur de la fenetre, creation des panels, des menus, des couleurs
	 * etc.
	 */
	public Frame() {

		this.setSize(950, 800); // POUR LES ORDIS DE CO 02*

		// COULEUR DES BACKGROUNDS DES PANELS ET DE LA BARRE DE MENUS
		this.bg = new Color(245, 245, 255, 255); // Color(int red, int green,
													// int blue, float alpha)
		this.colorMenu = Color.LIGHT_GRAY;
		// COULEUR DES BACKGROUNDS DES PANELS ET DE LA BARRE DE MENUS

		// CREATION DU PANEL A ONGLETS
		this.tabs = new JTabbedPane();
		this.tabs.addMouseListener(this);
		// CREATION DU PANEL A ONGLETS

		// CREATION DES LISTES QUI CONTIENDRONT LES TELECHARGEMENTS EN COURS
		this.pathList = new ArrayList<String>();
		this.torrentList = new ArrayList<Torrent>();
		this.downloads = new ArrayList<Download>();
		this.piecesPanels = new ArrayList<PiecesFrame>();
		// CREATION DES LISTES QUI CONTIENDRONT LES TELECHARGEMENTS EN COURS

		this.savingPath = System.getProperty("user.home") + "/Downloads"
				+ File.separator;

		// BACKGROUND ET CONTENT PANE DE LA FENTRE
		this.setBackground(bg);
		this.panel = new JPanel();
		this.setContentPane(panel);
		// BACKGROUND ET CONTENT PANE DE LA FENETRE

		/*
		 * MENU
		 */
		this.menuBar = new JMenuBar();
		this.file = new JMenu("File");
		this.tools = new JMenu("Tools");
		this.window = new JMenu("Pieces");
		this.client = new JMenu("Client");

		// BOUTON OPEN
		this.open = new JMenuItem("Open");
		this.open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.changeSavingPath = new JMenuItem("Saving Path");
		this.credits = new JMenuItem("Credits");
		this.open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pathList.size() < 10) {
					JFileChooser fc = new JFileChooser();
					fc.addChoosableFileFilter(new DotTorrentFilter());
					int returnVal = fc.showOpenDialog(panel);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String path = fc.getSelectedFile().getPath();
						System.out.println(path);
						pathList.add(path);
						if (pathList.size() == 1) {
							download.removeAll();
						}
						infoTorrent.addTorrent(path);

						torrent = new Torrent(path, savingPath);

						download = new Download(bg, torrent);
						downloads.add(download);

						
						if (torrent.isComplete()) {
							window.setEnabled(false);
						} else {
							window.setEnabled(true);
						}
						
						selected = pathList.size() - 1;
						tabs.add(download.getTitle(), download);
						tabs.setSelectedIndex(selected);

						panel.add("Center", tabs);
						torrentList.add(torrent);
						piecesShowed = false;
					}
				}
			}
		});
		// BOUTON OPEN

		// BOUTON CLOSE
		this.close = new JMenuItem("Close");
		this.close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				KeyEvent.CTRL_MASK));
		this.close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateCloseConfirmation();
			}
		});
		// BOUTON CLOSE

		// BOUTON ENCRYPT
		this.encrypt = new JCheckBox("Encrypt");
		this.encrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				encryption = encrypt.isSelected();
				System.out.println("encryption " + encryption);
				PeerSettings.encryptionEnabled();
			}
		});
		// BOUTON ENCRYPT

		// BOUTON CHANGE SAVING PATH
		this.changeSavingPath.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		this.changeSavingPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(panel);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String path = fc.getSelectedFile().getPath();
					savingPath = path + File.separator;
				}
			}
		});
		// BOUTON CHANGE SAVING PATH

		// BOUTON CREDITS
		this.credits.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.credits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] buttons = { "D'accord", "J'accepte" };
				String cred = "Projet de Technologie de l'information\n"
						+ "Client BitTorrent\n"
						+ "CrŽŽ par :\nVENDE Antoine et MARGUET Alban\n\n"
						+ "Votre soutien est le bienvenu :\n"
						+ "Comme par exemple un don de 1000 CHFÉ par exemple\n"
						+ "Voulez-vous faire le don?\n";

				JOptionPane.showOptionDialog(new JFrame(), cred, "Credits",
						JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE,
						new ImageIcon("data/icons/credits.png"), buttons,
						buttons[0]);
			}
		});
		// BOUTON CREDITS

		// BOUTON AFFICHER LISTE DES PIECES
		this.pieces = new JMenuItem("Show pieces");
		this.pieces.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!piecesShowed) {
						piecesPanels.add(new PiecesFrame(torrent));
						download.getDesktop().removeAll();
						download.getDesktop().add(new PiecesFrame(torrent));
						download.validate();
						piecesShowed = true;
				}
			}
		});
		this.blue = new JMenuItem("PIECE DOWNLOADED");
		this.blue.setBackground(Color.blue);
		this.blue.setForeground(Color.black);
		this.blue.setEnabled(false);
		this.red = new JMenuItem("PIECE NOT DOWNLOADED");
		this.red.setBackground(Color.red);
		this.red.setForeground(Color.black);
		this.red.setEnabled(false);
		this.yellow = new JMenuItem("PIECE DOWNLOADING");
		this.yellow.setBackground(Color.yellow);
		this.yellow.setForeground(Color.black);
		this.yellow.setEnabled(false);
		// BOUTON AFFICHER LISTE DES PIECES

		// BOUTON INFOS CLIENT
		this.infoClient = new JMenuItem("About the client");
		this.infoClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = "User name : " + System.getProperty("user.name")
						+ "\n" + "Operating System : "
						+ System.getProperty("os.name") + "\n" + "- version : "
						+ System.getProperty("os.version") + "\n"
						+ "aTorrent v.1.0";
				JOptionPane.showMessageDialog(null, s, "Client",
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
								"data/icons/client.png"));
			}
		});
		// BOUTON INFOS CLIENT

		// AJOUTS DES BOUTONS AUX MENUS
		this.file.add(open);
		this.file.add(close);
		this.file.setBackground(colorMenu);
		this.tools.add(encrypt);
		this.tools.addSeparator();
		this.tools.add(changeSavingPath);
		this.tools.add(credits);
		this.tools.setBackground(colorMenu);
		this.window.add(pieces);
		this.window.addSeparator();
		this.window.add(blue);
		this.window.add(yellow);
		this.window.add(red);
		this.window.setBackground(colorMenu);
		this.window.setEnabled(false);
		this.client.add(infoClient);
		this.client.setBackground(colorMenu);
		// AJOUTS DES BOUTONS AUX MENUS

		this.setJMenuBar(menuBar);

		// AJOUTS DES MENUS A LA BARRE DE MENUS
		this.menuBar.add(file);
		this.menuBar.add(tools);
		this.menuBar.add(window);
		this.menuBar.add(client);
		this.menuBar.setBackground(colorMenu);
		this.menuBar.setForeground(Color.BLACK);
		this.menuBar.setVisible(true);
		// AJOUTS DES MENUS A LA BARRE DE MENUS

		// CREATION DES PANELS PRINCIPAUX DE LA FRAME
		this.download = new Download(bg);
		this.infoTorrent = new InfoTorrent(bg);
		// CREATION DES PANELS PRINCIPAUX DE LA FRAME

		// AJOUTS DES PANELS PRINCIPAUX AU PANEL DE LA FRAME
		this.panel.setBackground(bg);
		this.panel.setLayout(new BorderLayout(5, 5));
		this.panel.add("West", new JPanel());
		this.panel.add("Center", download);
		this.panel.add("East", infoTorrent);
		// AJOUTS DES PANELS PRINCIPAUX AU PANEL DE LA FRAME

		// MENU CONTEXTUEL
		this.popup = new JPopupMenu();
		this.cancel = new JMenuItem("Cancel download");
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = tabs.getSelectedIndex();
				downloads.remove(index).stopLoading();
				torrentList.remove(index);
				pathList.remove(index);
				tabs.removeTabAt(index);
				piecesShowed = false;
			}
		});
		this.popup.add(cancel);
		tabs.setComponentPopupMenu(popup);
		// MENU CONTEXTUEL

		this.addWindowListener(this);

		// AJUSTEMENT DE LA FENETRE
		this.setTitle("PROJET DE TECHNOLOGIE DE L'INFORMATION - GROUPE 36 - CLIENT BITTORRENT");
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setLocationRelativeTo(null);
		this.setSize(this.getWidth(), this.getHeight());
		this.setVisible(true);
		// AJUSTEMENT DE LA FENETRE

	}

	/**
	 * methode main du client, cree la fenetre qui cree les panels et initie le
	 * client au demarrage
	 * 
	 * @param argv
	 *            : argument non utilise ici
	 */
	public static void main(String[] argv) {
		new Frame();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * gere l'evenement de fermeture de la fenetre
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		generateCloseConfirmation();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * affiche les informations du metainfo lors du changement d'onglet
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		this.selected = this.tabs.getSelectedIndex();
		this.piecesShowed = false;
		// this.panel.remove(tabs);
		// this.panel.add("Center", tabs);
		if (selected != -1) {
			this.infoTorrent.addTorrent(pathList.get(selected));
			this.downloads.get(selected).getDesktop().removeAll();
			this.downloads.get(selected).getDesktop()
					.add(piecesPanels.get(selected));
			this.downloads.get(selected).getDesktop().validate();
			this.downloads.get(selected).validate();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	/**
	 * methode qui cree un popup de confirmation lors de la fermeture du client
	 */
	public void generateCloseConfirmation() {
		String[] buttons = { "YES", "NO" };
		String title = "Closing confirmation";
		String question = "Do you really want to close the bittorrent client?";
		int confirm = JOptionPane.showOptionDialog(new JFrame(), question,
				title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
				new ImageIcon("data/icons/confirm-closing-icon.png"), buttons,
				buttons[1]);
		if (confirm == 0) {
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			System.exit(0);
		} else {
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 * cree un menu contextuel lors du clic sur le JTabbedPane contenant les
	 * torrents, (avec item : cancel pour ferme l'onglet)
	 */
	public void processMouseEvent(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
		super.processMouseEvent(e);
	}

	public Download getDownload() {
		return download;
	}
}