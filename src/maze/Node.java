package maze;

import java.util.Vector;

public class Node {
	public int x, y, value;
	public Vector<Node> pointsInterest = new Vector<Node>();
	
	public Node(int x, int y, int value)
	{
		this.x = x;
		this.y = y;
		this.value = value;
	}
	
	public static Node getNode(Vector<Node> vect, int x, int y)
	{
		for(int i=0; i<vect.size(); i++)
		{
			Node tmp = vect.get(i);
			
			if ( tmp.x == x && tmp.y == y ) return tmp;
		}
		
		return null;
	}
	
}
