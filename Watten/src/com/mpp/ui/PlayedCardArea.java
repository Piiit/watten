package com.mpp.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class PlayedCardArea extends Image {

	Image standardDrawable;
	
	public PlayedCardArea(float xPosition, float yPosition) {
		super();
	Texture standardTexture = new Texture(Gdx.files.internal("data/playedcardarea.png"));
	standardDrawable = new Image(standardTexture );
	this.setDrawable(standardDrawable.getDrawable());
	
		this.setPosition(xPosition, yPosition);
		this.setSize(CardTable.getCardWidth(), CardTable.getCardHeight());

	}
	

	@Override
	public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);
	}
	
	//Add card if played
	public void addCard(Card2D card){
		this.setDrawable(card.getDrawable());
		this.setSize(CardTable.getCardWidth(), CardTable.getCardHeight());
	}
	
	//Remove card after every round
	public void removeCard(){
		this.setDrawable(standardDrawable.getDrawable());
		this.setSize(CardTable.getCardWidth(), CardTable.getCardHeight());

	}

}
