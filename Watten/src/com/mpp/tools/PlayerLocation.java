package com.mpp.tools;

public enum PlayerLocation {
	South(0), West(1), North(2), East(3);
	
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
