package cards;

public class WattenCardTools {
	
	public static Card getGuater(Card bestCard) {
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
	
	public static boolean isGuater(Card card, Card bestCard) {
		if(card == null) {
			throw new NullPointerException("Card can not be null!");
		}
		return (getGuater(bestCard).equals(card));
	}
	
	public static boolean isRechter(Card card, Card bestCard) {
		if(card == null) {
			throw new NullPointerException("Card can not be null!");
		}
		return (getRechter(bestCard).equals(card));
	}
	
	public static boolean isBlinder(Card card, Card bestCard) {
		if(card == null) {
			throw new NullPointerException("Card can not be null!");
		}
		if(card.getRank() != Rank.WELI && card.getSuit() != bestCard.getSuit() && card.getRank() == bestCard.getRank()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this.card is the better card, where this.card is considered to be thrown first...
	 * @param card1
	 * @param card2
	 * @return
	 */
	public static boolean isBetterCardChronological(Card card, Card card2, Card bestCard) {
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
