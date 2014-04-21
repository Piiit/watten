package com.mpp.testing;

import java.util.ArrayList;
import java.util.List;

import cards.Card;
import cards.Deck;
import com.mpp.testing.Table.Position;
import com.mpp.tools.Rank;
import com.mpp.tools.Suit;

/**
 * TURN
 * - select best card's rank
 * - select best card's suit
 * - every player plays a card
 * - calculate the winner of this turn
 * @author Peter Moser (pemoser)
 */
public class Watten {
	
	private String name;
	private int maxPoints = 2;
	private Deck deck;
	private Table table;
	private Suit bestCardSuit;
	private Rank bestCardRank;
	private Position cardDealerPosition = null;
	private int team1Tricks = 0;
	private int team2Tricks = 0;
	private int team1Points = 0;
	private int team2Points = 0;
	private Position turnWinnerPosition = null;
	private WattenStatus status;
	private List<WattenStatus> allowedStates = new ArrayList<WattenStatus>();
	
	public Watten(String name) {
		this.name = name;
		deck = new Deck();
		table = new Table();
		
		status = WattenStatus.INIT;
		setAllowedStates(WattenStatus.GAME_START);
	}
	
	public Player getTurnWinner() {
		return table.getPlayer(turnWinnerPosition);
	}
	
	public int getTurnTricksTeam1() {
		return team1Tricks;
	}
	
	public int getTurnTricksTeam2() {
		return team2Tricks;
	}
	
	public int getPointsTeam1() {
		return team1Points;
	}

	public int getPointsTeam2() {
		return team2Points;
	}
	
	public void stateGameEntry() throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.GAME_START);
		if(table.getPlayerCount() != 4) {
			throw new Exception("We need 4 players to start the game!");
		}
		team1Points = 0;
		team2Points = 0;
		turnWinnerPosition = Position.NORTH;
		
		setStatus(WattenStatus.GAME_START);
		setAllowedStates(WattenStatus.ROUND_START);
	}
	
	private void setAllowedStates(WattenStatus ... states) {
		allowedStates.clear();
		for(WattenStatus state : states) {
			allowedStates.add(state);
		}
	}
	
	private void throwExceptionIfNotAllowed(WattenStatus statusToCheck) throws Exception {
		if(!allowedStates.contains(statusToCheck)) {
			throw new Exception("Can not change to status " + statusToCheck + " from status " + status + ": allowed states = " + getAllowedStates());
		}
	}
	
	/**
	 * A new round means:
	 * - remove cards from table;
	 * - shuffle cards;
	 * - next player is considered the new dealer;
	 * - deal cards
	 * - set tricks to zero
	 * @throws Exception 
	 */
	public void stateRoundEntry() throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.ROUND_START);
		deck.reset();
		deck.shuffle();
		team1Tricks = 0;
		team2Tricks = 0;
		if(cardDealerPosition == null || cardDealerPosition.getIndex() + 1 > 3) {
			cardDealerPosition = Position.NORTH;
		} else {
			cardDealerPosition = Position.get(cardDealerPosition.getIndex() + 1);
		}

		//Deal cards...
		for(Player p : table.getPlayers()) {
			p.getHand().clear();
			for(int i = 1; i <= 5; i++) {
				p.addCard(deck.getNextCard());
			}
		}

		setStatus(WattenStatus.ROUND_START);
		setAllowedStates(WattenStatus.TURN_START);
	}
	
	/**
	 * A new turn means:
	 * - reset old best card values;
	 * @throws Exception 
	 */
	public void stateTurnEntry() throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.TURN_START);
		bestCardRank = null;
		bestCardSuit = null;
		table.setCurrentPlayer(turnWinnerPosition);
		table.resetCardList();
		setStatus(WattenStatus.TURN_START);
		setAllowedStates(WattenStatus.SELECT_RANK);
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	@Override
	public String toString() {
		return String.format(
				"Watten: name=%s; max. points=%d; status=%s; best=%s; tricks1=%d; tricks2=%d; winner=%s; points1=%d; points2=%d\n%s", 
				getName(), 
				maxPoints,
				getStatus(),
				"" + bestCardSuit + ":" + bestCardRank, 
				getTurnTricksTeam1(),
				getTurnTricksTeam2(),
				getTurnWinner() == null ? "n/a" : getTurnWinner().getName(),
				getPointsTeam1(),
				getPointsTeam2(),
				table
				);
	}
	
	public String getName() {
		return name; 
	}
	
	public WattenStatus getStatus() {
		return status;
	}
	
	private void setStatus(WattenStatus status) {
		System.out.println("Status switch from " + this.status + " to " + status);
		this.status = status;
	}
	
	public Table getTable() {
		return table;
	}
	
	public Deck getDeck() {
		return deck;
	}
	
	public void setDeck(Deck deck) {
		this.deck = deck;
	}
 	
	public void stateTurnPlayCard(Card card) throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.PLAY_CARD);
		
		if(table.getCurrentPlayerCard() != null) {
			throw new Exception("Player " + table.getCurrentPlayer().getName() + " can not put another card!");
		}

		table.putCardUpdatePlayer(card);
		table.nextPlayer();
		
		if(table.getCurrentPlayerCard() != null) {
			setAllowedStates(WattenStatus.TURN_FINISHED);
			stateTurnExit();
		} else {
			setStatus(WattenStatus.PLAY_CARD);
			setAllowedStates(WattenStatus.PLAY_CARD/*, WattenStatus.BET*/);
		}
	}
	
	public List<WattenStatus> getAllowedStates() {
		return allowedStates;
	}
	
	private void stateRoundExit() throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.ROUND_FINISHED);
		
		if(getTurnTricksTeam1() >= 3) {
			team1Points += getCurrentBet();
		} else {
			team2Points += getCurrentBet();
		}
		
		setStatus(WattenStatus.ROUND_FINISHED);
		
		if(team1Points >= getMaxPoints() || team2Points >= getMaxPoints()) {
			setAllowedStates(WattenStatus.GAME_FINISHED);
			stateGameExit();
		} else {
			setAllowedStates(WattenStatus.ROUND_START);
		}
	}
	
	private void stateGameExit() throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.GAME_FINISHED);
		setStatus(WattenStatus.GAME_FINISHED);
		setAllowedStates(WattenStatus.GAME_START);
	}

	private int getCurrentBet() {
		return 2;
	}

	public void stateSelectBestCardSuit(Suit suit) throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.SELECT_SUIT);
		bestCardSuit = suit;
		setStatus(WattenStatus.SELECT_SUIT);
		setAllowedStates(WattenStatus.PLAY_CARD);
	}
	
	public void stateSelectBestCardRank(Rank rank) throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.SELECT_RANK);
		bestCardRank = rank;
		setStatus(WattenStatus.SELECT_RANK);
		setAllowedStates(WattenStatus.SELECT_SUIT);
	}
	
	public boolean isGuater(Card card) {
		if(card.getRank() != Rank.WELI && card.getSuit() == bestCardSuit) {
			int index = bestCardRank.getIndex() + 1;
			if(index > 8) {
				index = 1;
			}
			if(card.getRank() == Rank.get(index)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isRechter(Card card) {
		if(card.getRank() != Rank.WELI && card.getSuit() == bestCardSuit && card.getRank() == bestCardRank) {
			return true;
		}
		return false;
	}
	
	public boolean isBlinder(Card card) {
		if(card.getRank() != Rank.WELI && card.getSuit() != bestCardSuit && card.getRank() == bestCardRank) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the better card, where card1 is considered to be thrown first...
	 * @param card1
	 * @param card2
	 * @return
	 */
	private Card getBetterCardChronological(Card card1, Card card2) {
		if(isGuater(card1)) {
			return card1;
		}
		if(isGuater(card2)) {
			return card2;
		}
		if(isRechter(card1)) {
			return card1;
		}
		if(isRechter(card2)) {
			return card2;
		}
		if(isBlinder(card1)) {
			return card1;
		}
		if(isBlinder(card2)) {
			return card2;
		}
		if(card1.getSuit() == bestCardSuit && card2.getSuit() == bestCardSuit) {
			if(card1.getRank().getIndex() > card2.getRank().getIndex()) {
				return card1;
			}
			return card2;
		}
		if(card1.getSuit() == bestCardSuit) {
			return card1;
		}
		if(card2.getSuit() == bestCardSuit) {
			return card2;
		}
		if(card1.getSuit() == card2.getSuit()) {
			if(card1.getRank().getIndex() > card2.getRank().getIndex()) {
				return card1;
			}
			return card2;
		}
		return card1;
	}
	
	private void stateTurnExit() throws Exception {
		throwExceptionIfNotAllowed(WattenStatus.TURN_FINISHED);
		
		table.setCurrentPlayer(cardDealerPosition);
		
		Card card1 = table.getCurrentPlayerCard();
		table.nextPlayer();
		Card card2 = table.getCurrentPlayerCard();
		table.nextPlayer();
		Card card3 = table.getCurrentPlayerCard();
		table.nextPlayer();
		Card card4 = table.getCurrentPlayerCard();
		table.nextPlayer();
		
		Card bestCard = card1;
		int bestPlayerIndex = 0;
		if(getBetterCardChronological(card1, card2) == card2) {
			bestCard = card2;
			bestPlayerIndex = 1;
		}
		if(getBetterCardChronological(bestCard, card3) == card3) {
			bestCard = card3;
			bestPlayerIndex = 2;
		}
		if(getBetterCardChronological(bestCard, card4) == card4) {
			bestCard = card4;
			bestPlayerIndex = 3;
		}
		
		bestPlayerIndex = (cardDealerPosition.getIndex() + bestPlayerIndex) % 4;
		if(Position.get(bestPlayerIndex) == Position.NORTH || Position.get(bestPlayerIndex) == Position.SOUTH) {
			team1Tricks++;
		} else {
			team2Tricks++;
		}
		turnWinnerPosition = Position.get(bestPlayerIndex);
		
		setStatus(WattenStatus.TURN_FINISHED);
		
		if(getTurnTricksTeam1() >= 3 || getTurnTricksTeam2() >= 3) {
			setAllowedStates(WattenStatus.ROUND_FINISHED);
			stateRoundExit();
		} else {
			setAllowedStates(WattenStatus.TURN_START);
		}

	}

	public Player[] getWinners() {
		if(status != WattenStatus.GAME_FINISHED) {
			return null;	
		}
		
		if(getPointsTeam1() >= getMaxPoints()) {
			return new Player[]{table.getPlayer(Position.NORTH), table.getPlayer(Position.SOUTH)};
		}
		return new Player[]{table.getPlayer(Position.WEST), table.getPlayer(Position.EAST)};
	}

	public String getWinnersAsString() {
		Player list[] = getWinners();
		return list[0].getName() + " and " + list[1].getName();
	}
	
}
