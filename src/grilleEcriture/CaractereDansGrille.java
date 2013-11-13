package grilleEcriture;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JLabel;

public class CaractereDansGrille extends JLabel {
  static final long         serialVersionUID = 315;
  BasicStroke               trait;
  public static final float DEFAUT_EPAISSEUR = 1;
  Color                     couleurGrille    = Color.LIGHT_GRAY;
  
  public CaractereDansGrille(String chaine, float epaisseur) {
	super(chaine);
	setHorizontalAlignment(JLabel.CENTER);
	setVerticalAlignment(JLabel.CENTER);
	trait = new BasicStroke(epaisseur);
  }
  
  public CaractereDansGrille(String chaine) {
	this(chaine, DEFAUT_EPAISSEUR);
	
  }
  
  @Override
  public void paint(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	g.setColor(couleurGrille);
	g2.setStroke(trait);
	/*Rectangle r = getFont().getStringBounds(this.getText(),
	    g2.getFontRenderContext()).getBounds(); // test pour vérifier si le texte ne dépasse pas les limites du dessin
	if (getWidth() >= r.width && getHeight() >= r.height) {*/
	g.drawLine(getWidth() / 2, 0, getWidth() / 2,
	    getHeight() - (int) trait.getLineWidth());
	g.drawLine(0, getHeight() / 2, getWidth() - (int) trait.getLineWidth(),
	    getHeight() / 2);
	g.drawRect(0, 0, getWidth() - (int) trait.getLineWidth(), getHeight()
	    - (int) trait.getLineWidth());
	//}
	super.paint(g);
  }
  
  /* (non-Javadoc)
   * @see java.awt.Component#setSize(java.awt.Dimension)
   */
  @Override
  public void setSize(Dimension arg0) {
	this.setSize(arg0.width, arg0.height);
  }
  
  /* (non-Javadoc)
   * @see java.awt.Component#setSize(int, int)
   */
  @Override
  public void setSize(int width, int height) {
	int taille = Math.min(width, height);
	super.setSize(taille, taille);
  }
  
  /* (non-Javadoc)
   * @see java.awt.Component#setBounds(int, int, int, int)
   */
  @Override
  public void setBounds(int x, int y, int width, int height) {
	int taille = Math.min(width, height);
	super.setBounds(x, y, taille, taille);
  }
  
  /* (non-Javadoc)
   * @see java.awt.Component#setBounds(java.awt.Rectangle)
   */
  @Override
  public void setBounds(Rectangle r) {
	this.setBounds(r.x, r.y, r.width, r.height);
  }
  
  /* (non-Javadoc)
   * @see javax.swing.JComponent#getMaximumSize()
   */
  @Override
  public Dimension getMaximumSize() {
	Dimension d = super.getMaximumSize();
	int t = Math.min(d.width, d.height);
	return new Dimension(t, t);
  }
  
  /* (non-Javadoc)
   * @see javax.swing.JComponent#getMinimumSize()
   */
  @Override
  public Dimension getMinimumSize() {
	Dimension d = super.getMinimumSize();
	int t = Math.max(d.width, d.height);
	return new Dimension(t, t);
  }
  
  /* (non-Javadoc)
   * @see javax.swing.JComponent#getPreferredSize()
   */
  @Override
  public Dimension getPreferredSize() {
	Dimension d = super.getPreferredSize();
	int t = Math.max(d.width, d.height);
	return new Dimension(t, t);
  }
}
