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
