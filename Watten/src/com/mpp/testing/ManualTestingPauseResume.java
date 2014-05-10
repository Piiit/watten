package com.mpp.testing;

import com.mpp.tools.UserInterface;
import com.mpp.tools.xml.SimpleXML;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;
import com.mpp.watten.logic.Player;
import com.mpp.watten.logic.Watten;
import com.mpp.watten.logic.WattenFeature;

public class ManualTestingPauseResume {

	public static void main(String[] args) {
		
		try {
			Watten watten = new Watten("test");
			watten.setMaxPoints(18);

			watten.getTable().addPlayer(new Player("Matthäus"));
			watten.getTable().addPlayer(new Player("Patrick"));
			watten.getTable().addPlayer(new Player("Peter"));
			watten.getTable().addPlayer(new Player("Seppl"));
			
			watten.start();
			
			int kick = 0;
			
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
					case RESUME:
						watten.addPlayer(new Player("My name is nobody"));
					break;
					
					default:
						throw new Exception("Feature without action: " + watten.getConstraints().get(selected));
				}
				if(watten.getStatus() == WattenFeature.PLAY_CARD) {
					System.out.println(watten.toString());
				} else {
					System.out.println(watten.toStringInfo());
				}
				
				SimpleXML xml = new SimpleXML(watten.getTable().serialize());
				xml.parse();
//				System.out.println(xml.toStringIntented());
				
				kick++;
				if(kick == 3) {
					watten.kickPlayer(watten.getTable().getCurrentPlayer());
				}
				
			};
			
			System.out.println("Done. Winners are " + watten.getWinnersAsString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
