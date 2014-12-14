package maze;

import java.util.Map;
import java.util.Vector;

import org.jcodec.common.model.Point;

import utilities.Dijkstra;
import utilities.DirectedGraph;
import agents.DriverAgentBDI;

public class City {

	// Constants
	public final static int CLOSED_ROAD = -1;
	public final static int ROAD = 0;
	public final static int POI = 1;
	public final static int CAR = 2;
	public final static int GOAL = 3;

	// Agent owner
	DriverAgentBDI agent = null;

	// Source and destination points
	public Node current = null;
	public Vector<Node> destinations = new Vector<Node>();
	public DirectedGraph<Node> graph = new DirectedGraph<Node>();
	public Vector<Node> nodes = new Vector<Node>();
	private int maze[][] = null;

	public String draw()
	{
		String ret = "";
		
		for(int i=0; i<maze.length; i++)
		{
			 for(int j=0; j<maze[i].length; j++)
			 {				 
				 // Im there
				 if ( j == this.current.x && i == this.current.y )
				 {
					 ret += "P";
				 }
				 else
				 {
					 boolean driverFound = false;
					 
					 // Find other drivers
					 findDrivers:
					 for(int k=0; k<AgentsVector.agents.size(); k++)
					 {
						 Node n = AgentsVector.agents.get(k).maze.current;
						 
						 if ( j == n.x && i == n.y )
						 {
							 driverFound = true;
							 ret += (char)('A' + k);
							 break findDrivers;
						 }
					 }
				 
					 if ( !driverFound )
					 {
						 //if ( j == this.current.x && i == this.current.y ) ret += "P";
						 if ( j == this.destinations.firstElement().x && i == this.destinations.firstElement().y ) ret += "X";
						 else if ( maze[i][j] == City.CLOSED_ROAD ) ret += "-";
						 else ret += maze[i][j]; 
					 }
				 }
				 
				 ret += " ";
			 }
			 
			 ret += "\n";
		}
		
		return ret;
	}
	 
	public void setEdges(Node src, Node dest)
	{		
		if (src != null &&  dest != null && src.value == City.ROAD && dest.value == City.ROAD )
		{
			/*if ( dest.value == 0 )*/ this.graph.addEdge(src, dest, 1);
			//else if ( dest.value == 1 ) src.pointsInterest.addElement(dest);
		}
	}
	
	public void removeNode(Node dst)
	{		
		if ( dst != null )
		{
			// Get edges from dst
			Map<Node, Double> edges = this.graph.edgesFrom(dst);
			Vector<Node> toRemove = new Vector<Node>();
			
			// Add to remove
			for( Node n : edges.keySet() ) toRemove.add(n);
			
			// Remove
			for( Node n : toRemove ) this.graph.removeEdge(dst, n);
						
			// Set closed
			dst.value = City.CLOSED_ROAD;
			this.maze[dst.y][dst.x] = City.CLOSED_ROAD;
		}
	}
	
	public void removeNodeByCoords(int x, int y)
	{		
		this.removeNode( Node.getNode(this.nodes, x, y) );
	}
	 
	public Map<Node, Double> getShortestPath(Node source)
	{
		return Dijkstra.shortestPaths(this.graph, source);
	}
	 	
	public City(DriverAgentBDI a)
	{		 
		this.agent = a;
		
		// Creates
		this.maze = new int[Application.driversMaze.length][];
		
		// Copy Application.driversMaze
		for(int i=0; i<Application.driversMaze.length; i++)
		{
			this.maze[i] = new int[Application.driversMaze[i].length];
			
			for(int j=0; j<Application.driversMaze[i].length; j++)
			{	
				this.maze[i][j] = Application.driversMaze[i][j];
			}
		}
		
			 
		for(int i=0; i<maze.length; i++)
		{
			for(int j=0; j<maze[i].length; j++)
			{		
				Node n = new Node(j, i, maze[i][j]);
				
				// Add node				
				if ( n.value == City.ROAD )
				{
					nodes.add(n);	
					graph.addNode(n);
				}
			}
		}

		// Adiciona edges a estrada
		for (int i = 0; i < nodes.size(); i++) {
			Node src = nodes.get(i);

			// Vizinhos
			this.setEdges(src, Node.getNode(nodes, src.x - 1, src.y));
			this.setEdges(src, Node.getNode(nodes, src.x + 1, src.y));
			this.setEdges(src, Node.getNode(nodes, src.x, src.y - 1));
			this.setEdges(src, Node.getNode(nodes, src.x, src.y + 1));
		}

		// Get destinationPoints
		current = Node.getNode(nodes, this.agent.control.x, this.agent.control.y);
		
		for ( Point dst : this.agent.control.points )
		{
			destinations.add( Node.getNode(nodes, dst.getX(), dst.getY()) );	
		}
		
		this.agent.log("Start point: " + current.x + "|" + current.y, false);
		this.agent.log("End point: " + destinations.firstElement().x + "|" + destinations.firstElement().y, false);
	}

	public double findShortNode(Node check) {
		Map<Node, Double> path = this.getShortestPath(check);
		return path.get( this.destinations.firstElement() );
	}
	 
	public boolean moveCloser()
	{		
		Node nextNode = null, working = null;
		double distance = Double.POSITIVE_INFINITY, newValue = Double.POSITIVE_INFINITY;

		// Check nearest
		if ((working = Node.getNode(nodes, current.x - 1, current.y)) != null
				&& working.value == City.ROAD) {
			newValue = this.findShortNode(working);
			if (newValue < distance) {
				distance = newValue;
				nextNode = working;
			}
		}
		if ((working = Node.getNode(nodes, current.x + 1, current.y)) != null
				&& working.value == City.ROAD) {
			newValue = this.findShortNode(working);
			if (newValue < distance) {
				distance = newValue;
				nextNode = working;
			}
		}
		if ((working = Node.getNode(nodes, current.x, current.y - 1)) != null
				&& working.value == City.ROAD) {
			newValue = this.findShortNode(working);
			if (newValue < distance) {
				distance = newValue;
				nextNode = working;
			}
		}
		if ((working = Node.getNode(nodes, current.x, current.y + 1)) != null
				&& working.value == City.ROAD) {
			newValue = this.findShortNode(working);
			if (newValue < distance) {
				distance = newValue;
				nextNode = working;
			}
		}

		// See if no path found exit
		if (nextNode == null) {
			this.agent.log("No path found (nextNode==null)", true);
			return false;
		}
		
		// Set next node
		current = nextNode;
		
		this.agent.log("Best path(" + distance + "): [" + nextNode.x + "," + nextNode.y + "]", false);
		return true;
	}
	
}
