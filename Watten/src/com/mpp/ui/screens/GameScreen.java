package com.mpp.ui.screens;

import com.mpp.tools.WTools;
import com.mpp.ui.CardTable;
import com.mpp.ui.WattenGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameScreen extends WattenScreen {

	Table wrapperTable;
	CardTable table;

	public GameScreen(WattenGame game, String gameName) {
		this.game = game;
		stage = new Stage();

		skin = WattenGame.getSkin();
		layoutTable = new Table(skin);
		layoutTable.setPosition(0, 0);
		layoutTable.setSize(game.getScreenWidth(), game.getScreenHeight());
		layoutTable.setBackground(WTools.getTableImage().getDrawable());
		
		table = new CardTable(game);
		layoutTable.add(table).fill();

		game.setCurrentGame(table);
		this.resize((int)game.getScreenWidth(), (int)game.getScreenHeight());
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
		super.show();
		// TODO Auto-generated method stub
		stage.addActor(layoutTable);
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
