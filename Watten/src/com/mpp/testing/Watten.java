package com.mpp.testing;

import cards.Card;
import cards.Deck;
import com.mpp.tools.GameAction;

public class Watten {
	
	private String name;
	private GameAction status;
	private char maxPoints = 18;
	private Deck deck;
	private Table table;
	
	public Watten(String name) {
		this.name = name;
		deck = new Deck();
		table = new Table();
	}

	public char getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(char maxPoints) {
		this.maxPoints = maxPoints;
	}

	@Override
	public String toString() {
		return String.format("Watten: name=%s; max. points=%d\n%s!", getName(), (int)maxPoints, table);
	}
	
	public String getName() {
		return name; 
	}
	
	public GameAction getStatus() {
		return status;
	}
	
	public void giveCards() throws Exception {
		if(table.getPlayerCount() != 4) {
			throw new Exception("We need 4 players to start the game!");
		}
		deck.shuffle();
		for(Player p : table.getPlayers()) {
			for(int i = 1; i <= 5; i++) {
				p.addCard(deck.getNextCard());
			}
		}
	}
	
	public Table getTable() {
		return table;
	}
	
	public void playCard(Card card) throws Exception {
		if(table.getCurrentPlayerCard() != null) {
			throw new Exception("Player " + table.getCurrentPlayer().getName() + " can not put another card!");
		}
		table.putCardUpdatePlayer(card);
	}

}
