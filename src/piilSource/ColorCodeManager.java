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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JComponent;

import javax.swing.BoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorCodeManager extends JDialog{
	
	JPanel colorPanel, containerPanel;
	RangeSlider ranger;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/logoIcon.png"));
	TabsInfo theTab = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
	Character metaType = theTab.getMetaType();
	float[] ranges = theTab.getRanges();
	SpinnerModel spinRange = new SpinnerNumberModel(ranges[2], 1.0, 10.0, 0.1);
	JSpinner foldSpinner;
	JLabel methylationLabel, expressionLabel;
	
	public ColorCodeManager(){
		ranger = new RangeSlider();		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		foldSpinner = new JSpinner(spinRange);
		foldSpinner.setPreferredSize(new Dimension(60, 25));
		methylationLabel = new JLabel("DNA methylation range:");
		expressionLabel = new JLabel("Expression fold change:");
		
		ranger.setMinimum(0);
		ranger.setMaximum(10);
		ranger.setValue(Math.round(ranges[0]));
		ranger.setUpperValue(Math.round(ranges[1]));
		
		labelTable.put( new Integer( 0 ), new JLabel("0.0") );
		labelTable.put( new Integer( 5 ), new JLabel("0.5") );
		labelTable.put( new Integer( 10 ), new JLabel("1.0") );
		
		ranger.setLabelTable(labelTable);
		ranger.setPaintLabels(true);
		ranger.setMinorTickSpacing(1);
		ranger.setMajorTickSpacing(1);
		ranger.setPaintTicks(true);
		
		ranger.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                RangeSlider slider = (RangeSlider) e.getSource();
                if (metaType.equals('M')){
                	
                	if (ranger.getValue() == ranger.getUpperValue()){
                		resetRanger();
                		
                	}
                }
            }
        });
		
		colorPanel = new JPanel();
		colorPanel.setLayout(new GridBagLayout());
		
		addComp(colorPanel, methylationLabel, 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(colorPanel, ranger, 1, 0, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(colorPanel, expressionLabel, 0, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(colorPanel, foldSpinner, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		
		containerPanel= new JPanel();
		containerPanel.setLayout(new BorderLayout());
		containerPanel.add(colorPanel, BorderLayout.CENTER);
		
		int result = JOptionPane.showConfirmDialog(null, containerPanel, "Please choose ...", JOptionPane.OK_CANCEL_OPTION,0,icon);
		
		if (result == JOptionPane.OK_OPTION){
			theTab.setRanges(ranger.getValue(), ranger.getUpperValue(), Float.parseFloat(foldSpinner.getValue().toString()));
			
			Genes.changeBgColor(theTab.getPointer(), metaType);
		}
		
	}

	protected void resetRanger() {
		if (ranger.getValue() >= 5){
			ranger.setValue(4);
			ranger.setUpperValue(6);
		}
		else {
			ranger.setUpperValue(6);
			ranger.setValue(4);
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
		gridConstraints.insets = new Insets(5,5,5,5);
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);
		
	}

}
