package crypto;

import java.io.IOException;
import java.io.OutputStream;

public class SymmetricOutputStream extends OutputStream {
	private byte[] key;
	private OutputStream os;
	private int compteur;
	
	public SymmetricOutputStream(byte[] key, OutputStream os) {
		this.os = os;
		this.key = key;
		this.compteur = 0;
	}

	@Override
	public void write(int b) throws IOException {
		int valeur = b ^ key[compteur];
		compteur = (compteur + 1) % key.length;
		os.write(valeur);
		
	}

	@Override
	public void close() throws IOException {
		os.close();
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}

	
}
