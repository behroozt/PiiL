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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

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
			TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
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
				geneNode.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.ORANGE, Color.YELLOW));
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

	public static void changeBgColor(int newPointer,Character type) {
		
		int activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo thisTab = ParseKGML.getTabInfo(activeTab);
		HashMap<String, Genes> matchedGenes = thisTab.getMapedGeneLabel();
		String value;
		List<List<String>> data;
		
		if (type.equals('M')){
			for (Entry<String, Genes> oneNode : matchedGenes.entrySet()){
				data = thisTab.getDataForGene(oneNode.getKey());
				int multiRegion = data.size();
				if (multiRegion == 1){
					value = data.get(0).get(newPointer);
					if (!isNumeric(value)){
						oneNode.getValue().setBgColor(-1);
					}
					else {
						oneNode.getValue().setBgColor(Double.parseDouble(value));
					}
					
				}
				else { // there are multipel regions
					double sum = 0, average = 0;
					for (int i = 0; i < multiRegion; i++){
						String val = data.get(i).get(newPointer);
						
						if (!isNumeric(val)){
							continue;
						}
						sum += Double.parseDouble(val);
					}
					
					average = sum / multiRegion;
					if (Double.isNaN(average)){
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
			bgColor = Color.BLACK;
			geneNode.setForeground(Color.WHITE);
		}
		else {
			bgColor = getColor(parseDouble);
		}
		geneNode.setBackground(bgColor);
		geneNode.setBorder(BorderFactory.createLineBorder(new Color(139,69,19), 2));
		borderStyle = BorderFactory.createLineBorder(new Color(139,69,19), 2);
	}
	
	private void setSpecialBgColor(double parseDouble) {
		Color bgColor = null;
		if (parseDouble == -1){
			bgColor = Color.BLACK;
			geneNode.setForeground(Color.WHITE);
		}
		else {
			bgColor = getColor(parseDouble);
		}
		geneNode.setBackground(bgColor);
		geneNode.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.MAGENTA));
		borderStyle = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.MAGENTA);
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
	
	private Color getColor(double val) {
		
		double r = 0,b = 0,g = 0;
		
		if (val == 0.5){
			r = 255; b=255; g = 255;
		}
		else if (val < 0.5){
			b= 255; r = 255 - Math.round(510 * (0.5 - val)); g = 255 - Math.round(510 * (0.5 - val));
		}
		else if (val > 0.5){ 
			r = 255; b = 255 - Math.round(510 * (val - 0.5)); g = 255 - Math.round(510 * (val - 0.5));
		}
		Color myColor = new Color((int) (r),(int) (g), (int) (b));
		
		changeTextColor(myColor);
	    return myColor;
	}
	
	private Color getExpressionLevel(double value, List<List<String>> values) {
		
		Statistics expressionValues = new Statistics(values.get(0));
		double theMean = expressionValues.getMean();
		double r = 0,b = 0,g = 0;
		double logDifference = Math.log10(value) - Math.log10(theMean);
		
		if (logDifference == Double.NEGATIVE_INFINITY || logDifference == Double.POSITIVE_INFINITY){
			logDifference = 0;
		}
		
		if (logDifference == 0){
			r=255; b=255; g=255;
		}
		else if (logDifference < 0) {
			
			b= 255; r = 255 - Math.round(logDifference * -63.75); g = 255 - Math.round(logDifference * -63.75);
		}
		else if (logDifference > 0){
			r = 255; b = 255 - Math.round(logDifference * 63.75); g = 255 - Math.round(logDifference * 63.75);
		}
		
		Color expressionColor = new Color((int)r, (int) g, (int) b);
		changeTextColor(expressionColor);
		return expressionColor;
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
	
	public static Color paintLabel(double d){
		double r = 0,b = 0,g = 0;
		double value = d;
		if (value == 0.5){
			r = 255; b=255; g = 255;
		}
		else if (value < 0.5){
			b= 255; r = 255 - Math.round(510 * (0.5 - value)); g = 255 - Math.round(510 * (0.5 - value));
		}
		else if (value > 0.5){ 
			r = 255; b = 255 - Math.round(510 * (value - 0.5)); g = 255 - Math.round(510 * (value - 0.5));
		}
		Color myColor = new Color((int) (r),(int) (g), (int) (b));
		
		return myColor;
	}
	
	public static Color paintLabel(double value, List<List<String>> values) {
		
		Statistics expressionValues = new Statistics(values.get(0));
		double theMean = expressionValues.getMean();
		double r = 0,b = 0,g = 0;
		double logDifference = Math.log10(value) - Math.log10(theMean);
		
		if (logDifference == Double.NEGATIVE_INFINITY || logDifference == Double.POSITIVE_INFINITY){
			logDifference = 0;
		}
		
		if (logDifference == 0){
			r=255; b=255; g=255;
		}
		else if (logDifference < 0) {
			
			b= 255; r = 255 - Math.round(logDifference * -63.75); g = 255 - Math.round(logDifference * -63.75);
		}
		else if (logDifference > 0){
			r = 255; b = 255 - Math.round(logDifference * 63.75); g = 255 - Math.round(logDifference * 63.75);
		}
		
		Color expressionColor = new Color((int)r, (int) g, (int) b);
		return expressionColor;
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
		TabsInfo thisTab = ParseKGML.getTabInfo(activeTab);
		HashMap<String, Genes> matchedGenes = thisTab.getMapedGeneLabel();
		for (Entry<String, Genes> oneNode : matchedGenes.entrySet()){
			JLabel geneLabel = oneNode.getValue().getLabel();
			geneLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			geneLabel.setForeground(Color.red);
			geneLabel.setBackground(Color.white);
		}
		
		
	}

}

class Statistics {
    List<String> data;
    int size; 
    Object[] measurements;

    public Statistics(List<String> list) 
    {
        this.data = list;
        size = list.size();
        this.measurements = list.toArray();

    }   

    double getMean()
    {
        double sum = 0.0;
        for(String a : data){
        	if (!isNumeric(a)){
        		continue;
        	}
        	else {
        		sum += Double.parseDouble(a);
        	}
        }
        return sum/size;
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
        for(String a :data){
        	if (Double.isNaN(Double.parseDouble(a))){
        		continue;
        	}
        	else {
        		temp += (mean-Double.parseDouble(a))*(mean-Double.parseDouble(a));
        	}
        }
            
        return temp/size;
    }

    double getStdDev()
    {
        return Math.sqrt(getVariance());
    }
    
}
