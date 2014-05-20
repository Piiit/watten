package com.mpp.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.mpp.watten.WattenGame;

public class ErrorDialog extends Dialog {

	public ErrorDialog(String message, Stage stage) {
		super("Error!", WattenGame.getSkin());
		createErrorDialog(message, stage);

	}

	public ErrorDialog(String title, String message, Stage stage) {

		super(title, WattenGame.getSkin());
		createErrorDialog(message, stage);
	}

	public void createErrorDialog(String message, Stage stage) {
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

		this.show(stage);
	}

}
