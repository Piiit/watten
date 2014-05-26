package com.mpp.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.mpp.watten.WattenGame;

public class ErrorDialog extends Dialog {

	public ErrorDialog(String message) {
		super("Error!", WattenGame.getSkin());
		createErrorDialog(message);

	}

	public ErrorDialog(String title, String message) {

		super(title, WattenGame.getSkin());
		createErrorDialog(message);
	}

	public void createErrorDialog(String message) {
		this.text(message);
		this.button("Ok").addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {

				hide();
			}
		});

		// this.show((Stage)Gdx.input.getInputProcessor()); //Bad solution!!!
		this.show(WattenGame.getCurrentScreen().getStage());
	}

}
