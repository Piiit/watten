package com.mpp.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

public class ChatWidget extends Table {
	ScrollPane chatPane;
	WattenGame game;
	Table chatTable;
	TextField messageField;
	Button sendButton;
	String chatContent;

	public ChatWidget(WattenGame game) {
		super(game.getSkin());
		this.game = game;
		this.setSize(CardTable.getCardWidth(), CardTable.getCardHeight());
		this.setPosition(0, 0);
		widgetSetup();

	}

	/*
	 * Sets up and adds the chat interface to a table
	 */
	private void widgetSetup() {
		chatTable = new Table(game.getSkin());
		chatTable.setColor(Color.WHITE);

		chatPane = new ScrollPane(chatTable);
		chatPane.setColor(Color.WHITE);
		chatPane.setScrollingDisabled(true, false);
		chatPane.setScrollBarPositions(true, true);
		chatPane.setFadeScrollBars(true);
		chatPane.setOverscroll(false, false);

		messageField = new TextField("", game.getSkin());

		sendButton = new Button(game.getSkin());
		sendButton.add("Ok");
		sendButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (!messageField.getText().isEmpty()) {
					game.sendRequest("chat " + messageField.getText());
					messageField.setText("");
				}

			}
		});
		


		this.add(chatPane).width(CardTable.getCardWidth() * 2)
				.height(CardTable.getCardHeight() * 0.8f).top().left().fill();
		this.row();
		this.add(messageField).width(CardTable.getCardWidth() * 1.6f)
				.height(CardTable.getCardHeight() * 0.2f);
		this.add(sendButton).width(CardTable.getCardWidth() * 0.4f)
				.height(CardTable.getCardHeight() * 0.2f);

	}

	/*
	 * Appends a new message to the messageField
	 */
	public synchronized void addMessage(String text) {
		
		chatTable.row();
		Label label = new Label(text, WattenGame.getSkin());
		label.setWrap(true);
		label.setWidth(CardTable.getCardWidth() * 2);
		chatTable.add(label).width(CardTable.getCardWidth() * 2).left().top();

		chatPane.scrollToCenter(0, chatPane.getMaxY() - CardTable.getCardHeight()
				* 0.6f, CardTable.getCardWidth() * 2,
				CardTable.getCardHeight() * 0.6f);
		chatPane.updateVisualScroll();
chatPane.act(Gdx.graphics.getDeltaTime());
		
		chatPane.setScrollY(chatPane.getMaxY()+10f);
		chatPane.setScrollPercentY(100f);
		chatPane.act(Gdx.graphics.getDeltaTime());

		chatPane.updateVisualScroll();
		chatPane.act(Gdx.graphics.getDeltaTime());


	}
}
