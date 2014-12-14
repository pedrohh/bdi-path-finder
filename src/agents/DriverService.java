package agents;

import java.util.Collection;

import maze.AgentsVector;
import maze.Application;
import maze.City;
import maze.Node;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.tutorial.IChatService;
 
@Service
public class DriverService implements IChatService {
 
	@ServiceComponent
	protected IInternalAccess agent;
	
	private String ownId = null;
	private DriverAgentBDI thisParent = null;
	private City maze = null;
	
	public void message(final String sender, final String text) {
		
		// First run
		if ( ownId == null ) ownId = agent.getServiceContainer().getId().toString();
		if ( thisParent == null ) thisParent = AgentsVector.getAgent(ownId);
		if ( maze == null ) maze = thisParent.maze;
		
		//thisParent.log(ownId + ": received from " + sender + " message " + text, true);
		String responseArray[] = text.split("\\|");
		
		// Ignore own messages
		if ( !ownId.equals(sender) && !ownId.equals(responseArray[1]) )
		{									
			// My channels (own id or all drivers)	
			if ( ownId.equals(responseArray[0]) || "drivers".equals(responseArray[0]) )
			{				
				switch(responseArray[2]) 
				{
					case "closed":
						
						Node n = Node.getNode(maze.nodes, Integer.parseInt(responseArray[3]), Integer.parseInt(responseArray[4]));
						
						// See if already dealt
						if ( n.value != City.CLOSED_ROAD )
						{
							thisParent.log("IN closed_road from " + sender + ": " + responseArray[3] + "|" + responseArray[4], true);
							
							// Remove
							maze.removeNodeByCoords(Integer.parseInt(responseArray[3]), Integer.parseInt(responseArray[4]));
							
							// Tell other drivers (except if came already from drivers channel or in other way if it was the world)
							if ( sender.toLowerCase().contains("world") )
							{
								sendMessage("drivers|" + sender + "|closed|" + responseArray[3] + "|" + responseArray[4]);
								
								thisParent.log(maze.draw(), false);
							}
							
							// Update gui
							Application.gui.setBlockPosition(Integer.parseInt(responseArray[3]), Integer.parseInt(responseArray[4]));
						}
						
						break;
						
					case "discovered":
											
						// Tell the driver
						for( Node nn : maze.nodes )
						{
							if ( nn.value == City.CLOSED_ROAD ) 
							{
								sendMessage(sender + "|null|closed|" + nn.x + "|" + nn.y);
							}
						}		
						
						break;
						
				}	
			}
		}
	}
	
	public void sendMessage(final String text)
	{
		// Log
		thisParent.log("OUT " + text, false);
		
		IFuture<Collection<IChatService>> chatservices = agent.getServiceContainer().getRequiredServices("chatservices");
		chatservices.addResultListener(new DefaultResultListener<Collection<IChatService>>() {
			public void resultAvailable(Collection<IChatService> result) {
				for(IChatService cs : result) {
					cs.message(ownId, text);
				}
			}
		});
			
	}
 
}