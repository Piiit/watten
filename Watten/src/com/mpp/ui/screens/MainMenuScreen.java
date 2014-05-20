package com.mpp.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.mpp.tools.WTools;
import com.mpp.ui.ErrorDialog;
import com.mpp.watten.WattenGame;

public class MainMenuScreen implements Screen {
	WattenGame game;
	Table layoutTable;
	Button joinGameButton;
	Button createGameButton;
	Skin skin;
	Stage stage;
	Label welcomeLabel;
	Dialog dg;
	
	public MainMenuScreen(WattenGame _game) {

		this.game = _game;
		skin = WattenGame.getSkin();
		stage = new Stage();

		layoutTable = new Table(skin);
		layoutTable.setPosition(0, 0);
		layoutTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		layoutTable.setBackground(WTools.getTableImage().getDrawable());

		welcomeLabel = new Label("", skin);
		welcomeLabel.setAlignment(Align.center);
		
		joinGameButton = new Button(skin);
		joinGameButton.add("Join Game");
		joinGameButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,

			int pointer, int button) {
				game.setScreen(new GameScreen(game)); // For testing, replace by
														// selectgamescreen
			}
		});

		createGameButton = new Button(skin);
		createGameButton.add("Create Game");
		createGameButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,

			int pointer, int button) {
				
				
				new ErrorDialog("Errors", "Lots of errors!!!!!", stage);
				
			}
		});
		
		
		layoutTable.add(welcomeLabel).width(300f).height(50f).center();
		layoutTable.row();
		layoutTable.add(joinGameButton).width(300f).height(50f);
		layoutTable.row();
		layoutTable.add(createGameButton).width(300f).height(50f);
		
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
		welcomeLabel.setText("Welcome " + game.getLocalPlayerName() );
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
