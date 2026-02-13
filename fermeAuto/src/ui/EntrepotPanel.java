package ui;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Panneau latéral affichant l'état de l'entrepôt :
 * budget, fertilisant, calories œufs, calories lait,
 * comptage des entités (vaches, poules, tomates, blé),
 * et statistiques de vivants/morts journalières.
 */
public class EntrepotPanel extends VBox 
{
    
    private final Label budgetLabel = new Label();
    private final Label fertiLabel = new Label();
    private final Label oeufsLabel = new Label();
    private final Label laitLabel = new Label();
    private final Label vacheLabel = new Label();
    private final Label pouleLabel = new Label();
    private final Label tomateLabel = new Label();
    private final Label bleLabel = new Label();
    private final Label mortsAnimauxLbl   = new Label();
    private final Label mortsPlantesLbl   = new Label();

    

    /**
     * @param icon entrepôt icon
     */
    public EntrepotPanel() 
    {
        setSpacing(8);
        setPadding(new Insets(10));

        ;

        getChildren().addAll(
            
            new Label("Entrepôt"),
            budgetLabel,
            fertiLabel,
            oeufsLabel,
            laitLabel,
            vacheLabel,
            pouleLabel,
            tomateLabel,
            bleLabel,
            mortsAnimauxLbl,
            mortsPlantesLbl
        );
        for (javafx.scene.Node node : getChildren()) 
        {
            if (node instanceof Label lbl && lbl != getChildren().get(0)) {
                lbl.setStyle(
                  "-fx-border-color: #888; " +
                  "-fx-background-color: #ddd; " +
                  "-fx-padding: 5 10; " +
                  "-fx-max-width: 150; " +
                  "-fx-alignment: center-left;"
                );
            }
        }
    }

    /**
     * Met à jour les valeurs affichées.
     *
     * @param budget      budget en Y
     * @param fertilisant stock de fertilisant en g
     * @param oeufsCal    calories totales d’œufs
     * @param laitCal     calories totales de lait
     * @param nbVaches    nombre de vaches
     * @param nbPoules    nombre de poules
     * @param nbTomates   nombre de tomates
     * @param nbBle       nombre de blé
     * @param vivants     nombre d’entités vivantes aujourd’hui
     * @param morts       nombre d’entités mortes aujourd’hui
     */
    public void update(double budget, double fertilisant,
                       double oeufsCal, double laitCal,
                       int nbVaches, int nbPoules,
                       int nbTomates, int nbBle,
                       int mortsAnimauxCumules,
                       int mortsPlantesCumules) {
        budgetLabel.setText(String.format("Budget: %.1f Y", budget));
        fertiLabel.setText(String.format("Fertilisant: %.0f g", fertilisant));
        oeufsLabel.setText(String.format("Œufs: %.0f cal", oeufsCal));
        laitLabel.setText(String.format("Lait: %.0f cal", laitCal));
        vacheLabel.setText("Vaches: " + nbVaches);
        pouleLabel.setText("Poules: " + nbPoules);
        tomateLabel.setText("Tomates: " + nbTomates);
        bleLabel.setText("Blé: " + nbBle);
        mortsAnimauxLbl  .setText("Ani morts : " + mortsAnimauxCumules);
        mortsPlantesLbl  .setText("Pl mortes : " + mortsPlantesCumules);
    }
}
