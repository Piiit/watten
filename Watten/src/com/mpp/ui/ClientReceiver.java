package com.mpp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.mpp.tools.PlayerLocation;
import com.mpp.tools.xml.SimpleXML;
import com.mpp.ui.message.MessageDialog;
import com.mpp.ui.message.MessageType;
import com.mpp.ui.screens.GameScreen;
import com.mpp.watten.WattenGame;
import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.MultipleCards;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;
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
							Gdx.app.postRunnable(new Runnable() {

								@Override
								public void run() {
									game.getLocalPlayer().setPlayerLocation(
											PlayerLocation.get(0));
									game.setScreen(new GameScreen(game,
											xml.root.getNode("name").getData()));
									game.addPlayerCurrentGame(game
											.getLocalPlayer());
								}
							});

						} else {
							error(xml);
						}
						break;
					case "join_game":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							Gdx.app.postRunnable(new Runnable() {

								@Override
								public void run() {
									game.getLocalPlayer().setPlayerLocation(
											PlayerLocation.get(Integer
													.parseInt(xml.root.getNode(
															"position")
															.getData())));
									game.setScreen(new GameScreen(game,
											xml.root.getNode("name").getData()));
									game.addPlayerCurrentGame(game
											.getLocalPlayer());

									game.sendRequest("other_players "
											+ xml.root.getNode("name")
													.getData());

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

							}
						});

						break;

					case "start_game":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {

							// No idea what to put here

						} else if ("NAK".equalsIgnoreCase(xml.root.getNode(
								"type").getData())) {

							error(xml);

						}
						break;
					case "game_ready":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							game.sendRequest("start_game");

						}
						break;

					case "select_rank":
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {

								game.getLocalPlayer().handReveal();
								game.getLocalPlayer().setSelect_rank(true);
							}
						});
						break;

					case "rank_selected":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							game.getLocalPlayer().setSelect_rank(false);

						} else if ("NAK".equalsIgnoreCase(xml.root.getNode(
								"type").getData())) {

							error(xml);

						}
						break;

					case "select_suit":
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {

								game.getLocalPlayer().handReveal();
								game.getLocalPlayer().setSelect_suit(true);
							}
						});
						break;

					case "suit_selected":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							game.getLocalPlayer().setSelect_suit(false);

						} else if ("NAK".equalsIgnoreCase(xml.root.getNode(
								"type").getData())) {

							error(xml);

						}
						break;
					case "start_round":

						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {

								MultipleCards hand = new MultipleCards();
								hand.load(xml.root.getNode("hand"));
								game.getLocalPlayer().addHand(hand);

							}
						});

						break;

					case "reveal_hand":
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {

								game.getLocalPlayer().handReveal();
							}
						});
						break;
					case "your_turn":
						game.getLocalPlayer().setPlaying(true);
						break;
					case "play_card":
						if ("ACK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							
							Gdx.app.postRunnable(new Runnable() {

								@Override
								public void run() {

									int cardIndex = Integer.parseInt(xml.root
											.getNode("card_index").getData());

									game.getLocalPlayer().playCard(
											game.getLocalPlayer().getCard2D(
													cardIndex));
									game.getLocalPlayer().setPlaying(false);
								}
							});
						}else if ("NAK".equalsIgnoreCase(xml.root.getNode("type")
								.getData())) {
							error(xml);
						}
						break;

					case "card_played":
						Gdx.app.postRunnable(new Runnable() {

							@Override
							public void run() {
								try {
									game.getPlayer(
											xml.root.getNode("name").getData())
											.playCard(
													new Card2D(
															new Card(
																	Suit.valueOf(xml.root
																			.getNode(
																					"suit")
																			.getData()),
																	Rank.valueOf(xml.root
																			.getNode(
																					"rank")
																			.getData()),false ),
															game.getPlayer(xml.root
																	.getNode(
																			"name")
																	.getData())));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									MessageDialog.createErrorDialog(e.getMessage());
								}
							}
						});
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
		 MessageDialog.createErrorDialog("ERROR: "
				+ unescape(xml.root.getNode("message").getData()));

	}

}
