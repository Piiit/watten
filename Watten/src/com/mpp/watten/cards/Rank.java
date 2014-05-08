package com.mpp.watten.cards;

public enum Rank {
	
	WELI(0), SEVEN(1), EIGHT(2), NINE(3), TEN(4), JACK(5), QUEEN(6), KING(7), ACE(8);
	
	private final int index;

	Rank(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public static Rank get(int index) {
		return Rank.values()[index];
	}

}
