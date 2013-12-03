package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE HAVE : modelise un message Have qui signifie que l'on possede une certaine piece.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class Have extends Message {
	private byte[] index;
	
	/**
	 * CONSTRUCTEUR qui herite de Message.
	 * @param index : index du message.
	 */
	public Have(byte[] index) {
		super(5, ID.have);
		this.index = index.clone();
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
			dos.write(index);
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
	
	/**
	 * 
	 * @return index : byte[], l'index du message.
	 */
	public byte[] getIndex() {
		return index;
	}
}
