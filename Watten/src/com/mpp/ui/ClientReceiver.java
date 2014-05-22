package com.mpp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.mpp.tools.PlayerLocation;
import com.mpp.tools.xml.SimpleXML;
import com.mpp.ui.screens.ErrorDialog;
import com.mpp.ui.screens.GameScreen;
import com.mpp.watten.WattenGame;
import com.mpp.watten.cards.MultipleCards;
import com.mpp.watten.logic.Player;

public class ClientReceiver extends Thread {

	private BufferedReader input = null;
	private Socket socket = null;
	private WattenGame game;

	public ClientReceiver(Socket socket, WattenGame game) {
		this.socket = socket;
		this.game = game;
	}

	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

		String line = "";
		try {
			while (!socket.isClosed()) {
				line = input.readLine();
				if (line != null) {

					System.err.println("OUTPUT:" + line);

					final SimpleXML xml = new SimpleXML(line);
					xml.parse();

					String command = xml.root.getNode("command").getData()
							.toLowerCase();

					switch (command) {
					case "quit":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							socket.close();
						} else {
							error(xml);
						}
						break;
					case "create_game":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							System.out.println("Gamescreen ACK");
							Gdx.app.postRunnable(new Runnable() {

								@Override
								public void run() {
									game.getPlayer(0).setPlayerLocation(
											PlayerLocation.get(0));
									game.setScreen(new GameScreen(game,
											xml.root.getNode("name").getData()));
									game.addPlayerCurrentGame(game.getPlayer(0));
									System.out.println("Runnable");

								}
							});

						} else {
							error(xml);
						}
						break;
					case "join_game":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							System.out.println("Gamescreen ACK");
							Gdx.app.postRunnable(new Runnable() {

								@Override
								public void run() {
									game.getPlayer(0).setPlayerLocation(
											PlayerLocation.get(Integer
													.parseInt(xml.root.getNode(
															"position")
															.getData())));
									game.setScreen(new GameScreen(game,
											xml.root.getNode("name").getData()));
									game.addPlayerCurrentGame(game.getPlayer(0));

									game.sendRequest("other_players "
											+ xml.root.getNode("name")
													.getData());
									System.out.println("Runnable");

								}
							});

						} else {
							error(xml);
						}

						break;

					case "player_joined":
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {

								Player player = new Player(xml.root.getNode(
										"name").getData());
								player.setPlayerLocation(PlayerLocation
										.get(Integer.parseInt(xml.root.getNode(
												"position").getData())));
								game.addPlayerCurrentGame(new PlayerUI(player));

							}
						});

						break;

					case "other_players":
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								int otherPlayers = Integer.parseInt(xml.root
										.getNode("other_players").getData());
								for (int i = 0; i < otherPlayers; i++) {
									Player player = new Player(xml.root
											.getNode("player" + i).getData());
									player.setPlayerLocation(PlayerLocation
											.get(Integer.parseInt(xml.root
													.getNode(
															"player" + i
																	+ "_loc")
													.getData())));
									game.addPlayerCurrentGame(new PlayerUI(
											player));

								}
								System.out.println("Runnable");

							}
						});

						break;

					case "start_game":
					case "select_rank":
					case "select_suit":
						if ("NAK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {

							error(xml);

						}
						break;
					case "start_round":
						MultipleCards hand = new MultipleCards();
						hand.load(xml.root.getNode("hand"));
						break;
					case "help":
					case "list_games":
					case "info":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData()))
							System.out.println(unescape(xml.root.getNode(
									"message").getData()));
						break;
					case "chat":
						System.out.println(unescape(xml.root.getNode("message")
								.getData()));
						break;
					default:
						System.err.println(line);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Closing client output.");
			e.printStackTrace(System.err);
		} finally {
			try {
				input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}

		System.err.println("Bye");
	}

	private String unescape(String text) {
		return text.replace("\\n", "\n");
	}

	public void error(SimpleXML xml) {
		new ErrorDialog("ERROR: "
				+ unescape(xml.root.getNode("message").getData()), null);

	}

}
