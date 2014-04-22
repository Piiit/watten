package com.mpp.testing;

import logic.Player;
import logic.Watten;
import logic.WattenFeature;

import cards.Rank;
import cards.Suit;

public class ManualTesting {

	public static void main(String[] args) {
		Watten watten = new Watten("test");
		watten.setMaxPoints(18);
		
		try {
			watten.getTable().addPlayer(new Player("Matthäus"));
			watten.getTable().addPlayer(new Player("Patrick"));
			watten.getTable().addPlayer(new Player("Peter"));
			watten.getTable().addPlayer(new Player("Seppl"));
			
			watten.start();
			
			//Play a whole game...
			while(watten.getStatus() != WattenFeature.GAME_FINISHED) {

				int selected = 0;
				int i = 0;
				if(watten.getConstraints().size() > 1) {
					for(WattenFeature allowedStatus : watten.getConstraints()) {
						System.out.println(i + ": " + allowedStatus);
						i++;
					}
					selected = UserInterface.inputFieldInteger("Select!");
				} 

				switch(watten.getConstraints().get(selected)) {
					case SELECT_RANK:
						watten.stateSelectBestCardRank(Rank.EIGHT);
					break;
					case SELECT_SUIT:
						watten.stateSelectBestCardSuit(Suit.HEARTS);
					break;
					case BET:
						watten.stateTurnBet(watten.getTeam());
					break;
					case PLAY_CARD: 
						// Plays always the first hand card 
//						System.out.println(watten.getTable().getCurrentPlayer().getHand().toStringFaceUp());
//						int selectCard = UserInterface.inputFieldInteger(watten.getTable().getCurrentPlayer().getName() + ": Card?");
//						watten.playCard(watten.getTable().getCurrentPlayer().getHand().getCard(selectCard));
						watten.stateTurnPlayCard(watten.getAllowedCards().getCard());
					break;
					case SURRENDER_OR_HOLD:
						watten.stateTurnSurrenderOrHold(Watten.SURRENDER);
					break;
					
					default:
						throw new Exception("Feature without action! " + watten.getConstraints().get(selected));
				}
				if(watten.getStatus() == WattenFeature.PLAY_CARD) {
					System.out.println(watten.toString());
				} else {
					System.out.println(watten.toStringInfo());
				}
			};
			
			System.out.println("Done. Winners are " + watten.getWinnersAsString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
