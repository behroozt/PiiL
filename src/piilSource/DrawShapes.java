package piilSource;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JComponent;


public class DrawShapes extends JComponent{
	
	Graphics2D graphical;
//	int tabIndex = Interface.tabPane.getSelectedIndex();
	List<Shape> drawings;
	static int numOfRegions;
	static List<List<String>> values;
	static Rectangle2D theBounds;
	List<Edges> edges;
	
	public DrawShapes(List<Shape> graphicsItems, List<Edges> edgeItems) {
		drawings = graphicsItems;
		edges = edgeItems;
	}
	
	public void paint(Graphics g){
		graphical = (Graphics2D) g;
		
		graphical.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	
		
		//draw edges
		
		for (Edges eachEdge : edges){
			
			Shape theEdge = eachEdge.getEdgeLine();
			graphical.setStroke(eachEdge.getStroke());
			
			Color edgeColor = eachEdge.getEdgeColor();
			graphical.setColor(edgeColor);
			graphical.draw(theEdge);
		}
		
		graphical.setColor(Color.BLACK);
		graphical.setStroke(new BasicStroke());
		
		for (Shape item : drawings){
			graphical.draw(item);
		}
	}
	
	

}
