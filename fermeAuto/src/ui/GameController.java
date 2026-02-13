package ui;

import modele.Position;

import modele.Ferme;
import gestion.Magasin;
import ui.IsoFarmView;
import ui.SimulationEngine;
import gestion.Jeu;

/**
 * Contrôleur principal : gère les clics, lit les prix via Magasin,
 * affiche les alertes/toasts, et rafraîchit l’UI via l’engine.
 */
public class GameController 
{
    private final Ferme ferme;
    private final Magasin magasin;
    private final Jeu jeu;
    private final IsoFarmView farmView;
    private final SimulationEngine engine;
    private String modeActif;

    /**
     * Maintenant à 5 paramètres :
     * - ferme, magasin, jeu explicitement
     * - farmView, engine pour le contrôle et rafraîchissement
     */
    public GameController(Ferme ferme,
                          Magasin magasin,
                          Jeu jeu,
                          IsoFarmView farmView,
                          SimulationEngine engine) {
        this.ferme    = ferme;
        this.magasin  = magasin;
        this.jeu      = jeu;
        this.farmView = farmView;
        this.engine   = engine;
        this.modeActif = null;

        farmView.setCaseClickListener(this::handleCaseClick);
    }

    /** Définit le mode (animal, plante, fertilisant, puits, destruire). */
    public void setModeActif(String mode) 
    {
        if (engine.isRunning()) {
            FenetreAlerte.erreur("Action refusée", "Attendez la fin de la saison.");
            return;
        }
        this.modeActif = mode;
        FenetreAlerte.toast(farmView, "Mode : " + mode + " (cliquez sur une case)");
    }

    /** Achète du fertilisant et met à jour les stats. */
    public void acheterFertilisant(int qte) 
    {
        double prix = magasin.getPrixFertilisant(qte);
        if (ferme.getBudget() < prix) {
            FenetreAlerte.erreur("Budget insuffisant",
                                 "Il vous faut " + prix + " Y pour " + qte + " g.");
            return;
        }
        magasin.acheterFertilisant(qte);
        FenetreAlerte.info("Fertilisant acheté",
                           qte + " g pour " + prix + " Y.");
        engine.refreshStats();
    }

    /** Gère le clic sur une case selon le modeActif. */
    private void handleCaseClick(int i, int j) 
    {
        if (engine.isRunning() || modeActif == null) return;
        Position pos = new Position(i, j);

        switch (modeActif) {
            case "Poule", "Vache" -> 
            {
                double prix = magasin.getPrixAnimal(modeActif);
                if (ferme.getBudget() < prix) {
                    FenetreAlerte.erreur("Budget insuffisant",
                                         "Il vous faut " + prix + " Y pour une " + modeActif + ".");
                    break;
                }
                magasin.acheterAnimal(modeActif, pos);
                FenetreAlerte.info("Achat " + modeActif,
                                   modeActif + " pour " + prix + " Y.");
            }

            case "Tomate", "Ble" -> 
            {
                double prix = magasin.getPrixPlante(modeActif);
                if (ferme.getBudget() < prix) {
                    FenetreAlerte.erreur("Budget insuffisant",
                                         "Il vous faut " + prix + " Y pour du " + modeActif + ".");
                    break;
                }
                magasin.acheterPlante(modeActif, pos);
                FenetreAlerte.info("Achat " + modeActif,
                                   modeActif + " pour " + prix + " Y.");
            }

            case "Fertilisant" -> 
            {
                if (magasin.getStockFertilisant() < 50) {
                    FenetreAlerte.erreur("Stock insuffisant",
                                         "Au moins 50 g requis.");
                    break;
                }
                magasin.utiliserFertilisant(pos, 50);
                FenetreAlerte.toast(farmView,
                      "+50 g fertilisant appliqués en ["+i+","+j+"]");
            }

            case "Puit" -> 
            {
                double prixPuit = magasin.getPrixPuit();
                if (ferme.getBudget() < prixPuit) {
                    FenetreAlerte.erreur("Budget insuffisant",
                                         "Il vous faut " + prixPuit + " Y pour un puits.");
                    break;
                }
                magasin.acheterPuits(pos);
                FenetreAlerte.toast(farmView,
                      "Puits construit en ["+i+","+j+"]");
            }

            case "DetruirePuit" -> 
            {
                boolean ok = FenetreAlerte.confirmation(
                    "Confirmer destruction",
                    "Détruire le puits en ["+i+","+j+"] ? (200 Y)"
                );
                if (!ok) break;
                magasin.detruirePuits(pos);
                FenetreAlerte.toast(farmView, "Puits détruit, -200 Y");
            }

            default -> { /* pas de mode */ }
        }

        // réinitialise et met à jour
        modeActif = null;
        engine.refreshStats();
        farmView.redraw();
    }
}
