package torrent.messages;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * CLASSE MESSAGEREADER : interprete le message recu.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
public class MessageReader {
	private DataInputStream dis;
	
	/**
	 * Constructeur de MessageReader.
	 * initialise dis.
	 * @param dis : DataInputStream 
	 */
	public MessageReader(DataInputStream dis) {
		this.dis = dis;
	}
	
	/**
	 * Quand le peerHandler recoit un message, il utilise cette methode pour 
	 * le lire et savoir de quel type de message il s'agit.
	 * @return Message : le message reçu avec son contenu.
	 */
	public Message readMessage() {
		int l = 0;
		try {
			l = dis.readInt();
			System.out.println("longueur message : " + l);
			ID id = ID.values() [dis.readByte()];
			if (l == 0) {
				return new KeepAlive();
			} else {
				System.out.println("type message : " + id);
				
				switch (id) {
				case choke:
					return new Choke();
				case unchoke:
					return new Unchoke();
				case interested:
					return new Interested();
				case notInterested:
					return new NotInterested();
				case have:
					byte[] index = new byte[l - 1];
					dis.readFully(index);
					return new Have(index);
				case bitfield:
					byte[] x = new byte[l - 1];
					dis.readFully(x);
					return new BitField(x);
				case request:
					int indexRequest = dis.readInt();
					int beginRequest = dis.readInt();
					int lengthRequest = dis.readInt();
					return new Request(indexRequest, beginRequest, lengthRequest);
				case piece:
					int indexPiece = dis.readInt();
					int beginPiece = dis.readInt();
					byte[] lengthPiece = new byte[l - 9];
					dis.readFully(lengthPiece);
					return new SendBlock(indexPiece, beginPiece, lengthPiece);
				case sendRSAKey:
					int N = dis.readInt();
					int keySize = dis.readInt();
					byte[] key = new byte[keySize];
					dis.readFully(key);
					int moduleSize = dis.readInt();
					byte[] module = new byte[moduleSize];
					dis.readFully(module);
					return new SendRSAKey(N, key, module);
				case sendSymmetricKey:
					byte[] keySym = new byte[l - 1];
					dis.readFully(keySym);
					return new SendSymmetricKey(keySym);
				default:
					int read;
					do {
						read = dis.read();
						dis.skipBytes(read);
					} while (read != -1);
					return null;
				}
			}
		} catch (IOException e) {
			System.out.println("erreur fichier");
			return null;
		}
	}
}
