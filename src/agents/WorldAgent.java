package agents;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.tutorial.IChatService;

@Agent
@Description("An agent with a chat service.")
@ProvidedServices(@ProvidedService(type=IChatService.class, implementation=@Implementation(WorldService.class)))
@RequiredServices(@RequiredService(name="chatservices", type=IChatService.class, multiple=true, binding=@Binding(dynamic=true, scope=Binding.SCOPE_PLATFORM)))
public class WorldAgent {

}
