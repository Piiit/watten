package logic;

public enum WattenFeature {
	INIT(0), SELECT_RANK(1), SELECT_SUIT(2), 
	PLAY_CARD(3), BET (4), TURN_FINISHED(5), 
	ROUND_FINISHED(6), GAME_FINISHED(7), SELECT_MAXPOINTS(8), 
	SELECT_TEAM(9), ROUND_START(10), TURN_START(11), 
	GAME_START(12), SURRENDER_OR_HOLD(13);
	
	private final int index;

	WattenFeature(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
	
	public static WattenFeature get(int index) {
		return WattenFeature.values()[index];
	}
	
}