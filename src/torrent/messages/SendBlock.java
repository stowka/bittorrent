package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE SENDBLOCK : modelise un message SendBlock qui permet d'envoyer un
 * block de donnees.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 * 
 */
public class SendBlock extends Message {
	private byte[] body;

	/**
	 * CONSTRUCTEUR
	 * 
	 * @param index
	 *            : int, index du message.
	 * @param begin
	 *            : int, debut du message.
	 * @param x
	 *            : byte[], le reste du message.
	 */
	public SendBlock(int index, int begin, byte[] x) {
		super(9 + x.length, ID.piece);
		body = new byte[x.length];
		body = constructBody(index, begin, x);
		System.out.println("sendblock created");
	}

	/**
	 * methode d'ecriture du message
	 * 
	 * @param dos
	 *            : DataOutputStream, flux de donnŽes dans laquelle la methode
	 *            va Žcrire.
	 */
	@Override
	public void write(DataOutputStream dos) {
		try {
			dos.writeInt(length);
			dos.write(id.ordinal());
			dos.write(body);
			dos.flush();
			System.out.println("sendblock written");
		} catch (IOException e) {
			System.out.println("erreur envoi message");
		}
	}

	public byte[] constructBody(int index, int begin, byte[] x) {
		byte[] total = new byte[8 + x.length];
		byte[] indexB = new byte[] {
			(byte) (index >>> 24),
			(byte) (index >>> 16),
			(byte) (index >>> 8),
			(byte) (index)
		};
		System.arraycopy(indexB, 0, total, 0, indexB.length);
		byte[] beginB = new byte[] {
				(byte) (begin >>> 24),
				(byte) (begin >>> 16),
				(byte) (begin >>> 8),
				(byte) (begin)
			};
		System.arraycopy(beginB, 0, total, 4, beginB.length);
		System.arraycopy(x, 0, total, 8, x.length);
		return total;
	}

	/**
	 * @param visitor
	 *            : MessageVisitor, permet l'utilisation du pattern Visitor avec
	 *            le message Unchoke.
	 */
	public void accept(MessageVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * getter
	 * 
	 * @return body : byte[] le corps du message.
	 */
	public byte[] getBody() {
		return body;
	}
}
