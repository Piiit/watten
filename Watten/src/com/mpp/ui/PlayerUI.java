package com.mpp.ui;

import java.util.ArrayList;

import com.esotericsoftware.tablelayout.Cell;
import com.mpp.tools.PlayerLocation;
import com.mpp.ui.message.MessageDialog;
import com.mpp.watten.cards.MultipleCards;
import com.mpp.watten.logic.Player;

public class PlayerUI {

	int teamNumber;
	int tricks; // Stich

	boolean local = false;
	boolean select_rank = false;
	boolean select_suit = false;
	boolean playing = false;

	Cell[] handCells;
	Cell playedCardCell;
	Cell playerInfoCell;
	Cell chatCell;

	PlayedCardArea playedCardArea;
	PlayerInfoTable playerInfoTable;
	ChatWidget chatWidget;
	Player player;
	ArrayList<Card2D> handUI = new ArrayList<Card2D>();
	WattenGame game;

	public PlayerUI(Player player) {
		// TODO Auto-generated constructor stub
		this.player = player;
		tricks = 0;
		handCells = new Cell[5];
		playedCardArea = new PlayedCardArea(0, 0);
		playerInfoTable = new PlayerInfoTable(this);

	}

	// Adds player to table
	public void addToTable(Cell playedCardCell, Cell playerInfoCell,
			Cell[] handCells, Cell chatWidgetCell) {
		this.playedCardCell = playedCardCell;
		this.playedCardCell.setWidget(playedCardArea);

		this.playerInfoCell = playerInfoCell;
		this.playerInfoCell.setWidget(playerInfoTable);

		if (local) {
			this.handCells = handCells;
			this.chatCell = chatWidgetCell;
			chatWidget = new ChatWidget(game);
			this.chatCell.setWidget(chatWidget);
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
		return tricks;
	}

	// Simple turn win tracking counter(stich)
	public void winsTurn() {
		tricks++;
	}

	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	// Clears all of the data regarding the players hand(cards)
	public void resetHand() {
		if (local) {
			for (int i = 0; i < handCells.length; i++) {
				handCells[i].setWidget(null);
			}
			player.clearHand();
			player.setHand(null);
		}
		handUI = new ArrayList<Card2D>();
		handUI.clear();
		System.out.println("reset hand");

	}

	/*
	 * Resets the players tricks and his hand after a round
	 */
	public void resetForRound() {
		tricks = 0;
		resetHand();
		System.out.println("Reset for round!");
	}

	/*
	 * Resets the players playedcardarea removing any card he played last time
	 */
	public void resetForTurn() {
		playedCardArea.removeCard();

	}

	/*
	 * Removes the player from the current table, works for remote ones too
	 */
	public void leaveTable() {
		resetForTurn();
		resetForRound();
		this.playedCardCell.setWidget(null);
		this.playerInfoCell.setWidget(null);

		if (local)
			chatWidget = null;
			game.toMainMenu();
		

	}

	public PlayerLocation getPlayerLocation() {
		return player.getPlayerLocation();
	}

	public void setPlayerLocation(PlayerLocation location) {
		player.setPlayerLocation(location);
	}

	/*
	 * Adds card to players hand, if the player is local it adds it to the
	 * screen
	 */

	public void addCard(Card2D card, int handIndex) {
		handUI.add(card);
		if (local) {

			card.setParentCell(handCells[handIndex]);
			handCells[handIndex].setWidget(card);
		}

	}

	/*
	 * Adds an entire hand
	 */
	public void addHand(MultipleCards hand) {
		player.setHand(hand);
		if (handUI != null)
			System.out.println("HandUI lenght before add: " + handUI.size());
		else
			System.out.println("HandUI null");
		for (int i = 0; i < hand.getAllCards().size(); i++)
			addCard(new Card2D(hand.getCard(i), this), i);

	}

	/*
	 * Adds card to playedcardarea, if local removes from hand, checks if card.
	 * Only called if a play actions is receives an ACK from the server
	 */
	public void playCard(Card2D card) {

		if (local) {
			card.removeFromParent();
			player.removeCard(card.getCard());
			handUI.remove(card);

		}
		// Add card to playedcardarea, same for all players
		playedCardArea.addCard(card);
	}

	public boolean isLocalPlayer() {
		return local;
	}

	public void setLocalPlayer(boolean isLocal) {
		local = isLocal;
	}

	public void handReveal() {
		player.getHand().revealAllCards();
		checkCardFacing();
	}

	public void handHide() {
		player.getHand().hideAllCards();
		checkCardFacing();
	}

	public void checkCardFacing() {
		for (int i = 0; i < handUI.size(); i++) {
			if (handUI.get(i) != null)
				handUI.get(i).flipCard();

		}
	}

	public boolean isSelect_rank() {
		return select_rank;
	}

	public void setSelect_rank(boolean select_rank) {
		this.select_rank = select_rank;
	}

	public boolean isSelect_suit() {
		return select_suit;
	}

	public void setSelect_suit(boolean select_suit) {
		this.select_suit = select_suit;
	}

	public boolean isPlaying() {
		return playing;
	}

	public synchronized void setPlaying(boolean playing) {
		this.playing = playing;
		System.out.println("playing: " + this.playing);
	}

	public int getCardIndex(Card2D card) throws Exception {
		return player.getHand().getIndex(card.getCard());

	}

	public Card2D getCard2D(int index) {
		return handUI.get(index);
	}

	/*
	 * Sends a suit_selected request to the server
	 */
	public void selectSuit(Card2D card) {
		try {
			game.sendRequest("suit_selected " + getCardIndex(card));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageDialog.createErrorDialog("Suit select error: "
					+ e.getMessage());
		}
	}

	/*
	 * Sends a rank_selected request to the server
	 */
	public void selectRank(Card2D card) {
		try {
			game.sendRequest("rank_selected " + getCardIndex(card));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageDialog.createErrorDialog("Rank select error: "
					+ e.getMessage());
		}
	}

	/*
	 * Sends a card_played request to the server
	 */
	public void requestCardPlay(Card2D card) {
		try {
			game.sendRequest("card_played " + getCardIndex(card));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageDialog.createErrorDialog("Card play select error: "
					+ e.getMessage());
		}
	}

	/*
	 * Appends a new message to the chatwidget
	 */
	public void postChatMessage(String text) {
		if (local && chatWidget != null)
			chatWidget.addMessage(text);
	}

	public void setWattenGame(WattenGame game) {
		this.game = game;
	}
}
