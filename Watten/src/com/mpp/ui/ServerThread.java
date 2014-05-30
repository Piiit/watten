package com.mpp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mpp.tools.xml.SimpleXML;
import com.mpp.watten.logic.Player;
import com.mpp.watten.logic.Watten;
import com.mpp.watten.logic.WattenFeature;

public class ServerThread extends Thread {

	private Map<String, Watten> games = new ConcurrentHashMap<String, Watten>();
	private Map<String, PrintWriter> clients = new ConcurrentHashMap<String, PrintWriter>();

	private Socket socket;
	private BufferedReader input = null;
	private PrintWriter output = null;

	private final int maxClients;

	public ServerThread(Socket socket, Map<String, PrintWriter> clients,
			Map<String, Watten> games, final int maxClients) {
		this.socket = socket;
		this.clients = clients;
		this.maxClients = maxClients;
		this.games = games;
	}

	public void run() {
		Player player = null;
		System.out.println("Client @ port " + socket.getPort());
		try {
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

		try {
			String playerName = input.readLine();
			player = new Player(playerName);
			if (clients.size() == maxClients) {
				output.println("NACK");
				output.println("Sorry, the server is full (max=" + maxClients
						+ "). You can not enter now!");
				socket.close();
				return;
			}

			// If a player with the same name is already on the server, the
			// request is rejected
			if (clients.containsKey(playerName)) {
				output.println("NACK");
				output.println("Sorry, a player with this name is already playing!");
				socket.close();
				return;
			}

			output.println("ACK");

			synchronized (clients) {

				clients.put(player.getName(), this.output);
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		String line = "";

		sendResponse("chat", "message", "Welcome [" + player.getName()
				+ "]! Type H to see all commands.");
		sendResponseToOthers("chat", "message", "[" + player.getName()
				+ "] entered the lobby...");

		try {
			while (!socket.isClosed()) {
				line = input.readLine();
				System.out.println("SERVER: Client [" + player.getName()
						+ "] @ port " + socket.getPort() + " sent a request: "
						+ line);

				handleProtocol(line, player);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.out.println("SERVER: Client [" + player.getName()
					+ "] @ port " + socket.getPort() + " left the room!");
			sendResponseToOthers("chat", "message", "[" + player.getName()
					+ "] left the lobby...");
		} finally {
			try {
				input.close();
				socket.close();
				clients.remove(player.getName());
				synchronized (player) {
					getGame(player).kickPlayer(player);
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	private Watten getGame(Player player) {
		synchronized (games) {
			for (Watten game : games.values()) {
				try {
					game.getTable().getPlayer(player.getName());
					return game;
				} catch (Exception e) {
				}
			}
		}
		return null;
	}

	private void handleProtocol(String line, Player player) throws Exception {
		if (line == null) {
			socket.close();
		}

		Watten currentGame = getGame(player);

		Player currentPlayer = currentGame == null ? null : currentGame
				.getTable().getCurrentPlayer();
		String gameName = currentGame == null ? "" : currentGame.getName();

		SimpleXML xml = new SimpleXML(line);
		xml.parse();

		String command = xml.root.getNode("command").getData().toLowerCase();

		boolean isCurrentPlayer = (currentPlayer != null && player.getName()
				.equals(currentPlayer.getName())) ? true : false;

		switch (command) {
		case "quit":
			clients.remove(player.getName());
			sendResponse(command, "type", "ACK");
			sendResponseToAll("chat", "message", "Player '" + player.getName()
					+ "' left the game!");
			socket.close();
			return;
		case "create_game":
			gameName = xml.root.getNode("name").getData();
			try {
				if (gameName == null || gameName.length() == 0) {
					throw new Exception(
							"You can not create a game without a name.");
				}
				if (games.get(gameName) != null) {
					throw new Exception("A game with this name already exists!");
				}
				games.put(gameName, new Watten(gameName));
				games.get(gameName).addPlayer(player);
				sendResponse(command, "type", "ACK", "message",
						"You created and joined the game ", "name", gameName);
				break;
			} catch (Exception e) {
				sendResponse(
						command,
						"type",
						"NAK",
						"message",
						"You can not create the game '" + gameName + "': "
								+ e.getMessage());
				e.printStackTrace(System.err);
				break;
			}
		case "join_game":
			gameName = xml.root.getNode("name").getData();

			try {
				if (gameName == null) {
					throw new Exception("Please specify a game name!");
				}
				if (games.get(gameName) == null) {
					throw new Exception("Game '" + gameName
							+ "' does not exist!");
				}

				games.get(gameName).addPlayer(player);

				sendResponse(command, "type", "ACK", "message",
						"You joined the game " + games.get(gameName), "name",
						gameName, "position", ""
								+ player.getPlayerLocation().getIndex());

				sendResponseToOthersInGame(gameName, "player_joined", "name",
						player.getName(), "position", ""
								+ player.getPlayerLocation().getIndex());

				sendResponseToOthersInGame(gameName, "chat", "message", "["
						+ player.getName() + "] joined your game!");

				// Tells current player that game can be started
				System.out.println("Current playerCount: "+games.get(gameName).getPlayerCount() );
				if (games.get(gameName).getPlayerCount() == 4) {
					sendResponseTo(player.getName(), "game_ready", "type",
							"ACK");
				}
			} catch (Exception e) {
				sendResponse(
						command,
						"type",
						"NAK",
						"message",
						"You can not join the game '" + gameName + "': "
								+ e.getMessage());
				e.printStackTrace(System.err);
			}
			break;

		case "start_game":
			gameName = currentGame.getName();

			try {
				if (gameName == null) {
					throw new Exception("Please specify a game name!");
				}
				if (games.get(gameName) == null) {
					throw new Exception("Game '" + gameName
							+ "' does not exist!");
				}

				currentGame.start();

				sendResponse(command, "type", "ACK");
				for (Player p : currentGame.getTable().getPlayers()) {
					String hand = p.getHand().serialize();
				
					sendResponseTo(p.getName(), "start_round", "hand", hand);
				}

				sendResponseToAllInGame(gameName, "chat", "message",
						"Game with name '" + gameName + "' started!");
				sendResponseToAllInGame(gameName, "chat", "message",
						"Current player = " + currentPlayer.getName());

				sendResponseTo(currentGame.getSelectRankPlayer().getName(),
						"select_rank");

			} catch (Exception e) {
				e.printStackTrace(System.err);
				sendResponse(
						command,
						"type",
						"NAK",
						"message",
						"Can not start game '" + gameName + "', because '"
								+ e.getMessage() + "'.");
			}
			break;
		case "rank_selected":
			if (isCurrentPlayer) {
				currentGame.stateSelectBestCardRank(currentPlayer
						.getHand()
						.getCard(
								Integer.parseInt(xml.root.getNode("card_index")
										.getData())).getRank());
				sendResponse(command, "type", "ACK");

				sendResponseTo(currentGame.getSelectSuitPlayer().getName(),
						"select_suit");

			} else {
				sendResponse(command, "type", "NAK", "message",
						"You are not allowed to select the rank ! It's "
								+ currentPlayer.getName() + " turn !");
			}
			break;
		case "suit_selected":
			if (isCurrentPlayer) {
				currentGame.stateSelectBestCardSuit(currentPlayer
						.getHand()
						.getCard(
								Integer.parseInt(xml.root.getNode("card_index")
										.getData())).getSuit());

				sendResponse(command, "type", "ACK");
				sendResponseToOthersInGame(gameName, "reveal_hand", "");
				sendResponseTo(currentGame.getTable().getCurrentPlayer()
						.getName(), "your_turn");

			} else {
				sendResponse(command, "type", "NAK", "message",
						"You are not allowed to select the suit ! It's "
								+ currentPlayer.getName() + " turn !");
			}
			break;
		case "card_played":
			if (isCurrentPlayer) {
				int cardIndex = Integer.parseInt(xml.root.getNode("card_index")
						.getData());
				try{
				currentGame.stateTurnPlayCard(currentPlayer.getHand().getCard(
						cardIndex));
				}catch(Exception e){
					sendResponse(command, "type", "NAK", "message", e.getMessage());
					break;
				}
				// sendResponse(command, "type", "ACK");
				// Add if's or switch to check game status, then depending on
				// status send command
				// if(currentGame.getStatus() ==WattenFeature.ROUND_FINISHED){
				// sendResponseToAll("round_finished", "winner",
				// currentGame.getTurnWinner().getName());
				// }
				sendResponseTo(player.getName(), "play_card", "type", "ACK","card_index", ""
						+ cardIndex);
				sendResponseToOthers("card_played", "name", player.getName(),
						"rank", player.getHand().getCard(cardIndex).getRank()
								.toString(), "suit",
						player.getHand().getCard(cardIndex).getSuit()
								.toString());
				
				System.err.println("Current status: "+currentGame.getStatus());
				sendResponseTo(currentGame.getTable().getCurrentPlayer()
						.getName(), "your_turn");
			} else {
				sendResponse(command, "type", "NAK", "message",
						"You are not allowed to play this card ! It's "
								+ currentPlayer.getName() + " turn !");
			}
			break;

		case "other_players":

			if (gameName == null) {
				throw new Exception("Please specify a game name!");
			}
			if (games.get(gameName) == null) {
				throw new Exception("Game '" + gameName + "' does not exist!");
			}
			int arrayCounter = 2;
			int playerCounter = 0;
			String[] playerInfo = new String[18];
			for (Player p : currentGame.getTable().getPlayers()) {
				if (p != null && p != player) {
					playerInfo[arrayCounter] = "player" + playerCounter;
					arrayCounter++;

					playerInfo[arrayCounter] = p.getName();
					arrayCounter++;

					playerInfo[arrayCounter] = "player" + playerCounter
							+ "_loc";
					arrayCounter++;

					playerInfo[arrayCounter] = ""
							+ p.getPlayerLocation().ordinal();
					arrayCounter++;

					playerCounter++;

				}

			}
			playerInfo[0] = "other_players";
			playerInfo[1] = "" + playerCounter;
			sendResponse(command, playerInfo);
			break;
		case "list_games":
			String gameList = "";
			int i = 0;
			for (Watten game : games.values()) {
				i++;
				gameList += i + " : " + game.getName() + "\n";
			}
			sendResponse(command, "type", "ACK", "message",
					"--- LIST OF GAMES --------------------\n" + gameList);
			break;
		case "help":
			sendResponse(
					command,
					"type",
					"ACK",
					"message",
					"--- HELP -----------------------------\n"
							+ "Q           : exit\n"
							+ "N [gameName]: create a new game\n"
							+ "J [gameName]: join a created game\n"
							+ "R [name]    : New nick name\n"
							+ "L           : list all games\n"
							+ "C [text]    : broadcasts some chat to all players\n"
							+ "S           : start the game\n"
							+ "I [gameName]: Information about a game\n"
							+ "--------------------------------------");
			break;
		case "chat":
			String msg = xml.root.getNode("message").getData();
			sendResponse(command, "type", "ACK", "message", "[-YOU-] " + msg);
			sendResponseToOthers(command, "message", "[" + player.getName()
					+ "] " + msg);
			break;
		case "info":
			if (xml.root.getNode("name").getData() == null
					|| games.get(xml.root.getNode("name").getData()) == null) {
				sendResponse(command, "type", "NAK", "message",
						"Can not find the game '" + gameName + "'.");
			} else {
				sendResponse(command, "type", "ACK", "message",
						games.get(xml.root.getNode("name").getData())
								.toString());
			}
			break;
		// case "R":
		// i = 0;
		// String newName = "";
		// for(String part: parts) {
		// if(i > 0) {
		// newName += part;
		// }
		// i++;
		// }
		// if (newName.length() == 0) {
		// output.println("This name is too short!");
		// } else if (nameExists(newName)) {
		// output.println("This name exists already. Please choose another one!");
		// } else {
		// broadcastResponse("chat", "message", player.getName() +
		// " changed to " + newName);
		// output.println("Your new name is " + newName);
		// player.setName(newName);
		// }
		//
		// break;
		default:
			sendResponse(command, "type", "NAK", "message",
					"Please enter a valid command. Type H to see all commands!");
		}
	}

	private void sendResponseTo(String playerName, String command,
			String... details) {
		synchronized (clients) {
			synchronized (clients.get(playerName)) {
				clients.get(playerName).println(getResponse(command, details));
			}
		}
	}

	private void sendResponse(String command, String... details) {
		synchronized (output) {
			output.println(getResponse(command, details));
		}
	}

	private String getResponse(String command, String... details) {
		String out = "";
		int i = 0;
		String tagName = "";
		for (String s : details) {
			if (i % 2 == 0) {
				tagName = s;
			} else {
				out += SimpleXML.createTag(tagName, s);
			}
			i++;
		}
		out = out.replace("\n", "\\n");
		return SimpleXML.createTag("response",
				SimpleXML.createTag("command", command) + out);
	}

	private void sendResponseToAll(String command, String... details) {
		synchronized (clients) {
			for (PrintWriter client : clients.values()) {
				synchronized (client) {
					client.println(getResponse(command, details));
				}
			}
		}
	}

	private void sendResponseToOthers(String command, String... details) {
		synchronized (clients) {
			for (PrintWriter client : clients.values()) {
				synchronized (client) {
					if (client != this.output) {
						client.println(getResponse(command, details));
					}

				}
			}
		}
	}

	private void sendResponseToOthersInGame(String gameName, String command,
			String... details) throws Exception {
		synchronized (games) {
			Watten game = games.get(gameName);
			if (game == null) {
				throw new Exception("Game with name '" + gameName
						+ "' does not exist!");
			}
			for (Player p : game.getTable().getPlayers()) {
				synchronized (clients) {
					if (p != null && clients.get(p.getName()) != this.output) {
						sendResponseTo(p.getName(), command, details);
					}
				}
			}
		}
	}

	private void sendResponseToAllInGame(String gameName, String command,
			String... details) throws Exception {
		synchronized (games) {
			Watten game = games.get(gameName);
			if (game == null) {
				throw new Exception("Game with name '" + gameName
						+ "' does not exist!");
			}
			for (Player p : game.getTable().getPlayers()) {
				if (p != null) {
					sendResponseTo(p.getName(), command, details);
				}
			}
		}
	}

}
