package com.mpp.watten;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mpp.tools.WTools;
import com.mpp.ui.screens.InputPlayerNameScreen;
import com.mpp.ui.screens.MainMenuScreen;

public class WattenGame extends Game {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Stage stage;
	private static Skin skin;
	private String localPlayerName = "";
	InputPlayerNameScreen firstScreen; // Splashscreen later
	MainMenuScreen mainMenuScreen;

	@Override
	public void create() {
		
		WTools.initiate();
		// Visual Setup
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(w, h);
		batch = new SpriteBatch();

		// Loading Skin for all visual items
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("skins/uiskin.atlas"));
		skin = new Skin(Gdx.files.internal("skins/uiskin.json"), atlas);

		// Loading of Screens
		firstScreen = new InputPlayerNameScreen(this);
		mainMenuScreen = new MainMenuScreen(this);

		// Starts the game displaying the first screen
		this.setScreen(firstScreen);

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

	public void toMainMenu() {
		this.setScreen(mainMenuScreen);
	}

	public void setLocalName(String name) {
		localPlayerName = name;
	}

	public String getLocalPlayerName() {
		return localPlayerName;
	}
}
