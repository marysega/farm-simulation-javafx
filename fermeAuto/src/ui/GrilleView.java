package ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.HPos;
import javafx.geometry.VPos;

/**
 * Grille statique 6×6 affichant le sol labouré et une bordure de clôtures.
 * Utilise les images situées dans data/images/
 * Pas utilisé pour mon Jeu seulement un premier test.
 */
public class GrilleView extends GridPane 
{
	
	public static final int SIZE = 6;
	public static final int TILE_SIZE = 60;

    private final Image labourTile;
    private final Image fenceTile;

    /**
     * Construit et dessine immédiatement la grille statique.
     */
    public GrilleView() 
    {
        this.labourTile = new Image("file:data/images/labour.png", TILE_SIZE, TILE_SIZE, true, true);
        this.fenceTile  = new Image("file:data/images/fence.png",  TILE_SIZE, TILE_SIZE, true, true);

        setHgap(0);
        setVgap(0);
        setPrefSize(TILE_SIZE * (SIZE + 2), TILE_SIZE * (SIZE + 2));
        setMaxSize(TILE_SIZE * (SIZE + 2), TILE_SIZE * (SIZE + 2));
        // Centre dans parent si utilisé dans un StackPane
        setHalignment(this, HPos.CENTER);
        setValignment(this, VPos.CENTER);

        drawStaticGrid();
     // pour voir la grille et sa taille
        setGridLinesVisible(true);
        setStyle("-fx-background-color: lightgray;");
        System.out.println("GrilleView construite, taille = " 
            + getPrefWidth() + "×" + getPrefHeight());
        
        setPrefSize(400, 400);
        setMaxSize(400, 400);
        drawStaticGrid();

    }

    /**
     * Dessine la grille de SOL + clôture.
     */
    public void drawStaticGrid() {
        for (int row = 0; row < SIZE + 2; row++) {
            for (int col = 0; col < SIZE + 2; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);

                if (row > 0 && row <= SIZE && col > 0 && col <= SIZE) {
                    // Case intérieure : sol labouré
                    ImageView iv = new ImageView(labourTile);
                    cell.getChildren().add(iv);
                } else {
                    // Bordure : clôture
                    ImageView iv = new ImageView(fenceTile);
                    iv.setFitWidth(TILE_SIZE);
                    iv.setFitHeight(TILE_SIZE);
                    iv.setRotate(getRotation(row, col));
                    cell.getChildren().add(iv);
                }

                add(cell, col, row);
            }
        }
    }

    /**
     * Calcule la rotation à appliquer à la clôture selon la position.
     */
    private double getRotation(int row, int col) {
        int max = SIZE + 1;
        // Coins
        if (row == 0 && col == 0)         return 270;
        if (row == 0 && col == max)       return   0;
        if (row == max && col == 0)       return 180;
        if (row == max && col == max)     return  90;
        // Bords
        if (row == 0)                     return   0;   // haut
        if (row == max)                   return 180;   // bas
        if (col == 0)                     return 270;   // gauche
        return 90;                                  // droite
    }
}
