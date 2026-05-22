package fr.iut.r402.security;

import fr.iut.r402.exception.ValidationException;

/**
 * Classe chargée de vérifier les champs du formulaire de création de compte
 * ou de connexion.
 *
 * Elle vérifie :
 * - le login ;
 * - l'adresse email ;
 * - le mot de passe.
 *
 * Le but est d'éviter d'envoyer en base de données des informations invalides.
 */
public class ValidateurFormulaire {

    /**
     * Expression régulière utilisée pour vérifier le format d'une adresse email.
     *
     * Exemple accepté :
     * alain.dupont@iut.fr
     */
    private static final String REGEX_EMAIL = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Vérifie l'ensemble du formulaire.
     *
     * Cette méthode appelle les méthodes spécialisées :
     * - validerLogin()
     * - validerEmail()
     * - validerMotDePasse()
     *
     * @param login login saisi
     * @param email email saisi
     * @param motDePasse mot de passe saisi
     */
    public void validerFormulaire(String login, String email, String motDePasse) {
        validerLogin(login);
        validerEmail(email);
        validerMotDePasse(motDePasse);
    }

    /**
     * Vérifie le login.
     *
     * Règles choisies :
     * - le login ne doit pas être vide ;
     * - il doit contenir entre 3 et 30 caractères ;
     * - il peut contenir des lettres, des chiffres, un point, un tiret ou un underscore.
     *
     * Ces règles évitent les logins trop courts, trop longs ou contenant
     * des caractères difficiles à gérer.
     *
     * @param login login à vérifier
     */
    public void validerLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new ValidationException("Le login est obligatoire.");
        }

        if (login.length() < 3) {
            throw new ValidationException("Le login doit contenir au moins 3 caractères.");
        }

        if (login.length() > 30) {
            throw new ValidationException("Le login ne doit pas dépasser 30 caractères.");
        }

        if (!login.matches("^[a-zA-Z0-9._-]+$")) {
            throw new ValidationException("Le login contient des caractères non autorisés.");
        }
    }

    /**
     * Vérifie l'adresse email.
     *
     * Règles choisies :
     * - l'email ne doit pas être vide ;
     * - il doit respecter un format classique : nom@domaine.extension.
     *
     * @param email email à vérifier
     */
    public void validerEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("L'adresse email est obligatoire.");
        }

        if (!email.matches(REGEX_EMAIL)) {
            throw new ValidationException("Le format de l'adresse email est invalide.");
        }
    }

    /**
     * Vérifie la robustesse du mot de passe.
     *
     * Règles choisies à partir des critères classiques de sécurité :
     * - au moins 8 caractères ;
     * - au moins une lettre majuscule ;
     * - au moins une lettre minuscule ;
     * - au moins un chiffre ;
     * - au moins un caractère spécial ;
     * - aucun espace.
     *
     * Ces règles correspondent aux critères de complexité indiqués dans le TP :
     * longueur minimale, majuscules, minuscules, chiffres et caractères spéciaux.
     *
     * @param motDePasse mot de passe à vérifier
     */
    public void validerMotDePasse(String motDePasse) {
        if (motDePasse == null || motDePasse.isBlank()) {
            throw new ValidationException("Le mot de passe est obligatoire.");
        }

        if (motDePasse.length() < 8) {
            throw new ValidationException("Le mot de passe doit contenir au moins 8 caractères.");
        }

        if (motDePasse.contains(" ")) {
            throw new ValidationException("Le mot de passe ne doit pas contenir d'espace.");
        }

        if (!motDePasse.matches(".*[A-Z].*")) {
            throw new ValidationException("Le mot de passe doit contenir au moins une majuscule.");
        }

        if (!motDePasse.matches(".*[a-z].*")) {
            throw new ValidationException("Le mot de passe doit contenir au moins une minuscule.");
        }

        if (!motDePasse.matches(".*[0-9].*")) {
            throw new ValidationException("Le mot de passe doit contenir au moins un chiffre.");
        }

        if (!motDePasse.matches(".*[^a-zA-Z0-9].*")) {
            throw new ValidationException("Le mot de passe doit contenir au moins un caractère spécial.");
        }
    }
}