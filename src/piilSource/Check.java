package piilSource;

import java.awt.Image;
import javax.swing.*;

public class Check {
    JFrame frame;
    public static void main(String[] args) {
        new Check().go();
    }
    private void go() {
        frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Image image = new ImageIcon("icon.png").getImage();
        System.out.println(image);
        frame.setIconImage(image);
        frame.setVisible(true);
        frame.setSize(300, 300);
    }
}