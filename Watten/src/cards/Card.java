package cards;

import xml.Loadable;
import xml.Node;
import xml.SimpleXML;

public class Card implements Comparable<Card>, Loadable {

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
	
	public Card () {
		this(null, null, true);
	}
	
	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
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
}