package mainSecondaire;



import gestion.Jeu;

import gestion.Magasin;
import modele.Ferme;
import modele.Position;


/**
 * Ce main exécute un test complet avant l'interface :
 * - Reproduction des animaux
 * - Gestion de la faim, soif
 * - Mort/vieillissement des entités
 * - État du stock (oeufs, lait, fertilisant)
 * - Rapport automatique via Jeu (vivants, morts, etc.)
 */
public class Main {

    public static void main(String[] args) {

        Ferme ferme = new Ferme();
        Magasin magasin = new Magasin(ferme);
        Jeu jeu = new Jeu(ferme, magasin);

        System.out.println("==== TEST COMPLET DE LA FONCTIONNALITÉ ====");
        System.out.println("Ajout initial de plusieurs animaux et plantes");

        // Placement initial (répartis sur la grille)
        magasin.acheterAnimal("Poule", new Position(1, 1));
        magasin.acheterAnimal("Poule", new Position(1, 2));
        magasin.acheterAnimal("Vache", new Position(2, 1));
        magasin.acheterAnimal("Vache", new Position(2, 2));
        magasin.acheterPlante("Tomate", new Position(3, 3));
        magasin.acheterPlante("Tomate", new Position(3, 4));
        magasin.acheterPlante("Ble", new Position(4, 3));
        magasin.acheterPlante("Ble", new Position(4, 4));
        magasin.acheterFertilisant(1000);
     // Fertilisant actif dès le départ
     magasin.utiliserFertilisant(new Position(1, 1), 50);
     magasin.utiliserFertilisant(new Position(1, 2), 50);
     magasin.utiliserFertilisant(new Position(2, 1), 50);
     magasin.utiliserFertilisant(new Position(2, 2), 50);
     magasin.utiliserFertilisant(new Position(3, 3), 50);
     magasin.utiliserFertilisant(new Position(3, 4), 50);
     magasin.utiliserFertilisant(new Position(4, 3), 50);
     magasin.utiliserFertilisant(new Position(4, 4), 50);


        // Construire quelques puits pour boire
        magasin.acheterPuits(new Position(0, 0));
        magasin.acheterPuits(new Position(5, 5));

        // Simulation complète de 120 jours = 4 saisons
        jeu.jouer(120);

        System.out.println("\n==== État final ====");
        System.out.printf("Budget restant : %.1f Y\n", ferme.getBudget());
        System.out.printf("Stock de fertilisant : %.1f g\n", magasin.getStockFertilisant());
        System.out.printf("Stock œufs : %.1f cal\n", jeu.getEntrepot().getCaloriesParType("Œufs"));
        System.out.printf("Stock lait  : %.1f cal\n", jeu.getEntrepot().getCaloriesParType("Lait"));
    }
}