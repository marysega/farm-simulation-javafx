package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

/**
 * Panneau latéral droit contenant l'icône du magasin et
 * les boutons d'actions pour la ferme (achat d'animaux, plantation, fertilisation, puits).
 */
public class ControlPanel extends VBox 
{
    /**
     * Crée le panneau de contrôle avec l'icône du magasin et les boutons.
     *
     * @param storeIcon         image du magasin (affichée en haut)
     * @param setMode           callback pour définir le mode d'action (placement)
     * @param acheterFertilisant callback pour acheter du fertilisant
     */
    public ControlPanel(Consumer<String> setMode, Runnable acheterFertilisant) 
    {
        setSpacing(10);
        setStyle("-fx-padding: 10;");

        

        // Titre
        Label title = new Label("Magasin");

        // Boutons
        Button bPoule     = createButton("Acheter Poule",      () -> setMode.accept("Poule"));
        Button bVache     = createButton("Acheter Vache",      () -> setMode.accept("Vache"));
        Button bTomate    = createButton("Acheter Tomate",     () -> setMode.accept("Tomate"));
        Button bBle       = createButton("Acheter Blé",        () -> setMode.accept("Ble"));
        Button bBuyFert   = createButton("Acheter Fertilisant", acheterFertilisant);
        Button bPlaceFert = createButton("Placer Fertilisant",  () -> setMode.accept("Fertilisant"));
        Button bPuit      = createButton("Construire Puits",   () -> setMode.accept("Puit"));
        Button bDetruPuit = createButton("Détruire Puits",     () -> setMode.accept("DetruirePuit"));

        // Assemblage
        getChildren().addAll(
            
            title,
            bPoule,
            bVache,
            bTomate,
            bBle,
            bBuyFert,
            bPlaceFert,
            bPuit,
            bDetruPuit
        );
    }

    /**
     * Crée un bouton plein largeur et définit son action.
     *
     * @param label  texte du bouton
     * @param action action exécutée au clic
     * @return le bouton configuré
     */
    private Button createButton(String label, Runnable action) 
    {
    	Button b = new Button(label);
        b.setPrefWidth(150);
        b.setMaxWidth(150);
        b.setOnAction(e -> action.run());
        return b;
    }
}
