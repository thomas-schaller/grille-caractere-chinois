package grilleEcriture;


import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Contient la feuille d'ecriture des caractères:gère son affichage et son impression
 *
 * @author Thomas SCHALLER (th.schaller@gmail.com)
 * @since 29 janv. 2012
 */
public class GrilleEcriture extends JPanel implements Printable {
    final static long serialVersionUID = 212;
    final int TAILLE_TITRE = 15;
    int nbCaracteres = 6;
    int nbEssais = 3;
    int nbARecopier = 5;
    JLabel jlTitre = new JLabel("Titre du document");
    JLabel jlTraduction[];
    JLabel jlPinYin[];
    JLabel jlCaractere[];
    JLabel jlClefSemantique[];
    int taillePoliceGrand = 30;
    int taillePolicePetit = 20;
    public JPanel jpGrille[];
    static final int ESPACE_TITRE = 10;
    Font fontChoisie;
    Color couleurEssai;

    public GrilleEcriture(String titre, int nbCaracteres, int nbARecopier,
                          int nbEssais, Font fontChoisie, Color couleurEssaiChoisi) {
        if (nbCaracteres * (nbEssais + 1) > 20)
            taillePolicePetit = 20;
        this.nbCaracteres = nbCaracteres;
        this.nbEssais = nbEssais;
        this.nbARecopier = nbARecopier;
        this.fontChoisie = fontChoisie;
        this.couleurEssai = couleurEssaiChoisi;
        jlTitre.setText(titre);
        jlTitre.setFont(new Font("Times", Font.BOLD, TAILLE_TITRE));
        jlTitre.setHorizontalAlignment(JLabel.CENTER);

        jpGrille = new JPanel[nbCaracteres];
        jlTraduction = new JLabel[nbCaracteres];
        jlPinYin = new JLabel[nbCaracteres];
        jlCaractere = new JLabel[nbCaracteres];
        jlClefSemantique = new JLabel[nbCaracteres];

        GroupLayout glMain = new GroupLayout(this);
        setLayout(glMain);
        glMain.setAutoCreateGaps(true);
        glMain.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup hGroup = glMain
                .createParallelGroup(GroupLayout.Alignment.CENTER);
        GroupLayout.SequentialGroup detailCaracteres = glMain
                .createSequentialGroup();
        GroupLayout.SequentialGroup vGroup = glMain.createSequentialGroup();

        GroupLayout.ParallelGroup explication = glMain
                .createParallelGroup(GroupLayout.Alignment.CENTER);
        GroupLayout.ParallelGroup traduc = glMain.createParallelGroup();
        GroupLayout.ParallelGroup grille = glMain.createParallelGroup();

        hGroup.addComponent(jlTitre);
        hGroup.addGroup(detailCaracteres);

        vGroup.addComponent(jlTitre);
        vGroup.addGap(ESPACE_TITRE);
        for (int i = 0; i < nbCaracteres; i++) {
            JSeparator jsSepare = new JSeparator(JSeparator.HORIZONTAL);
            jlCaractere[i] = new JLabel();
            jlCaractere[i].setFont(fontChoisie);
            jlCaractere[i].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            jlClefSemantique[i] = new JLabel("Clef Semantique");
            jlClefSemantique[i]
                    .setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            jlTraduction[i] = new JLabel("Traduction");
            jlTraduction[i].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            jlPinYin[i] = new JLabel("PinYin");
            jlPinYin[i].setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            jpGrille[i] = new JPanel(new GridLayout(nbEssais + 1, nbARecopier));
            jpGrille[i].setOpaque(false);
            remplirGrille(i, jlCaractere[i].getText());

            jlCaractere[i].setFont(jlCaractere[i].getFont().deriveFont(
                    (float) taillePoliceGrand));
            jlClefSemantique[i].setFont(jlClefSemantique[i].getFont().deriveFont(
                    Font.ITALIC));

            GroupLayout.ParallelGroup ligne = glMain
                    .createParallelGroup(GroupLayout.Alignment.CENTER);
            ligne.addGroup(glMain
                    .createSequentialGroup()
                    .addComponent(jlPinYin[i])
                    .addGroup(
                            glMain.createParallelGroup(GroupLayout.Alignment.CENTER)
                                    .addComponent(jlCaractere[i])
                                    .addComponent(jlClefSemantique[i])));
            ligne.addComponent(jlTraduction[i]);
            ligne.addComponent(jpGrille[i]);
            vGroup.addComponent(jsSepare);
            vGroup.addGroup(ligne);
            explication.addComponent(jlPinYin[i]).addGroup(
                    glMain.createSequentialGroup().addComponent(jlCaractere[i])
                            .addComponent(jlClefSemantique[i]));
            traduc.addComponent(jlTraduction[i]);
            grille.addComponent(jpGrille[i]);
            hGroup.addComponent(jsSepare);

        }
        glMain.linkSize(jlCaractere);
        glMain.linkSize(jpGrille);
        detailCaracteres.addGroup(explication).addGroup(traduc).addGroup(grille);
        glMain.setHorizontalGroup(hGroup);
        glMain.setVerticalGroup(vGroup);
        this.setOpaque(false);
    }

    public void setTailleGrandePolice(int t) {
        taillePoliceGrand = t;
        for (int i = 0; i < jlCaractere.length; i++)
            jlCaractere[i].setFont(jlCaractere[i].getFont().deriveFont((float) t));
    }

    public void setTaillePetitePolice(int t) {
        taillePolicePetit = t;
        for (int i = 0; i < jlCaractere.length; i++)
            remplirGrille(i, jlCaractere[i].getText());
    }

    public void setLigne(int position, String caractere, String traduction,
                         String pinYin, String clefSemantique) {
        jlCaractere[position].setText(caractere);
        jpGrille[position].removeAll();
        remplirGrille(position, caractere);
        jlTraduction[position].setText(traduction);
        jlPinYin[position].setText(pinYin);
        jlClefSemantique[position].setText(clefSemantique);

    }

    private void remplirGrille(int position, String s) {

        //Build the first line composing by a empty character.
        for (int i = 0; i < nbARecopier * nbEssais; i++) {
            CaractereDansGrille c = new CaractereDansGrille(" ");
            c.setFont(fontChoisie);
            jpGrille[position].add(c);
            c.setFont(c.getFont().deriveFont((float) taillePolicePetit));
        }
        //Build the first line composing by the character to copy.
        for (int i = 0; i < nbARecopier; i++) {
            CaractereDansGrille c = new CaractereDansGrille(s);
            c.setFont(fontChoisie);
            c.setForeground(couleurEssai);
            jpGrille[position].add(c);
            c.setFont(c.getFont().deriveFont((float) taillePolicePetit));
        }

    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        Graphics2D g2 = (Graphics2D) graphics;
        // document is center on axis x.
        g2.translate(pageFormat.getImageableX() + pageFormat.getImageableWidth()
                / 2 - this.getWidth() / 2, pageFormat.getImageableY());
        if (pageIndex > 0)
            return NO_SUCH_PAGE;
        printAll(graphics);
        graphics.setColor(Color.BLACK);
	/*
	int X = (int) pageFormat.getImageableX(), Y = (int) pageFormat
	    .getImageableY();
	int W = (int) pageFormat.getImageableWidth(), H = (int) pageFormat
	    .getImageableHeight();
	   graphics.drawRect(X + 1, Y + 1, W - 2, H - 2);
	    */
        return PAGE_EXISTS;

    }
}