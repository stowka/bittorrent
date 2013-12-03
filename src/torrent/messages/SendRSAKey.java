package torrent.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import crypto.KeyPair;

/**
 * CLASSE SesnRSAKey : modelise un message qui envoie les cles pour passer en mode cryptage RSA
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class SendRSAKey extends Message {
	private int N;
	private int keySize, moduleSize;
	private byte[] key, module;

	/**
	 * constructeur
	 * @param N int : longueur de la cle
	 * @param key byte[] cle RSA (e dans KeyPair)
	 * @param module byte[] module pour RSA ( n dans KeyPair)
	 */
	public SendRSAKey(int N, byte[] key, byte[] module) {
		super(13 + key.length + module.length, ID.sendRSAKey);
		this.N = N;
		this.key = key;
		this.keySize = key.length;
		this.module = module;
		this.moduleSize = module.length;
	}

	/**
	 * override write() et ecrit le message sur le stream pour envoyer la cle publique
	 * @param dos : DataOutputStream
	 */
	@Override
	public void write(DataOutputStream dos) {
		try {
			dos.writeInt(length);
			dos.write(id.ordinal());
			dos.writeInt(N);
			dos.writeInt(keySize);
			dos.write(key);
			dos.writeInt(moduleSize);
			dos.write(module);
			dos.flush();
		} catch (IOException e) {
			System.out.println("erreur envoi message");
		}
	}
	

	/**
	 * permet d'accepter le visiteur et le laisser lire un mesage reçu de ce type
	 * @param visitor : visiteur 
	 */
	@Override
	public void accept(MessageVisitor visitor) {
		throw new UnsupportedOperationException();
	}

}
