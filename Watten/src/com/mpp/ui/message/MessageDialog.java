package com.mpp.ui.message;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.mpp.ui.Card2D;
import com.mpp.watten.WattenGame;

public class MessageDialog extends Dialog {

	private static MessageDialog mD;

	private MessageDialog(String title) {

		super(title, WattenGame.getSkin());
	}

	public static void createErrorDialog(String message) {
		mD = new MessageDialog("ERROR!");
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

	public void createCardShownDialog(String playerName, Card2D shown,
			Card2D nextBest) {
		mD = new MessageDialog("INFO:");
		mD.add("Card shown:");
		mD.add("2nd best card:");
		mD.row();
		mD.add(shown);
		mD.add(nextBest);

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
}
