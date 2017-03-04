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

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;


public class Genes {
	
	private JLabel geneNode;
	private String geneTitle;
	private boolean matchTag;
	private String[] allNames;
	private boolean selected;
	private Border borderStyle;
	private boolean expanded;
	private JLabel[] extendedLabels;
	private Point selectionPoint;
	static float[] ranges;

	public String[] getAllNames() {
		return allNames;
	}

	public void setAllNames(String[] allNames) {
		this.allNames = allNames;
	}
	
	public void setExpandStatus(){
		expanded = !expanded ;
	}
	
	public boolean getExpandStatus(){
		return expanded;
	}
	
	public void setGeneBorder(Border border){
		borderStyle = border;
	}
	
	public Border getGeneBorder(){
		return borderStyle;
	}
	
	public void setSelectionPoint(Point point){
		selectionPoint = point;
	}
	
	public Point getSelectionPoint(){
		return selectionPoint;
	}

	public Genes(JLabel theLabel, String nodeLabel, String[] nameAlternatives, boolean b, boolean s) {
		geneNode = theLabel;
		geneTitle = nodeLabel; 
		matchTag = false;
		selected = false;
		allNames = nameAlternatives;
		borderStyle = BorderFactory.createLineBorder(Color.BLACK);
		expanded = false;
	}
	
	public void setSelectedStatus(Point point){
		if (matchTag) {
			TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex(),0);
			if (selected) {
				selected = false;
				pathway.setSelectedGenesCount(-1);
				geneNode.setBorder(borderStyle);
			} else {
				selected = true;
				pathway.setSelectedGenesCount(1);
				geneNode.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.ORANGE, Color.YELLOW));
				setSelectionPoint(point);
			}
		}
	}
	
	public void setSelectedStatus(){
		if (matchTag) {
			if (selected) {
				selected = false;
				geneNode.setBorder(borderStyle);
			} else {
				selected = true;
				geneNode.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.MAGENTA, Color.YELLOW));
			}
		}
	}
	
	public boolean getSelectedStatus(){
		return selected;
	}
	
	public JLabel getLabel(){
		return geneNode;
	}
	
	public String getText(){
		return geneTitle;
	}
	
	public boolean getTag(){
		return matchTag;
	}
	
	public void setLabel(JLabel target){
		geneNode = target;
	}
	
	public void setTest(String targetTitle){
		geneTitle = targetTitle;
	}
	
	public void setTag(boolean match){
		matchTag = match;
	}
	
	public static void setSignificantSites(){
		int fail = 0,pass =0, geneNum = 0;
		int activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo thisTab = ParseKGML.getTabInfo(activeTab,0);
		List<List<String>> data;
		
		HashMap<String, List<Integer>> selection = new HashMap<String, List<Integer>>();
		float threshold = thisTab.getSDThreshold();
		HashMap<String, Genes> matchedGenes = thisTab.getMapedGeneLabel();
		
		for (Entry<String, Genes> oneNode : matchedGenes.entrySet()){
			List<Integer> selectedSites = new ArrayList<Integer>();
			data = thisTab.getDataForGene(oneNode.getKey());
			int multiRegion = data.size();
			selectedSites.clear();
			for (int i = 0; i < multiRegion; i++){
				Statistics calculator = new Statistics(data.get(i));
				
				if (!calculator.checkMissingValues()){
					if (calculator.getStdDev() > threshold){
						
						selectedSites.add(i);
					}
				}
			}
			if (selectedSites.size() == 0){
				selectedSites.add(-1);
			}
			selection.put(oneNode.getKey(), selectedSites);
		}
		
		thisTab.setSelectedSites(selection);
	}
	

	public static void changeBgColor(int newPointer,Character type) {
		
		int activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo thisTab = ParseKGML.getTabInfo(activeTab,0);
		ranges = thisTab.getRanges();
		HashMap<String, Genes> matchedGenes = thisTab.getMapedGeneLabel();
		String value = null;
		List<List<String>> data;
		float threshold = thisTab.getSDThreshold();
		
		if (type.equals('M')){
			
			double sum = 0, average = 0;
			int validSites = 0; int multiRegion = 0; 
			
			for (Entry<String, Genes> oneNode : matchedGenes.entrySet()){
				
				data = thisTab.getDataForGene(oneNode.getKey());
				multiRegion = data.size();
				
				if (multiRegion == 1){
					
					value = data.get(0).get(newPointer);
					if (thisTab.getSelectedSites(oneNode.getKey()) == null){
						
						if (!isNumeric(value)){
							oneNode.getValue().setBgColor(-1);
						}
						else {
							oneNode.getValue().setBgColor(Double.parseDouble(value));
						}
					}
					else {
						
						if (thisTab.getSelectedSites(oneNode.getKey()).get(0) == -1){
							oneNode.getValue().setBgColor(-1);
						}
						else {
							oneNode.getValue().setBgColor(Double.parseDouble(value));
						}
					}
					
				}
				else { // there are multiple regions
					
					sum = 0 ; average = 0; validSites = 0;
					
					if (threshold == 0){
						if ( thisTab.getSelectedSites(oneNode.getKey()) != null ){
							if (thisTab.getSelectedSites(oneNode.getKey()).get(0) == -1){
								// none of the sites have passed SD filtering
								validSites = 0;
							}
							else {
								for (int i : thisTab.getSelectedSites(oneNode.getKey())){
									String val = data.get(i).get(newPointer);
									
									if (!isNumeric(val)){
										continue;
									}
									sum += Double.parseDouble(val);
									validSites ++;
								}
							}
						}
						else {
							for (int i = 0; i < multiRegion; i++){
								String val = data.get(i).get(newPointer);
								
								if (!isNumeric(val)){
									continue;
								}
								sum += Double.parseDouble(val);
								validSites ++;
							}
						}
					}
					
					else if ( (threshold > 0)){  // there is an SD filter
						if (thisTab.getSelectedSites(oneNode.getKey()) == null){
							continue;
						}
						if (thisTab.getSelectedSites(oneNode.getKey()).get(0) == -1){
							// none of the sites have passed SD filtering
							validSites = 0;
						}
						else {
							for (int i : thisTab.getSelectedSites(oneNode.getKey())){
								String val = data.get(i).get(newPointer);
								
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
						oneNode.getValue().setSpecialBgColor(-1);
					}
					else {
						oneNode.getValue().setSpecialBgColor(average);
					}
				}
			}
		}
		else if (type.equals('E')){
			
			for (Entry<String, Genes> oneNode : matchedGenes.entrySet()){
				data = thisTab.getDataForGene(oneNode.getKey());
				value = data.get(0).get(newPointer);
				if (isNumeric(value)){
					oneNode.getValue().setBgColor(Double.parseDouble(value), data);
				}
				else {
					oneNode.getValue().setBgColor(-1, data);
				}
				
			}
		}
	} // end of changeBgColor
	
	public static void changeSecondBoxBgColor(int newPointer,Character type) {
		int activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo methylationPathway = ParseKGML.getTabInfo(activeTab,0);
		TabsInfo expressionPathway = ParseKGML.getTabInfo(activeTab,1);
		List<List<String>> data;
		String value;
		Color bgColor;
		HashMap<String, Genes> matchedGenes = methylationPathway.getMapedGeneLabel();
		for (Entry<String, Genes> oneNode : matchedGenes.entrySet()){
			Genes geneNode = oneNode.getValue();
			JLabel geneLabel = geneNode.getLabel();
			geneLabel.setVisible(true);
			JLabel[] expandedOnes = geneNode.getExpandedLabels();
			data = expressionPathway.getDataForGene(oneNode.getKey());
			value = data.get(0).get(newPointer);
			if (isNumeric(value)){
				bgColor = findColor(Double.parseDouble(value), data);
				expandedOnes[0].setBackground(bgColor);
			}
			else {
				bgColor = findColor(-1, data);
				expandedOnes[0].setBackground(bgColor);
				
			}
		}
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

	private void setBgColor(double parseDouble) {
		Color bgColor = null;
		if (parseDouble == -1){
			bgColor = Color.LIGHT_GRAY;
			geneNode.setForeground(Color.WHITE);
			geneNode.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		}
		else {
			bgColor = getColor(parseDouble);
			geneNode.setBorder(BorderFactory.createLineBorder(new Color(139,69,19), 2));
		}
		geneNode.setBackground(bgColor);
		
		borderStyle = BorderFactory.createLineBorder(new Color(139,69,19), 2);
	}
	
	private void setSpecialBgColor(double parseDouble) {
		Color bgColor = null;
		if (parseDouble == -1){
			bgColor = Color.LIGHT_GRAY;
			geneNode.setBorder(BorderFactory.createMatteBorder(2,2,2,2, Color.LIGHT_GRAY));
			geneNode.setForeground(Color.WHITE);
			
		}
		else {
			bgColor = getColor(parseDouble);
			geneNode.setBorder(BorderFactory.createMatteBorder(2,2,2,2, new Color(0,170,0)));
		}
		geneNode.setBackground(bgColor);
		
		borderStyle = BorderFactory.createMatteBorder(2,2,2,2,new Color(0,170,0));
	}
	
	public static Color findColor(double value, List<List<String>> values){
		Statistics expressionValues = new Statistics(values.get(0));
//		double theMean = expressionValues.getMean();
		double theMedian = expressionValues.getMedian();
		
		double logDifference = Math.log10(value+1) - Math.log10(theMedian);
		double foldDifference = ranges[2];
		
		if (logDifference == Double.NEGATIVE_INFINITY || logDifference == Double.POSITIVE_INFINITY || Double.isNaN(logDifference)){
			return Color.BLACK;
		}
		
		double val = logDifference / foldDifference;
		Color back = Color.WHITE;
		Color overExpressed = Interface.getHypoColor();
		Color underExpressed = Interface.getHyperColor();
		
		if (val > 1) val = 1;
		if (val < -1 ) val = -1;
		
		if (val > 0) {
	   
			float r = (float) (overExpressed.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (overExpressed.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (overExpressed.getBlue() * val + back.getBlue() * (1 - val));
			Color bg = new Color((int) r, (int) g, (int) b);
			return bg;
		}
		else if (val == 0){
			return back;
		}
		else {
			val = -val;
			float r = (float) (underExpressed.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (underExpressed.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (underExpressed.getBlue() * val + back.getBlue() * (1 - val));
	   
			Color bg = new Color((int) r, (int) g, (int) b);
			return bg;
		}
	}
	
	private void setBgColor(double value, List<List<String>> values) {
		Color bgColor = null;
		if (value == -1){
			bgColor = Color.BLACK;
			geneNode.setForeground(Color.WHITE);
		}
		else {
			bgColor = getExpressionLevel(value, values);
		}
		
		geneNode.setBackground(bgColor);
		geneNode.setBorder(BorderFactory.createLineBorder(new Color(139,69,19), 2));
		borderStyle = BorderFactory.createLineBorder(new Color(139,69,19), 2);
	}
	
	private Color getColor(double val1){
		
		double whiteValue = ((ranges[1] - ranges[0]) / 20) + (ranges[0]/10);
		double val = (val1 * 2) -1;
		Color back = Color.WHITE;
		Color hyper = Interface.getHyperColor();
		Color hypo = Interface.getHypoColor();
		if (val > 0) {
	   
			float r = (float) (hyper.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (hyper.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (hyper.getBlue() * val + back.getBlue() * (1 - val));
			Color bg = new Color((int) r, (int) g, (int) b);
			changeTextColor(bg);
			return bg;
		}
		else if (val == whiteValue){
			return back;
		}
		else {
			val = -val;
			float r = (float) (hypo.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (hypo.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (hypo.getBlue() * val + back.getBlue() * (1 - val));
	   
			Color bg = new Color((int) r, (int) g, (int) b);
			changeTextColor(bg);
			return bg;
		}
	}

	
	private Color getExpressionLevel(double value, List<List<String>> values) {
		Statistics expressionValues = new Statistics(values.get(0));
//		double theMean = expressionValues.getMean();
		double theMedian = expressionValues.getMedian();
		
		double logDifference = Math.log10(value+1) - Math.log10(theMedian);
		double foldDifference = ranges[2];
		
		if (logDifference == Double.NEGATIVE_INFINITY || logDifference == Double.POSITIVE_INFINITY || Double.isNaN(logDifference)){
			return Color.BLACK;
		}
		
		double val = logDifference / foldDifference;
		Color back = Color.WHITE;
		Color overExpressed = Interface.getHypoColor();
		Color underExpressed = Interface.getHyperColor();
		if (val > 1) val = 1;
		if (val < -1) val = -1;
		if (val > 0) {
	   
			float r = (float) (overExpressed.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (overExpressed.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (overExpressed.getBlue() * val + back.getBlue() * (1 - val));
			Color bg = new Color((int) r, (int) g, (int) b);
			changeTextColor(bg);
			return bg;
		}
		else if (val == 0){
			return back;
		}
		else {
			val = -val;
			float r = (float) (underExpressed.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (underExpressed.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (underExpressed.getBlue() * val + back.getBlue() * (1 - val));
	   
			Color bg = new Color((int) r, (int) g, (int) b);
			changeTextColor(bg);
			return bg;
		}
		
		
	}

	private void changeTextColor(Color myColor) {
		
		if (myColor.getRed() == 255){
			if (myColor.getGreen() < 40){
				geneNode.setForeground(Color.LIGHT_GRAY);
			}
			else {
				geneNode.setForeground(Color.BLACK);
			}
		}
		else if (myColor.getBlue() == 255){
			if (myColor.getGreen() < 105){
				geneNode.setForeground(Color.WHITE);
			}
			else {
				geneNode.setForeground(Color.BLACK);
			}
		}
	}
	
	public static Color paintLabel(double val1){
		
		double whiteValue = ((ranges[1] - ranges[0]) / 20) + (ranges[0]/10);
		double val = (val1 * 2) -1;
		Color back = Color.WHITE;
		Color hyper = Interface.getHyperColor();
		Color hypo = Interface.getHypoColor();
		if (val > 0) {
	   
			float r = (float) (hyper.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (hyper.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (hyper.getBlue() * val + back.getBlue() * (1 - val));
			Color bg = new Color((int) r, (int) g, (int) b);
			return bg;
		}
		else if (val == whiteValue){
			return back;
		}
		else {
			val = -val;
			float r = (float) (hypo.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (hypo.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (hypo.getBlue() * val + back.getBlue() * (1 - val));
	   
			Color bg = new Color((int) r, (int) g, (int) b);
			return bg;
		}
	}
	
	public static Color paintLabel(double value, List<List<String>> values, int increase) {
		Statistics expressionValues = new Statistics(values.get(0));
//		double theMean = expressionValues.getMean();
		double theMedian = expressionValues.getMedian();
		
		double logDifference =  (Math.log10(value+increase) / Math.log10(2.0) ) -  (Math.log10(theMedian) / Math.log10(2.0));
//		double logDifference =  Math.log10(value+increase) -  Math.log10(theMedian);
		double foldDifference = ranges[2];
		
		if (logDifference == Double.NEGATIVE_INFINITY || logDifference == Double.POSITIVE_INFINITY || Double.isNaN(logDifference)){
			return Color.BLACK;
		}
		
		double val = logDifference / foldDifference;
		Color back = Color.WHITE;
		Color overExpressed = Interface.getHypoColor();
		Color underExpressed = Interface.getHyperColor();
		
		if (val > 1) val = 1;
		if (val < -1) val = -1;
		
		if (val > 0) {
	   
			float r = (float) (overExpressed.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (overExpressed.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (overExpressed.getBlue() * val + back.getBlue() * (1 - val));
			Color bg = new Color((int) r, (int) g, (int) b);
			return bg;
		}
		else if (val == 0){
			return back;
		}
		else {
			val = -val;
			float r = (float) (underExpressed.getRed() * val + back.getRed() * (1 - val));
			float g = (float) (underExpressed.getGreen() * val + back.getGreen() * (1 - val));
			float b = (float) (underExpressed.getBlue() * val + back.getBlue() * (1 - val));
	   
			Color bg = new Color((int) r, (int) g, (int) b);
			return bg;
		}
	}
	

	public void addExpandedLabels(JLabel[] extraLabels) {
		int expansionSize = extraLabels.length;
		extendedLabels = new JLabel[expansionSize];
		extendedLabels = extraLabels;
	}
	
	public JLabel[] getExpandedLabels(){
		return extendedLabels;
	}

	public static void highlightGenes() {
		int activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo thisTab = ParseKGML.getTabInfo(activeTab,0);
		HashMap<String, Genes> matchedGenes = thisTab.getMapedGeneLabel();
		for (Entry<String, Genes> oneNode : matchedGenes.entrySet()){
			JLabel geneLabel = oneNode.getValue().getLabel();
			geneLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			geneLabel.setForeground(Color.BLACK);
			geneLabel.setBackground(Color.ORANGE);
		}
	}
}

class Statistics {
    List<String> data;
    int size;
    Float[] measurements;
    boolean missingvalues = false;
    
    public Statistics(List<String> list)
    {
        this.data = list;
        size = list.size();
        //        this.measurements = list.toArray();
        
    }
    
    double getMedian(){
    	
    	measurements = new Float[data.size()];
        for (int i = 0 ; i < data.size(); i++){
        	measurements[i] = Float.parseFloat(data.get(i));
        }
        Arrays.sort(measurements);
    	if (measurements.length % 2 == 0)
    	{
    		double a = Double.parseDouble(measurements[(measurements.length / 2) - 1].toString()) +1 ;
    		double b = Double.parseDouble(measurements[(measurements.length / 2) ].toString()) + 1;
    		return (a + b) / 2.0;
    	}
    	else
    	{
    		return Double.parseDouble(measurements[measurements.length / 2].toString()) + 1;
    	}
    }
    
    boolean checkMissingValues() {
    	int naCounter = 0;
    	
    	for(String a : data){
        	if (!isNumeric(a)){
        		naCounter++;
        		continue;
        	}
        	if (Double.isNaN(Double.parseDouble(a))){
        		naCounter ++;
        		continue;
        	}
        	
        }
    	
		if (naCounter >= (size/3)){
			return true;
		}
		return false;
	}
    
	double getMean()
    {
        double sum = 0.0;
        int naCounter = 0;
        
        for(String a : data){
        	if (!isNumeric(a)){
        		naCounter++;
        		continue;
        	}
        	if (Double.isNaN(Double.parseDouble(a))){
        		naCounter ++;
        		continue;
        	}
        	else {
        		sum += Double.parseDouble(a) + 1;
        	}
        }
        
        return sum/(size - naCounter);
    }
    
    private boolean isNumeric(String str) {
    	try {
		    double d = Double.parseDouble(str);
		}
		catch(NumberFormatException nfe){
		    return false;
		}
		return true;
	}
    
	double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        int naCounter = 0;
        for(String a :data){
        	if (!isNumeric(a)){
        		naCounter ++;
        		continue;
        	}
        	if (Double.isNaN(Double.parseDouble(a))){
        		naCounter ++;
        		continue;
        	}
        	else {
        		temp += (mean-(Double.parseDouble(a)+1))*(mean-(Double.parseDouble(a)+1));
        	}
        }
        
        return temp/(size - naCounter);
    }
    
    double getStdDev()
    {
        return Math.sqrt(getVariance());
    }
    
}