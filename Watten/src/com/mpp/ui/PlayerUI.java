package com.mpp.ui;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.tablelayout.Cell;
import com.mpp.tools.PlayerLocation;
import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.MultipleCards;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;
import com.mpp.watten.logic.Player;

public class PlayerUI {

	int teamNumber;
	int roundWins; // Stich
	boolean local = false;
	Cell[] handCells;
	Cell playedCardCell;
	Cell playerInfoCell;
	PlayedCardArea playedCardArea;
	PlayerInfoTable playerInfoTable;
	Player player;
	Card2D[] handUI = new Card2D[5];

	public PlayerUI(Player player) {
		// TODO Auto-generated constructor stub
		this.player = player;
		roundWins = 0;
		handCells = new Cell[5];
		playedCardArea = new PlayedCardArea(0, 0);
		playerInfoTable = new PlayerInfoTable(this);

	}

	// Adds player to table
	public void addToTable(Cell playedCardCell, Cell playerInfoCell,
			Cell[] handCells) {
		this.playedCardCell = playedCardCell;
		this.playedCardCell.setWidget(playedCardArea);

		this.playerInfoCell = playerInfoCell;
		this.playerInfoCell.setWidget(playerInfoTable);

		if (local) {
			this.handCells = handCells;

			// TESTING
			try {
				
				// addCard(new Card2D(new Card(Suit.ACORNS, Rank.ACE, true),
				// this));
				// addCard(new Card2D(new Card(Suit.ACORNS, Rank.ACE, true),
				// this));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("cards added");
			// TESTING
		}

	}

	// Removes player form table
	public void removeFromTable() {

		this.playedCardCell.setWidget(null);

		this.playerInfoCell.setWidget(null);
	}

	// If player local, sets cells where hand is
	public void setHandCells(Cell[] hand) {
		this.handCells = hand;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public String getPlayerName() {
		return player.getName();
	}

	public int getRoundWins() {
		return roundWins;
	}

	// Simple round tracking counter(stich)
	public void winsRound() {
		roundWins++;
	}

	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	public void resetHand() {

		for (int i = 0; i < handCells.length; i++) {
			handCells[i].setWidget(null);
		}
		handUI = new Card2D[5];
		player.setHand(null);
	}

	public void resetForRound() {
		roundWins = 0;
		resetHand();
	}

	public PlayerLocation getPlayerLocation() {
		return player.getPlayerLocation();
	}

	public void setPlayerLocation(PlayerLocation location) {
		player.setPlayerLocation(location);
	}

	// Adds card to players hand
	public void addCard(Card2D card, int handIndex) {
		System.out.println("Cell counter");
		if (local) {
			card.setParentCell(handCells[handIndex]);
			handCells[handIndex].setWidget(card);
			System.out.println("Adding card to table");
		}

	}

	public void addHand(MultipleCards hand) {
		player.setHand(hand);

		for (int i = 0; i < hand.getAllCards().size(); i++)
			addCard(new Card2D(hand.getCard(i), this), i);

		System.out.println(hand.toString());
		System.out.println("Hand added");
	}

	/*
	 * Adds card to playedcardarea, if local removes from hand, checks if card
	 * playable goes here
	 */
	public void playCard(Card2D card) {
		if (local) {
			card.removeFromParent();
		}
		player.removeCard(card.getCard());
		// Add card to playedcard area
		playedCardArea.addCard(card);
	}

	public boolean isLocalPlayer() {
		return local;
	}

	public void setLocalPlayer(boolean isLocal) {
		local = isLocal;
	}

}
