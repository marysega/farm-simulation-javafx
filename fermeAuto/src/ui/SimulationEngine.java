package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import modele.Ferme;
import gestion.Magasin;
import gestion.Jeu;

import java.util.Map;

/**
 * Initialise le moteur de simulation avec les composants du modèle et de l'interface.
 *
 * @param jeu instance du modèle de jeu gérant la logique métier
 * @param ferme la ferme dont on simule l'évolution
 * @param magasin gestionnaire de stock de fertilisant
 * @param controlPanel panneau de contrôle pour activer/désactiver l'UI
 * @param farmView vue isométrique de la ferme à redessiner
 * @param statsPanel panneau affichant les statistiques de l'entrepôt
 * @param seasonLabel label affichant la saison actuelle
 * @param dayLabel label affichant le jour dans la saison
 * @param btnPlay bouton pour démarrer/pause la simulation
 * @param statusLabel label affichant le statut de la simulation
 */

public class SimulationEngine 
{
    private final Jeu jeu;
    private final Ferme ferme;
    private final Magasin magasin;
    private final IsoFarmView farmView;
    private final EntrepotPanel statsPanel;
    private final Label seasonLabel;
    private final Label dayLabel;
    private final Button btnPlay;
    private final Label statusLabel;
    private final Timeline timeline;
    private final ControlPanel controlPanel;


    private int mortsAnimauxCumules = 0;
    private int mortsPlantesCumules = 0;

    public SimulationEngine(Jeu jeu,
                            Ferme ferme,
                            Magasin magasin,ControlPanel controlPanel,
                            IsoFarmView farmView,
                            EntrepotPanel statsPanel,
                            Label seasonLabel,
                            Label dayLabel,
                            Button btnPlay,
                            Label statusLabel) {
        this.jeu         = jeu;
        this.ferme       = ferme;
        this.magasin     = magasin;
        this.farmView    = farmView;
        this.statsPanel  = statsPanel;
        this.seasonLabel = seasonLabel;
        this.dayLabel    = dayLabel;
        this.btnPlay     = btnPlay;
        this.statusLabel = statusLabel;
        this.controlPanel = controlPanel;

        // Timeline : 1 tick = 1 jour toutes les 5 secondes, en boucle infinie
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> tick()));
        this.timeline.setCycleCount(Animation.INDEFINITE);

        // Play/Pause
        btnPlay.setOnAction(e -> {
            if (timeline.getStatus() == Animation.Status.RUNNING) {
                // si c'est en cours, alors on met en pause
            	System.out.println(">>> Play/Pause cliqué, status avant = " + timeline.getStatus());
                timeline.pause();
                statusLabel.setText("Simulation en pause.");
                btnPlay.setText("Play");
                
            } else {
                // on lance la saison
            	onPlayPressed() ;
            	
            }
        

        });
    }

    /**
     * Configure l'état initial de la simulation : en pause et UI interactive.
     */
    public void init() {
    	// Au démarrage, on est en pause et on peut interagir avant la saison
        timeline.pause();
        controlPanel.setDisable(false);
        farmView.setMouseTransparent(false);
    }
    
    /**
     * Démarre ou reprend la simulation : désactive l'UI et lance la timeline.
     */
    
    private void onPlayPressed() {
        // Désactiver UI quand on lance la saison
        controlPanel.setDisable(true);
        farmView.setMouseTransparent(true);
        statusLabel.setText("Simulation en cours…");
        btnPlay.setText("Pause");
        timeline.play();
    }


    /**
     * Un tick de simulation correspondant à un jour :
     * avance le modèle, gère la fin de saison, met à jour l'UI et redessine la ferme.
     */
    private void tick() {
        // 1) avance le modèle
        jeu.jouer(1);

        // 2) si on vient de clôturer la saison dans Jeu.jouer(),
        //    jourDansSaison est remis à 1 : on considère que la saison vient de changer.
        if (jeu.getJourDansSaison() == 1) {
            // on arrête et on réactive l’UI
            timeline.pause();
            btnPlay.setText("Play");
            statusLabel.setText("Fin de saison. Vous pouvez interagir.");
            controlPanel.setDisable(false);
            farmView.setMouseTransparent(false);
        }

        // 3) mise à jour des libellés Saison/Jour
        seasonLabel.setText("Saison : " + jeu.getSaison().name());
        dayLabel   .setText("Jour : "   + jeu.getJourDansSaison() + "/30");

        // 4) rafraîchit l’EntrepôtPanel (vend déjà dedans via Jeu.jouer)
        refreshStats();

        // 5) redessine la vue
        farmView.redraw();
    }
    
    /**
     * Indique si la simulation est en cours d'exécution.
     *
     * @return vrai si la timeline est en cours, faux sinon
     */
    
    public boolean isRunning() {
        return timeline.getStatus() == Animation.Status.RUNNING;
    }
    


    /**
     * Met à jour le panneau de statistiques sans modifier le modèle :
     * calcule les décès quotidiens et cumulés, puis affiche les données.
     */
    public void refreshStats() {
        // décès journaliers
        int mortsAnimauxJour = jeu.getDecesAnimauxJournalier()
                                   .values().stream().mapToInt(i->i).sum();
        int mortsPlantesJour = jeu.getDecesPlantesJournalier()
                                   .values().stream().mapToInt(i->i).sum();
        // cumul
        mortsAnimauxCumules += mortsAnimauxJour;
        mortsPlantesCumules += mortsPlantesJour;

        // comptes vivants par type
        Map<String,Integer> animParType   = jeu.getAnimauxParType();
        Map<String,Integer> planteParType = jeu.getPlantesParType();
        int nbVaches  = animParType.getOrDefault("Vache", 0);
        int nbPoules  = animParType.getOrDefault("Poule", 0);
        int nbTomates = planteParType.getOrDefault("Tomate", 0);
        int nbBle     = planteParType.getOrDefault("Ble", 0);

        statsPanel.update(
            ferme.getBudget(),
            magasin.getStockFertilisant(),
            jeu.getEntrepot().getCaloriesParType("Œufs"),
            jeu.getEntrepot().getCaloriesParType("Lait"),
            nbVaches, nbPoules, nbTomates, nbBle,
            mortsAnimauxCumules,
            mortsPlantesCumules
        );
    }
}
