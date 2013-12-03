package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE BITFIELD : modelise un message BitField qui annonce la liste des index 
 * des pieces disponibles sous la forme d'un tableau de byte ou chaque bit se 
 * comportera comme un boolean (1, true le pair possede la piece ; 
 * 0, false la pair ne possede pas la piece).
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class BitField extends Message {
	private byte[] body;

	/**
	 * CONSTRUCTEUR qui herite de Message.
	 * @param body : byte[], le corps du message.
	 */
	public BitField(byte[] body) {
		super(1 + body.length, ID.bitfield);
		this.body = body.clone();
	}

	/**
	 * methode d'ecriture du message
	 * @param dos : DataOutputStream, flux de données dans laquelle la methode va écrire.
	 */
	@Override
	public void write(DataOutputStream dos) {
		try {
			System.out.println(length);
			System.out.println(id.ordinal());
			for (int i = 0; i < body.length; i++) {
				System.out.println(body[i]);
			}
			dos.writeInt(length);
			dos.write(id.ordinal());
			dos.write(body);
			dos.flush();
		} catch (IOException e) {
			System.out.println("erreur envoi bitfield");
			e.printStackTrace();
		}
	}
	
	/**
	 * @param visitor : MessageVisitor, permet l'utilisation du pattern Visitor avec le message Unchoke.
	 */
	public void accept(MessageVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * getter
	 * @return body : byte[] le corps du message.
	 */
	public byte[] getBody() {
		return body;
	}
}
