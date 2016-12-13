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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Dialog.ModalityType;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.freehep.util.export.ExportDialog;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class PiilMenubar extends JMenuBar{

	JMenuItem exitAction, loadAction, aboutAction, exportEntire, manualAction, exportVisible,
	newAction, openAction, openWebAction, newSamplesInfo, reportAction, newMethylation,
	duplicateAction, duplicateMetaData, newExpression, exportGenes, snapShot, citeUs, 
	highlightGenes, checkUpdates, piilgridAction, significantSites, sdFilterSites, regionFilterSites, valueFilterSites;
	JMenu menuFile, menuLoad, menuHelp, openKGML, menuTools, loadMethylation,
	loadExpression, duplicatePathway, menuView;
	static JMenuItem multiSampleView, singleSampleView, groupWiseView, manageColors ;
	static JMenu loadSamplesInfo, selectSubset;
	JMenu menuPathwayImage;
	JMenu menuExport;
	JMenuBar menubar;
	JFileChooser fileSelector;
	JMenuBar menu;
	OpenFromWeb webLoad;
	CustomExport export;
	static String openedDirectory = System.getProperty("user.home");
	byte validInput;
	JLabel waitMessage = new JLabel();
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	final Double version = 0.11;
	String latestVersion;
	
	public PiilMenubar(){
		 menu = makeMenubar();		 
	}
	
	public JMenuBar getMenu(){
		return menu;
	}
	
	public static String getLastOpenedDir(){
		return openedDirectory;
	}

	private JMenuBar makeMenubar() {

		menubar = new JMenuBar();
		
		// File menu items
		menuFile = new JMenu("File");
		newAction = new JMenuItem("New");
		openKGML = new JMenu("Open KGML");
		openAction = new JMenuItem("from hard drive");
		openWebAction = new JMenuItem("from web");
		piilgridAction = new JMenuItem("Generate a PiiLgrid");
		exitAction = new JMenuItem("Exit");
				
		// Load menu items
		menuLoad = new JMenu("Load");
		loadMethylation = new JMenu("Methylation");
		newMethylation = new JMenuItem("New");
		loadExpression = new JMenu("Gene expression");
		newExpression = new JMenuItem("New");
		loadSamplesInfo = new JMenu("Samples information");
		newSamplesInfo = new JMenuItem("New");
		highlightGenes = new JMenuItem("List of genes");
		        
		// Export menu items  
		menuExport = new JMenu("Export");
		menuPathwayImage = new JMenu("Current pathway to image");
		exportVisible = new JMenuItem("The visible part of the pathway");
		exportEntire = new JMenuItem("The entire pathway");
		exportGenes = new JMenuItem("List of matched genes in each pathway");
		
		// View menu items
		menuView = new JMenu("View");
		singleSampleView = new JMenuItem("Single-sample view for all/selected genes");
		multiSampleView = new JMenuItem("Multiple-sample view for all/selected genes");
		groupWiseView = new JMenuItem("Group-wise view for all/selected genes");
		multiSampleView.setEnabled(false);
		singleSampleView.setEnabled(false);
		groupWiseView.setEnabled(false);
		
		// Tools menu items
		menuTools = new JMenu("Tools");
		duplicateAction = new JMenuItem("Duplicate this pathway in a new tab");
//		duplicateAction = new JMenuItem("Only the pathway");
//		duplicateMetaData = new JMenuItem("Pathway with its loaded metadata");
//		duplicateMetaData.setEnabled(false);
		snapShot = new JMenuItem("Take a snapshot of PiiL");
		snapShot.setVisible(false);
		manageColors = new JMenuItem("Manage color-coding");
		manageColors.setEnabled(false);
		sdFilterSites = new JMenuItem("Select sites by standard deviation filtering");
		regionFilterSites = new JMenuItem("Select sites by genomic region name");
		valueFilterSites = new JMenuItem("Select sites by their beta values");
		significantSites = new JMenuItem("Load a list of significant sites");
		selectSubset = new JMenu("Select a subset of CpG sites");
		selectSubset.setEnabled(false);
		
		
				        
		// Help menu items
		menuHelp = new JMenu("Help");
		reportAction = new JMenuItem("Report a bug / Send feedback");
		aboutAction = new JMenuItem("About");
		manualAction = new JMenuItem("Manual");
		citeUs = new JMenuItem("How to cite us");
		checkUpdates = new JMenuItem("Check for updates");
		
		openKGML.add(openAction);
		openKGML.add(openWebAction);
//		menuFile.add(newAction);
		menuFile.add(openKGML);
		menuFile.add(piilgridAction);
		menuFile.add(exitAction);
		menuLoad.add(loadMethylation);
		menuLoad.add(loadExpression);
		menuLoad.add(loadSamplesInfo);
		menuLoad.add(highlightGenes);
		loadMethylation.add(newMethylation);
		loadExpression.add(newExpression);
		loadSamplesInfo.setEnabled(false);
		loadSamplesInfo.add(newSamplesInfo);
		menuView.add(multiSampleView);
		menuView.add(singleSampleView);
		menuView.add(groupWiseView);
		menuPathwayImage.add(exportVisible);
		menuPathwayImage.add(exportEntire);
		menuExport.add(menuPathwayImage);
		menuExport.add(exportGenes);
		
//		duplicatePathway.add(duplicateAction);
//		duplicatePathway.add(duplcateMetaData);
		menuTools.add(snapShot);
		menuTools.add(duplicateAction);
		menuTools.add(manageColors);
		menuTools.add(selectSubset);
		selectSubset.add(sdFilterSites);
		selectSubset.add(regionFilterSites);
		selectSubset.add(valueFilterSites);
		selectSubset.add(significantSites);
		menuHelp.add(aboutAction);
		menuHelp.add(manualAction);
		menuHelp.add(reportAction);
		menuHelp.add(citeUs);
		menuHelp.add(checkUpdates);
		menubar.add(menuFile);
		menubar.add(menuLoad);
		menubar.add(menuView);
		menubar.add(menuExport);
		menubar.add(menuTools);
		menubar.add(menuHelp);
		
		ListenForMenu lForMenu = new ListenForMenu();
		openAction.addActionListener(lForMenu);
		exitAction.addActionListener(lForMenu);
		openWebAction.addActionListener(lForMenu);
		newMethylation.addActionListener(lForMenu);
		newExpression.addActionListener(lForMenu);
		newSamplesInfo.addActionListener(lForMenu);
		exportVisible.addActionListener(lForMenu);
		exportEntire.addActionListener(lForMenu);
		exportGenes.addActionListener(lForMenu);
		duplicateAction.addActionListener(lForMenu);
		snapShot.addActionListener(lForMenu);
		reportAction.addActionListener(lForMenu);
		aboutAction.addActionListener(lForMenu);
		manualAction.addActionListener(lForMenu);
		citeUs.addActionListener(lForMenu);
		multiSampleView.addActionListener(lForMenu);
		singleSampleView.addActionListener(lForMenu);
		highlightGenes.addActionListener(lForMenu);
		groupWiseView.addActionListener(lForMenu);
		checkUpdates.addActionListener(lForMenu);
		manageColors.addActionListener(lForMenu);
		sdFilterSites.addActionListener(lForMenu);
		regionFilterSites.addActionListener(lForMenu);
		valueFilterSites.addActionListener(lForMenu);
		piilgridAction.addActionListener(lForMenu);
		significantSites.addActionListener(lForMenu);
		
		return menubar;
	}
	
	private class ListenForMenu implements ActionListener{
		
		boolean validFormat=true;
		boolean validSampleInfo = false;

		/* item clicked events (ice) */
		public void actionPerformed(ActionEvent ice) {
			
			validFormat = true;			
			waitMessage.setText(" Loading organisms and pathways lists from the KEGG database ... ");
			final JDialog dialog = new JDialog(Interface.bodyFrame, "Loading data",ModalityType.APPLICATION_MODAL);
//			dialog.setUndecorated(true);
			JProgressBar progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(progressBar, BorderLayout.CENTER);
			panel.add(waitMessage, BorderLayout.PAGE_START);
			dialog.add(panel);
			dialog.pack();
			dialog.setLocationRelativeTo(Interface.bodyFrame);
			
			/* get the home directory */
			
			File directory = new File(openedDirectory);
			
			/* open KGML from hard item clicked */ 
			if (ice.getSource() == openAction){
				fileSelector = new JFileChooser();
				fileSelector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileSelector.setFileFilter(new XMLFileFilter());
				fileSelector.setCurrentDirectory(directory);
				int returnVal = fileSelector.showOpenDialog(null);
				
        		if (returnVal == JFileChooser.APPROVE_OPTION) { 
        			final File file = fileSelector.getSelectedFile();
        			openedDirectory = fileSelector.getSelectedFile().getAbsolutePath();
        			final String tabCaption = file.getName().toString();
        			       				
        			waitMessage.setText(" Loading the pathway ... ");
        			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
        				protected Void doInBackground() {
        					final Document xmlDoc = getDocument(file.toString());
        					if (xmlDoc != null && xmlDoc.getChildNodes().getLength() > 1){
        						new ParseKGML(xmlDoc,tabCaption,file,'H');
        					}
        					else {
        						validFormat = false;
        					}
        					
        					return null;
        				}
            				
        				protected void done() {
        					dialog.dispose();
        				}
            		};
            		worker.execute();
            		dialog.setVisible(true);
        		}
        		if (!validFormat){
        			JOptionPane.showMessageDialog(Interface.bodyFrame, "Unable to parse the input file. Make sure you are connected to the internet and/or check your KGML file format.","Error",0,icon);
        		}
			} // end of openAction			
			
			/* PiiLway item clicked */
			else if (ice.getSource() == piilgridAction){
				fileSelector = new JFileChooser();
				fileSelector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileSelector.setCurrentDirectory(directory);
				int returnVal = fileSelector.showOpenDialog(null);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) { 
        			final File file = fileSelector.getSelectedFile();
        			openedDirectory = fileSelector.getSelectedFile().getAbsolutePath();
        			final String tabCaption = file.getName().toString();
        			new PiiLgridMaker(file);
				}
				
			} // end of PiiLway action 
        			
			/* exit item clicked */
			else if (ice.getSource() == exitAction){
				int dialogResult = JOptionPane.showConfirmDialog(Interface.bodyFrame, "Are you sure to quit PiiL?", "Closing confirmation", 
			            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
				if (dialogResult == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			} // end of exitAction
			
			/* open KGML from web item clicked */
			else if (ice.getSource() == openWebAction){
				if (webLoad != null && webLoad.getSuccess()) {
					webLoad.ShowFrame();
				}
				else {
					waitMessage.setText(" Loading organisms and pathways lists from the KEGG database ... ");
					SwingWorker<Void, Void> webLoader = new SwingWorker<Void, Void>() {
						protected Void doInBackground() {
							webLoad = new OpenFromWeb();
							return null;
						}
						protected void done() {
							dialog.dispose();
						}
					};
					webLoader.execute();
					dialog.setVisible(true);
				}
			} // end of open KGML from web
			
			/* load new methylation item clicked */
			else if (ice.getSource() == newMethylation){
				validInput = 0;
				if (Interface.tabPane.getTabCount() > 0) {
					int currentTab = Interface.tabPane.getSelectedIndex();
					final TabsInfo theTab = ParseKGML.getTabInfo(currentTab);
					if (ParseKGML.getTabInfo(currentTab) == null) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!","Warning", 0, icon);
					} else if (theTab.getMapedGeneLabel().size() > 0) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!","Warning", 0, icon);
					} else {
						fileSelector = new JFileChooser();
						fileSelector.setFileFilter(null);
						directory = new File(openedDirectory);
						fileSelector.setCurrentDirectory(directory);
						int returnVal = fileSelector.showOpenDialog(null);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File selected = fileSelector.getSelectedFile();
							final CheckInputFile input = new CheckInputFile(selected, 'M');
							if (input.getChosenFile() != null){
								final File file = input.getChosenFile();
								openedDirectory = fileSelector.getSelectedFile().getAbsolutePath();
								theTab.setMetaType('M');
								theTab.setMetaFilePath(file);
								theTab.setSplitor(input.getSeparator());
							
								waitMessage.setText(" Please wait while the loaded file is being analyzed ... ");
								SwingWorker<Void, Void> methylLoader = new SwingWorker<Void, Void>() {
									protected Void doInBackground() {
										try {
											LineNumberReader lineNumberReader = null;
											lineNumberReader = new LineNumberReader(new FileReader(file));
											lineNumberReader.skip(Long.MAX_VALUE);
											int lines = lineNumberReader.getLineNumber();
											theTab.setMetaFileLines(lines);
											validInput = theTab.getGenesList(file,input);
										
										} catch (IOException e) {
											JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!","Warning", 0, icon);
										}
										return null;
									}
									protected void done() {
										dialog.dispose();
									}
								};
								methylLoader.execute();
								dialog.setVisible(true);
							
								if (validInput == 0 && theTab.getMapedGeneLabel().size() > 0 && theTab.getMapedGeneData().size() > 0){
									JMenuItem loadedFileItem = new JMenuItem(file.getName().toString());
								
									loadedFileItem.addActionListener(new ActionListener() {

											public void actionPerformed(ActionEvent reloadFile) {
												int currentTab = Interface.tabPane.getSelectedIndex();
												final TabsInfo thisTab = ParseKGML.getTabInfo(currentTab);
												if (ParseKGML.getTabInfo(currentTab) == null) {
													JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!","Warning", 0, icon);
												} else if (thisTab.getMapedGeneLabel().size() > 0) {
													JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!","Warning", 0, icon);
												} else {
													String fileName = reloadFile.getActionCommand();
													thisTab.setMetaType('M');
													thisTab.setMetaFilePath(file);
													thisTab.setSplitor(input.getSeparator());
													thisTab.setMetaFileLines(theTab.getMetaFileLines());

													File reloadableFile = TabsInfo.getLoadedFilePath(fileName);
														
														SwingWorker<Void, Void> methylReLoader = new SwingWorker<Void, Void>() {
															protected Void doInBackground() {
																try {
																	thisTab.getGenesList(file,input);
																
																} catch (IOException e) {
																	JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!","Error", 0, icon);
																}
																return null;
															}
															protected void done() {
																dialog.dispose();
															}
														};
														methylReLoader.execute();
														dialog.setVisible(true);
														
													if (thisTab.getMapedGeneLabel().size() > 0) {ControlPanel.enableControlPanel(0);
														thisTab.assignPointer(0);
														loadSamplesInfo.setEnabled(true);

													} else {
														JOptionPane.showMessageDialog(Interface.bodyFrame,"The loaded data has no overlap with this pathway (make sure column separator is selected correctly).","Message", 0, icon);
														thisTab.emptyStack();
													}
												}
											}
										});
								
									loadMethylation.add(loadedFileItem);
									Interface.bodyFrame.setJMenuBar(menubar);
								} // end of validInput
								else { // there is no overlap
									theTab.emptyStack();
								}
							}
						}
					}
				} // end of tabCount check
				else { // there is open tab
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!","Warning", 0, icon);
				}
				
			} //  end of newMethylation
			
			/* load new expression item clicked */
			else if (ice.getSource() == newExpression){
				validInput = 0;
				
				if (Interface.tabPane.getTabCount() > 0) {
					int currentTab = Interface.tabPane.getSelectedIndex();
					final TabsInfo theTab = ParseKGML.getTabInfo(currentTab);
					if (ParseKGML.getTabInfo(currentTab) == null) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!","Warning", 0, icon);
					} else if (theTab.getMapedGeneLabel().size() > 0) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!","Warning", 0, icon);
					} else {
						fileSelector = new JFileChooser();
						fileSelector.setFileFilter(null);
						directory = new File(openedDirectory);
						fileSelector.setCurrentDirectory(directory);
						int returnVal = fileSelector.showOpenDialog(null);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File selected = fileSelector.getSelectedFile();
							openedDirectory = fileSelector.getSelectedFile().getAbsolutePath();
							final CheckInputFile input = new CheckInputFile(selected, 'E');
							if (input.getChosenFile() != null){
							final File file = input.getChosenFile();
							theTab.setMetaType('E');
							theTab.setMetaFilePath(file);
							theTab.setSplitor(input.getSeparator());

							waitMessage.setText(" Please wait while the loaded file is being analyzed ... ");
							SwingWorker<Void, Void> expressionLoader = new SwingWorker<Void, Void>() {
								protected Void doInBackground() {
									try {
										validInput = theTab.getGenesList(file,input);
									} catch (IOException e) {
										JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!","Error", 0, icon);
									}
									return null;
								}
								protected void done() {
									dialog.dispose();
								}
							};
							expressionLoader.execute();
							dialog.setVisible(true);
							
							if (validInput == 0 && theTab.getMapedGeneLabel().size() > 0 && theTab.getMapedGeneData().size() > 0){
								JMenuItem loadedFileItem = new JMenuItem(file.getName().toString());
								loadedFileItem.addActionListener(new ActionListener() {

											public void actionPerformed(ActionEvent reloadFile) {
												int currentTab = Interface.tabPane.getSelectedIndex();
												TabsInfo thisTab = ParseKGML.getTabInfo(currentTab);
												if (ParseKGML.getTabInfo(currentTab) == null) {
													JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!","Warning", 0, icon);
												} else if (thisTab.getMapedGeneLabel().size() > 0) {
													JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!","Warning", 0, icon);
												} else {
													String fileName = reloadFile.getActionCommand();
													thisTab.setMetaType('E');
													thisTab.setMetaFilePath(file);
													thisTab.setSplitor(input.getSeparator());
													File reloadableFile = TabsInfo.getLoadedFilePath(fileName);

													try {
														thisTab.getGenesList(reloadableFile,input);
													} catch (IOException e) {
														e.printStackTrace();
														JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!","Error", 0, icon);
													}
													if (thisTab.getMapedGeneLabel().size() > 0) {
														ControlPanel.enableControlPanel(0);
														thisTab.assignPointer(0);
														loadSamplesInfo.setEnabled(true);

													} else {
														JOptionPane.showMessageDialog(Interface.bodyFrame,"The loaded list has no overlap with this pathway (make sure column separator is selected correctly).","Message", 0, icon);
														thisTab.emptyStack();
													}
												}
											}
										});
								loadExpression.add(loadedFileItem);
								Interface.bodyFrame.setJMenuBar(menubar);
							}
							else { // there is no overlap
								theTab.emptyStack();
							}
							}
						}
					}
				} // end of tabCount check
				else { // there is no open tab
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!","Warning", 0, icon);
				}
			} // end of newExpression
			
			/* load new samples info item clicked */
			else if (ice.getSource() == newSamplesInfo){
				
				fileSelector = new JFileChooser();
				fileSelector.setFileFilter(null);
				File selectedSampleInfoFile = null;
				directory = new File(openedDirectory);
				fileSelector.setCurrentDirectory(directory);
				int returnVal = fileSelector.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					JOptionPaneMultiInput patientsInfo  = new JOptionPaneMultiInput(fileSelector.getSelectedFile());
					selectedSampleInfoFile = patientsInfo.getSelectedFile();
					openedDirectory = fileSelector.getSelectedFile().getAbsolutePath();
					if (patientsInfo.validFileLoaded & selectedSampleInfoFile != null){
						
						SampleInformation inform = TabsInfo.getSamplesInformationFile(selectedSampleInfoFile.getName());
						TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
						try {
							validSampleInfo = pathway.extractSamplesInfo(inform.getFile(), inform.getIndex(), inform.getSeparator(), inform.getHeader(), inform.getColumns(), pathway.getGroupingIndex(), inform.getBarcode());
							final JMenuItem loadedInfoFile = new JMenuItem(selectedSampleInfoFile.getName());
							if (validFormat) {
								
								loadedInfoFile.addActionListener(new ActionListener() {

											public void actionPerformed(ActionEvent ev) {
												SampleInformation inform = TabsInfo.getSamplesInformationFile(ev.getActionCommand());

												try {
													TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
													validSampleInfo = pathway.extractSamplesInfo(inform.getFile(),inform.getIndex(),inform.getSeparator(),inform.getHeader(),inform.getColumns(),pathway.getGroupingIndex(), inform.getBarcode());
													if (validSampleInfo) {
														loadSamplesInfo.add(loadedInfoFile);
														Interface.bodyFrame.setJMenuBar(menubar);
														Interface.editFields.setVisible(true);
														Interface.editFields.setEnabled(true);
														groupWiseView.setEnabled(true);
													}
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										});
							}
							
							if (validSampleInfo){
								loadSamplesInfo.add(loadedInfoFile);
								Interface.bodyFrame.setJMenuBar(menubar);
								Interface.editFields.setVisible(true);
								Interface.editFields.setEnabled(true);
								groupWiseView.setEnabled(true);
							}
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}	
				}	
				
			} // end of newSampleInfo
			
			/* load list of genes item clicked */
			else if (ice.getSource() == highlightGenes){
				if (Interface.tabPane.getTabCount() > 0) {
					int currentTab = Interface.tabPane.getSelectedIndex();
					TabsInfo pathway = ParseKGML.getTabInfo(currentTab);
					if (ParseKGML.getTabInfo(currentTab) == null) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!","Warning", 0, icon);
					} else if (pathway.getMapedGeneLabel().size() > 0) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one list for each pathway!","Warning", 0, icon);
					} else {
						fileSelector = new JFileChooser();
						fileSelector.setFileFilter(null);
						fileSelector.setCurrentDirectory(directory);
						int returnVal = fileSelector.showOpenDialog(null);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							final File file = fileSelector.getSelectedFile();
							openedDirectory = fileSelector.getSelectedFile().getAbsolutePath();
							try {
								pathway.highlightInPathway(file);
								pathway.setMetaType('H');  // H for highlight genes
								pathway.setMetaFilePath(file);
							} catch (IOException e) {
								JOptionPane.showMessageDialog(Interface.bodyFrame, "Error reading the file!","Error", 0, icon);
							}
						}
					}
				}
				else { // no open tab
					JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!","Warning", 0, icon);
				}
			} // end of highlightGenes
			
			/* export visible item clicked */
			else if (ice.getSource() == exportVisible){
				int tabIndex = Interface.tabPane.getSelectedIndex();
				if (Interface.tabInfoTracker == null || ParseKGML.getTabInfo(tabIndex) == null){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!","Warning", 0, icon);
				}
				else {
					export = new CustomExport();
					String tabTitle = Interface.tabPane.getTitleAt(tabIndex);
					JViewport viewport = Interface.scrollPaneHolder.get(tabIndex).getViewport();
					
					export.showExportDialog(Interface.bodyFrame, "Export view as ...",viewport, tabTitle);
				}
			} // end of exportVisible
			
			/* export entire pathway item clicked */
			else if (ice.getSource() == exportEntire){
				int index = Interface.tabPane.getSelectedIndex();
				if (Interface.tabInfoTracker == null || ParseKGML.getTabInfo(index) == null){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!","Warning", 0, icon);
				}
				else{
					export = new CustomExport();
					String tabTitle = Interface.tabPane.getTitleAt(index);
					JComponent currentPane = Interface.panelHolder.get(index);
					export.showExportDialog(Interface.bodyFrame, "Export view as ...", currentPane, tabTitle);
					
				}
			} // end of exportEntire
			
			/* export matched genes list item clicked */
			else if (ice.getSource() == exportGenes){
				if (Interface.tabInfoTracker == null ){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!","Warning", 0, icon);
				}
				else {
					boolean metaExists = false;
					for (int i = 0; i < Interface.tabInfoTracker.size(); i ++){
						if (ParseKGML.getTabInfo(i).getMetaType() != ' '){
							metaExists = true;
						}
					}
					if (!metaExists){
						JOptionPane.showMessageDialog(Interface.bodyFrame, "At least one of the tabs must have loaded meta data!","Warning", 0, icon);
					}
					else {
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
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
					    	List<String> v = new ArrayList<String>();
					    	
					    	for (int i = 0; i < Interface.tabInfoTracker.size(); i ++){
					    		TabsInfo info = ParseKGML.getTabInfo(i);
					    		outFile.printf("loaded file: "
										+ info.getMetaFilePath().getName() + " checked in '" + info.getPathwayName() + "' pathway");
								if (info.getMapedGeneLabel() != null) {
									for (Entry<String, Genes> found : info.getMapedGeneLabel().entrySet()) {
										Genes node = found.getValue();
										String geneName = node.getText();
										v.add(geneName);
									}
									Set<String> hashsetList = new TreeSet<String>(v);
									outFile.printf("%nsorted matched genes: %s%n",hashsetList);
									v.clear();
								}
								else if (info.getMapedGeneLabel() == null){
									outFile.printf("%nNone of the genes were matched in the pathway!");
								}
							}
					    	outFile.close();
					    	JOptionPane.showMessageDialog(Interface.bodyFrame, "Information was successfully written to " + matchedGenesFile,"Done", 0, icon);
					    }
					}
				}
			} // end of exportGenes
			
			/* duplicate pathway item clicked */
			else if (ice.getSource() == duplicateAction){
				if (Interface.tabPane.getTabCount() > 0) {
					int tabIndex = Interface.tabPane.getSelectedIndex();
					TabsInfo pathway = ParseKGML.getTabInfo(tabIndex);
					File loadedFile = pathway.getLoadedFilePath();
					Document doc = pathway.getDocument();
					Character source = pathway.getLoadSource();
					String tabCaption = (pathway.getLoadSource().equals('H')) ? loadedFile.getName().toString() : pathway.getPathwayName();
					if (pathway.getLoadSource().equals('L')){
						new PiiLgridMaker(pathway.getGridGenesList());
					}
					else if (pathway.getLoadSource().equals('G')){
						new PiiLgridMaker(pathway.getLoadedFilePath());
					}
					else{
						new ParseKGML(doc, tabCaption, loadedFile, source);
					}
					
				}
				else {
					JOptionPane.showMessageDialog(Interface.bodyFrame, "There is no pathway to duplicate!","Warning",0,icon);
				}
			} // end of duplicateAction
			
			/* snapShot item clicked */
			else if (ice.getSource() == snapShot){
				export = new CustomExport();
				export.showExportDialog(Interface.bodyFrame, "Snapshot ...", (JComponent) Interface.backgroundPanel, "PiiL snapshot");
			}
			
			// show multiple-sample view
			else if (ice.getSource()== multiSampleView){
				int activeTab = Interface.tabPane.getSelectedIndex();
				TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
				int anySelected = pathway.getSelectedGenesCount();
					
				for (Entry<String, Genes> gene : pathway.getMapedGeneLabel().entrySet()) {
					String nodeID = gene.getKey();
					Genes geneNode = gene.getValue();
					if ((anySelected > 0) && (!gene.getValue().getSelectedStatus())){
						continue;
					}
						
						if (!gene.getValue().getExpandStatus()) {
							geneNode.setExpandStatus();
							JLabel label = pathway.getMapedGeneLabel().get(nodeID).getLabel();
							Character type = pathway.getMetaType();
							if (anySelected > 0){
								geneNode.setSelectedStatus();
							}
							
							List<List<String>> data = pathway.getDataForGene(nodeID);
							int pointer = pathway.getPointer();
							int x = (int) label.getBounds().getX();
							int y = (int) label.getBounds().getY();
							int width = (int) label.getBounds().getWidth();
							int height = (int) label.getBounds().getHeight();
							Color bgColor;
							Rectangle nw = new Rectangle(x, y, width / 2,height / 2);
							Rectangle ne = new Rectangle(x + width / 2, y,width / 2, height / 2);
							Rectangle sw = new Rectangle(x, y + height / 2,width / 2, height / 2);
							Rectangle se = new Rectangle(x + width / 2, y+ height / 2, width / 2, height / 2);
							
							int numberOfSamples = pathway.getSamplesIDs().size();
							int availableSamples = numberOfSamples - pointer - 1;
							int expansionSize = 0;
							if (availableSamples >= 10) {
								expansionSize = 10;
							} else {
								expansionSize = availableSamples;
							}
							JLabel[] extraLabels = new JLabel[expansionSize];

							Point expansionSide;
							if (anySelected > 0){
								expansionSide = geneNode.getSelectionPoint();
							}
							else {
								expansionSide = new Point(x+width, y + height);
							}
							

							for (int i = 1; i < (expansionSize + 1); i++) {
								int newX = 0, newY = 0;
								JLabel newOne = new JLabel();
								if (nw.contains(expansionSide)) {
									newX = x - (i * 6);
									newY = y - (i * 4);
								} else if (ne.contains(expansionSide)) {
									newX = x + (i * 6);
									newY = y - (i * 4);
								} else if (sw.contains(expansionSide)) {
									newX = x - (i * 6);
									newY = y + (i * 4);
								} else if (se.contains(expansionSide)) {
									newX = x + (i * 6);
									newY = y + (i * 4);
								} else {
									newX = x + (i * 6);
									newY = y + (i * 4);
								}
								newOne.setBounds(newX, newY, width, height);
								double sum = 0;
								for (int j = 0; j < data.size(); j++) {
									sum += Double.parseDouble(data.get(j).get(pointer + i));
								}

								if (type.equals('M')) {
									bgColor = Genes.paintLabel(sum	/ data.size());
								} else {
									bgColor = Genes.paintLabel(sum / data.size(), data, 1);
								}

								newOne.setBackground(bgColor);
								newOne.setOpaque(true);
								newOne.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
								String hint = "sample ID: "	+ ControlPanel.samplesIDsCombo.getItemAt(i).toString();
								if (pathway.getSamplesInfo().size() > 0) {
									hint += " / " + ControlPanel.setSampleInfoLabel(pointer	+ i);
								}
								newOne.setToolTipText(hint);
								Interface.panelHolder.get(activeTab).add(newOne, BorderLayout.CENTER);
								extraLabels[i - 1] = newOne;
							}
							geneNode.addExpandedLabels(extraLabels);
						}
					
				} // end of for all genes
				
					pathway.setViewMode((byte) 1);
					DrawShapes shapes = new DrawShapes(pathway.getGraphicsItems(), pathway.getEdges());
					shapes.setPreferredSize(new Dimension((int) pathway.getMaxX(), (int) pathway.getMaxY()));
					Component[] components = Interface.panelHolder.get(activeTab).getComponents();
					for (int i = 0; i < components.length; i++) {
						if (components[i].getClass().equals(shapes.getClass())) {
							Interface.panelHolder.get(activeTab).remove(components[i]);
						}
					}
					Interface.panelHolder.get(activeTab).add(shapes,BorderLayout.CENTER);
					Interface.bodyFrame.repaint();
					Interface.scrollPaneHolder.get(activeTab).getVerticalScrollBar().setUnitIncrement(16);
					Interface.scrollPaneHolder.get(activeTab).getHorizontalScrollBar().setUnitIncrement(16);

			} // end of multi-sample view
			
			/* single-sample view item clicked */
			else if (ice.getSource() == singleSampleView){
				int activeTab = Interface.tabPane.getSelectedIndex();
				TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
				
				if (pathway.getViewMode() == 2){
					pathway.setViewMode((byte) 0);
					ControlPanel.enableControlPanel(pathway.getPointer());
				}
				
				pathway.setViewMode((byte) 0);
				
				boolean anyExpanded = false;
				for (Entry<String, Genes> gene : pathway.getMapedGeneLabel().entrySet()) {
					Genes geneNode = gene.getValue();
					JLabel geneLabel = geneNode.getLabel();
					geneLabel.setVisible(true);
					if (geneNode.getExpandStatus()){
						anyExpanded = true;
						geneNode.setExpandStatus();
						JLabel[] expandedOnes = geneNode.getExpandedLabels();
						int expansionSize = expandedOnes.length;
						for (int i = 0; i < (expansionSize); i++){
							Interface.panelHolder.get(activeTab).remove(expandedOnes[i]);
						}
					}
				}
				if (!anyExpanded){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "All genes are already in single-sample view!","Warning", 0, icon);
				}
				else {
					if (pathway.getLoadSource().equals('H') || pathway.getLoadSource().equals('W')){
						DrawShapes shapes = new DrawShapes(pathway.getGraphicsItems(), pathway.getEdges());
						shapes.setPreferredSize(new Dimension((int) pathway.getMaxX(), (int) pathway.getMaxY()));
						Component[] components = Interface.panelHolder.get(activeTab).getComponents();
						for (int i = 0; i < components.length; i++) {
							if (components[i].getClass().equals(shapes.getClass())) {
								Interface.panelHolder.get(activeTab).remove(components[i]);
							}
						}
						Interface.panelHolder.get(activeTab).add(shapes,BorderLayout.CENTER);
					}
					else {
						JLabel PiiLLogo = new JLabel("", icon, JLabel.CENTER);
						PiiLLogo.setVisible(false);
						Interface.panelHolder.get(activeTab).add(PiiLLogo,BorderLayout.CENTER);
					}				
					
					Interface.bodyFrame.repaint();
					Interface.scrollPaneHolder.get(activeTab).getVerticalScrollBar().setUnitIncrement(16);
					Interface.scrollPaneHolder.get(activeTab).getHorizontalScrollBar().setUnitIncrement(16);
					pathway.setSelectedGenesCount(0);
					pathway.resetIDsInGroups(-1); // remove the ids in groups
				}
			} // end of single-sample view
			
			/* group-wise view item clicked */
			else if (ice.getSource() == groupWiseView){
				
				int activeTab = Interface.tabPane.getSelectedIndex();
				TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
				pathway.setViewMode((byte) 2);
				
				new ModifyGroup();
				
			} // end of group-wise view
			
			/* manage color-code clicked */
			else if (ice.getSource() == manageColors){
				new ColorCodeManager();
			} // end of manageColorCoding
			
			/* SD filter sites item clicked */
			else if(ice.getSource() == sdFilterSites){
				new SDFilter();
			} 
			
			/* select sites by region name item clicked */
			else if(ice.getSource() == regionFilterSites){
				new RegionFilter();
			}
			
			/* select sites by their beta values */
			else if (ice.getSource() == valueFilterSites){
				new ValueFilter();
			} 
			
			/* filter sites according to a list */
			else if (ice.getSource() == significantSites){
				fileSelector = new JFileChooser();
				fileSelector.setFileFilter(null);
				directory = new File(openedDirectory);
				fileSelector.setCurrentDirectory(directory);
				int returnVal = fileSelector.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					openedDirectory = fileSelector.getSelectedFile().getAbsolutePath();
					new SignificantSiteSelector(fileSelector.getSelectedFile());	
				}
				
			}
			
			/* about item clicked */
			else if (ice.getSource() == aboutAction){
				new AboutPiil();
			}
			
			/* manual item clicked */
			else if (ice.getSource() == manualAction){
				String URL = "http://behroozt.github.io/PiiL/";
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Problem opening the online documentation page!","Error", 0, icon);
				}
			} // end of manualAction
			
			/* report a bug item clicked */
			else if (ice.getSource() == reportAction){
				Desktop desktop;
				if (Desktop.isDesktopSupported() && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
					URI mailto;
					try {
						mailto = new URI("mailto:piil.feedback@gmail.com?subject=Reporting%20a%20bug%20/%20Sending%20feedback");
						try {
							desktop.mail(mailto);
						}
						catch (IOException e1) {
							e1.printStackTrace();
						}
					} 
					catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			} // end of reportAction
			
			/* how to cite item clicked */
			else if (ice.getSource() == citeUs){
				JOptionPane.showMessageDialog(Interface.bodyFrame, "Please cite our publication.", "Cite us", JOptionPane.QUESTION_MESSAGE,icon);
			}
			
			else if (ice.getSource() == checkUpdates){
				
				waitMessage.setText(" Checking PiiL's github for the latest version ... ");
				SwingWorker<Void, Void> methylLoader = new SwingWorker<Void, Void>() {
					protected Void doInBackground() {
						try {
							URL url = null;
							
							URLConnection con = null;
							InputStream in = null;
							String encoding = null;
							url = new URL("https://github.com/behroozt/PiiL");
							con = url.openConnection();
							in = con.getInputStream();
							encoding = con.getContentEncoding();
							encoding = encoding == null ? "UTF-8" : encoding;
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							byte[] buf = new byte[8192];
							int len = 0;
							while ((len = in.read(buf)) != -1) {
							    baos.write(buf, 0, len);
							}
							String body = new String(baos.toByteArray(), encoding);
							
							Pattern pattern = Pattern.compile("PiiL-v([0-9]*\\.?[0-9]+)");
							Matcher matcher = pattern.matcher(body);
							if (matcher.find()){
								latestVersion = matcher.group(1);
							}
						
						} catch (IOException e) {
							JOptionPane.showMessageDialog(Interface.bodyFrame,"Error connecting to PiiL's github!","Error", 0, icon);
						}
						return null;
					}
					protected void done() {
						dialog.dispose();
					}
				};
				methylLoader.execute();
				dialog.setVisible(true);
				if (Double.parseDouble(latestVersion) > version){
					int choice = JOptionPane.showConfirmDialog(null, "A newer version (v" + latestVersion + ") is available. Press OK to download it from PiiL's github."
					, "Checking for updates",JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,icon);
					if (choice == JOptionPane.OK_OPTION){
						String URL = "https://github.com/behroozt/PiiL/raw/master/PiiL-v" + latestVersion + ".jar";
						try {
							java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(Interface.bodyFrame, "Problem opening the online documentation page!","Error", 0, icon);
						}
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "You are using the latest version v" + latestVersion, "Checking for updates", JOptionPane.PLAIN_MESSAGE, icon);
				}
			} // end of checkUpdates
				
		} // end of actionPerformed

		private Document getDocument(String docString) {
		
			try {			
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setIgnoringComments(true);
			
				// Ignore white space in elements
				factory.setIgnoringElementContentWhitespace(true);
			           					
				// Validate the XML as it is parsed         
				factory.setValidating(false);	             
				// Provides access to the documents data	             
				DocumentBuilder builder = factory.newDocumentBuilder();
				
				// Takes the document      
				
				return builder.parse(new InputSource(docString));						
			}
			catch (Exception e){
				
			}
			return null;
		}
		
	} // end of menu item clicked

	public static void setSampleIdMenu(boolean value) {
		loadSamplesInfo.setEnabled(value);
	}

}
