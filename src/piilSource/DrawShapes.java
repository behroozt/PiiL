/*  
    PiiL: Pathways Interactive vIsualization tooL
    Copyright (C) 2016  Behrooz Torabi Moghadam

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
