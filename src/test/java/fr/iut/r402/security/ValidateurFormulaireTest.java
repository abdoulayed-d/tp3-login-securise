package fr.iut.r402.security;

import fr.iut.r402.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitaires de la classe ValidateurFormulaire.
 *
 * Ces tests vérifient les différents scénarios possibles pour les champs :
 * - login ;
 * - email ;
 * - mot de passe.
 *
 * L'objectif est de s'assurer que les données valides sont acceptées
 * et que les données invalides provoquent bien une ValidationException.
 */
class ValidateurFormulaireTest {

    private final ValidateurFormulaire validateur = new ValidateurFormulaire();

    @Test
    @DisplayName("Accepter un formulaire valide")
    void givenValidForm_whenValiderFormulaire_thenDoesNotThrowException() {
        // Given
        String login = "abdoulaye.kaba";
        String email = "abdoulaye.kaba@iut.fr";
        String motDePasse = "Secure123!";

        // When / Then
        assertThatCode(() -> validateur.validerFormulaire(login, email, motDePasse))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Refuser un login vide")
    void givenBlankLogin_whenValiderLogin_thenThrowsValidationException() {
        // Given
        String login = "";

        // When / Then
        assertThatThrownBy(() -> validateur.validerLogin(login))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le login est obligatoire.");
    }

    @Test
    @DisplayName("Refuser un login trop court")
    void givenTooShortLogin_whenValiderLogin_thenThrowsValidationException() {
        // Given
        String login = "ab";

        // When / Then
        assertThatThrownBy(() -> validateur.validerLogin(login))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le login doit contenir au moins 3 caractères.");
    }

    @Test
    @DisplayName("Refuser un login avec des caractères interdits")
    void givenInvalidCharactersInLogin_whenValiderLogin_thenThrowsValidationException() {
        // Given
        String login = "abdoulaye@kaba";

        // When / Then
        assertThatThrownBy(() -> validateur.validerLogin(login))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le login contient des caractères non autorisés.");
    }

    @Test
    @DisplayName("Refuser un email vide")
    void givenBlankEmail_whenValiderEmail_thenThrowsValidationException() {
        // Given
        String email = "";

        // When / Then
        assertThatThrownBy(() -> validateur.validerEmail(email))
                .isInstanceOf(ValidationException.class)
                .hasMessage("L'adresse email est obligatoire.");
    }

    @Test
    @DisplayName("Refuser un email au format invalide")
    void givenInvalidEmail_whenValiderEmail_thenThrowsValidationException() {
        // Given
        String email = "abdoulaye.kaba-iut.fr";

        // When / Then
        assertThatThrownBy(() -> validateur.validerEmail(email))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le format de l'adresse email est invalide.");
    }

    @Test
    @DisplayName("Refuser un mot de passe trop court")
    void givenTooShortPassword_whenValiderMotDePasse_thenThrowsValidationException() {
        // Given
        String motDePasse = "Aa1!";

        // When / Then
        assertThatThrownBy(() -> validateur.validerMotDePasse(motDePasse))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le mot de passe doit contenir au moins 8 caractères.");
    }

    @Test
    @DisplayName("Refuser un mot de passe sans majuscule")
    void givenPasswordWithoutUppercase_whenValiderMotDePasse_thenThrowsValidationException() {
        // Given
        String motDePasse = "secure123!";

        // When / Then
        assertThatThrownBy(() -> validateur.validerMotDePasse(motDePasse))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le mot de passe doit contenir au moins une majuscule.");
    }

    @Test
    @DisplayName("Refuser un mot de passe sans minuscule")
    void givenPasswordWithoutLowercase_whenValiderMotDePasse_thenThrowsValidationException() {
        // Given
        String motDePasse = "SECURE123!";

        // When / Then
        assertThatThrownBy(() -> validateur.validerMotDePasse(motDePasse))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le mot de passe doit contenir au moins une minuscule.");
    }

    @Test
    @DisplayName("Refuser un mot de passe sans chiffre")
    void givenPasswordWithoutDigit_whenValiderMotDePasse_thenThrowsValidationException() {
        // Given
        String motDePasse = "SecurePassword!";

        // When / Then
        assertThatThrownBy(() -> validateur.validerMotDePasse(motDePasse))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le mot de passe doit contenir au moins un chiffre.");
    }

    @Test
    @DisplayName("Refuser un mot de passe sans caractère spécial")
    void givenPasswordWithoutSpecialCharacter_whenValiderMotDePasse_thenThrowsValidationException() {
        // Given
        String motDePasse = "Secure123";

        // When / Then
        assertThatThrownBy(() -> validateur.validerMotDePasse(motDePasse))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le mot de passe doit contenir au moins un caractère spécial.");
    }

    @Test
    @DisplayName("Refuser un mot de passe contenant un espace")
    void givenPasswordWithSpace_whenValiderMotDePasse_thenThrowsValidationException() {
        // Given
        String motDePasse = "Secure 123!";

        // When / Then
        assertThatThrownBy(() -> validateur.validerMotDePasse(motDePasse))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Le mot de passe ne doit pas contenir d'espace.");
    }
}