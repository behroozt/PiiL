/*  
    PiiL: Pathway Interactive vIsualization tooL
    Copyright (C) 2015  Behrooz Torabi Moghadam

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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ParseKGML {
	
	float maxX, maxY, maxWidth, maxHeight;
	NodeList elementsList, relationsList;
	List<Shape> graphicsItems;
	HashMap<String, Nodes> nodeHandler;
	HashMap<String, Genes> geneHandler;
	List<Edges> edgeItems;
	static ArrayList<TabsInfo> tabInfoTracker;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/logoIcon.png"));
	private String caption;
	private File loadedFile;
	Character loadSource;

	public ParseKGML(Document xmlDoc, String tabCaption, File file, Character source) {
		if (tabInfoTracker == null){
        	tabInfoTracker = new ArrayList<TabsInfo>();
        }
		geneHandler = new HashMap<String, Genes>();
		edgeItems = new ArrayList<Edges>();
		nodeHandler = new HashMap<String, Nodes>();
		graphicsItems = new ArrayList<Shape>();
		caption = tabCaption;
		loadedFile = file;
		loadSource = source;
		parse(xmlDoc);
	}

	public static TabsInfo getTabInfo(int selectedTab){
		return tabInfoTracker.get(selectedTab);
	}
	
	public static void closedTab(int selectedTab){
		tabInfoTracker.remove(selectedTab);
	}

	private void parse(Document kgmlInput) {
		
		maxX = 0; maxWidth =0; maxY=0; maxHeight=0;
		
		elementsList = kgmlInput.getElementsByTagName("entry");
		relationsList = kgmlInput.getElementsByTagName("relation");
		
		if (elementsList.getLength() > 0){
			
			openNewTab(caption);
			int tabIndex = Interface.tabPane.getSelectedIndex();
			makeNodes(elementsList);
			makeLabels(elementsList);
			makeEdges(relationsList);
			
			TabsInfo pathway = tabInfoTracker.get(tabIndex);
			pathway.setNodes(nodeHandler);
			pathway.setGenes(geneHandler);
			pathway.setDocument(kgmlInput);
			pathway.setEdgeItems(edgeItems);
			pathway.setGraphicsItems(graphicsItems);
			pathway.setMaxX(maxX);
			pathway.setMaxY(maxY);
			
			for (Entry<String, Genes> node : geneHandler.entrySet()){
				JLabel element = node.getValue().getLabel();
				Interface.panelHolder.get(tabIndex).add(element,BorderLayout.CENTER);
			}
	        DrawShapes shapes = new DrawShapes(graphicsItems,edgeItems);
	        shapes.setPreferredSize(new Dimension((int) maxX,(int) maxY));
			Interface.panelHolder.get(tabIndex).add(shapes,BorderLayout.CENTER);
	        Interface.scrollPaneHolder.get(tabIndex).getVerticalScrollBar().setUnitIncrement(16);
			Interface.scrollPaneHolder.get(tabIndex).getHorizontalScrollBar().setUnitIncrement(16);
			
			
		}
		else {
			JOptionPane.showMessageDialog(Interface.bodyFrame, "There is a problem with the input file. Please check it or try a different one.","Error",0,icon);
		}
				
	}
	

	private void openNewTab(String newTabCaption) {
		
		JPanel newPanel = new JPanel();
        newPanel.setLayout(new BorderLayout());
        newPanel.setBackground(Color.WHITE);
        Interface.addNewPanel(newPanel);
        JScrollPane newScrollPane = new JScrollPane(newPanel);
        newScrollPane.setBackground(Color.WHITE);
        Interface.addNewScrollPane(newScrollPane);
        
        Interface.tabPane.addTab(newTabCaption,newScrollPane);
        int tabIndex = Interface.tabPane.getTabCount() - 1;
        Interface.tabPane.setSelectedIndex(tabIndex);
        String nameWithoutExtension = extractName(newTabCaption);
        tabInfoTracker.add(new TabsInfo(newTabCaption, loadedFile, loadSource, nameWithoutExtension));
	}

	private void makeLabels(NodeList allNodes) {
		
		String nodeLabel = null,nodeType;
		String[] nameAlternatives;
		int labelX, labelY,nodeWidth, nodeHeight, labelWidth,labelHeight;
		
		for (Entry<String, Nodes> oneNode : nodeHandler.entrySet()){
			
			Nodes value = (Nodes) oneNode.getValue();
		    final String nodeID = oneNode.getKey();
							
			nodeType = value.getNodeType();
			
			if (nodeType.equals("group")){
				continue;
			}
			labelX = (int) value.getNodeCordX();
			labelY = (int) value.getNodeCordY();
			nodeWidth = (int) value.getNodeWidth();
			nodeHeight = (int) value.getNodeHeight();
			
			nameAlternatives = value.getNodeLabel().split(",");
			nodeLabel = nameAlternatives[0];
			final JLabel theLabel = new JLabel(nodeLabel,SwingConstants.CENTER);
			labelWidth = nodeLabel.length() * 10;
			labelHeight = 10;
			
			if (nodeType.equals("compound")){
				theLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
				
				labelY += 10;
				labelX -= 10;
			}
			theLabel.setBounds(labelX, labelY, labelWidth, nodeHeight);
			theLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
			if (nodeType.equals("map")) {
				if ((nodeLabel.length() * 7) > nodeWidth ){
					theLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
				}
				labelX = labelX + (nodeWidth / 2) - (labelWidth / 2);
				theLabel.setBounds(labelX, labelY, labelWidth, nodeHeight);
			}
			if (nodeType.equals("ortholog")){
				theLabel.setOpaque(true);
				theLabel.setBackground(Color.decode(value.getNodeBgColor()));
				theLabel.setBounds(labelX, labelY, nodeWidth, nodeHeight);
				theLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				theLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
			}
			if (nodeType.equals("gene")){
				final String tip = value.getNodeLabel();
				theLabel.setOpaque(true);
				theLabel.setBackground(Color.decode(value.getNodeBgColor()));
				theLabel.setBounds(labelX, labelY, nodeWidth, nodeHeight);
				theLabel.addMouseListener(new MouseListener() {
					
					public void mouseReleased(MouseEvent arg0) {
						
					}
					
					public void mousePressed(MouseEvent mp) {
						
					}
					
					public void mouseExited(MouseEvent arg0) {
						
					}
					
					public void mouseEntered(MouseEvent me) {
						theLabel.setToolTipText(tip);
					}
					
					public void mouseClicked(MouseEvent mc) {
						int x = (int) (mc.getX() + theLabel.getBounds().getX());
						int y = (int) (mc.getY() + theLabel.getBounds().getY());
						Point point = new Point(x,y);
						
						if (SwingUtilities.isRightMouseButton(mc)){
							new RightClickMenu(mc.getComponent(), mc.getX(), mc.getY(),nodeID, point);
						}
						else if (mc.getClickCount() == 2){
							Genes selectedNode = geneHandler.get(nodeID);
							selectedNode.setSelectedStatus(point);
						}
					}
				});
				
				theLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				theLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
//				if (labelWidth >= 50){
//					theLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
//					if (labelWidth >= 70){
//						theLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
//					}
//				}
			} // end of if a 'gene'
			
			Genes geneNodeLabel = new Genes(theLabel, nodeLabel	, nameAlternatives, false, false);
			geneHandler.put(nodeID, geneNodeLabel);
			
		} // end of for 
		
		
	} // end of makeLabels

	private void makeEdges(NodeList allEdges) {
		
		float[] dashingPattern1 = {5f, 5f};
		Stroke strokeDashed = new BasicStroke(2.5f, BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER, 2.0f, dashingPattern1, 2.0f);
		
		float arrowStartX = 0, arrowStartY = 0, arrowEndX = 0, arrowEndY = 0;
		float nodeFromX,nodeFromY, nodeToX, nodeToY, nodeFromWidth, nodeFromHeight, nodeToWidth, nodeToHeight;
		String nodeFromID, nodeToID, componentID, relationName, type, relationValue;
		Nodes component;
		Shape relationEdge;
		Stroke lineStroke = new BasicStroke();
		Color lineColor = Color.BLACK;
		
		for (int i = 0; i < allEdges.getLength(); i ++){
			nodeFromID = allEdges.item(i).getAttributes().getNamedItem("entry1").getNodeValue();
			nodeToID = allEdges.item(i).getAttributes().getNamedItem("entry2").getNodeValue();
			type = allEdges.item(i).getAttributes().getNamedItem("type").getNodeValue();
			
			NodeList attribs = allEdges.item(i).getChildNodes();
			if (attribs.getLength() == 0){
				relationName = "unknown";
				relationValue = "unknown";
			}
			else{
				relationName = allEdges.item(i).getChildNodes().item(0).getAttributes().getNamedItem("name").getNodeValue();
				relationValue = allEdges.item(i).getChildNodes().item(0).getAttributes().getNamedItem("value").getNodeValue();
			}
			
			Nodes nodeFrom = (Nodes) nodeHandler.get(nodeFromID);
			Nodes nodeTo = (Nodes) nodeHandler.get(nodeToID);
			
			if (nodeFrom.getNodeType().equals("group")){
				componentID = nodeFrom.getComponentID();
				component = (Nodes) nodeHandler.get(componentID);
				nodeFromX = component.getNodeCordX();
				nodeFromY = component.getNodeCordY();
				nodeFromWidth = component.getNodeWidth();
				nodeFromHeight = component.getNodeHeight();
			} else{
				nodeFromX = nodeFrom.getNodeCordX();
				nodeFromY = nodeFrom.getNodeCordY();
				nodeFromWidth = nodeFrom.getNodeWidth();
				nodeFromHeight = nodeFrom.getNodeHeight();
			}
			
			
			if (nodeTo.getNodeType().equals("group")){
				componentID = nodeTo.getComponentID();
				component = (Nodes) nodeHandler.get(componentID);
				nodeToX = component.getNodeCordX();
				nodeToY = component.getNodeCordY();
				nodeToWidth = component.getNodeWidth();
				nodeToHeight = component.getNodeHeight();
			} 
			else{
				nodeToX = nodeTo.getNodeCordX();
				nodeToY = nodeTo.getNodeCordY();
				nodeToWidth = nodeTo.getNodeWidth();
				nodeToHeight = nodeTo.getNodeHeight();
			}
			
			if (nodeFromX < nodeToX){
				if ((nodeToX - nodeFromX) < nodeToWidth/2){
					arrowStartX = nodeFromX + (nodeFromWidth/2) ;
					arrowStartY = nodeFromY + nodeFromHeight;
					arrowEndX = nodeToX + (nodeToWidth/2) ;
					arrowEndY = nodeToY ;
				}
				else {
					arrowStartX = nodeFromX + nodeFromWidth ;
					arrowStartY = nodeFromY + (nodeFromHeight/2);
					arrowEndX = nodeToX  ;
					arrowEndY = nodeToY + (nodeToHeight/2);
				}
			}
			else if (nodeFromX == nodeToX) {
				if (nodeFromY < nodeToY){
					arrowStartX = nodeFromX + (nodeFromWidth / 2);
					arrowStartY = nodeFromY + nodeFromHeight;
					arrowEndX = nodeToX + (nodeToWidth /2);
					arrowEndY = nodeToY;
				} 
				else {
					arrowStartX = nodeFromX + (nodeFromWidth / 2);
					arrowStartY = nodeFromY;
					arrowEndX = nodeToX + (nodeToWidth /2);
					arrowEndY = nodeToY + nodeToHeight;
				}
			} 
			else if (nodeFromX > nodeToX) {
				if ((nodeFromX - nodeToX) < nodeToWidth/2){
					arrowStartX = nodeFromX + (nodeFromWidth/2) ;
					arrowStartY = nodeFromY + nodeFromHeight;
					arrowEndX = nodeToX + (nodeToWidth/2) ;
					arrowEndY = nodeToY ;
				}
				else {
					arrowStartX = nodeFromX ;
					arrowStartY = nodeFromY + (nodeFromHeight/2);
					arrowEndX = nodeToX + nodeToWidth;
					arrowEndY = nodeToY + (nodeToHeight/2);
				}					
			}
			
			if (relationName.equals("activation")){
				lineStroke = new BasicStroke((float) 1.5);
				lineColor = Color.DARK_GRAY;		
				relationEdge = ArrowEdge.createArrow(arrowStartX, arrowStartY, arrowEndX, arrowEndY);
			}

			else {
				relationEdge = new Line2D.Float(arrowStartX, arrowStartY, arrowEndX, arrowEndY);
				if (relationName.equals("expression")){
					lineColor = new Color(0,100,0);
					lineStroke = new BasicStroke((float) 1.5);
				}
				else if (relationName.equals("inhibition")){
					lineColor = Color.blue;
//					lineStroke = new BasicStroke((float) 1.5);
					lineStroke = strokeDashed;
				}
				else if (relationName.equals("repression")){
					lineStroke = strokeDashed;
					lineColor = Color.ORANGE;
				}
				else if (relationName.equals("methylation")){
					lineColor = Color.RED;
					lineStroke = new BasicStroke((float) 1.5);
				}
				else {
					lineStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
					lineColor = Color.BLACK;
				}
			}
			Edges edge = new Edges(nodeFromID, nodeToID, type, relationName, relationValue, relationEdge, lineStroke, lineColor);
			edgeItems.add(edge);
		}
	}

	private void makeNodes(NodeList allNodes) {
		
		float x,y,width,height;
		String coords;
		Shape nodeShape = null;
		String bgColor,fgColor,entryID, entryType, graphicShape, graphicLabel, entryName, entryLink;
		Nodes newEntry;
		Stroke lineStroke = new BasicStroke();
		Color lineColor;
		
		for (int i = 0; i < allNodes.getLength(); i ++){
			
			entryType = allNodes.item(i).getAttributes().getNamedItem("type").getNodeValue();
			if (i == 0){				
				graphicLabel = elementsList.item(i).getParentNode().getAttributes().getNamedItem("title").getNodeValue();
			}
			else if (entryType.equals("group")){
				graphicLabel = "no_name";
			}
			else {
				graphicLabel = elementsList.item(i).getFirstChild().getAttributes().getNamedItem("name").getNodeValue();
			}
			
			entryID = allNodes.item(i).getAttributes().getNamedItem("id").getNodeValue();
			entryName = allNodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
			entryLink = "nolink";
			graphicShape = allNodes.item(i).getFirstChild().getAttributes().getNamedItem("type").getNodeValue();
			if(graphicShape.equals("line")){
				coords = allNodes.item(i).getFirstChild().getAttributes().getNamedItem("coords").getNodeValue();
				fgColor = allNodes.item(i).getFirstChild().getAttributes().getNamedItem("fgcolor").getNodeValue();
				String[] parts = coords.split(",");
				Shape relationEdge = ArrowEdge.createArrow(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
				Edges edge = new Edges(null, null, "line", "line", "0", relationEdge, lineStroke, Color.decode(fgColor));
				edgeItems.add(edge);
				continue;
			}
			x = Float.parseFloat(allNodes.item(i).getFirstChild().getAttributes().getNamedItem("x").getNodeValue());
			y = Float.parseFloat(allNodes.item(i).getFirstChild().getAttributes().getNamedItem("y").getNodeValue());
			width = Float.parseFloat(allNodes.item(i).getFirstChild().getAttributes().getNamedItem("width").getNodeValue());
			height = Float.parseFloat(allNodes.item(i).getFirstChild().getAttributes().getNamedItem("height").getNodeValue());
			
			bgColor = allNodes.item(i).getFirstChild().getAttributes().getNamedItem("bgcolor").getNodeValue();
			fgColor = allNodes.item(i).getFirstChild().getAttributes().getNamedItem("fgcolor").getNodeValue();
			
			if (x > maxX){ maxX = x;}
			if (y > maxY){ maxY = y;}
			if ((width > maxWidth)) { maxWidth = width;}
			if (height > maxHeight) {maxHeight = height;}
			
			if (graphicShape.equals("circle")){
				nodeShape = new Ellipse2D.Float(x, y, width, height);
				graphicsItems.add(nodeShape);
			}
			else if (graphicShape.equals("roundrectangle")){
				nodeShape = new RoundRectangle2D.Float(x, y, width, height, 10, 10);
				graphicsItems.add(nodeShape);
			}
			else {
				if (entryType.equals("group") == false){
					nodeShape = new Rectangle2D.Float(x, y, width, height);
					entryLink = allNodes.item(i).getAttributes().getNamedItem("link").getNodeValue();
				}
			}
			
			/* reocrd the nodes */
			if (entryType.equals("group")){
				String componentID = allNodes.item(i).getChildNodes().item(1).getAttributes().getNamedItem("id").getNodeValue();
				newEntry = new Nodes(x, y, width, height, bgColor, fgColor, entryName, entryType, entryLink, graphicLabel, componentID);
			}
			else {
				newEntry = new Nodes(x, y, width, height, bgColor, fgColor, entryName, entryType, entryLink, graphicLabel);
			}
			
			newEntry.setGraphicShape(nodeShape);
			nodeHandler.put(entryID, newEntry);	
			
		} // end of for (checking all nodes)

		maxX = maxX + maxWidth;
		maxY = maxY + maxHeight;
		
	} // end of makeNodes 
	
	private String extractName(String name) {
		int pos = name.lastIndexOf(".");
		if (pos == -1) {
			return name;
		} else {
			return name.substring(0, pos);
		}
	}
	

}
