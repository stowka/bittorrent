package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE SendSymmetricKey extends Message : modelise un message qui permet de partager les cles pour le cryptage XOR
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class SendSymmetricKey extends Message {
	private byte[] key;

	/**
	 * Constructeur
	 * @param key : cle de cryptage XOR
	 */
	public SendSymmetricKey(byte[] key) {
		super(1 + key.length, ID.sendSymmetricKey);
		this.key = key;
	}

	/**
	 * override write() permets d'ecrire la cle sur le flux.
	 * @param dos : DataOutputStream
	 */
	@Override
	public void write(DataOutputStream dos) {
		try {
			dos.writeInt(length);
			dos.write(id.ordinal());
			dos.write(key);
			dos.flush();
		} catch (IOException e) {
			System.out.println("erreur envoi message");
		}
	}

	/**
	 * Permets d'accepter le visiteur et le laisse lire un message de ce type.
	 * @param visitor : Visiteur
	 */
	@Override
	public void accept(MessageVisitor visitor) {
		throw new UnsupportedOperationException();
	}

}
