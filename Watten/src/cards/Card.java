package cards;

import xml.Loadable;
import xml.Node;
import xml.SimpleXML;

public class Card implements Comparable<Card>, Loadable {

	private static Suit bestCardSuit = null;
	private static Rank bestCardRank = null;
	
	private Suit suit;
	private Rank rank;
	boolean faceDown;
	
	public Card (Suit suit, Rank rank, boolean faceDown) {
		this.rank = rank;
		this.suit = suit;
		this.faceDown = faceDown;
	}
	
	public Card (Suit suit, Rank rank) {
		this(suit, rank, true);
	}
	
	/**
	 * Constructor of a card with a card-configuration-string
	 * @param cfg card configuration string suit:rank:faceDown (example: Diamond:c2:false)
	 */
	public Card (String cfg) {
		String arr[] = cfg.split(":");
		try {
			suit = Suit.valueOf(arr[0]);
			rank = Rank.valueOf(arr[1]);
			faceDown = Boolean.parseBoolean(arr[2]);
		} catch (IllegalArgumentException ie) {
			throw new Error ("'" + cfg + "' is not a valid card configuration string!");
		}
	}
	
	public void faceDown() {
		faceDown = true;
	}
	
	public void faceUp() {
		faceDown = false;
	}
		
	public String toString() {
		if (faceDown) {
			return "{?-FACE-DOWN-?}";
		}
		return toStringFaceUp();
	}
	
	public String toStringFaceUp() {
		return "{" + suit + ":" + rank + "}";
	}

	
	public boolean isFaceDown() {
		return faceDown;
	}

	@Override
	public int compareTo(Card card) {
		if (card.getRank().ordinal() > rank.ordinal()) {
			return -1;
		}
		if (card.getRank().ordinal() < rank.ordinal()) {
			return 1;
		}
		return 0;
	}
	
	public boolean equals(Card card) {
		if (getRank() == card.getRank() && getSuit() == card.getSuit()) {
			return true;
		}
		return false;
	}
	
	public Suit getSuit() {
		return suit;
	}

	public Rank getRank() {
		return rank;
	}

	@Override
	public String serialize() {
		return SimpleXML.createTag("card", 
				SimpleXML.createTag("suit", suit.toString()) + 
				SimpleXML.createTag("rank", rank.toString()) + 
				SimpleXML.createTag("facedown", Boolean.toString(faceDown))
				);
	}

	public String toStringDebug() {
		return "{" + suit + ":" + rank + ":" + (faceDown ? "face down" : "face up") + "}";
	}

	@Override
	public void load(Node node) {
		// TODO Auto-generated method stub
	}
	
	public static void setBestCardSuit(Suit suit) {
		bestCardSuit = suit;
	}

	public static void setBestCardRank(Rank rank) {
		bestCardRank = rank;
	}

	public static Suit getBestCardSuit() {
		return bestCardSuit;
	}

	public static Rank getBestCardRank() {
		return bestCardRank;
	}
	
	public boolean isGuater() {
		if(getRank() != Rank.WELI && getSuit() == bestCardSuit) {
			int index = bestCardRank.getIndex() + 1;
			if(index > 8) {
				index = 1;
			}
			if(getRank() == Rank.get(index)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isRechter() {
		if(getRank() != Rank.WELI && getSuit() == bestCardSuit && getRank() == bestCardRank) {
			return true;
		}
		return false;
	}
	
	public boolean isBlinder() {
		if(getRank() != Rank.WELI && getSuit() != bestCardSuit && getRank() == bestCardRank) {
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
	public boolean isBetterCardChronological(Card card2) {
		if(isGuater()) {
			return true;
		}
		if(card2.isGuater()) {
			return false;
		}
		if(isRechter()) {
			return true;
		}
		if(card2.isRechter()) {
			return false;
		}
		if(isBlinder()) {
			return true;
		}
		if(card2.isBlinder()) {
			return false;
		}
		if(getSuit() == bestCardSuit && card2.getSuit() == bestCardSuit) {
			if(getRank().getIndex() > card2.getRank().getIndex()) {
				return true;
			}
			return false;
		}
		if(getSuit() == bestCardSuit) {
			return true;
		}
		if(card2.getSuit() == bestCardSuit) {
			return false;
		}
		if(getSuit() == card2.getSuit()) {
			if(getRank().getIndex() > card2.getRank().getIndex()) {
				return true;
			}
			return false;
		}
		return true;
	}

}