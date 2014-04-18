package com.mpp.testing;

import cards.Card;
import cards.MultipleCards;

public class Player {

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
}
