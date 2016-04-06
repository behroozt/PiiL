package piilSource;


import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ModifySampleFields{
	
	JPanel myPanel;
	JLabel choose;
	TabsInfo pathway = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
	
	public ModifySampleFields() {
		
		myPanel = new JPanel();
		myPanel.setLayout(new FlowLayout());
		choose = new JLabel("Select the fields to be shown for each sample:");
		
		CheckBoxList fieldCheckBox = new CheckBoxList();
		
		List<String> header = pathway.getSamplesInfo().get("-1");
		List<String> options = pathway.getSamplesInfo().get("0");
		JCheckBox[] myList = new JCheckBox[header.size()];
		for (int i=0; i < header.size(); i ++){
			JCheckBox check = new JCheckBox(header.get(i));
			myList[i] = check;
		}
		for (int i = 0; i < options.size(); i ++){
			int index = header.indexOf(options.get(i));
			myList[index].setSelected(true);
		}
		fieldCheckBox.setListData(myList);
		myPanel.add(choose);
		myPanel.add(fieldCheckBox);
		
		int result = JOptionPane.showConfirmDialog(null, myPanel, "Please choose ...", JOptionPane.OK_CANCEL_OPTION);
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
