package maze;

import graphics.Gui;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcodec.common.model.Point;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

public class Application {
	
	// City array
	public static int[][] driversMaze = null;
	public static Vector<DestinationPoints> agentPoints = new Vector<DestinationPoints>();
	public static Vector<Point> blocks = new Vector<Point>();
	public static Gui gui = null;

 	public static void main(String [] args)
 	{
 		System.out.println("Started from main..");
 		boolean withGui = true;
 				
 		// Deal with input
 		if ( args.length >= 2 )
 		{
 			if ( args[0].equals("-gui") && args[1].equals("false") ) withGui = false;	
 		}
		
 		// Load 
 		Application.loadConfigFile("map.proprietes", withGui);
 		
 		// Thread
 		ThreadSuspendable sus = new ThreadSuspendable();
 		
 		// Config (gui==false)
 		String[] defargs = new String[]
		{
		  "-gui", "false",
		  "-welcome", "false",
		  "-cli", "false",
		  "-printpass", "false"
		};
 		
 		String[] newargs = new String[defargs.length];
 		//String[] newargs = new String[defargs.length+args.length];
 		System.arraycopy(defargs, 0, newargs, 0, defargs.length);
 		//System.arraycopy(args, 0, newargs, defargs.length, args.length);
 		
 		 // The interface for accessing components from the outside.
 		//IExternalAccess pl = Starter.createPlatform(new String[0]).get(sus);
 		IExternalAccess pl = Starter.createPlatform(newargs).get(sus);
	   
 		// General interface for components that the container can execute.
 		IComponentManagementService cms = SServiceProvider.getService(pl.getServiceProvider(), 
			   IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
		 
	    // Creates WorldAgent and DriverAgentBDI
		IComponentIdentifier agentCID = cms.createComponent("agents/WorldAgent.class", null).getFirstResult(sus);
		System.out.println("Started WorldAgent: " + agentCID);
		
		// Spawn drivers
		for ( DestinationPoints dst : Application.agentPoints )
		{
			Map<String, Object> agentArgs = new HashMap<String, Object>();
		    agentArgs.put("control", dst);
		    CreationInfo cInfo = new CreationInfo(agentArgs);
		    
			agentCID = cms.createComponent("agents/DriverAgentBDI.class", cInfo).getFirstResult(sus);
 			System.out.println("Started DriverAgentBDI: " + agentCID);
		}
 	}

 	public static void loadConfigFile(String file, boolean withGui)
 	{
		BufferedReader br = null;
		 
		try {
			Vector<String> map = new Vector<String>();
			Vector<String> points = new Vector<String>();
			
			String currentLine = "";
			Pattern MAP_PATTERN = Pattern.compile("^[A-Z0-9-]+$");
			Pattern POINTS_PATTERN = Pattern.compile("^[A-Z0-9,=|]+$");
 
			br = new BufferedReader(new FileReader(file));
 
			while ( (currentLine = br.readLine()) != null ) 
			{
				// Remove whitespace
				currentLine = currentLine.replaceAll("\\s+", "");
				
				// Matcher
				Matcher mapMatcher = MAP_PATTERN.matcher(currentLine);
				Matcher pointsMatcher = POINTS_PATTERN.matcher(currentLine);
				
				if ( mapMatcher.find() )
				{
					map.add( currentLine ); 
					//System.out.println("Map: " + currentLine);
				}
				else if ( pointsMatcher.find() )
				{
					points.add( currentLine );
					//System.out.println("Points: " + currentLine);
				}

			}
						
			// Parse  map
			Application.driversMaze = new int[map.size()][];
			
			for(int i=0; i<map.size(); i++)
			{
				String s = map.get(i);
				Application.driversMaze[i] = new int[s.length()];
				
				for(int j=0; j<s.length(); j++)
				{
					char c = s.charAt(j);
					
					// A-Z
					if ( c >= 65 && c <= 90 )
					{
						agentPoints.add( new DestinationPoints(String.valueOf(c), j, i) );
					}
					// -
					else if ( c == 45 )
					{
						blocks.add( new Point(j, i) );
					}
					 
					// Add to drivers maze
					Application.driversMaze[i][j] = (Character.getNumericValue( s.charAt(j) ) == 1 ? 1 : 0);
				}
			}
			
			// Initialize gui and copy maze
			if ( withGui ) Application.gui = new Gui(Application.driversMaze);

			// Parse points
			for( String point : points )
			{
				String control[] = point.split("\\=");
				
				if ( control.length == 2 )
				{
					DestinationPoints agent = DestinationPoints.getAgent(agentPoints, control[0]);

					if ( agent == null )
					{
						System.out.println("Parse: agent not found => " + control[0]);
						System.exit(1);
					}
					else
					{
						String pointsArr[] = control[1].split("\\,");
						
						for( String s : pointsArr )
						{
							String numberArr[] = s.split("\\|");
							
							if ( numberArr.length == 2 )
							{
								int x = Integer.parseInt(numberArr[0]), y = Integer.parseInt(numberArr[1]);
								
								if ( 
										y >= 0 && y < Application.driversMaze.length && x >= 0 && x < Application.driversMaze[y].length
										&& Application.driversMaze[y][x] == City.ROAD								
									)
								{
									agent.points.add( new Point(x, y) );
								}
								else 
								{
									System.out.println("Parse: point error not road => " + x + "|" + y);
									System.exit(1);
								}	
							}
							else
							{
								System.out.println("Parse: numberArr error size => " + numberArr.length + " (str: " + s + ")");
								System.exit(1);
							}
						}
					}	
				}
				else
				{
					System.out.println("Parse: control error size => " + control.length + " (str: " + point + ")");
					System.exit(1);
				}
			}
			
			// Check if all agents has points
			for( DestinationPoints dst : agentPoints )
			{
				if ( dst.points.size() == 0 )
				{
					System.out.println("Agent " + dst.name + " dont has destination points");
					System.exit(1);
				}
			}			
			
			System.out.println("Config file read..");
		} catch (IOException e) {
			//e.printStackTrace();
			
			PrintWriter writer;
			
			try {
				// Write default file
				writer = new PrintWriter("map.proprietes", "UTF-8");
				writer.println("% use char %'s to comment");
				writer.println("");
				writer.println(" Mapa exemplo 1");
				writer.println("1 A 1 0 1 0 1 B 1 0 1 C 1 0 1 ");
				writer.println("0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 ");
				writer.println("1 0 1 0 1 0 1 0 1 0 1 0 1 0 1 ");
				writer.println("0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 ");
				writer.println("1 0 1 0 1 0 1 0 1 0 1 0 1 0 1");
				writer.println("0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ");
				writer.println("1 0 1 0 1 0 1 0 1 0 1 0 1 0 1 ");
				writer.println("0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 ");
				writer.println("1 0 1 0 1 0 1 0 1 0 1 0 1 0 1 ");
				writer.println("0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ");
				writer.println("1 0 1 0 1 0 1 0 1 0 1 0 1 0 1");
				writer.println("0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
				writer.println("1 0 1 0 1 0 1 0 1 0 1 0 1 0 1 ");
				writer.println("");
				writer.println("A=1|12");
				writer.println("B=1|12");
				writer.println("C=1|12");
				writer.println("");
				writer.println("%1111111111111");
				writer.println("%0A-C000000-0B");
				writer.println("%0-00000000000");
				writer.println("%00000000000-0");
				writer.println("%1111111111111");
				writer.println("");
				writer.println("%A=1|3,6|3,12|3");
				writer.println("%B=6|3");
				writer.println("%C=1|1");
				writer.close();
				
				System.out.println("No map.proprietes found, a default has been created");
				System.out.println("Edit at your will and run the program again");
				Application.loadConfigFile("map.proprietes", withGui);
				return;
			} catch (FileNotFoundException | UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} finally {
			
			try {
				if (br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		}	
 	}
 	
}
