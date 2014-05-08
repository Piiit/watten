package com.mpp.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import com.mpp.tools.xml.Loadable;
import com.mpp.tools.xml.SimpleXML;
import com.mpp.watten.logic.Player;
import com.mpp.watten.logic.Watten;

public class ServerThread extends Thread {
	
	//vector should do the job, but just to be sure, I use semaphores.
	private Semaphore clientsPermit = new Semaphore(1); 
	private Semaphore gamesPermit = new Semaphore(1);
	
	//Vector is nearly deprecated(?), but works with concurrent access
	private Vector<ServerThread> clients; 	
	
	private Vector<Watten> games;
	
	private Map<String, PrintWriter> mapPlayerNameClient = new HashMap<String, PrintWriter>();
	
	private Watten joinedGame;
	
	private Socket socket;
	private BufferedReader input = null;
	private PrintWriter output = null;
	private Player player = null;
	private final int maxClients;
	
	public ServerThread(Socket socket, Vector<ServerThread> clients, Vector<Watten> games, final int maxClients) {
		this.socket = socket;
		try {
			clientsPermit.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.clients = clients;
		clientsPermit.release();
		this.maxClients = maxClients;
		try {
			gamesPermit.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.games = games;
		gamesPermit.release();
	}
	
	public void run() {
		System.out.println("Client @ port " + socket.getPort());
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			clientsPermit.acquire();
			if(clients.size() == maxClients) {
				output.println("Sorry, the server is full (max=" + maxClients + "). You can not enter now!");
				clientsPermit.release();
				socket.close();
				return;
			} 
			clients.add(this);
			player = new Player("Player" + socket.getPort());
			mapPlayerNameClient.put(player.getName(), output);
			clientsPermit.release();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		boolean done = false;
		String line = "";
		
		sendResponse("chat", "message", "Welcome [" + player.getName() + "]! Type H to see all commands.");
		broadcastResponse("chat", "message", "[" + player.getName() + "] entered the lobby...");
		
		done = false;
		while(!done) {
			try {
				line = input.readLine();
				done = handleProtocol(line);
			} catch (Exception e) {
				e.printStackTrace();
				done = true;
			}
		}	

		try {
			socket.close();
			clientsPermit.acquire();
			clients.remove(this);
			mapPlayerNameClient.remove(this.player.getName());
			//TODO Reset or pause game if some client exists...
			// getGame(player).;
			clientsPermit.release();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("SERVER: Client [" + player.getName() + "] @ port " + socket.getPort() + " left the room!");
		broadcastResponse("chat", "message", "[" + player.getName() + "] left the chat room...");
	}
	
	private Watten getGame(Player player) {
		for(Watten game : games) {
			try {
				game.getTable().getPlayer(player.getName());
				return game;
			} catch(Exception e) {
			}
		}
		return null;
	}
	
	private boolean handleProtocol(String line) throws Exception {
		if (line == null) {
			return false;
		}
		
		Watten currentGame = getGame(player);
		String gameName = currentGame == null ? "" : currentGame.getName();
		
		SimpleXML xml = new SimpleXML(line);
		xml.parse();
		
		System.out.println("SERVER: " + line);
		
		String command = xml.root.getNode("command").getData().toLowerCase();
		
		switch(command) {
			case "quit":
				clientsPermit.acquire();
				mapPlayerNameClient.remove(player.getName());
				clients.removeElement(this);
				clientsPermit.release();
				sendResponse(command, "type", "ACK");
				return true;
			case "create_game":
				gameName = xml.root.getNode("name").getData();
				try {
					if(gameName == null || gameName.length() == 0) {
						throw new Exception("You can not create a game without a name.");
					}
					for(Watten g : games) {
						if(g.getName().equalsIgnoreCase(gameName)) {
							throw new Exception("A game with this name already exists!");
						}
					}
					gamesPermit.acquire();
					games.add(new Watten(gameName));
					gamesPermit.release();
					sendResponse(command, "type", "ACK", "message", "You created the game " + gameName);
				} catch (Exception e) {
					sendResponse(command, "type", "NAK", "message", "You can not create the game '" + gameName + "': " + e.getMessage());
					e.printStackTrace();
					break;
				}
			case "join_game":
				gameName = xml.root.getNode("name").getData();

				try {
					joinedGame = null;
					gamesPermit.acquire();
					for(Watten game: games) {
						if(game.getName().equalsIgnoreCase(gameName)) {
							game.getTable().addPlayer(this.player);
							joinedGame = game;
							sendResponse(command, "type", "ACK", "message", "You joined the game " + joinedGame);
							for(Player p : game.getTable().getPlayers()) {
								if(p != null && this.player != p) {
									broadcastResponse("chat", "message", "[" + p.getName() + "] joined your game!");
								}
							}
							break;
						}
					}
					gamesPermit.release();
					if(joinedGame == null) {
						throw new Exception("Game does not exist!");
					}
				} catch (Exception e) {
					sendResponse(command, "type", "NAK", "message", "You can not join the game '" + gameName + "': " + e.getMessage());
					e.printStackTrace();
				}
			break;

			case "start_game":
				try {
					currentGame.start();
					
					sendResponse(command, "type", "ACK");
					for(Player player : currentGame.getTable().getPlayers()) {
						String hand = player.getHand().serialize();
						sendResponseTo(player.getName(), "start_round", "hand", hand, "current_player", currentGame.getTable().getCurrentPlayer().serialize());
					}
					
					broadcastAndOutput("chat", "message", gameName + " started!");
					broadcastAndOutput("chat", "message", "Current player = " + currentGame.getTable().getCurrentPlayer());
				} catch (Exception e1) {
					e1.printStackTrace();
					sendResponse(command, "type", "NAK", "message", "Can not start game " + gameName + ": " + e1.getMessage());
				}
			break;
			case "list_games":
				String gameList = "";
				int i = 0;
				for(Watten game : games) {
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
				boolean found = false;
				for(Watten game : games) {
					if(game.getName().equals(xml.root.getNode("name").getData())) {
						sendResponse(command, "type", "ACK", "message", game.toString());
						found = true;
						break;
					}
				}
				if(!found) {
					sendResponse(command, "type", "NAK", "message", "Can not find the game " + gameName);
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
		
		return false;
	}

	private void sendResponseTo(String playerName, String command, String ... details) {
		mapPlayerNameClient.get(playerName).println(getResponse(command, details));
	}

	private void sendResponse(String command) {
		output.println(getResponse(command, (String[])null));
	}
	
	private void sendResponse(String command, String ... details) {
		output.println(getResponse(command, details));
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
	
	
	private void sendResponse(String id, String message, Loadable data) {
		output.println(SimpleXML.createTag("response", 
				SimpleXML.createTag("id", id)) + 
				SimpleXML.createTag("message", message) +
				SimpleXML.createTag("data", data.serialize())
				);
	}

	private void broadcastAndOutput(String command, String ... details) {
		try {
			clientsPermit.acquire();
			for(ServerThread client : clients) {
				client.output.println(getResponse(command, details));
			}
			clientsPermit.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
//	private boolean nameExists(String name) {
//		for(ClientHandler client : clients) {
//			if(name.equalsIgnoreCase(client.player.getName())) {
//				return true;
//			}
//		}
//		return false;
//	}

	private void broadcastResponse(String command, String ... details) {
		try {
			clientsPermit.acquire();
			for(ServerThread client : clients) {
				if(client != this) {
					client.output.println(getResponse(command, details));
				}
			}
			clientsPermit.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
