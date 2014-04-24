package logic;


import xml.Loadable;
import xml.Node;
import xml.SimpleXML;
import cards.Card;

public class Table implements Loadable {

	private static final String DESCRIPTION_EMPTY_SEAT = "(empty)";
	private static final String DESCRIPTION_EMPTY_CARDSLOT = "(no card)";
	
	private Player playerList[] = new Player[4];
	private Card cardList[] = new Card[4];
	private int currentPlayerIndex = 0;

	public enum Position {
		NORTH(0), EAST(1), SOUTH(2), WEST(3);
		
		private final int index;

		Position(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}
		
		public static Position get(int index) {
			return Position.values()[index];
		}
	}
	
	public void addPlayer(Player player, Position seat) throws Exception {
		if(player == null) {
			throw new NullPointerException("Player can not be null!");
		}
		if(seat != null && !isFreeSeat(seat)) {
			throw new Exception("Seat " + seat + " is not free!");
		}
		
		int freeSeat = -1;
		for(int i = 0; i < 4; i++) {
			if(player.equals(getPlayer(Position.get(i)))) {
				throw new Exception("Player with name " + player.getName() + " already sits at this table!");
			}
			if(freeSeat == -1 && isFreeSeat(Position.get(i))) {
				freeSeat = i;
			}
		}
		if(seat != null) {
			playerList[seat.getIndex()] = player;
			return;
		} else {
			if(freeSeat != -1) {
				playerList[freeSeat] = player;
				return;
			}
		}
		throw new Exception("No seat free!");
	}
	
	public void addPlayer(Player player) throws Exception {
		addPlayer(player, null);
	}
	
	public Player getPlayer(Position seat) {
		return playerList[seat.getIndex()];
	}
	
	public Player getPlayer(String playerName) throws Exception {
		if(playerName == null) {
			throw new NullPointerException("Player name can not be null!");
		}
		for(int i = 0; i < 4; i++) {
			if(playerList[i] != null && playerName.equals(playerList[i].getName())) {
				return playerList[i];
			}
		}
		throw new Exception("Player " + playerName + " does not sit at this table!");
	}
	
	public boolean isFreeSeat(Position seat) {
		return (playerList[seat.getIndex()] == null);
	}
	
	public boolean isFreeCardPosition(Position position) {
		return (cardList[position.getIndex()] == null);
	}
	
	public Card getCard(Position position) {
		return cardList[position.getIndex()];
	}
	
	public void putCard(Card card, Position position) throws Exception {
		if(!isFreeCardPosition(position)) {
			throw new Exception("Card position " + position + " is not free!");
		}
		card.faceUp();
		cardList[position.getIndex()] = card;
	}
	
	public void putCard(Card card) throws Exception {
		putCard(card, Position.get(currentPlayerIndex));
	}
	
	public void putCardUpdatePlayer(Card card, Player player) throws Exception {
		if(card == null || player == null) {
			throw new NullPointerException("Player and card can not be null!");
		}
		try {
			int cardIndex = player.getHand().getIndex(card);
			player.getHand().removeCard(cardIndex);
			putCard(card, getPlayerPosition(player));
		} catch (Exception e) {
			throw new Exception("Player " + player.getName() + " can not put card " + card.toStringDebug() + ", because " + e.getMessage());
		}
	}

	public void putCardUpdatePlayer(Card card) throws Exception {
		putCardUpdatePlayer(card, getCurrentPlayer());
	}

	public Position getPlayerPosition(Player player) {
		for(int i = 0; i < 4; i++) {
			if(player.equals(playerList[i])) {
				return Position.get(i);
			}
		}
		return null;
	}
	
	public Player[] getPlayers() {
		return playerList;
	}
	
	public Player getCurrentPlayer() throws Exception {
		if(isFreeSeat(Position.get(currentPlayerIndex))) {
			throw new Exception("No player on this seat!");
		}
		return playerList[currentPlayerIndex];
	}
	
	public void setCurrentPlayer(Position position) {
		currentPlayerIndex = position.getIndex();
		System.out.println("Current player is " + playerList[currentPlayerIndex].getName());
	}
	
	public Card getCurrentPlayerCard() {
		return getCard(Position.get(currentPlayerIndex));
	}
	
	public Player nextPlayer() throws Exception {
		currentPlayerIndex++;
		if(currentPlayerIndex >= 4) {
			currentPlayerIndex = 0;
		}
		return getCurrentPlayer();
	}
	
	public void reset() {
		for(int i = 0; i < 4; i++) {
			playerList[i] = null;
			cardList[i] = null;
		}
	}

	public void resetPlayerList() {
		for(int i = 0; i < 4; i++) {
			playerList[i] = null;
		}
	}

	public void resetCardList() {
		for(int i = 0; i < 4; i++) {
			cardList[i] = null;
		}
	}
	
	public int getPlayerCount() {
		int count = 0;
		for(int i = 0; i < 4; i++) {
			if(playerList[i] != null) {
				count++;
			}
		}
		return count;
	}
	
	private String toStringPlayer(Position position) {
		return playerList[position.getIndex()] == null ? DESCRIPTION_EMPTY_SEAT : playerList[position.getIndex()].getName();
	}
	
	private String toStringCard(Position position) {
		return cardList[position.getIndex()] == null ? DESCRIPTION_EMPTY_CARDSLOT : cardList[position.getIndex()].toString();
	}

	@Override
	public String toString() {
		String output = "";
		output = String.format("%25s%-15s\n%25s%-15s\n", " ", toStringPlayer(Position.get(0)), " ", toStringCard(Position.get(0)));
		output += String.format("%-15s %-15s%3s%-15s%-15s\n", toStringPlayer(Position.get(3)), toStringCard(Position.get(3)), " ", toStringCard(Position.get(1)), toStringPlayer(Position.get(1)));
		output += String.format("%25s%-15s\n%25s%-15s\n", " ", toStringCard(Position.get(2)), " ", toStringPlayer(Position.get(2)));
		return output;
	}

	@Override
	public void load(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String serialize() {
		String xmlPlayer = "";
		for(int i = 0; i < 4; i++) {
			if(playerList[i] != null) {
				xmlPlayer += playerList[i].serialize();
			}
		}
		return SimpleXML.createTag("table", SimpleXML.createTag("players", xmlPlayer));
	}
}
