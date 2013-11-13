package ihm;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

/**
 * @author Thomas SCHALLER (th.schaller@gmail.com)
 * @since 13 nov. 2013
 */
public class JResizableTextLabel extends JLabel {
  
  /**
   *  Première version
   */
  
  private static final long serialVersionUID = -6596224457017495137L;
  private static final int  SIZE             = 256;
  private BufferedImage     image;
  
  public JResizableTextLabel(String text) {
	super(text);
	
  }
  
  @Override
  public Dimension getPreferredSize() {
	return new Dimension(image.getWidth() / 2, image.getHeight() / 2);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
  }
  
  private BufferedImage createImage(String label) {
	Font font = getFont().deriveFont(SIZE);
	FontRenderContext frc = new FontRenderContext(null, true, true);
	TextLayout layout = new TextLayout(label, font, frc);
	Rectangle r = layout.getPixelBounds(null, 0, 0);
	System.out.println(r);
	BufferedImage bi = new BufferedImage(r.width + 1, r.height + 1,
	    BufferedImage.TYPE_INT_RGB);
	Graphics2D g2d = (Graphics2D) bi.getGraphics();
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	    RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setColor(getBackground());
	g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
	g2d.setColor(getForeground());
	layout.draw(g2d, 0, -r.y);
	g2d.dispose();
	return bi;
  }
}
