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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.freehep.util.export.ExportDialog;



public class GeneRegions extends JFrame{
	
	String nodeID;
	static int activeTab;
	int pointer, multipleHits;
	static List<List<String>> values;
	float increment;
	static List<JCheckBox> allLabels;
	
	JLabel geneLabel;
	int labelX, labelY,labelWidth, labelHeight, frameWidth, frameHeight, maxWidth;
	static JPanel northPanel, southPanel, mainPanel, labelPanel, regionsPanel, controlPanel;
	JPanel centerPanel;
	JFrame magnifyFrame;
	JButton exportButton, closeButton, applyButton;
	String geneName;
	JScrollPane regionsPane;
	static float[] ranges;
	static Color bgColor;
	GridBagConstraints gridConstraints;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	int NAcounter = 0;
	float sdThreshold;
	List<Integer> selectedSites;
	static TabsInfo  pathway;
	static int numberOfRegions;
	static Collection<String> tag;
	
	public GeneRegions(String entryID, int index, TabsInfo tab){
		
		activeTab  = index;
		pathway = tab;
		nodeID = entryID;
		pointer = pathway.getPointer();
		geneName = pathway.getMapedGeneLabel().get(nodeID).getText();
		ranges = pathway.getRanges();
		gridConstraints = new GridBagConstraints();
		sdThreshold = pathway.getSDThreshold();
		allLabels = new ArrayList<JCheckBox>();
		
		if (pointer < 0){
			pointer = 0;
		}
		values = pathway.getMapedGeneData().get(nodeID);
		tag = pathway.getMapedGeneRegion().get(nodeID);
		
		multipleHits = values.size();
		
		exportButton = new JButton("Export to image");
		exportButton.setPreferredSize(new Dimension(130, 32));
		closeButton= new JButton("Close");
		closeButton.setPreferredSize(new Dimension(130,32));
		applyButton = new JButton("Apply changes");
		applyButton.setPreferredSize(new Dimension(130,32));
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent bc) {
				ExportDialog export = new ExportDialog();
				export.showExportDialog(magnifyFrame, "Export view as ...",northPanel, geneName + "_regions");
			}
		});
		
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent bc) {
				List<Integer> selected = new ArrayList<Integer>(); 
				for (int i = 0 ; i < allLabels.size() ; i ++){
					if (allLabels.get(i).isSelected()){
						selected.add(i);						
					}
				}
				if (selected.size() == 0){
					selected.add(-1);
				}
				HashMap<String, List<Integer>> selection = pathway.getAllSites();
				
				for (Entry<String, Genes> gene : pathway.getMapedGeneLabel().entrySet()){
					if (gene.getValue().getText().equals(geneName)){
						selection.put(gene.getKey(),selected);
					}
				}
				pathway.setSelectedSites(selection);
				
				if (Controler.samplesIDsCombo.getSelectedIndex() == pathway.getPointer()){
					Genes.changeBgColor(pathway.getPointer(),pathway.getMetaType());
				}
				else {
					ControlPanel.samplesIDsCombo.setSelectedIndex(Controler.getPointer());
				}
			}
		});
		
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Controler.stopTimer();
				magnifyFrame.dispose();
			}
		});
		
		geneLabel = new JLabel();
		String geneTitle = multipleHits + " CpG site(s) annotated to the " + geneName + " gene";
		geneLabel.setText(geneTitle);
		geneLabel.setBounds(50, 25, geneTitle.length() * 10, 20);
		geneLabel.setFont(new Font("Serif", Font.BOLD, 18));
//		allLabels = new ArrayList<JCheckBox>();
		
		numberOfRegions = tag.size();
		
		labelX = 10; labelY= 10; labelHeight= 15; maxWidth=0;
		DecimalFormat df = new DecimalFormat("0.000");
		for (int i =0; i < numberOfRegions; i ++){
			
			if (!isNumeric(values.get(i).get(pointer))){
				
				String regionName = tag.toArray()[i].toString(); // + " (NA)";
				JCheckBox newLabel = new JCheckBox(regionName);
				newLabel.setToolTipText("NA");
				newLabel.setHorizontalAlignment(SwingConstants.LEFT);
				labelWidth = regionName.length() * 10 + 5;
				newLabel.setOpaque(true);
				
				newLabel.setBackground(Color.GRAY);
				newLabel.setForeground(setTextColor(Color.WHITE));
				newLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				allLabels.add(newLabel);
				continue;
			}
			Double betaValue = Double.parseDouble(values.get(i).get(pointer));
			bgColor = getColor(betaValue);
			String regionName = tag.toArray()[i].toString(); // + " (" + df.format(betaValue) + ")";
			JCheckBox newLabel = new JCheckBox(regionName);
			newLabel.setToolTipText(String.valueOf(df.format(betaValue)));
			newLabel.setHorizontalAlignment(SwingConstants.LEFT);
			labelWidth = regionName.length() * 10 + 10;
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
		
		magnifyFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        Controler.stopTimer();
		    }
		});
		
		northPanel = new JPanel();
		southPanel = new JPanel();
		mainPanel = new JPanel();
		labelPanel = new JPanel();
		centerPanel = new JPanel();
		regionsPanel = new JPanel();
		controlPanel = new Controler().makeSidePanel();
		
		controlPanel.setPreferredSize(new Dimension(150,360));
		regionsPanel.setLayout(new GridBagLayout());
		regionsPane = new JScrollPane(regionsPanel);
		labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,10));
		labelPanel.setPreferredSize(new Dimension(450,40));
		centerPanel.setPreferredSize(new Dimension(450,170));
		northPanel.setPreferredSize(new Dimension(450,250));
		southPanel.setPreferredSize(new Dimension(450,60));
		centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,10));
		regionsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		regionsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.setLayout(new BorderLayout());
		northPanel.setLayout(new BorderLayout());
		southPanel.setLayout(new GridBagLayout());
		magnifyFrame.setSize(600, 410);
		Toolkit tk = Toolkit.getDefaultToolkit();
		magnifyFrame.setResizable(false);
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (magnifyFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (magnifyFrame.getHeight() / 2);
		magnifyFrame.setLocation(xPos,yPos);
		magnifyFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		magnifyFrame.setTitle("CpG site(s) details of " + geneName);
		
		labelPanel.add(geneLabel);
		selectedSites = new ArrayList<Integer>();
		selectedSites = pathway.getSelectedSites(nodeID);
		for (int i = 0 ; i < numberOfRegions ; i ++) {
			JCheckBox region = allLabels.get(i);
			region.setPreferredSize(new Dimension(300,20));
			
			region.setAlignmentX(GridBagConstraints.WEST);
			region.setBorderPainted(true);
			if (sdThreshold > 0 || selectedSites != null){
				
				for (Integer item : selectedSites){
					if (item == i ){
						region.setSelected(true);
					}
				}
			}
			else {
				region.setSelected(true);
			}
			addComp(regionsPanel, region, 0, i, 1, 1,GridBagConstraints.NORTH, GridBagConstraints.NONE);
			
		}
		
		regionsPanel.setPreferredSize(new Dimension((frameWidth + 5), (frameHeight+5))) ;
		
		gridConstraints.insets = new Insets(2,2,2,2);
		addComp(southPanel, exportButton, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.CENTER);
		addComp(southPanel, applyButton, 1, 0, 1, 1, GridBagConstraints.EAST, GridBagConstraints.CENTER);
		addComp(southPanel, closeButton, 2, 0, 2, 1, GridBagConstraints.CENTER, GridBagConstraints.CENTER);
		
		regionsPane.setPreferredSize(new Dimension(380,270));
		regionsPane.getVerticalScrollBar().setUnitIncrement(16);
		regionsPane.getHorizontalScrollBar().setUnitIncrement(16);
		centerPanel.add(regionsPane);
				
		northPanel.add(labelPanel, BorderLayout.NORTH);
		northPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(northPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		magnifyFrame.add(controlPanel, BorderLayout.WEST);
		magnifyFrame.add(mainPanel, BorderLayout.EAST);
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
	
	private static Color getColor(double val) {
		
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
	
	private static Color setTextColor(Color myColor) {
		
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
	
	public static void setSitesColor(int pointer){
		
		Double betaValue;
		DecimalFormat df=new DecimalFormat("0.000");
		
		for (int i =0; i < numberOfRegions; i ++){
			JCheckBox site = allLabels.get(i);
			
			if (!isNumeric(values.get(i).get(pointer))){
				site.setBackground(Color.GRAY);
				site.setForeground(setTextColor(Color.WHITE));
				site.setToolTipText("NA");
				continue;
			}
			betaValue = Double.parseDouble(values.get(i).get(pointer));
			
			bgColor = getColor(betaValue);
			site.setBackground(bgColor);
			site.setForeground(setTextColor(bgColor));
			site.setToolTipText(String.valueOf(df.format(betaValue)));
		}
		
	}
	
	private static class Controler extends JPanel {
		
		JPanel holderPanel, buttonsPanel, emptyPanel;
		static JButton nextButton, previousButton, firstButton, lastButton, playButton;
		static JComboBox samplesIDsCombo;
		static boolean repeatPlayback;
		static JSlider timerSpeed;
		JLabel sampleTitle;
		static JLabel sampleIndexLabel;
		static int mainDelay = 1000;
		
		public static int getPointer(){
			return samplesIDsCombo.getSelectedIndex();
		}
		
		public JPanel makeSidePanel() {
			
			holderPanel = new JPanel();
			holderPanel.setLayout(new BorderLayout());
			buttonsPanel = new JPanel();
			emptyPanel = new JPanel();
			emptyPanel.setPreferredSize(new Dimension(150,40));
			
			nextButton = new JButton(">");
			nextButton.setBorder(new RoundedCornerBorder());
			nextButton.setPreferredSize(new Dimension(80,26));
			nextButton.setToolTipText("show next sample");
			
			previousButton = new JButton("<");
			previousButton.setBorder(new RoundedCornerBorder());
			previousButton.setPreferredSize(new Dimension(80,26));
			previousButton.setToolTipText("show previous sample");
			
			firstButton = new JButton("<<");
			firstButton.setBorder(new RoundedCornerBorder());
			firstButton.setPreferredSize(new Dimension(80,26));
			firstButton.setToolTipText("show the first sample");
			
			lastButton = new JButton(">>");
			lastButton.setBorder(new RoundedCornerBorder());
			lastButton.setPreferredSize(new Dimension(80,26));
			lastButton.setToolTipText("show the last sample");
			
			playButton = new JButton("Play");
			playButton.setBorder(new RoundedCornerBorder());
			playButton.setPreferredSize(new Dimension(80,26));
			playButton.setToolTipText("show samples consecutively");
			
			timerSpeed = new JSlider(JSlider.HORIZONTAL, 1,4,2);
			timerSpeed.setPaintLabels(true);
			timerSpeed.setMinorTickSpacing(1);
			timerSpeed.setMajorTickSpacing(1);
			timerSpeed.setPaintTicks(true);
			timerSpeed.setToolTipText("Playback speed");
			timerSpeed.setOpaque(true);
			timerSpeed.setPreferredSize(new Dimension(110, 40));
			
			sampleTitle = new JLabel("Sample ID", JLabel.CENTER);
			
			samplesIDsCombo = new JComboBox();
			samplesIDsCombo.setPreferredSize(new Dimension(135, 30));
			samplesIDsCombo.setBorder(new RoundedCornerBorder());
			samplesIDsCombo.setPrototypeDisplayValue("XXXXXXXX");
			
			sampleIndexLabel = new JLabel("index");
			
			buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
			buttonsPanel.add(nextButton);
			buttonsPanel.add(previousButton);
			buttonsPanel.add(firstButton);
			buttonsPanel.add(lastButton);
			buttonsPanel.add(playButton);
			
			buttonsPanel.add(timerSpeed);
			buttonsPanel.add(sampleTitle);
			buttonsPanel.add(samplesIDsCombo);
			buttonsPanel.add(sampleIndexLabel);
			
			ListenForButton lForButton = new ListenForButton();
			ListenForCombo lForCombo = new ListenForCombo();
			nextButton.addActionListener(lForButton);
			previousButton.addActionListener(lForButton);
			firstButton.addActionListener(lForButton);
			lastButton.addActionListener(lForButton);
			playButton.addActionListener(lForButton);
			
			holderPanel.add(emptyPanel, BorderLayout.NORTH);
			holderPanel.add(buttonsPanel, BorderLayout.CENTER);
			
			fillSamplesIDs();
			setSampleIndex(pathway.getPointer());
			samplesIDsCombo.addItemListener(lForCombo);
			
			return holderPanel;
		}

		private static void setSampleIndex(int pointer) {
			String totalSamples = Integer.toString(pathway.getSamplesIDs().size());
			sampleIndexLabel.setText(Integer.toString(samplesIDsCombo.getSelectedIndex() + 1) + " of " + totalSamples);
			
		}

		private void fillSamplesIDs() {
			List<String> identifiers = ParseKGML.getTabInfo(activeTab).getSamplesIDs();
			
			for (String sampleID : identifiers){
				samplesIDsCombo.addItem(sampleID);
			}
			samplesIDsCombo.setSelectedIndex(pathway.getPointer());
			
		}
		
		static Timer sitesTimer = new Timer(mainDelay, new ActionListener() {
			
		    public void actionPerformed(ActionEvent evt) {
		    	
		    	int index = samplesIDsCombo.getItemCount() - 1;
				int currentPointer = samplesIDsCombo.getSelectedIndex();
				int numberOfSamples = samplesIDsCombo.getItemCount();
		    	
		    	int newPointer = currentPointer + 1;
    			
    			if ( newPointer >= index){
    				playButton.setEnabled(false);
    			}
    			samplesIDsCombo.setEnabled(false);
    			samplesIDsCombo.setSelectedIndex(newPointer);
    			setSampleIndex(newPointer);

	      	  	if (newPointer >= (numberOfSamples - 1)){
	      	  		stopTimer();
	      	  		playButton.setEnabled(false);
	      	  	}

		    }    
		});
		
		private static void startTimer() {
			sitesTimer.setInitialDelay(0);
			sitesTimer.start();
			playButton.setText("||");
			timerSpeed.setEnabled(true);
		}

		private static void stopTimer() {
			sitesTimer.stop();
			samplesIDsCombo.setEnabled(true);
			playButton.setText("Play");
			timerSpeed.setEnabled(false);
		} // end of stopTimer
		
		private class ListenForCombo implements ItemListener{

			public void itemStateChanged(ItemEvent ice) {
				if (ice.getSource() == samplesIDsCombo ){
					if (ice.getStateChange() == ItemEvent.SELECTED){
						int newPointer = samplesIDsCombo.getSelectedIndex();
						
						pathway.assignPointer(newPointer);
						setSitesColor(newPointer);
						setSampleIndex(newPointer);
						ControlPanel.samplesIDsCombo.setSelectedIndex(newPointer);
						samplesIDsCombo.setToolTipText("Sample ID: " + samplesIDsCombo.getSelectedItem().toString());
						
					}
				}
				
			}
			
		}
		
		private static class ListenForButton implements ActionListener{
			
			public void actionPerformed(ActionEvent bc) {
				
				int index = samplesIDsCombo.getItemCount() - 1;
				int currentPointer = samplesIDsCombo.getSelectedIndex();
				
				if (bc.getSource() == nextButton){
					
					if (currentPointer < index ){
						stopTimer();
	        			int newPointer = currentPointer + 1;
	        			
	        			if ( newPointer >= index){
	        				playButton.setEnabled(false);
	        			}
	        			setSampleIndex(newPointer);
	        			samplesIDsCombo.setSelectedIndex(newPointer);
	        			
	        		}
				} // end if nextButton
				
				else if (bc.getSource() == previousButton){
					
					if (currentPointer > 0 ){
						stopTimer();
	        			int newPointer = currentPointer - 1;
	        			
	        			playButton.setEnabled(true);
	        			setSampleIndex(newPointer);
	        			samplesIDsCombo.setSelectedIndex(newPointer);
	        			
	        		}
				} // end if previousButton
				
				else if (bc.getSource() == firstButton){
					stopTimer();
					int newPointer = 0;
					playButton.setEnabled(true);
					setSampleIndex(newPointer);
					samplesIDsCombo.setSelectedIndex(newPointer);
				} // end of firstButton
				
				else if (bc.getSource() == lastButton){
					stopTimer();
					int newPointer = samplesIDsCombo.getItemCount() - 1;
					playButton.setEnabled(false);
					setSampleIndex(newPointer);
					samplesIDsCombo.setSelectedIndex(newPointer);
				}
				
				else if (bc.getSource() == playButton){
					if (currentPointer < index) {
						if (sitesTimer.isRunning()) {
							stopTimer();
							
						} else {
							startTimer();
						}
					}
				}
				
			} // end of action performed
			
		} // end of ListenForButton
		
	}
	
}

