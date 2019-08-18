package grilleEcriture;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JFrame;

public class TestFleche extends JFrame implements MouseListener {
  Vector<Fleche> f = new Vector<Fleche>();
  
  public TestFleche() {
	super("test du dessin d'une fleche");
	f.add(new Fleche());
	this.addMouseListener(this);
	
  }
  
  public void mouseClicked(MouseEvent e) {
	
  }
  
  public void mouseEntered(MouseEvent e) {
	
  }
  
  public void mouseExited(MouseEvent e) {
	
  }
  
  public void mousePressed(MouseEvent e) {
	switch (e.getButton()) {
	case MouseEvent.BUTTON1:
	  f.get(f.size() - 1).ajouterPoint(e.getPoint());
	  this.repaint();
	  break;
	case MouseEvent.BUTTON2:
	  if (f.get(f.size() - 1).getNbPoints() > 1)
		f.add(new Fleche());
	  break;
	case MouseEvent.BUTTON3:
	  f.get(f.size() - 1).effacerPoint();
	  this.repaint();
	  break;
	}
	
  }
  
  public void paint(Graphics g) {
	super.paint(g);
	for (int i = 0; i < f.size(); i++) {
	  f.get(i).paintComponent(g);
	}
  }
  
  public void mouseReleased(MouseEvent e) {
	
  }
  
  public static void main(String[] args) {
	TestFleche tf = new TestFleche();
	tf.setSize(500, 500);
	tf.setVisible(true);
  }
  
}
