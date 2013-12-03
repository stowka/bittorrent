package torrent.peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import settings.PeerSettings;
import torrent.Torrent;
import torrent.messages.BitField;
import torrent.messages.HandShake;
import torrent.messages.Interested;
import torrent.messages.KeepAlive;
import torrent.messages.Message;
import torrent.messages.MessageHandler;
import torrent.messages.MessageReader;
import torrent.messages.Request;
import torrent.messages.SendRSAKey;
import torrent.piece.Piece;
import torrent.piece.PieceManager;

import java.lang.Thread;

import crypto.KeyGenerator;
import crypto.KeyPair;

/**
 * CLASSE PEERHANDLER gère l'interaction entre le client et un pair. Ce
 * gestionnaire envoie les messages au pair. Chaque pair possède un son propre
 * PeerHandler par rapport au client avec qui il interagit.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 * 
 */
public class PeerHandler extends Thread {
	private Torrent torrent;
	private MessageHandler messageHandler;
	private MessageReader messageReader;
	private Peer peer;
	private PieceManager pieceManager;
	private InetAddress ip;
	private int port;
	private List<PeerHandler> listPh;
	private List<Piece> pieces;
	private List<Integer> peerPieces;
	private Piece interestPiece;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private HandShake hs;
	private Interested interested;
	private BitField bf;
	private Request req;
	private boolean choked, peerChoked;
	private boolean alive;
	private boolean loading, stop;
	private KeyPair keyPair;
	private long time;
	private Queue<Message> messages;
	private int index;
	private int begin;
	private int length;
	public static final int DEFAULT_LENGTH = 128;

	/**
	 * CONSTRUCTEUR (Client se connecte à un pair)
	 * 
	 * @param torrent
	 *            : Torrent concernant le fichier que l'on échange.
	 * @param peer
	 *            : Peer, pair du torrent associé à ce PeerHandler.
	 */
	public PeerHandler(Torrent torrent, Peer peer) {
		this.pieceManager = torrent.getPieceManager();
		this.keyPair = KeyGenerator.generateRSAKeyPair(DEFAULT_LENGTH);
		this.interested = null;
		this.listPh = new ArrayList<PeerHandler>();
		this.messages = new LinkedList<Message>();
		this.peer = peer;
		this.choked = true;
		this.peerChoked = false;
		this.alive = true;
		this.torrent = torrent;
		this.pieces = new ArrayList<Piece>();
		this.pieces.addAll(torrent.getPieces());
		this.peerPieces = new ArrayList<Integer>();
		this.interestPiece = null;
		this.loading = false;
		this.stop = false;
		this.index = 0;
		this.begin = 0;
		this.length = 0;
	}

	/**
	 * CONSTRUCTEUR (un pair se connecte au client)
	 * 
	 * @param torrent
	 *            : Torrentconcernant le fichier que l'on échange.
	 * @param peer
	 *            : Peer, pair du torrent associé à ce PeerHandler.
	 * @param socket
	 *            : Socket contenant les paramètres de la connexion entre le
	 *            pair et le client.
	 */
	public PeerHandler(Torrent torrent, Socket socket) {
		this.torrent = torrent;
		this.socket = socket;
	}

	/**
	 * 
	 * processus qui créé un socket puis envoie un HandShake et enfin exécute en
	 * boucle la gestion des messages :
	 * 
	 * 
	 */
	public void run() {
		if (torrent.isComplete()) {
			return;
		}

		if (socket == null && choked) {
			ip = peer.getIp();
			port = peer.getPort();
			try {
				System.out.println("ip : " + ip + " port : " + port);
				socket = new Socket(ip, port);
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				System.out.println("PeerHandler : socket initialisé");

				// HANDSHAKE !
				createHandShake();

				if (hs.readData(dis) == null) {
					System.out.println("PeerHandler : Handshake reçu mauvais");
					socket.close();
					return;
				} else {
					System.out.println("PeerHandler : Handshake reçu correct");
					shareKeys();
				}
				// HANDSHAKE

				// BITFIELD !
				createBitField();
				// BITFIELD

				time = System.currentTimeMillis();

				messageReader = new MessageReader(dis);
				messageHandler = new MessageHandler(this);
				while (!stop) {
					while (loading) {
						/*
						 * if ((System.currentTimeMillis() -
						 * messageHandler.getLastKeepAlive()) > 300000) { if()
						 * alive = false; }
						 */

						if (!alive) {
							System.out.println("alive false");
							socket.close();
							return;
						} else {
							if (dis.available() != 0) {
								System.out.println("message present dans dis");
								messageReader.readMessage().accept(
										messageHandler);
							} else {
								try {
									sleep(10);
									continue;
								} catch (InterruptedException e) {
									System.out.println("interrupted exception");
								}
							}

							createKeepAlive();

							if (choked) {
								interested = new Interested();
								this.addMessageQueue(interested);
								System.out.println("interested créé");
							} else {
								
								choosePiece();

								if (!peerChoked) {
									req = new Request(index, begin, length);
									System.out.println("request written");
								}
								listPh.add(this); // on fait une requête, on
													// ajoute donc ce
													// PeerHandler à la liste de
													// PH de Piece
								
								if (interestPiece != null && interestPiece.getList().containsKey(req)) {
									interestPiece.getList().get(req).add(this);
								} else if (interestPiece != null){
									interestPiece.getList().put(req, listPh);
								}
								messages.add(req);

							}

							for (int i = 0; i < messages.size(); i++) {
								messages.remove().write(dos);
								System.out.println("message sent");
							}
						}
					}
				}
			} catch (IOException e) {
				System.out.println("IOException");
				e.printStackTrace();
			}
		}
	}

	public void choosePiece() {
		interestPiece = torrent.getPieceManager().receivePiece(peerPieces);
		if (interestPiece == null) {
			System.out.println("plus rien à télécharger");
			return;
		}
		index = interestPiece.getIndex();
		begin = interestPiece.BlockSelection();
		if (begin == interestPiece.getNbBlock() - 1) {
			length = interestPiece.getLastBlockSize();
		} else {
			length = Piece.BLOCK_SIZE;
		}
	}

	public void createHandShake() {
		hs = new HandShake(torrent);
		hs.writeData(dos);
		System.out.println("PeerHandler : Handshake sent");
	}

	public void shareKeys() {
		System.out.println("hs crypt : " + hs.getCrypt());
		if (hs.getCrypt() && PeerSettings.encryptionEnabled()) {
			// SendRSAKey srsak = new SendRSAKey(keyPair.getN(),
			// keyPair.get_e().toByteArray(), keyPair.get_n()
			// .toByteArray());
			// srsak.write(dos);
			// KeyPair keyPairPeer = srsak.readData(null, dis);
			// dis = new DataInputStream(new RSAInputStream(keyPair,
			// dis));
			// dos = new DataOutputStream(new RSAOutputStream(
			// keyPairPeer, dos));
			// // SendSymmetricKey ssk = new SendSymmetricKey();
		}
	}

	public void createBitField() {
		byte[] bitfield = new byte[(int) Math.ceil((double) pieces.size() / 8)];
		byte b = 0;
		for (int i = 0; i < pieces.size(); i++) {
			if (i % 8 == 0 && i != 0 || i == pieces.size() - 1) {
				if (i == pieces.size() - 1 && i % 8 != 0) {
					b = (byte) (b << 1);
					if (pieces.get(i).getDownloadCompleteness() == 100) {
						b += 1;
					}
					System.out.println((i / 8) + " " + i);
					bitfield[(i / 8)] = b;
					b = 0;
				} else {
					System.out.println((i / 8) - 1 + " " + i);
					bitfield[(i / 8) - 1] = b;
					b = 0;
				}
			}

			b = (byte) (b << 1);
			if (pieces.get(i).getDownloadCompleteness() == 100) {
				b += 1;
			}
		}
		bf = new BitField(bitfield);
		bf.write(dos);
		System.out.println("PeerHandler : Bitfield écrit");
	}

	public void createKeepAlive() {
		if (System.currentTimeMillis() >= time + 120000) {
			time = System.currentTimeMillis();
			KeepAlive ka = new KeepAlive();
			this.addMessageQueue(ka);
			System.out.println("keepAlive créé");
		}
	}

	/**
	 * 
	 * @return le torrent auquel on s'intéresse.
	 */
	public Torrent getTorrent() {
		return torrent;
	}

	/**
	 * 
	 * @param choked
	 *            : modifie la valeur du boolean choked selon si le pair nous
	 *            envoie un message Choke.
	 */
	public void setChoked(boolean choked) {
		this.choked = choked;
	}

	/**
	 * 
	 * @param peerChoked
	 *            : modifie la valeur du boolean peerChoked selon si nous avons
	 *            choke ou non le pair.
	 */
	public void setPeerChoked(boolean peerChoked) {
		this.peerChoked = peerChoked;
	}

	/**
	 * 
	 * @param alive
	 *            : modifie la valeur du boolean alive par l'envoi de messages
	 *            KeepAlive.
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * 
	 * @return le statut de connexion alive
	 */
	public boolean getAlive() {
		return this.alive;
	}

	/**
	 * ajoute un message a la queue de messages
	 * 
	 * @param message
	 *            a rajouter a la queue
	 */
	public void addMessageQueue(Message message) {
		this.messages.add(message);
	}

	/**
	 * 
	 * @return la queue de messages
	 */
	public Queue<Message> getQueueMessage() {
		return messages;
	}

	/**
	 * 
	 * @param pieces
	 *            ajoute une liste de pieces a un pair
	 */
	public void setPeerPieces(List<Integer> pieces) {
		this.peerPieces.addAll(pieces);
	}

	/**
	 * 
	 * @return le statut choke d'un pair
	 */
	public boolean getPeerChoked() {
		return peerChoked;
	}

	/**
	 * 
	 * @return le PieceManager associe au torrent
	 */
	public PieceManager getPieceManager() {
		return pieceManager;
	}

	/**
	 * 
	 * @param loading
	 */
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	/**
	 * arrete completement le thread
	 */
	public void stopIt() {
		this.loading = false;
		this.stop = true;
	}
}
