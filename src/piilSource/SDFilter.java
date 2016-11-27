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
import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class SDFilter extends JDialog{

	JPanel filterPanel;
	JLabel filterLabel;
	JTextField sdValue;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	TabsInfo activeTab;
	JDialog dialog = new JDialog(Interface.bodyFrame, "Analyzing data",ModalityType.APPLICATION_MODAL);
	JButton applyButton = new JButton("Apply");
	
	public SDFilter(){
		activeTab  = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex());
		filterPanel = new JPanel();
		filterPanel.setLayout(new FlowLayout());
		filterLabel = new JLabel("Exclude CpG sites with standard deviation (over all samples) lower than ");
		sdValue = new JTextField(String.valueOf(activeTab.getSDThreshold()));
		sdValue.setPreferredSize(new Dimension(50,20));
		filterPanel.add(filterLabel);
		filterPanel.add(sdValue);
		filterPanel.add(applyButton);
		applyButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				float sdThreshold = 0;
				if (sdValue.getText() == "" | Float.parseFloat(sdValue.getText()) < 0){
					JOptionPane.showMessageDialog(Interface.bodyFrame, "Please select a positive real number.","Error",0,icon);
					
				}
				else {
					sdThreshold = Float.parseFloat(sdValue.getText());
					if (sdThreshold != activeTab.getSDThreshold()){
						activeTab.setSDThreshold(sdThreshold);
						
						SwingWorker<Void, Void> analyzeWorker = new SwingWorker<Void, Void>() {
							protected Void doInBackground() {
								Genes.setSignificantSites();
								if (activeTab.getViewMode() == 1){
									Genes.changeBgColor(activeTab.getPointer(), 'M');
								}
								else {
//									PiilMenubar.singleSampleView.doClick();
//									Genes.changeBgColor(activeTab.getPointer(), 'M');
//									activeTab.setViewMode((byte) 2);
								}
								return null;
							}

							protected void done() {
								dialog.dispose();
							}
						};
						analyzeWorker.execute();
						dialog.setVisible(true);
					}
				}
			}
		});
		
		dialog.setUndecorated(true);
		JLabel waitMessage = new JLabel("Analyzing CpG sites for all matched genes ... ");
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar, BorderLayout.CENTER);
		panel.add(waitMessage, BorderLayout.PAGE_START);
		dialog.add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(Interface.bodyFrame);
		
		Object[] buttons = {"Apply and Close", "Close"};
		int result = JOptionPane.showOptionDialog(null, filterPanel, "Filtering CpG sites ...", JOptionPane.OK_CANCEL_OPTION,0,icon, buttons, buttons[0]);
		if (result == JOptionPane.OK_OPTION){
			float sdThreshold = 0;
			if (sdValue.getText() == "" | Float.parseFloat(sdValue.getText()) < 0){
				JOptionPane.showMessageDialog(Interface.bodyFrame, "Please select a positive real number.","Error",0,icon);
				
			}
			else {
				sdThreshold = Float.parseFloat(sdValue.getText());
				if (sdThreshold != activeTab.getSDThreshold()){
					activeTab.setSDThreshold(sdThreshold);
					
					SwingWorker<Void, Void> analyzeWorker = new SwingWorker<Void, Void>() {
						protected Void doInBackground() {
							Genes.setSignificantSites();
							Genes.changeBgColor(activeTab.getPointer(), 'M');
							return null;
						}

						protected void done() {
							dialog.dispose();
						}
					};
					analyzeWorker.execute();
					dialog.setVisible(true);
				}
			}
			
		}
	}
}
