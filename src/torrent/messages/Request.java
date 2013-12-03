package torrent.messages;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * CLASSE REQUEST : modelise un message qui fait la demande d'un block ˆ un
 * pair.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 */
public class Request extends Message {
	private byte[] body;
	private int index;
	private int begin;

	/**
	 * CONSTRUCTEUR
	 * 
	 * @param index
	 *            : int, index du message.
	 * @param begin
	 *            : int, debut du message.
	 * @param length
	 *            : int, longueur du message.
	 */
	public Request(int index, int begin, int length) {
		super(13, ID.request);
		this.begin = begin;
		this.index = index;
		body = new byte[12];
		body = constructBody(index, begin, length);
		System.out.println("request created");
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
			System.out.println("request flush");
		} catch (IOException e) {
			System.out.println("erreur envoi message");
		}
	}
	
	public byte[] constructBody(int index, int begin, int length) {
		byte[] total = new byte[12];
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
		byte[] lengthB = new byte[] {
				(byte) (length >>> 24),
				(byte) (length >>> 16),
				(byte) (length >>> 8),
				(byte) (length)
			};
		System.arraycopy(lengthB, 0, total, 8, lengthB.length);
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
	 * 
	 * @return body : byte[] le corps du message.
	 */
	public byte[] getBody() {
		return body;
	}

	public int getIndex() {
		return index;
	}
	
	public int getBegin() {
		return begin;
	}
}
