package fr.iut.r402.service;

import fr.iut.r402.exception.ValidationException;
import fr.iut.r402.model.Utilisateur;
import fr.iut.r402.repository.UtilisateurRepository;
import fr.iut.r402.security.MotDePasseService;
import fr.iut.r402.security.ValidateurFormulaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitaires du service d'authentification.
 *
 * Ces tests vérifient l'ensemble du scénario métier :
 * - validation du formulaire ;
 * - hachage du mot de passe avec BCrypt ;
 * - stockage de l'utilisateur dans H2 ;
 * - connexion avec un mot de passe correct ;
 * - refus d'une connexion invalide.
 */
class AuthentificationServiceTest {

    private UtilisateurRepository utilisateurRepository;
    private AuthentificationService authentificationService;

    @BeforeEach
    void initialiser() {
        utilisateurRepository = new UtilisateurRepository();

        /*
         * On prépare une base propre avant chaque test.
         * Cela évite qu'un test influence le suivant.
         */
        utilisateurRepository.initialiserTable();
        utilisateurRepository.supprimerTousLesUtilisateurs();

        authentificationService = new AuthentificationService(
                utilisateurRepository,
                new MotDePasseService(),
                new ValidateurFormulaire()
        );
    }

    @Test
    @DisplayName("Inscrire un utilisateur avec un mot de passe haché")
    void givenValidUser_whenInscrire_thenUserIsSavedWithHashedPassword() {
        // Given
        String login = "abdoulaye";
        String email = "abdoulaye.kaba@iut.fr";
        String motDePasse = "Secure123!";

        // When
        authentificationService.inscrire(login, email, motDePasse);

        // Then
        Optional<Utilisateur> utilisateur = utilisateurRepository.trouverParLogin(login);

        assertThat(utilisateur).isPresent();
        assertThat(utilisateur.get().getLogin()).isEqualTo(login);
        assertThat(utilisateur.get().getEmail()).isEqualTo(email);

        /*
         * Le mot de passe stocké ne doit jamais être le mot de passe en clair.
         * Il doit être remplacé par un hash BCrypt.
         */
        assertThat(utilisateur.get().getMotDePasseHash()).isNotEqualTo(motDePasse);
        assertThat(utilisateur.get().getMotDePasseHash()).startsWith("$2a$");
    }

    @Test
    @DisplayName("Connecter un utilisateur avec le bon mot de passe")
    void givenRegisteredUser_whenConnecterWithCorrectPassword_thenReturnsTrue() {
        // Given
        authentificationService.inscrire(
                "abdoulaye",
                "abdoulaye.kaba@iut.fr",
                "Secure123!"
        );

        // When
        boolean resultat = authentificationService.connecter("abdoulaye", "Secure123!");

        // Then
        assertThat(resultat).isTrue();
    }

    @Test
    @DisplayName("Refuser la connexion avec un mauvais mot de passe")
    void givenRegisteredUser_whenConnecterWithWrongPassword_thenReturnsFalse() {
        // Given
        authentificationService.inscrire(
                "abdoulaye",
                "abdoulaye.kaba@iut.fr",
                "Secure123!"
        );

        // When
        boolean resultat = authentificationService.connecter("abdoulaye", "Mauvais123!");

        // Then
        assertThat(resultat).isFalse();
    }

    @Test
    @DisplayName("Refuser la connexion avec un login inconnu")
    void givenUnknownLogin_whenConnecter_thenReturnsFalse() {
        // When
        boolean resultat = authentificationService.connecter("inconnu", "Secure123!");

        // Then
        assertThat(resultat).isFalse();
    }

    @Test
    @DisplayName("Refuser l'inscription avec un formulaire invalide")
    void givenInvalidForm_whenInscrire_thenThrowsValidationException() {
        // When / Then
        assertThatThrownBy(() -> authentificationService.inscrire(
                "ab",
                "email-invalide",
                "court"
        ))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Refuser l'inscription avec un login déjà utilisé")
    void givenExistingLogin_whenInscrireAgain_thenThrowsValidationException() {
        // Given
        authentificationService.inscrire(
                "abdoulaye",
                "abdoulaye.kaba@iut.fr",
                "Secure123!"
        );

        // When / Then
        assertThatThrownBy(() -> authentificationService.inscrire(
                "abdoulaye",
                "autre.email@iut.fr",
                "Secure123!"
        ))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Ce login est déjà utilisé.");
    }
}