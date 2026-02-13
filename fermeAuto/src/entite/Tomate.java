package entite;

import modele.Case;
import modele.Position;
import modele.Saison;

/**
 * Représente un plant de tomate dans la ferme.
 * La croissance est fixe chaque jour, sauf si la plante a soif ou manque de fertilisant.
 */
public class Tomate extends Plante 
{

    /**
     * -crée une tomate à la position donnée.
     * -les caractéristiques sont chargées depuis tomate.properties.
     *
     * @param pos la position de la tomate sur la grille
     */
    public Tomate(Position pos) {
        super("tomate.properties", pos);
    }

    /**
     * -ce que fait la tomate à chaque tour :
     *  consomme de l’eau
     * ; applique les malus si soif ou pas assez de fertilisant
     * ; ajoute une quantité fixe de calories
     * ; spécifie les comportements pour chaque saison
     * ; En hiver : Les tomates se congèlent, arrêtant leur croissance ainsi que leurs besoins. 
     * ; En hiver :Elles ne peuvent pas non plus être mangées par les animaux.
     * ; en été : la croissance des tomates augmente de 20%
     * 
     *
     * @param c la case où elle se trouve
     * 
     */
    @Override
    public void tourSuivant(Case c, Saison saison) 
    {
        if (!vivante) return;

        // 1) Gèle en hiver
        if (saison == Saison.HIVER) {
            System.out.println("Tomate gelée en hiver à " + position);
            return;
        }

        // 2) Eau
        consommerEau(c);
        if (!vivante) return;

        // 3) Fertilisant
        if (c.getFertilisant() >= fertParJour) 
        {
            c.ajouterFertilisant(-fertParJour);
        } // sinon on applique un malus, pas de mort

        // 4) Croissance
        double gain = croissance;
        if (saison == Saison.ETE) gain *= 1.2;
        if (aSoif())      gain *= (1 - malusSoif/100.0);
        if (c.getFertilisant() < fertParJour) gain *= (1 - malusFaim/100.0);

        calInit += gain;
    }

}



