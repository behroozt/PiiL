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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingConstants;
import org.freehep.util.export.ExportDialog;


public class GeneRegions extends JFrame{
	
	String nodeID;
	int activeTab,pointer, multipleHits;
	List<List<String>> values;
	float increment;
	List<JLabel> allLabels;
	JLabel geneLabel;
	int labelX, labelY,labelWidth, labelHeight, frameWidth, frameHeight, maxWidth;
	public static JPanel northPanel, southPanel, mainPanel, labelPanel, regionsPanel;
	JPanel centerPanel;
	public static JFrame magnifyFrame;
	JButton exportButton, closeButton;
	String geneName;
	JScrollPane regionsPane;
	float[] ranges;
	Color bgColor;
	GridBagConstraints gridConstraints;
	
	
	public GeneRegions(String entryID){
		
		nodeID = entryID;
		activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
		pointer = pathway.getPointer();
		geneName = pathway.getMapedGeneLabel().get(nodeID).getText();
		ranges = pathway.getRanges();
		gridConstraints = new GridBagConstraints();
		
		if (pointer < 0){
			pointer = 0;
		}
		values = pathway.getMapedGeneData().get(nodeID);
		
		multipleHits = values.size();
		
		
		exportButton = new JButton("Export to image");
		exportButton.setPreferredSize(new Dimension(150, 33));
		closeButton= new JButton("Close");
		closeButton.setPreferredSize(new Dimension(150,33));
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent bc) {
				ExportDialog export = new ExportDialog();
				export.showExportDialog(magnifyFrame, "Export view as ...",northPanel, geneName + "_regions");
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				magnifyFrame.dispose();
			}
		});
		
		geneLabel = new JLabel();
		String geneTitle = "CpG site(s) genomic annotation for the " + geneName + " gene";
		geneLabel.setText(geneTitle);
		geneLabel.setBounds(50, 10, geneTitle.length() * 10, 20);
		geneLabel.setFont(new Font("Serif", Font.BOLD, 18));
		allLabels = new ArrayList<JLabel>();
		
		Collection<String> tag = pathway.getMapedGeneRegion().get(nodeID);
		int numberOfRegions = tag.size();
		
		labelX = 10; labelY= 10; labelHeight= 15; maxWidth=0;
		for (int i =0; i < numberOfRegions; i ++){
			DecimalFormat df=new DecimalFormat("0.000");
			if (!isNumeric(values.get(i).get(pointer))){
				continue;
			}
			Double betaValue = Double.parseDouble(values.get(i).get(pointer));
			bgColor = getColor(betaValue);
			String regionName = tag.toArray()[i].toString() + " (" + df.format(betaValue) + ")";
			JLabel newLabel = new JLabel(regionName,SwingConstants.CENTER);
			newLabel.setHorizontalAlignment(SwingConstants.CENTER);
			labelWidth = regionName.length() * 10 + 5;
			if (labelWidth > maxWidth){
				maxWidth = labelWidth;
			}
			
			newLabel.setOpaque(true);
			newLabel.setBackground(bgColor);
			newLabel.setForeground(setTextColor(bgColor));
			newLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			allLabels.add(newLabel);
		}
		frameWidth = maxWidth + 10;
		frameHeight = numberOfRegions * 20 + 10 ;
		
		
		magnifyFrame = new JFrame();
		magnifyFrame.setLayout(new BorderLayout());
		northPanel = new JPanel();
		southPanel = new JPanel();
		mainPanel = new JPanel();
		labelPanel = new JPanel();
		centerPanel = new JPanel();
		regionsPanel = new JPanel();
		regionsPanel.setLayout(new GridBagLayout());
		regionsPane = new JScrollPane(regionsPanel);
		labelPanel.setLayout(new FlowLayout());
		labelPanel.setPreferredSize(new Dimension(500,40));
		centerPanel.setPreferredSize(new Dimension(500,170));
		northPanel.setPreferredSize(new Dimension(500,250));
		southPanel.setPreferredSize(new Dimension(500,60));
		centerPanel.setLayout(new FlowLayout());
		regionsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		regionsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.setLayout(new BorderLayout());
		northPanel.setLayout(new BorderLayout());
		southPanel.setLayout(new GridBagLayout());
		magnifyFrame.setSize(500, 310);
		Toolkit tk = Toolkit.getDefaultToolkit();
		magnifyFrame.setResizable(false);
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (magnifyFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (magnifyFrame.getHeight() / 2);
		magnifyFrame.setLocation(xPos,yPos);
		magnifyFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		magnifyFrame.setTitle("Genomic details of " + geneName);
		
		labelPanel.add(geneLabel);
		for (int i = 0 ; i < numberOfRegions ; i ++) {
			JLabel region = allLabels.get(i);
			region.setPreferredSize(new Dimension(300,20));
			addComp(regionsPanel, region, 0, i, 1, 1,GridBagConstraints.NORTH, GridBagConstraints.NONE);
		}
		
		regionsPanel.setPreferredSize(new Dimension((frameWidth + 5), (frameHeight+5))) ;
		
		gridConstraints.insets = new Insets(2,2,2,2);
		addComp(southPanel, exportButton, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.CENTER);
		
		addComp(southPanel, closeButton, 1, 0, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.CENTER);
		
		
		regionsPane.setPreferredSize(new Dimension(350,160));
		regionsPane.getVerticalScrollBar().setUnitIncrement(16);
		regionsPane.getHorizontalScrollBar().setUnitIncrement(16);
		centerPanel.add(regionsPane);
				
		northPanel.add(labelPanel, BorderLayout.NORTH);
		northPanel.add(centerPanel, BorderLayout.CENTER);
		northPanel.setBackground(Color.red);
		southPanel.setBackground(Color.BLUE);
		mainPanel.add(northPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		magnifyFrame.add(mainPanel);
		magnifyFrame.setVisible(true);
	}
	
	private static boolean isNumeric(String str) {
		try {  
		    double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe){  
		    return false;  
		}  
		return true;
	} // end of isNumeric
	
	private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch){
		
		gridConstraints.gridx = xPos;
		gridConstraints.gridy = yPos;
		gridConstraints.gridwidth = compWidth;
		gridConstraints.gridheight = compHeight;
		gridConstraints.weightx = 0;
		gridConstraints.weighty = 0;
//		gridConstraints.insets = new Insets(0,0,0,5);
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);
	} // end of addComp
	
	private Color getColor(double val) {
		
		double r = 0,b = 0,g = 0;
		
		double whiteValue = ((ranges[1] - ranges[0]) / 20) + (ranges[0]/10);
		
		double difference = 255 / (whiteValue - (ranges[0]/10));
		
		if (val == whiteValue){
			r = 255; b=255; g = 255;
		}
		else if (val < whiteValue){
			b= 255; r = 255 - Math.round(difference * (whiteValue - val)); g = 255 - Math.round(difference * (whiteValue - val));
		}
		else if (val > whiteValue){ 
			r = 255; b = 255 - Math.round(difference * (val - whiteValue)); g = 255 - Math.round(difference * (val - whiteValue));
		}
		
		Color myColor = new Color(r < 0 ? 0 : (int) (r),g < 0 ? 0 : (int) (g), b < 0 ? 0 : (int) (b));
		
	    return myColor;
	} // end of getColor
	
	private Color setTextColor(Color myColor) {
		
		Color fgColor = Color.WHITE;
		if (myColor.getRed() == 255){
			if (myColor.getGreen() < 40){
				fgColor = Color.LIGHT_GRAY;
			}
			else {
				fgColor = Color.BLACK;
			}
		}
		else if (myColor.getBlue() == 255){
			if (myColor.getGreen() < 105){
				fgColor = Color.WHITE;
			}
			else {
				fgColor = Color.BLACK;
			}
		}
		return fgColor;
	} // end of setTextColor
}
