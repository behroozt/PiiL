/*  
    PiiL: Pathways Interactive vIsualization tooL
    Copyright (C) 2016  Behrooz Torabi Moghadam

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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.freehep.util.export.ExportDialog;


public class GeneRegions extends JFrame{
	
	String nodeID;
	int activeTab,pointer, multipleHits;
	List<List<String>> values;
	float increment;
	List<JLabel> allLabels;
	JLabel geneLabel;
	int labelX, labelY,labelWidth, labelHeight;
	public static JPanel northPanel, southPanel, mainPanel;
	public static JFrame magnifyFrame;
	JButton exportButton, closeButton;
	String geneName;
	
	
	public GeneRegions(String entryID){
		
		nodeID = entryID;
		activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
		pointer = pathway.getPointer();
		geneName = pathway.getMapedGeneLabel().get(nodeID).getText();
		
		if (pointer < 0){
			pointer = 0;
		}
		values = pathway.getMapedGeneData().get(nodeID);
		
		multipleHits = values.size();
		increment = 460 / multipleHits;
		
		exportButton = new JButton("Export to image");
		exportButton.setPreferredSize(new Dimension(150, 30));
		closeButton= new JButton("Close");
		closeButton.setPreferredSize(new Dimension(150,30));
		
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
		
		if (numberOfRegions > 5){
			labelX = 25; labelY= 45; labelWidth =15;
			for (int i =0; i < numberOfRegions; i ++){
				DecimalFormat df=new DecimalFormat("0.000");
				Double betaValue = Double.parseDouble(values.get(i).get(pointer));
				String regionName = tag.toArray()[i].toString() + " (" + df.format(betaValue) + ")";
				JLabel newLabel = new JLabel(regionName,SwingConstants.CENTER);
				newLabel.setUI(new VerticalLabelUI(true));
				newLabel.setHorizontalAlignment(SwingConstants.LEFT);
				labelHeight = regionName.length() * 10;
				newLabel.setBounds((int) (labelX + (i * increment)), labelY, labelWidth, labelHeight);
				allLabels.add(newLabel);
			}
		}
		else {
			labelX = 25; labelY= 45; labelHeight =15;
			for (int i =0; i < numberOfRegions; i ++){
				DecimalFormat df=new DecimalFormat("0.000");
				Double betaValue = Double.parseDouble(values.get(i).get(pointer));
				String regionName = tag.toArray()[i].toString() + " (" + df.format(betaValue) + ")";
				JLabel newLabel = new JLabel(regionName,SwingConstants.CENTER);
				newLabel.setHorizontalAlignment(SwingConstants.LEFT);
				labelWidth = regionName.length() * 10;
				newLabel.setBounds((int) (labelX + (i * increment)), labelY, labelWidth, labelHeight);
				allLabels.add(newLabel);
			}
		}
		
		magnifyFrame = new JFrame();
		magnifyFrame.setLayout(new BorderLayout());
		northPanel = new JPanel();
		southPanel = new JPanel();
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		northPanel.setLayout(new BorderLayout());
		southPanel.setLayout(new FlowLayout());
		magnifyFrame.setSize(500, 240);
		Toolkit tk = Toolkit.getDefaultToolkit();
		magnifyFrame.setResizable(false);
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (magnifyFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (magnifyFrame.getHeight() / 2);
		magnifyFrame.setLocation(xPos,yPos);
		magnifyFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		magnifyFrame.setTitle("Genomic details of " + geneName);
		
		northPanel.add(geneLabel,BorderLayout.CENTER);
		for (JLabel region : allLabels){
			northPanel.add(region,BorderLayout.CENTER);
		}
		DrawGene regionalMap = new DrawGene();
		northPanel.add(regionalMap, BorderLayout.CENTER);
		southPanel.add(exportButton);
		southPanel.add(closeButton);
		mainPanel.add(northPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		magnifyFrame.add(mainPanel);
		magnifyFrame.setVisible(true);
	}
	
	private class DrawGene extends JPanel {

		private Color getColor(double val)
		{
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
		    return myColor;
		}
		
		public void paint(Graphics g){
			
			Graphics2D graphical = (Graphics2D) g;
					
			graphical.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Shape element = new Rectangle2D.Float(20,40,460,135);
			float shapeW = element.getBounds().width;
			float shapeH = element.getBounds().height;
			float shapeX = element.getBounds().x;
			float shapeY = element.getBounds().y;
			float intent = shapeW / multipleHits;
			
			for (int j = 0; j < multipleHits ; j++){
				float value = Float.parseFloat(values.get(j).get(pointer));
				Color geneColor = getColor(value);
				graphical.setColor(geneColor);
				Shape nodeShape = new Rectangle2D.Float(shapeX + (j * intent), shapeY, intent, shapeH);
				graphical.fill(nodeShape);
				graphical.setColor(Color.BLACK);
				graphical.draw(nodeShape);
				
				if (geneColor.getRed() == 255){
					if (geneColor.getGreen() < 40){
						allLabels.get(j).setForeground(Color.LIGHT_GRAY);
					}
					else {
						allLabels.get(j).setForeground(Color.BLACK);
					}
				}
				else if (geneColor.getBlue() == 255){
					if (geneColor.getGreen() < 105){
						allLabels.get(j).setForeground(Color.WHITE);
					}
					else {
						allLabels.get(j).setForeground(Color.BLACK);
					}
				}
				
			}
			
			graphical.setColor(Color.BLACK);
			graphical.draw(element);
			
		}
	}
	
}
