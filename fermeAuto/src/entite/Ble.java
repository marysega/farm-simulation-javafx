package entite;

import modele.Case;
import modele.Position;
import modele.Saison;

/**
 * -représente une plante de blé dans la ferme.
 * -le blé pousse chaque jour selon un pourcentage de ses calories actuelles.
 */
public class Ble extends Plante {

    /**
     * -crée un blé à la position donnée.
     * -les données sont chargées depuis ble.properties.
     *
     * @param pos la position du blé sur la grille
     */
    public Ble(Position pos) {
        super("ble.properties", pos);
    }

    /**
     * -ce que le blé fait à chaque jour :
     * ; consomme de l’eau
     * ; vérifie le fertilisant
     * ; calcule sa croissance selon un pourcentage
     * ; applique les malus si soif ou manque de fertilisant
     * ; en hiver : la production de blé baisse de 30%.
     * ; en été : La sécheresse augmente les besoins en eau du blé (+30%) et a croissance de 15%.
     *
     * @param c la case où il se trouve
     */
    
    
   
    public void tourSuivant(Case c, Saison saison) {
        if (!vivante) return;

        // Eau
        consommerEau(c);
        if (!vivante) return;

        // Fertilisant
        if (c.getFertilisant() >= fertParJour) {
            c.ajouterFertilisant(-fertParJour);
        }

        // Croissance en pourcentage
        double gain = calInit * (croissance / 100.0);
        if (saison == Saison.HIVER) gain *= 0.7;
        if (saison == Saison.ETE)   gain *= 1.15;
        if (aSoif())      gain *= (1 - malusSoif/100.0);
        if (c.getFertilisant() < fertParJour) gain *= (1 - malusFaim/100.0);

        calInit += gain;
    }


}
