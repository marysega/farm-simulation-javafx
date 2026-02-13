package modele;

import entite.Animal;
import entite.Plante;


/**
 * -représente une case de la ferme.
 * -elle contient de l'eau, du fertilisant, et peut accueillir un puits.
 * -on peut acceder aux plantes et aux animaux sur la case
 */
public class Case 
{

    private final Position position; // la position (x, y) de la case
    private double eauDispo;         // eau disponible pour les plantes
    private double fertilisant;      // fertilisant disponible pour les plantes
    private Puit puit;		        // puits construit sur cette case
    private Ferme ferme;             // référence vers la ferme entière
    private Plante plante;  		// ref vers la plante si il y'a
    private Animal animal ;
    
    
    /**
     * -crée une nouvelle case à la position donnée.
     * -initialement, elle est vide : pas d'eau, pas de fertilisant, pas de puits.
     *
     * @param position la position de la case dans la grille
     * @param ferme la ferme à laquelle appartient cette case
     */
    public Case(Position position, Ferme ferme) 
    {
        if (position == null)
            throw new IllegalArgumentException("position nulle ?");
        if (ferme == null)
        	throw new IllegalArgumentException("ferme nulle ?");
        this.position = position;
        this.ferme = ferme;
        this.eauDispo = 0.0;
        this.fertilisant = 0.0;
        this.puit = null;
    }

    /**
     * @return la position (x, y) de la case
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return la quantité d'eau disponible sur la case
     */
    public double getEauDisponible() {
        return eauDispo;
    }

    /**
     * @return la quantité de fertilisant disponible sur la case
     */
    public double getFertilisant() {
        return fertilisant;
    }

    /**
     * @return vrai si un puits est construit sur cette case
     */
    public boolean aUnPuits() {
    	return puit != null;
    }

    /**
     * -ajoute de l’eau à la case.
     * @param quantite la quantité d’eau à ajouter (doit être > 0)
     */
    public void ajouterEau(double quantite) {
        if (quantite > 0) eauDispo += quantite;
    }

    /**
     * -ajoute du fertilisant à la case.
     * @param quantite la quantité à ajouter (doit être > 0)
     */
    public void ajouterFertilisant(double quantite) {
        if (quantite > 0) fertilisant += quantite;
    }

    /**
     * -construit un puits sur la case.
     */
    public void construirePuits() {
        this.puit = new Puit();
    }

    /**
     * -détruit le puits s'il y en a un sur la case.
     */
    public void detruirePuits() {
        this.puit = null;
    }

    /**
     * -vérifie si un puits est présent sur une des 4 cases voisines.
     * @return vrai si un puits est à côté
     */
    public boolean puitsAutour() 
    {
        int x = position.getX();
        int y = position.getY();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            Position voisine = Position.dansGrille(x + dir[0], y + dir[1]);
            if (voisine != null && ferme.getCase(voisine).aUnPuits()) {
                return true;
            }
        }
        return false;
    }


    /**
     * -vérifie si une plante sur cette case peut boire.
     * @return vrai si eau présente ou puits à côté
     */
    public boolean plantePeutBoire() {
        return eauDispo > 0 || puitsAutour();
    }

    /**
     * -vérifie si un animal peut boire sur cette case.
     * @return vrai si la case contient un puits
     */
    public boolean animalPeutBoire() {
        return aUnPuits();
    }

    
    
    /**
     * @return la plante  la case (ou null si vide)
     */
    public Plante getPlante() {
        return plante;
    }

    /**
     * -place une plante sur la case.
     * @param p la plante à placer
     */
    public void setPlante(Plante p) {
        this.plante = p;
    }

    /**
     * @return l’animal présent sur la case (ou null)
     */
    public Animal getAnimal() {
        return animal;
    }

    /**
     * -place un animal sur la case.
     * @param a l’animal à ajouter
     */
    public void setAnimal(Animal a) {
        this.animal = a;
    }
    
    
    

    /**
     * -affiche les informations de la case
     */

    @Override
    public String toString() 
    {
        return "Case " + position + " [Eau=" + eauDispo + "L, Fertilisant=" + fertilisant +
                "g, Puits=" + aUnPuits() + ", Plante=" + ((plante != null ? plante.getNom() : "aucune")
) +
                ", Animal=" + (animal != null ? animal.getNom() : "aucun") + "]";
    }
    
    
    /**
     * -met à jour la plante et l’animal présents sur cette case si ils sont vivants.
     * -à appeler une fois par jour.
     */
    public void maj(Saison saison) 
    {
        if (plante != null && plante.estVivante()) {
            plante.tourSuivant(this, saison);
        }
        if (animal != null && animal.estVivant()) {
            animal.tourSuivant(ferme, saison);
        }
    }
    
    /** Avoir l'eau disponible pour la case. */
    public double getEau() {
        return eauDispo;
    }
    
    



}