package entite;


import java.util.Properties;

import modele.Case;
import modele.Data;
import modele.Position;
import modele.Saison;

/**
 * Représente une plante abstraite placée sur une case de la ferme.
 * Elle consomme de l'eau et du fertilisant pour produire des calories.
 * Elle a un reservoir de capacité limité d'eau
 */
public abstract class Plante 
{

    protected String nom;
    protected int prix;
    protected double eauParJour;
    protected double capEau;
    protected double eauActuelle;
    protected double calInit;
    protected int fertParJour;
    protected double croissance;
    protected int malusSoif;
    protected int malusFaim;
    protected boolean vivante;
    protected Position position;
    

    /**
     * Crée une plante en chargeant ses caractéristiques depuis un fichier .properties que
     * on peut modifer facilement.
     *
     * @param nomFichier nom du fichier .properties (ex : "tomate.properties")
     * @param pos position de la plante dans la grille
     */
    public Plante(String nomFichier, Position pos) 
    {
        Properties p = Data.chargerFichier(nomFichier);

        this.nom = p.getProperty("nom");
        this.prix = Integer.parseInt(p.getProperty("prix"));
        this.eauParJour = Double.parseDouble(p.getProperty("eauParJour"));
        this.capEau = Double.parseDouble(p.getProperty("capEau"));
        this.eauActuelle = this.capEau;
        this.calInit = Double.parseDouble(p.getProperty("calInit"));
        this.fertParJour = Integer.parseInt(p.getProperty("fertParJour"));
     // croissance fixe de tomate ou croissance en pourcentage blé
        if (p.getProperty("croissance") != null) {
            this.croissance = Double.parseDouble(p.getProperty("croissance"));
        } else {
            this.croissance = Double.parseDouble(p.getProperty("croissancePourcent"));
        }


        
        this.malusSoif = Integer.parseInt(p.getProperty("malusSoif"));
        this.malusFaim = Integer.parseInt(p.getProperty("malusFaim"));

        this.vivante = true;
        this.position = pos;
    }

    /**
     * Vérifie si la plante est encore en vie.
     */
    public boolean estVivante() {
        return vivante;
    }

    /**
     * Vérifie si la plante est en manque d'eau.
     * @return vrai si son réservoir est à moitié vide ou moins
     */
    public boolean aSoif() {
        return eauActuelle <= capEau / 2;
    }

    /**
     * Donne de l’eau à la plante.
     * @param qte quantité d’eau donnée
     */
    public void boire(double qte) {
        eauActuelle = Math.min(capEau, eauActuelle + qte);
    }


    /**
     * Fait consommer de l’eau à la plante :
     * 1) Si un puits est à côté → réservoir plein
     * 2) Sinon si la case a au moins eauParJour → puise eauParJour dans la case et remplit son réservoir
     * 3) Sinon si la case a un peu d’eau → puise tout ce qui reste
     * 4) Sinon → décrémente son réservoir interne (sécheresse)
     * Meurt si son réservoir tombe à zéro.
     */
    public void consommerEau(Case c) 
    {
        if (c.puitsAutour()) {
            // eau infinie grâce au puits
            eauActuelle = capEau;
        } else if (c.getEauDisponible() >= eauParJour) {
            // boit eauParJour depuis la case
            c.ajouterEau(-eauParJour);
            eauActuelle = Math.min(capEau, eauActuelle + eauParJour);
        } else if (c.getEauDisponible() > 0) {
            // boit tout ce qui reste
            double dispo = c.getEauDisponible();
            c.ajouterEau(-dispo);
            eauActuelle = Math.min(capEau, eauActuelle + dispo);
        } else {
            // pas d'eau dans la case 
            eauActuelle -= eauParJour;
        }

        if (eauActuelle <= 0) {
            eauActuelle = 0;
            mourir();
        }
    }


    /**
     * Réduit les calories de la plante quand elle est mangée.
     * Si les calories tombent à 0, elle meurt.
     * @param quantiteDemande quantité de calories que l’animal veut prendre
     * @return quantité réellement fournie (≤ quantiteDemande et ≤ stock restant)
     */
    public double etreMangee(double quantiteDemande) 
    {
        double fournie = Math.min(quantiteDemande, calInit);
        calInit -= fournie;
        if (calInit <= 0) {
            calInit = 0;
            mourir();
        }
        return fournie;
    }


    /**
     * Fait mourir la plante.
     */
    public void mourir() {
        vivante = false;
        System.out.println(nom + " est mort à la position " + position +
                " (Eau=" + eauActuelle );
    }

    /**
     * @return la position actuelle
    */
    
    public Position getPosition() {
        return position;
    }

    /**
     * Méthode exécutée à chaque tour de simulation.
     * Chaque sous-classe la définit selon son type (tomate, blé…).
     *
     * @param c la case où se trouve la plante
     */
    

    
    
    public abstract void tourSuivant(Case c, Saison saison);
    
    
    
    
    /**
     * Retourne la quantité de calories actuellement stockée dans la plante.
     * @return calories disponibles
     */
    public double getCalories() {
        return calInit;
        
    }
    
    /**
     * @return le nom de la plante (chargé depuis le fichier .properties)
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * @return l'eau qu'a la plante (chargé depuis le fichier .properties)
     */
    
    public double getEauActuelle() { 
        return eauActuelle; 
    }
    /**
     * @return la capacité max de la plante (chargé depuis le fichier .properties)
     */

    public double getCapEau() { 
        return capEau; 
    }

}




