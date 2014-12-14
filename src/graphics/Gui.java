package graphics;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Gui extends JFrame {
	
	public MatrixDrawer drawer = null;
	
	public Gui(int[][] m)
	{
		this.setTitle("AIAD - Path finder");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.drawer = new MatrixDrawer(m);
		this.add(drawer);
		this.setLocation(0, 0);
		this.setVisible(false);
		//this.setLocationRelativeTo(null);
		
		// Discover max width and height
		int max_width = 0, max_height = m.length;
		
		for(int i=0; i<m.length; i++)
		{
			for(int j=0; j<m[i].length; j++)
			{
				if ( m[i].length > max_width ) max_width = m[i].length;
			}
		}
		
		// Set window size
		this.setSize((max_width+1)*MatrixDrawer.CELL_WIDTH, (max_height+2)*MatrixDrawer.CELL_HEIGHT);
	}
	
	public void updatePositions()
	{
		if ( !this.isDisplayable() ) this.setVisible(true);
		
		this.drawer.updatePositions();
		this.repaint();
	}
	
	public void setBlockPosition(int x, int y)
	{
		this.drawer.setBlockPosition(x, y);
		this.repaint();
	}
}
