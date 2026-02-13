package modele; 



import java.io.InputStream;
import java.util.Properties;





    /**
     * Charge un fichier de configuration depuis data/config/.
     * @param nomFichier simple (p.ex. "poule.properties") ou déjà préfixé ("config/…")
     * @return les Properties chargées
     * @throws IllegalStateException si le fichier n’existe pas sur le classpath
     */
public class Data 
{
    
	public static Properties chargerFichier(String nomFichier) 
	{
	    Properties props = new Properties();
	    try (InputStream input = Data.class.getClassLoader().getResourceAsStream(nomFichier)) {
	        System.out.println("Chargement de : " + nomFichier);
	        System.out.println("Trouvé ? " + (input != null));
	        if (input == null) {
	            return props; // fichier non trouvé
	        }
	        props.load(input);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return props;
	}

}
