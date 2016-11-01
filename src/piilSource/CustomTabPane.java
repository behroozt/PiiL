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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.metal.MetalIconFactory;

public class CustomTabPane extends JTabbedPane{
	
	final ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/icon.png"));
	
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, component, tip);
		int count = this.getTabCount() - 1;
		setTabComponentAt(count, new CloseButtonTab(component, title, icon));
		PiilMenubar.manageColors.setEnabled(true);
	}

	@Override
	public void addTab(String title, Icon icon, Component component) {
		addTab(title, icon, component, null);
	}

	@Override
	public void addTab(String title, Component component) {
		addTab(title, null, component);
	}

	public class CloseButtonTab extends JPanel {
		private Component tab;

		public CloseButtonTab(final Component tab, String title, Icon icon) {
			
			this.tab = tab;
			setOpaque(false);
			FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 3, 3);
			setLayout(flowLayout);
			setVisible(true);

			JLabel jLabel = new JLabel(title);
			jLabel.setIcon(icon);
			add(jLabel);
			JButton button = new JButton(MetalIconFactory.getInternalFrameCloseIcon(16));
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
			button.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					int dialogResult = JOptionPane.showConfirmDialog(Interface.bodyFrame, "Are you sure you want to close this tab?", "Action confirmation", 0, JOptionPane.WARNING_MESSAGE,logoIcon);
					if (dialogResult == JOptionPane.YES_OPTION) {
						JTabbedPane tabbedPane = (JTabbedPane) getParent().getParent();
						int tabIndex = tabbedPane.indexOfComponent(tab);
						
						Interface.removeSelectedPanel(tabIndex);
						Interface.removeSelectedScrollPane(tabIndex);
						ParseKGML.closedTab(tabIndex);
						tabbedPane.remove(tab);
						if (Interface.tabPane.getTabCount() == 0){
							PiilMenubar.manageColors.setEnabled(false);
						}
					}
					
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
					JButton button = (JButton) e.getSource();
					button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
				}

				public void mouseExited(MouseEvent e) {
					JButton button = (JButton) e.getSource();
					button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
				}
			});
			add(button);
		}
		
		public CloseButtonTab(final Component tab, String title) {
			this.tab = tab;
			setOpaque(false);
			FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 3, 3);
			setLayout(flowLayout);
			setVisible(true);

			JLabel jLabel = new JLabel(title);
			add(jLabel);
			
		}
	}
}
