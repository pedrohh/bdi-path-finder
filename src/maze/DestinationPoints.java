package maze;

import java.util.Vector;
import org.jcodec.common.model.Point;

public class DestinationPoints {
	
	public String name;
	public int x, y;
	Vector<Point> points = new Vector<Point>();
	
	public DestinationPoints(String n, int x, int y)
	{
		this.name = n;
		this.x = x;
		this.y = y;
		this.points.clear();
	}
	
	public static DestinationPoints getAgent(Vector<DestinationPoints> vect, String name)
	{
		for( DestinationPoints d : vect )
		{
			if ( d.name.equals(name) ) return d;
		}
		
		return null;
	}
	
}
