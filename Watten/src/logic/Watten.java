package logic;

import java.util.ArrayList;
import java.util.List;
import logic.Table.Position;
import cards.Card;
import cards.Deck;
import cards.MultipleCards;
import cards.Rank;
import cards.Suit;
import cards.WattenCardTools;


/**
 * TURN
 * - select best card's rank
 * - select best card's suit
 * - every player plays a card
 * - calculate the winner of this turn
 * @author Peter Moser (pemoser)
 */
public class Watten {
	
	public final static int NOTEAM = 0;
	public final static int TEAM1 = 1;
	public final static int TEAM2 = 2;
	public final static boolean SURRENDER = false;
	public final static boolean HOLD = true;
	
	private String name;
	private int maxPoints = 2;
	private Deck deck;
	private Table table;
	private Position cardDealerPosition = null;
	private int team1Tricks = 0;
	private int team2Tricks = 0;
	private int team1Points = 0;
	private int team2Points = 0;
	private Card bestCard = null;
	private int lastBetTeamNumber = NOTEAM; 
	private Position turnWinnerPosition = null;
	private WattenFeature status;
	private List<WattenFeature> allowedStates = new ArrayList<WattenFeature>();
	private Suit firstCardsSuit = null;
	private int lastBet;
	
	public Watten(String name) throws Exception {
		this.name = name;
		deck = new Deck();
		table = new Table();
		
		status = WattenFeature.INIT;
		setConstraints(WattenFeature.GAME_START);
	}
	
	public Player getTurnWinner() {
		if(turnWinnerPosition == null) {
			return null;
		}
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
	
	public int getTeam(Player player) {
		return getTeam(table.getPlayerPosition(player));
	}
	
	public int getTeam(Position position) {
		if(position == Position.NORTH || position == Position.SOUTH) {
			return TEAM1;
		} 
		return TEAM2; 
	}
	
	public int getTeam() throws Exception {
		return getTeam(table.getPlayerPosition(table.getCurrentPlayer()));
	}
	
	public void start() throws Exception {
		stateGameEntry();
	}
	
	private void stateGameEntry() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.GAME_START);
		if(table.getPlayerCount() != 4) {
			throw new Exception("We need 4 players to start the game!");
		}
		team1Points = 0;
		team2Points = 0;
		turnWinnerPosition = Position.NORTH;
		
		setStatus(WattenFeature.GAME_START);
		setConstraints(WattenFeature.ROUND_START);
		stateRoundEntry();
	}
	
	private void setConstraints(WattenFeature ... states) {
		allowedStates.clear();
		for(WattenFeature state : states) {
			allowedStates.add(state);
		}
	}
	
	private void throwExceptionIfNotAllowed(WattenFeature statusToCheck) throws Exception {
		if(!allowedStates.contains(statusToCheck)) {
			throw new Exception("Can not change to status " + statusToCheck + " from status " + status + ": allowed states = " + getConstraints());
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
	private void stateRoundEntry() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.ROUND_START);
		deck.reset();
		deck.shuffle();
		team1Tricks = 0;
		team2Tricks = 0;
		lastBet = 2;
		lastBetTeamNumber = NOTEAM;
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

		setStatus(WattenFeature.ROUND_START);
		setConstraints(WattenFeature.TURN_START);
		stateTurnEntry();
	}
	
	/**
	 * A new turn means:
	 * - reset old best card values;
	 * @throws Exception 
	 */
	private void stateTurnEntry() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.TURN_START);
		if(bestCard == null) {
			bestCard = new Card();
		} else {
			bestCard.setSuit(null);
			bestCard.setRank(null);
		}
		firstCardsSuit = null;
		table.setCurrentPlayer(turnWinnerPosition);
		table.resetCardList();
		setStatus(WattenFeature.TURN_START);
		setConstraints(WattenFeature.SELECT_RANK);
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	@Override
	public String toString() {
		return String.format("Watten: %s\n%s", toStringInfo(),	table);
	}
	
	public String getName() {
		return name; 
	}
	
	public WattenFeature getStatus() {
		return status;
	}
	
	private void setStatus(WattenFeature status) {
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
		throwExceptionIfNotAllowed(WattenFeature.PLAY_CARD);
		
		if(table.getCurrentPlayerCard() != null) {
			throw new Exception("Player " + table.getCurrentPlayer().getName() + " can not put another card!");
		}

		//If first player throws a card with the same suit as the best card's suit, other players must throw a best suit card as well
		//if existent, except "Guater" or "Rechter"
		if(firstCardsSuit == bestCard.getSuit() && card.getSuit() != bestCard.getSuit()) {
			boolean hasSuitCard = false;
			MultipleCards hand = table.getCurrentPlayer().getHand();
			for(int cardIndex = hand.getIndex(); cardIndex < hand.getCount(); cardIndex++) {
				Card c = hand.getCard(cardIndex);
				if(c.getSuit() == bestCard.getSuit() && !WattenCardTools.isGuater(c, bestCard) && !WattenCardTools.isRechter(c, bestCard)) {
					hasSuitCard = true;
				}
			}
			if(hasSuitCard) {
				setStatus(WattenFeature.PLAY_CARD);
				setConstraints(WattenFeature.PLAY_CARD);
				throw new Exception("You must throw a best suit card!");
			}
		}
		table.putCardUpdatePlayer(card);
		table.nextPlayer();
		if(firstCardsSuit == null) {
			firstCardsSuit = card.getSuit();
		}
		
		if(table.getCurrentPlayerCard() != null) {
			setConstraints(WattenFeature.TURN_FINISHED);
			stateTurnExit();
		} else {
			setStatus(WattenFeature.PLAY_CARD);
			setConstraints(WattenFeature.PLAY_CARD, WattenFeature.BET);
		}
	}
	
	public MultipleCards getAllowedCards() throws Exception {
		MultipleCards hand = table.getCurrentPlayer().getHand();
		if(firstCardsSuit != bestCard.getSuit()) {
			return hand;
		}
		
		MultipleCards allowedCards = new MultipleCards();
		for(int cardIndex = hand.getIndex(); cardIndex < hand.getCount(); cardIndex++) {
			Card c = hand.getCard(cardIndex);
			if(c.getSuit() == bestCard.getSuit()) {
				allowedCards.addCard(c);
			}
		}
		if(allowedCards.getCount() == 0) {
			return hand;
		}
		return allowedCards;
	}
	
	public void stateTurnBet(int team) throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.BET);
		if(team != TEAM1 && team != TEAM2) {
			throw new Exception("Team number must be 1 (north/south) or 2 (east/west)!");
		}
		if(team == lastBetTeamNumber) {
			throw new Exception("Team " + team + " can not bet twice!");
		}
		lastBetTeamNumber = team;
		setConstraints(WattenFeature.SURRENDER_OR_HOLD);
	}
	
	public List<WattenFeature> getConstraints() {
		return allowedStates;
	}
	
	private void stateRoundExit() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.ROUND_FINISHED);
		
		if(lastBetTeamNumber == NOTEAM) {
			if(getTurnTricksTeam1() >= 3) {
				team1Points += getCurrentBet();
			} else {
				team2Points += getCurrentBet();
			}
		} else {
			if(lastBetTeamNumber == TEAM1) {
				team1Points += getCurrentBet();
			} else {
				team2Points += getCurrentBet();
			}
		}
		
		setStatus(WattenFeature.ROUND_FINISHED);
		
		if(team1Points >= getMaxPoints() || team2Points >= getMaxPoints()) {
			setConstraints(WattenFeature.GAME_FINISHED);
			stateGameExit();
		} else {
			setConstraints(WattenFeature.ROUND_START);
			stateRoundEntry();
		}
	}
	
	private void stateGameExit() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.GAME_FINISHED);
		setStatus(WattenFeature.GAME_FINISHED);
		setConstraints(WattenFeature.GAME_START);
	}

	private int getCurrentBet() {
		return lastBet;
	}

	public void stateSelectBestCardSuit(Suit suit) throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.SELECT_SUIT);
		bestCard.setSuit(suit);
		setStatus(WattenFeature.SELECT_SUIT);
		setConstraints(WattenFeature.PLAY_CARD, WattenFeature.BET);
	}
	
	public void stateSelectBestCardRank(Rank rank) throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.SELECT_RANK);
		bestCard.setRank(rank);
		setStatus(WattenFeature.SELECT_RANK);
		setConstraints(WattenFeature.SELECT_SUIT);
	}
	
	private void stateTurnExit() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.TURN_FINISHED);
		
		table.setCurrentPlayer(cardDealerPosition);
		
		Card card1 = table.getCurrentPlayerCard();
		table.nextPlayer();
		Card card2 = table.getCurrentPlayerCard();
		table.nextPlayer();
		Card card3 = table.getCurrentPlayerCard();
		table.nextPlayer();
		Card card4 = table.getCurrentPlayerCard();
		table.nextPlayer();
		
		Card betterCard = card1;
		int bestPlayerIndex = 0;
		if(WattenCardTools.isBetterCardChronological(card1, card2, bestCard) == false) {
			betterCard = card2;
			bestPlayerIndex = 1;
		}
		if(WattenCardTools.isBetterCardChronological(betterCard, card3, bestCard) == false) {
			betterCard = card3;
			bestPlayerIndex = 2;
		}
		if(WattenCardTools.isBetterCardChronological(betterCard, card4, bestCard) == false) {
			betterCard = card4;
			bestPlayerIndex = 3;
		}
		
		bestPlayerIndex = (cardDealerPosition.getIndex() + bestPlayerIndex) % 4;
		if(Position.get(bestPlayerIndex) == Position.NORTH || Position.get(bestPlayerIndex) == Position.SOUTH) {
			team1Tricks++;
		} else {
			team2Tricks++;
		}
		turnWinnerPosition = Position.get(bestPlayerIndex);
		
		setStatus(WattenFeature.TURN_FINISHED);
		
		if(getTurnTricksTeam1() >= 3 || getTurnTricksTeam2() >= 3) {
			setConstraints(WattenFeature.ROUND_FINISHED);
			stateRoundExit();
		} else {
			setConstraints(WattenFeature.TURN_START);
			stateTurnEntry();
		}

	}

	public Player[] getWinners() {
		if(status != WattenFeature.GAME_FINISHED) {
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

	public void stateTurnSurrenderOrHold(boolean hold) throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.SURRENDER_OR_HOLD);
		if(hold) {
			lastBet++;
			setConstraints(WattenFeature.PLAY_CARD);
		} else {
			setConstraints(WattenFeature.ROUND_FINISHED);
			stateRoundExit();
		}
	}

	public String toStringInfo() {
		return String.format(
				"name=%s; max. points=%d; status=%s; best=%s; tricks1=%d; tricks2=%d; winner=%s; points1=%d; points2=%d; bet=%d; betTeam=%d", 
				getName(), 
				maxPoints,
				getStatus(),
				bestCard.toStringFaceUp(), 
				getTurnTricksTeam1(),
				getTurnTricksTeam2(),
				getTurnWinner() == null ? "n/a" : getTurnWinner().getName(),
				getPointsTeam1(),
				getPointsTeam2(),
				lastBet,
				lastBetTeamNumber
				);
	}
	
}