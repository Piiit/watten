package com.mpp.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;

public class WTools {

	private static Texture tableTex;
	private static Image tableImage;
	private static Texture[][] cardsImages;
	private static Texture backTexture;

	public static void initiate() {

		tableTex = new Texture(Gdx.files.internal("data/table.jpg"));
		tableTex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		tableImage = new Image(tableTex);
		
		cardsImages = new Texture[Rank.values().length][Suit.values().length];
		
		backTexture = new Texture(Gdx.files.internal("data/cardback.png"));
		backTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		loadCards();
	}

	public static Image getTableImage() {
		return tableImage;
	}

	private static void loadCards() {
		for (int rank = 0; rank < Rank.values().length; rank++) {
			for (int suit = 0; suit < Suit.values().length; suit++) {
				if (suit == 0 && rank == 0)
					cardsImages[rank][suit] = new Texture(
							Gdx.files.internal("data/cards/" + Rank.get(rank)
									+ Suit.get(suit)+".png"));
				else if (rank > 0)
					cardsImages[rank][suit] = new Texture(
							Gdx.files.internal("data/cards/" + Rank.get(rank)
									+ Suit.get(suit)+".png"));

			}
		}
	}

	public static Image getCard(Rank rank, Suit suit) {
		return new Image(cardsImages[rank.ordinal()][suit.ordinal()]);

	}

	public  static Image getCardBackImage() {
		return new Image(backTexture);
	}

}
