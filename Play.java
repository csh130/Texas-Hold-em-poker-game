import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Project for CS546, Literature game, This class is the system class
 * 
 * @author Sihan Cheng
 */
public class Play {
	// curPlayer represents the current player
	public static Player p1;
	public static Player p2;
	public int total_hands;
	public int cur_hands;

	public Player foldPlayer;
	public static int p1_totalWinMoney;
	public static int p2_totalWinMoney;
	public static boolean rational;
	public static boolean verbose; // it is optional, default set to false
									// (means
	// summary results)
	public static ArrayList<Card> cards_onTable;
	private static ArrayList<Card> cardList;
	public static int maxDepth = 0;
	public static Node parent;

	public static int p1_bet;
	public static int p2_bet;
	/**********************************************/
	// All the actions that player may do
	final static String CALL = "CALL";
	final static String BET = "BET";
	final static String BET_ALLIN = "BET_ALLIN";
	final static String FOLD = "FOLD";
	final static String CHECK = "CHECK";
	final static String RAISE_DOUBLE = "RAISE_DOUBLE";
	final static String RAISE_ALLIN = "RAISE_ALLIN";
	/**********************************************/
	public static List<Node> leafNode = new ArrayList<Node>();
	public int maxPlayer = 1; // 1 means player1 , 0 means player2 is max player
	public static Card fifthCard;

	public int tree_buildTimes = 1;

	public static void main(String[] args) {
		rational = false;
		verbose = false;
		if (args.length >= 2) {
			if (args[1].equals("r"))
				rational = true;
			else if (args[1].equals("n"))
				rational = false;
			if (args.length == 3) {
				if (args[2].equals("v"))
					verbose = true;
			}
		} else {
			System.out.println("Invalid input, program exits!");
			System.exit(0);
		}
		Play p = new Play(Integer.parseInt(args[0]), rational, verbose);
		p.initialCards();
		p.startGame();
		System.exit(0);
	}

	/**
	 * Constructor, inital hands, rational, verbose, cardList, and creat two
	 * players
	 * 
	 * @param p1
	 *            player 1
	 * @param player
	 *            2
	 * */
	public Play(int hands, boolean rational, boolean verbose) {
		this.total_hands = hands;
		this.cur_hands = 1;
		this.rational = rational;
		this.verbose = verbose;
		this.parent = null;
		this.p1_totalWinMoney = 0;
		this.p2_totalWinMoney = 0;
		cardList = new ArrayList<Card>();
		cards_onTable = new ArrayList<Card>();
		p1 = new Player("p1");
		p2 = new Player("p2");
	}

	/**
	 * This method is to generate one pack of cards and use shuffle method to
	 * make them in random order
	 * 
	 * @param value
	 *            the attribute of the Card class,represents the value of the
	 *            card
	 * @param suit
	 *            the suite of the card, one pack of cards have 4 suits.
	 * @param cardList
	 *            cardList is a List of card objects which is used to recieve
	 *            the shuffled pack of cards
	 */
	public void initialCards() {
		final String[] suit = { "C", "H", "S", "D" };
		// 11 J,12 Q, 13K,14A
		final String[] value = { "2", "3", "4", "5", "6", "7", "8", "9", "10",
				"J", "Q", "K", "A" };
		// List random = new ArrayList();
		for (int i = 0; i < suit.length; i++) {
			for (int j = 0; j < value.length; j++) {
				// Create a new card object which has two attributes
				Card c = new Card(suit[i], value[j]);
				cardList.add(c);
			}
		}
		// Shuffle the card
		Collections.shuffle(cardList);
		// create 2 set of cards, each has 2cards for the 2 player, and remove
		// them from the cardlist
		ArrayList<Card> c1 = new ArrayList<Card>();
		c1.add(cardList.remove(0));
		c1.add(cardList.remove(0));
		ArrayList<Card> c2 = new ArrayList<Card>();
		c2.add(cardList.remove(0));
		c2.add(cardList.remove(0));
		p1.setCards(c1);
		p2.setCards(c2);
		// initial the cards on the table, total 4 cards
		cards_onTable = new ArrayList<Card>();
		cards_onTable.add(cardList.remove(0));
		cards_onTable.add(cardList.remove(0));
		cards_onTable.add(cardList.remove(0));
		cards_onTable.add(cardList.remove(0));
		fifthCard = cardList.get(0);

	}

	/**
	 * Taking previous moves of players as input and find the next best move in
	 * the game tree
	 * 
	 * */
	public String takeBestMove(int minmax, List<String> preAction) {
		// if there is no move.
		if (preAction.isEmpty()) {
			return parent.bestAction;
		}
		getCurrentMoveNode(minmax, parent, 0, preAction);
		return bestMove;
	}

	public static String bestMove = "FOLD";

	public static void getCurrentMoveNode(int minmax, Node parent, int i,
			List<String> preAction) {
		for (Node n : parent.getChildList()) {
			if (n.action.equals(preAction.get(i))) {
				if (i == (preAction.size() - 1)) {
					if (minmax == 1)
						bestMove = n.bestAction;
					else {
						bestMove = n.minP_bestAction;
					}
					return;
				}
				i++;
				getCurrentMoveNode(minmax, n, i, preAction);
				break;
			}
		}
	}

	/**
	 * This method is to check whether a player has accomplished a suite
	 * */
	public ArrayList<String> getAvailableAction(int player, String pre_moves,
			Node n) {
		ArrayList<String> moves = new ArrayList<String>();
		int p1_capital = 1500 - n.p1_bet;
		int p2_capital = 1500 - n.p2_bet;
		int p1_b = n.p1_bet;
		int p2_b = n.p2_bet;

		// if current hand is the player 1 to act first
		if (cur_hands % 2 == 1) {
			if (player == 1) {
				if (pre_moves.equals("")) {
					moves.add(CHECK);
					moves.add(BET);
					moves.add(BET_ALLIN);
				} else {
					if (pre_moves.equals(RAISE_DOUBLE)) {
						if (p1_capital >= p2_b)
							moves.add(CALL);
						if (p1_capital >= 2 * p2_b) {
							moves.add(RAISE_DOUBLE);
						}
						moves.add(RAISE_ALLIN);
						moves.add(FOLD);

					} else if (pre_moves.equals(RAISE_ALLIN)) {
						// if both player all in, return null .this round is
						// over
						// if (p1_capital == 0 && p2.getCapital() == 0)
						// return null;
						if (p1_capital > 0)
							moves.add(CALL);
						moves.add(FOLD);
					} else if (pre_moves.equals(BET)) {
						if (p1_capital >= p2_b) {
							moves.add(CALL);
							if (p1_capital >= 2 * p2_b)
								moves.add(RAISE_DOUBLE);
						}
						moves.add(RAISE_ALLIN);
						moves.add(FOLD);
					} else if (pre_moves.equals(BET_ALLIN)) {
						moves.add(FOLD);
						if (p1_capital > 0)
							moves.add(CALL);
					}
				}
			}
			// in the first round, player 2's option
			else {

				if (pre_moves.equals(CHECK)) {
					moves.add(CHECK);
					moves.add(BET);
					moves.add(BET_ALLIN);
				} else if (pre_moves.equals(BET)) {
					// if player2's capital is greater than the amount
					// player1 has bet
					if (p2_capital >= p1_b) {
						moves.add(CALL);
						if (p2_capital >= 2 * p1_b)
							moves.add(RAISE_DOUBLE);
					}
					moves.add(RAISE_ALLIN);
					moves.add(FOLD);
					// if current capital is less than the player's bet
				} else if (pre_moves.equals(BET_ALLIN)) {
					if (p2_capital > 0)
						moves.add(CALL);
					moves.add(FOLD);
				} else if (pre_moves.equals(RAISE_DOUBLE)) {
					if (p2_capital >= p1_b) {
						moves.add(CALL);
						if (p2_capital >= 2 * p1_b)
							moves.add(RAISE_DOUBLE);
					}
					moves.add(RAISE_ALLIN);
					moves.add(FOLD);

				} else if (pre_moves.equals(RAISE_ALLIN)) {
					if (p2_capital > 0)
						moves.add(CALL);
					moves.add(FOLD);
				}
			}
		}
		// if it is player2's trurn to act
		else {
			if (player == 1) {
				if (pre_moves.equals("")) {
					moves.add(CHECK);
					moves.add(BET);
					moves.add(BET_ALLIN);
				} else {
					if (pre_moves.equals(RAISE_DOUBLE)) {
						if (p2_capital >= p1_b)
							moves.add(CALL);
						if (p2_capital >= 2 * p1_b) {
							moves.add(RAISE_DOUBLE);
						}
						moves.add(RAISE_ALLIN);
						moves.add(FOLD);

					} else if (pre_moves.equals(RAISE_ALLIN)) {
						// if both player all in, return null .this round is
						// over
						// if (p1_capital == 0 && p2.getCapital() == 0)
						// return null;
						if (p2_capital > 0)
							moves.add(CALL);
						moves.add(FOLD);
					} else if (pre_moves.equals(BET)) {
						if (p2_capital >= p1_b) {
							moves.add(CALL);
							if (p2_capital >= 2 * p1_b)
								moves.add(RAISE_DOUBLE);
						}
						moves.add(RAISE_ALLIN);
						moves.add(FOLD);
					} else if (pre_moves.equals(BET_ALLIN)) {
						moves.add(FOLD);
						if (p2_capital > 0)
							moves.add(CALL);
					}
				}
			}
			// in the first round, player 1's option
			else {

				if (pre_moves.equals(CHECK)) {
					moves.add(CHECK);
					moves.add(BET);
					moves.add(BET_ALLIN);
				} else if (pre_moves.equals(BET)) {
					// if player2's capital is greater than the amount
					// player1 has bet
					if (p1_capital >= p2_b) {
						moves.add(CALL);
						if (p1_capital >= 2 * p2_b)
							moves.add(RAISE_DOUBLE);
					}
					moves.add(RAISE_ALLIN);
					moves.add(FOLD);
					// if current capital is less than the player's bet
				} else if (pre_moves.equals(BET_ALLIN)) {
					if (p1_capital > 0)
						moves.add(CALL);
					moves.add(FOLD);
				} else if (pre_moves.equals(RAISE_DOUBLE)) {
					if (p1_capital >= p2_b) {
						moves.add(CALL);
						if (p1_capital >= 2 * p2_b)
							moves.add(RAISE_DOUBLE);
					}
					moves.add(RAISE_ALLIN);
					moves.add(FOLD);

				} else if (pre_moves.equals(RAISE_ALLIN)) {
					if (p1_capital > 0)
						moves.add(CALL);
					moves.add(FOLD);
				}

			}
		}
		return moves;
	}

	// parent represents previous move, level is equal to 1 menas player 1, 0
	// means player 2

	/**
	 * Build the game tree every hand
	 * 
	 * @param parent
	 *            the node that needs to be add children to
	 * @param parentLevel
	 *            1 or 0, means different level
	 * @param preAction
	 *            the node name
	 * */
	public void buildGameTree(Node parent, int parentLevel, String preAction,
			boolean secondTree) {
		int level = 0;
		if (parentLevel == 0)
			level = 1;
		int depth = parent.depth + 1;
		maxDepth = Math.max(depth, maxDepth);
		// if it is player1' turn to act fist
		ArrayList<String> avai_moves = new ArrayList<String>();
		avai_moves.addAll(getAvailableAction(parentLevel, preAction, parent));
		for (String move : avai_moves) {
			Node n = new Node(move, parent, level, depth);
			n.fifthCard = parent.fifthCard;
			if (secondTree)
				n.secondTree = true;
			parent.addChildNode(n);
			setMoneyForNode(move, parentLevel, n);
			// if anyone fold，game over
			if (move.equals(FOLD)) {
				// player 1 act first
				if (maxPlayer == 1) {
					if (level == 0) {
						// player 1 fold
						n.score = -n.p1_bet;
					} else {
						n.score = n.p2_bet + 500;
					}
				} else {
					if (level == 0) {
						// player 1 fold
						n.score = -n.p2_bet;
					} else {
						n.score = n.p1_bet + 500;
					}

				}
				leafNode.add(n);
				continue;
			}

			// both player has no money left ,game over
			if (n.p1_bet == 1500 && n.p2_bet == 1500 && !n.secondTree) {
				addChanceNode(n, maxPlayer);
				continue;

			} else if (n.p1_bet == 1500 && n.p2_bet == 1500 && n.secondTree) {
				// game over
				generateScoreForLeafNode(n, maxPlayer);
				leafNode.add(n);
				continue;
			}

			// if there are 2 checks appear, then add another tree
			if ((move.equals(CHECK) && n.parent.action.equals(CHECK)
					&& !n.secondTree && !parent.secondTree)) {
				addSecondTree(n, maxPlayer);
			}
			// game over if 4 checks appear
			else if ((move.equals(CHECK) && n.parent.action.equals(CHECK)
					&& n.secondTree && parent.secondTree)) {
				// game over, calculate the money
				generateScoreForLeafNode(n, maxPlayer);
				leafNode.add(n);
				continue;
			}

			if (move.equals(CALL) && !n.secondTree) {
				// System.out.println("Build tree:" + n);
				addSecondTree(n, maxPlayer);
				// buildGameTree(n, 1, "", true);
			} else if (move.equals(CALL) && n.secondTree) {
				// game over, calculate the money
				generateScoreForLeafNode(n, maxPlayer);
				leafNode.add(n);
				continue;
			}
			preAction = move;
			buildGameTree(n, level, preAction, n.secondTree);
		}

	}

	public void calculateScore(Node n, int maxPlayer) {
		Player player;
		Player opponent;

		int loseMoney = 0;
		int winsMoney = 0;
		int drawMoney = (n.p1_bet + n.p2_bet + 500) / 2;

		double result = 0;
		if (maxPlayer == 1) {
			player = p1;
			opponent = p2;
			loseMoney = -n.p1_bet;
			winsMoney = 500 + n.p2_bet;

		} else {
			player = p2;
			opponent = p1;
			loseMoney = -n.p2_bet;
			winsMoney = 500 + n.p1_bet;
		}
		Card fifth_Card = n.fifthCard;
		// set 7 cards in total for the current player
		List<Card> maxPlayer_cards = new ArrayList<Card>();
		maxPlayer_cards.addAll(player.getCards());
		maxPlayer_cards.addAll(cards_onTable);
		maxPlayer_cards.add(fifth_Card);
		RankingUtil.checkRanking(player, maxPlayer_cards);
		List<Card> curr_ranking = player.rankingList;
		RankingEnum maxPlayer_rankType = player.rankingEnum;

		List<Card> minPlayer_cards = new ArrayList<Card>();
		minPlayer_cards.addAll(cards_onTable); // 4 cards
		minPlayer_cards.add(fifth_Card); // 1 card
		minPlayer_cards.addAll(opponent.getCards()); // 1 card
		RankingUtil.checkRanking(opponent, minPlayer_cards);
		List<Card> oppo_ranking = opponent.rankingList;
		RankingEnum oppo_rankType = opponent.rankingEnum;

		if (maxPlayer_rankType.ordinal() > oppo_rankType.ordinal()) {
			// player wins;
			result = 1;

		} else if (maxPlayer_rankType.ordinal() < oppo_rankType.ordinal()) {
			// opponent wins
			result = -1;
		} else {
			// if both player has the same rank, then compare
			// the high card
			// if the ranking is the same, and sequence is the
			// same,
			// then get the highest card in the ranking list
			Card curr_card = player.highCard;
			Card oppo_card = opponent.highCard;
			int m = checkHighCard(curr_card, oppo_card);
			int result2 = compareRestCards(maxPlayer_cards, curr_ranking,
					minPlayer_cards, oppo_ranking);
			if (result2 > 0) {
				result = 1;
			} else if (result2 < 0) {
				result = -1;
			} else {
				result = 0.5;
			}
		}
		// end
		minPlayer_cards.clear(); // clear min player's temp
		if (result == 1)
			n.score = winsMoney;
		else if (result == -1)// cards
			n.score = loseMoney;
		else
			n.score = drawMoney;
	}

	/**
	 * Add second tree to the leaf node of those which ends the first round of
	 * beting, and the game is not over yet
	 * 
	 * @param parent
	 *            the leaf node that build second tree on
	 * */
	public void addSecondTree(Node parent, int player) {
		int depth = parent.depth + 1;
		maxDepth = Math.max(depth, maxDepth);
		// if current player is player 1, add player2's 2 cards in the card list
		List<Card> temp_cardsList = new ArrayList<Card>();
		temp_cardsList.addAll(cardList);
		if (player == 1)
			temp_cardsList.addAll(p2.getCards());
		else
			temp_cardsList.addAll(p1.getCards());
		for (int i = 0; i < temp_cardsList.size(); i++) {
			Card fifth_Card = temp_cardsList.get(i);
			Node cardNode = new Node(fifth_Card.getSuit()
					+ fifth_Card.getRank(), parent, 3, depth);
			// 设置牌节点 的fifthcard 为 第五张牌
			cardNode.fifthCard = fifth_Card;
			parent.addChildNode(cardNode);
			cardNode.p1_bet = parent.p1_bet;
			cardNode.p2_bet = parent.p2_bet;
			buildGameTree(cardNode, 1, "", true);
		}
	}

	/**
	 * Add 46 possible cards of the fifth card on the table as node into the
	 * leaf node
	 * 
	 * @param parent
	 *            the node that build chance node on
	 * @param maxPlayer
	 *            the current max player, 1 means plyaer1, other means player2
	 * */

	public void addChanceNode(Node parent, int maxPlayer) {
		Player player;
		Player opponent;

		int depth = parent.depth + 1;
		maxDepth = Math.max(depth, maxDepth);

		double result = 0;
		if (maxPlayer == 1) {
			player = p1;
			opponent = p2;
		} else {
			player = p2;
			opponent = p1;
		}
		double chanceNodeScore = 0;

		// build the 46 cards
		List<Card> temp_cardsList = new ArrayList<Card>();
		temp_cardsList.addAll(cardList);
		temp_cardsList.addAll(opponent.getCards());

		for (int i = 0; i < temp_cardsList.size(); i++) {
			Card fifth_Card = temp_cardsList.get(i);
			Node cardNode = new Node(fifth_Card.getSuit()
					+ fifth_Card.getRank(), parent, 3, depth);
			cardNode.fifthCard = fifth_Card;
			parent.addChildNode(cardNode);
			cardNode.p1_bet = parent.p1_bet;
			cardNode.p2_bet = parent.p2_bet;
			double totalMoney = 0;
			// for every possible fifth card, calculate its score
			generateScoreForLeafNode(cardNode, maxPlayer);
			chanceNodeScore += cardNode.score;
		}
		// set parent node's score , the total score of 46* 45* 22 nodes /
		// 46*45*22
		parent.score = chanceNodeScore / 46;
	}

	/**
	 * Calculate the score for every leaf node ,consider opponent's 2 cards,
	 * total 45*22 possible hands
	 * 
	 * @param n
	 *            the node that needs to be calculate the score
	 * @param maxPlayer
	 *            the current maxPlayer, 1 reprents player1, 0 represents
	 *            player2
	 * **/
	public void generateScoreForLeafNode(Node n, int maxPlayer) {
		Player player;
		Player opponent;
		double loseMoney = 0;
		double winsMoney = 0;
		double drawMoney = (n.p1_bet + n.p2_bet + 500) / 2;

		double result = 0;
		if (maxPlayer == 1) {
			player = p1;
			opponent = p2;
			loseMoney = -n.p1_bet;
			winsMoney = 500 + n.p2_bet;

		} else {
			player = p2;
			opponent = p1;
			loseMoney = -n.p2_bet;
			winsMoney = 500 + n.p1_bet;
		}
		double parentScore = 0;

		Card fifth_Card = n.fifthCard;

		// set 7 cards in total for the current player
		List<Card> maxPlayer_cards = new ArrayList<Card>();
		maxPlayer_cards.addAll(player.getCards());
		maxPlayer_cards.addAll(cards_onTable);
		maxPlayer_cards.add(fifth_Card);

		// check the rank of cards on hand
		RankingUtil.checkRanking(player, maxPlayer_cards);
		List<Card> curr_ranking = player.rankingList;
		RankingEnum maxPlayer_rankType = player.rankingEnum;

		List<Card> temp_cardList = new ArrayList<Card>();
		temp_cardList.addAll(cardList);
		temp_cardList.addAll(opponent.getCards());
		temp_cardList.remove(fifth_Card);
		// 45*22 possible hands of opponent's card
		List<Card> minPlayer_cards = new ArrayList<Card>();
		double totalMoney = 0;
		for (int j = 0; j < temp_cardList.size() - 1; j++) {
			Card first = temp_cardList.get(j);
			for (int k = j + 1; k < temp_cardList.size(); k++) {
				// let the opponent's card not equal to the fifth card
				Card second = temp_cardList.get(k);

				minPlayer_cards.addAll(cards_onTable); // 4 cards
				minPlayer_cards.add(fifth_Card); // 1 card
				minPlayer_cards.add(first); // 1 card
				minPlayer_cards.add(second); // 1 card

				RankingUtil.checkRanking(opponent, minPlayer_cards);
				List<Card> oppo_ranking = opponent.rankingList;
				RankingEnum oppo_rankType = opponent.rankingEnum;

				result = checkRanking(curr_ranking, maxPlayer_rankType,
						oppo_ranking, oppo_rankType, maxPlayer_cards,
						minPlayer_cards);
				minPlayer_cards.clear();
				if (result == 1)
					totalMoney += (winsMoney / (45 * 22));
				else if (result == -1)// cards
					totalMoney += (loseMoney / (45 * 22));
				else
					totalMoney += (drawMoney / (45 * 22));
			}
		} // end the fifthcard loop
		n.score = totalMoney;
	}

	public int checkRanking(List<Card> curr_ranking,
			RankingEnum curr_rankingEnum, List<Card> oppo_ranking,
			RankingEnum oppo_rankingEnum, List<Card> curr_cards,
			List<Card> oppo_cards) {
		int coefficient = 0;

		if (curr_rankingEnum.ordinal() > oppo_rankingEnum.ordinal()) {
			coefficient = 1;
		} else if (curr_rankingEnum.ordinal() < oppo_rankingEnum.ordinal()) {
			// set minus money to node
			coefficient = -1;
		} else {
			int n = checkHighSequence(curr_ranking, oppo_ranking);
			if (n > 0) {
				coefficient = 1;
			} else if (n < 0) {
				coefficient = -1;
			} else {
				// if the ranking is the same, and sequence is the same,
				// then get the highest card in the ranking list

				Collections.sort(curr_ranking);
				Collections.sort(oppo_ranking);

				Card curr_card = curr_ranking.get(curr_ranking.size() - 1);
				Card oppo_card = oppo_ranking.get(oppo_ranking.size() - 1);

				int m = checkHighCard(curr_card, oppo_card);

				if (m > 0) {
					coefficient = 1;
				} else if (m < 0) {
					coefficient = -1;
				} else {
					int result = compareRestCards(curr_cards, curr_ranking,
							oppo_cards, oppo_ranking);
					if (result > 0) {
						coefficient = 1;
					} else if (result < 0) {
						coefficient = -1;
					} else {
						coefficient = 0;
					}
				}
			}
		}
		return coefficient;
	}

	/**
	 * Set min max value for all the nodes
	 **/
	public void setMinMaxValueForNode() {
		for (int i = maxDepth - 1; i >= 0; i--) {
			List<Node> nodes = new ArrayList<Node>();
			getNodesByDepth(i, parent, nodes);
			for (Node n : nodes) {
				n.score = getMinMax(n);
			}
		}
	}

	/**
	 * get min max value for a parent node, find all it's children node, if it
	 * doesn't have children node,return its own score
	 * 
	 * @param parent
	 *            the node needs to be set scrore
	 * */
	public double getMinMax(Node parent) {
		double result = 0;
		double minP_result = 0;
		double minPlayer_result;
		if (parent.getChildList().isEmpty()) {
			return parent.score;
		}
		if (parent.level == 1) {
			// get the max value of next node
			if (parent.getChildList().get(0).level == 3)
				// if this node is the parent of card node
				result = 0;
			else {
				result = Integer.MIN_VALUE;
				minPlayer_result = Integer.MAX_VALUE;
			}

		} else if (parent.level == 0) {

			if (parent.getChildList().get(0).level == 3)
				result = 0;
			else {
				// we need to get min value
				result = Integer.MAX_VALUE;
				minPlayer_result = Integer.MIN_VALUE;
			}
		} else if (parent.level == 3) {
			// if this node is the card node
			if (parent.parent.level == 1) {
				// we need to get max value
				result = Integer.MIN_VALUE;
				minPlayer_result = Integer.MAX_VALUE;
			} else if (parent.parent.level == 0) {
				// we need to get min value
				result = Integer.MAX_VALUE;
				minPlayer_result = Integer.MIN_VALUE;
			}
		}

		// if this node is not the leaf node
		for (Node node : parent.getChildList()) {
			if (node.level == 3) {
				// if the parent's children are card node, then get the average
				// score for all the card node
				result += node.score / 46;
				// System.out.println("=== card node set score:" + node);
			} else {
				if (parent.level == 1) {
					// get max value of all children score
					if (node.score > result) {
						result = node.score;
						parent.bestAction = node.action;
					}
					if (node.score < result) {
						parent.minP_bestAction = node.action;
					}
				} else if (parent.level == 0) {
					// get min value of all children score
					if (node.score < result) {
						result = node.score;
						parent.bestAction = node.action;
					}
					if (node.score > result) {
						parent.minP_bestAction = node.action;
					}
				} else if (parent.level == 3) {
					// if it is the card node
					if (parent.parent.level == 1) {
						if (node.score > result) {
							result = node.score;
							parent.bestAction = node.action;
						}
						if (node.score < result) {
							parent.minP_bestAction = node.action;
						}
					} else {
						if (node.score < result) {
							result = node.score;
							parent.bestAction = node.action;
						}
						if (node.score > result) {
							parent.minP_bestAction = node.action;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * get all the nodes by its depth
	 * 
	 * @param depth
	 *            the depth that need to find all the nodes
	 * @param the
	 *            ancestor node
	 * @param the
	 *            nodes list whose depth is we are looking for
	 * */
	private static void getNodesByDepth(int depth, Node par, List<Node> nodes) {

		if (depth == 0) {
			nodes.add(parent);
		}
		for (Node n : par.getChildList()) {
			if (n.depth == depth) {
				nodes.add(n);
			}
			if (!n.getChildList().isEmpty()) {
				getNodesByDepth(depth, n, nodes);
			} else
				continue;

		}
	}

	public int compareRestCards(List<Card> onHand, List<Card> curr_ranking,
			List<Card> oppoHand, List<Card> oppo_ranking) {
		List<Card> curr = new ArrayList<Card>();
		curr.addAll(onHand);
		curr.removeAll(curr_ranking);

		List<Card> oppo = new ArrayList<Card>();
		oppo.addAll(oppoHand);
		oppo.removeAll(oppo_ranking);

		int result = checkHighSequence(curr, oppo);

		return result;
	}

	/**
	 * check the highest sequence
	 * 
	 * @param curr
	 * @param oppo
	 * @return bigger than 0 means current player has the highest sequence,
	 *         equals to 0 means current player has the same highest sequence
	 *         with opponent, smaller than 0 means opponent player has the
	 *         highest sequence
	 */
	public int checkHighSequence(List<Card> curr, List<Card> oppo) {
		int seq1 = 0;
		int seq2 = 0;
		for (Card card : curr) {
			seq1 += card.getIntRank();
		}
		for (Card card : oppo) {
			seq1 += card.getIntRank();
		}
		return seq1 - seq2;
	}

	/**
	 * check the highest card
	 * 
	 * @param curr
	 * @param oppo
	 * @return bigger than 0 means current player has the highest card, equals
	 *         to 0 means current player has the same highest card with
	 *         opponent, smaller than 0 means opponent player has the highest
	 *         card
	 */
	public int checkHighCard(Card curr, Card oppo) {
		curr.setHigh();
		oppo.setHigh();

		int dis = curr.getIntRank() - oppo.getIntRank();

		curr.setLow();
		oppo.setLow();

		return dis;
	}

	/**
	 * Calculate the money of a node, according to its previous move
	 * 
	 * @param action
	 *            reprsents the previous action
	 * */
	public void setMoneyForNode(String action, int level, Node n) {
		n.p1_bet = n.parent.p1_bet;
		n.p2_bet = n.parent.p2_bet;
		if (action.equals(BET))
			if (level == 1)
				n.p1_bet += 250;
			else
				n.p2_bet += 250;
		if (action.equals(BET_ALLIN))
			if (level == 1)
				n.p1_bet = 1500;
			else
				n.p2_bet = 1500;
		if (action.equals(RAISE_DOUBLE))
			if (level == 1)
				n.p1_bet = 2 * n.p2_bet;
			else
				n.p2_bet = 2 * n.p1_bet;
		if (action.equals(RAISE_ALLIN))
			if (level == 1)
				n.p1_bet = 1500;
			else
				n.p2_bet = 1500;
		if (action.equals(CALL))
			if (level == 1)
				n.p1_bet = n.p2_bet;
			else
				n.p2_bet = n.p1_bet;

	}

	public void deleteTree(Node n) {
		if (!n.getChildList().isEmpty())
			for (Node node : n.getChildList()) {
				deleteTree(node);
				node = null;
			}
		else
			n = null;
	}

	/**
	 * If the first player is rational, and the second rational player use fixed
	 * strategy
	 * */
	public void nonRationalGame() {
		LinkedList<String> actions = new LinkedList<String>();
		int result = Integer.MIN_VALUE;
		maxPlayer = 1;
		if (!verbose)
			System.out.println("Game P1_Result P2_Result");
		while (cur_hands <= total_hands) {
			int p1_firstRoundBet = 0;
			int p2_firstRoundBet = 0;
			int secondRoundActionIndex = 0;
			parent = new Node("Inital", null, 1, 0);
			buildGameTree(parent, 1, "", false);
			setMinMaxValueForNode();
			boolean flag = false;
			List<Integer> betList = new ArrayList<Integer>();
			while (!gameOver(actions)) {
				// If it is the player 1 to act first
				if (cur_hands % 2 == 1) {
					// if the game is not over
					if (!flag) {
						if (secondRound(actions)) {
							cards_onTable.add(fifthCard);
							if (verbose) {
								printVerboseResultFirstRound(betList, actions,
										flag);
								p1_firstRoundBet = p1_bet;
								p2_firstRoundBet = p2_bet;
								secondRoundActionIndex = actions.size() + 1;
							}
							actions.add(fifthCard.getSuit()
									+ fifthCard.getRank());
							flag = true;
						}
					}
					// actions.add(moves1[i]);
					// takeAction(p1, moves1[i]);
					// i++;
					// actions.add(moves1[i]);
					// takeAction(p2, moves1[i]);
					String p1Move = takeBestMove(1, actions);
					takeAction(p1, p1Move);
					actions.add(p1Move);
					if (p1Move.equals(FOLD) || p1Move.equals(CHECK))
						betList.add(0);
					else
						betList.add(p1_bet);

					if (gameOver(actions))
						break;
					String p2Move = fixedStrategy(p2, p1Move);
					actions.add(p2Move);
					if (p2Move.equals(FOLD) || p2Move.equals(CHECK))
						betList.add(0);
					else
						betList.add(p2_bet);
				} else {
					// if player2 act firs
					if (!flag) {
						if (secondRound(actions)) {
							cards_onTable.add(fifthCard);
							if (verbose) {
								printVerboseResultFirstRound(betList, actions,
										flag);
								p1_firstRoundBet = p1_bet;
								p2_firstRoundBet = p2_bet;
								secondRoundActionIndex = actions.size() + 1;
							}
							actions.add(fifthCard.getSuit()
									+ fifthCard.getRank());
							flag = true;
						}
					}
					// player2 take action
					String p2Move = "";
					if (actions.isEmpty())
						p2Move = fixedStrategy(p2, "");
					else {
						p2Move = fixedStrategy(p2, actions.getLast());
					}
					actions.add(p2Move);
					if (p2Move.equals(FOLD) || p2Move.equals(CHECK))
						betList.add(0);
					else
						betList.add(p2_bet);

					if (gameOver(actions))
						break;
					// player1 take action
					String p1Move = takeBestMove(1, actions);
					takeAction(p1, p1Move);
					actions.add(p1Move);
					if (p1Move.equals(FOLD) || p1Move.equals(CHECK))
						betList.add(0);
					else
						betList.add(p1_bet);
				}

			}

			// when this hand ends
			// for (String a : actions) {
			// System.out.print(a + "->");
			// }
			boolean fold = false;
			if (foldPlayer != null)
				fold = true; // if any player fold ,it is true
			result = getWinner(fold);
			addWinningTimes(result); // set the winning times for the player
			List<Integer> values = new ArrayList<Integer>();
			values.addAll(setWinMoney(result));

			int p1Money = Math.abs(values.get(0));
			int p2Money = Math.abs(values.get(1));
			if (!verbose)
				printNonVHandResult(p1Money, p2Money, result);
			else {
				if (!flag) {
					printVerboseResultFirstRound(betList, actions, flag);
				} else {
					printVerboseResultSecondRound(secondRoundActionIndex,
							actions, betList);
				}
				printVerboseResult(p1Money, p2Money, result);
			}
			resetGame(); // end this hand
			actions.clear();
			cur_hands++;
		}// end all the hands
	}

	/**
	 * Start the game
	 * */
	public void startGame() {

		if (!rational)
			nonRationalGame();
		else {
			rationalGame();
		}
		printGameResult();
	}

	/**
	 * If both players are rational player, they will both use minmax algorithm
	 * */
	public void rationalGame() {
		LinkedList<String> actions = new LinkedList<String>();
		int result = Integer.MIN_VALUE;
		if (!verbose)
			System.out.println("Game P1_Result P2_Result");
		while (cur_hands <= total_hands) {
			int p1_firstRoundBet = 0;
			int p2_firstRoundBet = 0;
			int secondRoundActionIndex = 0;
			parent = new Node("Inital", null, 1, 0);
			List<Integer> betList = new ArrayList<Integer>();
			buildGameTree(parent, 1, "", false);
			setMinMaxValueForNode();
			boolean flag = false;
			if (cur_hands % 2 == 1) {
				maxPlayer = 1;
			} else
				maxPlayer = 0;
			while (!gameOver(actions)) {
				// If it is the player 1 to act first
				if (cur_hands % 2 == 1) {
					// if the game is not over
					if (!flag) {
						if (secondRound(actions)) {
							cards_onTable.add(fifthCard);
							if (verbose) {
								printVerboseResultFirstRound(betList, actions,
										flag);
								p1_firstRoundBet = p1_bet;
								p2_firstRoundBet = p2_bet;
								secondRoundActionIndex = actions.size() + 1;
							}
							actions.add(fifthCard.getSuit()
									+ fifthCard.getRank());
							flag = true;
						}
					}
					String p1Move = takeBestMove(1, actions);
					takeAction(p1, p1Move);
					actions.add(p1Move);
					if (p1Move.equals(FOLD) || p1Move.equals(CHECK))
						betList.add(0);
					else
						betList.add(p1_bet);
					if (gameOver(actions))
						break;
					if (!flag) {
						if (secondRound(actions)) {
							cards_onTable.add(fifthCard);
							if (verbose) {
								printVerboseResultFirstRound(betList, actions,
										flag);
								p1_firstRoundBet = p1_bet;
								p2_firstRoundBet = p2_bet;
								secondRoundActionIndex = actions.size() + 1;
							}
							actions.add(fifthCard.getSuit()
									+ fifthCard.getRank());
							flag = true;
						}
					}
					String p2Move = takeBestMove(0, actions);
					takeAction(p2, p2Move);
					actions.add(p2Move);
					if (p1Move.equals(FOLD) || p2Move.equals(CHECK))
						betList.add(0);
					else
						betList.add(p2_bet);
					// String p1Move = takeBestMove(1, actions);
					// takeAction(p1, p1Move);
					// actions.add(p1Move);
					// if (gameOver(actions))
					// break;
					// String p2Move = takeBestMove(0, actions);
					// takeAction(p2, p2Move);
					// actions.add(p2Move);
				} else {
					// if player2 act first
					if (!flag) {
						if (secondRound(actions)) {
							cards_onTable.add(fifthCard);
							if (verbose) {
								printVerboseResultFirstRound(betList, actions,
										flag);
								p1_firstRoundBet = p1_bet;
								p2_firstRoundBet = p2_bet;
								secondRoundActionIndex = actions.size() + 1;
							}
							actions.add(fifthCard.getSuit()
									+ fifthCard.getRank());
							flag = true;
						}
					}
					String p2Move = takeBestMove(1, actions);
					takeAction(p2, p2Move);
					actions.add(p2Move);
					if (p2Move.equals(FOLD) || p2Move.equals(CHECK))
						betList.add(0);
					else
						betList.add(p2_bet);
					if (gameOver(actions))
						break;
					if (!flag) {
						if (secondRound(actions)) {
							cards_onTable.add(fifthCard);
							if (verbose) {
								printVerboseResultFirstRound(betList, actions,
										flag);
								p1_firstRoundBet = p1_bet;
								p2_firstRoundBet = p2_bet;
								secondRoundActionIndex = actions.size() + 1;
							}
							actions.add(fifthCard.getSuit()
									+ fifthCard.getRank());
							flag = true;
						}
					}
					String p1Move = takeBestMove(0, actions);
					takeAction(p1, p1Move);
					actions.add(p1Move);
					if (p1Move.equals(FOLD) || p1Move.equals(CHECK))
						betList.add(0);
					else
						betList.add(p1_bet);
					// String p2Move = takeBestMove(1, actions);
					// takeAction(p2, p2Move);
					// actions.add(p2Move);
					//
					// if (gameOver(actions))
					// break;
					//
					// String p1Move = takeBestMove(0, actions);
					// takeAction(p1, p1Move);
					// actions.add(p1Move);
				}
				// end else
			}
			// when this hand ends
			boolean fold = false;
			if (foldPlayer != null)
				fold = true; // if any player fold ,it is true
			result = getWinner(fold);
			addWinningTimes(result); // set the winning times for the player
			List<Integer> values = new ArrayList<Integer>();
			values.addAll(setWinMoney(result));

			int p1Money = Math.abs(values.get(0));
			int p2Money = Math.abs(values.get(1));
			if (!verbose)
				printNonVHandResult(p1Money, p2Money, result);
			else {
				if (!flag) {
					// if at the fisr round the game ends
					printVerboseResultFirstRound(betList, actions, flag);
				} else {
					printVerboseResultSecondRound(secondRoundActionIndex,
							actions, betList);
				}
				printVerboseResult(p1Money, p2Money, result);
			}
			resetGame(); // end this hand
			actions.clear();
			cur_hands++;
		}// end all the hands

	}

	private void printVerboseResultSecondRound(int i, List<String> moves,
			List<Integer> betList) {
		System.out.println();
		if (cur_hands % 2 == 1) {
			for (int j = i; j < moves.size(); j++) {
				if (j % 2 == 1) {
					System.out.println("Play1:" + moves.get(j) + " $"
							+ betList.get(j - 1));

				} else {
					System.out.println("Play2:" + moves.get(j) + " $"
							+ betList.get(j - 1));
				}
			}
		} else {
			for (int j = i; j < moves.size(); j++) {
				if (j % 2 == 0) {
					System.out.println("Play1:" + moves.get(j) + " $"
							+ betList.get(j - 1));
				} else {
					System.out.println("Play2:" + moves.get(j) + " $"
							+ betList.get(j - 1));
				}
			}
		}
	}

	private void printVerboseResult(int p1Money, int p2Money, int result) {
		System.out.println();
		String s1 = "";
		String s2 = "";
		if (result == 1) {
			s1 = "Win";
			s2 = "Lose";
		} else {
			s1 = "Lose";
			s2 = "Win";
		}
		System.out.println("Play1: " + p1.rankingEnum.toString() + " "
				+ p1.rankingList);
		System.out.println("Play2: " + p2.rankingEnum.toString() + " "
				+ p2.rankingList);
		System.out.println("Play1:" + s1 + " " + p1Money);
		System.out.println("Play2:" + s2 + " " + p2Money);

	}

	private void printVerboseResultFirstRound(List<Integer> betList,
			List<String> moves, boolean secondRound) {
		System.out.println("---------------------");
		System.out.println("    Hand " + cur_hands);
		System.out.println("---------------------");
		System.out.println("play1 " + p1.getCards());
		System.out.println("play2 " + p2.getCards());
		System.out.println("Table" + cards_onTable);
		System.out.println("Pot:$500");
		System.out.println();
		if (cur_hands % 2 == 1) {
			System.out.println("Play1: Acts first.");
			System.out.println();
			for (int i = 0; i < moves.size(); i++) {
				if (i % 2 == 0) {
					System.out.println("Play1:" + moves.get(i) + " $"
							+ betList.get(i));
				} else {
					System.out.println("Play2:" + moves.get(i) + " $"
							+ betList.get(i));
				}
			}
		} else {
			System.out.println("Play2: Acts first.");
			System.out.println();
			for (int i = 0; i < moves.size(); i++) {
				if (i % 2 == 1) {
					System.out.println("Play1:" + moves.get(i) + " $"
							+ betList.get(i));
				} else {
					System.out.println("Play2:" + moves.get(i) + " $"
							+ betList.get(i));
				}
			}
		}
		if (secondRound) {
			System.out.println();
			System.out.println("Play1:" + p1.getCards());
			System.out.println("Play2:" + p2.getCards());
			System.out.print("Table:");
			for (Card c : cards_onTable) {
				System.out.print(" " + c);
			}
			System.out.println(" " + fifthCard);
			System.out.println("Pot: $" + (500 + p1_bet + p2_bet));
			System.out.println();
		}
	}

	public void resetGame() {
		cards_onTable.clear();
		cardList.clear();
		deleteTree(parent);
		p1_bet = 0;
		p2_bet = 0;
		parent = null;
		fifthCard = null;
		p1.reset();
		p2.reset();
		initialCards();
	}

	private void printNonVHandResult(int p1, int p2, int result) {
		String s1 = "";
		String s2 = "";
		if (result == 1) {
			s1 += "+";
			s2 += "-";
		} else if (result == -1) {
			s1 += "-";
			s2 += "+";
		}
		System.out.println(cur_hands + ". " + s1 + p1 + " " + s2 + p2);
	}

	private List<Integer> setWinMoney(int result) {
		List<Integer> values = new ArrayList<Integer>();
		int p1Money = 0, p2Money = 0;
		switch (result) {
		case 1:
			p1Money = p2_bet + 500;
			p2Money = -p2_bet;
			break;

		case -1:
			p1Money = -p1_bet;
			p2Money = p1_bet + 500;
			break;
		case 0:
			p1Money = (p1_bet + p2_bet + 500) / 2;
			p2Money = (p1_bet + p2_bet + 500) / 2;
			break;
		default:
			break;
		}
		p1_totalWinMoney += p1Money;
		p2_totalWinMoney += p2Money;
		values.add(p1Money);
		values.add(p2Money);
		return values;
	}

	public void printGameResult() {
		System.out.println("---------------------");
		System.out.println("	 Results");
		System.out.println("---------------------");
		System.out.println("Player1: " + p1.winTime + "win, "
				+ (total_hands - p1.winTime) + " lose " + " :"
				+ p1_totalWinMoney);
		System.out.println("Player2: " + p2.winTime + "win, "
				+ (total_hands - p2.winTime) + " lose " + " :"
				+ p2_totalWinMoney);
	}

	public void addWinningTimes(int result) {
		switch (result) {
		case 1:
			p1.winTime++;
			break;
		case -1:
			p2.winTime++;
			break;
		case 0:
			System.out.println("Game draw");
			break;
		default:
			break;
		}
	}

	/**
	 * After the game ends, find the winner
	 * 
	 * @param fold
	 *            if any player, it is true
	 * */
	public int getWinner(boolean fold) {

		ArrayList<Card> player_cards = new ArrayList<Card>();
		player_cards.addAll(p1.getCards());
		player_cards.addAll(cards_onTable);

		ArrayList<Card> opponent_cards = new ArrayList<Card>();
		opponent_cards.addAll(p2.getCards());
		opponent_cards.addAll(cards_onTable);
		int result = 0;

		// check the rank of cards on hand
		RankingUtil.checkRanking(p1, player_cards);
		List<Card> curr_ranking = p1.rankingList;
		RankingEnum maxPlayer_rankType = p1.rankingEnum;

		RankingUtil.checkRanking(p2, opponent_cards);
		List<Card> oppo_ranking = p2.rankingList;
		RankingEnum oppo_rankType = p2.rankingEnum;
		if (fold) {
			if (foldPlayer.name.equals("p1"))
				return -1;
			else
				return 1;
		}

		result = checkRanking(curr_ranking, maxPlayer_rankType, oppo_ranking,
				oppo_rankType, player_cards, opponent_cards);
		return result;
	}

	public boolean secondRound(LinkedList<String> actions) {
		int count_call = 0;
		int count_check = 0;
		for (String action : actions) {
			if (action.equals(CALL))
				count_call++;
			else if (action.equals(CHECK))
				count_check++;
		}
		if (count_check == 2)
			return true;
		if (count_call == 1)
			return true;
		return false;

	}

	public boolean gameOver(LinkedList<String> actions) {
		if (!actions.isEmpty()) {
			if (p1.getCapital() == 0 && p2.getCapital() == 0)
				return true;
			if (actions.getLast().equals(FOLD))
				return true;
			int count_call = 0;
			int count_check = 0;
			for (String action : actions) {
				if (action.equals(CALL))
					count_call++;
				else if (action.equals(CHECK))
					count_check++;
			}
			if (count_check == 4)
				return true;
			if (count_call == 2)
				return true;
			if (count_check == 2 && count_call == 1)
				return true;
		} else
			return false;

		return false;
	}

	/**
	 * Make operation on the money, according to the player's move
	 * 
	 * @param p
	 *            the player who takes action
	 * @param action
	 *            what action he takes
	 * */
	public void takeAction(Player p, String action) {

		if (action.equals(FOLD)) {
			foldPlayer = p;
		} else if (action.equals(BET))
			bet(p, 0);
		else if (action.equals(BET_ALLIN))
			bet(p, 1);
		else if (action.equals(CALL))
			call(p);
		else if (action.equals(RAISE_DOUBLE))
			raise(p, 0);
		else if (action.equals(RAISE_ALLIN))
			raise(p, 1);
	}

	public void bet(Player p, int option) {
		if (option == 0) {
			if (p.name.equals(p2.name))
				p2_bet += 250;
			else
				p1_bet += 250;
			int cap = p.getCapital();
			p.setCapital(cap - 250);

			// bet $250
		} else {
			if (p.name.equals(p2.name))
				p2_bet += p2.getCapital();
			else
				p1_bet += p1.getCapital();
			// bet all in
			p.setCapital(0);
		}
	}

	public void call(Player p) {
		int cap = p.getCapital();
		if (p.name.equals(p2.name)) {
			p2_bet = p1_bet;
			p2.setCapital(cap - p2_bet);

		} else {
			p1_bet = p2_bet;
			p1.setCapital(cap - p1_bet);

		}
		// this round is over

	}

	/**
	 * represents the raise option,
	 * 
	 * @param option
	 *            0 reprensents raise double, 1 reprensents raise all in
	 * */
	public void raise(Player p, int option) {
		if (option == 0) {
			// raise double
			int cap = p.getCapital();
			if (p.name.equals(p2.name)) {
				p2_bet = 2 * p1_bet;
				p2.setCapital(cap - p2_bet);

			} else {
				p1_bet = 2 * p2_bet;
				p1.setCapital(cap - p2_bet);

			}

		} else {
			// raise all in
			if (p.name.equals(p2.name)) {
				p2_bet = 1500;
				p2.setCapital(0);

			} else {
				p1_bet = 1500;
				p1.setCapital(0);

			}
		}
	}

	/**
	 * Fixed strategy for the player2
	 * */
	public String fixedStrategy(Player p, String pre_moves) {
		ArrayList<Card> p_all_cards = new ArrayList<Card>();
		p_all_cards.addAll(p.getCards());
		p_all_cards.addAll(cards_onTable);
		RankingUtil.checkRanking(p, p_all_cards);
		List<Card> oppo_ranking = p.rankingList;
		RankingEnum oppo_rankType = p.rankingEnum;
		// If Player 1 bets, Player 2 will Call with any single pair, and Fold
		// all other hands.

		if (p.rankingEnum.ordinal() >= 2) {
			if (pre_moves.equals(BET) || pre_moves.equals(RAISE_DOUBLE)
					|| pre_moves.equals(RAISE_ALLIN)
					|| pre_moves.equals(BET_ALLIN)) {
				takeAction(p, CALL);
				return CALL;

			}
			if (pre_moves.isEmpty() || pre_moves.equals(CHECK))
				takeAction(p, BET);
			return BET;
		}
		// if Player 2 has a hand that is worse than two pair, they
		// will always Check
		else {
			if (pre_moves.isEmpty() || pre_moves.equals(CHECK)) {
				takeAction(p, CHECK);
				return CHECK;
			}
			if (pre_moves.equals(BET)) {
				if (p.rankingEnum.ordinal() == 1) {
					takeAction(p, CALL);
					return CALL;
				}
			}
			takeAction(p, FOLD);
			return FOLD;

		}
	}
}
