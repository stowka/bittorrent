package torrent.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import torrent.Torrent;
import torrent.peer.PeerHandler;
import torrent.piece.Piece;

/**
 * CLASSE MESSAGEHANDLER : traite la reponse d'un message.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 * 
 */
public class MessageHandler implements MessageVisitor {
	private PeerHandler ph;
	private List<Integer> pieces;
	private SendBlock sb;
	private long lastKeepAlive;

	/**
	 * Constructeur de MessageHandler : initialise le peerHandler et la liste de
	 * pieces du pair.
	 * 
	 * @param peerHandler
	 */
	public MessageHandler(PeerHandler peerHandler) {
		this.ph = peerHandler;
		this.pieces = null;
	}

	/**
	 * Lors de de la reception d'un message BitField, on met a jour la liste de
	 * pieces que possede le pair.
	 * 
	 * @param bf
	 *            : BitField : le pair a envoye un bitfield.
	 */
	@Override
	public void visit(BitField bf) {
		System.out.println("process bitfield");
		pieces = new ArrayList<Integer>();
		byte[] body = bf.getBody();
		// boucle afficher body
		/*
		 * byte b, mask2 = 0x01; for (int i = 0; i < body.length; i++) { b =
		 * body[i]; for (int j = 0; j < 8; j++) { if ((b & mask2) == 0) {
		 * System.out.println(0); } else { System.out.println(1); } } }
		 */
		// boucle afficher body
		int mask;
		for (int i = 0; i < body.length; i++) {
			mask = body[i];
			for (int j = 0; j < 8; j++) {
				if ((mask & 0x80) != 0) {
					pieces.add(j + (i * 8));
				}
				mask = mask << 1;
			}
		}
		ph.setPeerPieces(pieces);
		System.out.println(pieces.size());
		// for(Integer i : pieces) {
		// System.out.println("bitfield : " + i);
		// }
		System.out.println("taille bitfield : " + pieces.size());
	}

	/**
	 * Lors de la reception d'un choke, le client passe a l'etat choked.
	 * 
	 * @param c
	 *            : Choke, le pair a envoye un choke.
	 */
	@Override
	public void visit(Choke c) {
		System.out.println("visit choke");
		ph.setChoked(true);
	}

	/**
	 * Met a jour la liste de pieces du pair.
	 * 
	 * @param h
	 *            : Have, le pair a envoye un Have
	 */
	@Override
	public void visit(Have h) {
		// Reception de l'entier indiquant
		// l'index de la pièce possédée par
		// le pair.
		System.out.println("visit have");
		if (pieces == null) {
			pieces = new ArrayList<Integer>();
		}
		pieces.add(byteArrayToInt(h.getIndex()));
	}

	/**
	 * Lors de la reception d'un Interested, on cree un Unchoke en reponse au
	 * pair et le client passe dans l'etat unchoked.
	 * 
	 * @param i
	 *            : Interested, le pair a envoye un Interested.
	 */
	@Override
	public void visit(Interested i) {
		System.out.println("interested reçu");
		ph.addMessageQueue(new Unchoke());
		ph.setPeerChoked(false);
		System.out.println("peerChoked : " + ph.getPeerChoked());
	}

	/**
	 * Lors de la reception d'un KeepAlive, on garde la connexion ouverte.
	 * 
	 * @param ka
	 *            : KeepAlive, le pair a envoye un KeepAlive
	 */
	@Override
	public void visit(KeepAlive ka) {
		System.out.println("peer keepAlive connection");
		ph.setAlive(true);
		this.lastKeepAlive = System.currentTimeMillis();
	}

	/**
	 * Lors de la reception d'un NotInterested, on cree un Choke en reponse et
	 * le pair associe passe dans l'etat choked.
	 * 
	 * @param ni
	 *            : NotInterested, le pair a envoye un message NotInterested.
	 */
	@Override
	public void visit(NotInterested ni) {
		System.out.println("peer is notInterested");
		ph.addMessageQueue(new Choke());
		ph.setPeerChoked(true);
		System.out.println("peerChoked : " + ph.getPeerChoked());
	}

	/**
	 * Lors de la reception d'une reauete, on decode le corps du message et on
	 * cree un SendBlock en reponse : on lui envoie le bloc demande.
	 * 
	 * @param r
	 *            : Request, le pair a envoye un Request.
	 */
	@Override
	public void visit(Request r) {
		if (!ph.getPeerChoked()) {
			System.out.println("requete reçue");
			byte[] body = r.getBody();
			byte[] indexB = new byte[4], beginB = new byte[4], lengthB = new byte[4];
			Torrent torrent = ph.getTorrent();
			int index = 0, begin = 0, length = 0;

			indexB = Arrays.copyOfRange(body, 0, 4);
			index = byteArrayToInt(indexB);

			beginB = Arrays.copyOfRange(body, 4, 8);
			begin = byteArrayToInt(beginB);

			lengthB = Arrays.copyOfRange(body, 8, body.length);
			length = byteArrayToInt(lengthB);

			Piece piece = torrent.getPieces().get(index);
			if (index <= torrent.getPieces().size()) {
				byte[] block = new byte[length];
				byte[] pieceByte = piece.getData();
				for (int i = 0; i < length; i++) {
					block[i] = pieceByte[begin + i];
				}
				sb = new SendBlock(index, begin, block);
				this.ph.addMessageQueue(sb);
				System.out.println("sendBlock envoyé");
			} else {
				System.out.println("Index trop grand");
			}
		}
	}

	/**
	 * Lors de la reception d'un SendBlock, on decode le corps et on feed la
	 * piece correspondante.
	 * 
	 * @param sb
	 *            : SendBlock, Le pair a envoye un SendBlock.
	 */
	@Override
	public void visit(SendBlock sb) {
		System.out.println("SendBlock reçu");
		byte[] body = sb.getBody();
		byte[] indexB = new byte[4], beginB = new byte[4];
		Torrent torrent = ph.getTorrent();
		int index = 0, begin = 0;
		byte[] block = new byte[sb.length - 9];

		indexB = Arrays.copyOfRange(body, 0, 4);
		index = byteArrayToInt(indexB);

		beginB = Arrays.copyOfRange(body, 4, 8);
		begin = byteArrayToInt(beginB);

		block = Arrays.copyOfRange(body, 8, body.length);

		Piece piece = torrent.getPieces().get(index);
		piece.feed(begin, block);
		System.out.println("piece fed");
		if (piece.getChecked()) {
			Have h = new Have(indexB);
			this.ph.addMessageQueue(h);
			System.out.println("have envoye, block bien reçu");
		}
	}

	/**
	 * Lors de la reception d'un Unchoke, on a l'etat unchoked.
	 * 
	 * @param uc
	 *            = Unchoke, le pair a envoye un Unchoke.
	 */
	@Override
	public void visit(Unchoke uc) {
		System.out.println("unchoke reçu");
		ph.setChoked(false);
		System.out.println("choked : false");
	}

	/**
	 * Vide : on ne recoit pas de message abstrait.
	 * 
	 * @param message
	 *            : Message
	 */
	@Override
	public void visit(Message message) {
	}

	/**
	 * getter
	 * 
	 * @return pieces : List<Integers> liste des index des pieces que le pair
	 *         possede.
	 */
	public List<Integer> getPieces() {
		return pieces;
	}

	// Getters des autres messages !

	/**
	 * methode auxiliaire Convertit un tableau de 4 bytes en entier.
	 * 
	 * @param byteArray
	 *            ; byte[]
	 */
	public int byteArrayToInt(byte[] byteArray) {
		// http://snippets.dzone.com/posts/show/93
		return (byteArray[0] << 24) + ((byteArray[1] & 0xFF) << 16)
				+ ((byteArray[2] & 0xFF) << 8) + (byteArray[3] & 0xFF);
	}

	public long getLastKeepAlive() {
		return lastKeepAlive;
	}

	@Override
	public void visit(SendRSAKey srsak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SendSymmetricKey ssyk) {
		// TODO Auto-generated method stub
		
	}
}

/**
 * Interface MessageVisitor interface implementee par MessageHandler, pour
 * permettre dans le cadre du Visitor Pattern, de notifier d'un message.
 * 
 */
interface MessageVisitor {
	/**
	 * Methode de visite du Message
	 * 
	 * @param message
	 *            : Message
	 */
	public void visit(Message message);

	/**
	 * Methode de visite du BitField
	 * 
	 * @param bf
	 *            : BitField
	 */
	public void visit(BitField bf);

	/**
	 * Methode de visite du Choke
	 * 
	 * @param c
	 *            ; Choke
	 */
	public void visit(Choke c);

	/**
	 * Methode de visite du Have
	 * 
	 * @param h
	 *            : Have
	 */
	public void visit(Have h);

	/**
	 * Methode de visite du Interested
	 * 
	 * @param i
	 *            : Interested
	 */
	public void visit(Interested i);

	/**
	 * Methode de visite du KeepAlive
	 * 
	 * @param ka
	 *            : KeepAlive
	 */
	public void visit(KeepAlive ka);

	/**
	 * Methode de visite du NotInterested
	 * 
	 * @param ni
	 *            : NotInterested
	 */
	public void visit(NotInterested ni);

	/**
	 * Methode de visite du Request
	 * 
	 * @param r
	 *            : Request
	 */
	public void visit(Request r);

	/**
	 * Methode de visite du SendBlock
	 * 
	 * @param sb
	 *            : SendBlock
	 */
	public void visit(SendBlock sb);

	/**
	 * Methode de visite du Unchoke
	 * 
	 * @param uc
	 *            : Unchoke
	 */
	public void visit(Unchoke uc);
	
	public void visit(SendRSAKey srsak);
	
	public void visit(SendSymmetricKey ssyk);
}

/**
 * interface VisitorAccept Interface implementee par tous les messages pour
 * accepter la visite des Messages.
 * 
 * 
 */
interface VisitorAccept {
	/**
	 * Methode permettant d'accepter le visiteur.
	 * 
	 * @param visitor
	 *            ; MessageVisitor
	 */
	public void accept(MessageVisitor visitor);
}