# Introduction #

Step-by-step state based decision possibilities, implemented as a protocol.
Assuming it is according to "Open Watten" rules without surrender/bet
(easier to debug - for first version at least:)

# Details #

1st PHASE: Game initialization
  * 4 players must sit at a table
  * 2 players per team (team #1: NORTH/SOUTH, team #2: WEST/EAST)

2nd PHASE: Game round initialization
  * Card deck gets shuffled and dealt (5 cards / player) by "current player"
  * "Current player"'s right neighbour decides Rank "Schlog"
  * "Current player" decides Suit "Forb"

3rd PHASE: Game iteration
  * "Current player" is now the player who decided this games' rank
  * Every player plays a card
  * Game logic decides how won this round
  * ...this phase gets repeated until a team won three times