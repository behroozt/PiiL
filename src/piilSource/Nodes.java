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

import java.awt.Shape;

public class Nodes {

	private String bgColor ;
	private String fgColor; 
	private float cordX;
	private float cordY;
	private float width;
	private float height;
	private Shape nodeShape;
	private String nodeLabel;
	private String entryName;
	private String entryType;
	private String entryLink;
	private String componentID;

	
	public Nodes(float nodeX, float nodeY, float nodeWidth, float nodeHeight,
			String nodeBgColor, String nodeFgColor, String nodeName, String nodeType,
			String nodeLink, String graphicLabel, String compntID) {
		
		cordX = nodeX;
		cordY = nodeY;
		width = nodeWidth;
		height = nodeHeight;
		entryName = nodeName;
		entryType = nodeType;
		entryLink = nodeLink;
		bgColor = nodeBgColor;
		fgColor = nodeFgColor;
		nodeLabel = graphicLabel;
		componentID = compntID;
	}

	public Nodes(float nodeX, float nodeY, float nodeWidth, float nodeHeight,
			String nodeBgColor, String nodeFgColor, String nodeName, String nodeType,
			String nodeLink, String graphicLabel) {
		
		cordX = nodeX;
		cordY = nodeY;
		width = nodeWidth;
		height = nodeHeight;
		entryName = nodeName;
		entryType = nodeType;
		entryLink = nodeLink;
		bgColor = nodeBgColor;
		fgColor = nodeFgColor;
		nodeLabel = graphicLabel.trim();
		
	}

	public void setGraphicShape(Shape graphicShape) {
		nodeShape = graphicShape;
		
	}

	public Shape getNodeShape() {
		return nodeShape;
	}

	public String getNodeType() {
		return entryType;
	}

	public float getNodeCordX() {
		return cordX;
	}

	public float getNodeCordY() {
		return cordY;
	}

	public float getNodeWidth() {
		return width;
	}

	public float getNodeHeight() {
		return height;
	}

	public String getNodeLabel() {
		return nodeLabel;
	}

	public String getNodeBgColor() {
		return bgColor;
	}

	public String getComponentID() {
		return componentID;
	}

}
