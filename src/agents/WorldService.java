package agents;

import java.util.Collection;
import org.jcodec.common.model.Point;
import maze.Application;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.tutorial.IChatService;
 
@Service
public class WorldService implements IChatService {
 
	@ServiceComponent
	protected IInternalAccess agent;
	
	private String ownId = null;
	
	public void message(final String sender, final String text) {
		
		// First run
		if ( ownId == null ) ownId = agent.getServiceContainer().getId().toString();
		
		//System.out.println(ownId + ": received from " + sender + " message " + text);
		String responseArray[] = text.split("\\|");
						
		// Ignore own messages
		if ( !ownId.equals(sender) && !ownId.equals(responseArray[1]) )
		{									
			// My channels (own id)
			if ( "world".equals(responseArray[0]) ) 
			{				
				switch(responseArray[2]) 
				{
					case "move":
						
						//System.out.println("Received move event: " + responseArray[3] + "|" + responseArray[4]);
						
						int currentX = Integer.parseInt(responseArray[3]), currentY = Integer.parseInt(responseArray[4]);
						double distance = Integer.MAX_VALUE;
												
						// Check radius
						for( Point p : Application.blocks )
						{
							distance = Math.sqrt(Math.pow(p.getX()-currentX, 2) + Math.pow(p.getY()-currentY, 2));
							
							// Radius of 1 send road closed
							if ( distance <= 1 )
							{						
								sendMessage(sender + "|null|closed|" + p.getX() + "|" + p.getY());		
							}
						}
						
						// Update gui position (moved received)
						if ( Application.gui != null ) Application.gui.updatePositions();
						
						break;
				
				}
			}
		}
	}
	
	public void sendMessage(final String text)
	{
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