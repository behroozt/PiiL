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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.w3c.dom.Document;

import junit.awtui.ProgressBar;


public class SimilarityFinder {
	
	File input;
	JPanel northTop, northCenter, northBottom, northPanel, centerPanel, southPanel, containerPanel, southTop, southBottom;	
	JLabel progressNote, selectStartLabel, selectEndLabel, loadedFileLabel, rowCounterLabel, progressLabel, selectMiddleLabel;
	JTextField percentageField;
	static int rowCounter;
	JComboBox similarityChoice;
	String geneName;
	List<Float> targetGeneValues;
	int numberOfSamples;
	float percentage;
	TreeMap<Float, String> distanceMap;
	JScrollPane scrollPane;
	JTable genesTable;
	DefaultTableModel model;
	String geneColumn, cgColumn, distanceColumn;
	JComboBox distanceCombo;
	TabsInfo pathway;
	JButton loadButton, findButton, saveButton, generateButton;
	JFileChooser fileChooser;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	GridBagConstraints c = new GridBagConstraints();
	static JProgressBar progressBar;
	Task task;
	
	final JDialog dialog = new JDialog(Interface.bodyFrame, "Loading data",ModalityType.APPLICATION_MODAL);
	
	
	public SimilarityFinder(TabsInfo tabInfo, File file, String name, List<Float> siteValues) {
		
		input = file;
		pathway = tabInfo;
		geneName = name;
		targetGeneValues = siteValues;
		numberOfSamples = pathway.getSamplesIDs().size();
		distanceMap = new TreeMap<Float, String>();
		String[] choices = {"most", "least"};
		similarityChoice = new JComboBox(choices);
		
		model = new DefaultTableModel(new Object[]{"Gene", "CpG ID/coordinate", "Distance", "Selected"}, 0);
		
		genesTable = new JTable(model) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if(getValueAt(row, column) instanceof Boolean) {
                    return super.getDefaultRenderer(Boolean.class);
                } else {
                    return super.getCellRenderer(row, column);
                }
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if(getValueAt(row, column) instanceof Boolean) {
                    return super.getDefaultEditor(Boolean.class);
                } else {
                    return super.getCellEditor(row, column);
                }
            }
        };
		
		containerPanel =  new JPanel();
		containerPanel.setLayout(new BorderLayout());
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		
		southBottom = new JPanel();
		southBottom.setLayout(new FlowLayout(FlowLayout.RIGHT,0,5));
		selectStartLabel = new JLabel("Select top ");
		selectMiddleLabel = new JLabel(" genes with the ");
		selectEndLabel = new JLabel(" similar patterns.");
		loadedFileLabel = new JLabel(input.getName() + " has been loaded for this tab.");
		loadButton = new JButton("Load another file");
		loadButton.setPreferredSize(new Dimension(140,33));
		percentageField = new JTextField("20");
		percentageField.setPreferredSize(new Dimension(40,20));
		rowCounterLabel = new JLabel("Analayzed rows: ");
		progressLabel = new JLabel("0");
		findButton = new JButton("Find similar genes");
		findButton.setPreferredSize(new Dimension(140,33));
		scrollPane = new JScrollPane(genesTable);
		genesTable.setAutoCreateRowSorter(true);
		scrollPane.setPreferredSize(new Dimension(200,300));
		saveButton = new JButton("Save");
		saveButton.setPreferredSize(new Dimension(140,33));
		saveButton.setEnabled(false);
		generateButton = new JButton("Generate PiiLgrid");
		generateButton.setPreferredSize(new Dimension(140,33));
		generateButton.setEnabled(false);
		
		progressBar = new JProgressBar(0, pathway.getMetaFileLines());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
		
		northPanel = new JPanel(); 
		northPanel.setLayout(new BorderLayout());
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		northTop = new JPanel();
		northTop.setLayout(new BorderLayout());
		northTop.setPreferredSize(new Dimension(700,35));
		northCenter = new JPanel();
		northCenter.setLayout(new FlowLayout(FlowLayout.LEFT,0,5));
		northCenter.setPreferredSize(new Dimension(600,35));
		northBottom = new JPanel();
		northBottom.setLayout(new BorderLayout(10, 10));
		northBottom.setPreferredSize(new Dimension(600,35));	
		
		northTop.add(loadedFileLabel, BorderLayout.WEST);
		northTop.add(loadButton, BorderLayout.EAST);
		northCenter.add(selectStartLabel);
		northCenter.add(percentageField);
		northCenter.add(selectMiddleLabel);
		northCenter.add(similarityChoice);
		northCenter.add(selectEndLabel);
		northBottom.add(rowCounterLabel, BorderLayout.WEST);
		northBottom.add(progressLabel, BorderLayout.CENTER);
		northBottom.add(findButton, BorderLayout.EAST);
		
		northPanel.add(northTop, BorderLayout.NORTH);
		northPanel.add(northCenter,BorderLayout.CENTER);
		northPanel.add(northBottom, BorderLayout.SOUTH);
		
		northPanel.setSize(new Dimension(400,105));
		southPanel.setSize(new Dimension(400, 400));
		southBottom.setPreferredSize(new Dimension(600,40));
		
		centerPanel.add(scrollPane, BorderLayout.NORTH);
		centerPanel.add(progressBar,BorderLayout.SOUTH);
		southBottom.add(generateButton);
		southBottom.add(saveButton);
		southPanel.add(southBottom, BorderLayout.CENTER);
		
		containerPanel.add(northPanel, BorderLayout.NORTH);
		containerPanel.add(centerPanel, BorderLayout.CENTER);
		containerPanel.add(southPanel, BorderLayout.SOUTH);
		
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent bc) {
				File newFile;
				if (bc.getSource() == loadButton) {
					fileChooser = new JFileChooser();
					fileChooser.setFileFilter(null);
					String home = System.getProperty("user.home");
					File directory = new File(home);
					fileChooser.setCurrentDirectory(directory);
					int returnVal = fileChooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) { 
						newFile = fileChooser.getSelectedFile();
						if (newFile == null){
							JOptionPane.showMessageDialog(Interface.bodyFrame, "No file was loaded!","Warning",JOptionPane.WARNING_MESSAGE,icon);
						}
						else {
							loadedFileLabel.setText(newFile.getName() + " was selected.");
							input = newFile;
						}
					}
				}
					
			}
		});
		
		findButton.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent e) {
				
				if (percentageField.getText() != null){
					percentage = (int) Float.parseFloat(percentageField.getText());
				}
				
				if (distanceMap.entrySet().isEmpty()){
					progressBar.setStringPainted(true);
					task = new Task();
			        task.addPropertyChangeListener(new PropertyChangeListener() {
						
						@Override
						public void propertyChange(PropertyChangeEvent e) {
							if ("progress".equals(e.getPropertyName())) {
				                progressBar.setIndeterminate(false);
				                progressBar.setValue((Integer) e.getNewValue());
				            }
							
						}
					});
			        findButton.setEnabled(false);
			        saveButton.setEnabled(false);
			        generateButton.setEnabled(false);
			        task.execute();
				}
				else {
					model.getDataVector().removeAllElements();
					if (similarityChoice.getSelectedIndex() == 0){  // list most similar genes
						for (int i = 0 ; i < percentage; i++){
							geneColumn = (String) distanceMap.values().toArray()[i];
							model.addRow(new Object[]{geneColumn.split("_")[0], geneColumn.split("_")[1], distanceMap.keySet().toArray()[i], new Boolean(true)});
						}	
					}
					else { // list least similar genes
						int lastItemIndex = distanceMap.size() - 1;
						for (int i = lastItemIndex ; i > lastItemIndex - percentage ; i --){
							geneColumn = (String) distanceMap.values().toArray()[i];
							model.addRow(new Object[]{geneColumn.split("_")[0], geneColumn.split("_")[1], distanceMap.keySet().toArray()[i], new Boolean(true)});
						}
					}
				}
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser saveFile = new JFileChooser();
			    if (saveFile.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			    	File matchedGenesFile = saveFile.getSelectedFile();
			    	if (!matchedGenesFile.exists()) {
			    		try {
							matchedGenesFile.createNewFile();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
			    	
			    	PrintWriter outFile = null;
					try {
						outFile = new PrintWriter(matchedGenesFile);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
			    	List<String> v = new ArrayList<String>();
			    	String rowData;
			    	
			    	outFile.println("Gene,CpG ID/coordinate,distance");
			    	for (int row = 0; row < genesTable.getRowCount(); row++) {
			    		rowData = "";
			    		
			    		if ((Boolean) genesTable.getValueAt(row, 3)){
			    			for (int col = 0; col < genesTable.getColumnCount() - 1; col++) {
				    	    	rowData += genesTable.getValueAt(row, col) + ",";
				    	    }
			    			outFile.println(rowData.substring(0, rowData.length()-1));
			    		}
			    	}
			    	outFile.close();
			    	
			    }
				
			}
		});
		
		generateButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				ArrayList<String> genesList = new ArrayList<String>();
				for (int row = 0; row < genesTable.getRowCount() ; row ++){
					if ((Boolean) genesTable.getValueAt(row, 3)){
						if (!genesList.contains((String) genesTable.getValueAt(row, 0)))
							genesList.add((String) genesTable.getValueAt(row, 0));	
					}
				}
				new PiiLgridMaker(genesList);
			}
		});
		
		int result = JOptionPane.showConfirmDialog(null, containerPanel, "Finding similar patterns to gene " + geneName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
		
	} // end of constructor
	

	protected void analyzeData() throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = null;
		String dataSplitor = pathway.getReloadableSplitor();
		progressLabel.setVisible(true);
		rowCounterLabel.setVisible(true);
		String header = br.readLine();
		String[] elements;
		String[] geneInfo;
		float distance;
		
		rowCounter = 0;
		
		while ((line = br.readLine()) != null){
			rowCounter++;
			elements = line.split(dataSplitor);
			geneInfo = elements[0].split("_");
			if (! geneInfo[0].equals(geneName) && (elements.length -1) == numberOfSamples){
//			if ( (elements.length -1) == numberOfSamples){	
				distance = calculateEuclidianDistance(Arrays.copyOfRange(elements, 1, elements.length));
				if (distance >= 0){
					distanceMap.put(distance,elements[0]);
				}
			}
			
		} // end of while
		
		progressLabel.setText(Integer.toString(rowCounter));
		model.getDataVector().removeAllElements();
		
		for (int i = 0 ; i < percentage; i++){
			geneColumn = (String) distanceMap.values().toArray()[i];
			model.addRow(new Object[]{geneColumn.split("_")[0], geneColumn.split("_")[1], distanceMap.keySet().toArray()[i], Boolean.TRUE});
		}
		
	} // end of analyzeData


	private float calculateEuclidianDistance(String[] values) {
		
		float sum = 0;
		int invalid = 0;
		float difference;
		
		for (int i = 0; i < numberOfSamples ; i ++){
			
			if (!isNumeric(values[i])){
				invalid ++;
				continue;
			}
			difference = targetGeneValues.get(i) - Float.parseFloat(values[i]);
			sum += Math.pow(difference, 2);
		}
		if (invalid >= numberOfSamples){
			return -1;
		}
		return (float) Math.sqrt(sum);
	}


	private boolean isNumeric(String str) {
		try {  
		    double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe){  
		    return false;  
		}  
		return true;
	}
	
	
	class Task extends SwingWorker<Void, Void> {
       
        public Void doInBackground() {
        	int progress = 0;
            //Initialize progress property.
            setProgress(0);
            
            try {
				BufferedReader br = new BufferedReader(new FileReader(input));
				String line = null;
				String dataSplitor = pathway.getReloadableSplitor();
				progressLabel.setVisible(true);
				rowCounterLabel.setVisible(true);
				String header = br.readLine();
				String[] elements;
				String[] geneInfo;
				float distance;
				
				rowCounter = 0;
				
				while ((line = br.readLine()) != null){
					rowCounter++;
					progressBar.setValue(rowCounter);
					elements = line.split(dataSplitor);
					geneInfo = elements[0].split("_");
					if (! geneInfo[0].equals(geneName) && (elements.length -1) == numberOfSamples){
						distance = calculateEuclidianDistance(Arrays.copyOfRange(elements, 1, elements.length));
						if (distance >= 0){
							distanceMap.put(distance,elements[0]);
						}
					}
					
				} // end of while
				findButton.setEnabled(true);
	            saveButton.setEnabled(true);
	            generateButton.setEnabled(true);
	            progressBar.setStringPainted(true);
	            progressLabel.setText(Integer.toString(rowCounter));
				model.getDataVector().removeAllElements();
				
				for (int i = 0 ; i < percentage; i++){
					geneColumn = (String) distanceMap.values().toArray()[i];
					model.addRow(new Object[]{geneColumn.split("_")[0], geneColumn.split("_")[1], distanceMap.keySet().toArray()[i], Boolean.TRUE});
				}
				progressBar.setValue(progressBar.getMaximum());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            return null;
        }

        public void done() {
            Toolkit.getDefaultToolkit().beep();
        }
    }
	

}
