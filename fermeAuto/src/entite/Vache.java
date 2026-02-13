package entite;


import modele.Case;
import modele.Ferme;
import modele.Position;
import modele.Saison;


/**
 * -représente une vache dans la ferme.
 * -elle consomme de l’eau et de la nourriture, produit du lait, dépose du fertilisant,
 * -se déplace et vieillit au fil des jours.
 */
public class Vache extends Animal 
{

    /**
     * -crée une vache à la position donnée.
     * -les caractéristiques sont chargées depuis le fichier "vache.properties".
     *
     * @param pos la position initiale de la vache sur la grille
     */
    public Vache(Position pos) {
        super("vache.properties", pos);
    }

    /**
     * -exécute le cycle quotidien de la vache :
     * elle boit, mange, produit du lait, vieillit,
     * dépose du fertilisant et se déplace aléatoirement.
     *  -en hiver : La production de lait baisse de 50%
     * -en été: besoin en nourriture des poules (+25%).
     *
     * @param ferme la ferme entière (accès aux cases voisines, etc.)
     */
    
    
    @Override
    public void tourSuivant(Ferme ferme, Saison saison)
    {
        if (!vivant) return;

        Case c = ferme.getCase(position);

        // Été : augmente le besoin en eau
        if (saison == Saison.ETE) {
            eauParJour = baseEauParJour * 1.3;
        } else {
            eauParJour = baseEauParJour;
        }

        consommerEau(c);
        consommerNourriture(c);
        if (!vivant) return;

        deposerFertilisant(c);

        // Hiver : production de lait réduite
        double prod = production;
        if (saison == Saison.HIVER) prod *= 0.5;

        if (aSoif()) prod -= prod * malusSoif / 100.0;
        if (aFaim()) prod -= prod * malusFaim / 100.0;

        

        seDeplacer(ferme);
        essayerReproduction(ferme);
    }



    /**
     * -crée une nouvelle vache à l’état de bébé (non productive, pas encore mature).
     *
     * @param pos position du bébé sur la grille
     * @return une instance de Vache avec le statut bébé
     */
    
    public Animal creerBebe(Position pos) 
    {
        Vache bebe = new Vache(pos);
        bebe.estBebe = true;
        return bebe;
    }
}

