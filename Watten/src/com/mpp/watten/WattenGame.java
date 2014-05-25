package com.mpp.watten;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mpp.tools.WTools;
<<<<<<< HEAD
import com.mpp.ui.CardTable;
import com.mpp.ui.ClientReceiver;
import com.mpp.ui.ClientSender;
import com.mpp.ui.PlayerUI;
=======
import com.mpp.ui.ClientReceiver;
import com.mpp.ui.ClientSender;
>>>>>>> refs/remotes/origin/master
import com.mpp.ui.screens.InputPlayerNameScreen;
import com.mpp.ui.screens.MainMenuScreen;
import com.mpp.watten.logic.Player;

public class WattenGame extends Game {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private static Skin skin;
	private PlayerUI players[] = new PlayerUI[4];
	InputPlayerNameScreen firstScreen; // Splashscreen later
	MainMenuScreen mainMenuScreen;
	ClientSender clientOut;
<<<<<<< HEAD
	CardTable currentGame = null;
=======
>>>>>>> refs/remotes/origin/master

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

	public void createLocalPlayer(String name) {
		players[0] = new PlayerUI(new Player(name));
		players[0].setLocalPlayer(true);
	}

	public String getLocalPlayerName() {
		return players[0].getPlayerName();
	}
	
	public PlayerUI getPlayer(int id){
		return players[id];
	}

	public  void sendRequest(String requestLine) {
		clientOut.prepareRequest(requestLine);
				
	}
	
	public void setCurrentGame(CardTable currentGame){
		this.currentGame = currentGame;
	}
	
	public void addPlayerCurrentGame(PlayerUI player){
		if(currentGame != null){
			currentGame.addPlayer(player);
		}
	}

	private WattenGame returnThisGame() {
		return this;
	}

	public void startClientNetworkingThread() {
		new Thread() {
			public void run() {
				clientOut = new ClientSender();
				clientOut.startClientSenderThread(returnThisGame());
			}

		}.start();
	}

	public  void sendRequest(String requestLine) {
		clientOut.prepareRequest(requestLine);
				
	}

	private WattenGame returnThisGame() {
		return this;
	}

	public void startClientNetworkingThread() {
		new Thread() {
			public void run() {
				clientOut = new ClientSender();
				clientOut.startClientSenderThread(returnThisGame());
			}

		}.start();
	}
}
