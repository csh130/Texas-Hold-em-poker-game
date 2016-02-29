/**
 * This class checks the completion of the card
 * */
import java.util.*;
 
public class RankingUtil {
	
	public final static String suits[] = new String[]{"S","H","D","C"};
	public final static String ranks[] = new String[]{"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
	
	
	public static void checkRanking(Player player, List<Card> onHand)
	{					
		//ROYAL_FLUSH
		List<Card> rankingList = getRoyalFlush(onHand);
		if(rankingList != null)
		{
			setRankingEnumAndList(player, RankingEnum.ROYAL_FLUSH, rankingList);			
			return;
		}
		
		//STRAIGHT_FLUSH
		rankingList = getStraightFlush(onHand);
		if(rankingList != null)
		{
			setRankingEnumAndList(player, RankingEnum.STRAIGHT_FLUSH, rankingList);
			return;
		}
		
		//FOUR_OF_A_KIND
		rankingList = getFourOfAKind(onHand);
		if(rankingList != null)
		{
			setRankingEnumAndList(player, RankingEnum.FOUR_OF_A_KIND, rankingList);
			return;
		}
		
		//FULL_HOUSE
		rankingList = getFullHouse(onHand);
		if(rankingList != null)
		{
			setRankingEnumAndList(player, RankingEnum.FULL_HOUSE, rankingList);
			return;
		}
		
		//FLUSH
		rankingList = getFlush(onHand);
		if(rankingList != null)
		{
			setRankingEnumAndList(player, RankingEnum.FLUSH, rankingList);
			return;
		}
		
		//STRAIGHT
		rankingList = getStraight(onHand);
		if (rankingList != null) {
			setRankingEnumAndList(player, RankingEnum.STRAIGHT, rankingList);
			return;
		}
		
		//THREE_OF_A_KIND
		rankingList = getThreeOfAKind(onHand);
		if (rankingList != null) {
			setRankingEnumAndList(player, RankingEnum.THREE_OF_A_KIND, rankingList);
			return;
		}
		
		//TWO_PAIR
		rankingList = getTwoPair(onHand);
		if (rankingList != null) {
			setRankingEnumAndList(player, RankingEnum.TWO_PAIR, rankingList);
			return;
		}
		
		//ONE_PAIR
		rankingList = getOnePair(onHand);
		if (rankingList != null) {
			setRankingEnumAndList(player, RankingEnum.ONE_PAIR, rankingList);
			return;
		}

		//HIGH_CARD		
		rankingList = getHighCard(onHand);		
		setRankingEnumAndList(player, RankingEnum.HIGH_CARD, rankingList);
		return;
	}
	
	public static List<Card> getRoyalFlush(List<Card> onHand)
	{
		Map<String,List<Card>> cards = new HashMap<String,List<Card>>();
		for(String suit : suits)
		{
			List<Card> suitCards = new ArrayList<Card>();
			for(Card card : onHand)
			{
				if(card.suit.equals(suit))
				{
					if(card.rank.equals("A") || card.rank.equals("10") || card.rank.equals("J") ||
							card.rank.equals("Q") || card.rank.equals("K"))
					{
						suitCards.add(card);
					}else{
						if(suitCards.size()==5)
						{
							return suitCards;
						}
					}
				}				
			}
			if(suitCards.size()==5)
			{
				return suitCards;
			}		
		}
		return null;
	}
	
	private static List<Card> getStraightFlush(List<Card> onHand)
	{
		return getSequence(onHand, true);
	}
	
	private static List<Card> getFourOfAKind(List<Card> onHand)
	{
		return checkPair(onHand, 4);			
	}
	
	private static List<Card> getFullHouse(List<Card> cards)
	{
		List<Card> onHand = new ArrayList<Card>();
		onHand.addAll(cards);
		List<Card> threeList = checkPair(onHand, 3);
		if(threeList != null)
		{
			onHand.removeAll(threeList);
			List<Card> twoList = checkPair(onHand, 2);
			if(twoList != null)
			{
				threeList.addAll(twoList);
				return threeList;
			}
		}
		return null;
	}
	
	private static List<Card> getFlush(List<Card> cards)
	{
		List<Card> onHand = new ArrayList<Card>();
		onHand.addAll(cards);
		
		for(Card card : onHand)
		{
			if(card.rank.equals("A"))
			{
				card.setHigh();
			}
		}
		Collections.sort(onHand);

		List<Card> flushList = new ArrayList<Card>();
		for(int i=onHand.size()-1;i>=0;i--)
		{
			Card card = onHand.get(i);
			for(int j=onHand.size()-1;j>=0;j--)
			{
				Card temp = onHand.get(j);
				if(card.suit.equals(temp.suit))
				{
					if(!flushList.contains(temp))
					{
						flushList.add(temp);
					}
					if(!flushList.contains(card))
					{
						flushList.add(card);
					}
				}
				if(flushList.size() == 5)
				{
					return flushList;
				}
			}			
			flushList.clear();
		}
		return null;
	}
	
	public static List<Card> getStraight(List<Card> onHand)
	{
		return getSequence(onHand, false);
	}
	
	public static List<Card> getThreeOfAKind(List<Card> onHand)
	{
		return checkPair(onHand, 3);
	}
	
	private static List<Card> getSequence(List<Card> cards, boolean compareSuit)
	{
		List<Card> onHand = new ArrayList<Card>();
		onHand.addAll(cards);
		
		Collections.sort(onHand);
		
		deletedRepeatedCard(onHand);
		
		List<Card> sequenceList = new ArrayList<Card>();
		
		if(compareSuit)
		{			
			
			Card cardPrevious = null;		
			for(int i=onHand.size()-1;i>=0;i--)
			{
				Card card = onHand.get(i);
				if(cardPrevious != null)
				{
					if(cardPrevious.getIntRank()-card.getIntRank()==1)
					{
						if(cardPrevious.suit.equals(card.suit))
						{
							if(sequenceList.size()==0)
							{
								sequenceList.add(cardPrevious);
							}
							sequenceList.add(card);
							if(sequenceList.size() == 5)
							{
								return sequenceList;
							}
						}
						
					}else{						
						sequenceList.clear();
					}
				}
				cardPrevious = card;
			}
		}else{			
			Card cardPrevious = null;
			
			//judge if contains A and K
			if(onHand.get(0).rank.equals("A"))
			{				
				if(onHand.get(onHand.size()-1).rank.equals("K"))
				{					
					onHand.get(0).setHigh();
					//second sort after changed the position of A
					Collections.sort(onHand);
				}
			}
									
			for(int i=onHand.size()-1;i>=0;i--)
			{					
				Card card = onHand.get(i);
				if(cardPrevious != null)
				{
					if(cardPrevious.getIntRank()-card.getIntRank()==1)
					{
						if(sequenceList.size()==0)
						{
							sequenceList.add(cardPrevious);
						}
						sequenceList.add(card);
						if(sequenceList.size() == 5)
						{
							return sequenceList;
						}
					}else{						
						sequenceList.clear();
					}
				}
				cardPrevious = card;
			}
		}
		if(sequenceList.size()==5)
		{
			return sequenceList;
		}
		return null;
	}
	
	public static List<Card> getTwoPair(List<Card> cards)
	{
		List<Card> onHand = new ArrayList<Card>();
		onHand.addAll(cards);
		List<Card> twoPair = checkPair(onHand, 2);
		if(twoPair != null)
		{
			onHand.removeAll(twoPair);
			List<Card> temp = checkPair(onHand, 2);
			if(temp != null)
			{
				twoPair.addAll(temp);
				return twoPair;
			}
		}
		return null;
	}
	
	public static List<Card> getOnePair(List<Card> onHand)
	{
		return checkPair(onHand, 2);
	}
	
	private static List<Card> checkPair(List<Card> cards, int pairSize)
	{				
		List<Card> onHand = new ArrayList<Card>();
		onHand.addAll(cards);
		for(Card card : onHand)
		{
			if(card.rank.equals("A"))
			{
				card.setHigh();
			}
		}
		Collections.sort(onHand);
				
		List<Card> checkPair = new ArrayList<Card>();
		for(int i=onHand.size()-1;i>=0;i--)
		{
			Card card = onHand.get(i);
			checkPair.add(card);
			for(int j=onHand.size()-1;j>0;j--)
			{
				Card temp = onHand.get(j);
				if(!card.equals(temp) && card.rank.equals(temp.rank))
				{
					checkPair.add(temp);
				}
				if(checkPair.size() == pairSize)
				{
					return checkPair;
				}
			}			
			checkPair.clear();
		}
		return null;
	}	
	
	private static void deletedRepeatedCard(List<Card> onHand)
	{
		//H,S,D,C
		int cardSuit[] = new int[4];
		//get most suit
		String mainSuit = "";
		for(Card card : onHand)
		{
			if(card.suit.equals("H"))
			{
				cardSuit[0]++;
			}else if(card.suit.equals("S"))
			{
				cardSuit[1]++;
			}else if(card.suit.equals("D"))
			{
				cardSuit[2]++;
			}else{
				cardSuit[3]++;
			}
		}
		for(int i=0;i<cardSuit.length;i++)
		{
			if(cardSuit[i]>=5)
			{
				mainSuit = i==0?"H":(i==1?"S":(i==2?"D":"C"));
			}
		}
		
		List<Card> threePair = checkPair(onHand, 3);
		if(threePair != null)
		{			
			for(Card card : threePair)
			{
				if(card.suit.equals(mainSuit))
				{					
					onHand.removeAll(threePair);
					onHand.add(card);
					break;
				}
			}
			
		}
		List<Card> twoPair = checkPair(onHand, 2);
		if(twoPair != null)
		{
			Card first = twoPair.get(0);
			Card second = twoPair.get(1);
			if(first.suit.equals(mainSuit))
			{
				onHand.remove(second);
			}else if(second.suit.equals(mainSuit)){
				onHand.remove(first);
			}
		}	
	}
	
	private static void setRankingEnumAndList(Player player,
			RankingEnum rankingEnum, List<Card> rankingList) {
		player.setRankingEnum(rankingEnum);
		player.setRankingList(rankingList);
		
		Collections.sort(rankingList);
		
		Card highestCard = rankingList.get(rankingList.size()-1);
		player.setHighCard(highestCard);
	}
	
	public static List<Card> getHighCard(List<Card> data)
	{				
		List<Card> onHand = new ArrayList<Card>();
		onHand.addAll(data);
		
		List<Card> highCard = new ArrayList<Card>();
		
		for(Card card : onHand)
		{
			if(card.getRank().equals("A"))
			{
				card.setHigh();
			}
		}
		Collections.sort(onHand);
		
		for(int i=onHand.size()-1;i>=2;i--)
		{
			Card card = onHand.get(i);
			highCard.add(card);
		}
		
		return highCard;
	}
}

