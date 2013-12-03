package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE NOTINTERESTED : modelise un message NotInterested qui signifie 
 * que l'on ne desire pas une piece. (On possede deja cette piece.)
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
public class NotInterested extends Message {

	/**
	 * CONSTRUCTEUR qui herite de Message.
	 */
	public NotInterested() {
		super(1, ID.notInterested);
	}

	/**
	 * methode d'ecriture du message
	 * @param dos : DataOutputStream, flux de données dans laquelle la methode va écrire.
	 */
	@Override
	public void write(DataOutputStream dos) {
		try {
			dos.writeInt(length);
			dos.write(id.ordinal());
			dos.flush();
		} catch (IOException e) {
			System.out.println("erreur envoi message");
		}
	}
	
	/**
	 * @param visitor : MessageVisitor, permet l'utilisation du pattern Visitor avec le message Unchoke.
	 */
	public void accept(MessageVisitor visitor) {
		visitor.visit(this);
	}
}
