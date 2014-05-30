package com.mpp.watten;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.mpp.tools.PlayerLocation;
import com.mpp.tools.WTools;
import com.mpp.ui.CardTable;
import com.mpp.ui.ClientReceiver;
import com.mpp.ui.ClientSender;
import com.mpp.ui.PlayerUI;

import com.mpp.ui.screens.InputPlayerNameScreen;
import com.mpp.ui.screens.MainMenuScreen;
import com.mpp.ui.screens.WattenScreen;
import com.mpp.watten.logic.Player;

public class WattenGame extends Game {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private static Skin skin;
	private String localPlayerName;
	private Map<String, PlayerUI> currentPlayers = new ConcurrentHashMap<String, PlayerUI>();
	private float screenWidth;
	private float screenHeight;
	InputPlayerNameScreen firstScreen; // Splashscreen later
	static WattenScreen currentScreen;
	MainMenuScreen mainMenuScreen;
	ClientSender clientOut;

	CardTable currentGame = null;

	@Override
	public void create() {

		WTools.initiate();
		// Visual Setup
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		camera = new OrthographicCamera(screenWidth, screenHeight);
		batch = new SpriteBatch();

		// Loading Skin for all visual items
		TextureAtlas atlas = new TextureAtlas(
				Gdx.files.internal("skins/uiskin.atlas"));
		skin = new Skin(Gdx.files.internal("skins/uiskin.json"), atlas);

		// Loading of Screens
		firstScreen = new InputPlayerNameScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		localPlayerName = "AlbertoPatrizio";

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

	public void toMainMenu() {
		this.setScreen(mainMenuScreen);
	}

	public void createLocalPlayer(String name) {
		if (currentPlayers.containsKey(localPlayerName))
			currentPlayers.remove(localPlayerName);
		localPlayerName = name;
		PlayerUI player = new PlayerUI(new Player(name));
		player.setLocalPlayer(true);
		currentPlayers.put(localPlayerName, player);
	}

	public String getLocalPlayerName() {
		return localPlayerName;
	}

	public void removeCurrentPlayers() {
		PlayerUI player = currentPlayers.get(localPlayerName);

		currentPlayers.clear();

		currentPlayers.put(localPlayerName, player);
	}

	public PlayerUI getLocalPlayer() {
		return currentPlayers.get(localPlayerName);
	}

	public PlayerUI getPlayer(String name) {
		return currentPlayers.get(name);
	}

	public void sendRequest(String requestLine) {
		clientOut.prepareRequest(requestLine);

	}

	public void setCurrentGame(CardTable currentGame) {
		this.currentGame = currentGame;
	}

	public CardTable getCurrentGame() {
		return this.currentGame;
	}

	public void addPlayerCurrentGame(PlayerUI player) {
		if (currentGame != null) {
			if (player != getLocalPlayer())
				currentPlayers.put(player.getPlayerName(), player);

			player.setWattenGame(this);
			currentGame.addPlayer(player);
		}
	}

	public void removePlayerCurrentGame(String player) {
		if (currentGame != null) {
			if (currentPlayers.get(player) != getLocalPlayer()) {
				currentPlayers.get(player).leaveTable();
				currentPlayers.remove(player);
			}
		}
	}

	public void setCurrentScreen(WattenScreen currentScreen) {
		this.currentScreen = currentScreen;
	}

	public static WattenScreen getCurrentScreen() {
		return currentScreen;
	}

	private WattenGame returnThisGame() {
		return this;
	}

	public Collection<PlayerUI> getPlayers() {
		return currentPlayers.values();
	}

	public void startClientNetworkingThread() {
		new Thread() {
			public void run() {
				clientOut = new ClientSender();
				clientOut.startClientSenderThread(returnThisGame());
			}

		}.start();
	}

	public float getScreenWidth() {
		return screenWidth;
	}

	public float getScreenHeight() {
		return screenHeight;
	}
}
