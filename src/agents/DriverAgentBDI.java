package agents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.jcodec.common.model.Point;

import maze.DestinationPoints;
import maze.AgentsVector;
import maze.City;
import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.tutorial.IChatService;
 
@Agent
@Arguments({
	@Argument(name="control", clazz=DestinationPoints.class)
})
@Description("An agent with a persistent goal (reach goal destination).")
@ProvidedServices(@ProvidedService(type=IChatService.class, implementation=@Implementation(DriverService.class)))
@RequiredServices(@RequiredService(name="chatservices", type=IChatService.class, multiple=true, binding=@Binding(dynamic=true, scope=Binding.SCOPE_PLATFORM)))
public class DriverAgentBDI {
	
	// City
	public City maze = null;
	public DestinationPoints control = null;
	protected long startingTime = System.currentTimeMillis();
 
	@Agent
	protected BDIAgent agent;
		 
	@Belief
	protected long currentTime = this.getCurrentTime();
	
	@Belief(updaterate=1000)
	protected Point currentPosition = this.updateCurrentPosition();
	
	
	
	public BDIAgent getAgent() {
		return this.agent;
	}
	
	public Point getCurrentPosition() {
		return this.currentPosition;
	}
	
	public long getStartingTime() {
		return this.startingTime;
	}
	
	public Point updateCurrentPosition() {
		if ( this.maze != null && this.maze.current != null )
			return new Point(this.maze.current.x, this.maze.current.y);
		
		return null;
	}
 
	public long getCurrentTime() {
		return System.currentTimeMillis();
	}
	
	public void log(String text, boolean print)
	{
		// Create log file
		try {
			// Create folder
			new File("logs").mkdir();

			// Create file
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("logs/" + agent.getServiceContainer().getId().toString() + ".txt", true)));
			
			// Get date
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			
			// Write and close
			out.println(dateFormat.format(date));
			out.println(text);
			out.close();	
		} catch (IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ( print ) System.out.println(text);
	}
 
	@AgentBody
	public void body() {		
		
		// Control
		this.control = (DestinationPoints) agent.getArgument("control");
		
		// Generate city
		maze = new City(this);
		log(this.maze.draw(), false);
			
		// Add to vect
		AgentsVector.agents.add(this);
		
		// Say spawn position
		sendMessage("world|null|move|" + maze.current.x + "|" + maze.current.y);
				
		// Ask for discovered block roads
		sendMessage("drivers|null|discovered");
				
		// BDI agent
		agent.dispatchTopLevelGoal(new DriverAgentGoal()).get();
		log("Finished " + agent.getServiceContainer().getId().toString(), true);
	}
	
	@Goal(recur=true)
	public class DriverAgentGoal {
	 
		@GoalRecurCondition(beliefs="currentPosition")
		public boolean checkRecur(ChangeEvent event) {
			//System.out.println("Keeping goal at " + (long) event.getValue());
			return true;
		}
	 
	}
	 
	@Plan(trigger=@Trigger(goals=DriverAgentGoal.class))
	public class FailingPlan {
 
		@PlanBody
		protected void failingPlan() {
			//System.out.println("Attempt at " + getCurrentTime());
			
			// Wait 2 secounds before begin (receive block positions from chat)
			if ( (getCurrentTime() - getStartingTime()) < 2000 )
			{
				throw new PlanFailureException();
			}
			// Check if not in destination
			else if 
			( 
					getCurrentPosition() == null 
					|| getCurrentPosition().getX() != maze.destinations.firstElement().x 
					|| getCurrentPosition().getY() != maze.destinations.firstElement().y 
			) 
			{
				boolean moved = maze.moveCloser();
				log(maze.draw(), false);
				
				// If not moved no position possible is available so exit
				if ( moved )
				{
					// Say we moved to position
					sendMessage("world|null|move|" + maze.current.x + "|" + maze.current.y);
															
					// Throw PlanFailureException
					throw new PlanFailureException();	
				}
			}
			// Reached destination (see if there is more to go)
			else
			{				
				// Remove and go to the next one
				if ( maze.destinations.size() > 1 )
				{
					maze.destinations.remove(0);
					
					// Throw PlanFailureException
					throw new PlanFailureException();
				}
			}
		}
 
		@PlanPassed
		public void passed() {
			log("Plan finished at " + getCurrentTime() + " in " + (getCurrentTime()-getStartingTime())/1000 + "s", true);
		}
 
	}
	
	public void sendMessage(final String text)
	{
		// Log
		log("OUT " + text, false);
		
		IFuture<Collection<IChatService>> chatservices = agent.getServiceContainer().getRequiredServices("chatservices");
		chatservices.addResultListener(new DefaultResultListener<Collection<IChatService>>() {
			public void resultAvailable(Collection<IChatService> result) {
				for(IChatService cs : result) {
					cs.message(agent.getServiceContainer().getId().toString(), text);
				}
			}
		});
			
	}
 
}