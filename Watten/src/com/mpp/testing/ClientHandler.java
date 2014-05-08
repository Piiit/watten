package com.mpp.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import cards.Card;
import xml.Loadable;
import xml.SimpleXML;
import logic.Player;
import logic.Watten;
import logic.WattenFeature;

public class ClientHandler extends Thread {
	
	//vector should do the job, but just to be sure, I use semaphores.
	private Semaphore clientsPermit = new Semaphore(1); 
	private Semaphore gamesPermit = new Semaphore(1);
	
	//Vector is nearly deprecated(?), but works with concurrent access
	private Vector<ClientHandler> clients; 	
	
	private Vector<Watten> games;
	
	private Watten joinedGame;
	
	private Socket socket;
	private BufferedReader input = null;
	private PrintWriter output = null;
	private Player player = null;
	private final int maxClients;
	
	public ClientHandler(Socket socket, Vector<ClientHandler> clients, Vector<Watten> games, final int maxClients) {
		this.socket = socket;
		this.clients = clients;
		this.maxClients = maxClients;
		this.games = games;
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
			if(clients.size() == maxClients) {
				output.println("Sorry, the server is full (max=" + maxClients + "). You can not enter now!");
				socket.close();
				return;
			} 
			clientsPermit.acquire();
			clients.add(this);
			clientsPermit.release();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		boolean done = false;
		String line = "";
		
		player = new Player("Player" + clients.size());
		output.println("Welcome <" + player.getName() + ">! Type H to see all commands.");
		broadcast("<" + player.getName() + "> entered the lobby...");
		
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
			clientsPermit.release();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("SERVER: Client <" + player.getName() + "> @ port " + socket.getPort() + " left the room!");
		broadcast("<" + player.getName() + "> left the chat room...");
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
		line = line.trim();
		
		String regex = "\\s";
		String parts[] = line.split(regex);
		
		String cmd = parts[0].toUpperCase();
		int i = 0;
		Watten currentGame = getGame(player);
		String gameName = currentGame == null ? "" : currentGame.getName();
		
		//broadcastAndOutput(WattenFeature.GAME_FINISHED.serialize());
		
		SimpleXML xml = new SimpleXML(line);
		xml.parse();
		
		
//		if(xml.root.getName().equals("card")) {
//			Card c = new Card();
//			c.load(xml.root);
//		}
		System.out.println("SERVER: " + line);
		
		String command = xml.root.getNode("command").getData().toLowerCase();
		
		
		switch(command) {
			case "quit":
				sendResponse("ACK");
				return true;
			case "S":
				try {
					currentGame.start();
					broadcastAndOutput(gameName + " started!");
					broadcastAndOutput("Current player = " + currentGame.getTable().getCurrentPlayer());
				} catch (Exception e1) {
					e1.printStackTrace();
					output.println("Can not start game " + gameName + ": " + e1.getMessage());
				}
			break;
			case "Q": 
				output.println("*** Bye " + player.getName() + "***");
				return true;
			case "N":
				i = 0;
				gameName = "";
				for(String part: parts) {
					if(i > 0) {
						gameName += part;
					}
					i++;
				}
				try {
					for(Watten g : games) {
						if(g.getName().equalsIgnoreCase(gameName)) {
							output.println("You can not create the game " + gameName + ". A game with this name already exists!");
							return false;
						}
					}
					gamesPermit.acquire();
					games.add(new Watten(gameName));
					gamesPermit.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
				output.println("You created the game " + gameName);
			case "J":
				i = 0;
				gameName = "";
				for(String part: parts) {
					if(i > 0) {
						gameName += part;
					}
					i++;
				}

				try {
					joinedGame = null;
					for(Watten game: games) {
						if(game.getName().equalsIgnoreCase(gameName)) {
							game.getTable().addPlayer(player);
							joinedGame = game;
							output.println("You joined the game " + joinedGame);
							for(Player player : game.getTable().getPlayers()) {
								if(player != null) {
									broadcast("<" + player.getName() + "> joined your game!");
								}
							}
							break;
						}
					}
					if(joinedGame == null) {
						throw new Exception("Game does not exist!");
					}
				} catch (Exception e) {
					output.println("You can not join game " + gameName + ": " + e.getMessage());
					e.printStackTrace();
				}
			break;
			case "L":
				output.println("--- LIST OF GAMES --------------------");
				i = 0;
				for(Watten game : games) {
					i++;
					output.println(i + " > " + game.getName());
				}
			break;
			case "H":
				output.println("--- HELP -----------------------------");
				output.println("Q           : exit");
				output.println("N [gameName]: create a new game");
				output.println("J [gameName]: join a created game");
				output.println("R [name]    : New nick name");
				output.println("L           : list all games");
				output.println("C [text]    : broadcasts some chat to all players");
				output.println("S           : start the game");
				output.println("--------------------------------------");
			break;
			case "C":
				i = 0;
				String msg = "";
				for(String part: parts) {
					if(i > 0) {
						msg += part;
					}
					i++;
				}
				output.println("<-YOU-> " + msg);
				broadcast("<" + player.getName() + "> " + msg);
            break;
			case "R":
				i = 0;
				String newName = "";
				for(String part: parts) {
					if(i > 0) {
						newName += part;
					}
					i++;
				}
				if (newName.length() == 0) {
					output.println("This name is too short!");
				} else if (nameExists(newName)) {
					output.println("This name exists already. Please choose another one!");
				} else {
					broadcast(player.getName() + " changed to " + newName);
					output.println("Your new name is " + newName);
					player.setName(newName);
				}
				
			break;
			default:
				output.println("Please enter a valid command. Type H to see all commands!");
		}
		
		return false;
	}
	

	private void sendResponse(String id) {
		output.println(SimpleXML.createTag("response", SimpleXML.createTag("id", id)));
	}
	
	private void sendResponse(String id, String message) {
		output.println(SimpleXML.createTag("response", 
				SimpleXML.createTag("id", id)) + 
				SimpleXML.createTag("message", message)
				);
	}
	
	private void sendResponse(String id, String message, Loadable data) {
		output.println(SimpleXML.createTag("response", 
				SimpleXML.createTag("id", id)) + 
				SimpleXML.createTag("message", message) +
				SimpleXML.createTag("data", data.serialize())
				);
	}

	private void broadcast(String text) {
		for(ClientHandler client : clients) {
			if(client != this) {
				client.output.println(text);
			}
		}
	}
	
	private void broadcastAndOutput(String text) {
		for(ClientHandler client : clients) {
			client.output.println(text);
		}
	}

	
	private boolean nameExists(String name) {
		for(ClientHandler client : clients) {
			if(name.equalsIgnoreCase(client.player.getName())) {
				return true;
			}
		}
		return false;
	}

}
