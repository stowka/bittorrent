package torrent.messages;

import java.io.DataOutputStream;

/**
 * CLASSE ABSTRAITE MESSAGE : modelise un message abstrait dont tous les 
 * autres messages (sauf HandShake) vont heriter.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public abstract class Message {
	
	protected int length;
	protected ID id;
	final protected int MESSAGE_LENGTH = 4;
	
	/**
	 * CONSTRUCTEUR
	 * @param length : int, longueur du message.
	 * @param id : ID, id du message. (cf. enum ID)
	 */
	public Message(int length, ID id) {
		this.length = length;
		this.id = id;
	}
	
	/**
	 * methode d'ecriture du message
	 * @param dos : DataOutputStream, flux de données dans laquelle la methode va écrire.
	 */
	public abstract void write(DataOutputStream dos);
	
	/**
	 * @param visitor : MessageVisitor, permet l'utilisation du pattern Visitor avec le message Unchoke.
	 */
	public abstract void accept(MessageVisitor visitor);
}
