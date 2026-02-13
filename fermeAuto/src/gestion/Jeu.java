package gestion;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entite.Animal;
import entite.Plante;
import modele.Case;
import modele.Entrepot;
import modele.Ferme;
import modele.Position;
import modele.Saison;

/**

 * -gère la simulation de la ferme avec rapports détaillés.
 * 
 * -collecte quotidienne des productions animales.
 *  ;suivi des naissances et décès des plantes et animaux.
 *  ;bilan quotidien et bilan de saison.
 * 
 */
 
public class Jeu {
    private final Ferme ferme;
    private final Entrepot entrepot;
    private final Magasin magasin;
    private int jour;
    private Saison saison;
    /** Liste des espèces présentes au lancement de la simulation */
    private final Set<String> typesAnimauxInit;
    private final Set<String> typesPlantesInit;


 // Statistiques journalières
    private Map<String, Double> productionJournalieres;
    private Map<String, Integer> decesAnimauxJournalier;
    private Map<String, Integer> animauxVivantsJournalier;
    private Map<String, Integer> decesPlantesJournalier;
    private Map<String, Integer> plantesVivantesJournalier;

    // Statistiques saisonnières (cumul des ventes)
    private Map<String, Double> ventesAnimauxSaison;
    private Map<String, Double> ventesPlantesSaison;

    // Effectifs au début de chaque période pour calculer les décès
    private Map<String, Integer> effectifsAnimauxPrecedents;
    private Map<String, Integer> effectifsPlantesPrecedents;
 // Pour que le repositionnement des animaux vivants à la fin e chaque saison
    private List<Animal> animauxARemettre = new ArrayList<>();

    
    /**
     * -constructeur : initialise la simulation du jeu avec une ferme existante.
     *
     * @param ferme la ferme à simuler
     * */

    public Jeu(Ferme ferme, Magasin magasin)  
    {
        this.ferme = ferme;
        this.entrepot = new Entrepot();
        this.magasin = magasin; 
        this.jour = 1;
        this.saison = Saison.AUTOMNE;

        this.productionJournalieres = new HashMap<>();
        this.decesAnimauxJournalier = new HashMap<>();
        this.animauxVivantsJournalier = new HashMap<>();
        this.decesPlantesJournalier = new HashMap<>();
        this.plantesVivantesJournalier = new HashMap<>();
        this.ventesAnimauxSaison = new HashMap<>();
        this.ventesPlantesSaison = new HashMap<>();

        // Comptage initial pour détecter les décès à J1
        this.effectifsAnimauxPrecedents = compterAnimauxParType();
        this.effectifsPlantesPrecedents = compterPlantesParType();
     // À la création, on recopie la liste des clefs (animaux et plantes)  
        this.typesAnimauxInit  = new HashSet<>(this.effectifsAnimauxPrecedents.keySet());
        this.typesPlantesInit  = new HashSet<>(this.effectifsPlantesPrecedents.keySet());
        

    }


    /**
     * -démarre la boucle de simulation pour un nombre de jours donné.
     * -affiche un rapport quotidien et un bilan de fin de saison.
     *
     * @param nbJours nombre de jours à simuler
     */
    
    public void jouer(int nbJours) 
    {
        for (int i = 0; i < nbJours; i++) {
            initStatsJournalieres();
            System.out.printf("--- Jour %d/30 (%s) ---%n", getJourDansSaison(), saison);

            majSaison();
            fairePluie();

            // Mise à jour de chaque case
            for (Case[] ligne : ferme.getGrille()) {
                for (Case c : ligne) {
                    c.maj(saison);
                }
            }

            // Collecte de la production animale du jour
            collecterProductionAnimale();

            // Calcul des décès et vivants par type
            Map<String, Integer> Animaux = compterAnimauxParType();
            Map<String, Integer> Plantes = compterPlantesParType();
            miseAJourDecesEtVivants(Animaux, Plantes);
            ferme.retirerEntitesMortes();

            // Affichage du rapport quotidien
            afficherRapportJournalier();

            // Bilan de fin de saison
            if (jour % 30 == 0) {
               // 1) Avant toute vente : on retire du modèle les entités mortes
              ferme.retirerEntitesMortes();
   
                // 2) On replace d'abord les animaux vivants
                replacerAnimauxVivant();
                System.out.println("\n*** Bilan de la saison " + saison + " ***");
                cloturerSaison();
                afficherResumeSaison();
                ventesAnimauxSaison.clear();
                ventesPlantesSaison.clear();
            }

            // Début de saison
            if (jour % 30 == 1) {
                replacerAnimauxVivant();  // repositionnement des animaux vivants
                reproduireDebutSaison();  // reproduction animale automatique
            }

            jour++;
        }
    }

    private void reproduireDebutSaison() {
        for (Case[] ligne : ferme.getGrille()) {
            for (Case c : ligne) {
                Animal a = c.getAnimal();
                if (a != null && a.estVivant()) {
                    a.essayerReproduction(ferme);
                }
            }
        }
    }


    /**
     * -réinitialise les statistiques journalières avant chaque nouveau jour.
     */
    private void initStatsJournalieres() 
    {
        productionJournalieres.clear();
        decesAnimauxJournalier.clear();
        animauxVivantsJournalier.clear();
        decesPlantesJournalier.clear();
        plantesVivantesJournalier.clear();
    }

    
    /**
     * -met à jour la saison en fonction du numéro de jour écoulé.
     */
    private void majSaison() 
    {
        switch ((jour - 1) / 30 % 4) {
            case 0 -> saison = Saison.AUTOMNE;
            case 1 -> saison = Saison.HIVER;
            case 2 -> saison = Saison.PRINTEMPS;
            default -> saison = Saison.ETE;
        }
    }

    /**
     * -applique les effets météorologiques (pluie) selon la saison.
     * -chaque case sans puits reçoit de l'eau avec une probabilité donnée.
     */
    private void fairePluie() 
    {
        double proba, pluie;
        switch (saison) {
            case AUTOMNE -> { proba = 0.8; pluie = 0.2; }
            case HIVER ->   { proba = 0.4; pluie = 0.2; }
            case PRINTEMPS -> { proba = 0.6; pluie = 0.1; }
            default ->       { proba = 0.05; pluie = 0.1; }
        }
        if (Math.random() < proba) {
            System.out.println("Pluie ! +" + pluie + " L sur toutes les cases sans puits.");
            for (Case[] ligne : ferme.getGrille()) {
                for (Case c : ligne) {
                    if (!c.aUnPuits()) {
                        c.ajouterEau(pluie);
                    }
                }
            }
        }
    }

    /**
     * -parcourt toutes les cases et collecte la production des animaux vivants.
     * -met à jour l'Entrepôt et les statistiques journalières et saisonnières.
     * -affiche clairement "Œufs" et "Lait" plutôt que les espèces pour qu'on se retrouve mieux.
     */
    private void collecterProductionAnimale() 
    {
        for (Case[] ligne : ferme.getGrille()) {
            for (Case c : ligne) {
                Animal a = c.getAnimal();
                if (a != null && a.estVivant()) {
                    double prod = a.produire();
                    if (prod > 0) {
                        // Déterminer le type de produit (œufs ou lait)
                        String espece = a.getNom();
                        String produit;
                        if ("Poule".equalsIgnoreCase(espece)) {
                            produit = "Œufs";
                        } else if ("Vache".equalsIgnoreCase(espece)) {
                            produit = "Lait";
                        } else {
                            produit = espece; // fallback
                        }
                        entrepot.ajouter(produit, prod);
                        productionJournalieres.merge(produit, prod, Double::sum);
                        ventesAnimauxSaison.merge(produit, prod, Double::sum);
                    }
                }
            }
        }
    }

    /**
     * -compte les animaux vivants par type dans la ferme.
     *
     * @return map type -> nombre d'animaux vivants
     */
    private Map<String, Integer> compterAnimauxParType() {
        Map<String, Integer> comptes = new HashMap<>();
        for (Case[] ligne : ferme.getGrille()) {
            for (Case c : ligne) {
                Animal a = c.getAnimal();
                if (a != null && a.estVivant()) {
                    comptes.merge(a.getNom(), 1, Integer::sum);
                }
            }
        }
        return comptes;
    }

    /**
     * -compte les plantes vivantes par type dans la ferme.
     *
     * @return map type -> nombre de plantes vivantes
     */
    private Map<String, Integer> compterPlantesParType() {
        Map<String, Integer> comptes = new HashMap<>();
        for (Case[] ligne : ferme.getGrille()) {
            for (Case c : ligne) {
                Plante p = c.getPlante();
                if (p != null && p.estVivante()) {
                    comptes.merge(p.getNom(), 1, Integer::sum);
                }
            }
        }
        return comptes;
    }

    /**
     * -met à jour les statistiques de décès et de vivants selon les effectifs précédents.
     *
     * @param Animaux effectifs actuels d'animaux par type
     * @param Plantes effectifs actuels de plantes par type
     */
    private void miseAJourDecesEtVivants(Map<String, Integer> Animaux,
            Map<String, Integer> Plantes) {

    			for (String type : Animaux.keySet()) {
    				int prev = effectifsAnimauxPrecedents.getOrDefault(type, 0);
    				int curr = Animaux.get(type);
    				int deces = Math.max(0, prev - curr);
    				decesAnimauxJournalier.put(type, deces);
    				animauxVivantsJournalier.put(type, curr);
    			}

    			for (String type : Plantes.keySet()) {
    				int prev = effectifsPlantesPrecedents.getOrDefault(type, 0);
    				int curr = Plantes.get(type);
    				int deces = Math.max(0, prev - curr);
    				decesPlantesJournalier.put(type, deces);
    				plantesVivantesJournalier.put(type, curr);
    			}

    			// Mise à jour des effectifs pour la saison suivante
    			effectifsAnimauxPrecedents = Animaux;
    			effectifsPlantesPrecedents = Plantes;

    			// Affichage clair
    			System.out.println("----- Rapport journalier -----");
    			Animaux.forEach((type, nb) -> 
    			{
    				int morts = decesAnimauxJournalier.getOrDefault(type, 0);
    				System.out.printf("Animal: %s | Vivants: %d | Morts: %d%n", type, nb, morts);
    			});
    			Plantes.forEach((type, nb) -> {
    				int morts = decesPlantesJournalier.getOrDefault(type, 0);
    				System.out.printf("Plante: %s | Vivantes: %d | Mortes: %d%n", type, nb, morts);
    			});
    }


    /**
     * -affiche le rapport détaillé de la journée :
     * productions, vivants et décès pour chaque type
     * et le stock de l'entrepot(lait oeufs fertilisants).
     */
    private void afficherRapportJournalier() 
    {
    	
    	System.out.println("Production du jour :");
    	for (String type : productionJournalieres.keySet()) {
    	    double cal = productionJournalieres.get(type);
    	    System.out.printf("  - %s : %.1f cal%n", type, cal);
    	}

    	System.out.println("État des animaux :");
    	for (String type : animauxVivantsJournalier.keySet()) {
    	    int vivants = animauxVivantsJournalier.get(type);
    	    int deces = decesAnimauxJournalier.getOrDefault(type, 0);
    	    System.out.printf("  - %s : %d vivants, %d morts%n", type, vivants, deces);
    	}

    	System.out.println("État des plantes :");
    	for (String type : plantesVivantesJournalier.keySet()) {
    	    int vivantes = plantesVivantesJournalier.get(type);
    	    int mortes = decesPlantesJournalier.getOrDefault(type, 0);
    	    System.out.printf("  - %s : %d vivantes, %d mortes%n", type, vivantes, mortes);
    	}


        System.out.printf("Stock de fertilisant (g) : %.1f%n", magasin.getStockFertilisant());
        
        System.out.printf("Stock Entrepôt (cal) : Œufs = %.1f, Lait = %.1f%n",
        	    entrepot.getCaloriesParType("Œufs"),
        	    entrepot.getCaloriesParType("Lait")
        	);

    }

    /**
     * -traite la fin de saison : récolte des plantes, vente des productions et remise à zéro des stocks.
     */
    private void cloturerSaison() 
    {
    	animauxARemettre.clear(); // important pour éviter les doublons

    	for (Case[] ligne : ferme.getGrille()) {
    	    for (Case c : ligne) {
    	        Animal a = c.getAnimal();
    	        if (a != null) {
    	            if (a.estVivant() && a.getAge() < a.getEsperanceVie() * 30) {
    	                animauxARemettre.add(a);
    	            }
    	            c.setAnimal(null); // vide dans tous les cas
    	        }

    	        Plante p = c.getPlante();
    	        if (p != null && p.estVivante()) {
    	            entrepot.ajouter(p.getNom(), p.getCalories());
    	            ventesPlantesSaison.merge(p.getNom(), p.getCalories(), Double::sum);
    	            c.setPlante(null);
    	        }
    	    }
    	}

        // Vente et mise à jour du budget
        double total = entrepot.getTotal();
        System.out.printf("Ventes saisonnières totales : %.1f Y%n", total);
        ferme.ajouterBudget(total);
        entrepot.vider();
    }

    /**
     * -affiche le résumé des ventes de la saison par type.
     */
    private void afficherResumeSaison() {
        System.out.println("Ventes plantes cette saison :");
        ventesPlantesSaison.forEach((type, cal) ->
            System.out.printf("  - %s : %.1f cal vendues%n", type, cal)
        );
        System.out.println("Ventes animaux cette saison :");
        ventesAnimauxSaison.forEach((type, cal) ->
            System.out.printf("  - %s : %.1f cal vendus%n", type, cal)
        );
    }
    
    
    /**
     * @return une copie de l'entrepôt consolidant la production (œufs, lait, plantes)
     * -tout appel ne touchera pas au vrai stock 
     */
    public Entrepot getEntrepot() 
    {
    	  return new Entrepot(entrepot); // constructeur qui clone la map interne
    	}
    
    /**
     * -replace les animaux , utiliser en début de saison pour ceux qui sont vivants
     */
    private void replacerAnimauxVivant() 
    {
        Collections.shuffle(animauxARemettre); // pour randomiser le placement
        for (Animal a : animauxARemettre) {
            boolean placé = false;
            for (int i = 0; i < 6 && !placé; i++) {
                for (int j = 0; j < 6 && !placé; j++) {
                    Position pos = new Position(i, j);
                    Case c = ferme.getCase(pos);
                    if (c.getAnimal() == null) {
                        c.setAnimal(a);
                        a.setPosition(pos);
                        placé = true;
                    }
                }
            }
        }
        System.out.println("Repositionnement de " + animauxARemettre.size() + " animaux vivants.");

        animauxARemettre.clear(); // une fois replacés
    }

    public int getJourDansSaison() {
        return (jour - 1) % 30 + 1;
    }
    
 
    public Saison getSaison() {
        return saison;
    }

    /**  
     * Expose le décompte d’animaux vivants par type à la fin du dernier jour simulé  
     */  
    public Map<String, Integer> getAnimauxVivantsJournalier() {  
        return Collections.unmodifiableMap(animauxVivantsJournalier);  
    }  

    /**  
     * Expose le décompte de décès d’animaux par type à la fin du dernier jour simulé  
     */  
    public Map<String, Integer> getDecesAnimauxJournalier() {  
        return Collections.unmodifiableMap(decesAnimauxJournalier);  
    }  

    /**  
     * Expose le décompte de plantes vivantes par type à la fin du dernier jour simulé  
     */  
    public Map<String, Integer> getPlantesVivantesJournalier() {  
        return Collections.unmodifiableMap(plantesVivantesJournalier);  
    }  

    /**  
     * Expose le décompte de décès de plantes par type à la fin du dernier jour simulé  
     */  
    public Map<String, Integer> getDecesPlantesJournalier() {  
        return Collections.unmodifiableMap(decesPlantesJournalier);  
    }  

    /**  
     * Expose le nombre d’animaux vivants par type aujourd’hui (avant récolte).  
     */  
    public Map<String, Integer> getAnimauxParType() {  
        return compterAnimauxParType();  
    }  

    /**  
     * Expose le nombre de plantes vivantes par type aujourd’hui (avant récolte).  
     */  
    public Map<String, Integer> getPlantesParType() {  
        return compterPlantesParType();  
    }  



}
