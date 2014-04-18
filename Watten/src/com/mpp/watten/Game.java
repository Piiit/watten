package com.mpp.watten;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mpp.game.Card;
import com.mpp.tools.Suit;
import com.mpp.tools.Rank;

public class Game implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Stage stage;

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(w,h);
		batch = new SpriteBatch();

		

		stage = new Stage();
		stage.addActor(new Card(Suit.ACORNS, Rank.EIGHT));
		Gdx.input.setInputProcessor(stage);

		}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	
}
