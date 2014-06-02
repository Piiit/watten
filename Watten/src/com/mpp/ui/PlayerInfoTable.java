package com.mpp.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlayerInfoTable extends Table {
	PlayerUI player;
	String team;
	String name;
	String roundWins;
	Label teamLabel ;
	Label turnWinsLabel;

	public PlayerInfoTable(PlayerUI player) {
		super(WattenGame.getSkin());
		this.player = player;
		team = "Team " + this.player.getTeamNumber();
		name = player.getPlayerName();
		roundWins = "Stich: " + player.getRoundWins();
		 teamLabel = new Label("Team " + this.player.getTeamNumber(), WattenGame.getSkin());
		 turnWinsLabel = new Label("Stich: " + player.getRoundWins(), WattenGame.getSkin());
		this.add(name);
		this.row();
		this.add(teamLabel);
		this.row();
		this.add(turnWinsLabel);
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		turnWinsLabel.setText("Stich: " + player.getRoundWins());
		teamLabel.setText("Team " + this.player.getTeamNumber());
		
	}

	

}
