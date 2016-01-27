package piilSource;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;


public class Splash extends JWindow {
	
	private int duration;
	JFrame splashFrame;

	public Splash(int d) {
	    duration = d;
	    showSplashAndExit();
	}

	public void showSplash() {
		
		ImageIcon splashImage = new ImageIcon(getClass().getResource("/resources/splash-Piil.png"));
		ImagePanel panel = new ImagePanel(splashImage.getImage());
		
		JPanel content = (JPanel) getContentPane();
		
		int width = 450;
		int height = 300;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width-width)/2;
		int y = (screen.height-height)/2;
		setBounds(x,y,width,height);
		content.add(panel, BorderLayout.CENTER);
		
	    setVisible(true);

	    try { Thread.sleep(duration); } catch (Exception e) {}

	    setVisible(false);
	}
	public void showSplashAndExit() {
	    showSplash();

	}
	
}
