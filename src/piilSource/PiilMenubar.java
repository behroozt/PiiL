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
import java.io.IOException;
import java.io.InputStream;
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
	duplicateAction, duplcateMetaData, newExpression, exportGenes, snapShot, citeUs, highlightGenes, checkUpdates;
	JMenu menuFile, menuLoad, menuHelp, openKGML, menuTools, loadMethylation,
	loadExpression, duplicatePathway;
	static JMenuItem multiSampleView, singleSampleView, groupWiseView, manageColors;
	static JMenu loadSamplesInfo;
	JMenu menuPathwayImage;
	JMenu menuExport;
	JMenuBar menubar;
	JFileChooser fileSelector;
	JMenuBar menu;
	OpenFromWeb webLoad;
	CustomExport export;
	String openedDirectory = System.getProperty("user.home");
	byte validInput;
	JLabel waitMessage = new JLabel();
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/logoIcon.png"));
	final Double version = 0.04;
	String latestVersion;
	
	public PiilMenubar(){
		 menu = makeMenubar();		 
	}
	
	public JMenuBar getMenu(){
		return menu;
	}

	private JMenuBar makeMenubar() {

		menubar = new JMenuBar();
		
		// File menu items
		menuFile = new JMenu("File");
		newAction = new JMenuItem("New");
		openKGML = new JMenu("Open KGML");
		openAction = new JMenuItem("from hard drive");
		openWebAction = new JMenuItem("from web");
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
		
		// Tools menu items
		menuTools = new JMenu("Tools");
		duplicatePathway = new JMenu("Duplicate the current tab");
		duplicateAction = new JMenuItem("Only the pathway");
		duplcateMetaData = new JMenuItem("Pathway with its loaded metadata");
		duplcateMetaData.setEnabled(false);
//		snapShot = new JMenuItem("Take a snapshot of PiiL");
		multiSampleView = new JMenuItem("Multiple-sample view for all/selected genes");
		singleSampleView = new JMenuItem("Single-sample view for all/selected genes");
		groupWiseView = new JMenuItem("Group-wise view for all/selected genes");
		manageColors = new JMenuItem("Manage color-coding");
		multiSampleView.setEnabled(false);
		singleSampleView.setEnabled(false);
		groupWiseView.setEnabled(false);
		manageColors.setEnabled(false);
				        
		// Help menu items
		menuHelp = new JMenu("Help");
		reportAction = new JMenuItem("Report a bug");
		aboutAction = new JMenuItem("About");
		manualAction = new JMenuItem("Manual");
		citeUs = new JMenuItem("How to cite us");
		checkUpdates = new JMenuItem("Check for updates");
		
		openKGML.add(openAction);
		openKGML.add(openWebAction);
//		menuFile.add(newAction);
		menuFile.add(openKGML);
		menuFile.add(exitAction);
		menuLoad.add(loadMethylation);
		menuLoad.add(loadExpression);
		menuLoad.add(loadSamplesInfo);
		menuLoad.add(highlightGenes);
		loadMethylation.add(newMethylation);
		loadExpression.add(newExpression);
		loadSamplesInfo.setEnabled(false);
		loadSamplesInfo.add(newSamplesInfo);
		menuPathwayImage.add(exportVisible);
		menuPathwayImage.add(exportEntire);
		menuExport.add(menuPathwayImage);
		menuExport.add(exportGenes);
		menuTools.add(duplicatePathway);
		duplicatePathway.add(duplicateAction);
		duplicatePathway.add(duplcateMetaData);
//		menuTools.add(snapShot);
		menuTools.add(multiSampleView);
		menuTools.add(singleSampleView);
		menuTools.add(groupWiseView);
		menuTools.add(manageColors);
		menuHelp.add(aboutAction);
		menuHelp.add(manualAction);
		menuHelp.add(reportAction);
		menuHelp.add(citeUs);
		menuHelp.add(checkUpdates);
		menubar.add(menuFile);
		menubar.add(menuLoad);
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
//		snapShot.addActionListener(lForMenu);
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
			dialog.setUndecorated(true);
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
        			JOptionPane.showMessageDialog(Interface.bodyFrame, "Unable to parse the input file. Make sure you are connected to the internet and/or check your KGML file format.");
        		}
			} // end of openAction
        			
			/* exit item clicked */
			else if (ice.getSource() == exitAction){
				int dialogResult = JOptionPane.showConfirmDialog(Interface.bodyFrame, "Are you sure you want to exit?", "Action confirmation", 0);
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
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!");
					} else if (theTab.getMapedGeneLabel().size() > 0) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!");
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
								
								theTab.setMetaType('M');
								theTab.setMetaFilePath(file);
							
								waitMessage.setText(" Please wait while the loaded file is being analyzed ... ");
								SwingWorker<Void, Void> methylLoader = new SwingWorker<Void, Void>() {
									protected Void doInBackground() {
										try {
											validInput = theTab.getGenesList(file,input);
										
										} catch (IOException e) {
											JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!");
										}
										return null;
									}
									protected void done() {
										dialog.dispose();
									}
								};
								methylLoader.execute();
								dialog.setVisible(true);
							
								if (validInput == 0 && theTab.getMapedGeneLabel().size() > 0){
									JMenuItem loadedFileItem = new JMenuItem(file.getName().toString());
								
									loadedFileItem.addActionListener(new ActionListener() {

											public void actionPerformed(ActionEvent reloadFile) {
												int currentTab = Interface.tabPane.getSelectedIndex();
												final TabsInfo thisTab = ParseKGML.getTabInfo(currentTab);
												if (ParseKGML.getTabInfo(currentTab) == null) {
													JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!");
												} else if (thisTab.getMapedGeneLabel().size() > 0) {
													JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!");
												} else {
													String fileName = reloadFile.getActionCommand();
													thisTab.setMetaType('M');
													thisTab.setMetaFilePath(file);

													File reloadableFile = TabsInfo.getLoadedFilePath(fileName);

													
														
														SwingWorker<Void, Void> methylReLoader = new SwingWorker<Void, Void>() {
															protected Void doInBackground() {
																try {
																	thisTab.getGenesList(file,input);
																
																} catch (IOException e) {
																	JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!");
																}
																return null;
															}
															protected void done() {
																dialog.dispose();
															}
														};
														methylReLoader.execute();
														dialog.setVisible(true);
														
//														thisTab.getGenesList(reloadableFile,input);
													
													
													if (thisTab.getMapedGeneLabel().size() > 0) {ControlPanel.enableControlPanel(0);
														thisTab.assignPointer(0);
														loadSamplesInfo.setEnabled(true);

													} else {
														JOptionPane.showMessageDialog(Interface.bodyFrame,"The loaded list has no overlap with this pathway");
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
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!");
				}
				
			} //  end of newMethylation
			
			/* load new expression item clicked */
			else if (ice.getSource() == newExpression){
				validInput = 0;
				
				if (Interface.tabPane.getTabCount() > 0) {
					int currentTab = Interface.tabPane.getSelectedIndex();
					final TabsInfo theTab = ParseKGML.getTabInfo(currentTab);
					if (ParseKGML.getTabInfo(currentTab) == null) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!");
					} else if (theTab.getMapedGeneLabel().size() > 0) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!");
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

							waitMessage.setText(" Please wait while the loaded file is being analyzed ... ");
							SwingWorker<Void, Void> expressionLoader = new SwingWorker<Void, Void>() {
								protected Void doInBackground() {
									try {
										validInput = theTab.getGenesList(file,input);
									} catch (IOException e) {
										JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!");
									}
									return null;
								}
								protected void done() {
									dialog.dispose();
								}
							};
							expressionLoader.execute();
							dialog.setVisible(true);
							
							if (validInput == 0 && theTab.getMapedGeneLabel().size() > 0){
								JMenuItem loadedFileItem = new JMenuItem(file.getName().toString());
								loadedFileItem.addActionListener(new ActionListener() {

											public void actionPerformed(ActionEvent reloadFile) {
												int currentTab = Interface.tabPane.getSelectedIndex();
												TabsInfo thisTab = ParseKGML.getTabInfo(currentTab);
												if (ParseKGML.getTabInfo(currentTab) == null) {
													JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!");
												} else if (thisTab.getMapedGeneLabel().size() > 0) {
													JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one methylation/expression file for each pathway!");
												} else {
													String fileName = reloadFile.getActionCommand();
													thisTab.setMetaType('E');
													thisTab.setMetaFilePath(file);
													File reloadableFile = TabsInfo.getLoadedFilePath(fileName);

													try {
														thisTab.getGenesList(reloadableFile,input);
													} catch (IOException e) {
														e.printStackTrace();
														JOptionPane.showMessageDialog(Interface.bodyFrame,"Error loading the file!");
													}
													if (thisTab.getMapedGeneLabel().size() > 0) {
														ControlPanel.enableControlPanel(0);
														thisTab.assignPointer(0);
														loadSamplesInfo.setEnabled(true);

													} else {
														JOptionPane.showMessageDialog(Interface.bodyFrame,"The loaded list has no overlap with this pathway");
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
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!");
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
					
					if (patientsInfo.validFileLoaded & selectedSampleInfoFile != null){
						
						SampleInformation inform = TabsInfo.getSamplesInformationFile(selectedSampleInfoFile.getName());
						TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
						try {
							validSampleInfo = pathway.extractSamplesInfo(inform.getFile(), inform.getIndex(), inform.getSeparator(), inform.getHeader(), inform.getColumns(), pathway.getGroupingIndex());
							final JMenuItem loadedInfoFile = new JMenuItem(selectedSampleInfoFile.getName());
							if (validFormat) {
								
								loadedInfoFile.addActionListener(new ActionListener() {

											public void actionPerformed(ActionEvent ev) {
												SampleInformation inform = TabsInfo.getSamplesInformationFile(ev.getActionCommand());

												try {
													TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
													validSampleInfo = pathway.extractSamplesInfo(inform.getFile(),inform.getIndex(),inform.getSeparator(),inform.getHeader(),inform.getColumns(),pathway.getGroupingIndex());
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
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!");
					} else if (pathway.getMapedGeneLabel().size() > 0) {
						JOptionPane.showMessageDialog(Interface.bodyFrame,"You can not load more than one list for each pathway!");
					} else {
						fileSelector = new JFileChooser();
						fileSelector.setFileFilter(null);
						fileSelector.setCurrentDirectory(directory);
						int returnVal = fileSelector.showOpenDialog(null);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							final File file = fileSelector.getSelectedFile();
							try {
								pathway.highlightInPathway(file);
								pathway.setMetaType('H');  // H for highlight genes
								pathway.setMetaFilePath(file);
							} catch (IOException e) {
								JOptionPane.showMessageDialog(Interface.bodyFrame, "Error reading the file!");
							}
						}
					}
				}
				else { // no open tab
					JOptionPane.showMessageDialog(Interface.bodyFrame,"You need to open a KGML file first!");
				}
			} // end of highlightGenes
			
			/* export visible item clicked */
			else if (ice.getSource() == exportVisible){
				int tabIndex = Interface.tabPane.getSelectedIndex();
				if (ParseKGML.tabInfoTracker == null || ParseKGML.getTabInfo(tabIndex) == null){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!");
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
				if (ParseKGML.tabInfoTracker == null || ParseKGML.getTabInfo(index) == null){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!");
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
				if (ParseKGML.tabInfoTracker == null ){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "You need to open a KGML file first!");
				}
				else {
					boolean metaExists = false;
					for (int i = 0; i < ParseKGML.tabInfoTracker.size(); i ++){
						if (ParseKGML.getTabInfo(i).getMetaType() != ' '){
							metaExists = true;
						}
					}
					if (!metaExists){
						JOptionPane.showMessageDialog(Interface.bodyFrame, "At least one of the tabs must have loaded meta data!");
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
					    	
					    	for (int i = 0; i < ParseKGML.tabInfoTracker.size(); i ++){
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
					    	JOptionPane.showMessageDialog(Interface.bodyFrame, "Information was successfully written to " + matchedGenesFile);
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
					String tabCaption = (pathway.getLoadSource().equals('H')) ? loadedFile
							.getName().toString() : pathway.getPathwayName();
					new ParseKGML(doc, tabCaption, loadedFile, source);
				}
				else {
					JOptionPane.showMessageDialog(Interface.bodyFrame, "There is no pathway to duplicate!","Warning",0,icon);
				}
			} // end of duplicateAction
			
			/* snapShot item clicked */
//			else if (ice.getSource() == snapShot){
//				export = new CustomExport();
//				export.showExportDialog(Interface.bodyFrame, "Snapshot ...", (JComponent) Interface.backgroundPanel, "PiiL snapshot");
//			}
			
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
									bgColor = Genes.paintLabel(sum / data.size(), data);
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
					ControlPanel.enableControlPanel(activeTab);
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
					JOptionPane.showMessageDialog(Interface.bodyFrame, "All genes are already in single-sample view!");
				}
				else {
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
					pathway.setSelectedGenesCount(0);
				}
			} // end of single-sample view
			
			/* group-wise view item clicked */
			else if (ice.getSource() == groupWiseView){
				
				int activeTab = Interface.tabPane.getSelectedIndex();
				TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
				pathway.setViewMode((byte) 2);
				
				new ModifyGroup();
//				GroupSamples(pathway, activeTab);
				
			} // end of group-wise view
			
			/* manage color-code clicked */
			else if (ice.getSource() == manageColors){
				
//				new ColorCodeManager(theTab.getMetaType());
				new ColorCodeManager();
			} // end of manageColorCoding
			
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
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Problem opening the online documentation page!");
				}
			} // end of manualAction
			
			/* report a bug item clicked */
			else if (ice.getSource() == reportAction){
				Desktop desktop;
				if (Desktop.isDesktopSupported() && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
					URI mailto;
					try {
						mailto = new URI("mailto:behrooz.torabi@icm.uu.se?subject=Reporting%20a%20bug%20for%20PiiL");
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
							JOptionPane.showMessageDialog(Interface.bodyFrame,"Error connecting to PiiL's github!");
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
							JOptionPane.showMessageDialog(Interface.bodyFrame, "Problem opening the online documentation page!");
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
