/*  
    PiiL: Pathways Interactive vIsualization tooL
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

