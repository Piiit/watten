package com.mpp.ui.screens;

import com.mpp.tools.PlayerLocation;
import com.mpp.ui.CardTable;
import com.mpp.watten.WattenGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameScreen implements Screen {

	WattenGame game;
	Table wrapperTable;
	CardTable table;
	Stage stage;

	public GameScreen(WattenGame game, String gameName) {
		this.game = game;
		wrapperTable = new Table(WattenGame.getSkin());
		table = new CardTable(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		wrapperTable.add(table).width(Gdx.graphics.getWidth())
				.height(Gdx.graphics.getHeight());
		wrapperTable.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() / 2);
		stage = new Stage();
		stage.addActor(wrapperTable);

		// Add local player to table
//		game.getPlayer(0).setPlayerLocation(PlayerLocation.South);
		game.setCurrentGame(table);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		Gdx.input.setInputProcessor(stage);
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
