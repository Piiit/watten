package com.mpp.testing;

import com.mpp.tools.Rank;
import com.mpp.tools.Suit;

public class ManualTesting {

	public static void main(String[] args) {
		Watten watten = new Watten("test");
		watten.setMaxPoints(18);
		
		try {
			watten.getTable().addPlayer(new Player("Matthäus"));
			watten.getTable().addPlayer(new Player("Patrick"));
			watten.getTable().addPlayer(new Player("Peter"));
			watten.getTable().addPlayer(new Player("Seppl"));
			
			watten.stateGameEntry();
			
			//Play a whole game...
			while(watten.getStatus() != WattenStatus.GAME_FINISHED) {

				int selected = 0;
				int i = 0;
				for(WattenStatus allowedStatus : watten.getAllowedStates()) {
					System.out.println(i + ": " + allowedStatus);
					i++;
				}

				if(i > 1) {
					selected = UserInterface.inputFieldInteger("Select!");
				} 
				switch(watten.getAllowedStates().get(selected)) {
					case TURN_START:
						watten.stateTurnEntry();
					break;
					case ROUND_START:
						watten.stateRoundEntry();
					break;
					case SELECT_RANK:
						watten.stateSelectBestCardRank(Rank.EIGHT);
					break;
					case SELECT_SUIT:
						watten.stateSelectBestCardSuit(Suit.HEARTS);
					break;
					case BET: // Bet not implemented yet
					case PLAY_CARD: 
						// Plays always the first hand card 
//						System.out.println(watten.getTable().getCurrentPlayer().getHand().toStringFaceUp());
//						int selectCard = UserInterface.inputFieldInteger(watten.getTable().getCurrentPlayer().getName() + ": Card?");
//						watten.playCard(watten.getTable().getCurrentPlayer().getHand().getCard(selectCard));
						watten.stateTurnPlayCard(watten.getTable().getCurrentPlayer().getHand().getCard());
					break;
				}

				System.out.println(watten);
			};
			
			System.out.println("Done. Winners are " + watten.getWinnersAsString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
