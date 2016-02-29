import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Project for CS546, Literature game, This class is the node class
 * 
 * @author Sihan Cheng
 */
public class Node implements Serializable {
	public String action; // name of the node
	public int p1_bet;// amount p1 bets
	public int p2_bet;// amount p2 bets
	public double score; // score for this node
	public double minPlayer_score; // score for the min player
	public int depth;
	public String bestAction; // best move for the max player
	public String minP_bestAction; // best move for the min player
	public Node parent;
	public boolean secondTree;
	public Card fifthCard;
	public int level;
	public ArrayList<Node> childList;

	public Node(String action, Node parent, int level, int depth) {
		this.parent = parent;
		this.action = action;
		this.p1_bet = 0;
		this.bestAction = "";
		this.minP_bestAction = "";
		this.p2_bet = 0;
		this.score = 0;
		this.level = level;
		this.childList = new ArrayList<Node>();
		this.secondTree = false;
		this.fifthCard = null;
		this.depth = depth;
	}

	public void addChildNode(Node treeNode) {
		childList.add(treeNode);
	}

	// return all the children as a lsit
	public List<Node> getChildList() {
		return childList;
	}

	public void setChildList(ArrayList<Node> childList) {
		this.childList = childList;
	}

	public Node getParentNode() {
		return parent;
	}

	public void setParentNode(Node parentNode) {
		this.parent = parentNode;
	}

	@Override
	public String toString() {
		if (parent == null)
			return "[ " + action + " p1 bet:" + p1_bet + " p2 bet:" + p2_bet
					+ " level: " + level + " depth: " + depth + " score :"
					+ score + "]";
		else {
			return "[ " + action + ":  parent:" + parent.action + " p1 bet:"
					+ p1_bet + " p2 bet:" + p2_bet + " level: " + level
					+ " depth: " + depth + " score :" + score
					+ " best Action:(" + bestAction + ")]";
		}
	}
}
