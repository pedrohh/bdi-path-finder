package maze;

import java.util.Vector;
import agents.DriverAgentBDI;

public class AgentsVector {

	public static Vector<DriverAgentBDI> agents = new Vector<DriverAgentBDI>();
	
 	public static void addAgent(DriverAgentBDI a)
 	{
 		if ( !AgentsVector.agents.contains(a) ) AgentsVector.agents.add(a);
 	}
 	
 	public static DriverAgentBDI getAgent(String name)
 	{
 		for( DriverAgentBDI a : AgentsVector.agents )
 		{
 			if ( a.getAgent().getServiceContainer().getId().toString().equals(name) ) return a;
 		}
 		
 		return null;
 	}	
}