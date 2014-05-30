package com.mpp.ui.message;

import com.mpp.tools.PlayerLocation;

public enum MessageType {
	ERROR(0), MESSAGE(1), CARD_SELECTED(2);
	private final int index;

	MessageType(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public static MessageType get(int index) {
		return MessageType.values()[index];
	}
}
