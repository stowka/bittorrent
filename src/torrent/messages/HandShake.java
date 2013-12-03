package torrent.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import torrent.Torrent;

/**
 * CLASS HANDSHAKE : modelise le message HandShake.
 * Ce message est le tout premier message qui est envoye.
 * Il permet au client de se "presenter" au pair.
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 *
 */
public class HandShake {
	private static final String pstr = "BitTorrent protocol";
	private static final byte pstrlen = 19;
	private byte[] reserved;
	private byte[] info_hash;
	private byte[] peer_id;
	private boolean crypt;

	/**
	 * CONSTRUCTEUR qui N'herite PAS de Message.
	 * @param torrent : Torrent, represente le torrent pris en charge par le client.
	 */
	public HandShake(Torrent torrent) {
		this.info_hash = torrent.getInfoHash();
		this.peer_id = torrent.getPeerId();
		this.reserved = new byte[8];
		for (int i = 0; i < reserved.length; i++) {
			reserved[i] = 0;
		}
		crypt = false;
	}

	/**
	 * CONSTRUCTEUR qui N'herite PAS de Message.
	 * @param pstr : String, chaine identifiant le protocole.
	 * @param pstrlen : byte, longueur de la chaine pstr.
	 * @param reserved : byte[8], qui permet de modifier le protocole standard 
	 * 					(tous les bytes sont a zero dans le protocole standard). 
	 * @param info_hash : byte[20], signature digitale du torrent.
	 * @param peer_id : byte[20], identifiant du pair.
	 */
	public HandShake(byte[] reserved, byte[] info_hash, byte[] peer_id) {
		this.info_hash = info_hash;
		this.peer_id = peer_id;
		this.reserved = reserved;
	}

	/**
	 * methode de lecture des donnees
	 * @param dis : DataInputStream, flux de donnŽes depuis laquelle la methode va lire.
	 * @return hsPeer : HandShake, le message lu.
	 */
	public HandShake readData(DataInputStream dis) {
		HandShake hsPeer = null;
		try {
			
			//RECUPERATION DU 60 bit
//			for(int i = 0; i < 6; i++) {
//				dis.readByte();
//			}
//			byte b = dis.readByte();
//			b = (byte) (b & 0x10);
//			if (b != 0) {
//				crypt = true;
//			}
//			System.out.println("pas encore reset");
//			dis.reset();
//			System.out.println("60eme bit lu");
			//RECUPERATION DU 60 bit
			
			byte pstrlen = dis.readByte();
			byte[] pstrByte = new byte[pstrlen];
			byte[] reserved = new byte[8];
			byte[] info_hash = new byte[20];
			byte[] peer_id = new byte[20];
			dis.readFully(pstrByte);
			dis.readFully(reserved);
			dis.readFully(info_hash);
			dis.readFully(peer_id);
			hsPeer = new HandShake(reserved, info_hash, peer_id);
			System.out.println("handshake reu correct");
			return hsPeer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hsPeer;
	}

	/**
	 * methode d'ecriture des donnees
	 * @param dos : DataOutputStream, flux de donnŽes dans laquelle la methode va Žcrire.
	 */
	public void writeData(DataOutputStream dos) {
		try {
			dos.writeByte(pstrlen);
			dos.writeBytes(pstr);
			dos.write(reserved);
			dos.write(info_hash);
			dos.write(peer_id);
			dos.flush();
		} catch (IOException e) {
			System.out.println("erreur envoi handshake");
			e.printStackTrace();
		}
	}
	
	public boolean getCrypt() {
		return crypt;
	}
}
