package com.mpp.ui;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.tablelayout.Cell;
<<<<<<< HEAD
import com.mpp.tools.PlayerLocation;
import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;
import com.mpp.watten.logic.Player;

public class PlayerUI {

	int teamNumber;
	int roundWins; // Stich
	boolean local = false;
	Cell[] hand;
	Cell playedCardCell;
	Cell playerInfoCell;
	int cellCounter;
	PlayedCardArea playedCardArea;
	PlayerInfoTable playerInfoTable;
	Player player;

	public PlayerUI(Player player) {
		// TODO Auto-generated constructor stub
		this.player = player;
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
			try {
				addCard(new Card2D(new Card(Suit.ACORNS, Rank.ACE, true), this));
				addCard(new Card2D(new Card(Suit.ACORNS, Rank.ACE, true), this));
				addCard(new Card2D(new Card(Suit.ACORNS, Rank.ACE, true), this));
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
		this.hand = hand;
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
		cellCounter = 0;
		for (int i = 0; i < hand.length; i++) {
			hand[i].setWidget(null);
		}
	}

	public void resetForRound() {
		roundWins = 0;

	}

	public PlayerLocation getPlayerLocation() {
		return player.getPlayerLocation();
	}

	public void setPlayerLocation(PlayerLocation location) {
		player.setPlayerLocation(location);
	}

	// Adds card to players hand
	public void addCard(Card2D card) {
		System.out.println("Cell counter");
		if (local && cellCounter <5) {
			card.setParentCell(hand[cellCounter]);
			hand[cellCounter].setWidget(card);
			cellCounter++;
		}

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
=======
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;

public class PlayerUI {

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

	public PlayerUI(String name) {
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
			addCard(new Card2D(Suit.ACORNS, Rank.ACE, false, this));
			addCard(new Card2D(Suit.ACORNS, Rank.ACE, false, this));
			addCard(new Card2D(Suit.ACORNS, Rank.ACE, false, this));
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
	public void addCard(Card2D card) {
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
	public void playCard(Card2D card) {
		if (local) {
			card.removeFromParent();
		}
>>>>>>> refs/remotes/origin/master
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
