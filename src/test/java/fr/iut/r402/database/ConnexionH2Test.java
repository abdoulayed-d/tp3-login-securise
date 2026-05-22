package fr.iut.r402.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires de la classe ConnexionH2.
 *
 * Ce test vérifie que l'application peut ouvrir une connexion JDBC
 * vers la base de données H2.
 */
class ConnexionH2Test {

    @Test
    @DisplayName("Ouvrir une connexion JDBC vers la base H2")
    void givenH2Configuration_whenOuvrirConnexion_thenConnectionIsValid() throws Exception {
        // When
        try (Connection connexion = ConnexionH2.ouvrirConnexion()) {

            // Then
            assertThat(connexion).isNotNull();
            assertThat(connexion.isClosed()).isFalse();
            assertThat(connexion.isValid(2)).isTrue();
        }
    }
}