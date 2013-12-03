package crypto;

import java.io.IOException;
import java.io.InputStream;

public class SymmetricInputStream extends InputStream {
	private InputStream is;
	private byte[] key;
	private int compteur;
	
	public  SymmetricInputStream(byte[] key, InputStream is) {
		this.is = is;
		this.key = key;
		this.compteur = 0;
	}

	@Override
	public int read() throws IOException {
		int valeur = 0;
		int read = is.read();
		if (read == -1) {
			valeur = -1;
		} else {
			valeur = (read & 0xFF) ^ key[compteur];
			compteur = (compteur + 1) % key.length;
		}
		return valeur;
	}

	@Override
	public void close() throws IOException {
		is.close();
	}
	
	

}
