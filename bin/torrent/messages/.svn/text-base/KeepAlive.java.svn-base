package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE KEEPALIVE : modelise un message KeepAlive qui permet de maintenir la 
 * connexion regulierement entre le client et un pair.
 * Signifie que au pair que la connexion est toujours active.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class KeepAlive extends Message {
	
	/**
	 * CONSTRUCTEUR qui herite de Message.
	 */
	public KeepAlive() {
		super(0, null);
	}

	/**
	 * methode d'ecriture du message
	 * @param dos : DataOutputStream, flux de donnees dans laquelle la methode va Žcrire.
	 */
	@Override
	public void write(DataOutputStream dos) {
		try {
			dos.writeInt(length);
			dos.flush();
		} catch (IOException e) {
			System.out.println("erreur envoi fichier");
		}
	}

	/**
	 * @param visitor : MessageVisitor, permet l'utilisation du pattern Visitor avec le message Unchoke.
	 */
	public void accept(MessageVisitor visitor) {
		visitor.visit(this);
	}
}
