
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import torrent.Torrent;
import torrent.piece.Piece;
import bencoding.InvalidBEncodingException;

public class UpdatePiecePriorityTest {

	@Test
	public void testPieceFeeding() throws InvalidBEncodingException, IOException {
		Torrent torrent = new Torrent("data/LePetitPrince.torrent");
		torrent.setWritingEnabled(true);
		
		int pieceLength = torrent.getPieces().get(0).getLength();
		int pieceCount = torrent.getPieces().size();
		
		List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < pieceCount; i++) {
			indices.add(i);
		}
		Collections.shuffle(indices);
		Assert.assertEquals(pieceCount, indices.size());
		Assert.assertEquals(pieceLength, 1 << 15);
		
		RandomAccessFile file = new RandomAccessFile("data/st_exupery_le_petit_prince.pdf", "r");
		for(int index: indices) {
			Piece piece = torrent.getPieces().get(index);
			
			// do the second block first, if any
			int secondBlockSize = piece.getLength() - Piece.BLOCK_SIZE;
			if(secondBlockSize > 0) {
				file.seek(index * pieceLength + Piece.BLOCK_SIZE);
				byte[] block = new byte[secondBlockSize];
				file.read(block);
				piece.feed(Piece.BLOCK_SIZE, block);
			}
			
			// now do the first block
			{
				int firstBlockSize = Math.min(Piece.BLOCK_SIZE, piece.getLength()); 
				file.seek(index * pieceLength);
				byte[] block = new byte[firstBlockSize];
				file.read(block);
				piece.feed(0, block);
			}
		}
		file.close();
		//torrent.getPieceManager().showProgress();
		torrent.getPieceManager().updatePriorities();
		Assert.assertTrue(torrent.isComplete());
	}
	
}
