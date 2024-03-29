package com.mpp.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.mpp.tools.WTools;
import com.mpp.ui.WattenGame;

public class JoinGameScreen  extends WattenScreen  {

	Button confirmNameButton;
	Button menuButton;
	TextField nameField;
	Label nameLabel;


	public JoinGameScreen(WattenGame _game) {

		this.game = _game;
		skin = WattenGame.getSkin();
		
		//Sets up the layoutTable and all the UI elements it will contain
		layoutTable = new Table(skin);
		layoutTable.setPosition(0, 0);
		layoutTable.setSize(game.getScreenWidth(), game.getScreenHeight());
		layoutTable.setBackground(WTools.getTableImage().getDrawable());

		
		nameLabel = new Label("Enter games name to join:", skin);
		nameLabel.setAlignment(Align.center);

		nameField = new TextField("", skin);

		confirmNameButton = new Button(skin);
		confirmNameButton.add("Join Game");

		confirmNameButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
			int pointer, int button) {
				game.sendRequest("join_game " + nameField.getText());


			}
		});
		
		menuButton = new Button(skin);
		menuButton.add("Menu");

		menuButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,

			int pointer, int button) {
				game.toMainMenu();
			}
		});
		// Adds elements to layoutTable and the table to the stage
		layoutTable.add(nameLabel).width(300f).height(50f);
		layoutTable.row();
		layoutTable.add(nameField).width(300f).height(50f);
		layoutTable.row();
		layoutTable.add(confirmNameButton).width(300f).height(50f);
		layoutTable.row();
		layoutTable.add(menuButton).width(300f).height(50f);
		stage = new Stage();

	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		super.show();
		stage.addActor(layoutTable);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		stage.clear();
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
