package crypto;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Classe KEYGENERATOR : permet de generer une paire de cles publiques/privees.
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (grouep 36)
 *
 */
public class KeyGenerator {
	
	/**
	 * Methode generateRSAKeyPair : genere les cles
	 * @param N : int longueur de la cle
	 * @return objet KeyPair keys : paire de cles
	 */
	public static KeyPair generateRSAKeyPair(int N) {
		
		//Générer un nombre aléatoire cryptographiquement fort
		SecureRandom rnd = new SecureRandom();
		BigInteger p, q;
		
		//Générer p et q premiers
		int l = N / 2; //longueur de la clé divisée par 2 = longueur de p et q
		do {
			p = BigInteger.probablePrime(l, rnd);
			q = BigInteger.probablePrime(l, rnd);
		} while (p.equals(q));
		
		//n = (p * q)
		BigInteger n = p.multiply(q);
		
		//Création des variables décrémentées de p et q
		BigInteger pDec = p.subtract(BigInteger.ONE);
		BigInteger qDec = q.subtract(BigInteger.ONE);
		
		//phi = (p - 1) * (q - 1)
		BigInteger phi = pDec.multiply(qDec);
		BigInteger e = new BigInteger("65537");
		
		//Créer la clé privée
		BigInteger d = e.modInverse(phi);
		KeyPair keys = new KeyPair(n, e, d, N);
		
		return keys;
	}
	
	public static byte[] generateSymmetricKey(int keyLength) {
		SecureRandom sr = new SecureRandom();
		byte[] bytes = new byte[keyLength];
		sr.nextBytes(bytes);
		return bytes;
	}
}
