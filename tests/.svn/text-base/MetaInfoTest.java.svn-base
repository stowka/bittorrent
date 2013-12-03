import bencoding.*;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * CLASSE METAINFOTEST : permet de tester les informations codees dans un fichier MetaInfo
 * 
 * @author DE GIETER-VENDE Antoine && MARGUET Alban (groupe 36)
 * 
 *
 */
public class MetaInfoTest {
	
	public static void main(String[] args) {
		String author;
		long time;
		String comment;
		String announce;
		List<BEValue> announceList;
		Date date = new Date();
		
		try {
			BDecoder bdec = new BDecoder(new FileInputStream(
					"data/LePetitPrince-local.torrent"));
			BEValue dico = bdec.bdecodeMap();
			author = dico.getMap().get("created by").getString();
			time = dico.getMap().get("creation date").getLong();
			comment = dico.getMap().get("comment").getString();
			announce = dico.getMap().get("announce").getString();
			date.setTime(time * 1000);

			System.out.println("Author : " + author + "\nComment : " + comment
					+ "\nDate : " + date + "\nTrackers : ");
			
			try {
				announceList = dico.getMap().get("announce-list").getList();
				for (int i = 0; i < announceList.size(); i++) {
					System.out.println(announceList.get(i).getList().get(0).getString());
				}
			} catch (NullPointerException e) {
				System.out.println(announce);
			}
			
			
		} catch (IOException e) {
			System.out.println("Erreur fichier");
		}
	}
}
