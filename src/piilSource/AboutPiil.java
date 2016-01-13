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

