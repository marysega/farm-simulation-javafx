package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import modele.Ferme;
import gestion.Magasin;
import gestion.Jeu;

import java.util.Map;

/**
 * Constructeur de l'interface utilisateur pour la ferme isométrique.
 * Crée et assemble la scène principale, incluant la vue de la ferme,
 * les panneaux de contrôle et de statistiques, ainsi que les barres
 * d'informations de saison et de jour.
 */
public class UIBuilder {

    /**
     * Résultat de la construction de l'interface, encapsulant
     * la scène JavaFX, la racine du layout, la vue de la ferme et
     * le panneau de statistiques.
     */
    public static class Result {
        private final Scene scene;
        private final StackPane rootPane;
        private final IsoFarmView farmView;
        private final EntrepotPanel statsPanel;

        /**
         * Initialise le résultat de la construction d'UI.
         *
         * @param scene la scène principale
         * @param rootPane le conteneur racine contenant tous les éléments
         * @param farmView vue isométrique de la ferme
         * @param statsPanel panneau affichant les statistiques de l'entrepôt
         */
        public Result(Scene scene,
                      StackPane rootPane,
                      IsoFarmView farmView,
                      EntrepotPanel statsPanel) {
            this.scene      = scene;
            this.rootPane   = rootPane;
            this.farmView   = farmView;
            this.statsPanel = statsPanel;
        }

        /**
         * @return la scène JavaFX construite
         */
        public Scene getScene()              { return scene; }

        /**
         * @return le conteneur racine de la scène
         */
        public StackPane getRootPane()       { return rootPane; }

        /**
         * @return la vue isométrique de la ferme
         */
        public IsoFarmView getFarmView()     { return farmView; }

        /**
         * @return le panneau affichant les statistiques
         */
        public EntrepotPanel getStatsPanel() { return statsPanel; }
    }

    /**
     * Construit la scène principale de l'application de simulation de ferme.
     * Assemble la vue de la ferme, les panneaux de contrôle et de statistiques,
     * ainsi que les barres d'information pour la saison et le jour.
     *
     * @param ferme instance du modèle de la ferme
     * @param magasin gestionnaire du stock de fertilisant
     * @param jeu logique métier de la simulation
     * @param icons collection d'icônes pour le rendu graphique
     * @param width largeur de la scène
     * @param height hauteur de la scène
     * @return un objet Result contenant la scène et ses composants clés
     */
    public static Result buildScene(Ferme ferme,
                                    Magasin magasin,
                                    Jeu jeu,
                                    Map<String, Image> icons,
                                    double width,
                                    double height) {
        // 1) Vue et panels
        IsoFarmView isoFarm       = new IsoFarmView(ferme, icons, width, height);
        EntrepotPanel statsPanel  = new EntrepotPanel();
        ControlPanel controlPanel = new ControlPanel(null, null);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setPickOnBounds(false);

        // 2) Barre supérieure affichant saison et jour
        Label seasonLabel = new Label("Saison : " + jeu.getSaison());
        seasonLabel.setId("seasonLabel");
        seasonLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Label dayLabel    = new Label("Jour : " + jeu.getJourDansSaison() + "/30");
        dayLabel.setId("dayLabel");
        dayLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        HBox topBar = new HBox(20, seasonLabel, dayLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(8));
        topBar.setBackground(new Background(new BackgroundFill(
            Color.rgb(0, 0, 0, 0.6), CornerRadii.EMPTY, Insets.EMPTY
        )));
        topBar.setMaxWidth(Double.MAX_VALUE);

        // 3) Barre inférieure Play/Pause et statut
        Button btnPlay    = new Button("Play");
        btnPlay.setId("btnPlay");
        btnPlay.setStyle("-fx-font-size: 14px;");

        Label statusLabel = new Label("Simulation en pause.");
        statusLabel.setId("statusLabel");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        HBox bottomBar = new HBox(10, btnPlay, statusLabel);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(8,8,40,8));
        bottomBar.setBackground(new Background(new BackgroundFill(
            Color.rgb(0, 0, 0, 0.6), CornerRadii.EMPTY, Insets.EMPTY
        )));
        bottomBar.setMaxWidth(Double.MAX_VALUE);

        // 4) Layout principal BorderPane
        BorderPane main = new BorderPane();
        main.setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0,0,0,0));
        main.setLeft(controlPanel);
        main.setCenter(isoFarm);
        main.setRight(statsPanel);

        // 5) Fond herbe neutre en arrière-plan
        Background neutralBg = new Background(new BackgroundImage(
            icons.get("grass"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT
        ));

        StackPane root = new StackPane(new Region(), main);
        root.setBackground(neutralBg);
        StackPane.setAlignment(main, Pos.CENTER);
        bottomBar.setPadding(new Insets(8, 8, 8, 8));

        // 6) Ajout de la barre inférieure en overlay
        root.getChildren().add(bottomBar);
        StackPane.setAlignment(bottomBar, Pos.BOTTOM_CENTER);
        StackPane.setMargin(bottomBar, new Insets(700,0,0,0));

        // 7) Initialisation du moteur de simulation
        SimulationEngine engine = new SimulationEngine(
            jeu,
            ferme,
            magasin,
            controlPanel,
            isoFarm,
            statsPanel,
            seasonLabel,
            dayLabel,
            btnPlay,
            statusLabel
        );
        engine.init();

        // 8) Contrôleur de jeu pour gérer les interactions utilisateur
        GameController controller = new GameController(
            ferme,
            magasin,
            jeu,
            isoFarm,
            engine
        );
        ControlPanel cp2 = new ControlPanel(
            controller::setModeActif,
            () -> controller.acheterFertilisant(150)
        );
        cp2.setPadding(new Insets(10));
        cp2.setPickOnBounds(false);
        main.setLeft(cp2);

        // 9) Premier redraw et mise à jour des stats
        Platform.runLater(() -> {
            isoFarm.redraw();
            engine.refreshStats();
        });

        // 10) Création et retour de la scène
        Scene scene = new Scene(root, width, height);
        return new Result(scene, root, isoFarm, statsPanel);
    }
}




