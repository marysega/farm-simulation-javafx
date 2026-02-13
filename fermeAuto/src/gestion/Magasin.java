package gestion;

import java.util.Properties;

import modele.Data;
import java.util.Properties;
import entite.Animal;
import entite.Ble;
import entite.Plante;
import entite.Poule;
import entite.Tomate;
import entite.Vache;
import modele.Data;
import modele.Ferme;
import modele.Position;
import modele.Case;

/**
 * -classe Magasin : gère l'achat et la vente des éléments de la ferme (animaux, plantes, puits, fertilisants).
 * -capacité illimitée. Utilise le budget de la ferme pour valider les transactions.
 */
public class Magasin 
{
    private final Ferme ferme;
    private double stockFertilisant; // en grammes
    
    /** Coût du fertilisant : 2000 Y par kg (soit 2 Y par gramme) */
    private static final double PRIX_FERTILISANT_PAR_GRAMME = 2.0;

    /**
     * -construit un magasin lié à une ferme existante.
     * @param ferme la ferme sur laquelle porter les achats et ventes
     */
    public Magasin(Ferme ferme) 
    {
        if (ferme == null) throw new IllegalArgumentException("Ferme non fournie à Magasin");
        this.ferme = ferme;
        this.stockFertilisant = 0;
    }
    
    /**
     * -achète du fertilisant et l'ajoute au stock interne.
     * -il ne pose pas le fertilisant
     * @param quantiteGrammes quantité désirée en grammes
     */
    public void acheterFertilisant(double quantiteGrammes) 
    {
        if (quantiteGrammes <= 0) {
            System.out.println("Quantité de fertilisant invalide");
            return;
        }
        double cout = quantiteGrammes * PRIX_FERTILISANT_PAR_GRAMME;
        if (ferme.getBudget() < cout) {
            System.out.println("Budget insuffisant pour acheter " + quantiteGrammes + "g de fertilisant.");
            return;
        }
        ferme.retirerBudget(cout);
        stockFertilisant += quantiteGrammes;
        System.out.printf("Achat de %.0f g de fertilisant pour %.1f Y. Stock fertilisant = %.0f g.\n",
            quantiteGrammes, cout, stockFertilisant);
    }

    /**
     * -achète un animal et le place sur la grille si le budget le permet.
     * @param espece nom de l'espèce ("Poule" ou "Vache")
     * @param pos position d'installation de l'animal
     */
    public void acheterAnimal(String espece, Position pos) {
        String fichier = espece.toLowerCase() + ".properties";
        Properties props = Data.chargerFichier(fichier);
        int prix = Integer.parseInt(props.getProperty("prix", "0"));
        if (ferme.getBudget() < prix) {
            System.out.println("Budget insuffisant pour acheter une " + espece);
            return;
        }
        Animal a;
        switch (espece.toLowerCase()) {
            case "poule": a = new Poule(pos); break;
            case "vache": a = new Vache(pos); break;
            default:
                throw new IllegalArgumentException("Espèce non reconnue : " + espece);
        }
        try {
            // Un animal peut partager la case avec une plante ou un puits
            Case c = ferme.getCase(pos);
            if (c.getAnimal() != null) {
                throw new IllegalArgumentException("Case déjà occupée par un autre animal : " + pos);
            }
            ferme.ajouterAnimal(pos, a);
            ferme.retirerBudget(prix);
            System.out.println(espece + " achetée et placée en " + pos + ". Budget restant : " + ferme.getBudget() + " Y");
        } catch (IllegalArgumentException ex) {
            System.out.println("Impossible de placer l'animal : " + ex.getMessage());
        }
    }
    
    /**
     * -achète une plante et la place sur la grille si le budget le permet.
     * @param type nom du type de plante ("Tomate" ou "Ble")
     * @param pos position d'installation de la plante
     * @throws IllegalArgumentException si le type est invalide ou si la position est hors-grille
     */
    public void acheterPlante(String type, Position pos) 
    {
        String fichier = type.toLowerCase() + ".properties";
        Properties props = Data.chargerFichier(fichier);
        int prix = Integer.parseInt(props.getProperty("prix", "0"));
        if (ferme.getBudget() < prix) {
            System.out.println("Budget insuffisant pour acheter du " + type);
            return;
        }
        // Vérifier l'occupation de la case :
        Case c = ferme.getCase(pos);
        if (c.getPlante() != null) {
            System.out.println("Impossible de placer la plante : une autre plante est déjà présente à " + pos);
            return;
        }
        // Seule combinaison autorisée avec plante : présence d'un animal
        if (c.aUnPuits() && c.getAnimal() == null) {
            System.out.println("Impossible de placer la plante : case occupée par un puits sans animal à " + pos);
            return;
        }
        Plante p;
        switch (type.toLowerCase()) {
            case "tomate": p = new Tomate(pos); break;
            case "ble": p = new Ble(pos); break;
            default:
                throw new IllegalArgumentException("Type de plante non reconnu : " + type);
        }
        try {
            ferme.ajouterPlante(pos, p);
            ferme.retirerBudget(prix);
            System.out.println(type + " achetée et placée en " + pos + ". Budget restant : " + ferme.getBudget() + " Y");
        } catch (IllegalArgumentException ex) {
            System.out.println("Impossible de placer la plante : " + ex.getMessage());
        }
    }

    /**
     * -construit un puits sur la position donnée via la ferme si le budget est suffisant.
     * -le prix est de 3000Y
     * @param pos position où construire le puits
     */
    public void acheterPuits(Position pos) {
        int prix = 3000;
        if (ferme.getBudget() < prix) {
            System.out.println("Pas assez de budget pour un puits");
            return;
        }
        // Vérifier l'occupation de la case :
        Case c = ferme.getCase(pos);
        if (c.aUnPuits()) {
            System.out.println("Impossible de construire un puits : un puits existe déjà à " + pos);
            return;
        }
        // Seule combinaison autorisée avec puits : présence d'un animal
        if (c.getPlante() != null && c.getAnimal() == null) {
            System.out.println("Impossible de construire un puits : case occupée par une plante sans animal à " + pos);
            return;
        }
        try {
            ferme.retirerBudget(prix);
            ferme.construirePuits(pos);
            System.out.println("Puits construit en " + pos);
        } catch (Exception e) {
            System.out.println("Échec construction : " + e.getMessage());
        }
    }

    /**
     * -détruit un puits sur la position donnée via la ferme si le budget est suffisant.
     * le prix est de 200Y
     * @param pos position du puits à détruire
     */
    public void detruirePuits(Position pos) 
    {
        int prix = 200;
        if (ferme.getBudget() < prix) {
            System.out.println("Pas assez de budget pour détruire puits");
            return;
        }
        ferme.retirerBudget(prix);
        try {
            ferme.detruirePuits(pos);
            System.out.println("Puits détruit en " + pos);
        } catch(Exception e) {
            System.out.println("Échec destruction : " + e.getMessage());
        }
    }

    /**
     * -dépose du fertilisant acheté sur la grille en passant par Ferme.
     * -on décrèmente donc le stock de fertilisant acheté en l'utilisant.
     */
    public void utiliserFertilisant(Position pos, double quantiteGrammes) 
    {
        if (quantiteGrammes <= 0 || quantiteGrammes > stockFertilisant) {
            System.out.println("Quantité de fertilisant non disponible ou invalide");
            return;
        }
        try {
            ferme.poserFertilisant(pos, quantiteGrammes);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur lors du dépôt de fertilisant : " + e.getMessage());
            return;
        }
        stockFertilisant -= quantiteGrammes;
        System.out.printf("Posé %.0f g de fertilisant en %s. Stock restant = %.0f g.%n",
            quantiteGrammes, pos, stockFertilisant);
    }
    
    /**
     * -retourne le stock de fertilisant.
     */
    public double getStockFertilisant() {
        return stockFertilisant;
    }
    
    /** Lit le prix de l’animal (depuis "poule.properties" ou "vache.properties"). */
    public double getPrixAnimal(String nomAnimal) {
        Properties props = Data.chargerFichier(nomAnimal.toLowerCase() + ".properties");
        return Double.parseDouble(props.getProperty("prix", "0"));
    }

    /** Lit le prix de la plante (depuis "tomate.properties" ou "ble.properties"). */
    public double getPrixPlante(String nomPlante) 
    {
        Properties props = Data.chargerFichier(nomPlante.toLowerCase() + ".properties");
        return Double.parseDouble(props.getProperty("prix", "0"));
    }

    /** Renvoie le prix au gramme (2 Y/g). */
    public double getPrixFertilisant(int quantite) 
    {
        return quantite * 2.0;
    }

    /** Renvoie le prix de construction d’un puits (3000 Y). */
    public double getPrixPuit() {
        return 3000.0;
    }

    /** Renvoie le coût de destruction d’un puits (200 Y). */
    public double getPrixDestructionPuit() {
        return 200.0;
    }
}


