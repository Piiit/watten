package com.mpp.watten;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mpp.ui.screens.MainMenuScreen;

public class WattenGame extends Game {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Stage stage;
	private static Skin skin;
	MainMenuScreen mainMenu;

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(w, h);
		batch = new SpriteBatch();
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("skins/uiskin.atlas"));
		skin = new Skin(Gdx.files.internal("skins/uiskin.json"), atlas);
		mainMenu = new MainMenuScreen(this);
		this.setScreen(mainMenu);
	

	}

	@Override
	public void dispose() {
		batch.dispose();
		// stage.dispose();

	}

	@Override
	public void render() {
		super.render();
		camera.update();
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

	public static Skin getSkin() {
		return skin;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

}
