package com.mpp.watten.logic;

import java.util.ArrayList;
import java.util.List;

import com.mpp.tools.PlayerLocation;
import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.Deck;
import com.mpp.watten.cards.MultipleCards;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;
import com.mpp.watten.cards.WattenCardTools;

/**
 * TURN - select best card's rank - select best card's suit - every player plays
 * a card - calculate the winner of this turn
 * 
 * @author Peter Moser (pemoser)
 */
/**
 * @author Peter Moser (pemoser)
 * 
 */
public class Watten {

	public final static int NOTEAM = 0;
	public final static int TEAM1 = 1;
	public final static int TEAM2 = 2;
	public final static boolean SURRENDER = false;
	public final static boolean HOLD = true;

	private String name;
	private int maxPoints = 6;
	private Deck deck;
	private Table table;
	private PlayerLocation cardDealerPlayerLocation = null;
	private int team1Tricks = 0;
	private int team2Tricks = 0;
	private int team1Points = 0;
	private int team2Points = 0;
	private Card bestCard = null;
	private Card suitCard = null;
	private Card rankCard = null;
	private int lastBetTeamNumber = NOTEAM;
	private PlayerLocation turnWinnerPlayerLocation = null;
	private int roundWinningTeam;
	private WattenFeature status;
	private List<WattenFeature> allowedStates = new ArrayList<WattenFeature>();
	private Card firstThrownCard = null;
	private int lastBet;

	public Watten(String name) throws Exception {
		this.name = name;
		deck = new Deck();
		table = new Table();
		// Needed otherwise nullpointer when joining unstarted game
		team1Points = 0;
		team2Points = 0;
		status = WattenFeature.INIT;
		setConstraints(WattenFeature.GAME_READY);
	}

	public Player getTurnWinner() {
		if (turnWinnerPlayerLocation == null) {
			return null;
		}
		return table.getPlayer(turnWinnerPlayerLocation);
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
		return getTeam(table.getPlayerLocation(player));
	}

	public int getTeam(PlayerLocation playerLocation) {
		if (playerLocation == PlayerLocation.North
				|| playerLocation == PlayerLocation.South) {
			return TEAM1;
		}
		return TEAM2;
	}

	public int getTeam() throws Exception {
		return getTeam(table.getPlayerLocation(table.getCurrentPlayer()));
	}

	public void start() throws Exception {
		stateGameEntry();
	}

	private void stateGameEntry() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.GAME_START);
		if (table.getPlayerCount() != 4) {
			throw new Exception("We need 4 players to start the game!");
		}
		team1Points = 0;
		team2Points = 0;

		setStatus(WattenFeature.GAME_START);
		setConstraints(WattenFeature.ROUND_START);
		stateRoundEntry();
	}

	private void setConstraints(WattenFeature... states) {
		allowedStates.clear();
		for (WattenFeature state : states) {
			allowedStates.add(state);
		}
	}

	private void throwExceptionIfNotAllowed(WattenFeature statusToCheck)
			throws Exception {
		if (!allowedStates.contains(statusToCheck)) {
			throw new Exception("Can not change to status " + statusToCheck
					+ " from status " + status + ": allowed states = "
					+ getConstraints());
		}
	}

	/**
	 * <pre>
	 * A new round means: 
	 * - remove cards from table; 
	 * - shuffle cards; 
	 * - next player is considered the new dealer; 
	 * - deal cards 
	 * - set tricks to zero
	 * </pre>
	 * 
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
		if (cardDealerPlayerLocation == null
				|| cardDealerPlayerLocation.getIndex() + 1 > 3) {
			cardDealerPlayerLocation = PlayerLocation.South;
		} else {
			cardDealerPlayerLocation = PlayerLocation
					.get(cardDealerPlayerLocation.getIndex() + 1);
		}
		turnWinnerPlayerLocation = getSelectRankPlayer().getPlayerLocation();

		// Deal cards...
		for (Player p : table.getPlayers()) {
			p.getHand().clear();
			for (int i = 1; i <= 5; i++) {
				p.addCard(deck.getNextCard());
			}
		}

		setStatus(WattenFeature.ROUND_START);
		setConstraints(WattenFeature.TURN_START);
		// stateTurnEntry();
	}

	public void startTurn() throws Exception {
		stateTurnEntry();
	}

	/**
	 * <pre>
	 * A new turn means: 
	 * - reset old best card values;
	 * </pre>
	 * 
	 * @throws Exception
	 */
	private void stateTurnEntry() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.TURN_START);

		firstThrownCard = null;
		table.setCurrentPlayer(turnWinnerPlayerLocation);
		table.resetCardList();

		setStatus(WattenFeature.TURN_START);

	}

	public boolean evaluateRoundFirstTurnStart() throws Exception {
		if (getTurnTricksTeam1() > 0 || getTurnTricksTeam2() > 0) {

			setConstraints(WattenFeature.PLAY_CARD, WattenFeature.BET);
			return false;
		} else {
			if (bestCard == null) {
				bestCard = new Card();
			} else {
				bestCard.setSuit(null);
				bestCard.setRank(null);
			}
			suitCard = null;
			rankCard = null;
			setConstraints(WattenFeature.SELECT_RANK);
			return true;
		}
	}

	public int getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	@Override
	public String toString() {
		return String.format("Watten: %s\n%s", toStringInfo(), table);
	}

	public String getName() {
		return name;
	}

	public WattenFeature getStatus() {
		return status;
	}

	private void setStatus(WattenFeature status) {
		System.out.println("Status switch from " + this.status + " to "
				+ status);
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

		if (table.getCurrentPlayerCard() != null) {
			throw new Exception("Player " + table.getCurrentPlayer().getName()
					+ " can not put another card!");
		}

		// If first player throws a card with the same suit as the best card's
		// suit, other players must throw a best suit card as well
		// if existent, except "Guater" or "Rechter"
		if (firstThrownCard != null
				&& firstThrownCard.getSuit() == bestCard.getSuit()
				&& card.getSuit() != bestCard.getSuit()) {
			boolean hasSuitCard = false;
			MultipleCards hand = table.getCurrentPlayer().getHand();
			for (int cardIndex = hand.getIndex(); cardIndex < hand.getCount(); cardIndex++) {
				Card c = hand.getCard(cardIndex);
				if (c.getSuit() == bestCard.getSuit()
						&& !WattenCardTools.isGuater(c, bestCard)
						&& !WattenCardTools.isRechter(c, bestCard)) {
					hasSuitCard = true;
				}
			}
			if (hasSuitCard) {
				setStatus(WattenFeature.PLAY_CARD);
				setConstraints(WattenFeature.PLAY_CARD);
				throw new Exception("You must throw a best suit card!");
			}
		}
		table.putCardUpdatePlayer(card);
		table.nextPlayer();
		if (firstThrownCard == null) {
			firstThrownCard = card;
		}

		if (table.getCurrentPlayerCard() != null) {
			setConstraints(WattenFeature.TURN_FINISHED);
			stateTurnExit();
		} else {
			setStatus(WattenFeature.PLAY_CARD);
			setConstraints(WattenFeature.PLAY_CARD, WattenFeature.BET);
		}
	}

	public MultipleCards getAllowedCards() throws Exception {
		return WattenCardTools.getAllowedCards(table.getCurrentPlayer()
				.getHand(), firstThrownCard, bestCard);
	}

	public void stateTurnBet(int team) throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.BET);
		if (team != TEAM1 && team != TEAM2) {
			throw new Exception(
					"Team number must be 1 (north/south) or 2 (east/west)!");
		}
		if (team == lastBetTeamNumber) {
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
		roundWinningTeam = 0;
		if (lastBetTeamNumber == NOTEAM) {
			if (getTurnTricksTeam1() >= 3) {
				team1Points += getCurrentBet();
				roundWinningTeam = 1;
			} else {
				team2Points += getCurrentBet();
				roundWinningTeam = 2;
			}
		} else {
			if (lastBetTeamNumber == TEAM1) {
				team1Points += getCurrentBet();
				roundWinningTeam = 1;
			} else {
				team2Points += getCurrentBet();
				roundWinningTeam = 2;

			}
		}
		table.clearPlayerHands();
		setStatus(WattenFeature.ROUND_FINISHED);

	}

	public int getRoundWinningTeam() {
		return roundWinningTeam;
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
		table.setCurrentPlayer(getSelectRankPlayer().getPlayerLocation());

	}

	public void stateSelectBestCardRank(Rank rank) throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.SELECT_RANK);
		bestCard.setRank(rank);
		setStatus(WattenFeature.SELECT_RANK);
		setConstraints(WattenFeature.SELECT_SUIT);
		table.setCurrentPlayer(getSelectSuitPlayer().getPlayerLocation());
	}

	private void stateTurnExit() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.TURN_FINISHED);

		table.setCurrentPlayer(cardDealerPlayerLocation);

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
		if (WattenCardTools.isBetterCardChronological(card1, card2, bestCard) == false) {
			betterCard = card2;
			bestPlayerIndex = 1;
		}
		if (WattenCardTools.isBetterCardChronological(betterCard, card3,
				bestCard) == false) {
			betterCard = card3;
			bestPlayerIndex = 2;
		}
		if (WattenCardTools.isBetterCardChronological(betterCard, card4,
				bestCard) == false) {
			betterCard = card4;
			bestPlayerIndex = 3;
		}

		bestPlayerIndex = (cardDealerPlayerLocation.getIndex() + bestPlayerIndex) % 4;
		if (PlayerLocation.get(bestPlayerIndex) == PlayerLocation.North
				|| PlayerLocation.get(bestPlayerIndex) == PlayerLocation.South) {
			team1Tricks++;
		} else {
			team2Tricks++;
		}
		turnWinnerPlayerLocation = PlayerLocation.get(bestPlayerIndex);

		setStatus(WattenFeature.TURN_FINISHED);

	}

	public void evaluateTricks() throws Exception {
		if (getTurnTricksTeam1() >= 3 || getTurnTricksTeam2() >= 3) {
			setConstraints(WattenFeature.ROUND_FINISHED);
			stateRoundExit();
		} else {
			setConstraints(WattenFeature.TURN_START);
			stateTurnEntry();
		}
	}

	public void evaluatePoints() throws Exception {
		if (team1Points >= getMaxPoints() || team2Points >= getMaxPoints()) {
			setConstraints(WattenFeature.GAME_FINISHED);
			stateGameExit();
		} else {
			setConstraints(WattenFeature.ROUND_START);
			stateRoundEntry();
		}
	}

	public Player[] getWinners() {
		if (status != WattenFeature.GAME_FINISHED) {
			return null;
		}

		if (getPointsTeam1() >= getMaxPoints()) {
			return new Player[] { table.getPlayer(PlayerLocation.North),
					table.getPlayer(PlayerLocation.South) };
		}
		return new Player[] { table.getPlayer(PlayerLocation.West),
				table.getPlayer(PlayerLocation.East) };
	}

	public String getWinnersAsString() {
		Player list[] = getWinners();
		return list[0].getName() + " and " + list[1].getName();
	}

	public void stateTurnSurrenderOrHold(boolean hold) throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.SURRENDER_OR_HOLD);
		if (hold) {
			lastBet++;
			setConstraints(WattenFeature.PLAY_CARD);
		} else {
			setConstraints(WattenFeature.ROUND_FINISHED);
			stateRoundExit();
		}
	}

	public String toStringInfo() {
		return String
				.format("name=%s; max. points=%d; status=%s; best=%s; tricks1=%d; tricks2=%d; winner=%s; points1=%d; points2=%d; bet=%d; betTeam=%d",
						getName(), maxPoints, getStatus(),
						bestCard == null ? "n/a" : bestCard.toStringFaceUp(),
						getTurnTricksTeam1(), getTurnTricksTeam2(),
						getTurnWinner() == null ? "n/a" : getTurnWinner()
								.getName(), getPointsTeam1(), getPointsTeam2(),
						lastBet, lastBetTeamNumber);
	}

	public void kickPlayer(Player player) throws Exception {
		getTable().removePlayer(player);
		System.out.println("A player left the table: " + player.getName());
		if (getStatus() != WattenFeature.INIT
				&& getStatus() != WattenFeature.PAUSE) {
			setConstraints(WattenFeature.PAUSE);
			statePause();
		}
	}

	public void addPlayer(Player player) throws Exception {
		getTable().addPlayer(player);
		System.out.println("New player joined the table: " + player.getName());
		if (getStatus() == WattenFeature.PAUSE
				&& getTable().getPlayerCount() == 4) {
			stateResume();
		} else if (getTable().getPlayerCount() == 4) {
			setStatus(WattenFeature.GAME_READY);
			setConstraints(WattenFeature.GAME_START);
		}
	}

	private void statePause() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.PAUSE);
		setStatus(WattenFeature.PAUSE);
		setConstraints(WattenFeature.RESUME);
	}

	private void stateResume() throws Exception {
		throwExceptionIfNotAllowed(WattenFeature.RESUME);
		if (getTable().getPlayerCount() != 4) {
			throw new Exception(
					"Can not resume this game. We need 4 players to continue...");
		}
		setStatus(WattenFeature.RESUME);
		setConstraints(WattenFeature.ROUND_START);
		stateRoundEntry();
	}

	public int getPlayerCount() {
		return table.getPlayerCount();
	}

	public Player getSelectRankPlayer() {
		if (cardDealerPlayerLocation.getIndex() + 1 >= 3)
			return table.getPlayer(PlayerLocation.get(0));
		else
			return table.getPlayer(PlayerLocation.get(cardDealerPlayerLocation
					.getIndex() + 1));
	}

	public Player getSelectSuitPlayer() {
		return table.getPlayer(PlayerLocation.get(cardDealerPlayerLocation
				.getIndex()));
	}

	public void setSuitCard(Card card) {
		suitCard = card;
	}
	
	public void setRankCard(Card card){
		rankCard = card;
	}
	
	public Card getBestCard(){
		return bestCard;
	}
	
	public Card getSuitCard(){
		return suitCard;
	}
	public Card getRankCard(){
		return rankCard;
	}
}
