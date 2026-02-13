package modele;


import java.util.HashMap;
import java.util.Map;

/**
 * -Entrepôt central qui stocke toutes les productions de nourriture par type (œuf, lait, blé, tomate, etc.) 
 * -il fonctionne avec toutes les productions : plantes (Tomate, Ble) et animaux (Poule, Vache).
 * -Il permet de séparer les types pour des raisons graphiques, puis de vendre tout le stock à la fin de la saison.
 * pour ajouter “Miel” ou “Fromage”, il suffira de créer la sous-classe 
 * + .properties + getter getNom(), et tout le reste fonctionne sans toucher à l’Entrepot ni à Jeu
 */
public class Entrepot 
{



    /**
     * -stock des calories par type de production.
     */
    private final Map<String, Double> stock;

    /**
     * -constructeur par défaut : entrepôt vide.
     */
    public Entrepot() {
        this.stock = new HashMap<>();
    }

    /**
     * -constructeur de copie : clone le contenu d'un autre entrepôt.
     * @param autre entrepôt à copier
     */
    public Entrepot(Entrepot autre) {
        this.stock = new HashMap<>(autre.stock);
    }

    /**
     * ajoute des calories pour une production donnée.
     * @param type nom de la production (ex : "Tomate", "Poule", ...)
     * @param calories quantité à ajouter
     */
    public void ajouter(String type, double calories) 
    {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type de production invalide : " + type);
        }
        if (calories < 0) {
            throw new IllegalArgumentException("Calories négatives non autorisées : " + calories);
        }
        stock.put(type, stock.getOrDefault(type, 0.0) + calories);
    }
    /**
     * -calcule le total de calories toutes productions confondues.
     *
     * @return le total en calories
     */
    public double getTotal() {
        return stock.values().stream().mapToDouble(Double::doubleValue).sum();
    }
    
    /**
     * Récupère la quantité de calories stockées pour un type donné.
     * @param type le type de production
     * @return les calories disponibles (0 si le type n'existe pas)
     */
    public double getCaloriesParType(String type) 
    {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type de production invalide : " + type);
        }
        return stock.getOrDefault(type, 0.0);
    }



    /**
     * -vide tout le stock après l'avoir vendu et retourne les gains.
     * 
     *
     */
    public void vider() {
        stock.clear();
    }

    /**
     * -affiche le contenu actuel de l'entrepôt.
     */
    public void afficher() 
    {
        System.out.println("Entrepôt :");
        stock.forEach((type, cal) ->
            System.out.println("- " + type + " : " + cal + " cal")
        );
        System.out.println("TOTAL : " + getTotal() + " cal");
    }

} 


