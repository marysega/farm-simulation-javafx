
package ui;

import javafx.scene.image.Image;
import modele.Saison;

import java.util.HashMap;
import java.util.EnumMap;
import java.util.Map;

/**
 * Charge et fournit toutes les icônes de l'application depuis un répertoire donné.
 */
public class IconRegistry 
{
    /**
     * Charge les icônes nommées en mémoire.
     *
     * @param basePath chemin de base (ex. "file:data/images/")
     * @return Map associant la clé (nom de l'icône) à l'objet Image
     */
    public static Map<String, Image> loadIcons(String basePath) 
    {
        Map<String, Image> icons = new HashMap<>();
        icons.put("labour",   new Image(basePath + "labour.png"));
        icons.put("Vache",    new Image(basePath + "vache.png"));
        icons.put("Poule",    new Image(basePath + "poule.png"));
        icons.put("Tomate",   new Image(basePath + "tomate.png"));
        icons.put("Ble",      new Image(basePath + "ble.png"));
        icons.put("Puit",     new Image(basePath + "puit.png"));
        icons.put("fence",    new Image(basePath + "fence.png"));
        icons.put("grass",    new Image(basePath + "grass.png"));
        icons.put("magasin",  new Image(basePath + "magasin.png"));
        icons.put("entrepot", new Image(basePath + "entrepot.png"));
        return icons;
    }
 
}

