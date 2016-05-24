package piilSource;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JComponent;

import javax.swing.BoundedRangeModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

public class ColorCodeManager extends JDialog{
	
	JFrame colorFrame;
	JPanel colorPanel, containerPanel;
	RangeSlider ranger;
	
	
	
	public ColorCodeManager(Character metaType){
		ranger = new RangeSlider();		
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		
		if (metaType.equals('M')){
			ranger.setMinimum(0);
			ranger.setMaximum(10);
			ranger.setValue(0);
			ranger.setUpperValue(10);
			
			labelTable.put( new Integer( 0 ), new JLabel("0.0") );
			labelTable.put( new Integer( 5 ), new JLabel("0.5") );
			labelTable.put( new Integer( 10 ), new JLabel("1.0") );
		}
		else if (metaType.equals('E')){
			ranger.setMinimum(0);
			ranger.setMaximum(10);
			ranger.setValue(0);
			ranger.setUpperValue(10);
			labelTable.put( new Integer( 0 ), new JLabel("-5") );
			labelTable.put( new Integer( 5 ), new JLabel("0") );
			labelTable.put( new Integer( 10 ), new JLabel("5") );
		}
		
		ranger.setLabelTable(labelTable);
		ranger.setPaintLabels(true);
		ranger.setMinorTickSpacing(1);
		ranger.setMajorTickSpacing(1);
		ranger.setPaintTicks(true);
		
		colorPanel = new JPanel();
		colorPanel.setLayout(new GridBagLayout());
		
		colorPanel.add(ranger);
		containerPanel= new JPanel();
		containerPanel.setLayout(new BorderLayout());
		containerPanel.add(ranger, BorderLayout.CENTER);
		
		int result = JOptionPane.showConfirmDialog(null, containerPanel, "Please choose ...", JOptionPane.OK_CANCEL_OPTION);
		
		if (result == JOptionPane.OK_OPTION){
			
			System.out.println(ranger.getUpperValue() );
			System.out.println(ranger.getValue());
		}
		
	}

}
