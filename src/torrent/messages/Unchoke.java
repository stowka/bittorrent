package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE UNCHOKE : modelise un message Unchoke qui permet 
 * d'ouvrir les �changes entre le client et un pair.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
public class Unchoke extends Message {

	/**
	 * CONSTRUCTEUR qui h�site de Message.
	 */
	public Unchoke() {
		super(1, ID.unchoke);
	}

	/**
	 * methode d'ecriture du message Unchoke
	 * @param dos : DataOutputStream, flux de donn�es dans laquelle la methode va �crire.
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
