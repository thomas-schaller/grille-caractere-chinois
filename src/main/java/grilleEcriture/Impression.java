package grilleEcriture;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

class Impression extends Task<Void, Void> {
    PrinterJob p;
    boolean    afficherDialogueImpression = true;

    Impression(Application a, PrinterJob pj) {
        super(a);
        p = pj;
    }

    Impression(Application a, PrinterJob pj, boolean afficherDialog) {
        super(a);
        p = pj;
        afficherDialogueImpression = afficherDialog;
    }

    @Override
    protected Void doInBackground() {
        message("startMessage");
        if (!afficherDialogueImpression || p.printDialog())
            try {
                p.print();
            }
            catch (PrinterException pe) {
                this.failed(pe);
            }
        else
            cancel(true);
        return null;
    }

    @Override
    protected void cancelled() {
        message("cancelMessage");
    }

    protected void succeeded(Void result) {
        message("finishedMessage");
    }

    protected void failed(Exception e) {
        message("errorMessage");
    }
}
