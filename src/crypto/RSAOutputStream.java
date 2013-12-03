package crypto;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class RSAOutputStream extends OutputStream {
	private KeyPair keyPair;
	private OutputStream os;
	
	public RSAOutputStream(KeyPair keyPair, OutputStream os) {
		this.keyPair = keyPair;
		this.os = os;
	}
	
	@Override
	public void write(int b) throws IOException {
		int c = b & 0xFF;
		BigInteger bi = BigInteger.valueOf(c);
		byte[] b1 = new byte[keyPair.getN() / 8 + 1];
		byte[] mess = keyPair.encrypt(bi).toByteArray();
		System.arraycopy(mess, 0, b1, b1.length - mess.length, mess.length);
		os.write(mess);
		os.flush();
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
