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
import java.awt.Dimension;
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
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class JOptionPaneMultiInput {

	String[] choices = {"comma","tab","space","semicolon","dash"};
	JComboBox separatorCombo = new JComboBox(choices);
	SpinnerModel spinID = new SpinnerNumberModel(1,1,50,1);
	
	JFileChooser fileChooser;
	JSpinner idIndexSpinner = new JSpinner(spinID);
	JButton loadData = new JButton("Choose another file");
	DefaultListModel listModel;
	JPanel myPanel, containerPanel;
	JLabel separatorLabel, indexLabel, fileLabel;
	File selectedFile;
	boolean validFileLoaded = true;
	String[] header;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	JCheckBox tcga;
	
	public JOptionPaneMultiInput(File file) {

		myPanel = new JPanel();
		myPanel.setLayout(new GridBagLayout());
		containerPanel= new JPanel();
		containerPanel.setLayout(new BorderLayout());
		separatorLabel = new JLabel("The columns are separated by:");
		indexLabel = new JLabel("Sample ID appears in column:");
		listModel = new DefaultListModel();
		listModel.addElement(1);
		selectedFile = file;
		fileLabel = new JLabel(file.getName() + " was selected.");
		tcga = new JCheckBox("Sample IDs are TCGA barcodes.");
		tcga.setHorizontalTextPosition(SwingConstants.LEFT);
		tcga.setSelected(true);
		
		addComp(myPanel, fileLabel, 0, 0, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, separatorLabel, 0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, separatorCombo, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, indexLabel, 0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, idIndexSpinner, 1, 2, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, tcga, 0, 3, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, loadData, 0, 4, 2, 1, GridBagConstraints.LINE_END, GridBagConstraints.NONE);
		
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
				validFileLoaded = checkInputFile(selectedFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (selectedFile == null || validFileLoaded == false){
				JOptionPane.showMessageDialog(Interface.bodyFrame, "No valid file was loaded!","Error",0,icon);
			}
			else {
				int sampleIdIndex = Integer.parseInt(idIndexSpinner.getValue().toString());
				String separator = separatorCombo.getSelectedItem().toString();
				
				int[] fields = new int[header.length];

				for (int i = 0; i < header.length ; i++){
					fields[i] = i;
				}
				
				TabsInfo.setSamplesInformationFile(selectedFile.getName().toString(), new SampleInformation(sampleIdIndex,separator, header, fields,selectedFile,tcga.isSelected()));
			}
		}
		else {
			selectedFile = null;
		}
	}
	
	public File getSelectedFile(){
		return selectedFile;
	}
	
	private boolean checkInputFile(File selectedFile) throws IOException{
		
		String[] currentLine;
		boolean matchingID = false;
		TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex(),0);
		BufferedReader br = new BufferedReader(new FileReader(selectedFile));
		String line = null;
		String inputFileHeader = br.readLine();
		
		String splitBy;
		String seperator = separatorCombo.getSelectedItem().toString();
				
		if (seperator.equals("tab")){
			splitBy = "\t";
		} else if (seperator.equals("space")){
			splitBy = "\\s+";
		} else if (seperator.equals("comma")){
			splitBy = ",";
		}
		else if (seperator.equals("dash")){
			splitBy = "-";
		}
		else {
			splitBy = ";";
		}
		
		header = inputFileHeader.split(splitBy);
		
		while ((line = br.readLine()) != null){
			currentLine = line.split(splitBy);
			if (currentLine.length != header.length){
				JOptionPane.showMessageDialog(Interface.bodyFrame, "Some rows have different number of columns! Make sure you have chosen a correct separator and then check your input file.","Error",0,icon);
				br.close();
				return false;
			}
			else{
				for (String item : pathway.getSamplesIDs()){
					if (tcga.isSelected()){
						String[] barcode = item.split("-");
						item = barcode[0] + "-" + barcode[1] + "-" + barcode[2];
					}
					if (item.equals(currentLine[Integer.parseInt(idIndexSpinner.getValue().toString())-1])){
						matchingID = true;
					}
				}
//				if (pathway.getSamplesIDs().contains(currentLine[Integer.parseInt(idIndexSpinner.getValue().toString())-1])){
//					matchingID = true;
//				}
			}
		}
		br.close();
		if (!matchingID){
			JOptionPane.showMessageDialog(Interface.bodyFrame, "The sample IDs in the loaded file do not match with the IDs in the loaded methylation/expression file!","Error",0,icon);
			return false;
		}
		return true;
	} // end of checkInputFile
   
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
