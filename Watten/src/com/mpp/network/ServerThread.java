package com.mpp.network;

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

public class ServerThread extends Thread {
	
	private Map<String, Watten> games = new ConcurrentHashMap<String, Watten>();
	private Map<String, PrintWriter> clients = new ConcurrentHashMap<String, PrintWriter>();
	
	private Socket socket;
	private BufferedReader input = null;
	private PrintWriter output = null;
	
	private final int maxClients;
	
	public ServerThread(Socket socket, Map<String, PrintWriter> clients, Map<String, Watten> games, final int maxClients) {
		this.socket = socket;
		this.clients = clients;
		this.maxClients = maxClients;
		this.games = games;
	}
	
	public void run() {
		Player player = null;
		System.out.println("Client @ port " + socket.getPort());
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		
		try {
			if(clients.size() == maxClients) {
				output.println("Sorry, the server is full (max=" + maxClients + "). You can not enter now!");
				socket.close();
				return;
			} 

			synchronized (clients) {
				player = new Player("Player" + (clients.size() + 1));
				clients.put(player.getName(), this.output);
			}
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		String line = "";
		
		sendResponse("chat", "message", "Welcome [" + player.getName() + "]! Type H to see all commands.");
		broadcastResponse("chat", "message", "[" + player.getName() + "] entered the lobby...");
		
		try {
			while(!socket.isClosed()) {
				line = input.readLine();
				System.out.println("SERVER: Client [" + player.getName() + "] @ port " + socket.getPort() + " send a request: " + line);
				handleProtocol(line, player);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.out.println("SERVER: Client [" + player.getName() + "] @ port " + socket.getPort() + " left the room!" );
			broadcastResponse("chat", "message", "[" + player.getName() + "] left the chat room...");
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
			for(Watten game : games.values()) {
				try {
					game.getTable().getPlayer(player.getName());
					return game;
				} catch(Exception e) {
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
		String gameName = currentGame == null ? "" : currentGame.getName();
		
		SimpleXML xml = new SimpleXML(line);
		xml.parse();
		
		String command = xml.root.getNode("command").getData().toLowerCase();
		
		switch(command) {
			case "quit":
				clients.remove(player.getName());
				sendResponse(command, "type", "ACK");
				socket.close();
				return;
			case "create_game":
				gameName = xml.root.getNode("name").getData();
				try {
					if(gameName == null || gameName.length() == 0) {
						throw new Exception("You can not create a game without a name.");
					}
					if(games.get(gameName) != null) {
						throw new Exception("A game with this name already exists!");
					}
					games.put(gameName, new Watten(gameName));
					sendResponse(command, "type", "ACK", "message", "You created the game " + gameName);
				} catch (Exception e) {
					sendResponse(command, "type", "NAK", "message", "You can not create the game '" + gameName + "': " + e.getMessage());
					e.printStackTrace(System.err);
					break;
				}
			case "join_game":
				gameName = xml.root.getNode("name").getData();

				try {
					if(gameName == null) {
						throw new Exception("Please specify a game name!");
					}
					if(games.get(gameName) == null) {
						throw new Exception("Game '" + gameName + "' does not exist!");
					}
					
					games.get(gameName).addPlayer(player);
					sendResponse(command, "type", "ACK", "message", "You joined the game " + games.get(gameName));
					for(Player p : games.get(gameName).getTable().getPlayers()) {
						if(p != null && player != p) {
							broadcastResponse("chat", "message", "[" + p.getName() + "] joined your game!");
						}
					}
				} catch (Exception e) {
					sendResponse(command, "type", "NAK", "message", "You can not join the game '" + gameName + "': " + e.getMessage());
					e.printStackTrace(System.err);
				}
			break;

			case "start_game":
				try {
					currentGame.start();
					
					sendResponse(command, "type", "ACK");
					for(Player p : currentGame.getTable().getPlayers()) {
						String hand = p.getHand().serialize();
						sendResponseTo(p.getName(), "start_round", "hand", hand, "current_player", currentGame.getTable().getCurrentPlayer().serialize());
					}
					
					broadcastAndOutput("chat", "message", gameName + " started!");
					broadcastAndOutput("chat", "message", "Current player = " + currentGame.getTable().getCurrentPlayer());
				} catch (Exception e) {
					e.printStackTrace(System.err);
					sendResponse(command, "type", "NAK", "message", "Can not start game " + gameName + ": " + e.getMessage());
				}
			break;
			case "list_games":
				String gameList = "";
				int i = 0;
				for(Watten game : games.values()) {
					i++;
					gameList += i + " : " + game.getName() + "\n";
				}
				sendResponse(command, "type", "ACK", "message", "--- LIST OF GAMES --------------------\n" + gameList);
			break;
			case "help":
				sendResponse(command, "type", "ACK", "message", 
						"--- HELP -----------------------------\n" + 
						"Q           : exit\n" + 
						"N [gameName]: create a new game\n" + 
						"J [gameName]: join a created game\n" + 
						"R [name]    : New nick name\n" + 
						"L           : list all games\n" + 
						"C [text]    : broadcasts some chat to all players\n" + 
						"S           : start the game\n" + 
						"I [gameName]: Information about a game\n" +
						"--------------------------------------"); 
			break;
			case "chat":
				String msg = xml.root.getNode("message").getData();
				sendResponse(command, "type", "ACK", "message", "[-YOU-] " + msg);
				broadcastResponse(command, "message", "[" + player.getName() + "] " + msg);
            break;
			case "info":
				if(xml.root.getNode("name").getData() == null || games.get(xml.root.getNode("name").getData()) == null) {
					sendResponse(command, "type", "NAK", "message", "Can not find the game '" + gameName + "'.");
				} else {
					sendResponse(command, "type", "ACK", "message", games.get(xml.root.getNode("name").getData()).toString());
				}
			break;
//			case "R":
//				i = 0;
//				String newName = "";
//				for(String part: parts) {
//					if(i > 0) {
//						newName += part;
//					}
//					i++;
//				}
//				if (newName.length() == 0) {
//					output.println("This name is too short!");
//				} else if (nameExists(newName)) {
//					output.println("This name exists already. Please choose another one!");
//				} else {
//					broadcastResponse("chat", "message", player.getName() + " changed to " + newName);
//					output.println("Your new name is " + newName);
//					player.setName(newName);
//				}
//				
//			break;
			default:
				sendResponse(command, "type", "NAK", "message", "Please enter a valid command. Type H to see all commands!");
		}
	}

	private void sendResponseTo(String playerName, String command, String ... details) {
		synchronized (clients) {
			synchronized (clients.get(playerName)) {
				clients.get(playerName).println(getResponse(command, details));	
			}
		}
	}

	private void sendResponse(String command, String ... details) {
		synchronized (output) {
			output.println(getResponse(command, details));	
		}
	}
	
	private String getResponse(String command, String ... details) {
		String out = "";
		int i = 0;
		String tagName = ""; 
		for(String s : details) {
			if(i % 2 == 0) {
				tagName = s;
			} else {
				out += SimpleXML.createTag(tagName, s);
			}
			i++;	
		}
		out = out.replace("\n", "\\n");
		return SimpleXML.createTag("response", SimpleXML.createTag("command", command) + out);
	}
	
	private void broadcastAndOutput(String command, String ... details) {
		synchronized (clients) {
			for(PrintWriter client : clients.values()) {
				synchronized (client) {
					client.println(getResponse(command, details));	
				}
			}
		}
	}
	
	private void broadcastResponse(String command, String ... details) {
		synchronized (clients) {
			for(PrintWriter client : clients.values()) {
				synchronized (client) {
					if(client != this.output) {
						client.println(getResponse(command, details));
					}
					
				}
			}
		}
	}
	

}
