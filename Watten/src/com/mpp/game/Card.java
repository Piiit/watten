package com.mpp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mpp.tools.Suit;
import com.mpp.tools.Value;

public class Card extends Image {

	Texture tex;
	Sprite sprite;
	Suit cardSuit;
	Value cardValue;
	float touchPrevPosX;
	float touchPrevPosY;

	public Card(Suit _cardSuit, Value _cardValue) {
		super(new Texture(Gdx.files.internal("data/weli.jpg")));
		tex = new Texture(Gdx.files.internal("data/weli.jpg"));
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.setHeight(tex.getHeight());
		this.setWidth(tex.getWidth());
		this.setBounds(0, 0, getWidth(), getHeight());
		this.setTouchable(Touchable.enabled);
		this.setPosition(50, 50);
		sprite = new Sprite(tex);
		cardSuit = _cardSuit;
		cardValue = _cardValue;
		addActionListener();
		
	}
	
	public void playCard(){
		//Add card to playing field
	}
	public Suit getCardsSuit(){
		return cardSuit;
	}
	 public Value getCardValue(){
		 return cardValue;
	 }
	
	public void changePosition(float xOffset, float yOffset){
		this.setPosition(getX()+xOffset, getY()+yOffset);
	}

	@Override
	public void draw(Batch batch, float alpha) {
//		sprite.draw(batch, alpha);
		super.draw(batch, alpha);
		
	}

	private void addActionListener() {
		this.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				System.out.println("Touch Down Card: " + cardValue.name() + " " + cardSuit.name());
				touchPrevPosX = x;
				touchPrevPosY = y;
				System.out.println("Mouse Pos: " + x +", " + y + "   Card Pos: " + getX() + ", "+ getY());
				return true;
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				changePosition(x-touchPrevPosX, y-touchPrevPosY);
				touchPrevPosX = x;
				touchPrevPosY = y;		

			}

			public void touchUp(InputEvent event, float x, float y,
					
					int pointer, int button) {
				System.out.println("Touch Up Card: " + cardValue.name() + " " + cardSuit.name());


			}
		});
	}
}
