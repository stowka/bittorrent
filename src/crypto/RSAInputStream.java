package crypto;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class RSAInputStream extends InputStream {
	private KeyPair keyPair;
	private InputStream is;
	
	public RSAInputStream(KeyPair keyPair, InputStream is) {
		this.keyPair = keyPair;
		this.is = is;
	}

	@Override
	public int read() throws IOException {
		byte[] mess = new byte[keyPair.getN() / 8 + 1];
		is.read(mess);
		BigInteger bi = new BigInteger(mess);
		return keyPair.decrypt(bi).intValue();
	}

	@Override
	public void close() throws IOException {
		is.close();
	}
	
	
	
	
	
	/*@Override
	public int read(byte[] b) throws IOException {
		BigInteger bi;
		is.read(b);
		bi = new BigInteger(b);
		return bi.intValue();
	}*/

}
