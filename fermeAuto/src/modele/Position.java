package modele;


/**
 * -représente une position (x, y) dans la grille de la ferme.
 * -permet de localiser n'importe quel éléments.
 */
public final class Position 
{

    private final int x; // la position en largeur
    private final int y; // la position en hauteur

    /**
     * -crée une position avec les coordonnées données.
     *
     * @param x doit être positif ou nul
     * @param y doit être positif ou nul
     * @throws IllegalArgumentException si x ou y est négatif
     */
    public Position(int x, int y)
    {
        if (x < 0 || y < 0) throw new IllegalArgumentException(String.format("coordonnées négatives : x=%d, y=%d ??", x, y));
        this.x = x;
        this.y = y;
    }
    
    /**
     * -crée une position uniquement si elle est dans la grille (0 à 5).
     * -retourne null si elle est hors limites.
     *
     * @param x coordonnée horizontale
     * @param y coordonnée verticale
     * @return la Position si elle est valide, sinon null
     */
    public static Position dansGrille(int x, int y) 
    {
        if (x >= 0 && x < 6 && y >= 0 && y < 6) {
            return new Position(x, y);
        }
        return null;
    }


    /**
     * -renvoie x
     * @return la coordonnée horizontale
     */
    public int getX() {
        return x;
    }

    /**
     * -renvoie y
     * @return la coordonnée verticale
     */
    public int getY() {
        return y;
    }

    /**
     * -vérifie si deux positions sont identiques.
     * @param obj autre position à comparer
     * @return vrai si les coordonnées sont les mêmes
     */
    
    public boolean equals(Object obj) 
    {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position o = (Position) obj;
        return this.x == o.x && this.y == o.y;
    }

    /**
     * -utilisé pour stocker dans des listes ou tableaux 
     */
    
    public int hashCode() {
        return 31 * x + y;
    }

    /**
     * -affiche les coordonnées comme (x, y)
     */
    
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

