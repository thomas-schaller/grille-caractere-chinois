package grilleEcriture;

import javax.swing.*;
import java.awt.*;

public class CaractereDansGrille extends JLabel {

    static final long serialVersionUID = 315;
    BasicStroke trait;
    public static final float DEFAUT_EPAISSEUR = 1;
    Color couleurGrille = Color.LIGHT_GRAY;

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
        g.drawLine(getWidth() / 2, 0, getWidth() / 2,
                getHeight() - (int) trait.getLineWidth());
        g.drawLine(0, getHeight() / 2, getWidth() - (int) trait.getLineWidth(),
                getHeight() / 2);
        g.drawRect(0, 0, getWidth() - (int) trait.getLineWidth(), getHeight()
                - (int) trait.getLineWidth());
        super.paint(g);
    }

    @Override
    public void setSize(Dimension arg0) {
        this.setSize(arg0.width, arg0.height);
    }

    @Override
    public void setSize(int width, int height) {
        int taille = Math.min(width, height);
        super.setSize(taille, taille);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        int taille = Math.min(width, height);
        super.setBounds(x, y, taille, taille);
    }

    @Override
    public void setBounds(Rectangle r) {
        this.setBounds(r.x, r.y, r.width, r.height);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension d = super.getMaximumSize();
        int t = Math.min(d.width, d.height);
        return new Dimension(t, t);
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension d = super.getMinimumSize();
        int t = Math.max(d.width, d.height);
        return new Dimension(t, t);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int t = Math.max(d.width, d.height);
        return new Dimension(t, t);
    }
}
