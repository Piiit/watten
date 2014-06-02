package com.mpp.ui.message;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.mpp.tools.WTools;
import com.mpp.ui.Card2D;
import com.mpp.ui.CardTable;
import com.mpp.ui.WattenGame;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;

public class MessageDialog extends Dialog {

	private static MessageDialog mD;

	private MessageDialog(String title) {

		super(title, WattenGame.getSkin());
	}

	public static void createErrorDialog(String message) {
		mD = new MessageDialog("ERROR!");
		mD.setSkin(WattenGame.getSkin());

		mD.text(message);
		mD.button("Ok").addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {

				mD.hide();
			}
		});

		mD.show(WattenGame.getCurrentScreen().getStage());
	}

	public static void createMessageDialog(String message) {
		mD = new MessageDialog("INFO:");
		mD.setSkin(WattenGame.getSkin());

		mD.text(message);
		mD.button("Ok").addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {

				mD.hide();
			}
		});

		mD.show(WattenGame.getCurrentScreen().getStage());
	}

	public static void createCardShownDialog(Suit shownSuit, Rank shownRank,
			Suit bestSuit, Rank bestRank) {
		mD = new MessageDialog("INFO:");
		mD.setSkin(WattenGame.getSkin());
		mD.clear();
		mD.add("Card shown:");
		mD.add("2nd best card:");
		mD.row();
		mD.add(WTools.getCard(shownRank, shownSuit))
				.width(CardTable.getCardWidth())
				.height(CardTable.getCardHeight());
		mD.add(WTools.getCard(bestRank, bestSuit))
				.width(CardTable.getCardWidth())
				.height(CardTable.getCardHeight());
		mD.row();

		Button okButton = new Button(WattenGame.getSkin());
		okButton.add("Ok");
		okButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {

				mD.hide();
			}
		});
		mD.add(okButton).colspan(2).width(CardTable.getCardWidth());

		mD.show(WattenGame.getCurrentScreen().getStage());

	}
}
