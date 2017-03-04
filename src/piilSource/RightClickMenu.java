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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class RightClickMenu {
	

	JPopupMenu menu;
	JMenuItem histogram,rawData,  cpgView, geneCards, pubmed, ensembl, multipleSamples, similarGenes,
	sortAscending, sortDescending;
	JMenu geneInfo, sortSamples;
	String entryID;
	int activeTab;
	TabsInfo pathway;
	Character type;
	Point expansionSide;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));

	public RightClickMenu(Component component, int x, int y, String nodeID, Point point) {
		
		activeTab = Interface.tabPane.getSelectedIndex();
		pathway = ParseKGML.getTabInfo(activeTab,0);
		type = pathway.getMetaType();
		entryID = nodeID;
		expansionSide = new Point(point);
		menu = new JPopupMenu();
		geneInfo = new JMenu("Check this gene on ...");
		histogram = new JMenuItem("Histogram for all samples");
		rawData = new JMenuItem("Barplot for raw data");
		cpgView = new JMenuItem("Show CpG site(s) details");
		multipleSamples = new JMenuItem("Show multiple-sample view");
		similarGenes = new JMenuItem("Find genes with similar pattern");
		sortSamples = new JMenu("Sort samples");
		sortAscending = new JMenuItem("Ascending");
		sortDescending = new JMenuItem("Descending");
		geneCards = new JMenuItem("GeneCards");
		pubmed = new JMenuItem("Pubmed");
		ensembl = new JMenuItem("Ensembl");
		
		menu.add(geneInfo);
		menu.add(histogram);
		menu.add(rawData);
		menu.add(cpgView);
		menu.add(multipleSamples);
		menu.add(similarGenes);
		menu.add(sortSamples);
		geneInfo.add(geneCards);
		geneInfo.add(pubmed);
		geneInfo.add(ensembl);
		sortSamples.add(sortAscending);
		sortSamples.add(sortDescending);
		menu.show(component, x,y);
		
		
		if (pathway.getMapedGeneLabel().size() > 0){
			Genes gene = pathway.getMapedGeneLabel().get(entryID);
			if (gene == null) {
				histogram.setEnabled(false);
				rawData.setEnabled(false);
				cpgView.setEnabled(false);
				multipleSamples.setEnabled(false);
				similarGenes.setEnabled(false);
			}
			else {
				if (gene.getExpandStatus()){
					multipleSamples.setText("Single-sample view");
				}
				else {
					multipleSamples.setText("Multipe-sample view");
				}
				if (pathway.getMetaType().equals('E')){
					cpgView.setEnabled(false);
//					similarGenes.setEnabled(false);
				}
			}
			if (gene.getLabel().getBackground().equals(Color.LIGHT_GRAY)){
				histogram.setEnabled(false);
				rawData.setEnabled(false);
				sortSamples.setEnabled(false);
				multipleSamples.setEnabled(false);
				similarGenes.setEnabled(false);
			}
			if (pathway.getMapedGeneData().size() == 0){
				histogram.setEnabled(false);
				rawData.setEnabled(false);
				cpgView.setEnabled(false);
				multipleSamples.setEnabled(false);
				similarGenes.setEnabled(false);
			}
		}
		else {
			histogram.setEnabled(false);
			rawData.setEnabled(false);
			cpgView.setEnabled(false);
			multipleSamples.setEnabled(false);
			similarGenes.setEnabled(false);
		}
		
		ListenForClick lForClick = new ListenForClick();
		geneCards.addActionListener(lForClick);
		histogram.addActionListener(lForClick);
		rawData.addActionListener(lForClick);
		cpgView.addActionListener(lForClick);
		pubmed.addActionListener(lForClick);
		ensembl.addActionListener(lForClick);
		multipleSamples.addActionListener(lForClick);
		similarGenes.addActionListener(lForClick);
		sortAscending.addActionListener(lForClick);
		sortDescending.addActionListener(lForClick);
	}

	private class ListenForClick implements ActionListener{
		
		String geneName = pathway.getGenes().get(entryID).getText();
		String URL;
		int sampleIDIndex;
		
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
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Unable to browse GeneCards website!","Error", 0, icon);
				}
			} // end of geneCards
			
			else if (mc.getSource() == pubmed){

				URL = "http://www.ncbi.nlm.nih.gov/pubmed/?term=" + geneName;
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Unable to browse PubMed website!","Error", 0, icon);
				}
			} // end of pubmed
			
			else if (mc.getSource() == ensembl){
				
				URL = "http://www.ensembl.org/Multi/Search/Results?q=" + geneName + ";site=ensembl";
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Unable to browse Ensembl website!","Error", 0, icon);				}
			} // end of ensemble
			
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
				gene.getLabel().setVisible(true);
				gene.setExpandStatus();
			} // end of multipleSamples
			
			else if (mc.getSource() == cpgView){
				
				Collection<String> theRegion = pathway.getMapedGeneRegion().get(entryID);
				
				if (theRegion == null){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "No CpG site detail is available for this gene!","Message", 0, icon);
				}
				else{
					new GeneRegions(entryID,activeTab,pathway);
				}
			} // end of cpgView
			
			else if (mc.getSource() == similarGenes){
				File inputFile = pathway.getMetaFilePath();
				int numberOfSamples = pathway.getSamplesIDs().size();
				float sum = 0;
				List<Float> siteValues = new ArrayList<Float>();
				String geneName = pathway.getMapedGeneLabel().get(entryID).getText();
				
				if (type.equals('M')){
					if (pathway.getSelectedSites(entryID) == null){
						int numberOfRegions = pathway.getMapedGeneRegion().get(entryID).size();
						
						for (int i = 0 ; i < numberOfSamples; i ++){
							sum = 0;
							for (int j = 0 ; j < numberOfRegions; j ++){
								if (!isNumeric(pathway.getMapedGeneData().get(entryID).get(j).get(i))){
									continue;
								}
								sum += Float.parseFloat(pathway.getMapedGeneData().get(entryID).get(j).get(i));
							}
							siteValues.add(sum / numberOfRegions);
						}
					}
					else {
						int numberOfSignificantSites = pathway.getSelectedSites(entryID).size();
						for (int i = 0 ; i < numberOfSamples; i ++){
							sum = 0;
							for (int j : pathway.getSelectedSites(entryID)){
								if (!isNumeric(pathway.getMapedGeneData().get(entryID).get(j).get(i))){
									continue;
								}
								sum += Float.parseFloat(pathway.getMapedGeneData().get(entryID).get(j).get(i));
							}
							siteValues.add(sum / numberOfSignificantSites);
						}
					}
				}
				else if (type.equals('E')){
					for (int i = 0 ; i < numberOfSamples ; i++){
						siteValues.add(Float.parseFloat(pathway.getMapedGeneData().get(entryID).get(0).get(i)));
					}
				}
				
				
				new SimilarityFinder(pathway, inputFile, geneName, siteValues);
			} // end of similarGenes
			
			else { // sorting samples
				
				List<List<String>> list = pathway.getMapedGeneData().get(entryID);
				TreeMap<Float, String> sortingMap = new TreeMap<Float, String>();
				String geneCode = null;
				List<String> ids = ParseKGML.getTabInfo(activeTab,0).getSamplesIDs();
				float sum;
	        	int invalid = 0;
	        	float value;
		        
		        for (Entry<String, Genes> item : pathway.getMapedGeneLabel().entrySet()){
		        	if (item.getValue().getText().equals(geneName)){
		        		geneCode = item.getKey();
		        		continue;
		        	}
		        }
				
				if (type.equals('M')){
					
					List<Integer> significantSites = new ArrayList<Integer>();
		            significantSites = pathway.getSelectedSites(geneCode);
		        	
		        	for (int i = 0; i < list.get(0).size() ; i ++){
	                	sum = 0; value = 0; invalid = 0;
	                	if (significantSites == null){
	    					for (int j = 0; j < list.size() ; j ++){
	                    		if (!isNumeric(list.get(j).get(i))){
	                    			invalid ++;
	            					continue;
	            				}
	                    		sum += Float.parseFloat(list.get(j).get(i));
	                    	}
	                    	value = sum / (list.size() - invalid);
	                    	sortingMap.put(value,ids.get(i));
	    				}
	    				else { // some sites are selected
	    					for (int item : significantSites){
	            				if (item != -1){
	            					if (!isNumeric(list.get(item).get(i))){
	            						invalid ++;
	            						continue;
	            					}
	            					sum += Double.parseDouble(list.get(item).get(i));
	            				}
	            				
	            			}
	                		value = sum / (significantSites.size() - invalid);
	                		sortingMap.put(value, ids.get(i));
	    				}
	                }
					
				} // end of if-methylation
				
				else { // if expression
					for (int i = 0; i < list.get(0).size() ; i ++){
	                	sum = 0; value = 0;
	                	for (int j = 0; j < list.size() ; j ++){
	                		if (!isNumeric(list.get(j).get(i))){
	                			invalid ++;
	        					continue;
	        				}
	                		sum += Float.parseFloat(list.get(j).get(i));
	                	}
	                	value = sum / (list.size() - invalid);
	                	sortingMap.put(value, ids.get(i));
	                }
					
				} // end of if-expression
				
				if (mc.getSource() == sortAscending){
					DefaultComboBoxModel sortedIDs = new DefaultComboBoxModel(sortingMap.values().toArray());
					
					ControlPanel.samplesIDsCombo.setModel(sortedIDs);
					ControlPanel.samplesIDsCombo.setSelectedIndex(pathway.getPointer());
					ArrayList<String> sortedSamples = new ArrayList<String>();
					for (String item : sortingMap.values()){
						sortedSamples.add(item);
					}
					pathway.setSortedSampleIDs(sortedSamples);
					sampleIDIndex = pathway.getSamplesIDs().indexOf(ControlPanel.samplesIDsCombo.getSelectedItem());
					Genes.changeBgColor(sampleIDIndex,type);
					if (pathway.getSamplesInfo() != null && pathway.getSamplesInfo().size() > 0){
						ControlPanel.setSampleInfoLabel(sampleIDIndex);
					}
		      	    else {
						Interface.setSampleInfoLabel(ControlPanel.samplesIDsCombo.getItemAt(pathway.getPointer()).toString(), true);
					}
				}
				else if (mc.getSource() == sortDescending){
					
					Map<Float, String> descendingSortingMap = sortingMap.descendingMap();
					DefaultComboBoxModel sortedIDs = new DefaultComboBoxModel(descendingSortingMap.values().toArray());
					ControlPanel.samplesIDsCombo.setModel(sortedIDs);
					ControlPanel.samplesIDsCombo.setSelectedIndex(pathway.getPointer());
					sampleIDIndex = pathway.getSamplesIDs().indexOf(ControlPanel.samplesIDsCombo.getSelectedItem());
					ArrayList<String> sortedSamples = new ArrayList<String>();
					for (String item : sortingMap.values()){
						sortedSamples.add(item);
					}
					pathway.setSortedSampleIDs(sortedSamples);
					Genes.changeBgColor(sampleIDIndex,type);
					if (pathway.getSamplesInfo() != null && pathway.getSamplesInfo().size() > 0){
						ControlPanel.setSampleInfoLabel(sampleIDIndex);
					}
		      	    else {
						Interface.setSampleInfoLabel(ControlPanel.samplesIDsCombo.getItemAt(pathway.getPointer()).toString(), true);
					}
				}
				
			} // end of sortAscending
			
		} // end of actionPerformed
	} // end of listenForClick

	public void multiSampleView(Genes gene) {
		JLabel label = pathway.getMapedGeneLabel().get(entryID).getLabel();
		float threshold = pathway.getSDThreshold();
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
			int newPointer = pointer + i;
			int newX = 0, newY = 0;
			JLabel newOne = new JLabel();
			if (nw.contains(expansionSide)){
				newX = x - (i * 6);
				newY = y - (i * 4);
			}
			else if (ne.contains(expansionSide)){
				newX = x + (i * 6);
				newY = y - (i * 4);
			}
			else if (sw.contains(expansionSide)){
				newX = x - (i * 6);
				newY = y + (i * 4);
			}
			else if (se.contains(expansionSide)){
				newX = x + (i * 6);
				newY = y + (i * 4);
			}
			else {
				newX = x + (i * 6);
				newY = y + (i * 4);
			}
			
			newOne.setBounds(newX, newY, width, height);
			double sum = 0;
			String value = data.get(0).get(newPointer);
			
			if (type.equals('M')){
				double average = 0;
				int validSites = 0; 
				int multiRegion = data.size();
				if (multiRegion == 1){
					
					
					if (pathway.getSelectedSites(entryID) == null){
						
						if (!isNumeric(value)){
							bgColor = Color.LIGHT_GRAY;
						}
						else {
							bgColor = Genes.paintLabel(Double.parseDouble(value));
						}
					}
					else {
						
						if (pathway.getSelectedSites(entryID).get(0) == -1){
							bgColor = Color.LIGHT_GRAY;
						}
						else {
							bgColor = Genes.paintLabel(Double.parseDouble(value));
						}
					}
				} // end of multiple Regions
				
				else { // there are multiple regions
					
					sum = 0 ; average = 0; validSites = 0;
					
					if (threshold == 0){
						if ( pathway.getSelectedSites(entryID) != null ){
							if (pathway.getSelectedSites(entryID).get(0) == -1){
								// none of the sites have passed SD filtering
								validSites = 0;
							}
							else {
								for (int index : pathway.getSelectedSites(entryID)){
									String val = data.get(index).get(newPointer);
									
									if (!isNumeric(val)){
										continue;
									}
									sum += Double.parseDouble(val);
									validSites ++;
								}
							}
						}
						else {
							for (int j = 0; j < multiRegion; j++){
								String val = data.get(j).get(newPointer);
								
								if (!isNumeric(val)){
									continue;
								}
								sum += Double.parseDouble(val);
								validSites ++;
							}
						}
					}
					
					else if ( (threshold > 0)){  // there is an SD filter
						if (pathway.getSelectedSites(entryID) == null){
							continue;
						}
						if (pathway.getSelectedSites(entryID).get(0) == -1){
							// none of the sites have passed SD filtering
							validSites = 0;
						}
						else {
							for (int k : pathway.getSelectedSites(entryID)){
								String val = data.get(k).get(newPointer);
								
								if (!isNumeric(val)){
									continue;
								}
								sum += Double.parseDouble(val);
								validSites ++;
							}
						}
						
					}
					
					if (validSites == 0) validSites = 1;
					average = sum / validSites;
					
					if (Double.isNaN(average) | (average == 0)){
						bgColor = Color.LIGHT_GRAY;
					}
					else {
						bgColor = Genes.paintLabel(average);
					}
				}
				
			}
			else { // type = 'E'
				if (!isNumeric(value)){
					bgColor = Color.BLACK;
				}
				else {
					bgColor = Genes.findColor(Double.parseDouble(value), data);
				}
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

	private static boolean isNumeric(String str) {
		try {  
		    double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe){  
		    return false;  
		}  
		return true;
	}

}
