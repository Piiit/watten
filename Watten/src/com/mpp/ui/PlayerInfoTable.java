package com.mpp.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mpp.game.Player;
import com.mpp.watten.WattenGame;

public class PlayerInfoTable extends Table {
	Player player;
	String team;
	String name;
	String roundWins;

	public PlayerInfoTable(Player player) {
		super(WattenGame.getSkin());
		this.player = player;
		team = "Team " + this.player.getTeamNumber();
		name = player.getPlayerName();
		roundWins = "Stich: " + player.getRoundWins();
		
		this.add(name);
		this.row();
		this.add(team);
		this.row();
		this.add(roundWins);
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		roundWins = "Stich: " + player.getRoundWins();
	}
	

}
