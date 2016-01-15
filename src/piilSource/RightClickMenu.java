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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class RightClickMenu {
	

	JPopupMenu menu;
	JMenuItem histogram,rawData,  geneRegion, geneCards, pubmed, ensembl, multipleSamples;
	JMenu geneInfo;
	String entryID;
	int activeTab = Interface.tabPane.getSelectedIndex();
	TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
	Character type = pathway.getMetaType();
	Point expansionSide;

	public RightClickMenu(Component component, int x, int y, String nodeID, Point point) {
		
		entryID = nodeID;
		expansionSide = new Point(point);
		menu = new JPopupMenu();
		geneInfo = new JMenu("Check this gene on ...");
		histogram = new JMenuItem("Histogram for all samples");
		rawData = new JMenuItem("Barplot for raw data");
		geneRegion = new JMenuItem("Genomic region details");
		multipleSamples = new JMenuItem("Show multiple-sample view");
		geneCards = new JMenuItem("GeneCards");
		pubmed = new JMenuItem("Pubmed");
		ensembl = new JMenuItem("Ensembl");
		menu.add(geneInfo);
		menu.add(histogram);
		menu.add(rawData);
		menu.add(geneRegion);
		menu.add(multipleSamples);
		geneInfo.add(geneCards);
		geneInfo.add(pubmed);
		geneInfo.add(ensembl);
		menu.show(component, x,y);
		
		
		if (pathway.getMapedGeneLabel().size() > 0){
			Genes gene = pathway.getMapedGeneLabel().get(entryID);
			if (gene == null) {
				histogram.setEnabled(false);
				rawData.setEnabled(false);
				geneRegion.setEnabled(false);
				multipleSamples.setEnabled(false);
			}
			else {
				if (gene.getExpandStatus()){
					multipleSamples.setText("Single-sample view");
				}
				else {
					multipleSamples.setText("Multipe-sample view");
				}
			}
			if (pathway.getMapedGeneData().size() == 0){
				histogram.setEnabled(false);
				rawData.setEnabled(false);
				geneRegion.setEnabled(false);
				multipleSamples.setEnabled(false);
			}
		}
		else {
			histogram.setEnabled(false);
			rawData.setEnabled(false);
			geneRegion.setEnabled(false);
			multipleSamples.setEnabled(false);
		}
		
		ListenForClick lForClick = new ListenForClick();
		geneCards.addActionListener(lForClick);
		histogram.addActionListener(lForClick);
		rawData.addActionListener(lForClick);
		geneRegion.addActionListener(lForClick);
		pubmed.addActionListener(lForClick);
		ensembl.addActionListener(lForClick);
		multipleSamples.addActionListener(lForClick);
	}
	
	private class ListenForClick implements ActionListener{
		
		String geneName = pathway.getGenes().get(entryID).getText();
		String URL;
		
		public void actionPerformed(ActionEvent mc) {
			
			if (mc.getSource() == histogram ){
				new Histogram(geneName,pathway.getMapedGeneData().get(entryID), type);	
			}
			else if (mc.getSource() == rawData){
				new BarChart(geneName,pathway.getMapedGeneData().get(entryID),type);				
			} // end of rawData
			
			else if (mc.getSource() == geneCards){
				
				URL = "http://www.genecards.org/cgi-bin/carddisp.pl?gene=" + geneName;
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Unable to browse GeneCards website!");
				}
			} // end of geneCards
			
			else if (mc.getSource() == pubmed){

				URL = "http://www.ncbi.nlm.nih.gov/pubmed/?term=" + geneName;
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Unable to browse PubMed website!");
				}
			} // end of pubmed
			
			else if (mc.getSource() == ensembl){
				
				URL = "http://www.ensembl.org/Multi/Search/Results?q=" + geneName + ";site=ensembl";
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Unable to browse Ensembl website!");				}
			} // end of ensemble
			
			else if (mc.getSource() == geneRegion){
				Collection<String> theRegion = pathway.getMapedGeneRegion().get(entryID);
				if (theRegion == null){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "No genetic detail is available for this gene!");
				}
				else{
					new GeneRegions(entryID);
				}
			} // end of geneRegion
			
			else if (mc.getSource() == multipleSamples){
				
				Genes gene = pathway.getGenes().get(entryID);
				boolean expansion = gene.getExpandStatus();
				
				if (!expansion) {
					multiSampleView(gene);
				}
				else {
					JLabel[] expandedOnes = gene.getExpandedLabels();
					int expansionSize = expandedOnes.length;
					for (int i = 0; i < (expansionSize); i++){
						Interface.panelHolder.get(activeTab).remove(expandedOnes[i]);
					}
					DrawShapes shapes = new DrawShapes(pathway.getGraphicsItems(), pathway.getEdges());
					shapes.setPreferredSize(new Dimension((int) pathway.getMaxX(), (int) pathway.getMaxY()));
					Component[] components = Interface.panelHolder.get(activeTab).getComponents();
					for (int i = 0; i < components.length; i++) {
						if (components[i].getClass().equals(shapes.getClass())) {
							Interface.panelHolder.get(activeTab).remove(components[i]);
						}
					}
					
					Interface.panelHolder.get(activeTab).add(shapes,BorderLayout.CENTER);
					Interface.bodyFrame.repaint();
					Interface.scrollPaneHolder.get(activeTab).getVerticalScrollBar().setUnitIncrement(16);
					Interface.scrollPaneHolder.get(activeTab).getHorizontalScrollBar().setUnitIncrement(16);
					
				}
				gene.setExpandStatus();
			}
		}
	} // end of listenForClick

	public void multiSampleView(Genes gene) {
		JLabel label = pathway.getMapedGeneLabel().get(entryID).getLabel();
		Character type = pathway.getMetaType();
		List<List<String>> data = pathway.getDataForGene(entryID);
		int pointer = pathway.getPointer();
		int x = (int) label.getBounds().getX();
		int y = (int) label.getBounds().getY();
		int width = (int) label.getBounds().getWidth();
		int height = (int) label.getBounds().getHeight();
		Color bgColor = null;
		Rectangle nw = new Rectangle(x,y,width/2,height/2);
		Rectangle ne = new Rectangle(x+width/2,y, width/2, height/2);
		Rectangle sw = new Rectangle(x,y+height/2, width/2, height/2);
		Rectangle se = new Rectangle(x+width/2, y+height/2, width/2, height/2);
		
		int numberOfSamples = pathway.getSamplesIDs().size();
		int availableSamples = numberOfSamples - pointer - 1;
		int expansionSize =0;
		if (availableSamples >= 10){
			expansionSize = 10;
		}
		else {
			expansionSize = availableSamples;
		}
		JLabel[] extraLabels = new JLabel[expansionSize];
		
		for (int i = 1; i < (expansionSize+1); i++) {
			int newX = 0, newY = 0;
			JLabel newOne = new JLabel();
			if (nw.contains(expansionSide)){
				newX = x - (i * 10);
				newY = y - (i * 6);
			}
			else if (ne.contains(expansionSide)){
				newX = x + (i * 10);
				newY = y - (i * 6);
			}
			else if (sw.contains(expansionSide)){
				newX = x - (i * 10);
				newY = y + (i * 6);
			}
			else if (se.contains(expansionSide)){
				newX = x + (i * 10);
				newY = y + (i * 6);
			}
			else {
				newX = x + (i * 10);
				newY = y + (i * 6);
			}
			
			newOne.setBounds(newX, newY, width, height);
			double sum = 0;
			for (int j = 0 ; j < data.size(); j++){
				sum += Double.parseDouble(data.get(j).get(pointer + i));
			}
			if (type.equals('M')){
				bgColor = Genes.paintLabel(sum/data.size());
			}
			else {
				bgColor = Genes.paintLabel(sum/data.size(), data);
			}
			
			newOne.setBackground(bgColor);
			newOne.setOpaque(true);
			newOne.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			String hint = "sample ID: "
					+ ControlPanel.samplesIDsCombo.getItemAt(i).toString();
			if (pathway.getSamplesInfo().size() > 0){
				hint += " / " + ControlPanel.setSampleInfoLabel(pointer+i);
			}
			newOne.setToolTipText(hint);
			Interface.panelHolder.get(activeTab).add(newOne,BorderLayout.CENTER);
			extraLabels[i-1] = newOne;
		}
		DrawShapes shapes = new DrawShapes(pathway.getGraphicsItems(), pathway.getEdges());
		shapes.setPreferredSize(new Dimension((int) pathway.getMaxX(), (int) pathway.getMaxY()));
		Component[] components = Interface.panelHolder.get(activeTab).getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i].getClass().equals(shapes.getClass())) {
				Interface.panelHolder.get(activeTab).remove(components[i]);
			}
		}
		gene.addExpandedLabels(extraLabels);
		Interface.panelHolder.get(activeTab).add(shapes,BorderLayout.CENTER);
		Interface.bodyFrame.repaint();
		Interface.scrollPaneHolder.get(activeTab).getVerticalScrollBar().setUnitIncrement(16);
		Interface.scrollPaneHolder.get(activeTab).getHorizontalScrollBar().setUnitIncrement(16);
	}

}
