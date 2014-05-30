package com.mpp.watten.cards;

public enum Suit {
	BELLS(0), HEARTS(1), ACORNS(2), LEAVES(3);

	private final int index;

	Suit(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public static Suit get(int index) {
		return Suit.values()[index];
	}
}
