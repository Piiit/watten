package com.mpp.watten.logic;

import com.mpp.tools.PlayerLocation;
import com.mpp.tools.xml.Loadable;
import com.mpp.tools.xml.Node;
import com.mpp.tools.xml.SimpleXML;
import com.mpp.watten.cards.Card;

public class Table implements Loadable {

	private static final String DESCRIPTION_EMPTY_SEAT = "(empty)";
	private static final String DESCRIPTION_EMPTY_CARDSLOT = "(no card)";

	private Player playerList[] = new Player[4];
	private Card cardList[] = new Card[4];
	private int currentPlayerIndex = 0;

	public void addPlayer(Player player, PlayerLocation seat) throws Exception {
		if (player == null) {
			throw new NullPointerException("Player can not be null!");
		}
		if (seat != null && !isFreeSeat(seat)) {
			throw new Exception("Seat " + seat + " is not free!");
		}

		int freeSeat = -1;
		for (int i = 0; i < 4; i++) {
			if (player.equals(getPlayer(PlayerLocation.get(i)))) {
				throw new Exception("Player with name '" + player.getName()
						+ "' already sits at this table!");
			}
			if (freeSeat == -1 && isFreeSeat(PlayerLocation.get(i))) {
				freeSeat = i;
			}
		}
		if (seat != null) {
			playerList[seat.getIndex()] = player;
			player.setPlayerLocation(seat);
			return;
		} else {
			if (freeSeat != -1) {
				playerList[freeSeat] = player;
				player.setPlayerLocation(PlayerLocation.get(freeSeat));
				return;
			}
		}
		throw new Exception("No seat free!");
	}

	public void addPlayer(Player player) throws Exception {
		addPlayer(player, null);
	}

	public Player getPlayer(PlayerLocation seat) {
		return playerList[seat.getIndex()];
	}

	public Player getPlayer(String playerName) throws Exception {
		if (playerName == null) {
			throw new NullPointerException("Player name can not be null!");
		}
		for (int i = 0; i < 4; i++) {
			if (playerList[i] != null
					&& playerName.equals(playerList[i].getName())) {
				return playerList[i];
			}
		}
		throw new Exception("Player '" + playerName
				+ "' does not sit at this table!");
	}

	public boolean isFreeSeat(PlayerLocation seat) {
		return (playerList[seat.getIndex()] == null);
	}

	public boolean isFreeCardPlayerLocation(PlayerLocation PlayerLocation) {
		return (cardList[PlayerLocation.getIndex()] == null);
	}

	public Card getCard(PlayerLocation PlayerLocation) {
		return cardList[PlayerLocation.getIndex()];
	}

	public void putCard(Card card, PlayerLocation PlayerLocation)
			throws Exception {
		if (!isFreeCardPlayerLocation(PlayerLocation)) {
			throw new Exception("Card PlayerLocation '" + PlayerLocation
					+ "' is not free!");
		}
		card.faceUp();
		cardList[PlayerLocation.getIndex()] = card;
	}

	public void putCard(Card card) throws Exception {
		putCard(card, PlayerLocation.get(currentPlayerIndex));
	}

	public void putCardUpdatePlayer(Card card, Player player) throws Exception {
		if (card == null || player == null) {
			throw new NullPointerException("Player and card can not be null!");
		}
		try {
			int cardIndex = player.getHand().getIndex(card);
			player.getHand().removeCard(cardIndex);
			putCard(card, getPlayerLocation(player));
		} catch (Exception e) {
			throw new Exception("Player '" + player.getName()
					+ "' can not put card '" + card.toStringDebug()
					+ "', because '" + e.getMessage() + "'.");
		}
	}

	public void putCardUpdatePlayer(Card card) throws Exception {
		putCardUpdatePlayer(card, getCurrentPlayer());
	}

	public PlayerLocation getPlayerLocation(Player player) {
		for (int i = 0; i < 4; i++) {
			if (player.equals(playerList[i])) {
				return PlayerLocation.get(i);
			}
		}
		return null;
	}

	public Player[] getPlayers() {
		return playerList;
	}

	public Player getCurrentPlayer() throws Exception {
		if (isFreeSeat(PlayerLocation.get(currentPlayerIndex))) {
			throw new Exception("No player on this seat!");
		}
		return playerList[currentPlayerIndex];
	}

	public void setCurrentPlayer(PlayerLocation PlayerLocation) {
		currentPlayerIndex = PlayerLocation.getIndex();
		System.out.println("Current player is '"
				+ playerList[currentPlayerIndex].getName() + "'.");
	}

	public Card getCurrentPlayerCard() {
		return getCard(PlayerLocation.get(currentPlayerIndex));
	}

	public Player nextPlayer() throws Exception {
		currentPlayerIndex++;
		if (currentPlayerIndex >= 4) {
			currentPlayerIndex = 0;
		}
		return getCurrentPlayer();
	}

	public void reset() {
		for (int i = 0; i < 4; i++) {
			playerList[i] = null;
			cardList[i] = null;
		}
	}

	public void resetPlayerList() {
		for (int i = 0; i < 4; i++) {
			playerList[i] = null;
		}
	}

	public void resetCardList() {
		for (int i = 0; i < 4; i++) {
			cardList[i] = null;
		}
	}

	public int getPlayerCount() {
		int count = 0;
		for (int i = 0; i < 4; i++) {
			if (playerList[i] != null) {
				count++;
			}
		}
		return count;
	}

	private String toStringPlayer(PlayerLocation PlayerLocation) {
		return playerList[PlayerLocation.getIndex()] == null ? DESCRIPTION_EMPTY_SEAT
				: playerList[PlayerLocation.getIndex()].getName();
	}

	private String toStringCard(PlayerLocation PlayerLocation) {
		return cardList[PlayerLocation.getIndex()] == null ? DESCRIPTION_EMPTY_CARDSLOT
				: cardList[PlayerLocation.getIndex()].toString();
	}

	@Override
	public String toString() {
		String output = "";
		output = String.format("%25s%-15s\n%25s%-15s\n", " ",
				toStringPlayer(PlayerLocation.get(0)), " ",
				toStringCard(PlayerLocation.get(0)));
		output += String.format("%-15s %-15s%3s%-15s%-15s\n",
				toStringPlayer(PlayerLocation.get(3)),
				toStringCard(PlayerLocation.get(3)), " ",
				toStringCard(PlayerLocation.get(1)),
				toStringPlayer(PlayerLocation.get(1)));
		output += String.format("%25s%-15s\n%25s%-15s\n", " ",
				toStringCard(PlayerLocation.get(2)), " ",
				toStringPlayer(PlayerLocation.get(2)));
		return output;
	}

	@Override
	public void load(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public String serialize() {
		String xmlPlayer = "";
		for (int i = 0; i < 4; i++) {
			if (playerList[i] != null) {
				xmlPlayer += playerList[i].serialize();
			}
		}
		return SimpleXML.createTag("table",
				SimpleXML.createTag("players", xmlPlayer));
	}

	public void removePlayer(Player player) {
		playerList[getPlayerLocation(player).getIndex()] = null;
		resetCardList();
	}

	public void clearPlayerHands() {
		// TODO Auto-generated method stub
		for (Player p : playerList) {
			if (p != null) {
				p.clearHand();

			}
		}

	}

}
