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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPanel extends JPanel{
	
	static JPanel buttonsPanel, holderPanel, mapPanel;
	JButton nextButton, previousButton, lastButton, firstButton;
	static JButton playButton;
	static JComboBox samplesIDsCombo;
	static JComboBox matchedGenesCombo;
	static JCheckBox repeatPlayback;
	static JSlider timerSpeed;
	static JLabel sampleTitle, sampleIndexLabel, matchedGenesLabel, matchedNumberLabel;
	static int mainDelay = 1000;
	static ImagePanel colorMap;
	static ImagePanel relationMap;
	static ListenForCombo lForCombo;
	static JLabel metaDataLabel;
	static int sampleIDIndex;

	public JPanel makeSidePanel() {
		holderPanel = new JPanel();
		holderPanel.setLayout(new BorderLayout());
		mapPanel = new JPanel();
		mapPanel.setLayout(new GridBagLayout());
		mapPanel.setBackground(new Color(185,185,185));
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridBagLayout());
		buttonsPanel.setBackground(new Color(185,185,185));
		ImageIcon colorImage = new ImageIcon(getClass().getResource("/resources/map.png"));
		colorMap = new ImagePanel(colorImage.getImage());
		colorMap.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		ImageIcon relationImage = new ImageIcon(getClass().getResource("/resources/relations.png"));
		relationMap = new ImagePanel(relationImage.getImage());
		relationMap.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		metaDataLabel = new JLabel("Loaded Metadata");
		
		metaDataLabel.setEnabled(false);
		metaDataLabel.setBorder(BorderFactory.createRaisedBevelBorder());
		metaDataLabel.setPreferredSize(new Dimension(130, 40));
		metaDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		nextButton = new JButton(">");
		nextButton.setBorder(new RoundedCornerBorder());
		nextButton.setPreferredSize(new Dimension(85,30));
		nextButton.setToolTipText("show next sample");
		addComp(buttonsPanel, nextButton, 0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,45,2);
		previousButton = new JButton("<");
		previousButton.setBorder(new RoundedCornerBorder());
		previousButton.setPreferredSize(new Dimension(85,31));
		previousButton.setToolTipText("show previous sample");
		addComp(buttonsPanel, previousButton, 0, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,2,2);
		lastButton = new JButton(">>");
		lastButton.setBorder(new RoundedCornerBorder());
		lastButton.setPreferredSize(new Dimension(85,31));
		lastButton.setToolTipText("show the last sample");
		addComp(buttonsPanel, lastButton, 0, 2, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,2,2);
		firstButton = new JButton("<<");
		firstButton.setBorder(new RoundedCornerBorder());
		firstButton.setPreferredSize(new Dimension(85,31));
		firstButton.setToolTipText("show the first sample");
		addComp(buttonsPanel, firstButton, 0, 3, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,2,22);
		playButton = new JButton("Play");
		playButton.setBorder(new RoundedCornerBorder());
		playButton.setPreferredSize(new Dimension(85,31));
		playButton.setToolTipText("show samples consecutively");
		addComp(buttonsPanel, playButton, 0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,2,2);
		
		repeatPlayback = new JCheckBox("Repeat");
		repeatPlayback.setOpaque(true);
		repeatPlayback.setBackground(new Color(185,185,185));
//		repeatPlayback.setBorder(new RoundedCornerBorder());
		repeatPlayback.setPreferredSize(new Dimension(86, 30));
		addComp(buttonsPanel, repeatPlayback, 0, 6, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,1,20);
		
		timerSpeed = new JSlider(JSlider.HORIZONTAL, 1,4,2);
		timerSpeed.setPaintLabels(true);
		timerSpeed.setMinorTickSpacing(1);
		timerSpeed.setMajorTickSpacing(1);
		timerSpeed.setPaintTicks(true);
		timerSpeed.setToolTipText("Playback speed");
		timerSpeed.setOpaque(true);
		timerSpeed.setBackground(new Color(185,185,185));
		
		timerSpeed.setPreferredSize(new Dimension(110, 40));
		addComp(buttonsPanel, timerSpeed, 0, 5, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,1,1);
		
		ListenForSlider lForSlider = new ListenForSlider();
		timerSpeed.addChangeListener(lForSlider);
		
		sampleTitle = new JLabel("Sample ID", JLabel.CENTER);
		addComp(buttonsPanel, sampleTitle, 0, 7, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,3,0);
		
		samplesIDsCombo = new JComboBox();
		samplesIDsCombo.setPreferredSize(new Dimension(135, 30));
		samplesIDsCombo.setBorder(new RoundedCornerBorder());
		samplesIDsCombo.setPrototypeDisplayValue("XXXXXXXX");
		addComp(buttonsPanel, samplesIDsCombo, 0, 8, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,5,3);
		
		sampleIndexLabel = new JLabel("index");
		sampleIndexLabel.setForeground(new Color(185,185,185));
		addComp(buttonsPanel, sampleIndexLabel, 0, 9, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,1,10);
		
		matchedGenesLabel = new JLabel("Matched Genes", JLabel.CENTER);
		addComp(buttonsPanel, matchedGenesLabel, 0, 10, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,5,1);
		
		matchedGenesCombo = new JComboBox();
		matchedGenesCombo.setPreferredSize(new Dimension(135, 30));
		matchedGenesCombo.setBorder(new RoundedCornerBorder());
		matchedGenesCombo.setPrototypeDisplayValue("XXXXXXXX");
		
		addComp(buttonsPanel, matchedGenesCombo, 0, 11, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,5,1);
		
		matchedNumberLabel = new JLabel("matches");
		matchedNumberLabel.setForeground(new Color(185,185,185));
		addComp(buttonsPanel, matchedNumberLabel, 0, 12, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,5,10);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0;
		c.insets = new Insets(30,0,10,0);
		c.anchor = GridBagConstraints.PAGE_START;
		c.fill = GridBagConstraints.NONE;
		mapPanel.add(relationMap, c);
		
		c.insets = new Insets(5,0,0,0);
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		mapPanel.add(colorMap,c);
		c.insets = new Insets(20,0,0,0);
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.PAGE_START;
		mapPanel.add(metaDataLabel,c);
		
		
		
		lForCombo = new ListenForCombo();
		matchedGenesCombo.addItemListener(lForCombo);

		ListenForButton lForButton = new ListenForButton();
		nextButton.addActionListener(lForButton);
		previousButton.addActionListener(lForButton);
		firstButton.addActionListener(lForButton);
		lastButton.addActionListener(lForButton);
		playButton.addActionListener(lForButton);
		
		for (Component item : buttonsPanel.getComponents()){
			item.setEnabled(false);
//			item.setBackground(new Color(185,185,185));
		}
		
		holderPanel.add(buttonsPanel, BorderLayout.NORTH);
		holderPanel.add(mapPanel, BorderLayout.CENTER);
		
		return holderPanel;
	}
	
	static Timer timer = new Timer(mainDelay, new ActionListener() {
		
	    public void actionPerformed(ActionEvent evt) {
	    	int activeTab = Interface.tabPane.getSelectedIndex();
	    	TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
	    	int numberOfSamples = pathway.getSamplesIDs().size();
	    	Character type = pathway.getMetaType();
	    	int newPointer = pathway.movePointerForward();
	    	samplesIDsCombo.setSelectedIndex(newPointer);
	    	sampleIDIndex = pathway.getSamplesIDs().indexOf(samplesIDsCombo.getSelectedItem());
	    	Genes.changeBgColor(sampleIDIndex,type);
	    	setIndexLabel(newPointer);
      	  	
      	  	if (pathway.getSamplesInfo() != null && pathway.getSamplesInfo().size() > 0){
				setSampleInfoLabel(sampleIDIndex);
			}
      	    else {
				Interface.setSampleInfoLabel(samplesIDsCombo.getItemAt(newPointer).toString(), true);
			}
      	  	

      	  	if (newPointer >= (numberOfSamples - 1)){
	  			if (repeatPlayback.isSelected()) {
	  				pathway.assignPointer(-1);
				}
	  			else{
	  				stopTimer();
					playButton.setEnabled(false);
//					timerSpeed.setValue(2);
					
	  			}
      	  	}

	    }    
	});
	
	private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch, int above, int below){
		
		GridBagConstraints gridConstraints = new GridBagConstraints();
		
		gridConstraints.gridx = xPos;
		gridConstraints.gridy = yPos;
		gridConstraints.gridwidth = compWidth;
		gridConstraints.gridheight = compHeight;
		gridConstraints.weightx = 100;
		gridConstraints.weighty = 100;
		//insets (above, left,below, right)
		gridConstraints.insets = new Insets(above,3,below,3);
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);	
	}

	public static void enableControlPanel(int tabPointer) {
		
		int activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
		
		matchedGenesCombo.removeItemListener(lForCombo);
		fillMatchedGenes(activeTab);
		samplesIDsCombo.removeItemListener(lForCombo);
		fillSamplesIDs(activeTab);
		setIndexLabel(tabPointer);
		setMatchNumberLabel(tabPointer);
		
		if (samplesIDsCombo.getItemCount() > 0){
			samplesIDsCombo.setSelectedIndex(tabPointer);
			samplesIDsCombo.addItemListener(lForCombo);
			matchedGenesCombo.setSelectedIndex(pathway.getSelectedGeneIndex());
			
			if (pathway.getLoadSource().equals('H') || pathway.getLoadSource().equals('W')){
				setMatchedGene(matchedGenesCombo.getSelectedItem().toString());
				matchedGenesCombo.addItemListener(lForCombo);	
			}
			
			for (Component theComponent: buttonsPanel.getComponents()){
				if (theComponent.getClass() != JSlider.class) {
					theComponent.setEnabled(true);
				}
			}
		}		
		else {
			matchedGenesCombo.setEnabled(true);
		}
		
		if (ParseKGML.getTabInfo(activeTab).getSamplesInfo().size() > 0){
			ControlPanel.setSampleInfoLabel(tabPointer);
			Interface.editFields.setEnabled(true);
			PiilMenubar.groupWiseView.setEnabled(true);
		}
		else {
			if (samplesIDsCombo.getSelectedItem() != null){
				Interface.setSampleInfoLabel(samplesIDsCombo.getSelectedItem().toString(), true);
			}
			
			Interface.editFields.setEnabled(false);
			PiilMenubar.groupWiseView.setEnabled(false);
		}
		
		PiilMenubar.setSampleIdMenu(true);
		sampleIndexLabel.setForeground(Color.BLACK);
		matchedNumberLabel.setForeground(Color.BLACK);
		colorMap.setVisible(true);
		PiilMenubar.multiSampleView.setEnabled(true);
		PiilMenubar.singleSampleView.setEnabled(true);
		if (pathway.getMetaType().equals('M')){
			PiilMenubar.selectSubset.setEnabled(true);
			Interface.tabPane.setToolTipTextAt(activeTab, "Methylation data loaded");
			metaDataLabel.setText("DNA Methylation");
			metaDataLabel.setEnabled(true);
			
		}
		else if (pathway.getMetaType().equals('E')){
			
			Interface.tabPane.setToolTipTextAt(activeTab, "Expression data loaded");
			metaDataLabel.setText("Gene Expression");
			metaDataLabel.setEnabled(true);
			PiilMenubar.selectSubset.setEnabled(false);
			
		}
		if (pathway.getIDsInGroups() != null && pathway.getIDsInGroups().size() > 0){
			PiilMenubar.groupWiseView.setEnabled(true);
			if (pathway.getViewMode() == 2){
				ControlPanel.disableControlPanel(true);
			}
		}
	} // end of enableControlPanel

	private static void setMatchedGene(String geneName) {
		int tab = Interface.tabPane.getSelectedIndex();
		TabsInfo pathway = ParseKGML.getTabInfo(tab);
		
		Rectangle bounds = null;
		JLabel oneNode;
		
		for (Entry<String, Genes> target : pathway.getMapedGeneLabel().entrySet()) {

			oneNode = target.getValue().getLabel();
			if (geneName.equals(oneNode.getText())) {
				
				bounds = oneNode.getBounds();
				break;
			}
		}
		Interface.scrollPaneHolder.get(tab).getViewport().scrollRectToVisible(bounds);
		pathway.setSelectedGeneIndex(matchedGenesCombo.getSelectedIndex());
		matchedGenesCombo.setSelectedIndex(pathway.getSelectedGeneIndex());
		pathway.setSelectedGenePointer(new Point((int) bounds.getX(),(int) bounds.getY()));	
		
	}

	private static void setMatchNumberLabel(int tabPointer) {
		matchedNumberLabel.setText(matchedGenesCombo.getItemCount() + " Match(es)");
	}

	private static void setIndexLabel(int tabPointer) {
		int activeTab = Interface.tabPane.getSelectedIndex();
		TabsInfo tab = ParseKGML.getTabInfo(activeTab);
		String totalSamples = Integer.toString(tab.getSamplesIDs().size());
		if (tab.getSamplesIDs().size() > 0){
			sampleIndexLabel.setText(Integer.toString(tabPointer + 1) + " of " + totalSamples);
		}
		else {
			sampleIndexLabel.setText("--");
		}
		
	}

	private static void fillSamplesIDs(int activeTab) {
		samplesIDsCombo.removeAllItems();
		
		List<String> identifiers = ParseKGML.getTabInfo(activeTab).getSamplesIDs();
		for (String sampleID : identifiers){
			samplesIDsCombo.addItem(sampleID);
		}
	}

	private static void fillMatchedGenes(int activeTab) {
		matchedGenesCombo.removeAllItems();
		List<String> v = new ArrayList<String>();
		HashMap<String, Genes> match = ParseKGML.getTabInfo(activeTab).getMapedGeneLabel();
		for (Genes found : match.values()){
			v.add(found.getText().toString());
		}
		Set<String> hashsetList = new TreeSet<String>(v);
		for (String item : hashsetList){
			matchedGenesCombo.addItem(item);
		}
		matchedGenesCombo.setSelectedIndex(Interface.tabInfoTracker.get(Interface.tabPane.getSelectedIndex()).getSelectedGeneIndex());
		
	}

	public static void stopTimer(){
		timer.stop();
		
		playButton.setText("Play");
		timerSpeed.setEnabled(false);
		timerSpeed.setValue(2);	
	}
	
	public static void startTimer(){
		
		timer.setInitialDelay(0);
		timer.start();
		playButton.setText("||");
		timerSpeed.setEnabled(true);
	}

	public static void disableControlPanel(boolean enabled) {
		
		for (Component theComponent: buttonsPanel.getComponents()){
			theComponent.setEnabled(false);
		}
		
		matchedGenesCombo.removeAllItems();
		matchedGenesCombo.setPrototypeDisplayValue("XXXXXXXX");
		samplesIDsCombo.removeItemListener(lForCombo);
		samplesIDsCombo.removeAllItems();
		samplesIDsCombo.setPrototypeDisplayValue("XXXXXXXX");
		matchedNumberLabel.setForeground(new Color(185,185,185));
		sampleIndexLabel.setForeground(new Color(185,185,185));
		
		if (enabled){
//			Object[] hints = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex()).getIDsInGroups().keySet().toArray();
			List<String> hints = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex()).getShowableGroups();
			Interface.setSampleInfoLabel(hints, true, ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex()).getBaseGroupIndex());
			PiilMenubar.groupWiseView.setEnabled(true);
			
		}
		else {
			Interface.setSampleInfoLabel(" Sample's Information", false);
			PiilMenubar.groupWiseView.setEnabled(false);
			metaDataLabel.setText("Loaded Metadata");
			metaDataLabel.setEnabled(false);
		}
		Interface.editFields.setEnabled(false);
		PiilMenubar.setSampleIdMenu(false);
//		colorMap.setVisible(enabled);
		PiilMenubar.multiSampleView.setEnabled(false);
		PiilMenubar.selectSubset.setEnabled(enabled);
		PiilMenubar.singleSampleView.setEnabled(enabled);
		
		
		
		if (Interface.tabPane.getTabCount() == 0){
			Interface.sampleInfoLabel.setForeground(Interface.sampleInfoLabel.getBackground());
		}
		
	}

	public static String setSampleInfoLabel(int tabPointer) {
		
		int tab = Interface.tabPane.getSelectedIndex();
		TabsInfo thisTab = ParseKGML.getTabInfo(tab);
		
		String info = " ";
		String id = thisTab.getSamplesIDs().get(tabPointer);
		
		List<String> metaValues = thisTab.getSamplesInfo().get(id);
		
		List<String> colNames = thisTab.getSamplesInfo().get("0");
		List<String> header = thisTab.getSamplesInfo().get("-1");
		int fieldCount = colNames.size();
		
		for (int i = 0; i < fieldCount; i++) {
			if (metaValues != null){
				info += colNames.get(i) + " : " + metaValues.get(header.indexOf(colNames.get(i)));
				if (i < (fieldCount - 1)) {
					info += " | ";
				}
			}
			else {
				info = " No additional information";
			}
		}
		Interface.setSampleInfoLabel(info, true);
		return info;
	} // end of setSampleInfoLabel
	
	private class ListenForButton implements ActionListener{

		public void actionPerformed(ActionEvent bc) {
			int activeTab = Interface.tabPane.getSelectedIndex();
			TabsInfo currentTab = ParseKGML.getTabInfo(activeTab);
			Character type = currentTab.getMetaType();
			int currentPointer = currentTab.getPointer();
			int index = currentTab.getSamplesIDs().size() - 1;
			
			if (bc.getSource() == nextButton){
				
				if (currentPointer < index ){
					stopTimer();
        			int newPointer = currentTab.movePointerForward();
        			
        			if ( newPointer >= index){
        				playButton.setEnabled(false);
        			}
        			setIndexLabel(newPointer);
        			samplesIDsCombo.setSelectedIndex(newPointer);
        			if (currentTab.getSamplesInfo() != null && currentTab.getSamplesInfo().size() > 0){
        				setSampleInfoLabel(newPointer);
        			}
        			else {
        				Interface.setSampleInfoLabel(samplesIDsCombo.getItemAt(newPointer).toString(), true);
        			}
        		}
			} // end if nextButton
			
			else if (bc.getSource() == previousButton){
				if (currentPointer > 0){
					stopTimer();
        			int newPointer = currentTab.movePointerBackward();
        			playButton.setEnabled(true);
        			setIndexLabel(newPointer);
        			samplesIDsCombo.setSelectedIndex(newPointer);
        			if (currentTab.getSamplesInfo() != null && currentTab.getSamplesInfo().size() > 0){
        				setSampleInfoLabel(newPointer);
        			}
        			else {
        				Interface.setSampleInfoLabel(samplesIDsCombo.getItemAt(newPointer).toString(), true);
        			}
        		}
			} // end of previousButton 
			
			else if (bc.getSource() == firstButton){
				stopTimer();
				int newPointer = currentTab.assignPointer(0);
				playButton.setEnabled(true);
				timerSpeed.setValue(2);
				setIndexLabel(newPointer);
				samplesIDsCombo.setSelectedIndex(newPointer);
				if (currentTab.getSamplesInfo() != null && currentTab.getSamplesInfo().size() > 0){
    				setSampleInfoLabel(newPointer);
    			}
				else {
    				Interface.setSampleInfoLabel(samplesIDsCombo.getItemAt(newPointer).toString(), true);
    			}
			} // end of firstButton
			
			else if (bc.getSource() == lastButton){
				stopTimer();
				int newPointer = currentTab.assignPointer(currentTab.getSamplesIDs().size() - 1);
				playButton.setEnabled(false);
				setIndexLabel(newPointer);
				samplesIDsCombo.setSelectedIndex(newPointer);
				if (currentTab.getSamplesInfo() != null && currentTab.getSamplesInfo().size() > 0){
    				setSampleInfoLabel(newPointer);
    			}
				else {
    				Interface.setSampleInfoLabel(samplesIDsCombo.getItemAt(newPointer).toString(), true);
    			}
			} // end of lastButton
			
			else if (bc.getSource() == playButton){
				
				if (currentPointer < index) {
					if (timer.isRunning()) {
						stopTimer();
						
					} else {
						startTimer();
					}
				}
			} // end of playButton
		}
	} // end of ListenForButton
	
	private class ListenForSlider implements ChangeListener{

		public void stateChanged(ChangeEvent se) {
			if (se.getSource() == timerSpeed){
				timer.stop();
				int delay = mainDelay;
				int speed = timerSpeed.getValue();
				switch (speed){
				case 1:
					delay = mainDelay * 2;
					break;
				case 2:
					delay = mainDelay;
					break;
				case 3:
					delay = mainDelay / 2;
					break;
				case 4:
					delay = mainDelay / 5;
					break;
				}
				timer.setDelay(delay);
				if (timerSpeed.isEnabled()) {
					timer.start();
				}
			}	
		}
	} // end of ListenForSlider
	
	private class ListenForCombo implements ItemListener{

		public void itemStateChanged(ItemEvent ce) {
			
			if (ce.getSource() == samplesIDsCombo & !timer.isRunning() ){
				if (ce.getStateChange() == ItemEvent.SELECTED){
					int activeTab = Interface.tabPane.getSelectedIndex();
					int newPointer = samplesIDsCombo.getSelectedIndex();
					TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
					pathway.assignPointer(newPointer);
					sampleIDIndex = pathway.getSamplesIDs().indexOf(samplesIDsCombo.getSelectedItem());
					Genes.changeBgColor(sampleIDIndex,pathway.getMetaType());
					setIndexLabel(newPointer);
					samplesIDsCombo.setSelectedIndex(newPointer);
					samplesIDsCombo.setToolTipText("Sample ID: " + samplesIDsCombo.getSelectedItem().toString());
					if (pathway.getSamplesInfo() != null && pathway.getSamplesInfo().size() > 0){
	    				setSampleInfoLabel(sampleIDIndex);
	    			}
					else {
        				Interface.setSampleInfoLabel(samplesIDsCombo.getItemAt(newPointer).toString(), true);
        			}
				}
			}
			if (ce.getSource() == matchedGenesCombo){
				if (ce.getStateChange() == ItemEvent.SELECTED){
					String geneName = (String) ce.getItem();
					setMatchedGene(geneName);
				}
			}
		}
	} // end of ListenForCombo

}
