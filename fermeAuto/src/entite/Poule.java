package entite;


import modele.Case;
import modele.Ferme;
import modele.Position;
import modele.Saison;

/**
 * -représente une poule dans la ferme.
 * -elle consomme de l’eau et de la nourriture, produit des œufs, dépose du fertilisant,
 * -se déplace et vieillit au fil des jours.
 */
public class Poule extends Animal {

    /**
     * -crée une poule à la position donnée.
     * -les caractéristiques sont chargées depuis le fichier "poule.properties".
     *
     * @param pos la position initiale de la poule sur la grille
     */
    public Poule(Position pos) {
        super("poule.properties", pos);
    }

    /**
     * -exécute le cycle quotidien de la poule :
     * -elle boit, mange, produit des œufs, vieillit,
     * -dépose du fertilisant et se déplace aléatoirement.
     * 
     * -effets saisonniers :
     * ; en hiver : 1/3 chance de produire un œuf
     * ; en été : besoin en nourriture augmenté de 25%
     *
     * @param ferme  la ferme entière (accès aux cases voisines, etc.)
     * @param saison la saison actuelle
     */
    
    public void tourSuivant(Ferme ferme, Saison saison) {
        if (!vivant) return;

        Case c = ferme.getCase(position);
        consommerEau(c);
        consommerNourriture(c);
        if (!vivant) return;

        deposerFertilisant(c);

        // Ajustement saisonnier
        if (saison == Saison.ETE) {
            nourritureParJour = baseNourritureParJour * 1.25;
        } else {
            nourritureParJour = baseNourritureParJour;
        }

        double prod = production;
        if (aSoif()) prod -= prod * malusSoif / 100.0;
        if (aFaim()) prod -= prod * malusFaim / 100.0;

        if (saison == Saison.HIVER && Math.random() > (1.0 / 3)) prod = 0;

        

        seDeplacer(ferme);
        essayerReproduction(ferme);
    }

    /**
     *-crée une nouvelle poule à l’état de bébé (non productive, pas encore mature).
     *
     * @param pos position du bébé sur la grille
     * @return une Poule avec le statut bébé
     */
    
    public Animal creerBebe(Position pos) {
        Poule bebe = new Poule(pos);
        bebe.estBebe = true;
        return bebe;
    }
} 


