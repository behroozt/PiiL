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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.freehep.graphicsio.swf.SWFAction.GetVariable;
import org.jfree.ui.HorizontalAlignment;

public class PiiLgridMaker {
	
	private File loadedFile;
	Character loadSource;
	ArrayList<String> genes;
	HashMap<String, Nodes> nodeHandler;
	HashMap<String, Genes> geneHandler;
	int geneX;
	int geneY;
	int geneWidth = 65;
	int geneHeight = 25;
	int verticalGap = 15;
	int horizontalGap = 10;
	int gridStartX = 30;
	int gridStartY = 30;
	float maxX, maxY;
	int numOfColumns = 10;
	int numOfRows;
	String tabCaption = "PiiLgrid";
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	
	PiiLgridMaker(File file){
		
		if (Interface.tabInfoTracker == null){
        	Interface.tabInfoTracker = new ArrayList<List<TabsInfo>>();
        }
		genes = new ArrayList<String>();
		nodeHandler = new HashMap<String, Nodes>();
		geneHandler = new HashMap<String, Genes>();
		loadedFile = file;
		loadSource = 'G';
		tabCaption = file.getName();
		try {
			makeNodes(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		generatePiiLgrid();
	}
	
	public PiiLgridMaker(ArrayList<String> genesList) {
		genes = new ArrayList<String>();
		nodeHandler = new HashMap<String, Nodes>();
		geneHandler = new HashMap<String, Genes>();
		loadSource = 'L';
		makeNodes(genesList);
		generatePiiLgrid(genesList);
	}

	private void generatePiiLgrid(ArrayList<String> genesList) {
		if (nodeHandler.size() > 0){
			openNewTab(tabCaption);
			makeLabels();
		}
		
		int tabIndex = Interface.tabPane.getSelectedIndex();
		TabsInfo pathway = Interface.tabInfoTracker.get(tabIndex).get(0);
		pathway.setNodes(nodeHandler);
		pathway.setGenes(geneHandler);
		pathway.setMaxX(maxX);
		pathway.setMaxY(maxY);
		pathway.setGridGenesList(genesList);
		
			
		for (Entry<String, Genes> node : geneHandler.entrySet()){
			JLabel element = node.getValue().getLabel();
			Interface.panelHolder.get(tabIndex).add(element);
		}
		
		JLabel PiiLLogo = new JLabel("", icon, JLabel.CENTER);
		PiiLLogo.setVisible(false);
		Interface.panelHolder.get(tabIndex).add(PiiLLogo,BorderLayout.CENTER);
		
		Interface.panelHolder.get(tabIndex).setPreferredSize((new Dimension((int) maxX,(int) maxY)));
		
		Interface.scrollPaneHolder.get(tabIndex).getVerticalScrollBar().setUnitIncrement(16);
		Interface.scrollPaneHolder.get(tabIndex).getHorizontalScrollBar().setUnitIncrement(16);
		
	}

	private void generatePiiLgrid() {
		
		if (nodeHandler.size() > 0){
			openNewTab(tabCaption);
			makeLabels();
		}
		
		int tabIndex = Interface.tabPane.getSelectedIndex();
		TabsInfo pathway = Interface.tabInfoTracker.get(tabIndex).get(0);
		pathway.setNodes(nodeHandler);
		pathway.setGenes(geneHandler);
		pathway.setMaxX(maxX);
		pathway.setMaxY(maxY);
		
			
		for (Entry<String, Genes> node : geneHandler.entrySet()){
			JLabel element = node.getValue().getLabel();
			Interface.panelHolder.get(tabIndex).add(element);
		}
		
		JLabel PiiLLogo = new JLabel("", icon, JLabel.CENTER);
		PiiLLogo.setVisible(false);
		Interface.panelHolder.get(tabIndex).add(PiiLLogo,BorderLayout.CENTER);
		
		Interface.panelHolder.get(tabIndex).setPreferredSize((new Dimension((int) maxX,(int) maxY)));
		
		Interface.scrollPaneHolder.get(tabIndex).getVerticalScrollBar().setUnitIncrement(16);
		Interface.scrollPaneHolder.get(tabIndex).getHorizontalScrollBar().setUnitIncrement(16);
	}
	
	private void makeLabels() {
		
		String[] nameAlternatives = null;
		for (Entry<String, Nodes> oneNode : nodeHandler.entrySet()){
			
			final String nodeID = oneNode.getKey();
			final JLabel theLabel = new JLabel(oneNode.getValue().getNodeLabel(),SwingConstants.CENTER);
			final String geneName = oneNode.getValue().getNodeLabel();
			final String tip = oneNode.getValue().getNodeLabel();
			
			theLabel.setOpaque(true);
			theLabel.setBackground(Color.LIGHT_GRAY);
			theLabel.setBounds((int)oneNode.getValue().getNodeCordX(),(int)oneNode.getValue().getNodeCordY(),
					(int)oneNode.getValue().getNodeWidth(),(int)oneNode.getValue().getNodeHeight());
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
			nameAlternatives = oneNode.getValue().getNodeLabel().toString().split(",");
			Genes geneNodeLabel = new Genes(theLabel, oneNode.getValue().getNodeLabel().toString(), nameAlternatives, false, false);
			geneHandler.put(oneNode.getKey(), geneNodeLabel);
		}
	}

	private void makeNodes(List<String> genesList) {
		
		String line = null;
		int geneTracker = 0;
		int colPointer = 1;
		int rowPointer = 1;
		Nodes newEntry;
		Shape nodeShape = null;
		int horizontalSpace = geneWidth + horizontalGap;
		int verticalSpace = geneHeight + verticalGap;
		
		
		for (int i = 0; i < genesList.size() ; i++){
			line = genesList.get(i);
			if (line.startsWith("#")){
				Pattern columnPattern = Pattern.compile("#([\\d]+)");
				Matcher matcher = columnPattern.matcher(line);
				if (matcher.matches()) {
				    numOfColumns = Integer.parseInt(matcher.group(1));
				}
			}
			else if (line.startsWith("<")){
				Pattern spacePattern = Pattern.compile("<>");
				Pattern returnPattern = Pattern.compile("<br>");
				Matcher matcherSpace = spacePattern.matcher(line);
				Matcher matcherReturn = returnPattern.matcher(line);
				if (matcherSpace.matches()) {
				    colPointer ++;
				    if (colPointer > numOfColumns){
				    	colPointer = 1;
				    }
				}
				else if (matcherReturn.matches()){
					rowPointer ++;
					colPointer = 1;
				}
			}
			else {
				geneTracker ++;
				String id = Integer.toString(geneTracker);
				geneX = ((colPointer -1) * horizontalSpace) + (colPointer * gridStartX);
				geneY = ((rowPointer -1) * verticalSpace) + (rowPointer * gridStartY);
				nodeShape = new Rectangle2D.Float(geneX , geneY, geneWidth, geneHeight);
				newEntry = new Nodes(geneX , geneY, geneWidth, geneHeight, "Color.gray", "Color.black", line, "PiiLnet", null, line);
				newEntry.setGraphicShape(nodeShape);
				nodeHandler.put(id, newEntry);
				colPointer ++;
				if (colPointer > numOfColumns){
					colPointer = 1;
					rowPointer ++;
				}
			}
				
		}
		
		maxX = gridStartX + ((numOfColumns+1) * horizontalSpace);
		if (maxX > 800)
			maxX = Interface.tabPane.getWidth() + horizontalSpace;
		maxY = gridStartY + ((rowPointer+1) * verticalSpace);
		if (maxY > 600)
			maxY = Interface.tabPane.getHeight() + verticalSpace;
		
	}
	
	private void makeNodes(File file) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		int geneTracker = 0;
		int colPointer = 1;
		int rowPointer = 1;
		Nodes newEntry;
		Shape nodeShape = null;
		int horizontalSpace = geneWidth + horizontalGap;
		int verticalSpace = geneHeight + verticalGap;
		ArrayList<String> genes = new ArrayList<String>();
		
		while ((line = br.readLine()) != null){
			
			if (line.startsWith("#")){
				Pattern columnPattern = Pattern.compile("#([\\d]+)");
				Matcher matcher = columnPattern.matcher(line);
				if (matcher.matches()) {
				    numOfColumns = Integer.parseInt(matcher.group(1));
				}
			}
			else if (line.startsWith("<")){
				Pattern spacePattern = Pattern.compile("<>");
				Pattern returnPattern = Pattern.compile("<br>");
				Matcher matcherSpace = spacePattern.matcher(line);
				Matcher matcherReturn = returnPattern.matcher(line);
				if (matcherSpace.matches()) {
				    colPointer ++;
				    if (colPointer > numOfColumns){
				    	colPointer = 1;
				    }
				}
				else if (matcherReturn.matches()){
					rowPointer ++;
					colPointer = 1;
				}
			}
			else if (line.startsWith("Gene,")){
				continue;
			}
			else {
				if (genes.contains(line.split(",")[0])){
					continue;
				}
				else{
					genes.add(line.split(",")[0]);
				}
				geneTracker ++;
				String id = Integer.toString(geneTracker);
				geneX = ((colPointer -1) * horizontalSpace) + (colPointer * gridStartX);
				geneY = ((rowPointer -1) * verticalSpace) + (rowPointer * gridStartY);
				nodeShape = new Rectangle2D.Float(geneX , geneY, geneWidth, geneHeight);
				newEntry = new Nodes(geneX , geneY, geneWidth, geneHeight, "Color.gray", "Color.black", line.split(",")[0], "PiiLgrid", null, line.split(",")[0]);
				newEntry.setGraphicShape(nodeShape);
				nodeHandler.put(id, newEntry);
				colPointer ++;
				if (colPointer > numOfColumns){
					colPointer = 1;
					rowPointer ++;
				}
			}
				
		}
		br.close();
		
		maxX = gridStartX + ((numOfColumns+1) * horizontalSpace);
		if (maxX > 800)
			maxX = Interface.tabPane.getWidth() + horizontalSpace;
		maxY = gridStartY + ((rowPointer+1) * verticalSpace);
		if (maxY > 600)
			maxY = Interface.tabPane.getHeight() + verticalSpace;
		
	} // end of makeNodes(from file)

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
        TabsInfo newOne = new TabsInfo(newTabCaption, loadedFile, loadSource, nameWithoutExtension);
        List<TabsInfo> newList = new ArrayList<TabsInfo>();
        newList.add(newOne);
        Interface.tabInfoTracker.add(newList);
	}
	
	private String extractName(String name) {
		int pos = name.lastIndexOf(".");
		if (pos == -1) {
			return name;
		} else {
			return name.substring(0, pos);
		}
	}
}

