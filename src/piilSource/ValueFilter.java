package piilSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ValueFilter extends JDialog{
	
	JPanel filterPanel, iconPanel, rightPanel, buttonsPanel;
	JLabel filterLabel, betaLabel1, betaLabel2, nextRegion, iconLabel;
	JFrame holderFrame;
	JCheckBox addFilter;
	String[] choices = {"<=", "<"};
	JComboBox ge1, ge2, ge3, ge4;
	JTextField firstLower, firstUpper, secondLower, secondUpper;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	TabsInfo activeTab;
	JDialog dialog = new JDialog(Interface.bodyFrame, "Analyzing data",ModalityType.APPLICATION_MODAL);
	JButton applyButton = new JButton("Apply");
	JButton applyCloseButton = new JButton("Apply and Close");
	JButton cancelButton = new JButton("Cancel");
	TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
	GridBagConstraints gc = new GridBagConstraints();
	
	public ValueFilter(){
		applyButton.setPreferredSize(new Dimension(140,33));
		applyCloseButton.setPreferredSize(new Dimension(140,33));
		cancelButton.setPreferredSize(new Dimension(140,33));
		filterPanel = new JPanel();
		filterPanel.setLayout(new GridBagLayout());
		iconPanel = new JPanel();
		rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout());
		iconPanel.setLayout(new FlowLayout());
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		iconLabel = new JLabel(icon);
		iconPanel.add(iconLabel);
		
		holderFrame = new JFrame();
		holderFrame.setSize(600, 220);
		iconPanel.setPreferredSize(new Dimension(100, 220));
		rightPanel.setPreferredSize(new Dimension(500, 160));
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (holderFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (holderFrame.getHeight() / 2);
		holderFrame.setLocation(xPos,yPos);
		holderFrame.setResizable(false);
		holderFrame.setLayout(new BorderLayout());
		holderFrame.setTitle("Select CpG sites by beta-values");
		filterLabel = new JLabel("Select CpG sites that ");
		firstLower = new JTextField();
		firstUpper = new JTextField();
		secondLower = new JTextField();
		secondUpper = new JTextField();
		betaLabel1 = new JLabel(" beta value ");
		betaLabel2 = new JLabel(" beta value ");
		nextRegion = new JLabel(" OR ");
		firstLower.setPreferredSize(new Dimension(40,20));
		firstUpper.setPreferredSize(new Dimension(40,20));
		secondLower.setPreferredSize(new Dimension(40,20));
		secondUpper.setPreferredSize(new Dimension(40,20));
		addFilter = new JCheckBox("Apply over the current filter");
		ge1 = new JComboBox(choices);
		ge2 = new JComboBox(choices);
		ge3 = new JComboBox(choices);
		ge4 = new JComboBox(choices);
		ge1.setPreferredSize(new Dimension(70,25));
		ge2.setPreferredSize(new Dimension(70,25));
		ge3.setPreferredSize(new Dimension(70,25));
		ge4.setPreferredSize(new Dimension(70,25));
		
		setupForm();
		
		
//		Object[] buttons = {"Apply and Close", "Close"};
//		int result = JOptionPane.showOptionDialog(null, filterPanel, "Filtering CpG sites ...", JOptionPane.OK_CANCEL_OPTION,0,icon, buttons, buttons[0]);
		
		
	}

	private void setupForm() {
		
		gc.gridx = 1;
		gc.gridy = 1;
		gc.gridwidth =1; 
		gc.gridheight = 1; 
		gc.weightx = 1;
		gc.weighty = 1;
		gc.insets = new Insets(10,1,1,1);
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.NONE;
		filterPanel.add(filterLabel, gc);
		gc.gridx = 2;
		filterPanel.add(firstLower, gc);
		gc.gridx = 3;
		filterPanel.add(ge1, gc);
		gc.gridx = 4;
		filterPanel.add(betaLabel1, gc);
		gc.gridx = 5;
		filterPanel.add(ge2, gc);
		gc.gridx = 6; 
		filterPanel.add(firstUpper, gc);
		gc.gridy = 2;
		gc.gridx = 1;
		filterPanel.add(nextRegion,gc);
		gc.gridx = 2;
		filterPanel.add(secondLower,gc);
		gc.gridx = 3; 
		filterPanel.add(ge3,gc);
		gc.gridx = 4;
		filterPanel.add(betaLabel2, gc);
		gc.gridx = 5;
		filterPanel.add(ge4, gc);
		gc.gridx = 6;
		filterPanel.add(secondUpper, gc);
		gc.gridy = 5;
		gc.gridx = 2;
		gc.gridwidth = 3;
		gc.insets = new Insets(15,1,20,1);
		filterPanel.add(addFilter, gc);
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(applyCloseButton);
		buttonsPanel.add(applyButton);
		holderFrame.add(iconPanel, BorderLayout.WEST);
		holderFrame.add(rightPanel, BorderLayout.EAST);
		
		rightPanel.add(filterPanel);
		rightPanel.add(buttonsPanel);
//		holderFrame.pack();
		holderFrame.setVisible(true);
		
		cancelButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				holderFrame.dispose();
			}
		});
		
		applyCloseButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				applyButton.doClick();
				holderFrame.dispose();
			}
		});
		
		applyButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (checkInput()){
					filterSites();
				}
				
			}

			private boolean checkInput() {
				
				if (firstLower.getText().isEmpty() && firstUpper.getText().isEmpty() 
						&& secondLower.getText().isEmpty() && secondUpper.getText().isEmpty()){
					JOptionPane.showMessageDialog(holderFrame, "All fields are empty!","Error", 0, icon);
					return false;
				}
				else if (invalidInput(firstLower.getText()) || invalidInput(firstLower.getText())
						|| invalidInput(firstLower.getText()) || invalidInput(firstLower.getText())){
					return false;
				}
				else if ( ((!firstLower.getText().isEmpty() && !firstUpper.getText().isEmpty()) 
						&& (Float.parseFloat(firstLower.getText()) > Float.parseFloat(firstUpper.getText()))) 
						|| ((!secondLower.getText().isEmpty() && !secondUpper.getText().isEmpty()) 
								&& (Float.parseFloat(secondLower.getText()) > Float.parseFloat(secondUpper.getText())))){
					JOptionPane.showMessageDialog(holderFrame, "The left hand side value(s) must be smaller than the right hans side!","Error", 0, icon);
					return false;
					
				}
				
				return true;
			}

			private boolean invalidInput(String text) {
				float value;
				try {
					value = Float.parseFloat(text);
					if ((value < 0) || (value > 1)){
						JOptionPane.showMessageDialog(holderFrame, "Values must be between 0 and 1!","Error", 0, icon);
					}
				}
				catch (NumberFormatException e){
					JOptionPane.showMessageDialog(holderFrame, "Only numbers are allowed!","Error", 0, icon);
				}
				return false;
			}
		});
		
	}
	
	private void filterSites() {
		HashMap<String, List<Integer>> selection = new HashMap<String, List<Integer>>();
		String geneID, regionInfo;
		String siteID;
		boolean match = false;
		int numberOfSamples = pathway.getSamplesIDs().size();
		if (addFilter.isSelected()){
			for (Entry<String, List<List<String>>> site : pathway.getMapedGeneData().entrySet()){
				geneID = site.getKey();
				List<Integer> selectedSites = new ArrayList<Integer>();
				selectedSites = pathway.getSelectedSites(geneID);
				if (selectedSites == null){
					for (int i= 0; i < site.getValue().size() ; i ++){
						match = true;
						for (int j = 0; j < numberOfSamples ; j ++){
							if (!isNumeric (site.getValue().get(i).get(j))){
								continue;
							}
							if (!testCriteria(site.getValue().get(i).get(j))){
								match = false;
								break;
							}
						}
						if (match){
							selectedSites.add(i);
						}
					}
				}
				else {
					if (selectedSites.get(0) == -1){
						
					}
					else{
						
						List<Integer> excludingIndex = new ArrayList<Integer>();
						for (int i = 0 ; i < selectedSites.size(); i ++){
							match = true;							
							for (int j=0 ; j < numberOfSamples ; j ++){
								if (!isNumeric (site.getValue().get(i).get(j))){
									continue;
								}
								if (!testCriteria(site.getValue().get(i).get(j))){
									match = false;
									break;
								}
							}	
							if (!match){
								excludingIndex.add(i);
							}
						}
						Collections.sort(excludingIndex, Collections.reverseOrder());
						for (int i : excludingIndex){
							selectedSites.remove(i);
						}
						excludingIndex.clear();
					}
					
				}
				if (selectedSites.size() == 0){
					selectedSites.add(-1);
				}
				selection.put(site.getKey(), selectedSites);
				
				
				
				
			} // end of for each region
			
			pathway.setSelectedSites(selection);
			Genes.changeBgColor(pathway.getPointer(), 'M');
		} // end of if
		else {
			
			for (Entry<String, List<List<String>>> site : pathway.getMapedGeneData().entrySet()){
				List<Integer> selectedSites = new ArrayList<Integer>();
				geneID = site.getKey();
				
				selectedSites.clear();
				for (int i= 0; i < site.getValue().size() ; i ++){
					match = true;
					for (int j = 0; j < numberOfSamples ; j ++){
						if (!isNumeric (site.getValue().get(i).get(j))){
							continue;
						}
						if (!testCriteria(site.getValue().get(i).get(j))){
							match = false;
							break;
						}
					}
					if (match){
						selectedSites.add(i);
					}
				}
				if (selectedSites.size() == 0){
					selectedSites.add(-1);
				}
				selection.put(site.getKey(), selectedSites);
			} // end of for each gene
			pathway.setSelectedSites(selection);
			pathway.setSDThreshold(0);
			Genes.changeBgColor(pathway.getPointer(), 'M');
		} // end of else
		
	}

	private boolean testCriteria(String string) {
		boolean firstCondition, secondCondition, thirdCondition, forthCondition;
		firstCondition = secondCondition = thirdCondition = forthCondition = true;
		Float value = Float.parseFloat(string);
		if (!firstLower.getText().isEmpty()){
			if (value > Float.parseFloat(firstLower.getText())){
				firstCondition = true;
			}
			else {
				firstCondition = false;
			}
			
		}
		if (!firstUpper.getText().isEmpty()){
			if (value < Float.parseFloat(firstUpper.getText())){
				secondCondition = true;
			}
			else {
				secondCondition = false;
			}
		}
		if (!secondLower.getText().isEmpty()){
			if (value > Float.parseFloat(secondLower.getText())){
				thirdCondition = true;
			}
			else {
				thirdCondition = false;
			}
		}
		if (!secondUpper.getText().isEmpty()){
			if (value < Float.parseFloat(secondUpper.getText())){
				forthCondition = true;
			}
			else {
				forthCondition = false;
			}
		}
		
		return (firstCondition && secondCondition) || (thirdCondition && forthCondition);
		
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
	
	
}
