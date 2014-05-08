package com.mpp.tools;

public enum PlayerLocation {
	North(0), East(1), South(2), West(3);
	
	private final int index;

	PlayerLocation(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public static PlayerLocation get(int index) {
		return PlayerLocation.values()[index];
	}
}
