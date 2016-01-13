package piilSource;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

public class Edges {

	private String entryOneID;
	private String entryTwoID;
	private String type;
	private String relationName; 
	private String value;
	private Shape edgeLine;
	private Stroke edgeStroke;
	private Color edgeColor;
	
	public Edges(String entry1ID, String entry2ID, String edgeType, String edgeName, String edgeValue, Shape edgeModel, Stroke lineStroke, Color lineColor){
		
		entryOneID = entry1ID;
		entryTwoID = entry2ID;
		type = edgeType;
		relationName = edgeName;
		value = edgeValue; 
		edgeLine = edgeModel;
		edgeStroke = lineStroke;
		edgeColor = lineColor;
	}
	
	public String getEntryOneID(){
		return entryOneID;
	}
	
	public String getEntryTwoID(){
		return entryTwoID;
	}
	
	public String getType(){
		return type;
	}
	
	public String getRelationName(){
		return relationName;
	}
	
	public String getValue(){
		return value;
	}
	
	public Shape getEdgeLine(){
		return edgeLine;
	}
	
	public Stroke getStroke(){
		return edgeStroke;
	}
	
	public Color getEdgeColor(){
		return edgeColor;
	}
	
}

