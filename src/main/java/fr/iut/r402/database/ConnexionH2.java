package fr.iut.r402.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsable de la connexion à la base de données H2.
 *
 * Dans ce TP, nous utilisons H2 en mode "embedded".
 * Cela signifie que la base de données fonctionne directement dans
 * l'application Java, sans serveur externe à lancer.
 *
 * Avantage :
 * - pratique pour un TP ;
 * - simple à configurer ;
 * - compatible avec JDBC ;
 * - facile à tester.
 */
public class ConnexionH2 {

    /**
     * URL JDBC de la base H2.
     *
     * jdbc:h2 indique que l'on utilise H2.
     * ./data/tp3_login indique que les fichiers de la base seront créés
     * dans un dossier local nommé "data", à la racine du projet.
     *
     * H2 créera automatiquement les fichiers nécessaires si la base n'existe pas.
     */
    private static final String URL = "jdbc:h2:./data/tp3_login";

    /**
     * Nom d'utilisateur par défaut de H2.
     *
     * Dans H2, l'utilisateur par défaut est souvent "sa".
     */
    private static final String UTILISATEUR = "sa";

    /**
     * Mot de passe utilisé pour la connexion.
     *
     * Ici, il est vide pour simplifier le TP.
     * Dans une vraie application, il faudrait éviter de laisser un mot de passe vide.
     */
    private static final String MOT_DE_PASSE = "";

    /**
     * Constructeur privé.
     *
     * Cette classe ne contient que des méthodes utilitaires statiques.
     * On empêche donc son instanciation avec un constructeur privé.
     */
    private ConnexionH2() {
    }

    /**
     * Ouvre une connexion JDBC vers la base H2.
     *
     * DriverManager utilise l'URL, l'utilisateur et le mot de passe
     * pour créer une connexion à la base.
     *
     * @return une connexion active vers la base H2
     * @throws SQLException si la connexion échoue
     */
    public static Connection ouvrirConnexion() throws SQLException {
        return DriverManager.getConnection(URL, UTILISATEUR, MOT_DE_PASSE);
    }
}