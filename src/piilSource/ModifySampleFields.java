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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ModifySampleFields{
	
	JPanel myPanel;
	JLabel choose;
	JScrollPane checkBoxPane;
	TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	JButton selectAll;
	List<String> options;
	List<String> header;
	JCheckBox[] myList;
	
	public ModifySampleFields() {
		
		myPanel = new JPanel();
		myPanel.setLayout(new FlowLayout());
		choose = new JLabel("Select the fields to be shown for each sample:");
		
		CheckBoxList fieldCheckBox = new CheckBoxList();
		selectAll = new JButton("Uncheck All");
		selectAll.setSize(100, 20);
		header = pathway.getSamplesInfo().get("-1");
		options = pathway.getSamplesInfo().get("0");
		myList = new JCheckBox[header.size()];
		for (int i=0; i < header.size(); i ++){
			JCheckBox check = new JCheckBox(header.get(i));
			myList[i] = check;
		}
		for (int i = 0; i < options.size(); i ++){
			int index = header.indexOf(options.get(i));
			myList[index].setSelected(true);
		}
		fieldCheckBox.setListData(myList);
		checkBoxPane = new JScrollPane(fieldCheckBox);
		myPanel.add(choose);
		myPanel.add(checkBoxPane);
		myPanel.add(selectAll);
		
		selectAll.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent bc) {
				if (selectAll.getText().equals("Check All")){
					selectAll.setText("Uncheck All");
					for (int i = 0; i < options.size(); i ++){
						int index = header.indexOf(options.get(i));
						myList[index].setSelected(true);
					}
				}
				else {
					selectAll.setText("Check All");
					for (int i = 0; i < options.size(); i ++){
						int index = header.indexOf(options.get(i));
						myList[index].setSelected(false);
					}
				}
				
			}
		});
		
		int result = JOptionPane.showConfirmDialog(null, myPanel, "Please choose ...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
		if (result == JOptionPane.OK_OPTION) {
			List<String> selectedItems = new ArrayList<String>();
			for (JCheckBox chosen : myList){
				if (chosen.isSelected()){
					selectedItems.add(chosen.getText());
				}
			}
			
			if (selectedItems.size() > 0){
				pathway.setSamplesInfo("0",selectedItems);
			}
			else {
				
				pathway.setSamplesInfo("0",header);
			}
			ControlPanel.setSampleInfoLabel(pathway.getPointer());
			
		}
	}
}
