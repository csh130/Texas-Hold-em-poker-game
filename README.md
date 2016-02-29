#Implementation for the Expectiminimax algorithm 
Apply it to play in the final two betting rounds of a Texas Hold’em poker game. Your program generates multiple hands of play, either with all details, or in summary form. Associated with experimental analysis of results obtained by running the algorithm under various circumstances.

There are two types of AI player, one is rational and the other one is non rational player. 

#Rules
When a player chooses the “non-rational” mode, only Player 1 will use the Ex- pectiminimax algorithm. Although Player 1 will assume that Player 2 is also going to act rationally, Player 2 will in fact use a fixed strategy:• On each round of betting, if Player 2 has a hand that is worse than two pair, they will always Check if they are the first to bet, or if Player 1 has already checked. If Player 1 bets, Player 2 will Call with any single pair, and Fold all other hands.• On each round of betting, if Player 2 has a hand that is two pair or better, they will always Bet the minimum amount if they are first to act, or if Player 1 has checked. If Player 1 bets or raises, Player 2 will always Call.

#Output 
It has verbose mode and non-verbose mode, which has details for each hand and the other one only has the outcome of each game played, in terms of game number, and result (positive or negative for each player).

#Experimental result
Including graph analyzation and text.

#Running
compile the file first
Use command to run the program directly

e.g 	java Play 3 r v
e.g 	java Play 3 n