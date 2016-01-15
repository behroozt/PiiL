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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class JOptionPaneMultiInput {

	String[] choices = {"tab", "comma", "space"};
	JComboBox separatorCombo = new JComboBox(choices);
	SpinnerModel spinID = new SpinnerNumberModel(1,1,1,1);
	SpinnerModel spinField = new SpinnerNumberModel(1,1,20,1);
	JFileChooser fileChooser;
	JSpinner idIndexSpinner = new JSpinner(spinID);
	JSpinner fieldSpinner = new JSpinner(spinField);
	JList fieldsList;
	JButton loadData = new JButton("Load samples Info file");
	DefaultListModel listModel;
	JPanel myPanel, containerPanel;
	JLabel separatorLabel, indexLabel, numberLabel, filedsLabel ;
	File selectedFile;
	boolean validFileLoaded = true;
	static CheckBoxList fieldCheckBox;
	List<Integer> selectedColumns = new ArrayList<Integer>();
	
	public JOptionPaneMultiInput() {

		fieldCheckBox = new CheckBoxList();
		int activeTab = Interface.tabPane.getSelectedIndex();
		myPanel = new JPanel();
		myPanel.setLayout(new GridBagLayout());
		containerPanel= new JPanel();
		containerPanel.setLayout(new BorderLayout());
		separatorLabel = new JLabel("The columns are separated by:");
		indexLabel = new JLabel("Sample ID appears in column:");
		numberLabel = new JLabel("Number of fileds (columns):");
		filedsLabel = new JLabel("Fields to be shown:");
	   
		listModel = new DefaultListModel();
		listModel.addElement(1);
		
		JCheckBox check1 = new JCheckBox("1");
		JCheckBox[] myList = { check1};
		fieldCheckBox.setListData(myList);
	   
		fieldsList = new JList(listModel);
		fieldsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fieldsList.setLayoutOrientation(JList.VERTICAL);
		fieldsList.setVisibleRowCount(3);
		fieldsList.setEnabled(false);
		JScrollPane listScrollPane = new JScrollPane(fieldCheckBox);
		listScrollPane.setPreferredSize(new Dimension(50,55));
		listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	   
		fieldSpinner.addChangeListener(new ChangeListener() {
			
			int i = 0;
			public void stateChanged(ChangeEvent arg0) {
				int numberOfFields = Integer.parseInt(fieldSpinner.getValue().toString());
				JCheckBox[] myList = new JCheckBox[numberOfFields];
				for (i = 0; i < numberOfFields ; i++){
					int j = i + 1;
					final JCheckBox check = new JCheckBox("" + j);
					check.addChangeListener(new ChangeListener() {
						
						@Override
						public void stateChanged(ChangeEvent e) {
							if (check.isSelected()){
								selectedColumns.add(Integer.parseInt(check.getText()));
							}
							else{
								Iterator<Integer> search = selectedColumns.iterator();
								while (search.hasNext()){
									Integer field = search.next();
									if (field == Integer.parseInt(check.getText())){
										search.remove();
									}
								}
							}
						}
					});
					myList[i] = check;
				}
				
				fieldCheckBox.setListData(myList);
				int maxValue = (Integer) fieldSpinner.getValue();
				SpinnerModel newSpin = new SpinnerNumberModel(1,1,maxValue,1);
				idIndexSpinner.setModel(newSpin);
			}
		});
	   
		addComp(myPanel, separatorLabel, 0, 0, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, separatorCombo, 1, 0, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, numberLabel, 0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, fieldSpinner, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, indexLabel, 0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, idIndexSpinner, 1, 2, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, filedsLabel, 0, 3, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, listScrollPane, 1, 3, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, loadData, 0, 4, 2, 1, GridBagConstraints.LINE_END, GridBagConstraints.NONE);
		
		containerPanel.add(myPanel, BorderLayout.CENTER);
	   
		loadData.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent bc) {
			
			if (bc.getSource() == loadData) {
				fileChooser = new JFileChooser();
				fileChooser.setFileFilter(null);
				String home = System.getProperty("user.home");
				File directory = new File(home + "/PerspolisFiles");
				fileChooser.setCurrentDirectory(directory);
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) { 
					selectedFile = fileChooser.getSelectedFile();
					if (selectedFile == null){
						JOptionPane.showMessageDialog(Interface.bodyFrame, "No file was loaded!");
					}
					else {
						try {
							validFileLoaded = checkInputFile(selectedFile);
							if (validFileLoaded){
								JOptionPane.showMessageDialog(myPanel, selectedFile.getName() + " loaded successfully.");
							}
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

		
		});

		int result = JOptionPane.showConfirmDialog(null, containerPanel, 
               "Please choose ...", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				validFileLoaded = checkInputFile(selectedFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (selectedFile == null || validFileLoaded == false){
				JOptionPane.showMessageDialog(Interface.bodyFrame, "No valid file was loaded!");
			}
			else {
				int sampleIdIndex = Integer.parseInt(idIndexSpinner.getValue().toString());
				String separator = separatorCombo.getSelectedItem().toString();
				int fieldCount = Integer.parseInt(fieldSpinner.getValue().toString());
				int[] fields = new int[selectedColumns.size()];

				for (int i = 0; i < selectedColumns.size() ; i++){
					fields[i] = selectedColumns.get(i) - 1;
				}
				
				TabsInfo.setSamplesInformationFile(selectedFile.getName().toString(), new SampleInformation(sampleIdIndex, separator, fieldCount, fields,selectedFile));
				try {
					TabsInfo theTab = ParseKGML.getTabInfo(activeTab);
					theTab.extractSamplesInfo(selectedFile, sampleIdIndex, separator, fieldCount, fields);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public File getSelectedFile(){
		return selectedFile;
	}
	
	private boolean checkInputFile(File selectedFile) throws IOException{
		
		boolean columnSelected = true;
		String[] currentLine;
		BufferedReader br = new BufferedReader(new FileReader(selectedFile));
		String line = null;
		String inputFileHeader = br.readLine();
		
		String splitBy;
		String seperator = separatorCombo.getSelectedItem().toString();
		if (seperator.equals("tab")){
			splitBy = "\t";
		} else if (seperator.equals("space")){
			splitBy = " ";
		} else {
			splitBy = ",";
		}
		
		if (fieldCheckBox.getSelectedIndices().length == 0){
			JOptionPane.showMessageDialog(Interface.bodyFrame, "None of the columns are selected to be shown!");
			br.close();
			return false;
		}
		String[] columnNames = inputFileHeader.split(splitBy);
		int fieldCount = Integer.parseInt(fieldSpinner.getValue().toString());
		if (columnNames.length != fieldCount){
			JOptionPane.showMessageDialog(Interface.bodyFrame, "Selected number of fileds does not match with the loaded file!");
			br.close();
			return false;
		}
		else {
			while ((line = br.readLine()) != null){
				currentLine = line.split(splitBy);
				if (currentLine.length != fieldCount){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Some rows have different number of columns!");
					br.close();
					return false;
				}
			}
		}
		br.close();
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
