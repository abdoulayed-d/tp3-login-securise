package fr.iut.r402.service;

import fr.iut.r402.exception.ValidationException;
import fr.iut.r402.model.Utilisateur;
import fr.iut.r402.repository.UtilisateurRepository;
import fr.iut.r402.security.MotDePasseService;
import fr.iut.r402.security.ValidateurFormulaire;

import java.util.Optional;

/**
 * Service responsable de l'inscription et de l'authentification des utilisateurs.
 *
 * Cette classe joue le rôle de "chef d'orchestre" entre :
 * - la validation du formulaire ;
 * - le hachage sécurisé du mot de passe avec BCrypt ;
 * - l'enregistrement et la recherche des utilisateurs en base H2.
 *
 * Elle évite de mettre toute la logique dans le main().
 * Cela rend le code plus clair, plus maintenable et plus simple à tester.
 */
public class AuthentificationService {

    /**
     * Repository utilisé pour accéder à la base de données H2.
     *
     * Il permet notamment :
     * - de créer la table utilisateurs ;
     * - d'ajouter un utilisateur ;
     * - de rechercher un utilisateur par login.
     */
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Service utilisé pour hacher et vérifier les mots de passe avec BCrypt.
     *
     * Important :
     * Le mot de passe en clair n'est jamais stocké.
     */
    private final MotDePasseService motDePasseService;

    /**
     * Validateur utilisé pour contrôler les champs du formulaire.
     *
     * Il vérifie :
     * - le login ;
     * - l'email ;
     * - le mot de passe.
     */
    private final ValidateurFormulaire validateurFormulaire;

    /**
     * Constructeur du service d'authentification.
     *
     * On injecte les dépendances par le constructeur.
     * Cela permet de rendre la classe plus facile à tester.
     *
     * @param utilisateurRepository repository des utilisateurs
     * @param motDePasseService service de gestion des mots de passe
     * @param validateurFormulaire service de validation du formulaire
     */
    public AuthentificationService(
            UtilisateurRepository utilisateurRepository,
            MotDePasseService motDePasseService,
            ValidateurFormulaire validateurFormulaire
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.motDePasseService = motDePasseService;
        this.validateurFormulaire = validateurFormulaire;
    }

    /**
     * Inscrit un nouvel utilisateur.
     *
     * Étapes réalisées :
     * 1. vérifier que le formulaire est valide ;
     * 2. vérifier que le login n'existe pas déjà ;
     * 3. hacher le mot de passe avec BCrypt ;
     * 4. créer un objet Utilisateur ;
     * 5. enregistrer l'utilisateur dans la base H2.
     *
     * @param login login choisi par l'utilisateur
     * @param email adresse email de l'utilisateur
     * @param motDePasse mot de passe en clair saisi dans le formulaire
     */
    public void inscrire(String login, String email, String motDePasse) {
        /*
         * Avant toute opération en base, on vérifie que les champs sont corrects.
         * Cela évite d'enregistrer des données invalides.
         */
        validateurFormulaire.validerFormulaire(login, email, motDePasse);

        /*
         * On vérifie que le login n'est pas déjà utilisé.
         * Sinon, deux utilisateurs pourraient avoir le même identifiant.
         */
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.trouverParLogin(login);

        if (utilisateurExistant.isPresent()) {
            throw new ValidationException("Ce login est déjà utilisé.");
        }

        /*
         * Le mot de passe en clair est transformé en hash BCrypt.
         * C'est ce hash qui sera stocké en base, jamais le mot de passe original.
         */
        String motDePasseHash = motDePasseService.hacherMotDePasse(motDePasse);

        /*
         * On crée l'utilisateur avec le login, l'email et le hash BCrypt.
         */
        Utilisateur utilisateur = new Utilisateur(login, email, motDePasseHash);

        /*
         * On enregistre l'utilisateur dans la base H2.
         */
        utilisateurRepository.ajouterUtilisateur(utilisateur);
    }

    /**
     * Authentifie un utilisateur à partir de son login et de son mot de passe.
     *
     * Étapes réalisées :
     * 1. rechercher l'utilisateur par son login ;
     * 2. refuser la connexion si le login est inconnu ;
     * 3. comparer le mot de passe saisi avec le hash stocké ;
     * 4. retourner true si le mot de passe est correct.
     *
     * @param login login saisi
     * @param motDePasse mot de passe saisi
     * @return true si l'authentification réussit
     */
    public boolean connecter(String login, String motDePasse) {
        /*
         * On recherche l'utilisateur en base à partir de son login.
         */
        Optional<Utilisateur> utilisateurTrouve = utilisateurRepository.trouverParLogin(login);

        /*
         * Si aucun utilisateur n'existe avec ce login, la connexion est refusée.
         */
        if (utilisateurTrouve.isEmpty()) {
            return false;
        }

        /*
         * On récupère l'utilisateur trouvé.
         */
        Utilisateur utilisateur = utilisateurTrouve.get();

        /*
         * On compare le mot de passe saisi avec le hash BCrypt stocké.
         * BCrypt sait retrouver le salt depuis le hash enregistré.
         */
        return motDePasseService.verifierMotDePasse(
                motDePasse,
                utilisateur.getMotDePasseHash()
        );
    }
}