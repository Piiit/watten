package cards;

import static org.junit.Assert.*;

import org.junit.Test;

public class WattenCardToolsTest {

	@Test
	public void testGetGuater() throws Exception {
		
		assertTrue(WattenCardTools.getGuater(new Card(Suit.LEAVES, Rank.KING)).equals(new Card(Suit.LEAVES, Rank.ACE)));
		assertTrue(WattenCardTools.getGuater(new Card(Suit.LEAVES, Rank.ACE)).equals(new Card(Suit.LEAVES, Rank.SEVEN)));
		
		assertNull(WattenCardTools.getGuater(new Card(Suit.BELLS, Rank.WELI)));
	}

	@Test
	public void testIsGuater() throws Exception {
		
		assertTrue(WattenCardTools.isGuater(new Card(Suit.LEAVES, Rank.ACE), new Card(Suit.LEAVES, Rank.KING)));
		assertTrue(WattenCardTools.isGuater(new Card(Suit.LEAVES, Rank.SEVEN), new Card(Suit.LEAVES, Rank.ACE)));
		
		assertNull(WattenCardTools.isGuater(null, new Card(Suit.BELLS, Rank.WELI)));
	}

}
