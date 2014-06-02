package com.mpp.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.mpp.tools.WTools;
import com.mpp.ui.WattenGame;
import com.mpp.ui.message.MessageDialog;

public class InputPlayerNameScreen extends WattenScreen {

	Button confirmNameButton;
	TextField nameField;
	Label nameLabel;

	public InputPlayerNameScreen(WattenGame _game) {

		this.game = _game;
		skin = WattenGame.getSkin();

		// Sets up the layoutTable and all the UI elements it will contain
		layoutTable = new Table(skin);
		layoutTable.setPosition(0, 0);
		layoutTable.setSize(game.getScreenWidth(), game.getScreenHeight());
		layoutTable.setBackground(WTools.getTableImage().getDrawable());

		nameLabel = new Label("Enter name:", skin);
		nameLabel.setAlignment(Align.center);

		nameField = new TextField("", skin);

		confirmNameButton = new Button(skin);
		confirmNameButton.add("Continue");

		confirmNameButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				return true;
			}

			public void touchUp(InputEvent event, float x, float y,

			int pointer, int button) {
				if (nameField.getText().equals(""))
					MessageDialog.createErrorDialog("No name entered!");
				else {
					game.createLocalPlayer(nameField.getText());
					game.startClientNetworkingThread();
				}
			}
		});
		// Adds elements to layoutTable and the table to the stage
		layoutTable.add(nameLabel).width(300f).height(50f);
		layoutTable.row();
		layoutTable.add(nameField).width(300f).height(50f);
		layoutTable.row();
		layoutTable.add(confirmNameButton).width(300f).height(50f);
//		try {
//			layoutTable.add(new Card2D(new Card(Suit.ACORNS, Rank.ACE),new PlayerUI(new Player("me"))));
//			layoutTable.add(new Card2D(new Card(Suit.BELLS, Rank.WELI),new PlayerUI(new Player("me"))));
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		stage = new Stage();
		System.out.println("Table width: " + layoutTable.getWidth());
		System.out.println("Table height: " + layoutTable.getHeight());
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		super.show();
		stage.addActor(layoutTable);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		stage.clear();
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
