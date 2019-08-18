package grilleEcriture;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Test {
  
  /**
   * @param args
   */
  public static void main(String[] args) {
	JFrame pp = new JFrame("test");
	pp.setBackground(Color.GREEN);
	JLabel test = new JLabel("test");
	test.setBackground(Color.BLUE);
	test.setOpaque(true);
	pp.add(test);
	pp.pack();
	pp.setVisible(true);
  }
  
}
