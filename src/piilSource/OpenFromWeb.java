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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
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
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class OpenFromWeb extends JDialog{

	JButton loadButton, cancelButton;
	JComboBox organismCombo, pathwayCombo;
	JFrame webFrame;
	JPanel webPanel, containerPanel;
	JLabel organismLabel, pathwayLabel;
	private HashMap <String, String> organismMap = new HashMap<String, String>();
	private HashMap <String, String> pathwayMap = new HashMap<String, String>();
	boolean valid = true;
	Scanner s;
	URL url;
	boolean successful= false;
	
	public boolean getSuccess(){
		return successful;
	}
	
	public OpenFromWeb(){
		
		webFrame = new JFrame();
		webFrame.setSize(700, 100);
		
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
		
		try {
			retrieveOrganismlist();
			retrievePathwayList();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Interface.bodyFrame, "Error retrieving organism and/or pathway lists. Please check your internet connection!");
			successful = false;
			return;
		}
		successful = true;
		organismCombo.setModel(new DefaultComboBoxModel(getOrganismList()));
		
		pathwayLabel = new JLabel("Pathway:");
		pathwayCombo = new JComboBox();
		pathwayCombo.setPreferredSize(new Dimension(250,35));
		
		
		pathwayCombo.setModel(new DefaultComboBoxModel(getPathwayList()));
		
		ListenForButton lForButton = new ListenForButton();
		loadButton = new JButton("Load");
		loadButton.addActionListener(lForButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(lForButton);
		loadButton.setPreferredSize(new Dimension(100,30));
		cancelButton.setPreferredSize(new Dimension(100,30));
		addComp(webPanel,organismLabel,0,0,1,1,GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(webPanel,organismCombo,1,0,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(webPanel,pathwayLabel,2,0,1,1,GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(webPanel,pathwayCombo,3,0,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(webPanel,loadButton,1,1,1,2,GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(webPanel,cancelButton,2,1,1,2,GridBagConstraints.WEST, GridBagConstraints.NONE);
		
		organismCombo.setSelectedItem("Select Organism");
		pathwayCombo.setSelectedItem("Select Pathway");
		
		containerPanel.add(webPanel, BorderLayout.CENTER);
		webFrame.add(containerPanel);
		webFrame.pack();
		webFrame.setAlwaysOnTop(true);
		webFrame.setVisible(true);
	}
	
	public void ShowFrame(){
		webFrame.setVisible(true);
	}
	
	public String[] getPathwayList() {
        String[] list = new String[pathwayMap.size() + 1];
        list[0] = "Select Pathway";
        int index = 1;
        for (Map.Entry entry : pathwayMap.entrySet()) {
            list[index++] = (String) entry.getValue();
        }
        Arrays.sort(list);
        return list;
    }
	
	private String[] getOrganismList() {
        String[] list = new String[organismMap.size() + 1];
        list[0] = "Select Organism";
        int index = 1;
        for (Map.Entry entry : organismMap.entrySet()) {
            list[index++] = (String) entry.getValue();
        }
        Arrays.sort(list);
        return list;
    }
	
	private void retrievePathwayList() throws Exception {
        String url = "http://rest.kegg.jp/list/pathway";
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
			
			if (be.getSource() == loadButton){
				
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
				
				for (Entry<String, String> onePathway : pathwayMap.entrySet()){
					if (onePathway.getValue().equals(selectedPathway)){
						pathway = onePathway.getKey().replace("path:map", "");
						pathwayName = onePathway.getValue();
						break;
					}
				}
								
				String fixPart = "http://www.kegg.jp/kegg-bin/download?entry=";
				keggURL = fixPart + organism + pathway + "&format=kgml";
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
					JOptionPane.showMessageDialog(webFrame, "Error opening URL! Please check your internet conncetion.");
				}
				
					SwingWorker<Void, Void> checkWorker = new SwingWorker<Void, Void>() {
						protected Void doInBackground() {
							try {
								s = new Scanner(url.openStream());
							} catch (IOException e) {
								JOptionPane.showMessageDialog(webFrame, "Error reading downloaded file! Please try again.");
							}	
							return null;
						}

						protected void done() {
							dialog.dispose();
						}
					};
					checkWorker.execute();
					message.setText(" Cheking the validity of the pathway ... ");
					dialog.setVisible(true);
					
				if (! s.hasNext()) {
					JOptionPane.showMessageDialog(webFrame, "There is no KEGG entry for your chosen organism and pathway. Please choose a different one.");
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
									JOptionPane.showMessageDialog(webFrame, "Error writing KGML file to hard drive! File not found!");
								} catch (UnsupportedEncodingException e) {
									JOptionPane.showMessageDialog(webFrame, "Error writing KGML file to hard drive! Unsupported encoding exception!");
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
									JOptionPane.showMessageDialog(Interface.bodyFrame, "Pleae check the input data. The format does not match with supported KGML file format.");
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
		}
	}
	
	private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch){
		
		GridBagConstraints gridConstraints = new GridBagConstraints();
		
		gridConstraints.gridx = xPos;
		gridConstraints.gridy = yPos;
		gridConstraints.gridwidth = compWidth;
		gridConstraints.gridheight = compHeight;
		gridConstraints.weightx = 1;
		gridConstraints.weighty = 1;
		gridConstraints.insets = new Insets(1,1,1,1);
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);
		
	}


}
