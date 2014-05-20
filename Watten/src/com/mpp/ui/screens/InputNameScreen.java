package com.mpp.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mpp.watten.WattenGame;

public class InputNameScreen implements Screen {

	Table layoutTable;
	Button confirmButton;
	Skin skin;

	Stage stage;
	WattenGame game;

	public InputNameScreen(WattenGame _game) {
		this.game = _game;
		skin = game.getSkin();

		layoutTable = new Table(skin);
		layoutTable.setPosition(0, 0);
		layoutTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		confirmButton = new Button(skin);
		confirmButton.add("Continue");
		
		confirmButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,

			int pointer, int button) {
				game.setScreen(new MainMenuScreen(game));
			}
		});

	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
