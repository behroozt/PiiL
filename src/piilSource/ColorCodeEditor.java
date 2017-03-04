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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorCodeEditor extends JDialog{

	JLabel hypoText, hyperText, iconLabel;
	JButton okButton, cancelButton, hyperButton, hypoButton;
	JFrame holderFrame;
	JPanel myPanel, iconPanel, buttonsPanel, rightPanel;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	GridBagConstraints gridConstraints = new GridBagConstraints();
	
	public ColorCodeEditor(){
		myPanel = new JPanel();
		myPanel.setLayout(new GridBagLayout());
		okButton = new JButton("Save");
		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(new Dimension(80,33));
		okButton.setPreferredSize(new Dimension(80,33));
		holderFrame = new JFrame();
		holderFrame.setSize(450, 200);
		iconPanel = new JPanel();
		iconLabel = new JLabel(icon);
		iconPanel.add(iconLabel);
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		hypoButton = new JButton();
		hyperButton = new JButton();
		hypoButton.setPreferredSize(new Dimension(35, 35));
		hyperButton.setPreferredSize(new Dimension(35, 35));
		hyperButton.setBorderPainted(true);
		hypoButton.setBorderPainted(true);
		hypoText = new JLabel("hypo-methylated / over-expressed color:");
		hyperText = new JLabel("hyper-methylated / under-expressed color:");
		hyperButton.setOpaque(true);
		hypoButton.setOpaque(true);
		hypoButton.setBorderPainted(false);
		hyperButton.setBorderPainted(false);
		hypoButton.setBackground(Interface.getHypoColor());
		hyperButton.setBackground(Interface.getHyperColor());
		iconPanel.setPreferredSize(new Dimension(100, 200));
		rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(350, 200));
		rightPanel.setLayout(new FlowLayout());
		iconPanel.setLayout(new FlowLayout());
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (holderFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (holderFrame.getHeight() / 2);
		holderFrame.setLocation(xPos,yPos);
		holderFrame.setResizable(false);
		holderFrame.setLayout(new BorderLayout());
		holderFrame.setTitle("Edit Colors");
		
		gridConstraints.insets = new Insets(10,4,4,4);
		addComp(myPanel, hypoText, 0, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, hypoButton, 2, 1, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		gridConstraints.insets = new Insets(4,4,4,4);
		addComp(myPanel, hyperText, 0, 2, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, hyperButton, 2, 2, 2, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		holderFrame.add(iconPanel, BorderLayout.WEST);
		holderFrame.add(rightPanel, BorderLayout.EAST);
		rightPanel.add(myPanel);
		rightPanel.add(buttonsPanel);
		holderFrame.setVisible(true);
		
		hypoButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(null, "Choose a Color", Interface.getHypoColor());
				hypoButton.setBackground(c);
			}
		});
		hyperButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(null, "Choose a Color", Interface.getHyperColor());
				hyperButton.setBackground(c);
			}
		});
		
		okButton.addActionListener(new ActionListener() {
				
			public void actionPerformed(ActionEvent e) {
				Interface.setHyperColor(hyperButton.getBackground());
				Interface.setHypoColor(hypoButton.getBackground());
				holderFrame.dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				holderFrame.dispose();
			}
		});
	}
	
	private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch){
		
		gridConstraints.gridx = xPos;
		gridConstraints.gridy = yPos;
		gridConstraints.gridwidth = compWidth;
		gridConstraints.gridheight = compHeight;
		gridConstraints.weightx = 1;
		gridConstraints.weighty = 1;
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);
		
	}
	
}
