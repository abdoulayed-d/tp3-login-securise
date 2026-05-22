package fr.iut.r402.model;

/**
 * Représente un utilisateur de l'application.
 *
 * Dans ce TP, un utilisateur possède :
 * - un login ;
 * - une adresse email ;
 * - un mot de passe sécurisé sous forme de hash.
 *
 * Important :
 * On ne stocke jamais le mot de passe en clair.
 * On stockera plus tard uniquement le mot de passe haché avec BCrypt.
 */
public class Utilisateur {

    private final String login;
    private final String email;
    private final String motDePasseHash;

    /**
     * Constructeur de la classe Utilisateur.
     *
     * @param login login de l'utilisateur
     * @param email adresse email de l'utilisateur
     * @param motDePasseHash mot de passe haché avec BCrypt
     */
    public Utilisateur(String login, String email, String motDePasseHash) {
        this.login = login;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
    }

    /**
     * @return le login de l'utilisateur
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return l'adresse email de l'utilisateur
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return le mot de passe haché
     */
    public String getMotDePasseHash() {
        return motDePasseHash;
    }
}