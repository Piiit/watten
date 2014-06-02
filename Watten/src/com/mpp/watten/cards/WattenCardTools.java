package com.mpp.watten.cards;

public class WattenCardTools {
	
	public static Card getGuater(Card bestCard) throws Exception {
		if(bestCard.getRank() == Rank.WELI) {
			return null;
		}
		int index = bestCard.getRank().getIndex() % 8 + 1;
		return new Card(bestCard.getSuit(), Rank.get(index));
	}

	public static Card getRechter(Card bestCard) {
		if(bestCard.getRank() == Rank.WELI) {
			return null;
		}
		return bestCard;
	}
	
	public static boolean isGuater(Card card, Card bestCard) throws Exception {
		if(card == null || bestCard == null) {
			throw new NullPointerException("Card and best card must be set!");
		}
		return (card.equals(getGuater(bestCard)));
	}
	
	public static boolean isRechter(Card card, Card bestCard) {
		if(card == null) {
			throw new NullPointerException("Card and best card must be set!");
		}
		return (card.equals(getRechter(bestCard)));
	}
	
	public static boolean isBlinder(Card card, Card bestCard) {
		if(card == null || bestCard == null) {
			throw new NullPointerException("Card and best card must be set!");
		}
		if(card.getRank() != Rank.WELI && card.getSuit() != bestCard.getSuit() && card.getRank() == bestCard.getRank()) {
			return true;
		}
		return false;
	}
	
	public static boolean isSchlog(Card card, Card bestCard) {
		if(card == null || bestCard == null) {
			throw new NullPointerException("Card and best card must be set!");
		}
		if(card.getRank() == bestCard.getRank()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * <pre>
	 * Allowed cards:
	 * a) if first thrown card has best card's suit:
	 *    - add: cards with same suit, all "Schlog" cards and "Guater"
	 * b) if first thrown card has not best card's suit:
	 * 	  - if best card's rank is WELI, add: cards with same suit as best card's suit
	 *    - else: add all cards of the current player's hand
	 * </pre>
	 * @return set of allowed cards of current player
	 * @throws Exception when current player is not set
	 */
	public static MultipleCards getAllowedCards(MultipleCards hand, Card firstThrownCard, Card bestCard) throws Exception {
		
		// Nothing thrown, return full hand...
		if (firstThrownCard == null) {
			return hand;
		}
		
		// First thrown card has different suit as best card's suit and it is not WELI while WELI is the best card's rank...
		if(firstThrownCard.getSuit() != bestCard.getSuit()) {
			if(bestCard.getRank() == Rank.WELI) {
				if(firstThrownCard.getRank() != Rank.WELI) {
					return hand;
				}
			} else {
				return hand;
			}
		}

		MultipleCards allowedCards = new MultipleCards();
		for (int cardIndex = hand.getIndex(); cardIndex < hand.getCount(); cardIndex++) {
			Card c = hand.getCard(cardIndex);
			if (c.getSuit() == bestCard.getSuit() || WattenCardTools.isSchlog(c, bestCard) || WattenCardTools.isGuater(c, bestCard)) {
				allowedCards.addCard(c);
			}
		}
		if (allowedCards.getCount() == 0) {
			return hand;
		}
		return allowedCards;
	}

	/**
	 * Returns true if this.card is the better card, where this.card is considered to be thrown first...
	 * @param card1
	 * @param card2
	 * @return
	 * @throws Exception 
	 */
	public static boolean isBetterCardChronological(Card card, Card card2, Card bestCard) throws Exception {
		if(isGuater(card, bestCard)) {
			return true;
		}
		if(isGuater(card2, bestCard)) {
			return false;
		}
		if(isRechter(card, bestCard)) {
			return true;
		}
		if(isRechter(card2, bestCard)) {
			return false;
		}
		if(isBlinder(card, bestCard)) {
			return true;
		}
		if(isBlinder(card2, bestCard)) {
			return false;
		}
		if(card.getSuit() == bestCard.getSuit() && card2.getSuit() == bestCard.getSuit()) {
			if(card.getRank().getIndex() > card2.getRank().getIndex()) {
				return true;
			}
			return false;
		}
		if(card.getSuit() == bestCard.getSuit()) {
			return true;
		}
		if(card2.getSuit() == bestCard.getSuit()) {
			return false;
		}
		if(card.getSuit() == card2.getSuit()) {
			if(card.getRank().getIndex() > card2.getRank().getIndex()) {
				return true;
			}
			return false;
		}
		return true;
	}

}
