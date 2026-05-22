package fr.iut.r402.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires de la classe MotDePasseService.
 *
 * Ces tests vérifient que :
 * - un mot de passe est bien haché ;
 * - le hash ne contient pas le mot de passe en clair ;
 * - deux hachages du même mot de passe sont différents grâce au salage ;
 * - un mot de passe correct est accepté ;
 * - un mauvais mot de passe est refusé.
 */
class MotDePasseServiceTest {

    private final MotDePasseService motDePasseService = new MotDePasseService();

    @Test
    @DisplayName("Hacher un mot de passe sans le conserver en clair")
    void givenPassword_whenHacherMotDePasse_thenReturnsHashDifferentFromPlainPassword() {
        // Given
        String motDePasse = "Secure123!";

        // When
        String hash = motDePasseService.hacherMotDePasse(motDePasse);

        // Then
        assertThat(hash).isNotBlank();
        assertThat(hash).isNotEqualTo(motDePasse);
        assertThat(hash).startsWith("$2a$");
    }

    @Test
    @DisplayName("Générer deux hashes différents pour le même mot de passe grâce au salage")
    void givenSamePassword_whenHacherTwice_thenReturnsDifferentHashes() {
        // Given
        String motDePasse = "Secure123!";

        // When
        String premierHash = motDePasseService.hacherMotDePasse(motDePasse);
        String deuxiemeHash = motDePasseService.hacherMotDePasse(motDePasse);

        // Then
        assertThat(premierHash).isNotEqualTo(deuxiemeHash);
    }

    @Test
    @DisplayName("Accepter un mot de passe correspondant au hash stocké")
    void givenCorrectPassword_whenVerifierMotDePasse_thenReturnsTrue() {
        // Given
        String motDePasse = "Secure123!";
        String hash = motDePasseService.hacherMotDePasse(motDePasse);

        // When
        boolean resultat = motDePasseService.verifierMotDePasse(motDePasse, hash);

        // Then
        assertThat(resultat).isTrue();
    }

    @Test
    @DisplayName("Refuser un mot de passe différent du hash stocké")
    void givenWrongPassword_whenVerifierMotDePasse_thenReturnsFalse() {
        // Given
        String motDePasse = "Secure123!";
        String mauvaisMotDePasse = "Mauvais123!";
        String hash = motDePasseService.hacherMotDePasse(motDePasse);

        // When
        boolean resultat = motDePasseService.verifierMotDePasse(mauvaisMotDePasse, hash);

        // Then
        assertThat(resultat).isFalse();
    }
}