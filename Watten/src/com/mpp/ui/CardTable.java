package com.mpp.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.mpp.tools.PlayerLocation;
import com.mpp.tools.WTools;
import com.mpp.watten.WattenGame;
import com.mpp.watten.cards.Card;
import com.mpp.watten.cards.Rank;
import com.mpp.watten.cards.Suit;
import com.mpp.watten.logic.Player;

public class CardTable extends Table {

	float tableWidth;
	float tableHeight;
	float sideColumnsWidth;
	private static float cardWidth;
	private static float cardHeight;
	private float columnWidth;
	private float columnHeight;
	final float CELL_TO_CARD_FACTOR = 0.9f;
	final int COLUMNS = 7;
	final int ROWS = 5;
	final int BUTTON_ROW = 3;
	float opponentOffset;
	Cell[][] cardTableCells;
	Cell[][] playerCells;
	Cell[] localPlayerHand;
	int positionCorrection;
	boolean seatTaken[] = { false, false, false, false };
	PlayerUI[] players = new PlayerUI[4];

	/*
	 * A class that might get a UI component(stage?), which is the highest
	 * playing area. It contains the card areas, one for each of the four
	 * players.
	 */

	public CardTable(float _tableWidth, float _tableHeight) {
		super(WattenGame.getSkin());
		float maxCardWidth = _tableWidth / 6.5f;
		float maxCardHeight = _tableHeight / 4.15f;

		positionCorrection = 0;

		if (maxCardWidth / 0.556f <= maxCardHeight) {
			columnWidth = maxCardWidth;
			columnHeight = maxCardWidth / 0.556f;
		} else {
			columnWidth = maxCardHeight * 0.556f;
			columnHeight = maxCardHeight;
		}
		this.setPosition(0, 0);
		this.setSize(_tableWidth, _tableHeight);
		cardTableCells = new Cell[ROWS][COLUMNS];
		localPlayerHand = new Cell[5];
		playerCells = new Cell[4][2];
		cardWidth = columnWidth * 0.9f;
		cardHeight = columnHeight * 0.9f;
		float verticalFiller = (_tableWidth - columnWidth * 6.5f) / 6f;
		float horizontalFiller = (_tableHeight - columnHeight * 4.15f) / 6f;
		sideColumnsWidth = (_tableWidth - columnWidth * 5-verticalFiller*5) / 2;
		Table table = new Table(WattenGame.getSkin());
		table.setPosition(0, 0);
		table.setSize(_tableWidth, _tableHeight);
		this.setBackground(WTools.getTableImage().getDrawable());

		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMNS; column++) {
				if (column == 0 || column == 6) {
					cardTableCells[row][column] = table.add("").width(
							sideColumnsWidth);
				} else {
					if (row == BUTTON_ROW) {
						cardTableCells[row][column] = table.add("")
								.width(columnWidth)
								.height(columnHeight * 0.15f).center();

					} else {
						cardTableCells[row][column] = table.add("")
								.width(columnWidth).height(columnHeight);

					}
					if (column != 6)
						table.add("").width(verticalFiller);
				}

			}
			table.row();
			if (row != 4)
				table.add("").height(horizontalFiller);
			table.row();

		}
		this.add(table).fill();
		divideCardTable();

		System.out.println("cardWidth: " + cardWidth + " cardHeight: "
				+ cardHeight + " side columns: " + sideColumnsWidth);
		
	}

	public void refreshPoints(int team1, int team2) {
		Table table = new Table(WattenGame.getSkin());
		table.add("Team 1: " + team1);
		table.row();
		table.add("Team 2: " + team2);
		cardTableCells[0][0].setWidget(table);
	}

	// Add player to local table
	public synchronized void addPlayer(PlayerUI player) {

		// Local player is always south, so other players positions have to be
		// rotated for local table
		int tempPosition = player.getPlayerLocation().ordinal();
		if (player.getPlayerLocation() == PlayerLocation.South
				|| player.getPlayerLocation() == PlayerLocation.North)
			player.setTeamNumber(1);
		else {
			player.setTeamNumber(2);

		}

		if (player.isLocalPlayer())
			positionCorrection = 4 - tempPosition;

		tempPosition = (positionCorrection + tempPosition) % 4;

		PlayerLocation localTableLocation = PlayerLocation.get(tempPosition);

		System.out.println("player: " + player.getPlayerName() + " "
				+ localTableLocation);

		switch (localTableLocation) {

		case South:

			player.addToTable(playerCells[localTableLocation.ordinal()][0],
					playerCells[localTableLocation.ordinal()][1],
					localPlayerHand);
			break;
		case West:
			player.addToTable(playerCells[localTableLocation.ordinal()][0],
					playerCells[localTableLocation.ordinal()][1], null);
			break;
		case North:
			player.addToTable(playerCells[localTableLocation.ordinal()][0],
					playerCells[localTableLocation.ordinal()][1], null);
			break;
		case East:
			player.addToTable(playerCells[localTableLocation.ordinal()][0],
					playerCells[localTableLocation.ordinal()][1], null);
			break;

		}
		System.out.println("Table width: " + this	.getWidth());
		System.out.println("Table height: " + this.getHeight());
	}

	public float getTableWidth() {
		return tableWidth;
	}

	public float getTableHeight() {
		return tableHeight;
	}

	public static float getCardWidth() {
		return cardWidth;
	}

	public static float getCardHeight() {
		return cardHeight;
	}

	public float getCELL_TO_CARD_FACTOR() {
		return CELL_TO_CARD_FACTOR;
	}

	public float getColumnWidth() {
		return columnWidth;
	}

	public float getColumnHeight() {
		return columnHeight;
	}

	public void divideCardTable() {

		// South Player (local player)
		for (int i = 0; i < 5; i++)

			localPlayerHand[i] = cardTableCells[4][i + 1];

		playerCells[PlayerLocation.South.ordinal()][0] = cardTableCells[2][3];
		playerCells[PlayerLocation.South.ordinal()][1] = cardTableCells[2][4];

		// West Player
		playerCells[PlayerLocation.West.ordinal()][0] = cardTableCells[1][2];
		playerCells[PlayerLocation.West.ordinal()][1] = cardTableCells[1][1];

		// North Player
		playerCells[PlayerLocation.North.ordinal()][0] = cardTableCells[0][3];
		playerCells[PlayerLocation.North.ordinal()][1] = cardTableCells[0][4];

		// East Player
		playerCells[PlayerLocation.East.ordinal()][0] = cardTableCells[1][4];
		playerCells[PlayerLocation.East.ordinal()][1] = cardTableCells[1][5];
	}
}
