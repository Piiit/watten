package com.mpp.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.mpp.watten.WattenGame;
<<<<<<< HEAD
import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;

public class Card2D extends Image {

	Texture frontTexture; // Later get as parameter directly from helper class
							// creating image, reduce load
	Texture backTexture; // Later get as parameter directly from helper class
							// creating image, reduce load
	Image frontImage;
	Image backImage;
	Suit cardSuit;
	Rank cardRank;
	boolean facingDown;
	boolean played;
	PlayerUI owningPlayer;
	Card card;
	Cell parentCell;

	public Card2D(Card card, PlayerUI owningPlayer) {
		super();
		this.card = card;
		cardSuit = card.getSuit();
		cardRank = card.getRank();
		this.facingDown = card.isFaceDown();
		this.owningPlayer = owningPlayer;
		played = false;

		frontTexture = new Texture(Gdx.files.internal("data/weli.jpg"));
		frontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		frontImage = new Image(frontTexture);

		backTexture = new Texture(Gdx.files.internal("data/cardback.png"));
		backTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		backImage = new Image(backTexture);

		this.setBounds(0, 0, getWidth(), getHeight());
		this.setTouchable(Touchable.enabled);

		evaluateCardFacing();

		addActionListener();

	}

	/*
	 * Checks if card is facing up or down and changes drawable image according
	 * to position
	 */

	private void evaluateCardFacing() {
		if (facingDown)
			this.setDrawable(backImage.getDrawable());
		else
			this.setDrawable(frontImage.getDrawable());

		this.setSize(CardTable.getCardWidth(), CardTable.getCardHeight());
	}

	// So players which are not selecting can't see their cards
	public void flipCard() {
		facingDown = !facingDown;
		if (facingDown)
			card.faceUp();
		else
			card.faceDown();
		evaluateCardFacing();

	}

	// When card clicked
	public void playCard() {
		// Add card to playing field
		owningPlayer.playCard(this);
	}

	public Suit getCardsSuit() {
		return cardSuit;
	}

	public Rank getCardRank() {
		return cardRank;
	}

	public void moveCardTo(float xOffset, float yOffset) {
		this.addAction(Actions.moveTo(xOffset, yOffset, 2));
	}

	@Override
	public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);

	}

	public void setParentCell(Cell cell) {
		parentCell = cell;
	}

	// Remove it from parent cell
	public void removeFromParent() {
		parentCell.setWidget(null);
		parentCell = null;
	}

	private void addActionListener() {
		this.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				System.out.println("Touch Down Card: " + cardRank.name() + " "
						+ cardSuit.name());

				System.out.println("Facing down: " + facingDown);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y,

			int pointer, int button) {

				if (!played) {
					playCard();
					played = true;
				}

			}
		});
	}
	public Card getCard(){
		return card;
=======
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;

public class Card2D extends Image {

	Texture frontTexture; // Later get as parameter directly from helper class
							// creating image, reduce load
	Texture backTexture; // Later get as parameter directly from helper class
							// creating image, reduce load
	Image frontImage;
	Image backImage;
	Suit cardSuit;
	Rank cardRank;
	boolean facingDown;
	boolean played;
	PlayerUI owningPlayer;
	Cell parentCell;

	public Card2D(Suit _cardSuit, Rank _cardRank, boolean facingDown,	PlayerUI owningPlayer) {
		super();
		cardSuit = _cardSuit;
		cardRank = _cardRank;
		this.facingDown = facingDown;
		this.owningPlayer = owningPlayer;
		played = false;

		frontTexture = new Texture(Gdx.files.internal("data/weli.jpg"));
		frontTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		frontImage = new Image(frontTexture);

		backTexture = new Texture(Gdx.files.internal("data/cardback.png"));
		backTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		backImage = new Image(backTexture);

		this.setBounds(0, 0, getWidth(), getHeight());
		this.setTouchable(Touchable.enabled);

		evaluateCardFacing();

		addActionListener();

	}

	/*
	 * Checks if card is facing up or down and changes drawable image according
	 * to position
	 */

	private void evaluateCardFacing() {
		if (facingDown)
			this.setDrawable(backImage.getDrawable());
		else
			this.setDrawable(frontImage.getDrawable());

		this.setSize(CardTable.getCardWidth(), CardTable.getCardHeight());
	}

	// So players which are not selecting can't see their cards
	public void flipCard() {
		facingDown = !facingDown;
		evaluateCardFacing();

	}

	// When card clicked
	public void playCard() {
		// Add card to playing field
		owningPlayer.playCard(this);
	}

	public Suit getCardsSuit() {
		return cardSuit;
	}

	public Rank getCardRank() {
		return cardRank;
	}

	public void moveCardTo(float xOffset, float yOffset) {
		this.addAction(Actions.moveTo(xOffset, yOffset, 2));
	}

	@Override
	public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);

	}

	public void setParentCell(Cell cell) {
		parentCell = cell;
	}

	//Remove it from parent cell
	public void removeFromParent() {
		parentCell.setWidget(null);
		parentCell = null;
	}

	private void addActionListener() {
		this.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				System.out.println("Touch Down Card: " + cardRank.name() + " "
						+ cardSuit.name());

				System.out.println("Facing down: " + facingDown);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y,

			int pointer, int button) {
				
				if (!played) {
					playCard();
					played = true;
				}

			}
		});
>>>>>>> refs/remotes/origin/master
	}
}
