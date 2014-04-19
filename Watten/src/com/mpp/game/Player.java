package com.mpp.game;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.tablelayout.Cell;
import com.mpp.tools.Rank;
import com.mpp.tools.Suit;
import com.mpp.ui.PlayedCardArea;
import com.mpp.ui.PlayerInfoTable;

public class Player {

	String playerName;
	String playerID;
	int teamNumber;
	int roundWins; // Stich
	boolean local = false;
	Cell[] hand;
	Cell playedCardCell;
	Cell playerInfoCell;
	int cellCounter;
	PlayedCardArea playedCardArea;
	PlayerInfoTable playerInfoTable;

	public Player(String name) {
		// TODO Auto-generated constructor stub
		playerName = name;
		roundWins = 0;
		cellCounter = 0;
		hand = new Cell[5];
		playedCardArea = new PlayedCardArea(0, 0);
		playerInfoTable = new PlayerInfoTable(this);

	}

	// Adds player to table
	public void addToTable(Cell playedCardCell, Cell playerInfoCell, Cell[] hand) {
		this.playedCardCell = playedCardCell;
		this.playedCardCell.setWidget(playedCardArea);

		this.playerInfoCell = playerInfoCell;
		this.playerInfoCell.setWidget(playerInfoTable);

		if (local) {
			this.hand = hand;

			// TESTING
			addCard(new Card(Suit.ACORNS, Rank.ACE, false, this));
			addCard(new Card(Suit.ACORNS, Rank.ACE, false, this));
			addCard(new Card(Suit.ACORNS, Rank.ACE, false, this));
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
		this.hand = hand;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public String getPlayerName() {
		return playerName;
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

	public void setPlayerID(String _playerID) {
		playerID = _playerID;
	}

	public void resetForRound() {
		roundWins = 0;

	}

	// Adds card to players hand
	public void addCard(Card card) {
		if (local) {
			card.setParentCell(hand[cellCounter]);
			hand[cellCounter].setWidget(card);
			cellCounter++;
		}

	}

	/*
	 * Adds card to playedcardarea, if local removes from hand, checks if card
	 * playable goes here
	 */
	public void playCard(Card card) {
		if (local) {
			card.removeFromParent();
		}
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
