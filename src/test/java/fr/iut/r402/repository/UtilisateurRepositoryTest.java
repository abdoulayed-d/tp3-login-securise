package fr.iut.r402.repository;

import fr.iut.r402.model.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires de la classe UtilisateurRepository.
 *
 * Ces tests vérifient que les opérations JDBC fonctionnent :
 * - création de la table ;
 * - insertion d'un utilisateur ;
 * - recherche d'un utilisateur par login ;
 * - absence de résultat pour un login inconnu.
 */
class UtilisateurRepositoryTest {

    private UtilisateurRepository utilisateurRepository;

    @BeforeEach
    void initialiserBase() {
        utilisateurRepository = new UtilisateurRepository();

        /*
         * Avant chaque test, on s'assure que la table existe.
         * Ensuite, on vide la table pour que chaque test commence
         * avec une base propre.
         */
        utilisateurRepository.initialiserTable();
        utilisateurRepository.supprimerTousLesUtilisateurs();
    }

    @Test
    @DisplayName("Ajouter puis retrouver un utilisateur par son login")
    void givenUtilisateur_whenAjouterUtilisateur_thenCanFindUserByLogin() {
        // Given
        Utilisateur utilisateur = new Utilisateur(
                "abdoulaye",
                "abdoulaye.kaba@iut.fr",
                "$2a$12$hashbcryptfictifpourletest"
        );

        // When
        utilisateurRepository.ajouterUtilisateur(utilisateur);
        Optional<Utilisateur> resultat = utilisateurRepository.trouverParLogin("abdoulaye");

        // Then
        assertThat(resultat).isPresent();
        assertThat(resultat.get().getLogin()).isEqualTo("abdoulaye");
        assertThat(resultat.get().getEmail()).isEqualTo("abdoulaye.kaba@iut.fr");
        assertThat(resultat.get().getMotDePasseHash()).isEqualTo("$2a$12$hashbcryptfictifpourletest");
    }

    @Test
    @DisplayName("Retourner un résultat vide quand le login n'existe pas")
    void givenUnknownLogin_whenTrouverParLogin_thenReturnsEmptyOptional() {
        // When
        Optional<Utilisateur> resultat = utilisateurRepository.trouverParLogin("inconnu");

        // Then
        assertThat(resultat).isEmpty();
    }
}