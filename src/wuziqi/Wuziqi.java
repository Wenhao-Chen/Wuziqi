package wuziqi;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Wuziqi {

	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		
		WuziqiPanel panel = new WuziqiPanel(15);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Wuziqi");
		frame.setLocation(600, 150);
		frame.setSize(600, 700);
		frame.setMinimumSize(new Dimension(400 ,500));
		frame.add(panel);
		frame.setVisible(true);
		
	}
	
}
