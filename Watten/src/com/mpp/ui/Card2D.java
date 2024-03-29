package com.mpp.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.mpp.tools.WTools;

import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;

public class Card2D extends Table {

	
	
	Image frontImage;
	Image backImage;
	Suit cardSuit;
	Rank cardRank;
	boolean facingDown;
	PlayerUI owningPlayer;
	Card card;
	Cell parentCell;

	public Card2D(Card card, PlayerUI owningPlayer) {
		super();
		this.card = card;
		this.setSkin(WattenGame.getSkin());
		cardSuit = card.getSuit();
		cardRank = card.getRank();
		this.facingDown = card.isFaceDown();
		this.owningPlayer = owningPlayer;

		
		frontImage = WTools.getCard(cardRank, cardSuit);

		
		backImage = WTools.getCardBackImage();
//		this.setBounds(0, 0, CardTable.getCardWidth(), CardTable.getCardHeight());
		this.setSize(CardTable.getCardWidth(), CardTable.getCardHeight());

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
			this.background(backImage.getDrawable());
		else
			this.background(frontImage.getDrawable());

		this.setSize(CardTable.getCardWidth(), CardTable.getCardHeight());
	}

	// So players which are not selecting can't see their cards
	public void flipCard() {
		facingDown = card.isFaceDown();
		
		evaluateCardFacing();

	}

	// When card clicked
	public void playCard() {
		// Add card to playing field
		owningPlayer.requestCardPlay(this);
	}

	private void selectAsSuit() {
		owningPlayer.selectSuit(this);
	}

	private void selectAsRank() {
		owningPlayer.selectRank(this);
	}

	public Suit getCardsSuit() {
		return cardSuit;
	}

	public Rank getCardRank() {
		return cardRank;
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
		owningPlayer = null;
		
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
				if (owningPlayer != null) {
					System.out.println("player not null: " + owningPlayer.getPlayerName());
					if (owningPlayer.isPlaying()) {
						System.out.println("Player playing");
						playCard();
					}
					if (owningPlayer.isSelect_rank()) {
						selectAsRank();
					}
					if (owningPlayer.isSelect_suit()) {
						selectAsSuit();
					}

				}

			}
		});
	}

	public Card getCard() {
		return card;
	}
}