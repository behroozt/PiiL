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
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;


public class TabsInfo {
	
	Integer pointer;
	HashMap<String, Genes> mapedGeneLabel;
	HashMap<String,List<List<String>>> mapedGeneData;
	HashMap<String, List<String>> samplesInfo;
	HashMap<String, List<String>> idsInGroups;
	HashMap<String, List<String>> idsInBaseGroup;
	ArrayList<String> samplesIds;
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
	byte viewMode; // 0 for single, 1 for multiple, 2 for group-wise
	int selectedGenes;
	int groupingIndex;
	List<String> showableGroups;
	
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
		samplesInfo = new HashMap<String, List<String>>();
		samplesIds = new ArrayList<String>();
		viewMode = 0;
		selectedGenes = 0;
		groupingIndex = 0;
		
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
		if (idsInGroups == null){
			idsInGroups = new HashMap<String, List<String>>();
		}
		else {
			idsInGroups.clear();
		}
		
		for (int i = 0 ; i < (samplesIds.size()) ; i ++){
			String id = samplesIds.get(i);
			String group = samplesInfo.get(samplesIds.get(i)).get(newGroupingIndex);
			
			if (idsInGroups.get(group) == null){
				idsInGroups.put(group,new ArrayList<String>());
			}
			idsInGroups.get(group).add(id);
		}
		
		return idsInGroups;
	}
	
//	public HashMap<String, List<String>> setIDsInBase(int selectedIndex) {
//		
//		if (idsInBaseGroup == null){
//			idsInBaseGroup = new HashMap<String, List<String>>();
//		}
//		else {
//			idsInBaseGroup.clear();
//		}
//		
//		for (int i = 0 ; i < (samplesIds.size()) ; i ++){
//			String id = samplesIds.get(i);
//			String group = samplesInfo.get(samplesIds.get(i)).get(selectedIndex);
//			
//			if (idsInBaseGroup.get(group) == null){
//				idsInBaseGroup.put(group,new ArrayList<String>());
//			}
//			idsInBaseGroup.get(group).add(id);
//		}
//		
//		return idsInBaseGroup;
//		
//	}
	
	
	
	public boolean extractSamplesInfo(File file, int sampleIdIndex, String seperator, String[] fileHeader, int[] selectedIndexes, int sampleGroupIndex) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		String inputFileHeader = br.readLine();
				
		String splitBy;
		if (seperator.equals("tab")){
			splitBy = "\t";
		} else if (seperator.equals("space")){
			splitBy = " ";
		} else if (seperator.equals("comma")){
			splitBy = ",";
		}
		else if (seperator.equals("dash")){
			splitBy = " ";
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
		
		while ((line = br.readLine()) != null){
			String[] currentLine = line.split(splitBy);
			String id = currentLine[sampleIdIndex - 1];
			
			
			if (sampleGroupIndex != 0 & samplesIds.contains(id)){
				String sampleGroup = currentLine[sampleGroupIndex - 1];
				if (idsInGroups.get(sampleGroup) == null){
					idsInGroups.put(sampleGroup,new ArrayList<String>());
				}
				idsInGroups.get(sampleGroup).add(id);
				PiilMenubar.groupWiseView.setEnabled(true);
			}
			
				
			List<String> selectedValues = new ArrayList<String>(); 
			// the first item in selectedValues keeps the sample group
				
			for (int i = 0; i< selectedIndexes.length; i ++){
				selectedValues.add(currentLine[selectedIndexes[i]]);
			}
			samplesInfo.put(id, selectedValues);
			
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
	
	public byte getGenesList(File file) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		byte validity = 0;
		
		String inputFileHeader = br.readLine();
		String[] test = inputFileHeader.split("\t");
		int numOfColumns = test.length;
		
		for (int i = 1; i < test.length ; i++){
			samplesIds.add(test[i]);
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
			
			ControlPanel.enableControlPanel(0);
			assignPointer(0);
			PiilMenubar.loadSamplesInfo.setEnabled(true);
			
			if (loadedFilesMap == null){
				loadedFilesMap = new HashMap<String, File>();
			}
			loadedFilesMap.put(file.getName(), file);
			
			Character metaType = this.getMetaType();
			Genes.changeBgColor(0, metaType);
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
	
	private byte searchPathway(String line, int numOfColumns){
		
		String entryID = null;
		byte validData = 0;
		List<String> dataValues = new ArrayList<String>();
		String[] elements = line.split("\t");
		int numOfElements = elements.length;
		if (numOfElements != numOfColumns){
			validData = 1; // return 1 when columns length is not equal for all rows
			JOptionPane.showMessageDialog(Interface.bodyFrame, "Number of columns is not equal for all the rows, please check your input file.");
			return validData;
		}
		String geneInfo = elements[0];
		String[] geneInfoParts = geneInfo.split("_");
		String geneName = geneInfoParts[0];
		
		//check if GeneName_Region pattern exists
		
		for (Entry<String, Genes> oneNode : genes.entrySet()){
			if (match(geneName,oneNode.getValue().getAllNames())){
				
				oneNode.getValue().setTag(true);
				entryID = oneNode.getKey();
				mapedGeneLabel.put(oneNode.getKey(), oneNode.getValue());
		    	if (geneInfoParts.length > 1){
					// ****** add this to mapedGeneRegion
		    		if (mapedGeneRegion.get(entryID) == null){
		    			mapedGeneRegion.put(entryID, new ArrayList<String>());
		    			mapedGeneRegion.get(entryID).add(new String(geneInfoParts[1]));
		    		}
		    		else if (mapedGeneRegion.containsKey(entryID)){
		    			mapedGeneRegion.get(entryID).add(new String(geneInfoParts[1]));
		    		}
				}
		    	
		    	if (mapedGeneData.get(entryID) == null){
		    		mapedGeneData.put(entryID, new ArrayList<List<String>>());
		    		dataValues.clear();
		    		for (int i = 1; i < elements.length; i++){
		    			if (metaType.equals('M')){
		    				if (Double.parseDouble(elements[i]) > 1 || Double.parseDouble(elements[i]) < 0){
		    					JOptionPane.showMessageDialog(Interface.bodyFrame, "Invalid values in the input file! Beta values range is between 0 and 1.");
			    				validData = 2; // return 2 for invalid beta value
			    				return validData;
			    			}
			    			else{
			    				dataValues.add(elements[i]);
			    			}
		    			}
		    			else if (metaType.equals('E')){
		    				if (Double.parseDouble(elements[i]) < 0){
		    					JOptionPane.showMessageDialog(Interface.bodyFrame, "Invalid values in the input file! Expression values can not be negative.");
		    					validData = 3; // return 3 for invalid expression value
			    				return validData;
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

}
