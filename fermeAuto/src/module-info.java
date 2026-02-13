/**
 * Module principal de l'application de simulation de ferme.
 * Définit les dépendances JavaFX nécessaires et exporte les packages
 * contenant les interfaces utilisateur et la classe principale.
 */
module info {
    /**
     * Bibliothèque JavaFX pour les contrôles (boutons, labels, etc.).
     */
    requires javafx.controls;
    /**
     * Bibliothèque JavaFX pour le rendu graphique et les scènes.
     */
    requires javafx.graphics;

    /**
     * Package contenant les classes de l'interface utilisateur de la simulation.
     */
    exports ui;
    /**
     * Package contenant la classe principale de lancement de l'application.
     */
    exports mainPrincipale;
}

