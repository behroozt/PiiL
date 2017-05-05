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
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jfree.util.StringUtils;
import org.omg.CORBA.Bounds;
import org.w3c.dom.Document;


public class TabsInfo {
	
	Integer pointer;
	HashMap<String, Genes> mapedGeneLabel;
	HashMap<String,List<List<String>>> mapedGeneData;
	HashMap<String,List<Integer>> mappedGeneSelectedSites;
	HashMap<String, List<String>> samplesInfo;
	LinkedHashMap<String, List<String>> idsInGroups;
	HashMap<String, List<String>> idsInBaseGroup;
	ArrayList<String> samplesIds;
	ArrayList<String> orderedSamplesIds;
	HashMap<String, Collection<String>> mapedGeneRegion;
	String caption;
	String pathwayCode;
	String pathwayName; 
	String organismCode; 
	String organismName;
	File loadedFilePath;
	File metaFilePath;
	static String loadedSamplesInfoFile;
	Character metaType;
	Character loadSource;
	static HashMap<String,File> loadedFilesMap;
	static HashMap<String, SampleInformation> sampleInfoFiles;
	HashMap<String, Nodes> nodes;
	HashMap<String, Genes> genes;
	Document xmlDoc;
	List<Edges> edges;
	List<Shape> graphicNode;
	float scrollX;
	float scrollY;
	byte viewMode; // 0 for single, 1 for multiple, 2 for group-wise, 3 for Dual(expression and methylation together)
	int selectedGenes;
	int groupingIndex;
	List<String> showableGroups;
	String dataSplitor;
	int baseGroupIndex;
	float[] ranges;
	float sdThreshold;
	int selectedGeneIndex;
	Point selectedGenePointer;
	String reloadbleSplitor;
	static ArrayList<String> gridGenesList;
	int metaFileLines = 0;
	
	public TabsInfo(String tabCaption, File path, Character source, String pathway) {
		pointer = 0;
//		mapedGeneData = null;
//		mapedGeneLabel = null;
//		mapedGeneRegion = null;
//		samplesInfo = null;
		caption = tabCaption;
//		pathwayCode = null;
		pathwayName = pathway;
//		organismCode = null;
//		organismName = null;
//		metaFilePath = null;
		loadedFilePath = path;
		metaType = ' ';
		loadSource = source;
//		samplesIds = null;
//		sampleInfoFiles = null;
		nodes = new HashMap<String, Nodes>();
		genes = new HashMap<String, Genes>();
		mapedGeneData = new HashMap<String, List<List<String>>>();
		mapedGeneLabel = new HashMap<String, Genes>();
		mapedGeneRegion = new HashMap<String, Collection<String>>();
		mappedGeneSelectedSites = new HashMap<String, List<Integer>>();
		samplesInfo = new HashMap<String, List<String>>();
		samplesIds = new ArrayList<String>();
		orderedSamplesIds = new ArrayList<String>();
		viewMode = 0;
		selectedGenes = 0;
		groupingIndex = 0;
		baseGroupIndex = -1;
		ranges = new float[] {0,10,2}; // {methylationLow, methylationHigh, expressionFold}
		sdThreshold = 0;
		selectedGeneIndex = 0;
		selectedGenePointer = new Point(0, 0);
		gridGenesList = new ArrayList<String>();
		
	}
	
	public void setMetaFileLines(int lines){
		metaFileLines = lines;
	}
	
	public int getMetaFileLines(){
		return metaFileLines;
	}
	
	public void setGridGenesList(ArrayList<String> list){
		for (String item : list){
			gridGenesList.add(item);
		}
	}
	
	public ArrayList<String> getGridGenesList(){
		return gridGenesList;
	}
	
	public void setSelectedGenePointer(Point position){
		int activeTab = Interface.tabPane.getSelectedIndex();
		Component star = null;
		Component extraStar = null;
		
		if (Interface.tabInfoTracker.get(activeTab).size() > 1){
			TabsInfo pathway = ParseKGML.getTabInfo(activeTab,1);
			int secondLayer = pathway.getMapedGeneLabel().size();
			star  = Interface.panelHolder.get(Interface.tabPane.getSelectedIndex()).getComponent(Interface.panelHolder.get(Interface.tabPane.getSelectedIndex()).getComponentCount()-(secondLayer+3));
			extraStar  = Interface.panelHolder.get(Interface.tabPane.getSelectedIndex()).getComponent(Interface.panelHolder.get(Interface.tabPane.getSelectedIndex()).getComponentCount()-(secondLayer+2));
		}
		else {
			star  = Interface.panelHolder.get(Interface.tabPane.getSelectedIndex()).getComponent(Interface.panelHolder.get(Interface.tabPane.getSelectedIndex()).getComponentCount()-3);
			extraStar  = Interface.panelHolder.get(Interface.tabPane.getSelectedIndex()).getComponent(Interface.panelHolder.get(Interface.tabPane.getSelectedIndex()).getComponentCount()-2);
		}
		
		star.setBackground(Color.MAGENTA);
		extraStar.setBackground(Color.MAGENTA);
		star.setBounds((int)position.getX()-5, (int)position.getY()-5, 10, 10);
		extraStar.setBounds((int)position.getX()+ 41, (int)position.getY() + 12, 10, 10);
		((JComponent) star).setBorder(new RoundedCornerBorder());
		((JComponent) extraStar).setBorder(new RoundedCornerBorder());
		selectedGenePointer =  position;
	}
	
	public Point getSelectedGenePointer(){
		return selectedGenePointer;
	}
	
	public void setSelectedGeneIndex(int index){
		selectedGeneIndex = index;
	}
	
	public int getSelectedGeneIndex(){
		return selectedGeneIndex;
	}
	
	public void setSelectedSites(HashMap < String ,List<Integer>> selection){
		
		mappedGeneSelectedSites = selection;
	}
	
	public List<Integer> getSelectedSites(String gene){
		if (mappedGeneSelectedSites.get(gene) == null){
			return null;
		}
		else {
			return mappedGeneSelectedSites.get(gene);
		}
	}
	
	public HashMap<String, List<Integer>> getAllSites(){
		return mappedGeneSelectedSites;
	}
	
	public void setSDThreshold(float threshold){
		sdThreshold = threshold;
	}
	
	public float getSDThreshold(){
		return sdThreshold;
	}
	
	public void setRanges(float low, float high, float fold){
		ranges[0] = low ; ranges[1] = high; ranges[2] = fold;
	}
	
	public float[] getRanges(){
		return ranges;
	}
	
	public void setBaseGroupIndex(int index){
		baseGroupIndex = index;
	}
	
	public int getBaseGroupIndex(){
		return baseGroupIndex;
	}
	
	public void setGroupingIndex(int index){
		groupingIndex = index;
	}
	
	public int getGroupingIndex(){
		return groupingIndex;
	}
	
	public void setShowableGroups(List<String> list){
		if (showableGroups == null){
			showableGroups = new ArrayList<String>();
		}
		showableGroups = list;
	}
	
	public List<String> getShowableGroups(){
		return showableGroups;
	}
	
	public void setSelectedGenesCount(int value){
		if (value == 0){
			selectedGenes = 0;
		}
		else {
			selectedGenes = selectedGenes + value;
		}
	}
	
	public int getSelectedGenesCount(){
		return selectedGenes;
	}
	
	public byte getViewMode(){
		return viewMode;
	}
	
	public void setViewMode(byte mode){
		viewMode = mode;
	}
	
	public static File getLoadedFilePath(String fileName){
		return loadedFilesMap.get(fileName);
	}
	
	public void setDocument(Document doc){
		xmlDoc = doc;
	}
	
	public Document getDocument(){
		return xmlDoc;
	}
	
	public Integer getPointer() {
		return pointer;
	}

	public Integer assignPointer(Integer tabPointer) {
		pointer = tabPointer;
		return pointer;
	}

	public HashMap<String, Genes> getMapedGeneLabel() {
		return mapedGeneLabel;
	}

	public void setMapedGeneLabel(HashMap<String, Genes> matchedGenes) {
		
		mapedGeneLabel = matchedGenes;
	}

	public HashMap<String, List<List<String>>> getMapedGeneData() {
		return mapedGeneData;
	}
	
	public List<List<String>> getDataForGene(String gene){
		return mapedGeneData.get(gene);
	}

	public void setMapedGeneData(HashMap<String, List<List<String>>> matchedGeneData) {
		
		mapedGeneData = matchedGeneData;
	}

	public HashMap<String, List<String>> getSamplesInfo() {
		return samplesInfo;
	}
	
	public HashMap<String, List<String>> getIDsInGroups() {
		return idsInGroups;
	}
	
	public HashMap<String, List<String>> resetIDsInGroups(int newGroupingIndex){
		
		if (newGroupingIndex == -1){
			idsInGroups = null;
			return idsInGroups;
		}
		
		if (idsInGroups == null){
			idsInGroups = new LinkedHashMap<String, List<String>>();
		}
		else {
			idsInGroups.clear();
		}
		
		for (int i = 0 ; i < (samplesIds.size()) ; i ++){
			String id = samplesIds.get(i);
			if (samplesInfo.get(samplesIds.get(i)) == null){
				continue;
			}
			String group = samplesInfo.get(samplesIds.get(i)).get(newGroupingIndex);
			
			if (idsInGroups.get(group) == null){
				idsInGroups.put(group,new ArrayList<String>());
			}
			idsInGroups.get(group).add(id);
		}
		return idsInGroups;
	}
	
	public boolean extractSamplesInfo(File file, int sampleIdIndex, String seperator, String[] fileHeader, int[] selectedIndexes, int sampleGroupIndex, boolean flag) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		String inputFileHeader = br.readLine();
				
		String splitBy;
		if (seperator.equals("tab")){
			splitBy = "\t";
		} else if (seperator.equals("space")){
			splitBy = "\\s+";
		} else if (seperator.equals("comma")){
			splitBy = ",";
		}
		else if (seperator.equals("dash")){
			splitBy = "-";
		}
		else {
			splitBy = ";";
		}
		
		List<String> selectedColumns = new ArrayList<String>();
		List<String> header = new ArrayList<String>();
		String[] columnNames = fileHeader;
		
		for (int i = 0; i< selectedIndexes.length; i ++){
						
			selectedColumns.add(columnNames[selectedIndexes[i]]);
		}
		
		for (int i = 0 ; i < columnNames.length ; i ++){
			header.add(columnNames[i]);
		}
		
		samplesInfo.put(Integer.toString(-1), header);	
		samplesInfo.put(Integer.toString(0), selectedColumns);
		
		String barcode_id;
		
		while ((line = br.readLine()) != null){
			String[] currentLine = line.split(splitBy);
			String id = currentLine[sampleIdIndex - 1];
		
				for (String item : samplesIds){
					if (flag){
						String[] barcode = item.split("-");
						barcode_id = barcode[0] + "-" + barcode[1] + "-" + barcode[2];
						
					}
					else {
						barcode_id = item;
					}
					
					if (barcode_id.equals(id)){
						
						if (sampleGroupIndex != 0){
							String sampleGroup = currentLine[sampleGroupIndex - 1];
							if (idsInGroups.get(sampleGroup) == null){
								idsInGroups.put(sampleGroup,new ArrayList<String>());
							}
							idsInGroups.get(sampleGroup).add(item);
							PiilMenubar.groupWiseView.setEnabled(true);
						}
						List<String> selectedValues = new ArrayList<String>(); 
						// the first item in selectedValues keeps the sample group

						for (int i = 0; i< selectedIndexes.length; i ++){
							selectedValues.add(currentLine[selectedIndexes[i]]);
						}
						samplesInfo.put(item, selectedValues);
//						break;
					}
				}
			
//			if (sampleGroupIndex != 0 & samplesIds.contains(id)){
//				String sampleGroup = currentLine[sampleGroupIndex - 1];
//				if (idsInGroups.get(sampleGroup) == null){
//					idsInGroups.put(sampleGroup,new ArrayList<String>());
//				}
//				idsInGroups.get(sampleGroup).add(id);
//				PiilMenubar.groupWiseView.setEnabled(true);
//			}
			
				
//			List<String> selectedValues = new ArrayList<String>(); 
//			// the first item in selectedValues keeps the sample group
//				
//			for (int i = 0; i< selectedIndexes.length; i ++){
//				selectedValues.add(currentLine[selectedIndexes[i]]);
//			}
//			samplesInfo.put(item, selectedValues);
			
		} // end of while
		
		if (sampleGroupIndex == 0){
			idsInGroups = null;
		}
		
		ControlPanel.setSampleInfoLabel(pointer);
		return true;
		
	}

	public void setSamplesInfo(String key, List<String> items) {
		
		samplesInfo.put(key, items);
	}

	public HashMap<String, Collection<String>> getMapedGeneRegion() {
		return mapedGeneRegion;
	}

	public void setMapedGeneRegion(HashMap<String, Collection<String>> matchedGeneRegion) {
		
		mapedGeneRegion = matchedGeneRegion;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String tabCaption) {
		caption = tabCaption;
	}

	public String getPathwayCode() {
		return pathwayCode;
	}

	public void setPathwayCode(String pathCode) {
		pathwayCode = pathCode;
	}

	public String getPathwayName() {
		return pathwayName;
	}

	public void setPathwayName(String pathName) {
		this.pathwayName = pathName;
	}

	public String getOrganismCode() {
		return organismCode;
	}

	public void setOrganismCode(String orgCode) {
		organismCode = orgCode;
	}

	public String getOrganismName() {
		return organismName;
	}

	public void setOrganismName(String orgName) {
		organismName = orgName;
	}

	public File getLoadedFilePath() {
		return loadedFilePath;
	}

	public void setLoadedFilePath(File loadedFilePath) {
		this.loadedFilePath = loadedFilePath;
	}

	public File getMetaFilePath() {
		return metaFilePath;
	}

	public void setMetaFilePath(File metaFilePath) {
		this.metaFilePath = metaFilePath;
	}

	public Character getMetaType() {
		return metaType;
	}

	public void setMetaType(Character metaType) {
		this.metaType = metaType;
	}

	public Character getLoadSource() {
		return loadSource;
	}

	public void setLoadSource(Character loadSource) {
		this.loadSource = loadSource;
	}
	
	public static void setSamplesInformationFile(String fileName, SampleInformation sample){
		if (sampleInfoFiles == null){
			sampleInfoFiles = new HashMap<String, SampleInformation>();
		}
		sampleInfoFiles.put(fileName, sample);
		loadedSamplesInfoFile = fileName;
		
	}
	
	public SampleInformation getSamplesInformation(){
		return sampleInfoFiles.get(loadedSamplesInfoFile);
	}
	
	public static SampleInformation getSamplesInformationFile(String fileName){
		return sampleInfoFiles.get(fileName);
	}
	
	public byte getGenesList(File file, CheckInputFile input, boolean paint) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		byte validity = 0;
		dataSplitor = input.getSeparator();
		// skip lines to get to samples ids line
		for (int i = 0 ; i < (input.getSampleRow() - 1); i++){
			br.readLine();
		}
		
		String inputFileHeader = br.readLine();
		String[] test = inputFileHeader.split(dataSplitor);
		int numOfColumns = test.length;
		Pattern pattern = Pattern.compile("\"([^\"]*)\"");
		Matcher matcher;
		
		for (int i = 1; i < test.length ; i++){
			matcher = pattern.matcher(test[i]);
			if (matcher.find()){
				samplesIds.add(matcher.group(1));
				orderedSamplesIds.add(matcher.group(1));
			}
			else {
				samplesIds.add(test[i]);
				orderedSamplesIds.add(test[i]);
			}
		}
		
		// skip rows to get to the data lines
		int skip = input.getDataRow() - input.getSampleRow() - 1;
		for (int j = 0; j < skip ; j++){
			br.readLine();
		}
		
		while ((line = br.readLine()) != null){
			
			validity = searchPathway(line,numOfColumns);
			
			if (validity != 0){
				emptyStack();
				br.close();
				return validity;
			}
		}
		br.close();
		
		if (getMapedGeneLabel().size() > 0 ) {
			if (paint){
				ControlPanel.enableControlPanel(0);
				assignPointer(0);
				PiilMenubar.loadSamplesInfo.setEnabled(true);
			}
			
			
			if (loadedFilesMap == null){
				loadedFilesMap = new HashMap<String, File>();
			}
			
			loadedFilesMap.put(file.getName(), file);
			Character metaType = this.getMetaType();
			if (paint){
				
				Genes.changeBgColor(0, metaType);
			}
			
		}
		else {
			JOptionPane.showMessageDialog(Interface.bodyFrame, "The loaded list has no overlap with this pathway");
//			setMapedGeneData(null);
		}
		
		return validity;
	} // end getGenesList

	public List<String> getSamplesIDs(){
		return samplesIds;
	}
	
	public List<String> getOrderedSamplesIDs(){
		return orderedSamplesIds;
	}
	
	public void setSortedSampleIDs(ArrayList<String> list){
		orderedSamplesIds = list;
	}
	
	private byte searchPathway(String line, int numOfColumns){
		
		String entryID = null;
		byte validData = 0;
		List<String> dataValues = new ArrayList<String>();
		String[] elements = line.split(dataSplitor);
		int numOfElements = elements.length;
		
//		if (numOfElements != numOfColumns){
//			validData = 1; // return 1 when columns length is not equal for all rows
//			JOptionPane.showMessageDialog(Interface.bodyFrame, "Number of columns is not equal for all the rows, please check your input file.");
//			return validData;
//		}
		
		Pattern pattern = Pattern.compile("\"([^\"]*)\"");
		
		String geneInfo = elements[0];
		Matcher matcher = pattern.matcher(geneInfo);
		if (matcher.find()){
			geneInfo = matcher.group(1);
		}
		String[] geneInfoParts = geneInfo.split("_");
		String geneName = geneInfoParts[0];
		
		
		//check if GeneName_Region pattern exists
		
		for (Entry<String, Genes> oneNode : genes.entrySet()){
			if (match(geneName,oneNode.getValue().getAllNames())){
				oneNode.getValue().setTag(true);
				entryID = oneNode.getKey();
				mapedGeneLabel.put(oneNode.getKey(), oneNode.getValue());
		    	if (geneInfoParts.length > 1){
		    		String description = "";
		    		for (int i = 1; i < geneInfoParts.length ; i++){
		    			description += geneInfoParts[i] + "_";
		    		}
		    		description = description.substring(0, description.length()-1);
					// ****** add this to mapedGeneRegion
		    		if (mapedGeneRegion.get(entryID) == null){
		    			mapedGeneRegion.put(entryID, new ArrayList<String>());
		    			mapedGeneRegion.get(entryID).add(new String(description));
		    		}
		    		else if (mapedGeneRegion.containsKey(entryID)){
		    			mapedGeneRegion.get(entryID).add(new String(description));
		    		}
				}
		    	
		    	if (mapedGeneData.get(entryID) == null){
		    		mapedGeneData.put(entryID, new ArrayList<List<String>>());
		    		dataValues.clear();
		    		
		    		for (int i = 1; i < elements.length; i++){
		    			
		    			if (metaType.equals('M')){
		    				if (isNumeric(elements[i])){
		    					if (Double.parseDouble(elements[i]) > 1 || Double.parseDouble(elements[i]) < 0){
	    							JOptionPane.showMessageDialog(Interface.bodyFrame, "Invalid values in the input file! Beta values range is between 0 and 1.");
	    							validData = 2; // return 2 for invalid beta value
	    							return validData;
	    						}
	    						else{
	    							dataValues.add(elements[i]);
	    						}
		    				}
		    				else {
		    					dataValues.add(elements[i]);
			    			}
		    			}
		    			else if (metaType.equals('E')){
		    				if (isNumeric(elements[i])){
		    					if (Double.parseDouble(elements[i]) < 0){
			    					JOptionPane.showMessageDialog(Interface.bodyFrame, "Invalid values in the input file! Expression values can not be negative.");
			    					validData = 3; // return 3 for invalid expression value
				    				return validData;
			    				}
			    				else {
			    					dataValues.add(elements[i]);
			    				}
		    				}
		    				else {
		    					dataValues.add(elements[i]);
		    				}
		    				
		    			}
		    			
					}
		    		mapedGeneData.get(entryID).add(dataValues);
		    	}
		    	else if (mapedGeneData.containsKey(entryID)){
		    		dataValues.clear();
		    		for (int i = 1; i < elements.length; i++){
						dataValues.add(elements[i]);
					}
		    		mapedGeneData.get(entryID).add(new ArrayList<String>(dataValues));
		    	}
			}
			
		} // end for
		
		return validData;
	} // end of searchPathway 

	private boolean isNumeric(String str) {
		try {  
		    double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe){  
		    return false;  
		}  
		return true;
	}

	private boolean match(String geneName, String[] allNames) {
		boolean found = false;
		for (String item : allNames){
			if (geneName.equals(item)){
				found = true;
				break;
			}
		}
		return found;
	}

	public void setNodes(HashMap<String, Nodes> nodeHandler) {
		nodes = nodeHandler;
	}
	
	public HashMap<String, Nodes> getNodes(){
		return nodes;
	}

	public void setGenes(HashMap<String, Genes> geneHandler) {
		genes = geneHandler;
	}
	
	public HashMap<String, Genes> getGenes(){
		return genes;
	}

	public int movePointerForward() {
		pointer ++;
		return pointer;
	}

	public int movePointerBackward() {
		pointer --;
		return pointer;
	}

	public void setEdgeItems(List<Edges> edgeItems) {
		edges = edgeItems;
	}

	public void setGraphicsItems(List<Shape> graphicsItems) {
		graphicNode = graphicsItems;
		
	}
	
	public List<Shape> getGraphicsItems(){
		return graphicNode;
	}
	
	public List<Edges> getEdges(){
		return edges;
	}

	public void setMaxX(float maxX) {
		scrollX = maxX;
	}
	
	public float getMaxX(){
		return scrollX;
	}
	
	public void setMaxY(float maxY){
		scrollY = maxY;
	}
	
	public float getMaxY(){
		return scrollY;
	}

	public void emptyStack() {
		samplesIds.clear();
		mapedGeneLabel.entrySet().clear();
		mapedGeneData.entrySet().clear();
		mapedGeneRegion.entrySet().clear();
		
	}

	public void highlightInPathway(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		byte validity = 0;
		String entryID;

		while ((line = br.readLine()) != null){
			String geneName = line.trim();
			for (Entry<String, Genes> oneNode : genes.entrySet()){
				if (match(geneName,oneNode.getValue().getAllNames())){
					oneNode.getValue().setTag(true);
					entryID = oneNode.getKey();
					mapedGeneLabel.put(oneNode.getKey(), oneNode.getValue());
				}
			}
		}
		br.close();

		if (getMapedGeneLabel().size() > 0 ) {
			
			ControlPanel.enableControlPanel(0);
			assignPointer(0);
			
			if (loadedFilesMap == null){
				loadedFilesMap = new HashMap<String, File>();
			}
			loadedFilesMap.put(file.getName(), file);
			
			this.setMetaType('H'); // H for Highlight genes in pathway 
			Genes.highlightGenes();
		}
		else {
			JOptionPane.showMessageDialog(Interface.bodyFrame, "The loaded list has no overlap with this pathway");
//			setMapedGeneData(null);
		}
		
	}

	public void setSitesForGene(String nodeID, List<Integer> selected) {
		
		mappedGeneSelectedSites.put(nodeID, selected);
		
	}
	
	public List<Integer> getSitesForGene(String geneID){
		return mappedGeneSelectedSites.get(geneID);
	}

	public void setSplitor(String separator) {
		reloadbleSplitor = separator;
		
	}
	
	public String getReloadableSplitor(){
		return reloadbleSplitor;
	}

}
