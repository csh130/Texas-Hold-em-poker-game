import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sun.org.glassfish.external.statistics.annotations.Reset;

/**
 * Project for CS546, Literature game, This class represents player in the game,
 * every player has a name and a set of cards
 * 
 * @author Sihan Cheng
 */
public class Player {
	public String name;
	private ArrayList<Card> cards;
	public Card highCard;
	public List<Card> rankingList;
	public RankingEnum rankingEnum;
	public int capital;
	public int winTime;

	// When one hand is over, completes one of the rank, stores the max card
	// value of the completion in max_card_value

	public Player(String name) {
		this.name = name;
		this.rankingList = null;
		this.highCard = null;
		this.capital = 1500;
		this.winTime = 0;
		this.cards = new ArrayList<Card>();
	}

	public void reset() {
		this.capital = 1500;
		this.cards.clear();
		this.highCard = null;
		this.rankingList.clear();
		this.highCard = null;

	}

	public int getWinTime() {
		return winTime;
	}

	public void setWinTime(int winTime) {
		this.winTime = winTime;
	}

	public int getCapital() {
		return capital;
	}

	public void setCapital(int capital) {
		this.capital = capital;
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}

	public List<Card> getCards() {
		return cards;
	}

	public Card getHighCard() {
		return highCard;
	}

	public void setHighCard(Card highCard) {
		this.highCard = highCard;
	}

	public List<Card> getRankingList() {
		return rankingList;
	}

	public void setRankingList(List<Card> rankingList) {
		this.rankingList = rankingList;
	}

	public RankingEnum getRankingEnum() {
		return rankingEnum;
	}

	public void setRankingEnum(RankingEnum rankingEnum) {
		this.rankingEnum = rankingEnum;
	}

}
