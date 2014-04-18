package com.mpp.testing;

import cards.Card;

public class ManualTesting {

	public static void main(String[] args) {
		Watten watten = new Watten("test");
		
		try {
			watten.getTable().addPlayer(new Player("Matthäus"));
			watten.getTable().addPlayer(new Player("Patrick"));
			watten.getTable().addPlayer(new Player("Peter"));
			watten.getTable().addPlayer(new Player("Seppl"));
			
			watten.giveCards();
			
			for(Player p : watten.getTable().getPlayers()) {
				System.out.println(p.toString() + p.getHand());
			}
			
			Card c = watten.getTable().getPlayer("Matthäus").getHand().getCard();
			Card c2 = watten.getTable().getPlayer("Peter").getHand().getCard();
			
			watten.playCard(c);
			
			watten.getTable().nextPlayer();
			watten.getTable().nextPlayer();
			
			watten.playCard(c2);

			System.out.println("Table:\n" + watten.getTable());
			
			System.out.println(watten);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
