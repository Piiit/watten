package com.mpp.watten.cards;

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
		
		try {
			WattenCardTools.isGuater(new Card(Suit.BELLS, Rank.WELI), null);
			fail("NullPointerException not thrown!");
		} catch(Exception e) {
		}
	}
	
	@Test
	public void testAllowedCards() throws Exception {
		MultipleCards hand = new MultipleCards();
		hand.addCard(new Card(Suit.ACORNS, Rank.EIGHT));
		hand.addCard(new Card(Suit.ACORNS, Rank.NINE));
		hand.addCard(new Card(Suit.BELLS, Rank.ACE));
		hand.addCard(new Card(Suit.HEARTS, Rank.EIGHT));
		hand.addCard(new Card(Suit.LEAVES, Rank.TEN));
		
		Card bestCard = new Card(Suit.LEAVES, Rank.EIGHT);
		
		// No card thrown yet! -> return full hand...
		Card firstThrownCard = null;
		MultipleCards validCards = WattenCardTools.getAllowedCards(hand, firstThrownCard, bestCard);
		assertEquals(5, validCards.getCount());
		
		// First card with a suit that does not match best card's suit... -> return full hand
		firstThrownCard = new Card(Suit.ACORNS, Rank.JACK);
		validCards = WattenCardTools.getAllowedCards(hand, firstThrownCard, bestCard);
		assertEquals(5, validCards.getCount());
		
		// First card with best card's suit -> return all leave cards and "Schlog"/"Guater" cards
		firstThrownCard = new Card(Suit.LEAVES, Rank.JACK);
		validCards = WattenCardTools.getAllowedCards(hand, firstThrownCard, bestCard);
		assertEquals(3, validCards.getCount());
		assertEquals("{ACORNS:EIGHT}", validCards.getCard(0).toStringFaceUp());
		assertEquals("{HEARTS:EIGHT}", validCards.getCard(1).toStringFaceUp());
		assertEquals("{LEAVES:TEN}", validCards.getCard(2).toStringFaceUp());
	}

	
	@Test
	public void testAllowedCardsWeliIsBestCard() throws Exception {
		MultipleCards hand = new MultipleCards();
		hand.addCard(new Card(Suit.ACORNS, Rank.EIGHT));
		hand.addCard(new Card(Suit.ACORNS, Rank.NINE));
		hand.addCard(new Card(Suit.BELLS, Rank.ACE));
		hand.addCard(new Card(Suit.HEARTS, Rank.EIGHT));
		hand.addCard(new Card(Suit.LEAVES, Rank.TEN));
		
		Card bestCard = new Card(Suit.HEARTS, Rank.WELI);
		
		// No card thrown yet! -> return full hand...
		Card firstThrownCard = null;
		MultipleCards validCards = WattenCardTools.getAllowedCards(hand, firstThrownCard, bestCard);
		assertEquals(5, validCards.getCount());
		
		// First card with a suit that does not match best card's suit... -> return full hand
		firstThrownCard = new Card(Suit.ACORNS, Rank.JACK);
		validCards = WattenCardTools.getAllowedCards(hand, firstThrownCard, bestCard);
		assertEquals(5, validCards.getCount());
		
		// First card is WELI -> return all hearts cards...
		firstThrownCard = new Card(Suit.BELLS, Rank.WELI);
		validCards = WattenCardTools.getAllowedCards(hand, firstThrownCard, bestCard);
		assertEquals(1, validCards.getCount());
		assertEquals("{HEARTS:EIGHT}", validCards.getCard(0).toStringFaceUp());

		// Removing BELLS:ACE, adding WELI...
		hand.removeCard(2);
		hand.addCard(new Card(Suit.BELLS, Rank.WELI));
		
		// First card is a heart card (=best suit) -> return all hearts cards and WELI
		firstThrownCard = new Card(Suit.HEARTS, Rank.JACK);
		validCards = WattenCardTools.getAllowedCards(hand, firstThrownCard, bestCard);
		assertEquals(2, validCards.getCount());
		assertEquals("{HEARTS:EIGHT}", validCards.getCard(0).toStringFaceUp());
		assertEquals("{BELLS:WELI}", validCards.getCard(1).toStringFaceUp());
}

}
