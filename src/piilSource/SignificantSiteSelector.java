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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class SignificantSiteSelector {
	
	String[] choices = {"comma","tab","space","semicolon","dash"};
	JComboBox separatorCombo = new JComboBox(choices);
	JButton loadData = new JButton("Choose another file");
	SpinnerModel spinID = new SpinnerNumberModel(1,1,50,1);
	JSpinner idIndexSpinner = new JSpinner(spinID);
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	JLabel separatorLabel, indexLabel, fileLabel;
	JPanel myPanel, containerPanel;
	JFileChooser fileChooser;
	File selectedFile;
	ArrayList<String> significantSites;
	TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
	
	
	public SignificantSiteSelector(File file) {
		
		selectedFile = file;
		
		myPanel = new JPanel();
		myPanel.setLayout(new GridBagLayout());
		containerPanel= new JPanel();
		containerPanel.setLayout(new BorderLayout());
		separatorLabel = new JLabel("The columns are separated by:");
		indexLabel = new JLabel("Significant site identifiers appear in column:");
		fileLabel = new JLabel(file.getName() + " was selected.");
		significantSites = new ArrayList<String>();
		
		addComp(myPanel, fileLabel, 0, 0, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, separatorLabel, 0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, separatorCombo, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, indexLabel, 0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, idIndexSpinner, 1, 2, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, loadData, 0, 3, 2, 1, GridBagConstraints.LINE_END, GridBagConstraints.NONE);
		
		containerPanel.add(myPanel, BorderLayout.CENTER);
		
		loadData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent bc) {
				File newFile;
				if (bc.getSource() == loadData) {
					fileChooser = new JFileChooser();
					fileChooser.setFileFilter(null);
					
					File directory = new File(PiilMenubar.getLastOpenedDir());
					fileChooser.setCurrentDirectory(directory);
					int returnVal = fileChooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) { 
						newFile = fileChooser.getSelectedFile();
						if (newFile == null){
							JOptionPane.showMessageDialog(Interface.bodyFrame, "No file was loaded!","Warning",0,icon);
						}
						else {
							fileLabel.setText(newFile.getName() + " was selected.");
							selectedFile = newFile;
						}
					}
				}
			}
			});
		
		int result = JOptionPane.showConfirmDialog(null, containerPanel, "Please choose ...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
		if (result == JOptionPane.OK_OPTION) {
			
			try {
				getSites();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String geneID;
			String siteID;
			
			HashMap<String, List<Integer>> selection = new HashMap<String, List<Integer>>();
			for (Entry<String, Collection<String>> region : pathway.getMapedGeneRegion().entrySet()){
				List<Integer> selectedSites = new ArrayList<Integer>();
				geneID = region.getKey();
				selectedSites.clear();
				for (int i= 0; i < region.getValue().size() ; i ++){
					siteID = region.getValue().toArray()[i].toString().split("_")[0];
					if (significantSites.contains(siteID)){
						System.out.println(siteID);
						selectedSites.add(i);
					}
				}
				if (selectedSites.size() == 0){
					selectedSites.add(-1);
				}
				selection.put(region.getKey(), selectedSites);
			} // end of for
			pathway.setSelectedSites(selection);
			Genes.changeBgColor(pathway.getPointer(), 'M');
		} // end of OK_OPTION
		
	}
	
	private void getSites() throws IOException{
		String splitBy = getSeparator();
		String[] currentLine;
		BufferedReader br = new BufferedReader(new FileReader(selectedFile));
		String line = null;
		while ((line = br.readLine()) != null){
			currentLine = line.split(splitBy);
			significantSites.add(currentLine[Integer.parseInt(idIndexSpinner.getValue().toString())-1]);
		}
	}

	public String getSeparator(){
		String splitBy;
		String separator = separatorCombo.getSelectedItem().toString();
		if (separator.equals("tab")){
			splitBy = "\t";
		} else if (separator.equals("space")){
			splitBy = "\\s+";
		} else if (separator.equals("comma")){
			splitBy = ",";
		}
		else if (separator.equals("dash")){
			splitBy = " ";
		}
		else {
			splitBy = ";";
		}
		return splitBy;
	}
	
	private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch){
		
		GridBagConstraints gridConstraints = new GridBagConstraints();
		
		gridConstraints.gridx = xPos;
		gridConstraints.gridy = yPos;
		gridConstraints.gridwidth = compWidth;
		gridConstraints.gridheight = compHeight;
		gridConstraints.weightx = 1;
		gridConstraints.weighty = 1;
		gridConstraints.insets = new Insets(5,5,5,5);
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);
		
	}

}
