package grilleEcriture;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Vector;

public class Fleche {
  Vector<Point> positions = new Vector<Point>();
  Color         couleur   = Color.BLACK;
  Arrow         a         = new Arrow(Arrow.CLASSIC, 10, 10);
  
  public Fleche() {
  }
  
  public int getNbPoints() {
	return positions.size();
  }
  
  public void paintComponent(Graphics g) {
	// on dessine la liaison entre les points de la flèche
	for (int i = 1; i < positions.size(); i++) {
	  g.drawLine(positions.get(i - 1).x, positions.get(i - 1).y, positions
		  .get(i).x, positions.get(i).y);
	  
	}
	//on dessine la pointe de la flèche
	if (positions.size() > 1) {
	  //	  g.drawRect(positions.get(positions.size() - 1).x, positions.get(positions.size() - 1).y, 5, 5);
	  a.drawArrow((Graphics2D) g, positions.get(positions.size() - 2).x,
		  positions.get(positions.size() - 2).y, positions
		      .get(positions.size() - 1).x,
		  positions.get(positions.size() - 1).y, 0);
	}
	if (positions.size() == 1) {
	  g.drawRect(positions.get(0).x, positions.get(0).y, 5, 5);
	}
  }
  
  public void ajouterPoint(Point p) {
	positions.add(p);
  }
  
  public void effacerPoint() {
	if (getNbPoints() > 0)
	  positions.remove(getNbPoints() - 1);
  }
}
