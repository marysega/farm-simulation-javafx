package ui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.Optional;

/**
 * Utilitaire pour afficher :
 *  - des boîtes modales d'information, d'erreur, de confirmation,
 *  - un toast non bloquant positionné dans un parent StackPane.
 */
public class FenetreAlerte 
{

    /** Boîte modale d'information. */
    public static void info(String titre, String message) 
    {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    /** Boîte modale d'erreur. */
    public static void erreur(String titre, String message) 
    {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    /**
     * Boîte modale de confirmation Oui/Non.
     * @return true si l'utilisateur clique sur Oui.
     */
    public static boolean confirmation(String titre, String message) 
    {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(message);
        Optional<ButtonType> resp = a.showAndWait();
        return resp.isPresent() && resp.get() == ButtonType.OK;
    }

    /**
     * Affiche un toast non bloquant en bas du parent donné.
     * @param parent  le StackPane sur lequel overlay le toast
     * @param message le texte à afficher
     */
    /**
     * Affiche un toast non bloquant centré en bas du parentNode.
     */
    public static void toast(Node parentNode, String message) 
    {
        Label txt = new Label(message);
        txt.setStyle("-fx-background-color: rgba(0,0,0,0.7);"
                   + "-fx-text-fill: white; "
                   + "-fx-padding: 6px 12px; "
                   + "-fx-background-radius: 4px;");
        Popup popup = new Popup();
        popup.getContent().add(txt);
        popup.setAutoHide(true);

        // calculer la position relative au parentNode
        Pane container = (Pane)parentNode.getScene().getRoot();
        double x = (container.getWidth() - txt.getWidth()) / 2;
        double y = container.getHeight() - 80;
        popup.show(parentNode.getScene().getWindow(), 
                   container.localToScreen(x, y).getX(),
                   container.localToScreen(x, y).getY());

        FadeTransition ft = new FadeTransition(Duration.seconds(3), txt);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> popup.hide());
        ft.play();
    }
}
