package com.mpp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.mpp.tools.xml.SimpleXML;
import com.mpp.ui.message.MessageDialog;
import com.mpp.ui.message.MessageType;
import com.mpp.watten.WattenGame;

public class ClientSender {

	private static PrintWriter output = null;
	private static BufferedReader input = null;
	private static Socket socket = null;
	private static final int PORT = 9999;
	private static final String ADDRESS = "192.168.178.22";
	String userRequest;
	WattenGame game;

	public void startClientSenderThread(WattenGame game) {
		this.game = game;
		try {
			socket = new Socket(ADDRESS, PORT);
			try {
				output = new PrintWriter(socket.getOutputStream(), true);
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
			} catch (IOException e) {
				MessageDialog.createErrorDialog(e.getMessage());
			}

			// Some error-handling needed
			output.println(game.getLocalPlayerName());

			// Needed so that player-names are unique
			boolean playerAccepted = false;
			while (!playerAccepted && !socket.isClosed()) {
				String answer = input.readLine();
				if (answer.equals("NACK")) {
					MessageDialog.createErrorDialog(input.readLine());
					socket.close();
				} else if (answer.equals("ACK")) {
					game.toMainMenu();
					ClientReceiver clientOut = new ClientReceiver(socket, game);
					clientOut.start();
					playerAccepted = true;
				}
			}

			userRequest = "";
			while (!socket.isClosed()) {

				if (userRequest != "") {

					String regex = "\\s";
					String parts[] = userRequest.split(regex);

					String cmd = parts[0];
					String gameName = "";

					switch (cmd) {
					case "quit":
						sendRequest("quit");
						break;
					case "create_game":
						gameName = "";
						if (parts.length > 1) {
							gameName = parts[1];
						}
						sendRequest("create_game", "name", gameName);
						break;
					case "join_game":
						gameName = "";
						if (parts.length > 1) {
							gameName = parts[1];
						}
						sendRequest("join_game", "name", gameName);
						break;
					case "start_game":
						sendRequest("start_game");
						break;
					case "help":
						sendRequest("help");
						break;
					case "list_games":
						sendRequest("list_games");
						break;

					case "other_players":
						sendRequest("other_players", "name", gameName);

						break;
					case "chat":
						String msg = "";
						if (parts.length > 1) {
							for (int i = 1; i < parts.length; i++)
								msg += parts[i];
						}
						sendRequest("chat", "message", msg);
						break;
					case "info":
						gameName = "";
						if (parts.length > 1) {
							gameName = parts[1];
						}
						sendRequest("info", "name", gameName);
						break;
					case "rank_selected":
						sendRequest("rank_selected", "card_index", parts[1]);
						break;
					case "suit_selected":
						sendRequest("suit_selected", "card_index", parts[1]);
						break;
					case "card_played":
						sendRequest("card_played", "card_index", parts[1]);
						break;

					}

					System.out.println("CLIENT: " + userRequest);
					userRequest = "";
				}

			}

		} catch (IOException e) {
			MessageDialog.createErrorDialog(e.getMessage());
		} finally {
			try {
				System.out.println("Closing connection.");
				output.close();
				input.close();
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException e) {
				MessageDialog.createErrorDialog(e.getMessage());
			}
		}
	}

	private void sendRequest(String command) {
		if (socket == null || !socket.isConnected())
			MessageDialog.createErrorDialog("Not connected to server!");
		else
			output.println(SimpleXML.createTag("request",
					SimpleXML.createTag("command", command)));
	}

	// private void sendRequest(String command, String message, Loadable data) {
	// output.println(SimpleXML.createTag("request",
	// SimpleXML.createTag("command", command)) +
	// SimpleXML.createTag("message", message) +
	// SimpleXML.createTag("data", data.serialize())
	// );
	// }

	private synchronized void sendRequest(String command, String... details) {
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
		output.println(SimpleXML.createTag("request",
				SimpleXML.createTag("command", command) + out));
	}

	public synchronized void prepareRequest(String requestLine) {
		userRequest = requestLine;
	}

}
