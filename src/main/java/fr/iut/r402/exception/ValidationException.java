package fr.iut.r402.exception;

/**
 * Exception personnalisée utilisée lorsqu'une donnée du formulaire est invalide.
 *
 * Exemple :
 * - login vide ;
 * - email incorrect ;
 * - mot de passe trop faible.
 *
 * L'intérêt d'avoir une exception dédiée est de rendre le code plus clair :
 * lorsqu'une ValidationException est levée, on sait que le problème vient
 * d'une erreur de saisie de l'utilisateur.
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructeur de l'exception.
     *
     * @param message message décrivant précisément l'erreur rencontrée
     */
    public ValidationException(String message) {
        super(message);
    }
}