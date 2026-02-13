package ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import modele.Case;
import modele.Ferme;
import modele.Position;

import java.util.Map;

import entite.Animal;
import entite.Plante;

/**
 * Vue isométrique interactive de la ferme, avec :
 * - fond herbe, sol labouré, clôture
 * - entités (plantes, animaux, puits)
 * - icônes magasin et entrepôt aux coins supérieurs
 * - gestion des clics case
 */
public class IsoFarmView extends Canvas 
{

    private final Ferme ferme;
    private final Map<String, Image> icons;
    private static final double TILE_W = 100;
    private static final double TILE_H = TILE_W / 2;
    private double originX;
    private double originY;
    private CaseClickListener clickListener;
    private final Tooltip tooltip = new Tooltip();

    /**
     * @param ferme  le modèle de la ferme
     * @param icons  la map d’icônes (doit contenir "grass","labour","fence",
     *               "magasin" et "entrepot")
     * @param width  largeur du canvas
     * @param height hauteur du canvas
     */
    public IsoFarmView(Ferme ferme, Map<String,Image> icons) 
    {
        super();
        this.ferme = ferme;
        this.icons = icons;
        // Quand le canvas change de taille, on recalcule l'origine et on redraw
        widthProperty().addListener((o,oldW,newW) -> { recalcOrigin(); redraw(); });
        heightProperty().addListener((o,oldH,newH) -> { recalcOrigin(); redraw(); });
        setOnMouseClicked(this::handleClick);
        Tooltip.install(this, tooltip);
        
     // Gestion du survol
        this.setOnMouseMoved(e -> 
        {
            Position pos = calculerCaseSousSouris(e.getX(), e.getY());
            if (pos != null) {
                Case c = ferme.getCase(pos);
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("Case [%d,%d]\n",
                                        pos.getX(), pos.getY()));
                // Eau actuelle uniquement
                sb.append(String.format("Eau: %.1f\n", c.getEau()));
                // Fertilisant actuel
                sb.append(String.format("Fert.: %dg\n", (int)c.getFertilisant()));
                if (c.getAnimal() != null) {
                    Animal a = c.getAnimal();
                    sb.append(String.format(
                        "%s\nEau: %.1f/%.1f\nFaim: %.1f/%.1f\n",
                        a.getNom(),
                        a.getEauActuelle(), a.getCapEau(),
                        a.getNourritureActuelle(), a.getCapNourriture()
                    ));
                } else if (c.getPlante() != null) {
                    Plante p = c.getPlante();
                    sb.append(String.format(
                        "%s\nEau: %.1f/%.1f\n",
                        p.getNom(),
                        p.getEauActuelle(), p.getCapEau()
                    ));
                }
                tooltip.setText(sb.toString());
            } else {
                tooltip.setText("");
            }
        })
;
    }

    /**  
     * Si on encore besoin de l’ancien constructeur (taille statique),  
     * gardez-le ou adaptez-le pour appeler le nouveau :  
     */
    public IsoFarmView(Ferme ferme, Map<String,Image> icons, double w, double h) 
    {
        this(ferme, icons);
        setWidth(w);
        setHeight(h);
        recalcOrigin();
        redraw();
    }

    private void recalcOrigin() {
        double w = getWidth(), h = getHeight();
        int size = ferme.getTaille();
        this.originX = w/2;
        this.originY = h/2 - ((size-1)*TILE_H)/2;
    }

    /** Redessine tout : fond, grille, clôture, entités, magasin & entrepôt. */
    public void redraw() 
    {
        GraphicsContext gc = getGraphicsContext2D();
        double w = getWidth(), h = getHeight();
        gc.clearRect(0, 0, w, h);

        int size = ferme.getTaille();
        Image grass  = icons.get("grass");
        Image labour = icons.get("labour");
        Image fence  = icons.get("fence");
        Image store  = icons.get("magasin");
        Image depot  = icons.get("entrepot");

        // 1) Fond herbe répétitif
        for (double y = 0; y < h; y += TILE_H)
            for (double x = 0; x < w; x += TILE_W)
                gc.drawImage(grass, x, y, TILE_W, TILE_H);

        // 2) Sol labouré
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                gc.drawImage(labour,
                    toScreenX(i, j), toScreenY(i, j),
                    TILE_W, TILE_H
                );

        // 3) Clôture périphérique
        for (int j = 0; j < size; j++) drawFence(gc, -1, j, 35);
        for (int i = 0; i < size; i++) drawFence(gc, i, size, -10);
        for (int j = 0; j < size; j++) drawFence(gc, size, j, 35);
        for (int i = 0; i < size; i++) drawFence(gc, i, -1, -10);

        // 4) Entités (plantes 60%, animaux 80%, puits centré 50%)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Case c = ferme.getCase(new Position(i, j));
                double tileX = toScreenX(i, j), tileY = toScreenY(i, j);
                double baseX = tileX + TILE_W / 2, baseY = tileY + TILE_H;

                // plante
                if (c.getPlante() != null) {
                    Image img = icons.get(c.getPlante().getNom());
                    double sc = 0.6, w2 = TILE_W*sc, h2 = w2*img.getHeight()/img.getWidth();
                    gc.drawImage(img, baseX - w2/2, baseY - h2, w2, h2);
                }
                // animal
                if (c.getAnimal() != null) {
                    Image img = icons.get(c.getAnimal().getNom());
                    double sc = 0.8, w2 = TILE_W*sc, h2 = w2*img.getHeight()/img.getWidth();
                    gc.drawImage(img, baseX - w2/2, baseY - h2, w2, h2);
                }
                // puits
                if (c.aUnPuits()) {
                    Image img = icons.get("Puit");
                    double sc = 0.5, w2 = TILE_W*sc, h2 = w2*img.getHeight()/img.getWidth();
                    gc.drawImage(img,
                        tileX + (TILE_W-w2)/2,
                        tileY + (TILE_H-h2)/2,
                        w2, h2
                    );
                }
            }
        }

        // 5) Icônes magasin & entrepôt
        double iconSize = TILE_W*3; 
        // magasin en haut à gauche de la grille
        gc.drawImage(store,
            originX - TILE_W - iconSize*1.3,
            originY - TILE_H*3,
            iconSize, iconSize
        );
        // entrepôt en haut à droite
        gc.drawImage(depot,
            originX + (size-1)*(TILE_W/2.5) + iconSize*0,
            originY - TILE_H*4,
            iconSize, iconSize
        );
    }
    
    /**
     * Calcule la coordonnée écran X d’une tuile isométrique.
     *
     * @param i l’indice de ligne dans la grille (0 ≤ i < taille)
     * @param j l’indice de colonne dans la grille (0 ≤ j < taille)
     * @return la coordonnée X en pixels où dessiner la tuile (i,j)
     */

    private double toScreenX(int i, int j) {
        return originX + (j - i)*(TILE_W/2);
    }
    
    /**
     * Calcule la coordonnée écran Y d’une tuile isométrique.
     *
     * @param i l’indice de ligne dans la grille (0 ≤ i < taille)
     * @param j l’indice de colonne dans la grille (0 ≤ j < taille)
     * @return la coordonnée Y en pixels où dessiner la tuile (i,j)
     */
    private double toScreenY(int i, int j) {
        return originY + (i + j)*(TILE_H/2);
    }
    
    /**
     * Dessine une image de clôture (« fence ») pivotée et centrée sur la tuile (i,j).
     *
     * @param gc    le contexte graphique du Canvas
     * @param i     l’indice de ligne de la tuile autour de laquelle placer la clôture
     * @param j     l’indice de colonne de la tuile autour de laquelle placer la clôture
     * @param angle l’angle de rotation à appliquer (en degrés)
     */

    private void drawFence(GraphicsContext gc, int i, int j, double angle) {
        double x = toScreenX(i, j), y = toScreenY(i, j);
        double fw = TILE_W*0.7, fh = TILE_H*1.2;
        gc.save();
        gc.translate(x+TILE_W/2, y+TILE_H/2);
        gc.rotate(angle);
        gc.drawImage(icons.get("fence"), -fw/2, -fh/2, fw, fh);
        gc.restore();
    }
    
    /**
     * Listener fonctionnel pour recevoir les coordonnées de la tuile cliquée.
     */

    @FunctionalInterface
    public interface CaseClickListener {
    	/**
         * Est appelé lorsqu’une tuile isométrique est cliquée.
         *
         * @param i l’indice de ligne de la tuile cliquée
         * @param j l’indice de colonne de la tuile cliquée
         */void onCaseClicked(int i, int j); 
         }
    public void setCaseClickListener(CaseClickListener l) { this.clickListener = l; }
    
    /**
     * Gère l’événement de clic sur le Canvas en convertissant
     * les coordonnées souris (X,Y) en indices de grille (i,j)
     * puis notifie le CaseClickListener enregistré.
     *
     * @param e l’événement de souris reçu
     */

    private void handleClick(MouseEvent e) {
        double dx = e.getX() - (originX + TILE_W / 2);
        double dy = e.getY() - (originY + TILE_H / 2);
        double a = dy / TILE_H;
        double b = dx / TILE_W;
        int i = (int) Math.round(a - b);
        int j = (int) Math.round(a + b);
        if (i >= 0 && j >= 0 && i < ferme.getTaille() && j < ferme.getTaille()) {
            if (clickListener != null) clickListener.onCaseClicked(i, j);
        }
    }
    
    /**
     * À mettre dans IsoFarmView :
     * 
     * Calcule la case (i,j) sous la souris, ou renvoie null si hors-grille.
     */
    /**
     * Renvoie la case (i,j) sous la souris, ou null si hors grille.
     */
    private Position calculerCaseSousSouris(double mx, double my) 
    {
        // 1) Même décalage que dans handleClick
        double dx = mx - (originX + TILE_W / 2);
        double dy = my - (originY + TILE_H / 2);


        // 2) Normalisation
        double a = dy / TILE_H;
        double b = dx / TILE_W;


        // 3) Calcul des indices i,j
        int i = (int) Math.round(a - b);
        int j = (int) Math.round(a + b);


        // 4) Vérification des bornes
        if (i >= 0 && j >= 0 && i < ferme.getTaille() && j < ferme.getTaille()) {
            return new Position(i, j);
        } else {
            return null;
        }
    }



}
