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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RegionFilter extends JDialog{

	JPanel filterPanel;
	JLabel filterLabel;
	JCheckBox addFilter;
	JTextField regionName;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	TabsInfo activeTab;
	JDialog dialog = new JDialog(Interface.bodyFrame, "Analyzing data",ModalityType.APPLICATION_MODAL);
	JButton applyButton = new JButton("Apply");
	TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex(),0);
	
	public RegionFilter(){
		
		filterPanel = new JPanel();
		filterPanel.setLayout(new FlowLayout());
		filterLabel = new JLabel("Select CpG sites located at (separate values by comma):");
		regionName = new JTextField("");
		regionName.setPreferredSize(new Dimension(80,20));
		addFilter = new JCheckBox("Apply over the current filter");
		filterPanel.add(filterLabel);
		filterPanel.add(regionName);
		filterPanel.add(addFilter);
		filterPanel.add(applyButton);
		
		applyButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent bc) {
				HashMap<String, List<Integer>> selection = new HashMap<String, List<Integer>>();
				String[] choices = regionName.getText().split(",");
				String geneID, regionInfo;
				String siteID;
				boolean match = false;
				if (addFilter.isSelected()){
					for (Entry<String, Collection<String>> region : pathway.getMapedGeneRegion().entrySet()){
						geneID = region.getKey();
						List<Integer> selectedSites = new ArrayList<Integer>();
						selectedSites = pathway.getSelectedSites(geneID);
						if (selectedSites == null){
							for (int i= 0; i < region.getValue().size() ; i ++){
								for (int j=0 ; j < choices.length ; j ++){
									if (region.getValue().toArray()[i].toString().toLowerCase().contains(choices[j].toLowerCase().replaceAll("\\s+", ""))){
										selectedSites.add(i);
									}
								}
							}
						}
						else {
							if (selectedSites.get(0) == -1){
								
							}
							else{
								
								List<Integer> excludingIndex = new ArrayList<Integer>();
								for (int i = 0 ; i < selectedSites.size(); i ++){
									match = false;
									for (int j=0 ; j < choices.length ; j ++){
										if (region.getValue().toArray()[selectedSites.get(i)].toString().toLowerCase().contains(choices[j].toLowerCase().replaceAll("\\s+", ""))){
											match = true;
										}
									}	
									if (!match){
										excludingIndex.add(i);
									}
								}
								Collections.sort(excludingIndex, Collections.reverseOrder());
								for (int i : excludingIndex){
									selectedSites.remove(i);
								}
								excludingIndex.clear();
							}
							
						}
						if (selectedSites.size() == 0){
							selectedSites.add(-1);
						}
						selection.put(region.getKey(), selectedSites);
						
						
						
						
					} // end of for each region
					
					pathway.setSelectedSites(selection);
					Genes.changeBgColor(pathway.getPointer(), 'M');
				} // end of if
				else {
					
					for (Entry<String, Collection<String>> region : pathway.getMapedGeneRegion().entrySet()){
						List<Integer> selectedSites = new ArrayList<Integer>();
						geneID = region.getKey();
						
						selectedSites.clear();
						for (int i= 0; i < region.getValue().size() ; i ++){
							for (int j=0 ; j < choices.length ; j ++){
								if (region.getValue().toArray()[i].toString().toLowerCase().contains(choices[j].toLowerCase().replaceAll("\\s+", ""))){
									selectedSites.add(i);
									continue;
								}
							}
						}
						if (selectedSites.size() == 0){
							selectedSites.add(-1);
						}
						selection.put(region.getKey(), selectedSites);
					} // end of for each gene
					pathway.setSelectedSites(selection);
					pathway.setSDThreshold(0);
					Genes.changeBgColor(pathway.getPointer(), 'M');
				} // end of else
				
			}
		});
		
		Object[] buttons = {"Apply and Close", "Close"};
		int result = JOptionPane.showOptionDialog(null, filterPanel, "Filtering CpG sites ...", JOptionPane.OK_CANCEL_OPTION,0,icon, buttons, buttons[0]);
		if (result == JOptionPane.OK_OPTION){
			applyButton.doClick();
		} // end of OK_OPTION
		
	}
}
