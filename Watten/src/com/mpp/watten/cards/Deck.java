package com.mpp.watten.cards;
import com.mpp.tools.xml.Loadable;
import com.mpp.tools.xml.Node;
import com.mpp.tools.xml.SimpleXML;

public class Deck extends MultipleCards implements Loadable {

	/** 
	 * Generate the whole deck of cards (One of each suit/rank pair, except for WELI, which exists only once!)
	 * @throws Exception 
	 */
	public Deck () throws Exception {
		Rank ranksArray[] = Rank.values();
		Suit suitsArray[] = Suit.values();
		
		for (Suit suit: suitsArray) {
			for (Rank rank: ranksArray) {
				if(rank != Rank.WELI) {
					addCard(new Card (suit, rank));
				}
			}
		}
		
		addCard(new Card(Suit.BELLS, Rank.WELI));
	}
	
	@Override
	public void load(Node n) {
		if (n == null) {
			throw new NullPointerException("Deck: Parameter must be a valid node!");
		}
		super.load(n);
	}
	
	@Override
	public String serialize() {
		return SimpleXML.createTag("deck", super.serialize() );
	}
	
}
