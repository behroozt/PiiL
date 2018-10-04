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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class Interface extends JFrame{
	
	static JFrame bodyFrame;
	JPanel thePanel, sampleInfoPanel, bodyPanel, sidePanel, menuPanel, controlPanel, drawingPanel;
	static JPanel backgroundPanel;
	static JLabel sampleInfoLabel;
	GridBagConstraints gridConstraints = new GridBagConstraints();
		
	static PiilMenubar mainMenu;
	static CustomTabPane tabPane;
	static ArrayList<JScrollPane> scrollPaneHolder;
	static ArrayList<JPanel> panelHolder;
	static JButton editFields;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	static JScrollPane toolbox, informationPane;
	static ArrayList<List<TabsInfo>> tabInfoTracker;
	static Color hypoColor, hyperColor;
	
	public static void main(String[] args) {
		new Interface();
		
	}
	
	public static void setHypoColor(Color c){
		hypoColor = c;
	}
	
	public static void setHyperColor(Color c){
		hyperColor = c;
	}
	
	public static Color getHypoColor(){
		return hypoColor;
	}
	
	public static Color getHyperColor(){
		return hyperColor;
	}
	
	public Interface(){
		
		try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
		
		int scrWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int scrHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		bodyFrame = new JFrame();
		bodyFrame.setSize(scrWidth, scrHeight);
		bodyFrame.setTitle(" PiiL - Pathway interactive visualization tooL ");
		
		hyperColor = new Color(255,0,0);
		hypoColor = new Color(0,0,255);
		
		bodyFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);	
		
		bodyFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);		
		bodyFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(bodyFrame, 
		            "Are you sure to quit PiiL?", "Closing confirmation", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE, icon) == JOptionPane.YES_OPTION){
		            System.exit(0);
		        }
		    }
		});
		
		sampleInfoPanel = new JPanel();
		sampleInfoPanel.setPreferredSize(new Dimension(1000, 45));
		sampleInfoPanel.setLayout(new GridBagLayout());
		sampleInfoLabel = new JLabel(" Sample's Information");
		sampleInfoLabel.setEnabled(false);
		informationPane = new JScrollPane(sampleInfoLabel);
		informationPane.setPreferredSize(new Dimension(950,40));
		informationPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		informationPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sampleInfoPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		sampleInfoLabel.setOpaque(true);
		sampleInfoLabel.setBackground(sampleInfoPanel.getBackground());
		sampleInfoLabel.setForeground(sampleInfoPanel.getBackground());
		informationPane.setBackground(sampleInfoPanel.getBackground());
		editFields = new JButton("Edit Fields");
		editFields.setPreferredSize(new Dimension(120,40));
		editFields.setEnabled(false);
		editFields.setVisible(true);
		editFields.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent bc) {
				new ModifySampleFields();
			}
		});
		/* backgroundPanel keeps menuPanel and bodyPanel */
		backgroundPanel = new JPanel();
		backgroundPanel.setLayout(new BorderLayout());
		menuPanel = new JPanel();
		menuPanel.setLayout(new BorderLayout());
		bodyPanel= new JPanel();
		bodyPanel.setLayout(new BorderLayout());
		backgroundPanel.add(menuPanel,BorderLayout.NORTH);	
		backgroundPanel.add(bodyPanel);
		drawingPanel= new JPanel();
		drawingPanel.setLayout(new BorderLayout());
		
		/* controlPanel holds the sidePanel */
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(170,500));
		controlPanel.setLayout(new BorderLayout());
		sidePanel = new ControlPanel().makeSidePanel();
		
		sidePanel.setBackground(new Color(185,185,185));
		toolbox = new JScrollPane(sidePanel);
		toolbox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		toolbox.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		toolbox.setPreferredSize(new Dimension(170,500));
		controlPanel.add(toolbox, BorderLayout.WEST);
		controlPanel.setBackground(new Color(185,185,185));
		toolbox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		bodyPanel.add(controlPanel, BorderLayout.WEST);
		bodyPanel.add(drawingPanel, BorderLayout.CENTER);
		
		mainMenu = new PiilMenubar();
		bodyFrame.setJMenuBar(mainMenu.getMenu());
		menuPanel.add(mainMenu, BorderLayout.NORTH);
		panelHolder = new ArrayList<JPanel>();
        scrollPaneHolder = new ArrayList<JScrollPane>();
		tabPane = new CustomTabPane();
		tabPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		tabPane.setUI(new BasicTabbedPaneUI() {
			   
			   protected void installDefaults() {
			       super.installDefaults();
			       highlight = Color.RED;
			       lightHighlight = new Color(185,185,185);
			       
			       shadow = Color.red;
			       darkShadow = Color.cyan;
			       focus = new Color(215,12,0);
			   }
			});
		
		ChangeListener changeListener = new ChangeListener() {
			
			public void stateChanged(ChangeEvent changeEvent) {
				ControlPanel.stopTimer();
				final JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int selectedTab = sourceTabbedPane.getSelectedIndex();
				if (tabInfoTracker.size() == 0) {
					ControlPanel.disableControlPanel(false);
				}
				else {
					if (tabInfoTracker.size() < sourceTabbedPane.getTabCount()){
						ControlPanel.disableControlPanel(false);
					}
					else {
						if (tabPane.getTabCount() > 0) {
							TabsInfo thisTab = ParseKGML.getTabInfo(selectedTab,0);
							int tabPointer = thisTab.getPointer();
							
							if (thisTab.getMapedGeneLabel().size() > 0) {
								ControlPanel.enableControlPanel(tabPointer);
							} else {
								ControlPanel.disableControlPanel(false);
							}
						}
						else {
							ControlPanel.disableControlPanel(false);
							sampleInfoLabel.setForeground(sampleInfoPanel.getBackground());
						}
					}
					
				}
				
			}
		};
		
		tabPane.addChangeListener(changeListener);
		tabPane.addMouseListener(new MouseListener() {
			int sampleIDIndex;
			public void mouseReleased(MouseEvent arg0) {}		
		
			public void mousePressed(MouseEvent arg0) {}
			
			public void mouseExited(MouseEvent arg0) {}
			
			public void mouseEntered(MouseEvent arg0) {}
			
			public void mouseClicked(MouseEvent mc) {
				if (mc.getClickCount() == 2){
					TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex(),0);
					
					if (pathway.getMetaFilePath() != null){
						ControlPanel.samplesIDsCombo.removeAllItems();
						ArrayList<String> identifiers = ControlPanel.sortedItems;
						for (String sampleID : identifiers){
							ControlPanel.samplesIDsCombo.addItem(sampleID);
						}
						pathway.setSortedSampleIDs(identifiers);
						ControlPanel.samplesIDsCombo.setSelectedIndex(pathway.getPointer());
						sampleIDIndex = pathway.getSamplesIDs().indexOf(ControlPanel.samplesIDsCombo.getSelectedItem());
						Genes.changeBgColor(sampleIDIndex,pathway.metaType);
						if (pathway.getSamplesInfo() != null && pathway.getSamplesInfo().size() > 0){
							ControlPanel.setSampleInfoLabel(sampleIDIndex);
						}
			      	    else {
							Interface.setSampleInfoLabel(ControlPanel.samplesIDsCombo.getItemAt(pathway.getPointer()).toString(), true);
						}
					}
					
				}
				
			}
		});
		
		addComp(sampleInfoPanel, informationPane, 0, 0, 4, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH);
		addComp(sampleInfoPanel, editFields, 4, 0, 1, 1, 0,0, GridBagConstraints.EAST, GridBagConstraints.BOTH);
		drawingPanel.add(sampleInfoPanel, BorderLayout.NORTH);
		drawingPanel.add(tabPane);
		gridConstraints.insets = new Insets(1,15,4,1);
		
		bodyFrame.add(backgroundPanel,BorderLayout.CENTER);
		new Splash(3000);	
		bodyFrame.setIconImage(createImage("/resources/icon.png").getImage());
		bodyFrame.setVisible(true);
	}
	
	private ImageIcon createImage(String path) {
		
		return new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getClass().getResource(path));
	}

	public static void setSampleInfoLabel(String text, boolean enable){
		sampleInfoLabel.setText(" " + text + " ");
		sampleInfoLabel.setEnabled(enable);
		sampleInfoLabel.setForeground(Color.BLACK);
	}
	
	public static void setSampleInfoLabel(List<String> hints, boolean enable, int baseIndex) {
		String groups = "";
		String grpTag = null;
		for (String group : hints ){
			int members = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex(),0).getIDsInGroups().get(group).size();
			groups += (String) group + " (" + members + ") " + ", ";
		}
		
		groups = groups.substring(0, groups.length()-2);
		if (baseIndex != -1){
			groups = groups + " (the base group)";
		}
		grpTag = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex(),0).getSamplesInfo().get("-1").get(ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex(),0).getGroupingIndex());
		sampleInfoLabel.setText(" Samples grouped by '" + grpTag + "' (top to buttom): " + groups);
		
		sampleInfoLabel.setEnabled(enable);
		sampleInfoLabel.setForeground(Color.BLACK);
		
	}
	
	public static void addNewPanel(JPanel panel){
		panelHolder.add(panel);
	}
	
	public static void removeSelectedPanel(int tab){
		panelHolder.remove(tab);
	}
	
	public static void addNewScrollPane(JScrollPane scpane){
		scrollPaneHolder.add(scpane);
	}
	
	public static void removeSelectedScrollPane(int tab){
		scrollPaneHolder.remove(tab);
	}
	
	public void addComp(JComponent thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight,int compWx, int compWy, int place, int stretch){
		
		gridConstraints.gridx = xPos;
		gridConstraints.gridy = yPos;
		gridConstraints.gridwidth = compWidth;
		gridConstraints.gridheight = compHeight;
		gridConstraints.weightx = compWx;
		gridConstraints.weighty = compWy;
		gridConstraints.insets = new Insets(1,1,1,1);
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);	
	}

}
