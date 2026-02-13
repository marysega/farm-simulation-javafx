package mainPrincipale;


import javafx.application.Application;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import modele.Ferme;
import ui.UIBuilder;
import ui.IsoFarmView;
import ui.ControlPanel;
import ui.EntrepotPanel;
import ui.SimulationEngine;
import ui.GameController;
import ui.IconRegistry;

import gestion.Magasin;
import gestion.Jeu;


import java.util.Map;

/**
 * Point d’entrée de l’application JavaFX « Ferme Autosuffisante ».
 * 
 * Initialise le modèle (Ferme , Magasin, Jeu), charge les icônes via IconRegistry,
 * délègue la construction de l’interface à UIBuilder, puis démarre la fenêtre principale.
 * 
 */
public class AppFX extends Application 
{
	
	/**
     * Méthode appelée par le framework JavaFX au lancement de l’application.
     * <ul>
     *   <li>Crée les objets métier : Ferme, Magasin, Jeu.</li>
     *   <li>Charge les images via IconRegistry.</li>
     *   <li>Construit la scène et ses composants UI via UIBuilder.</li>
     *   <li>Configure et affiche la fenêtre (Stage).</li>
     * </ul>
     *  
     * @param stage Le conteneur principal de l’application dans lequel
     *              on place la Scene construite.
     */
    @Override
    public void start(Stage stage) 
    {
        Ferme ferme     = new Ferme();
        Magasin magasin = new Magasin(ferme);
        Jeu jeu         = new Jeu(ferme, magasin);

        // Charge les icônes depuis ui.IconRegistry
        Map<String, Image> icons = IconRegistry.loadIcons("file:data/images/");

        // Construit la scène via ui.UIBuilder
        UIBuilder.Result ui = UIBuilder.buildScene(
            ferme,
            magasin,
            jeu,
            icons,
            900, 650
        );

        stage.setScene(ui.getScene());
        stage.setTitle("Ferme Autosuffisante");
        stage.show();
    }
    
    /**
     * Point d’entrée Java standard.
     * Délègue au launcher JavaFX la création de l’application.
     *
     * @param args Arguments de la ligne de commande (inutilisés).
     */

    public static void main(String[] args) {
        launch();
    }
}





