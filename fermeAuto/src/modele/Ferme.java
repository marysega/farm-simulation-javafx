
package modele;

import entite.Animal;
import entite.Plante;


/**
 * -représente la ferme.
 * -contient une grille de cases et gère les modifications via méthodes contrôlées.
 */
public class Ferme 
{
    private static final int TAILLE = 6;
    private final Case[][] grille = new Case[TAILLE][TAILLE];
    private double budget = 15000;

    /**
     * -crée une nouvelle ferme avec grille initialisée.
     */
    public Ferme() 
    {
        for (int i = 0; i < TAILLE; i++) 
        {
            for (int j = 0; j < TAILLE; j++) 
            {
                grille[i][j] = new Case(new Position(i, j), this);
            }
        }
    }

    /**
     * -vérifie si une position est valide dans la grille.
     * @param p position à tester
     * @return true si 0 <= x,y < TAILLE
     */
    public boolean positionValide(Position p) 
    {
        return p != null
                && p.getX() >= 0 && p.getX() < TAILLE
                && p.getY() >= 0 && p.getY() < TAILLE;
    }

    /**
     * -récupère une copie de la grille interne. Toute modification du tableau retourné
     * n'affectera pas la grille de la ferme.
     * @return copie superficielle du tableau de Case
     */
    public Case[][] getGrille()
    {
        Case[][] clone = new Case[TAILLE][TAILLE];
        for (int i = 0; i < TAILLE; i++) {
            System.arraycopy(this.grille[i], 0, clone[i], 0, TAILLE);
        }
        return clone;
    }

    /**
     * -place un animal sur la grille à la position donnée.
     * @param pos position cible
     * @param a animal à ajouter
     * @throws IllegalArgumentException si position invalide ou déjà occupée
     */
    public void ajouterAnimal(Position pos, Animal a)
    {
        if (!positionValide(pos))
            throw new IllegalArgumentException("Position invalide : " + pos);
        Case c = getCaseInterne(pos);
        if (c.getAnimal() != null)
            throw new IllegalArgumentException("Case déjà occupée par un animal : " + pos);
        c.setAnimal(a);
    }

    /**
     * -place une plante sur la grille à la position donnée.
     * @param pos position cible
     * @param p plante à ajouter
     * @throws IllegalArgumentException si position invalide ou déjà occupée
     */
    public void ajouterPlante(Position pos, Plante p) 
    {
        if (!positionValide(pos))
            throw new IllegalArgumentException("Position invalide : " + pos);
        Case c = getCaseInterne(pos);
        if (c.getPlante() != null)
            throw new IllegalArgumentException("Case déjà occupée par une plante : " + pos);
        c.setPlante(p);
    }

    /**
     * -dépose du fertilisant sur une case.
     * @param pos position cible
     * @param quantiteGrammes quantité en grammes
     * @throws IllegalArgumentException si position invalide ou quantité <= 0
     */
    public void poserFertilisant(Position pos, double quantiteGrammes) {
        if (!positionValide(pos))
            throw new IllegalArgumentException("Position invalide : " + pos);
        if (quantiteGrammes <= 0)
            throw new IllegalArgumentException("Quantité de fertilisant doit être positive : " + quantiteGrammes);
        getCaseInterne(pos).ajouterFertilisant(quantiteGrammes);
    }

    /**
     * -construit un puits sur la case spécifiée.
     * @param pos position cible
     * @throws IllegalArgumentException si position invalide
     */
    public void construirePuits(Position pos) {
        if (!positionValide(pos))
            throw new IllegalArgumentException("Position invalide pour puits : " + pos);
        getCaseInterne(pos).construirePuits();
    }

    /**
     * -détruit le puits sur la case spécifiée.
     * @param pos position cible
     * @throws IllegalArgumentException si position invalide
     */
    public void detruirePuits(Position pos) {
        if (!positionValide(pos))
            throw new IllegalArgumentException("Position invalide pour destruction de puits : " + pos);
        getCaseInterne(pos).detruirePuits();
    }

    /**
     * -accès interne direct à une case (sans clone), pour usage interne Ferme.
     */
    private Case getCaseInterne(Position pos) {
        return grille[pos.getX()][pos.getY()];
    }

    /**
     * -retourne la case à la position donnée en vérifiant les limites.
     * @param pos position
     * @return case correspondante
     * @throws IllegalArgumentException si position invalide
     */
    public Case getCase(Position pos) {
        if (!positionValide(pos))
            throw new IllegalArgumentException("Position hors grille : " + pos);
        return getCaseInterne(pos);
    }

    /**
     * Ajoute une somme positive au budget.
     * @param montant montant à ajouter (>0)
     * @throws IllegalArgumentException si montant <=0
     */
    public void ajouterBudget(double montant) {
        if (montant <= 0) {
            System.out.printf("[Ferme] Aucune recette cette saison (%.1f Y)\\n", montant);
            return;
        }
        this.budget += montant;
    }


    /**
     * Retire une somme positive du budget.
     * @param montant montant à retirer (>0)
     * @throws IllegalArgumentException si montant <=0 ou budget insuffisant
     */
    public void retirerBudget(double montant) {
        if (montant <= 0)
            throw new IllegalArgumentException("Montant de retrait invalide : " + montant);
        if (budget < montant)
            throw new IllegalArgumentException("Budget insuffisant : " + budget + " < " + montant);
        budget -= montant;
    }
    /**
     * Retire de la grille les animaux et les plantes mortes
     */
    public void retirerEntitesMortes() 
    {
        for (int i = 0; i < TAILLE; i++) 
        {
            for (int j = 0; j < TAILLE; j++) 
            {
                Case c = grille[i][j];
                // retire l’animal mort
                if (c.getAnimal() != null && !c.getAnimal().estVivant()) {
                    c.setAnimal(null);
                }
                // retire la plante morte
                if (c.getPlante() != null && !c.getPlante().estVivante()) {
                    c.setPlante(null);
                }
            }
        }
    }



    /**
     * @return le budget actuel
     */
    public double getBudget() {
        return budget;
    }
    
    /**
     * @return la dimension n×n de la ferme
     */
    public int getTaille() {
        return TAILLE;
    }

}
