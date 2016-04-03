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
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class AboutPiil extends JDialog{
	
	JFrame aboutFrame;
	
	public AboutPiil(){
		
		ImageIcon aboutImage = new ImageIcon(getClass().getResource("/resources/aboutPiil.png"));
		ImagePanel panel = new ImagePanel(aboutImage.getImage());
		aboutFrame = new JFrame();
		aboutFrame.setSize(440, 290);
		aboutFrame.setResizable(false);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (aboutFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (aboutFrame.getHeight() / 2);
		aboutFrame.setLocation(xPos,yPos);
		aboutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		aboutFrame.setTitle("About PiiL");
		aboutFrame.add(panel);
		aboutFrame.pack();
		aboutFrame.setVisible(true);
		
	}
}

