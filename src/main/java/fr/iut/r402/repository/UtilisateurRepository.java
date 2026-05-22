package fr.iut.r402.repository;

import fr.iut.r402.database.ConnexionH2;
import fr.iut.r402.model.Utilisateur;

import java.sql.*;
import java.util.Optional;

/**
 * Classe responsable des échanges avec la base de données pour les utilisateurs.
 *
 * En architecture logicielle, une classe Repository sert à isoler le code SQL.
 * Cela évite de mélanger :
 * - la logique métier ;
 * - la sécurité ;
 * - les requêtes SQL ;
 * - les interactions avec la base de données.
 *
 * Ici, cette classe permet :
 * - de créer la table des utilisateurs ;
 * - d'ajouter un utilisateur ;
 * - de rechercher un utilisateur par son login.
 */
public class UtilisateurRepository {

    /**
     * Crée la table UTILISATEURS si elle n'existe pas déjà.
     *
     * La table contient :
     * - un identifiant technique auto-incrémenté ;
     * - un login unique ;
     * - un email unique ;
     * - un mot de passe haché avec BCrypt.
     *
     * Important :
     * Le mot de passe en clair ne doit jamais être stocké en base.
     */
    public void initialiserTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS utilisateurs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    login VARCHAR(30) NOT NULL UNIQUE,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    mot_de_passe_hash VARCHAR(100) NOT NULL
                )
                """;

        try (Connection connexion = ConnexionH2.ouvrirConnexion();
             Statement statement = connexion.createStatement()) {

            statement.execute(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la table utilisateurs.", e);
        }
    }

    /**
     * Enregistre un utilisateur dans la base de données.
     *
     * On utilise un PreparedStatement au lieu de concaténer les chaînes SQL.
     * Cela protège contre les injections SQL et rend le code plus propre.
     *
     * @param utilisateur utilisateur à enregistrer
     */
    public void ajouterUtilisateur(Utilisateur utilisateur) {
        String sql = """
                INSERT INTO utilisateurs (login, email, mot_de_passe_hash)
                VALUES (?, ?, ?)
                """;

        try (Connection connexion = ConnexionH2.ouvrirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            statement.setString(1, utilisateur.getLogin());
            statement.setString(2, utilisateur.getEmail());
            statement.setString(3, utilisateur.getMotDePasseHash());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'utilisateur.", e);
        }
    }

    /**
     * Recherche un utilisateur à partir de son login.
     *
     * Optional est utilisé car il est possible qu'aucun utilisateur
     * ne corresponde au login donné.
     *
     * @param login login recherché
     * @return un Optional contenant l'utilisateur s'il existe
     */
    public Optional<Utilisateur> trouverParLogin(String login) {
        String sql = """
                SELECT login, email, mot_de_passe_hash
                FROM utilisateurs
                WHERE login = ?
                """;

        try (Connection connexion = ConnexionH2.ouvrirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            statement.setString(1, login);

            try (ResultSet resultat = statement.executeQuery()) {
                if (resultat.next()) {
                    Utilisateur utilisateur = new Utilisateur(
                            resultat.getString("login"),
                            resultat.getString("email"),
                            resultat.getString("mot_de_passe_hash")
                    );

                    return Optional.of(utilisateur);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'utilisateur.", e);
        }
    }

    /**
     * Supprime tous les utilisateurs.
     *
     * Cette méthode sera surtout utile pour les tests unitaires,
     * afin de repartir d'une base propre avant chaque scénario.
     */
    public void supprimerTousLesUtilisateurs() {
        String sql = "DELETE FROM utilisateurs";

        try (Connection connexion = ConnexionH2.ouvrirConnexion();
             Statement statement = connexion.createStatement()) {

            statement.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression des utilisateurs.", e);
        }
    }
}