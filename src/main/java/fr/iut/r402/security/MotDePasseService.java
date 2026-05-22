package fr.iut.r402.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Service chargé de sécuriser les mots de passe.
 *
 * Cette classe utilise BCrypt, un algorithme de hachage spécialement conçu
 * pour le stockage sécurisé des mots de passe.
 *
 * BCrypt ajoute automatiquement :
 * - un salage (salt) ;
 * - un coût de calcul configurable ;
 * - une protection contre certaines attaques par force brute.
 *
 * Important :
 * On ne stocke jamais un mot de passe en clair dans la base de données.
 * Seul le hash BCrypt sera stocké.
 */
public class MotDePasseService {

    /**
     * Nombre de tours utilisés par BCrypt.
     *
     * Plus cette valeur est élevée :
     * - plus le calcul est lent ;
     * - plus la sécurité est élevée.
     *
     * La valeur 12 est un compromis classique entre sécurité et performance.
     */
    private static final int LOG_ROUNDS = 12;

    /**
     * Génère un hash BCrypt sécurisé à partir d'un mot de passe.
     *
     * BCrypt génère automatiquement un salt aléatoire.
     *
     * Exemple :
     * mot de passe : Secure123!
     * hash produit : $2a$12$...
     *
     * @param motDePasse mot de passe en clair
     * @return hash BCrypt du mot de passe
     */
    public String hacherMotDePasse(String motDePasse) {
        return BCrypt.hashpw(
                motDePasse,
                BCrypt.gensalt(LOG_ROUNDS)
        );
    }

    /**
     * Vérifie qu'un mot de passe correspond bien au hash stocké.
     *
     * BCrypt extrait automatiquement le salt depuis le hash stocké,
     * puis recalcule le hash pour comparer les deux valeurs.
     *
     * @param motDePasse mot de passe saisi par l'utilisateur
     * @param hashStocke hash BCrypt stocké dans la base
     * @return true si le mot de passe est correct
     */
    public boolean verifierMotDePasse(String motDePasse, String hashStocke) {
        return BCrypt.checkpw(motDePasse, hashStocke);
    }
}