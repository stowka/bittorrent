package crypto;
import java.math.BigInteger;

/**
 * Classe KEYPAIR : permet de chiffrer/dechiffrer par RSA des messages.
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class KeyPair {
	
	private BigInteger n; //Modulo: (p*q)
	private BigInteger e; //Exposant
	private BigInteger d; //Cle secrete
	private int N; //Multiple de 8
	
	/**
	 * Constructeur de KeyPair, initialise les cles publiques, privees
	 * @param n : BigInteger modulo (p*q)
	 * @param e : BigInteger exposant
	 * @param d : BigInteger cle secrete
	 * @param N : int multiple de 8 
	 */
	public KeyPair(BigInteger n, BigInteger e, BigInteger d, int N) {
		this.n = n;
		this.e = e;
		this.d = d;
		this.N = 0;
		if (N % 8 == 0) {
			this.N = N;
		} else {
			System.err.println("error !");
		}
	}
	
	/**
	 * Methode de cryptage du message non-code
	 * @param m : BigInteger message non code
	 * @return le message code : c
	 */
	public BigInteger encrypt(BigInteger m) {
		BigInteger c = m.modPow(e, n);
		return c;
	}
	
	/**
	 * Methode de decryptage du message code
	 * @param c : BigInteger message code
	 * @return message decode : m
	 */
	public BigInteger decrypt(BigInteger c) {
		BigInteger m = c.modPow(d, n);
		return m;
	}
	
	/**
	 * 
	 * @return : int longueur de la clef privee
	 */
	public int getN() {
		return N;
	}
	
	public BigInteger get_n() {
		return n;
	}
	
	public BigInteger get_e() {
		return e;
	}
	
	public BigInteger get_d() {
		return d;
	}
	
}
