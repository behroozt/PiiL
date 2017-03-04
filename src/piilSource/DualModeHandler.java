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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class DualModeHandler extends JDialog{
	String[] choices = {"comma","tab","space","semicolon","dash"};
	String[] mInputTypes = {"beta-values"};
	String[] eInputTypes = {"FPKM"};
	JComboBox mSeparatorCombo = new JComboBox(choices);
	JComboBox eSeparatorCombo = new JComboBox(choices);
	JComboBox mInputTypeCombo, eInputTypeCombo;
	SpinnerModel spinID = new SpinnerNumberModel(1,1,100,1);
	SpinnerModel spinData = new SpinnerNumberModel(2,2,100,1);
	JFrame holderFrame;
	byte mValidInput, eValidInput;
	JLabel waitMessage = new JLabel();
	GridBagConstraints gridConstraints = new GridBagConstraints();
	JFileChooser fileSelector;
	JSpinner mIdRowSpinner = new JSpinner(spinID);
	JSpinner eIdRowSpinner = new JSpinner(spinID);
	JSpinner mDataRowSpinner = new JSpinner(spinData);
	JSpinner eDataRowSpinner = new JSpinner(spinData);
	JButton loadMethylation = new JButton("Load Methylation");
	JButton loadExpression = new JButton("Load Expression");
	JButton okButton = new JButton("OK");
	JButton cancelButton = new JButton("Cancel");
	JPanel myPanel;
	JCheckBox mDataCheck, eDataCheck;
	JLabel mSeparatorLabel, iconLabel, eSeparatorLabel, mIdRowLabel, eIdRowLabel, mDataRowLabel, eDataRowLabel, mInputTypeLabel, eInputTypeLabel, mFileLabel, eFileLabel, mDataLabel, eDataLabel;
	File mSelectedFile, eSelectedFile;
	boolean validFileLoaded = true;
	String[] header;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	int samplesRowNumber, dataRowNumber;
	JPanel holderPanel, iconPanel, buttonsPanel, rightPanel;
	TabsInfo methylationTab, expressionTab;
	int currentTab;
	JDialog waiting;
	
	public DualModeHandler() {
		currentTab = Interface.tabPane.getSelectedIndex();
		myPanel = new JPanel();
		myPanel.setLayout(new GridBagLayout());		
		mDataCheck = new JCheckBox();
		mDataCheck.setEnabled(false);
		mDataLabel = new JLabel("DNA methylation data");
		mSeparatorLabel = new JLabel("The columns are separated by:");
		mIdRowLabel = new JLabel("Sample IDs appear in row number:");
		mDataRowLabel = new JLabel("Data starts from row number:");
		mInputTypeLabel =  new JLabel("The data type:");
		mInputTypeCombo = new JComboBox(mInputTypes);
		cancelButton.setPreferredSize(new Dimension(100,33));
		okButton.setPreferredSize(new Dimension(100,33));
		
		eDataCheck = new JCheckBox();
		eDataCheck.setEnabled(false);
		eDataLabel = new JLabel("Gene Expression data");
		eSeparatorLabel = new JLabel("The columns are separated by:");
		eIdRowLabel = new JLabel("Sample IDs appear in row number:");
		eDataRowLabel = new JLabel("Data starts from row number:");
		eInputTypeLabel =  new JLabel("The data type:");
		eInputTypeCombo = new JComboBox(eInputTypes);
		
		holderFrame = new JFrame();
		holderFrame.setSize(550, 500);
		iconPanel = new JPanel();
		iconLabel = new JLabel(icon);
		iconPanel.add(iconLabel);
		iconPanel.setPreferredSize(new Dimension(100, 500));
		rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(500, 500));
		rightPanel.setLayout(new FlowLayout());
		iconPanel.setLayout(new FlowLayout());
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());

		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (holderFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (holderFrame.getHeight() / 2);
		holderFrame.setLocation(xPos,yPos);
		holderFrame.setResizable(false);
		holderFrame.setLayout(new BorderLayout());
		holderFrame.setTitle("Loading methylation and expression data in the same tab");
		
		gridConstraints.insets = new Insets(10,4,4,4);
		addComp(myPanel, mDataCheck, 2, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, mDataLabel, 0, 0, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);		
		addComp(myPanel, loadMethylation, 1, 0, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(4,4,4,4);
		addComp(myPanel, mSeparatorLabel, 0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, mSeparatorCombo, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, mIdRowLabel, 0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, mIdRowSpinner, 1, 2, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, mDataRowLabel, 0, 3, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, mDataRowSpinner, 1, 3, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(5,5,20,5);
		addComp(myPanel, mInputTypeLabel, 0, 4, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, mInputTypeCombo, 1, 4, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		
		gridConstraints.insets = new Insets(4,4,4,4);
		addComp(myPanel, eDataCheck, 2, 6, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, eDataLabel, 0, 6, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, loadExpression, 1, 6, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, eSeparatorLabel, 0, 7, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, eSeparatorCombo, 1, 7, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, eIdRowLabel, 0, 8, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, eIdRowSpinner, 1, 8, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, eDataRowLabel, 0, 9, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, eDataRowSpinner, 1, 9, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(5,5,20,5);
		addComp(myPanel, eInputTypeLabel, 0, 10, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, eInputTypeCombo, 1, 10, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		holderFrame.add(iconPanel, BorderLayout.WEST);
		holderFrame.add(rightPanel, BorderLayout.EAST);
		rightPanel.add(myPanel);
		rightPanel.add(buttonsPanel);
		
		loadMethylation.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				fileSelector = new JFileChooser();
				fileSelector.setFileFilter(null);
				File directory = new File(PiilMenubar.getLastOpenedDir());
				fileSelector.setCurrentDirectory(directory);
				int returnVal = fileSelector.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					mSelectedFile = fileSelector.getSelectedFile();
					PiilMenubar.setLastOpenedDir(mSelectedFile.getAbsolutePath());
					mDataCheck.setSelected(true);
					PiilMenubar.setLastOpenedDir(mSelectedFile.getAbsolutePath());
				}
				
			}
		});
		
		loadExpression.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				fileSelector = new JFileChooser();
				fileSelector.setFileFilter(null);
				File directory = new File(PiilMenubar.getLastOpenedDir());
				fileSelector.setCurrentDirectory(directory);
				int returnVal = fileSelector.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					eSelectedFile = fileSelector.getSelectedFile();
					PiilMenubar.setLastOpenedDir(eSelectedFile.getAbsolutePath());
					eDataCheck.setSelected(true);
					PiilMenubar.setLastOpenedDir(eSelectedFile.getAbsolutePath());
				}
				
			}
		});
		holderFrame.setVisible(true);
		okButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if (mDataCheck.isSelected() == false || eDataCheck.isSelected() == false){
					JOptionPane.showMessageDialog(holderFrame, "Please load both DNA methylation and gene expression files!","Warning",JOptionPane.WARNING_MESSAGE,icon);
				}
				else {
					mValidInput = eValidInput = 0;
					if (Interface.tabPane.getTabCount() > 0) {
						methylationTab = ParseKGML.getTabInfo(currentTab,0);
						List<TabsInfo> newList = new ArrayList<TabsInfo>();
						newList = Interface.tabInfoTracker.get(currentTab);
						TabsInfo added = new TabsInfo(methylationTab.getCaption(), methylationTab.getLoadedFilePath(), methylationTab.getLoadSource(), methylationTab.getPathwayName());
						added.setNodes(methylationTab.getNodes());
						added.setGenes(methylationTab.getGenes());
				        newList.add(added);
				        Interface.tabInfoTracker.remove(currentTab);
				        Interface.tabInfoTracker.add(currentTab, newList);
						expressionTab = ParseKGML.getTabInfo(currentTab,1);
						
						if (ParseKGML.getTabInfo(currentTab,0) == null) {
							JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!","Warning", 0, icon);
						} else if (methylationTab.getMapedGeneLabel().size() > 0) {
							JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!","Warning", 0, icon);
						} else {
							
							waiting = new JDialog(holderFrame, "Loading data",ModalityType.APPLICATION_MODAL);
							JProgressBar progressBar = new JProgressBar();
							progressBar.setIndeterminate(true);
							JPanel panel = new JPanel(new BorderLayout());
							panel.add(progressBar, BorderLayout.CENTER);
							waitMessage.setText(" Please wait while the loaded file is being analyzed ... ");
							panel.add(waitMessage, BorderLayout.PAGE_START);
							waiting.add(panel);
							waiting.pack();
							waiting.setLocationRelativeTo(holderFrame);
							final CheckInputFile mInput = new CheckInputFile(getSeparator(mSeparatorCombo),Integer.parseInt(mIdRowSpinner.getValue().toString()),Integer.parseInt(mDataRowSpinner.getValue().toString()));
							final CheckInputFile eInput = new CheckInputFile(getSeparator(eSeparatorCombo),Integer.parseInt(eIdRowSpinner.getValue().toString()),Integer.parseInt(eDataRowSpinner.getValue().toString()));
								
							methylationTab.setMetaType('M');
							methylationTab.setMetaFilePath(mSelectedFile);
							methylationTab.setSplitor(getSeparator(mSeparatorCombo));
							expressionTab.setMetaType('E');
							expressionTab.setMetaFilePath(eSelectedFile);
							expressionTab.setSplitor(getSeparator(eSeparatorCombo));
							
							SwingWorker<Void, Void> methylLoader = new SwingWorker<Void, Void>() {
									protected Void doInBackground() {
									try {
										List<String> chosenGroups = new ArrayList<String>();
										chosenGroups.add("methylation");
										chosenGroups.add("expression");
										
										mValidInput = methylationTab.getGenesList(mSelectedFile,mInput, true);
										eValidInput = expressionTab.getGenesList(eSelectedFile,eInput, false);
										
										for (Entry<String, Genes> gene : expressionTab.getMapedGeneLabel().entrySet()) {
											String nodeID = gene.getKey();
											Genes geneNode = gene.getValue();
											geneNode.setExpandStatus();
											
											JLabel label = expressionTab.getMapedGeneLabel().get(nodeID).getLabel();
											
											List<List<String>> data = expressionTab.getDataForGene(nodeID);
											int pointer = methylationTab.getPointer();
											int x = (int) label.getBounds().getX();
											int y = (int) label.getBounds().getY();
											int width = (int) label.getBounds().getWidth();
											int height = (int) label.getBounds().getHeight();
											Color bgColor;
											Rectangle se = new Rectangle(x + width / 2, y+ height / 2, width / 2, height / 2);
											JLabel[] extraLabels = new JLabel[1];
											int newX = x + 12;
											int newY = y+ 9;
											JLabel newOne = new JLabel();
											newOne.setBounds(newX, newY, width, height);
											String value = data.get(0).get(pointer);
											if (!isNumeric(value)){
												bgColor = Color.BLACK;
											}
											else {
												bgColor = Genes.findColor(Double.parseDouble(value), data);
											}
																						
											newOne.setBackground(bgColor);
											newOne.setOpaque(true);
											newOne.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
											Interface.panelHolder.get(currentTab).add(newOne, BorderLayout.CENTER);
											extraLabels[0] = newOne;
											geneNode.addExpandedLabels(extraLabels);
											
										}
//										
										DrawShapes shapes = new DrawShapes(methylationTab.getGraphicsItems(), methylationTab.getEdges());
										shapes.setPreferredSize(new Dimension((int) methylationTab.getMaxX(), (int) methylationTab.getMaxY()));
										Component[] components = Interface.panelHolder.get(currentTab).getComponents();
										for (int i = 0; i < components.length; i++) {
											if (components[i].getClass().equals(shapes.getClass())) {
												Interface.panelHolder.get(currentTab).remove(components[i]);
											}
										}
										Interface.panelHolder.get(currentTab).add(shapes,BorderLayout.CENTER);
											
									} catch (IOException e) {
										JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!","Warning", 0, icon);
									}
									return null;
									}
									protected void done() {
										waiting.dispose();
										holderFrame.dispose();
										Interface.bodyFrame.repaint();
										Interface.scrollPaneHolder.get(currentTab).getVerticalScrollBar().setUnitIncrement(16);
										Interface.scrollPaneHolder.get(currentTab).getHorizontalScrollBar().setUnitIncrement(16);
									}
							};
							methylLoader.execute();
							waiting.setVisible(true);
								
									if ((mValidInput == 0 && methylationTab.getMapedGeneLabel().size() > 0 && methylationTab.getMapedGeneData().size() > 0) 
											|| (eValidInput == 0 && expressionTab.getMapedGeneLabel().size() > 0 && expressionTab.getMapedGeneData().size() > 0)){
//										JMenuItem loadedFileItem = new JMenuItem(mSelectedFile.getName().toString());
//										Interface.tabPane.setBackgroundAt(Interface.tabPane.getSelectedIndex(), new Color(154,205,50));
//										loadMethylation.add(loadedFileItem);
//										Interface.bodyFrame.setJMenuBar(menubar);
									} // end of validInput
									else { // there is no overlap
										methylationTab.emptyStack();
										expressionTab.emptyStack();
									}
						}
					} // end of tabCount check
					else { // there is open tab
						JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!","Warning", 0, icon);
					}
				}
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				holderFrame.dispose();
				
			}
		});
		
	}
	
	private String getSeparator(JComboBox combo){
		String splitBy;
		String separator = combo.getSelectedItem().toString();
		if (separator.equals("tab")){
			splitBy = "\t";
		} else if (separator.equals("space")){
			splitBy = "\\s+";
		} else if (separator.equals("comma")){
			splitBy = ",";
		}
		else if (separator.equals("dash")){
			splitBy = "-";
		}
		else {
			splitBy = ";";
		}
		return splitBy;
	}
	
	private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch){
		
		gridConstraints.gridx = xPos;
		gridConstraints.gridy = yPos;
		gridConstraints.gridwidth = compWidth;
		gridConstraints.gridheight = compHeight;
		gridConstraints.weightx = 1;
		gridConstraints.weighty = 1;
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);
		
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
