package com.mpp.watten.logic;

import com.mpp.tools.xml.Loadable;
import com.mpp.tools.xml.Node;
import com.mpp.tools.xml.SimpleXML;
import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.MultipleCards;

public class Player implements Loadable {

	private String playerName;
	private MultipleCards hand = new MultipleCards();
	
	public Player(String name) {
		playerName = name;
	}

	public String getName() {
		return playerName;
	}

	public void setName(String playerName) {
		this.playerName = playerName;
	}
	
	public void addCard(Card card) throws Exception {
		if(hand.getCount() >= 5) {
			throw new Exception("Max. 5 cards per hand!");
		}
		hand.addCard(card);
	}
	
	public MultipleCards getHand() {
		return hand;
	}

	@Override
	public String toString() {
		return "[Player: " + playerName + "]";
	}

	@Override
	public String serialize() {
		return SimpleXML.createTag("player",
				SimpleXML.createTag("name", playerName) +
				SimpleXML.createTag("hand", hand.serialize())
				);
	}

	@Override
	public void load(Node node) {
		// TODO Auto-generated method stub
		
	}
}
