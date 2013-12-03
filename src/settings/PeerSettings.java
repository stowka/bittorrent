package settings;

public class PeerSettings {
	private static int rsaKeyLength;
	private static int symmetricKeyLength;
	public static final int DEFAULT_RSA_KEY_LENGTH = 128;
	public static final int DEFAULT_SYMMETRIC_KEY_LENGTH = 128;
	public static final int DEFAULT_CHOICE = -1;
	
	

	public PeerSettings() {
		PeerSettings.rsaKeyLength = DEFAULT_CHOICE;
		PeerSettings.symmetricKeyLength = DEFAULT_CHOICE;
	}
	
	public static void enableEncryption(int rsaLength, int symmetricLength) {
		rsaKeyLength = rsaLength;
		symmetricKeyLength = symmetricLength;
	}
	
	public static boolean encryptionEnabled() {
		if (rsaKeyLength != DEFAULT_CHOICE && symmetricKeyLength != DEFAULT_CHOICE) {
			PeerSettings.rsaKeyLength = DEFAULT_RSA_KEY_LENGTH;
			PeerSettings.symmetricKeyLength = DEFAULT_SYMMETRIC_KEY_LENGTH;
			return true;
		}
		return false;
	}
}
