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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ModifyGroup extends JOptionPane{
	
	JPanel myPanel;
	JLabel fields,groups;
	JCheckBox baseCheck;
	CheckBoxList groupsList;
	JComboBox fieldsCombo, baseCombo;
	JScrollPane groupsScrollPane;
	int activeTab = Interface.tabPane.getSelectedIndex();
	TabsInfo pathway;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	
	ModifyGroup(){
		pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
		
		myPanel = new JPanel();
		myPanel.setLayout(new FlowLayout());
		fields = new JLabel("Group the samples by:");
		groups = new JLabel("Show the following groups:");
		baseCheck = new JCheckBox("Choose the base group");
		
		groupsList = new CheckBoxList();
		
		groupsScrollPane = new JScrollPane(groupsList);
		groupsScrollPane.setPreferredSize(new Dimension(100,70));
		groupsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		List<String> header = pathway.getSamplesInfo().get("-1");
		
		fieldsCombo = new JComboBox(header.toArray());
		int groupIndex = pathway.getGroupingIndex();
		if (groupIndex == 0){
			fieldsCombo.setSelectedIndex(header.size() -1);
		}
		else {
			fieldsCombo.setSelectedIndex(groupIndex);
		}
		
		
		pathway.setGroupingIndex(fieldsCombo.getSelectedIndex());
		pathway.resetIDsInGroups(fieldsCombo.getSelectedIndex());
		Object[] hints = pathway.getIDsInGroups().keySet().toArray();
		JCheckBox[] myList = new JCheckBox[hints.length];
		
		for (int i=0; i < hints.length; i ++){
			JCheckBox check = new JCheckBox(hints[i].toString());
			check.setSelected(true);
			myList[i] = check;
		}
		groupsList.setListData(myList);
		baseCombo = new JComboBox(hints);
		baseCombo.setPreferredSize(new Dimension(100,25));
		baseCombo.setEnabled(false);
		
		fieldsCombo.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent cbe) {
				JComboBox cb = (JComboBox) cbe.getSource();
				pathway.setGroupingIndex(cb.getSelectedIndex());
				pathway.resetIDsInGroups(cb.getSelectedIndex());
				Object[] hints = pathway.getIDsInGroups().keySet().toArray();
				JCheckBox[] myList = new JCheckBox[hints.length];
				
				for (int i=0; i < hints.length; i ++){
					JCheckBox check = new JCheckBox(hints[i].toString());
					check.setSelected(true);
					myList[i] = check;
				}
				groupsList.setListData(myList);
				baseCombo.removeAllItems();
				for (Object grp : hints){
					baseCombo.addItem(grp);
				}
			}
		});
		
		baseCheck.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (pathway.getMetaType().equals('M')){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "This option is available only for expression meta data!","Message",0,icon);
					baseCheck.setSelected(false);
				}
				else {
					JCheckBox cb = (JCheckBox) e.getSource();
					if (cb.isSelected()){
						baseCombo.setEnabled(true);
					}
					else {
						baseCombo.setEnabled(false);
					}
				}
			}
		});
		
		myPanel.add(fields);
		myPanel.add(fieldsCombo);
		myPanel.add(groups);
		myPanel.add(groupsScrollPane);
		myPanel.add(baseCheck);
		myPanel.add(baseCombo);
		
		int result = JOptionPane.showConfirmDialog(null, myPanel, "Please choose ...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
		
		if (result == JOptionPane.OK_OPTION){
			List<String> chosenGroups = new ArrayList<String>();
			
			for (int i = 0 ; i < groupsList.getModel().getSize(); i ++){
				JCheckBox check = (JCheckBox) groupsList.getModel().getElementAt(i);
				if (check.isSelected()){
					chosenGroups.add(check.getText());
				}
			}
			pathway.setShowableGroups(chosenGroups);
			java.util.Collections.sort(chosenGroups);
			if (chosenGroups.indexOf("NA") != -1){
				String toMoveUp = "NA";
			    while (chosenGroups.indexOf(toMoveUp) != (chosenGroups.size() -1)) {
			        int i = chosenGroups.indexOf(toMoveUp);
			        Collections.swap(chosenGroups, i, i + 1);
			    }
			}
			GroupSamples(chosenGroups);
			
		}
	}

	private void GroupSamples( List<String> chosenGroups){
		
		pathway.setViewMode((byte) 2);
		ControlPanel.disableControlPanel(true);
		Object[] hints = pathway.getIDsInGroups().keySet().toArray();
		HashMap<String, List<String>> groups = pathway.getIDsInGroups();
		Character type = pathway.getMetaType();
		double sum;
		double caseAverage;
		int valid;
		
		for (Entry<String, Genes> gene : pathway.getMapedGeneLabel().entrySet()) {
			JLabel[] extraLabels = new JLabel[chosenGroups.size()];
			Genes geneNode = gene.getValue();
			if (geneNode.getLabel().getBackground() == Color.LIGHT_GRAY){
				continue;
			}
			final String nodeID = gene.getKey();
			JLabel geneLabel = geneNode.getLabel();
			geneLabel.setVisible(false);
			
			if (geneNode.getExpandStatus() == false){
				geneNode.setExpandStatus();
			}
			else {
				
				JLabel[] expandedOnes = geneNode.getExpandedLabels();
				int expansionSize = expandedOnes.length;
				for (int i = 0; i < (expansionSize); i++){
					Interface.panelHolder.get(activeTab).remove(expandedOnes[i]);
				}
			}
			
			JLabel label = pathway.getMapedGeneLabel().get(nodeID).getLabel();
			int x = (int) label.getBounds().getX();
			int y = (int) label.getBounds().getY();
			int width = (int) label.getBounds().getWidth();
			int height = (int) label.getBounds().getHeight();
			Color bgColor = Color.DARK_GRAY;
			List<List<String>> data = pathway.getDataForGene(nodeID);
			
			if (baseCombo.isEnabled()){
				String baseGrp = baseCombo.getSelectedItem().toString();
				int index = chosenGroups.indexOf(baseGrp);
				if (index >=0){
					chosenGroups.remove(index);
					chosenGroups.add(baseGrp);
				}
				pathway.setBaseGroupIndex(index);
			}
			else {
				pathway.setBaseGroupIndex(-1);
			}
			
			for (int i = 0; i < chosenGroups.size(); i++) {

				String hint = chosenGroups.get(i);
				JLabel newOne = new JLabel();
				int newX = 0, newY = 0;
				newX = x + (i * 7);
				newY = y + (i * 4);
				newOne.setBounds(newX, newY, width, height);
				newOne.setOpaque(true);
				newOne.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				newOne.setToolTipText(hint);
				
				Interface.panelHolder.get(activeTab).add(newOne, BorderLayout.CENTER);
				extraLabels[i] = newOne;
				sum = 0;
				caseAverage = 0;
				valid = 0 ;

				List<String> members = new ArrayList<String>(); 
				
				for (int j = 0; j < groups.get(chosenGroups.get(i)).size(); j++) {
					String sampleID = groups.get(chosenGroups.get(i)).get(j);
					int sampleIndex = pathway.getSamplesIDs().indexOf(sampleID);
					
					if (type.equals('E')){
						for (int k = 0; k < data.size(); k++) {
							if (!isNumeric(data.get(k).get(sampleIndex))){
								continue;
							}
							members.add(data.get(k).get(sampleIndex));
						}
					}
					else {
						if ((pathway.getSDThreshold() > 0) | (pathway.getSelectedSites(gene.getKey()) != null)){
							List<Integer> significantSites = pathway.getSelectedSites(gene.getKey());
						
							for (int item : significantSites){
								if (item != -1){
									if (!isNumeric(data.get(item).get(sampleIndex))){
										continue;
									}
									sum += Double.parseDouble(data.get(item).get(sampleIndex));
									valid ++;
								}
							
							}
							caseAverage = sum / valid ;
						
						}
						else {
						
							for (int k = 0; k < data.size(); k++) {
								if (!isNumeric(data.get(k).get(sampleIndex))){
									continue;
								}
								sum += Double.parseDouble(data.get(k).get(sampleIndex));
								valid ++;
							}
							caseAverage = sum / valid;
						}
					}
				} // end of for each member of the group
				
				if (type.equals('M')) {
					bgColor = Genes.paintLabel(caseAverage);
				} else if (type.equals('E')){
					
					Statistics expressionValues = new Statistics(members);
					caseAverage = expressionValues.getMedian();
					
					if (baseCombo.isEnabled()){
						List<List<String>> baseData = new ArrayList<List<String>>();
						List<String> baseValues = new ArrayList<String>();
						for (int j = 0; j < groups.get(baseCombo.getSelectedItem().toString()).size(); j++) {
							String sampleID = groups.get(baseCombo.getSelectedItem().toString()).get(j);
							int sampleIndex = pathway.getSamplesIDs().indexOf(sampleID);
							baseValues.add(data.get(0).get(sampleIndex));
						}
						baseData.add(baseValues);
						bgColor = Genes.paintLabel(caseAverage,	baseData, 0);
					}
					else {
						bgColor = Genes.paintLabel(caseAverage,	data, 0);
					}
					
				}

				if (i == 0) {
					newOne.setText(label.getText());
					newOne.setFont(new Font("Lucida Grande",Font.PLAIN, 9));
					newOne.setHorizontalAlignment(SwingConstants.CENTER);
					if (bgColor.getRed() == 255) {
						if (bgColor.getGreen() < 40) {
							newOne.setForeground(Color.LIGHT_GRAY);
						} else {
							newOne.setForeground(Color.BLACK);
						}
					} 
					else if (bgColor.getBlue() == 255) {
						if (bgColor.getGreen() < 105) {
							newOne.setForeground(Color.WHITE);
						} else {
							newOne.setForeground(Color.BLACK);
						}
					}
					if (bgColor == Color.LIGHT_GRAY){
						newOne.setForeground(Color.BLACK);
					}
					newOne.addMouseListener(new MouseListener() {

						public void mouseClicked(MouseEvent mc) {
							int x = (int) mc.getX();
							int y = (int) (mc.getY());
							Point point = new Point(x, y);
							if (SwingUtilities.isRightMouseButton(mc)) {
								new RightClickMenu(mc.getComponent(), mc.getX(), mc.getY(),nodeID, point);
							}
						}

						public void mouseEntered(MouseEvent e) {
						}

						public void mouseExited(MouseEvent e) {
						}

						public void mouseReleased(MouseEvent e) {
						}

						public void mousePressed(MouseEvent mc) {
						}
					});

				} // end if i==0

				newOne.setBackground(bgColor);
			} // end of for each chosen group

			geneNode.addExpandedLabels(extraLabels);
		} // end of for each gene
		
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
		
		Interface.setSampleInfoLabel(chosenGroups, true, pathway.getBaseGroupIndex());
		
	} // end of GroupSamples

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
