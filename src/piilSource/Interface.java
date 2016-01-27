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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class Interface extends JFrame{
	
	static JFrame bodyFrame;
	JPanel thePanel, sampleInfoPanel, bodyPanel, sidePanel, menuPanel, drawingPanel, controlPanel;
	static JPanel backgroundPanel;
	static JLabel sampleInfoLabel;
	GridBagConstraints gridConstraints = new GridBagConstraints();
		
	static PiilMenubar mainMenu;
	static CustomTabPane tabPane;
	static ArrayList<JScrollPane> scrollPaneHolder;
	static ArrayList<JPanel> panelHolder;
	
	public static void main(String[] args) {
		new Interface();
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
		bodyFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		sampleInfoPanel = new JPanel();
		sampleInfoPanel.setSize(new Dimension(1000, 18));
		sampleInfoLabel = new JLabel("Sample Info");
		sampleInfoLabel.setEnabled(false);
		sampleInfoLabel.setPreferredSize(new Dimension(1000,18));
		sampleInfoPanel.setLayout(new GridBagLayout());
		sampleInfoPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
		sampleInfoLabel.setForeground(sampleInfoPanel.getBackground());
		
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
		drawingPanel.setLayout(new GridBagLayout());
		
		/* controlPanel holds the sidePanel */
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(160,500));
		sidePanel = new ControlPanel().makeSidePanel();
		
		sidePanel.setBackground(new Color(185,185,185));
		controlPanel.add(sidePanel, BorderLayout.NORTH);
		controlPanel.setBackground(new Color(185,185,185));
		controlPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
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
			   @Override
			   protected void installDefaults() {
			       super.installDefaults();
			       highlight = Color.pink;
			       lightHighlight = new Color(185,185,185);
			       shadow = Color.red;
			       darkShadow = Color.cyan;
			       focus = Color.BLUE;
			   }
			});

		ChangeListener changeListener = new ChangeListener() {
			
			public void stateChanged(ChangeEvent changeEvent) {
				ControlPanel.stopTimer();
				final JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				int selectedTab = sourceTabbedPane.getSelectedIndex();
				
				if (ParseKGML.tabInfoTracker.size() == 0) {
					ControlPanel.disableControlPanel();
				}
				else {
					if (ParseKGML.tabInfoTracker.size() < sourceTabbedPane.getTabCount()){
						ControlPanel.disableControlPanel();
					}
					else {
						if (tabPane.getTabCount() > 0) {
							TabsInfo thisTab = ParseKGML.getTabInfo(selectedTab);
							int tabPointer = thisTab.getPointer();
							
							if (thisTab.getMapedGeneLabel().size() > 0) {
								ControlPanel.enableControlPanel(tabPointer);
							} else {
								ControlPanel.disableControlPanel();
							}
						}
						else {
							ControlPanel.disableControlPanel();
							sampleInfoLabel.setForeground(sampleInfoPanel.getBackground());
						}
					}
					
				}
				
			}
		};
		
		tabPane.addChangeListener(changeListener);

		addComp(drawingPanel, sampleInfoPanel, 0, 0, 1, 1, 500, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);
		addComp(drawingPanel, tabPane, 0, 1, 1, 1, 1, 500, GridBagConstraints.CENTER, GridBagConstraints.BOTH);

		gridConstraints.insets = new Insets(1,15,4,1);
		addComp(sampleInfoPanel, sampleInfoLabel, 1, 1, 1, 1, 500,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL);
		bodyFrame.add(backgroundPanel,BorderLayout.CENTER);
		new Splash(3000);	
		bodyFrame.setVisible(true);
		
	}
	
	public static void setSampleInfoLabel(String text, boolean enable){
		sampleInfoLabel.setText(text);
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
