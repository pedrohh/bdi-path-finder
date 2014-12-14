package graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import maze.AgentsVector;
import maze.City;
import maze.Node;

/**
 * Interprets a maze and draws in on the screen
 *
 */
@SuppressWarnings("serial")
public class MatrixDrawer extends JPanel {
	
	private BufferedImage voidImage, roadImage, blockedImage, houseImage, carImage, goalImage;
	public final static int CELL_HEIGHT = 25, CELL_WIDTH = 25; // the size of																// each cell
	public int maze[][] = null;
	HashMap<Integer, BufferedImage> imageMap;

	public int[][] getMaze() {
		return maze;
	}
	
	public void updatePositions()
	{
		 // Reset car position
		 for (int i=0; i<this.maze.length; i++)
		 {
			 for( int j=0; j<this.maze[i].length; j++ )
			 {
				if ( this.maze[i][j] == City.CAR || this.maze[i][j] == City.GOAL )  this.maze[i][j] = City.ROAD;
			 }
		 }
		 
		 // Set goal position
		 for ( int i=0; i<AgentsVector.agents.size(); i++)
		 {
			 Node n = AgentsVector.agents.get(i).maze.destinations.firstElement();
			 this.maze[n.y][n.x] = City.GOAL; 
		 }	
		 
		 // Set car position
		 for ( int i=0; i<AgentsVector.agents.size(); i++)
		 {
			 Node n = AgentsVector.agents.get(i).maze.current;
			 this.maze[n.y][n.x] = City.CAR; 
		 }		 
	}
	
	public void setBlockPosition(int x, int y)
	{
		this.maze[y][x] = City.CLOSED_ROAD;
	}

	public MatrixDrawer(int[][] m) {
		super();
		
		// Creates
		this.maze = new int[m.length][];
		
		// Copy m
		for(int i=0; i<m.length; i++)
		{
			this.maze[i] = new int[m[i].length];
			
			for(int j=0; j<m[i].length; j++)
			{	
				this.maze[i][j] = m[i][j];
			}
		}
		
		try {
			voidImage = resizeImage(ImageIO.read(getClass().getResource("/images/void.png")), null);
			roadImage = resizeImage(ImageIO.read(getClass().getResource("/images/road.png")), null);
			blockedImage = resizeImage(ImageIO.read(getClass().getResource("/images/blocked_road.png")), roadImage);
			houseImage = resizeImage(ImageIO.read(getClass().getResource("/images/house.png")), roadImage);
			carImage = resizeImage(ImageIO.read(getClass().getResource("/images/car.png")), roadImage);
			goalImage = resizeImage(ImageIO.read(getClass().getResource("/images/goal.png")), roadImage);
		} catch (IOException e) {
			System.out.println("Error reading image");
		}
		imageMap = new HashMap<Integer, BufferedImage>();
		imageMap.put(City.ROAD, roadImage);
		imageMap.put(City.CLOSED_ROAD, blockedImage);
		imageMap.put(City.POI, houseImage);
		imageMap.put(City.CAR, carImage);
		imageMap.put(City.GOAL, goalImage);
	}
	
	public static int getCellHeight() {
		return CELL_HEIGHT;
	}

	public static int getCellWidth() {
		return CELL_WIDTH;
	}

	private static BufferedImage resizeImage(BufferedImage originalImage, BufferedImage background) {

		BufferedImage resizedImage = new BufferedImage(CELL_WIDTH, CELL_HEIGHT, originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		
		if ( background != null ) g.drawImage(background, 0, 0, CELL_WIDTH, CELL_HEIGHT, null);
		
		g.drawImage(originalImage, 0, 0, CELL_WIDTH, CELL_HEIGHT, null);
		g.dispose();
		return resizedImage;
	}

	private void drawImg(Graphics2D g2d, Image img, int x, int y) {
		g2d.drawImage(img, y * CELL_WIDTH, x * CELL_HEIGHT, null);
	}

	private void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for(int x = 0; x < maze.length; x++){
			for(int y = 0; y < maze[x].length; y++){
				BufferedImage currentImage = imageMap.get(maze[x][y]);
				if(currentImage != null){
					drawImg(g2d, currentImage, x, y);
				}else drawImg(g2d, voidImage, x, y);
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		doDrawing(g);
	}
}
