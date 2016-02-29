/**
 * Project for CS546, Literature game, This class is card class.
 * 
 * @author Sihan Cheng
 */
public class Card implements Comparable {
	public String rank;
	public String suit;
	public boolean isHigh; // whether A is 14 or value is 1

	public Card(String suit, String rank) {
		this.rank = rank;
		this.suit = suit;
	}

	public String getRank() {
		return rank;
	}

	public int getIntRank() {
		int result = 0;
		if (this.rank.equals("J")) {
			result = 11;
		} else if (this.rank.equals("Q")) {
			result = 12;
		} else if (this.rank.equals("K")) {
			result = 13;
		} else if (this.rank.equals("A")) {
			if (isHigh) {
				result = 14;
			} else {
				result = 1;
			}
		} else {
			result = Integer.parseInt(this.rank);
		}
		return result;
	}

	public void setHigh() {
		isHigh = true;
	}

	public void setLow() {
		isHigh = false;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getSuit() {
		return suit;
	}

	public void setSuit(String suit) {
		this.suit = suit;
	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		int data = this.getIntRank();
		int other = ((Card) o).getIntRank();
		return data - other;
	}

	public String toString() {
		return rank + suit;
	}
}
