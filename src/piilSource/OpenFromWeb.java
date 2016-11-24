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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class OpenFromWeb extends JDialog{

	JButton loadButton, cancelButton, fetchButton;
	JComboBox organismCombo, pathwayCombo;
	JFrame webFrame;
	JPanel webPanel, containerPanel;
	JLabel organismLabel, pathwayLabel, searchOrgLabel, searchPathLabel;
	JTextField searchOrganism, searchPathway;
	private HashMap <String, String> organismMap = new HashMap<String, String>();
	private HashMap <String, String> pathwayMap = new HashMap<String, String>();
	boolean valid = true;
	Scanner s;
	URL url;
	boolean successful= false;
	GridBagConstraints gridConstraints = new GridBagConstraints();
	String organismDefault = "Homo sapiens (human)";
	String pathwayDefault = "Press Fetch to reload";
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	JLabel waitMessage = new JLabel();
	String chosenOrganismCode;
	final JDialog dialog = new JDialog(null, "Loading data",ModalityType.APPLICATION_MODAL);
	
	public boolean getSuccess(){
		return successful;
	}
	
	public OpenFromWeb(){
		
		webFrame = new JFrame();
		webFrame.setSize(700, 150);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (webFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (webFrame.getHeight() / 2);
		webFrame.setLocation(xPos,yPos);
		webFrame.setResizable(false);
		webFrame.setTitle("Open KGML from web");
		webPanel = new JPanel();
		webPanel.setLayout(new GridBagLayout());
		containerPanel = new JPanel();
		containerPanel.setLayout(new BorderLayout());
		organismLabel = new JLabel("Organism:");
		organismCombo = new JComboBox();
		organismCombo.setPreferredSize(new Dimension(250,35));
		searchOrgLabel = new JLabel("Search:");
		searchPathLabel = new JLabel("Search:");
		searchOrganism = new JTextField();
		searchPathway = new JTextField();
		searchOrganism.setPreferredSize(organismCombo.getPreferredSize());
		searchPathway.setPreferredSize(organismCombo.getPreferredSize());
		
		webFrame.addWindowListener( new WindowAdapter() {
		    public void windowOpened( WindowEvent e ){
		        searchPathway.requestFocus();
		    }
		}); 
		
		waitMessage.setText(" Fetching available pathways for the selected organism from the KEGG database ... ");
		
		dialog.setUndecorated(true);
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(waitMessage, BorderLayout.PAGE_START);
		dialog.add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(webFrame);
		
		try {
			retrieveOrganismlist();
			retrievePathwayList();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Interface.bodyFrame, "Error retrieving organism and/or pathway lists. Please check your internet connection!","Warning",0,icon);
			successful = false;
			return;
		}
		
		successful = true;
		organismCombo.setModel(new DefaultComboBoxModel(getOrganismList("")));
		pathwayLabel = new JLabel("Pathway:");
		pathwayCombo = new JComboBox();
		pathwayCombo.setPreferredSize(new Dimension(250,35));
		pathwayCombo.setModel(new DefaultComboBoxModel(getPathwayList("")));
		
		ListenForButton lForButton = new ListenForButton();
		loadButton = new JButton("Load");
		loadButton.addActionListener(lForButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(lForButton);
		fetchButton = new JButton("Fetch available pathways");
		fetchButton.addActionListener(lForButton);
		fetchButton.setPreferredSize(new Dimension(200,32));
		loadButton.setPreferredSize(new Dimension(100,30));
		cancelButton.setPreferredSize(new Dimension(100,30));
		gridConstraints.insets = new Insets(8,10,1,1);
		addComp(webPanel,organismLabel,0,0,1,1,GridBagConstraints.EAST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(10,1,1,1);
		addComp(webPanel,organismCombo,1,0,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(12,5,5,5);
		addComp(webPanel,fetchButton,2,0,2,1,GridBagConstraints.CENTER, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(8,1,1,1);
		addComp(webPanel,pathwayLabel,4,0,1,1,GridBagConstraints.EAST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(10,1,1,10);
		addComp(webPanel,pathwayCombo,5,0,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(1,5,1,1);
		addComp(webPanel,searchOrgLabel,0,1,1,1,GridBagConstraints.EAST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(1,1,1,1);
		addComp(webPanel,searchOrganism,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(webPanel,searchPathLabel,4,1,1,1,GridBagConstraints.EAST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(1,1,1,10);
		addComp(webPanel,searchPathway,5,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(5,1,10,1);
		addComp(webPanel,loadButton,2,2,1,1,GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(webPanel,cancelButton,3,2,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE);
		
		searchOrganism.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {
			}
			
			public void keyReleased(KeyEvent arg0) {
				String searchItem = searchOrganism.getText();
				organismCombo.removeAllItems();
				organismCombo.setModel(new DefaultComboBoxModel(getOrganismList(searchItem)));
				organismCombo.setSelectedItem(organismDefault);
				if (organismCombo.getModel().getSize() > 0){
					
					fetchButton.setEnabled(true);
				}
				else {
					loadButton.setEnabled(false);
					fetchButton.setEnabled(false);
				}
			}
			
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER){
					if (organismCombo.getModel().getSize() > 0){
						fetchButton.doClick();
					}
				}
			}
		});
		
		searchPathway.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {
			}
			
			public void keyReleased(KeyEvent arg0) {
				String searchItem = searchPathway.getText();
				pathwayCombo.removeAllItems();
				pathwayCombo.setModel(new DefaultComboBoxModel(getPathwayList(searchItem)));
				if (pathwayCombo.getModel().getSize() == 0){
					loadButton.setEnabled(false);
				}
				else {
					loadButton.setEnabled(true);
				}
				
			}
			
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER){
					loadButton.doClick();
				}
			}
		});
		
		organismCombo.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {				
			}
			
			public void keyReleased(KeyEvent arg0) {
			}
			
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER){
					fetchButton.doClick();
				}
			}
		});
		
		organismCombo.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent arg0) {
				searchPathway.setText("");
				searchPathway.setEnabled(false);
				pathwayCombo.setSelectedItem(pathwayDefault);
				pathwayCombo.setEnabled(false);
				fetchButton.setEnabled(true);
			}
		});
		
		organismCombo.setSelectedItem(organismMap.get("hsa"));
		fetchButton.setEnabled(false);
		pathwayCombo.setEnabled(true);
		searchPathway.setEnabled(true);
		pathwayCombo.setSelectedIndex(0);
		containerPanel.add(webPanel, BorderLayout.CENTER);
		webFrame.add(containerPanel);
		webFrame.pack();
		webFrame.setAlwaysOnTop(true);
		webFrame.setVisible(true);
	} // end of constructor 
	
	public void ShowFrame(){
		webFrame.setVisible(true);
	}
	
	public String[] getPathwayList(String item) {
        List<String> list = new ArrayList<String>();
        
        if (item.equals("")){
        	list.add(pathwayDefault);
            for (Map.Entry entry : pathwayMap.entrySet()) {
                list.add(entry.getValue().toString());
            }
        }
        else {
        	for (Map.Entry entry : pathwayMap.entrySet()) {
        		String value = entry.getValue().toString();
        		if (value.toLowerCase().contains(item.toLowerCase())){
        			list.add(value);
        		}
        	}
        	
        }
        String[] itemsList = new String[list.size()];
        for (int i = 0; i < list.size() ; i ++){
        	itemsList[i] = list.get(i).toString();
        }
        Arrays.sort(itemsList);
        return itemsList;
    }
	
	private String[] getOrganismList(String item) {
		List<String> list = new ArrayList<String>();
        
        if (item.equals("")){
            for (Map.Entry entry : organismMap.entrySet()) {
                list.add(entry.getValue().toString());
            }
        }
        else {
        	for (Map.Entry entry : organismMap.entrySet()) {
        		String value = entry.getValue().toString();
        		if (value.toLowerCase().contains(item.toLowerCase())){
        			list.add(value);
        		}
        	}
        	
        }
		
        String[] itemsList = new String[list.size()];
        for (int i = 0; i < list.size() ; i ++){
        	itemsList[i] = list.get(i).toString();
        }
        Arrays.sort(itemsList);
        return itemsList;
    }
	
	private void retrievePathwayList() throws Exception {
        String url = "http://rest.kegg.jp/list/pathway/hsa";
        String result = null;
        try {
            result = sendRestRequest(url).toString();
        } catch (Exception e) {
//            JOptionPane.showMessageDialog(Interface.bodyFrame, "Error downloading pathways list from KEGG database.");
        }
        StringTokenizer tokenizer = new StringTokenizer(result, "\n");
        while (tokenizer.hasMoreTokens()) {
            StringTokenizer lineTokenizer = new StringTokenizer(tokenizer.nextToken(), "\t");
            if (lineTokenizer.hasMoreTokens())
                pathwayMap.put(lineTokenizer.nextToken(), lineTokenizer.nextToken());
        }
    }
	
	private void retrievePathwayList(String orgCode) throws Exception {
        String url = "http://rest.kegg.jp/list/pathway/" + orgCode;
        String result = null;
        
        try {
            result = sendRestRequest(url).toString();
        } catch (Exception e) {
//            JOptionPane.showMessageDialog(Interface.bodyFrame, "Error downloading pathways list from KEGG database.");
        }
        pathwayMap.clear();
        StringTokenizer tokenizer = new StringTokenizer(result, "\n");
        while (tokenizer.hasMoreTokens()) {
            StringTokenizer lineTokenizer = new StringTokenizer(tokenizer.nextToken(), "\t");
            if (lineTokenizer.hasMoreTokens())
                pathwayMap.put(lineTokenizer.nextToken(), lineTokenizer.nextToken());
        }
    }
	
	private void retrieveOrganismlist() throws Exception {
        String url = "http://rest.kegg.jp/list/organism";
        String result = null;
        try {
            result = sendRestRequest(url).toString();
        } catch (Exception e) {
//        	JOptionPane.showMessageDialog(Interface.bodyFrame, "Error downloading organisms list from KEGG database.");
        }
        organismMap.put("map", "generic map");
        StringTokenizer tokenizer = new StringTokenizer(result, "\n");
        while (tokenizer.hasMoreTokens()) {
            StringTokenizer lineTokenizer = new StringTokenizer(tokenizer.nextToken(), "\t");
            lineTokenizer.nextToken();
            if (lineTokenizer.hasMoreTokens())
                organismMap.put(lineTokenizer.nextToken(), lineTokenizer.nextToken());
        }
    }
	
	public static StringBuffer sendRestRequest(String url) throws Exception {

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
            in.close();
            return response;
        } catch (IOException e) {
            throw new Exception("Error while sending request: " + e.getMessage());
        }
    }
	
	private class ListenForButton implements ActionListener{

		public void actionPerformed(ActionEvent be) {
			
			if (be.getSource() == cancelButton){
				webFrame.setVisible(false);
			}
			
			else if (be.getSource() == loadButton){
				
				if ((organismCombo.getSelectedIndex() == -1) | (pathwayCombo.getSelectedIndex() == -1)){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Please select a pathway/organism from the list!","Warning",0,icon);
					webFrame.setVisible(true);
				}
				else if (pathwayCombo.getSelectedItem().equals(pathwayDefault)){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Please select a pathway/organism from the list!","Warning",0,icon);
					webFrame.setVisible(true);
				}

				else {

				final File downloaded;
				final String keggURL;
				JLabel message;
				
				final JDialog dialog = new JDialog(Interface.bodyFrame, "Loading data",ModalityType.APPLICATION_MODAL);
				dialog.setUndecorated(true);
				JProgressBar progressBar = new JProgressBar();
				progressBar.setIndeterminate(true);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(progressBar, BorderLayout.CENTER);
				message = new JLabel();
				message.setPreferredSize(new Dimension(270,15));
				panel.add(message, BorderLayout.PAGE_START);
				dialog.add(panel);
				dialog.pack();
				dialog.setLocationRelativeTo(Interface.bodyFrame);
				
				String selectedOrganism = organismCombo.getSelectedItem().toString();
				String organism = null, organismName = null;
								
				for (Entry<String, String> oneOrganism : organismMap.entrySet()){
					if (oneOrganism.getValue().equals(selectedOrganism)){
						organism = oneOrganism.getKey();
						organismName = oneOrganism.getValue();
						break;
					}
				}
				
				String selectedPathway = pathwayCombo.getSelectedItem().toString();
				String pathway = null, pathwayName = null;
				String removable = "path:" + organism;
				for (Entry<String, String> onePathway : pathwayMap.entrySet()){
					if (onePathway.getValue().equals(selectedPathway)){
						pathway = onePathway.getKey().replace(removable, "");
						pathwayName = onePathway.getValue();
						break;
					}
				}
				// http://rest.kegg.jp/get/hsa05130/kgml				
				String fixPart = "http://rest.kegg.jp/get/";
				keggURL = fixPart + organism + pathway + "/kgml";
				final String fileName = organism + pathway + ".xml";
				String home = System.getProperty("user.home");
				File directory = new File(home + "/PiilDownloads");
				if (! directory.exists()){
					directory.mkdir();
				}
				downloaded = new File(directory + File.separator + fileName);
				
				try {
					url = new URL(keggURL);
				} catch (MalformedURLException e) {
					JOptionPane.showMessageDialog(webFrame, "Error opening URL! Please check your internet conncetion.","Warning",0,icon);
				}
				
				try {
					s = new Scanner(url.openStream());
				} catch (IOException e) {
					JOptionPane.showMessageDialog(webFrame, "Error reading downloaded file! Please try again.","Warning",0,icon);
				}	
					
				if (! s.hasNext()) {
					JOptionPane.showMessageDialog(webFrame, "There is no KEGG entry for your chosen organism and pathway. Please choose a different one.","Mismatch",0,icon);
					valid = false;
					return;
				}
				
				SwingWorker<Void, Void> downloadWorker = new SwingWorker<Void, Void>() {
					protected Void doInBackground() {
						
						if (! downloaded.exists()){
								valid = true;
								PrintWriter writer = null;
								try {
									writer = new PrintWriter(downloaded, "UTF-8");
								} catch (FileNotFoundException e) {
									JOptionPane.showMessageDialog(webFrame, "Error writing KGML file to hard drive! File not found!","Error",0,icon);
								} catch (UnsupportedEncodingException e) {
									JOptionPane.showMessageDialog(webFrame, "Error writing KGML file to hard drive! Unsupported encoding exception!","Error",0,icon);
								}
								while (s.hasNextLine()) 
									writer.println(s.nextLine());
						        s.close();
						        writer.close();
						}
						return null;
					}

					protected void done() {
						dialog.dispose();
					}
				};
				
					downloadWorker.execute();
					message.setText(" Downloading from KEGG pathway database ... ");
					dialog.setVisible(true);
				
					final String tabCaption = pathwayCombo.getSelectedItem().toString();
                    					
					try {

						SwingWorker<Void, Void> loadWorker = new SwingWorker<Void, Void>() {
							protected Void doInBackground() {
								
								final Document xmlDoc = getDocument(downloaded.toString());
	        					if (xmlDoc != null){
	        						new ParseKGML(xmlDoc,tabCaption, downloaded, 'W');
	        					}
								return null;
							}
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
									JOptionPane.showMessageDialog(Interface.bodyFrame, "Pleae check the input data. The format does not match with supported KGML file format.","Error",0,icon);
								}
								return null;
							}
							protected void done() {
								dialog.dispose();
							}
						};
						loadWorker.execute();
						message.setText(" Loading pathway ... ");
						dialog.setVisible(true);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				webFrame.setVisible(false);
				}
			} //  end of loadButton
			
			else if (be.getSource() == fetchButton){
				String selectedOrganism = organismCombo.getSelectedItem().toString();

				for (Entry<String, String> oneOrganism : organismMap.entrySet()){
					if (oneOrganism.getValue().equals(selectedOrganism)){
						chosenOrganismCode = oneOrganism.getKey();
						break;
					}
				}
				
				SwingWorker<Void, Void> webLoader = new SwingWorker<Void, Void>() {
					protected Void doInBackground() {
						try {
							retrievePathwayList(chosenOrganismCode);
							searchPathway.setEnabled(true);
							pathwayCombo.setEnabled(true);
							loadButton.setEnabled(true);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pathwayCombo.removeAllItems();
						pathwayCombo.setModel(new DefaultComboBoxModel(getPathwayList("")));
						return null;
					}
					protected void done() {
						dialog.dispose();
					}
				};
				webLoader.execute();
				dialog.setVisible(true);
				webFrame.setAlwaysOnTop(true);
			}
		}
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


}
