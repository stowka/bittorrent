import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import torrent.piece.Piece;

public class PieceTest {

    private static MessageDigest shaDigest;

    @BeforeClass
    public static void setup() {
        try {
            shaDigest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Your Java runtime doesn't support SHA-1 digests - that's incredible but we can't continue.", e);
        }
    }

    @Test
    public void testCheckCorrectData() {
        byte[] data = new byte[Piece.BLOCK_SIZE * 4];
        Random r = new Random();
        r.nextBytes(data);
        
        byte[] hash = shaDigest.digest(data);
        Piece piece = new Piece(2, data.length, hash);

        assertNull(piece.getData());
        assertEquals(0, piece.getDownloadCompleteness());

        piece.feed(Piece.BLOCK_SIZE * 0, Arrays.copyOfRange(data, Piece.BLOCK_SIZE * 0, Piece.BLOCK_SIZE * 1));
        assertEquals(25, piece.getDownloadCompleteness());
        
        piece.feed(Piece.BLOCK_SIZE * 3, Arrays.copyOfRange(data, Piece.BLOCK_SIZE * 3, Piece.BLOCK_SIZE * 4));
        assertEquals(50, piece.getDownloadCompleteness());
        
        piece.feed(Piece.BLOCK_SIZE * 1, Arrays.copyOfRange(data, Piece.BLOCK_SIZE * 1, Piece.BLOCK_SIZE * 2));
        assertEquals(75, piece.getDownloadCompleteness());
        
        piece.feed(Piece.BLOCK_SIZE * 0, Arrays.copyOfRange(data, Piece.BLOCK_SIZE * 0, Piece.BLOCK_SIZE * 1));
        assertEquals(75, piece.getDownloadCompleteness()); // Duplicate receive should not increment counter
        
        piece.feed(Piece.BLOCK_SIZE * 2, Arrays.copyOfRange(data, Piece.BLOCK_SIZE * 2, Piece.BLOCK_SIZE * 3));
        assertEquals(100, piece.getDownloadCompleteness());
        
        assertTrue(piece.isComplete());
        assertTrue(piece.check());
        assertArrayEquals(data, piece.getData());
    }

    @Test
    public void testCheckIncorrectData() throws Exception {
        byte[] data = "Dummy data Dummy data 252".getBytes("UTF-8");
        byte[] hash = shaDigest.digest(data);
        Piece piece = new Piece(2, 25, hash);

        byte[] block = "Dummy data Dummy data 253".getBytes("UTF-8");
        piece.feed(0, block);

        // the check is done automatically when the piece is 'completed'
        assertEquals(0, piece.getDownloadCompleteness());
        assertTrue(!piece.isComplete());
        
        piece.feed(0, data);
        assertEquals(true, piece.check());
        
        piece.feed(0, block);
        assertEquals(true, piece.check());
        // if we give an already checked piece some wrong data, it must not be checked
    }
    
    public Piece setupPiece(final int length) {
        byte[] data = new byte[length];
        new Random().nextBytes(data);
        
        return new Piece(42, length, shaDigest.digest(data));
    }
    
    public void testIllegalBegin1(Piece piece) throws UnsupportedEncodingException {
        setupPiece(Piece.BLOCK_SIZE + 1).feed(Piece.BLOCK_SIZE - 1, new byte[Piece.BLOCK_SIZE]); // invalid begin
    }

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalBegin2() throws UnsupportedEncodingException {
		setupPiece(Piece.BLOCK_SIZE + 1).feed(-1, new byte[Piece.BLOCK_SIZE]); // index cannot be negative
	}
    
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalBegin3() throws UnsupportedEncodingException {
		setupPiece(Piece.BLOCK_SIZE + 1).feed(2 * Piece.BLOCK_SIZE, new byte[Piece.BLOCK_SIZE]); // index cannot be too big
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalBlockLength1() throws UnsupportedEncodingException {
		setupPiece(Piece.BLOCK_SIZE + 1).feed(0, new byte[Piece.BLOCK_SIZE / 2]); // invalid block length
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalBlockLength2() throws UnsupportedEncodingException {
		setupPiece(Piece.BLOCK_SIZE + 1).feed(0, new byte[Piece.BLOCK_SIZE + 1]); // invalid block length
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalBlockLength3() throws UnsupportedEncodingException {
		setupPiece(Piece.BLOCK_SIZE + 1).feed(Piece.BLOCK_SIZE, new byte[Piece.BLOCK_SIZE + 1]); // invalid begin or block length
	}
}
