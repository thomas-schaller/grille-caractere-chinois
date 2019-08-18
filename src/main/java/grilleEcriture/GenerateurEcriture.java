package grilleEcriture;

import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.View;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.IntrospectionException;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;

public class GenerateurEcriture extends SingleFrameApplication {

    // classe demandant à l'utilisateur les informations pour créer la grille d'écriture
    final static String propertiesFile = "properties.xml";
    int nbCaracteres;
    int nbEssais;
    int nbARecopier;
    int maxCaracteres = 7;
    String fontChoisie = null;
    JTextField jtfTitre;
    LigneEditable[] lignes;
    PageProprietes jfProprietes = null;
    JFrame apercuImpression = null;
    GrilleFileFilter filtre;
    private ResourceMap appResourceMap;
    JPanel jpLignesModifiables = new JPanel(new GridBagLayout());
    Color couleurEssaiChoisi = Color.BLACK;
    JScrollPane affichageGrilleEcriture = null;
    GrilleEcriture feuilleEcriture = null;

    //initialise les valeurs par défaut par celles contenu dans le fichier de ressources
    @Override
    protected void initialize(String[] args) {
        appResourceMap = getContext().getResourceMap();
        maxCaracteres = appResourceMap.getInteger("nbMaxCaracteres").intValue();
        nbCaracteres = appResourceMap.getInteger("nbCaracteres").intValue();
        if (nbCaracteres > maxCaracteres)
            nbCaracteres = maxCaracteres;
        nbEssais = appResourceMap.getInteger("nbEssais").intValue();
        nbARecopier = appResourceMap.getInteger("nbARecopier").intValue();
        fontChoisie = appResourceMap.getString("policeCaractereChoisie");

        filtre = new GrilleFileFilter(appResourceMap.getString("descripteurFiltre"));
    }

    @Override
    protected void startup() {
        // on récupère les propriétés sauvegardées
        LinkedHashMap lhm = null;
        try {
            Object o = getContext().getLocalStorage().load(propertiesFile);
            if (o instanceof LinkedHashMap)
                lhm = (LinkedHashMap) getContext().getLocalStorage().load(
                        propertiesFile);
        } catch (IOException e) {
            System.err.println(e);
        }
        if (lhm != null) {
            if (lhm.get("nbMaxCaracteres") != null)
                maxCaracteres = ((Integer) lhm.get("nbMaxCaracteres")).intValue();
            nbCaracteres = ((Integer) lhm.get("nbCaracteres")).intValue();
            if (nbCaracteres > maxCaracteres)
                nbCaracteres = maxCaracteres;
            nbEssais = ((Integer) lhm.get("nbEssais")).intValue();
            nbARecopier = ((Integer) lhm.get("nbARecopier")).intValue();
            fontChoisie = (String) lhm.get("caracterePoliceChoisie");
            couleurEssaiChoisi = (Color) lhm.get("couleurEssaiChoisi");
        }

        View v = this.getMainView();
        v.setComponent(createMainFrame());
        v.setMenuBar(createJMenuBar());
        show(v);

	/*
	try
	{
	PropertyDescriptor pd [] = Introspector.getBeanInfo(JTextField.class)
		.getPropertyDescriptors();
	for (int i=0;i<pd.length;i++)
	System.out.println(pd[i].getName());
	}
	catch(Exception e)
	{
	System.err.println(e);
	System.exit(1);
	}
	*/

    }

    //fonction permettant de récupérer une action d'arpès son nom
    private javax.swing.Action getAction(String actionName) {
        return getContext().getActionMap().get(actionName);
    }

    @org.jdesktop.application.Action
    //fonction qui enregistre en image le graphisme d'un panel
    public void sauverImage(ActionEvent ae) throws FileNotFoundException,
            IOException {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilterExtension("Fichier Image jpg", "jpg"));
        JRootPane frame = ((JButton) ae.getSource()).getRootPane();
        if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            JPanel panel = feuilleEcriture;
            // on récupère le panneau qui doit être enregistre: celui ci se trouvre au centre dans le borderLayout du contentPane du JRoootPane
            //enregistre dans un fichier image le JPanel contenant l'objet ayant lancé l'action
            BufferedImage tamponSauvegarde = new BufferedImage(panel.getSize().width,
                    panel.getSize().height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tamponSauvegarde.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, panel.getSize().width, panel.getSize().height);
            panel.print(g);
            File f = fc.getSelectedFile().getAbsoluteFile();
            if (fc.getSelectedFile().getName().indexOf(".") == -1)
                f = new File(f.getAbsolutePath() + ".jpg");
            ImageIO.write(tamponSauvegarde, "JPG", f);
            //	  System.out.println(f);
        }
    }

    private JFileChooser createFileChooser(String name) {
        JFileChooser fc = new JFileChooser();
        fc.setName(name);
        fc.setFileFilter(filtre);
        appResourceMap.injectComponents(fc);
        return fc;
    }

    @org.jdesktop.application.Action
    public void sauver() throws IOException, IntrospectionException {
        JFileChooser jfc = createFileChooser("sauverFichier");
        int choix = jfc.showSaveDialog(getMainFrame());
        if (choix == JFileChooser.APPROVE_OPTION) {
            File fichier = jfc.getSelectedFile();
            int i = fichier.getName().lastIndexOf('.');
            getContext().getLocalStorage().setDirectory(
                    fichier.getAbsoluteFile().getParentFile());
            System.out.println(getContext().getLocalStorage().getDirectory());
            if (i <= 0)
                fichier = new File(fichier.getAbsoluteFile().getName() + ".gri");
            System.out.println(fichier);
            XMLEncoder e = new XMLEncoder(getContext().getLocalStorage()
                    .openOutputFile(fichier.getName()));
            e.writeObject(new Integer(nbCaracteres));
            e.writeObject(new Integer(nbEssais));
            e.writeObject(new Integer(nbARecopier));
            e.writeObject(jtfTitre.getText());
            String[][] texteEditable = new String[lignes.length][4];
            for (i = 0; i < lignes.length; i++) {
                texteEditable[i][0] = lignes[i].getTextTraduction();
                texteEditable[i][1] = lignes[i].getCaractere();
                texteEditable[i][2] = lignes[i].getPinYin();
                texteEditable[i][3] = lignes[i].getClefSemantique();
            }
            e.writeObject(texteEditable);
            e.close();
        }
    }

    @org.jdesktop.application.Action
    public void charger() throws IOException {
        JFileChooser jfc = createFileChooser("ouvrirFichier");
        int choix = jfc.showOpenDialog(getMainFrame());
        if (choix == JFileChooser.APPROVE_OPTION) {

            getContext().getLocalStorage().setDirectory(
                    jfc.getSelectedFile().getAbsoluteFile().getParentFile());
            XMLDecoder d = new XMLDecoder(getContext().getLocalStorage()
                    .openInputFile(jfc.getSelectedFile().getName()));
            nbCaracteres = ((Integer) d.readObject()).intValue();
            nbEssais = ((Integer) d.readObject()).intValue();
            nbARecopier = ((Integer) d.readObject()).intValue();
            jtfTitre.setText((String) d.readObject());
            String[][] le = (String[][]) d.readObject();
            lignes = new LigneEditable[le.length];
            jpLignesModifiables.removeAll();
            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = GridBagConstraints.REMAINDER;
            GridBagLayout g = ((GridBagLayout) jpLignesModifiables.getLayout());
            for (int i = 0; i < le.length; i++) {
                lignes[i] = this.createWritingLine(le[i][1], le[i][2], le[i][0],
                        le[i][3]);
                g.setConstraints(lignes[i].panel, c);
                jpLignesModifiables.add(lignes[i].panel);
            }
            getMainFrame().setVisible(true);
        }
    }

    @org.jdesktop.application.Action
    public void showPageCaractere() {
        for (int i = 0; i < lignes.length; i++) {

            if (!lignes[i].getCaractere().isEmpty()) {
                JButton jbImprimer = new JButton();
                JButton jbEnregistreImage = new JButton("Sauver en Image");
                jbEnregistreImage.setAction(getAction("sauverImage"));
                jbImprimer.setName("jbImprimer");
                jbEnregistreImage.setAction(getAction("sauverImage"));
                jbImprimer.setAction(getAction("imprimerPageCaractere"));
                JPanel buttons = new JPanel();
                buttons.add(jbImprimer);
                buttons.add(jbEnregistreImage);

                PageCaractere page = new PageCaractere(this.jtfTitre.getText(),
                        lignes[i].getPinYin(), lignes[i].getCaractere(),
                        lignes[i].getTextTraduction(), new Font(fontChoisie, Font.PLAIN, 1));
                page.add(buttons, BorderLayout.NORTH);

            }
        }
    }

    @org.jdesktop.application.Action
    public void imprimerPageCaractere(ActionEvent ae) throws PrinterException {
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj.printDialog()) {
            JRootPane frame = ((JButton) ae.getSource()).getRootPane();
            PageCaractere page = (PageCaractere) frame.getParent();
            System.out.println(page);
            pj.setPrintable(page);
            pj.print();
            page.dispose();
        }
    }

    @org.jdesktop.application.Action
    public void showGrilleEcriture() {
        // affiche la grille d'écriture
        JPanel panneauBoutons = new JPanel();
        JButton jbImprimer = new JButton();
        JButton jbEnregistreImage = new JButton("Sauver en Image");
        jbEnregistreImage.setAction(getAction("sauverImage"));

        panneauBoutons.add(jbImprimer);
        panneauBoutons.add(jbEnregistreImage);

        jbImprimer.setName("jbImprimer");
        jbImprimer.setAction(getAction("imprimer"));
        int nbCarateresRemplis = 0;
        while (nbCarateresRemplis < lignes.length
                && !lignes[nbCarateresRemplis].getCaractere().isEmpty()) {
            nbCarateresRemplis++;
        }

        feuilleEcriture = new GrilleEcriture(jtfTitre.getText(),
                nbCarateresRemplis, nbARecopier, nbEssais, new Font(fontChoisie,
                Font.PLAIN, 1), couleurEssaiChoisi);
        for (int i = 0; i < nbCarateresRemplis; i++) {
            feuilleEcriture.setLigne(i, lignes[i].getCaractere(),
                    lignes[i].getTextTraduction(), lignes[i].getPinYin(),
                    lignes[i].getClefSemantique());
        }
        affichageGrilleEcriture = new JScrollPane(feuilleEcriture);
        if (apercuImpression == null || !apercuImpression.isVisible()) {
            apercuImpression = new JFrame("Aperçu de la grille d'ecriture");
            apercuImpression.getContentPane().add(affichageGrilleEcriture);
            apercuImpression.getContentPane().add(panneauBoutons, BorderLayout.NORTH);
            apercuImpression.pack();

        } else {
            apercuImpression.remove(((BorderLayout) apercuImpression.getContentPane()
                    .getLayout()).getLayoutComponent(BorderLayout.CENTER));
            apercuImpression.getContentPane().add(affichageGrilleEcriture);
        }

        apercuImpression.setVisible(true);
    }

    @org.jdesktop.application.Action(block = Task.BlockingScope.ACTION)
    public Task<Void, Void> imprimer() {
        if (apercuImpression == null || !apercuImpression.isVisible()) {
            showGrilleEcriture();
        }
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(feuilleEcriture);
        return new Impression(this, pj);
    }

    @org.jdesktop.application.Action
    public void voirProprietes() {
        show(getPageProprietes());
        jfProprietes.pack();
    }

    @org.jdesktop.application.Action
    public void annulerProprietes() {
        fermerProprietes();
    }

    //lorsque l'on valide, on sauvegarde les propriétés
    @org.jdesktop.application.Action
    public void validerProprietes() throws IOException {
        nbEssais = Integer.parseInt(jfProprietes.jtfNbEssais.getText());
        nbCaracteres = Integer.parseInt(jfProprietes.jtfNbCaracteres.getText());
        nbARecopier = Integer.parseInt(jfProprietes.jtfNbARecopier.getText());
        fontChoisie = (String) jfProprietes.jcbChoixFont.getSelectedItem();
        couleurEssaiChoisi = jfProprietes.jccCouleurFontEssai.getColor();
        LinkedHashMap lhm = new LinkedHashMap();
        lhm.put("nbCaracteres", new Integer(nbCaracteres));
        lhm.put("nbEssais", new Integer(nbEssais));
        lhm.put("nbARecopier", new Integer(nbARecopier));
        lhm.put("caracterePoliceChoisie", fontChoisie);
        lhm.put("couleurEssaiChoisi", couleurEssaiChoisi);
        getContext().getLocalStorage().save(lhm, propertiesFile);
        if (nbCaracteres != lignes.length)
            adjustWrittingLine();
        fermerProprietes();
    }

    private void adjustWrittingLine() {
        this.getMainFrame().setVisible(false);
        LigneEditable[] old = lignes;
        lignes = new LigneEditable[nbCaracteres];
        for (int i = 0; i < old.length && i < lignes.length; i++) {
            lignes[i] = old[i];
        }
        if (old.length < lignes.length) {
            GridBagLayout gbl = (GridBagLayout) jpLignesModifiables.getLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = GridBagConstraints.REMAINDER;
            for (int i = old.length; i < lignes.length; i++) {
                lignes[i] = createWritingLine();
                gbl.setConstraints(lignes[i].panel, c);
                jpLignesModifiables.add(lignes[i].panel);
            }
        } else
            for (int i = lignes.length; i < old.length; i++) {
                jpLignesModifiables.remove(old[i].panel);
            }
        this.getMainFrame().setVisible(true);
    }

    private void fermerProprietes() {
        jfProprietes.dispose();
        jfProprietes = null;
    }

    private JMenuBar createJMenuBar() {
        JMenuBar jmb = new JMenuBar();
        String actionsMenu[] = {"charger", "sauver", "-", "imprimer",
                "imprimerPageCaractere", "-", "quit"};
        String actionsMenu2[] = {"voirProprietes"};
        jmb.add(createMenu("menu1", actionsMenu));
        jmb.add(createMenu("menu2", actionsMenu2));
        return jmb;
    }

    private JMenu createMenu(String nomMenu, String[] actionsMenu) {
        JMenu menu = new JMenu();
        menu.setName(nomMenu);
        for (String actionMenu : actionsMenu) {
            if (actionMenu == "-")
                menu.addSeparator();
            else {
                JMenuItem jmi = new JMenuItem();
                jmi.setAction(getAction(actionMenu));
                menu.add(jmi);
            }
        }
        return menu;
    }

    private JPanel createMainFrame() {
        JPanel jpMain = new JPanel();
        JLabel jlTitre = new JLabel();
        jtfTitre = new JTextField();
        JTextField choixPinYin = new JTextField();
        choixPinYin.setName("choixPinYin");
        choixPinYin.setText(" ? á ? à ? é ? è ? ó ? ò ? í ? ì ? ú ? ù ? ? ? ? ");
        JButton jbImprimer = new JButton();
        JButton jbImprimerCaractere = new JButton();
        lignes = new LigneEditable[nbCaracteres];
        jbImprimer.setName("jbApercu");
        jbImprimerCaractere.setName("jbImprimerCaractere");
        jlTitre.setName("jlTitre");
        jtfTitre.setName("jtfTitre");

        jbImprimer.setAction(getAction("showGrilleEcriture"));
        jbImprimerCaractere.setAction(getAction("showPageCaractere"));

        GroupLayout glMain = new GroupLayout(jpMain);
        jpMain.setLayout(glMain);
        // Turn on automatically adding gaps between components
        glMain.setAutoCreateGaps(true);

        // Turn on automatically creating gaps between components that touch
        // the edge of the container and the container.
        glMain.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup hGroup = glMain.createParallelGroup(
                GroupLayout.Alignment.CENTER, false);
        GroupLayout.SequentialGroup vGroup = glMain.createSequentialGroup();
        hGroup.addGroup(glMain.createSequentialGroup().addComponent(jlTitre)
                .addComponent(jtfTitre));
        vGroup.addGroup(glMain
                .createParallelGroup(GroupLayout.Alignment.BASELINE, false)
                .addComponent(jlTitre).addComponent(jtfTitre));
        hGroup.addComponent(jpLignesModifiables);
        vGroup.addComponent(jpLignesModifiables);
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout gbl = (GridBagLayout) jpLignesModifiables.getLayout();
        c.gridwidth = GridBagConstraints.REMAINDER;
        for (int i = 0; i < nbCaracteres; i++) {
            lignes[i] = createWritingLine();
            gbl.setConstraints(lignes[i].panel, c);
            jpLignesModifiables.add(lignes[i].panel);
        }
        choixPinYin.setMargin(new Insets(0, 100, 0, 100));
        hGroup.addComponent(choixPinYin);
        vGroup.addComponent(choixPinYin);
        vGroup.addGroup(glMain.createParallelGroup().addComponent(jbImprimer)
                .addComponent(jbImprimerCaractere));
        hGroup.addGroup(glMain.createSequentialGroup().addComponent(jbImprimer)
                .addComponent(jbImprimerCaractere));
        glMain.setHorizontalGroup(hGroup);
        glMain.setVerticalGroup(vGroup);
        return jpMain;
    }

    private PageProprietes getPageProprietes() {
        if (jfProprietes == null) {
            jfProprietes = new PageProprietes();
        }
        return jfProprietes;
    }

    private LigneEditable createWritingLine() {
        return new LigneEditable();
    }

    private LigneEditable createWritingLine(String caractere, String pinYin,
                                            String traduction, String clefSemantique) {
        LigneEditable l = createWritingLine();
        l.setTextTraduction(traduction);
        l.setPinYin(pinYin);
        l.setCaractere(caractere);
        l.setClefSemantique(clefSemantique);
        return l;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        launch(GenerateurEcriture.class, args);
    }

    class PageProprietes extends JFrame {

        final static long serialVersionUID = 219;
        public JTextField jtfNbEssais = new JTextField();
        public JTextField jtfNbCaracteres = new JTextField();
        public JTextField jtfNbARecopier = new JTextField();
        JColorChooser jccCouleurFontEssai = null;
        JComboBox<String> jcbChoixFont = null;

        public PageProprietes() {
            setName("pageProprietes");

            JLabel jlNbEssais = new JLabel();
            JLabel jlNbCaracteres = new JLabel();
            JLabel jlNbARecopier = new JLabel();
            JLabel jlCaracterePoliceChoisie = new JLabel();
            jcbChoixFont = new JComboBox<String>(GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getAvailableFontFamilyNames(
                            Locale.CHINESE));
            if (couleurEssaiChoisi != null)
                jccCouleurFontEssai = new JColorChooser(couleurEssaiChoisi);
            else
                jccCouleurFontEssai = new JColorChooser();
            jcbChoixFont.setEditable(false);
            jcbChoixFont.setSelectedItem(fontChoisie);
            //jcbChoixFont.setAction(getAction("choisirFont"));
            JButton validation = new JButton();
            JButton annulation = new JButton();
            String nomActions[] = {"validerProprietes", "annulerProprietes"};
            GroupLayout glMain = new GroupLayout(this.getContentPane());
            GroupLayout.ParallelGroup hGroup = glMain
                    .createParallelGroup(GroupLayout.Alignment.CENTER);
            GroupLayout.SequentialGroup vGroup = glMain.createSequentialGroup();
            getContentPane().setLayout(glMain);

            hGroup.addGroup(glMain.createSequentialGroup().addComponent(jlNbEssais)
                    .addComponent(jtfNbEssais));
            hGroup.addGroup(glMain.createSequentialGroup()
                    .addComponent(jlNbCaracteres).addComponent(jtfNbCaracteres));
            hGroup.addGroup(glMain.createSequentialGroup()
                    .addComponent(jlNbARecopier).addComponent(jtfNbARecopier));
            hGroup.addGroup(glMain.createSequentialGroup()
                    .addComponent(jlCaracterePoliceChoisie).addComponent(jcbChoixFont));
            hGroup.addGroup(glMain.createSequentialGroup()
                    .addComponent(jlCaracterePoliceChoisie)
                    .addComponent(jccCouleurFontEssai));
            hGroup.addGroup(glMain.createSequentialGroup().addComponent(validation)
                    .addComponent(annulation));

            vGroup.addGroup(glMain.createBaselineGroup(false, false)
                    .addComponent(jlNbEssais).addComponent(jtfNbEssais));
            vGroup.addGroup(glMain.createBaselineGroup(false, false)
                    .addComponent(jlNbCaracteres).addComponent(jtfNbCaracteres));
            vGroup.addGroup(glMain.createBaselineGroup(false, false)
                    .addComponent(jlNbARecopier).addComponent(jtfNbARecopier));
            vGroup.addGroup(glMain.createBaselineGroup(false, false)
                    .addComponent(jlCaracterePoliceChoisie).addComponent(jcbChoixFont));
            vGroup.addGroup(glMain.createBaselineGroup(false, false)
                    .addComponent(jlCaracterePoliceChoisie)
                    .addComponent(jccCouleurFontEssai));
            vGroup.addGroup(glMain.createBaselineGroup(false, false)
                    .addComponent(validation).addComponent(annulation));

            glMain.setAutoCreateContainerGaps(true);
            glMain.setAutoCreateGaps(true);
            glMain.setHorizontalGroup(hGroup);
            glMain.setVerticalGroup(vGroup);

            jlNbEssais.setName("jlNbEssais");
            jlNbCaracteres.setName("jlNbCaracteres");
            jlNbARecopier.setName("jlNbARecopier");
            jlCaracterePoliceChoisie.setName("jlCaracterePoliceChoisie");

            jtfNbEssais.setName("jtfNbEssais");
            jtfNbCaracteres.setName("jtfNbCaracteres");
            jtfNbARecopier.setName("jtfNbARecopier");
            jcbChoixFont.setName("jcbChoixPolice");
            jtfNbARecopier.setText(new Integer(nbARecopier).toString());
            jtfNbEssais.setText(new Integer(nbEssais).toString());
            jtfNbCaracteres.setText(new Integer(nbCaracteres).toString());

            validation.setName("valider");
            annulation.setName("annuler");
            validation.setAction(getAction(nomActions[0]));
            annulation.setAction(getAction(nomActions[1]));

        }
	/*
		@Action
		public void choisirFont() {
		  fontChoisie = (String) jcbChoixFont.getSelectedItem();
		  System.out.println(fontChoisie);
		}
		*/
    }

    class LigneEditable {

        private JTextField jtfCaractere = new JTextField();
        private JTextField jtfPinYin = new JTextField();
        private JTextField jtfClefSemantique = new JTextField();
        private JTextField jtfTraduction = new JTextField();
        public JPanel panel = new JPanel();

        public LigneEditable() {
            JLabel jlCaractere = new JLabel();
            JLabel jlPinYin = new JLabel();
            JLabel jlClefSemantique = new JLabel();
            JLabel jlTraduction = new JLabel();

            jlCaractere.setName("jlCaractere");
            jlPinYin.setName("jlPinYin");
            jlTraduction.setName("jlTraduction");
            jlClefSemantique.setName("jlClefSemantique");

            jtfCaractere.setName("jtfCaractere");
            jtfPinYin.setName("jtfPinYin");
            jtfTraduction.setName("jtfTraduction");
            jtfClefSemantique.setName("jtfClefSemantique");
            GroupLayout glContenu = new GroupLayout(panel);
            panel.setLayout(glContenu);

            // Turn on automatically adding gaps between components
            glContenu.setAutoCreateGaps(true);

            // Turn on automatically creating gaps between components that touch
            // the edge of the container and the container.
            glContenu.setAutoCreateContainerGaps(true);

            GroupLayout.ParallelGroup vGroup = glContenu.createParallelGroup();
            vGroup.addComponent(jlCaractere);
            vGroup.addComponent(jtfCaractere);
            vGroup.addComponent(jlPinYin);
            vGroup.addComponent(jtfPinYin);
            vGroup.addComponent(jlTraduction);
            vGroup.addComponent(jtfTraduction);
            vGroup.addComponent(jlClefSemantique);
            vGroup.addComponent(jtfClefSemantique);

            GroupLayout.SequentialGroup hGroup = glContenu.createSequentialGroup();
            hGroup.addComponent(jlCaractere);
            hGroup.addComponent(jtfCaractere);
            hGroup.addComponent(jlPinYin);
            hGroup.addComponent(jtfPinYin);
            hGroup.addComponent(jlTraduction);
            hGroup.addComponent(jtfTraduction);
            hGroup.addComponent(jlClefSemantique);
            hGroup.addComponent(jtfClefSemantique);
            glContenu.setVerticalGroup(vGroup);
            glContenu.setHorizontalGroup(hGroup);
            appResourceMap.injectComponents(panel);
        }

        public String getTextTraduction() {
            return jtfTraduction.getText();
        }

        public void setTextTraduction(String s) {
            jtfTraduction.setText(s);
        }

        public String getCaractere() {
            return jtfCaractere.getText();
        }

        public void setCaractere(String s) {
            jtfCaractere.setText(s);
        }

        public void setPinYin(String s) {
            jtfPinYin.setText(s);
        }

        public String getPinYin() {
            return jtfPinYin.getText();
        }

        public void setClefSemantique(String s) {
            jtfClefSemantique.setText(s);
        }

        public String getClefSemantique() {
            return jtfClefSemantique.getText();
        }
    }

    private static class FileFilterExtension extends FileFilter {
        private final String description;
        private final String extension;

        FileFilterExtension(String description, String ext) {
            this.description = description;
            this.extension = ext;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String fileName = f.getName();
            int i = fileName.lastIndexOf('.');
            if ((i > 0) && (i < (fileName.length() - 1))) {
                String fileExt = fileName.substring(i + 1);
                if (extension.equalsIgnoreCase(fileExt)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return description;
        }

        public String getExtension() {
            return extension;
        }
    }

    private static class GrilleFileFilter extends FileFilter {
        private final String description;

        GrilleFileFilter(String description) {
            this.description = description;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String fileName = f.getName();
            int i = fileName.lastIndexOf('.');
            if ((i > 0) && (i < (fileName.length() - 1))) {
                String fileExt = fileName.substring(i + 1);
                if ("gri".equalsIgnoreCase(fileExt)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

}
