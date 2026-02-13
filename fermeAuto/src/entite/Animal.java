package entite;

import java.util.Properties;
import java.util.Random;

import modele.Case;
import modele.Data;
import modele.Ferme;
import modele.Position;
import modele.Saison;

import java.util.ArrayList;

/**
 * Classe abstraite représentant un animal dans la ferme.
 * Un animal consomme de l'eau et de la nourriture, peut produire un aliment,
 * se déplacer, se reproduire ou mourir.
 */
public abstract class Animal 
{

    protected String nom;
    protected int prix;

    protected double eauParJour;
    protected double capEau;
    protected double eauActuelle;

    protected double nourritureParJour;
    protected double capNourriture;
    protected double nourritureActuelle;

    protected int malusSoif;
    protected int malusFaim;

    protected double production;
    protected boolean vivant = true;
    protected boolean estBebe = false;
    protected boolean aReproduit = false;

    protected int fertilisantParJour;
    protected int age = 0;
    protected int esperanceVie;

    protected double probaReproduction;
    
    protected Position position;
    protected double baseNourritureParJour;
    protected double baseEauParJour;
    protected Ferme ferme; 

    /**
     * Crée un animal en chargeant ses caractéristiques depuis un fichier de propriétés.
     *
     * @param fichier nom du fichier .properties (ex : "poule.properties")
     * @param pos position initiale de l'animal dans la grille
     */
    protected Animal(String fichier, Position pos) 
    {
        Properties p = Data.chargerFichier(fichier);

        this.nom = p.getProperty("nom");
        this.prix = Integer.parseInt(p.getProperty("prix"));
        this.eauParJour = Double.parseDouble(p.getProperty("eauParJour"));
        this.baseEauParJour = this.eauParJour;
        
        this.capEau = Double.parseDouble(p.getProperty("capEau"));
        this.eauActuelle = this.capEau;

        this.nourritureParJour = Double.parseDouble(p.getProperty("nourritureParJour"));
        this.baseNourritureParJour = this.nourritureParJour;
        
        this.capNourriture = Double.parseDouble(p.getProperty("capNourriture"));
        this.nourritureActuelle = this.capNourriture;

        this.production = Double.parseDouble(p.getProperty("production"));
        this.fertilisantParJour = Integer.parseInt(p.getProperty("fertilisantParJour"));

        this.malusSoif = Integer.parseInt(p.getProperty("malusSoif"));
        this.malusFaim = Integer.parseInt(p.getProperty("malusFaim"));

        this.probaReproduction = Double.parseDouble(p.getProperty("probaReproduction"));
        this.esperanceVie = Integer.parseInt(p.getProperty("esperanceVie"));

        this.position = pos;
    }

    /**
     * Vérifie si l'animal est encore en vie.
     *
     * @return vrai si l'animal est vivant, faux sinon
     */
    public boolean estVivant() {
        return vivant;
    }

    /**
     * Vérifie si l'animal manque d'eau.
     *
     * @return vrai si le niveau d'eau est inférieur ou égal à la moitié de la capacité
     */
    public boolean aSoif() 
    {
        return eauActuelle <= capEau / 2;
    }

    /**
     * Vérifie si l'animal manque de nourriture.
     *
     * @return vrai si le niveau de nourriture est inférieur ou égal à la moitié de la capacité
     */
    public boolean aFaim() 
    {
        return nourritureActuelle <= capNourriture / 2;
    }

    /**
     * Tue l'animal et affiche un message d'état.
     */
    public void mourir() 
    {
        vivant = false;
        System.out.println(nom + " est mort à la position " + position +
                " (Eau=" + eauActuelle + " / Nourriture=" + nourritureActuelle + ")");
    }
    
    /**
     * Fait consommer de l'eau à l'animal.
     * Il boit au puits si possible, sinon utilise son réservoir.
     * Meurt si son réservoir tombe à zéro.
     *
     * @param c case où se trouve l'animal
     */
    public void consommerEau(Case c) 
    {
        if (c.animalPeutBoire()) {
            eauActuelle = capEau;
        } else {
            eauActuelle -= eauParJour;
        }

        if (eauActuelle <= 0) {
            eauActuelle = 0;
            mourir();
        }
    }

    /**
     * Fait consommer de la nourriture à l'animal.
     * Il mange une plante si disponible et vivante, sinon son réservoir diminue.
     * Meurt si le niveau atteint zéro.
     *
     * @param c case où se trouve l'animal
     */
    public void consommerNourriture(Case c) {
        Plante plante = c.getPlante();
        if (plante != null && plante.estVivante()) {
            double manque = capNourriture - nourritureActuelle;
            double mange = plante.etreMangee(manque);
            nourritureActuelle += mange;
        } else {
            nourritureActuelle -= nourritureParJour;
        }

        if (nourritureActuelle <= 0) {
            nourritureActuelle = 0;
            mourir();
        }
    }

    /**
     * Dépose une quantité fixe de fertilisant sur la case donnée.
     *
     * @param c case où déposer le fertilisant
     */
    public void deposerFertilisant(Case c) {
        c.ajouterFertilisant(fertilisantParJour);
    }
    
    /**
     * Calcule la production quotidienne de l'animal (œufs, lait...).
     * Si l'animal a faim ou soif, la production est réduite selon les malus.
     * Un bébé ne produit rien.
     *
     * @return quantité produite ce jour
     */
    public double produire() {
        if (estBebe) return 0;

        double resultat = production;
        if (aSoif()) resultat -= resultat * malusSoif / 100.0;
        if (aFaim()) resultat -= resultat * malusFaim / 100.0;
        return Math.max(0, resultat);
    }
    
    /**
     * Déplace l'animal vers une case voisine vide ou reste sur place.
     * Le déplacement est aléatoire parmi les directions cardinales.
     *
     * @param ferme ferme pour accéder aux cases
     */
    public void seDeplacer(Ferme ferme) {
        ArrayList<Position> options = new ArrayList<>();
        options.add(position);

        int x = position.getX();
        int y = position.getY();
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};

        for (int[] d : directions) {
            Position voisine = Position.dansGrille(x + d[0], y + d[1]);
            if (voisine != null && ferme.getCase(voisine).getAnimal() == null) {
                options.add(voisine);
            }
        }

        Position nouvellePos = options.get(new Random().nextInt(options.size()));
        if (!nouvellePos.equals(position)) {
            ferme.getCase(position).setAnimal(null);
            ferme.getCase(nouvellePos).setAnimal(this);
            setPosition(nouvellePos);
        }
    }
    
    /**
     * Effectue toutes les actions journalières de l'animal : boire, manger,
     * produire, vieillir et se déplacer. Spécifique à chaque espèce.
     *
     * @param f ferme contenant les cases et autres animaux
     * @param saison saison actuelle affectant le comportement
     */
    public abstract void tourSuivant(Ferme f, Saison saison);

    /**
     * Vérifie si l'animal peut se reproduire avec un autre.
     *
     * @param autre autre animal à tester
     * @return vrai si même espèce et ni l'un ni l'autre n'ont déjà reproduit
     */
    public boolean peutSeReproduireAvec(Animal autre) {
        return autre != null && autre.getClass() == this.getClass() && !this.aReproduit && !autre.aReproduit;
    }
    
    /**
     * Crée un nouvel animal de même type en tant que bébé.
     *
     * @param pos position du nouveau-né
     * @return instance bébé de l'animal
     */
    public abstract Animal creerBebe(Position pos);
    
    /**
     * Retourne la position actuelle de l'animal sur la grille.
     *
     * @return position actuelle
     */
    public Position getPosition() {
        return position;
    }
    
    /**
     * Met à jour la position de l'animal.
     *
     * @param p nouvelle position
     */
    public void setPosition(Position p) {
        this.position = p;
    }
    
    /**
     * Tente une reproduction avec un voisin de même espèce si possible.
     * Si la reproduction réussit, un bébé est placé sur une case libre.
     *
     * @param ferme ferme entière
     */
    public void essayerReproduction(Ferme ferme) {
        if (estBebe || aReproduit || !vivant) return;

        int x = position.getX();
        int y = position.getY();
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};

        for (int[] dir : directions) {
            Position voisine = Position.dansGrille(x + dir[0], y + dir[1]);
            if (voisine != null) {
                Case caseVoisine = ferme.getCase(voisine);
                Animal autre = caseVoisine.getAnimal();

                if (peutSeReproduireAvec(autre)) {
                    ArrayList<Position> casesLibres = new ArrayList<>();

                    for (int[] d : directions) {
                        Position autour = Position.dansGrille(x + d[0], y + d[1]);
                        if (autour != null && ferme.getCase(autour).getAnimal() == null) {
                            casesLibres.add(autour);
                        }
                        Position autour2 = Position.dansGrille(voisine.getX() + d[0], voisine.getY() + d[1]);
                        if (autour2 != null && ferme.getCase(autour2).getAnimal() == null) {
                            casesLibres.add(autour2);
                        }
                    }

                    if (!casesLibres.isEmpty()) {
                        double chance = Math.random();
                        if (chance < probaReproduction) {
                            Position cible = casesLibres.get(new Random().nextInt(casesLibres.size()));
                            Animal bebe = creerBebe(cible);
                            ferme.getCase(cible).setAnimal(bebe);

                            this.aReproduit = true;
                            autre.aReproduit = true;

                            System.out.println(nom + " et " + autre.nom + " ont eu un bébé en " + cible);
                        }
                    }
                    return;
                }
            }
        }
    }
    
    /**
     * Retourne l'âge actuel de l'animal en jours.
     *
     * @return âge en nombre de jours
     */
    public int getAge() {
        return age;
    }

    /**
     * Retourne l'espérance de vie maximale de l'animal en saisons.
     *
     * @return espérance de vie en saisons
     */
    public int getEsperanceVie() {
        return esperanceVie;
    }
    
    /**
     * Retourne le nom de l'animal, chargé depuis le fichier de propriétés.
     *
     * @return nom de l'animal
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * Retourne la quantité d'eau actuelle dans le réservoir.
     *
     * @return niveau d'eau actuel
     */
    public double getEauActuelle() {
        return eauActuelle;
    }

    /**
     * Retourne la capacité maximale d'eau du réservoir.
     *
     * @return capacité d'eau
     */
    public double getCapEau() {
        return capEau;
    }

    /**
     * Retourne la quantité de nourriture actuelle dans le réservoir.
     *
     * @return niveau de nourriture actuel
     */
    public double getNourritureActuelle() {
        return nourritureActuelle;
    }

    /**
     * Retourne la capacité maximale de nourriture du réservoir.
     *
     * @return capacité de nourriture
     */
    public double getCapNourriture() {
        return capNourriture;
    }

}


