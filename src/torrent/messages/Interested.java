package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE INTERESTED : modelise un message Interested qui permet d'annoncer 
 * a un pair que l'on desire un piece de donnees.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class Interested extends Message {

	/**
	 * CONSTRUCTEUR qui herite de message.
	 */
	public Interested() {
		super(1, ID.interested);
	}

	/**
	 * methode d'ecriture du message
	 * @param dos : DataOutputStream, flux de donnees dans laquelle la methode va Žcrire.
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
