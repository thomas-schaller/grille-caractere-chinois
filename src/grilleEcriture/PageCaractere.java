package grilleEcriture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PageCaractere extends JFrame implements Printable {
  
  /**
   * Classe qui imprime un caractere avec sa grille ainsi que son pinYin sur une page.
   * 
   */
  CaractereDansGrille c;
  JLabel              pinYin;
  JLabel              titre;
  JLabel              traduction;
  static final float  TAILLE_CARACTERE_DEFAUT = 400;
  static final float  TAILLE_DEFAUT           = 50;
  static final float  TAILLE_TITRE            = 20;
  static final int    ESPACE_COMPOSANT        = 40;
  static final float  EPAISSEUR_TRAIT         = 2;
  
  public PageCaractere(String titre, String pinYin, String caractere,
	  String traduction, Font fontChoisie)
  {
	this(titre, pinYin, caractere, traduction, TAILLE_CARACTERE_DEFAUT,
	    TAILLE_DEFAUT, fontChoisie);
  }
  
  public PageCaractere(String titre, String pinYin, String caractere,
	  String traduction, float tailleCaractere, float taillePinYin,
	  Font fontChoisie)
  {
	super("Impression d'un Caractere sur une Page (Apercu)");
	PrinterJob pj = PrinterJob.getPrinterJob();
	this.setSize(new Dimension((int) pj.defaultPage().getWidth(), (int) pj
	    .defaultPage().getHeight()));
	
	c = new CaractereDansGrille(caractere, EPAISSEUR_TRAIT);
	c.setFont(fontChoisie);
	this.pinYin = new JLabel(pinYin);
	this.pinYin.setVerticalAlignment(JLabel.CENTER);
	this.pinYin.setHorizontalAlignment(JLabel.CENTER);
	this.titre = new JLabel(titre);
	this.titre.setVerticalAlignment(JLabel.CENTER);
	this.titre.setHorizontalAlignment(JLabel.CENTER);
	this.titre.setFont(this.titre.getFont().deriveFont(TAILLE_TITRE));
	this.traduction = new JLabel(traduction);
	this.traduction.setVerticalAlignment(JLabel.CENTER);
	this.traduction.setHorizontalAlignment(JLabel.CENTER);
	
	JPanel p = new JPanel();
	p.setOpaque(false);
	GroupLayout layout = new GroupLayout(p);
	p.setLayout(layout);
	SequentialGroup vGroup = layout.createSequentialGroup();
	ParallelGroup hGroup = layout
	    .createParallelGroup(GroupLayout.Alignment.CENTER);
	vGroup.addComponent(this.titre);
	vGroup.addGap(ESPACE_COMPOSANT / 4);
	vGroup.addComponent(this.pinYin);
	vGroup.addGap(ESPACE_COMPOSANT);
	vGroup.addComponent(c);
	vGroup.addGap(ESPACE_COMPOSANT);
	vGroup.addComponent(this.traduction);
	hGroup.addComponent(this.titre);
	hGroup.addComponent(this.pinYin);
	hGroup.addGap(getWidth());
	hGroup.addComponent(c);
	hGroup.addComponent(this.traduction);
	
	layout.setHorizontalGroup(hGroup);
	layout.setVerticalGroup(vGroup);
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	
	this.setTaillePoliceCaractere(tailleCaractere);
	this.setTaillePoliceDefaut(taillePinYin);
	this.c.setFont(this.c.getFont().deriveFont(Font.BOLD));
	
	this.getContentPane().add(p, BorderLayout.CENTER);
	this.setResizable(false);
	this.setVisible(true);
	
  }
  
  public void setCaractere(String text) {
	c.setText(text);
  }
  
  public void setPinYin(String text) {
	pinYin.setText(text);
  }
  
  public void setTaillePoliceCaractere(float taille) {
	Font f = c.getFont();
	c.setFont(f.deriveFont(taille));
  }
  
  public void setTaillePoliceDefaut(float taille) {
	Font f = c.getFont().deriveFont(taille);
	
	pinYin.setFont(f);
	traduction.setFont(f);
  }
  
  public int print(Graphics arg0, PageFormat arg1, int arg2)
	  throws PrinterException
  {
	Graphics2D g2 = (Graphics2D) arg0;
	// document is center on axis x.
	g2.translate(
	    arg1.getImageableX() + arg1.getImageableWidth() / 2 - this.getWidth()
	        / 2, arg1.getImageableY());
	if (arg2 > 0)
	  return NO_SUCH_PAGE;
	JPanel panel = (JPanel) ((BorderLayout) getContentPane().getLayout())
	    .getLayoutComponent(BorderLayout.CENTER);
	panel.printAll(arg0);
	return PAGE_EXISTS;
  }
  
}
