package com.mpp.watten.logic;

import com.mpp.tools.PlayerLocation;
import com.mpp.tools.xml.Loadable;
import com.mpp.tools.xml.Node;
import com.mpp.tools.xml.SimpleXML;
import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.MultipleCards;

public class Player implements Loadable {

	private String playerName;
	private MultipleCards hand;
	private PlayerLocation playerLocation;
	
	public Player(String name) {
		playerName = name;
		hand = new MultipleCards();
		
	}

	public String getName() {
		return playerName;
	}

	public void setName(String playerName) {
		this.playerName = playerName;
	}
	
	public void setPlayerLocation(PlayerLocation location){
		this.playerLocation = location;
	}
	public PlayerLocation getPlayerLocation(){
		return this.playerLocation;
	}
	
	public boolean addCard(Card card) {
		if(hand.getCount() >= 5) {
//			throw new Exception("Max. 5 cards per hand!");
			return false;
		}
		hand.addCard(card);
		return true;
	}
	
	public boolean removeCard(Card card){
		if(hand.getCount()<= 0){
//			throw new Exception("No cards to remove!");
			return false;
		}
		try {
			hand.removeCard(hand.getIndex(card));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void setHand(MultipleCards hand)
	{
		this.hand = hand;
	}
	public MultipleCards getHand() {
		return hand;
	}
	
	public void clearHand(){
		hand.clear();
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
