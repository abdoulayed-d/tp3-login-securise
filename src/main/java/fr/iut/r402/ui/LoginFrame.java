package fr.iut.r402.ui;

import fr.iut.r402.exception.ValidationException;
import fr.iut.r402.service.AuthentificationService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;

/**
 * Fenêtre graphique de l'application de login sécurisé.
 *
 * Cette interface est réalisée avec Swing, qui est intégré directement à Java.
 * Cela évite d'ajouter une dépendance JavaFX dans le projet.
 *
 * L'interface propose deux onglets :
 * - Inscription : permet de créer un utilisateur avec login, email et mot de passe ;
 * - Connexion : permet de se connecter avec login et mot de passe.
 *
 * Cette classe ne contient pas la logique métier elle-même.
 * Elle appelle AuthentificationService, qui se charge de :
 * - valider les champs ;
 * - hacher le mot de passe avec BCrypt ;
 * - enregistrer ou rechercher l'utilisateur dans H2.
 */
public class LoginFrame extends JFrame {

    /**
     * Service métier utilisé par l'interface.
     *
     * L'interface graphique ne doit pas accéder directement à la base de données.
     * Elle passe par AuthentificationService.
     */
    private final AuthentificationService authentificationService;

    /**
     * Constructeur de la fenêtre.
     *
     * @param authentificationService service qui gère l'inscription et la connexion
     */
    public LoginFrame(AuthentificationService authentificationService) {
        this.authentificationService = authentificationService;

        setTitle("TP3 - Login sécurisé");
        setSize(520, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
         * JTabbedPane permet d'avoir plusieurs onglets dans la même fenêtre.
         * Ici, on sépare clairement l'inscription et la connexion.
         */
        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Inscription", creerPanneauInscription());
        onglets.addTab("Connexion", creerPanneauConnexion());

        add(onglets);
    }

    /**
     * Crée le panneau d'inscription.
     *
     * Ce panneau contient :
     * - un champ login ;
     * - un champ email ;
     * - un champ mot de passe ;
     * - un bouton OK ;
     * - un bouton Cancel ;
     * - une zone d'information.
     *
     * @return le panneau Swing d'inscription
     */
    private JPanel creerPanneauInscription() {
        JPanel panneau = new JPanel(new GridBagLayout());
        GridBagConstraints contraintes = creerContraintesDeBase();

        JTextField champLogin = new JTextField(22);
        JTextField champEmail = new JTextField(22);
        JPasswordField champMotDePasse = new JPasswordField(22);

        JButton boutonOk = new JButton("OK");
        JButton boutonCancel = new JButton("Cancel");

        JLabel message = new JLabel("Saisissez un login d'au moins 8 caractères.");
        message.setForeground(Color.DARK_GRAY);

        /*
         * Au départ, le mot de passe n'est pas éditable.
         * Le TP demande que le champ mot de passe ne soit pas éditable
         * si le nom de l'utilisateur fait moins de 8 caractères.
         */
        champMotDePasse.setEditable(false);
        champMotDePasse.setEnabled(false);

        boutonOk.setEnabled(false);
        boutonCancel.setEnabled(false);

        ajouterLigne(panneau, contraintes, 0, "Login :", champLogin);
        ajouterLigne(panneau, contraintes, 1, "Email :", champEmail);
        ajouterLigne(panneau, contraintes, 2, "Mot de passe :", champMotDePasse);

        contraintes.gridx = 0;
        contraintes.gridy = 3;
        contraintes.gridwidth = 2;
        panneau.add(message, contraintes);

        JPanel panneauBoutons = new JPanel();
        panneauBoutons.add(boutonOk);
        panneauBoutons.add(boutonCancel);

        contraintes.gridy = 4;
        panneau.add(panneauBoutons, contraintes);

        /*
         * Cette action met à jour l'état des champs et des boutons à chaque saisie.
         */
        Runnable miseAJour = () -> mettreAJourEtatInscription(
                champLogin,
                champEmail,
                champMotDePasse,
                boutonOk,
                boutonCancel,
                message
        );

        ajouterEcouteur(champLogin.getDocument(), miseAJour);
        ajouterEcouteur(champEmail.getDocument(), miseAJour);
        ajouterEcouteur(champMotDePasse.getDocument(), miseAJour);

        /*
         * Action du bouton OK :
         * On tente d'inscrire l'utilisateur via AuthentificationService.
         */
        boutonOk.addActionListener(e -> {
            String login = champLogin.getText().trim();
            String email = champEmail.getText().trim();
            String motDePasse = lireMotDePasse(champMotDePasse);

            try {
                authentificationService.inscrire(login, email, motDePasse);

                JOptionPane.showMessageDialog(
                        this,
                        "Utilisateur inscrit avec succès.",
                        "Inscription réussie",
                        JOptionPane.INFORMATION_MESSAGE
                );

                viderChamps(champLogin, champEmail, champMotDePasse);
                miseAJour.run();

            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        exception.getMessage(),
                        "Erreur d'inscription",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        /*
         * Action du bouton Cancel :
         * On vide simplement les champs du formulaire.
         */
        boutonCancel.addActionListener(e -> {
            viderChamps(champLogin, champEmail, champMotDePasse);
            miseAJour.run();
        });

        return panneau;
    }

    /**
     * Crée le panneau de connexion.
     *
     * Ce panneau permet à un utilisateur déjà inscrit de tenter une connexion.
     *
     * @return le panneau Swing de connexion
     */
    private JPanel creerPanneauConnexion() {
        JPanel panneau = new JPanel(new GridBagLayout());
        GridBagConstraints contraintes = creerContraintesDeBase();

        JTextField champLogin = new JTextField(22);
        JPasswordField champMotDePasse = new JPasswordField(22);

        JButton boutonOk = new JButton("OK");
        JButton boutonCancel = new JButton("Cancel");

        JLabel message = new JLabel("Saisissez votre login pour activer le mot de passe.");
        message.setForeground(Color.DARK_GRAY);

        champMotDePasse.setEditable(false);
        champMotDePasse.setEnabled(false);

        boutonOk.setEnabled(false);
        boutonCancel.setEnabled(false);

        ajouterLigne(panneau, contraintes, 0, "Login :", champLogin);
        ajouterLigne(panneau, contraintes, 1, "Mot de passe :", champMotDePasse);

        contraintes.gridx = 0;
        contraintes.gridy = 2;
        contraintes.gridwidth = 2;
        panneau.add(message, contraintes);

        JPanel panneauBoutons = new JPanel();
        panneauBoutons.add(boutonOk);
        panneauBoutons.add(boutonCancel);

        contraintes.gridy = 3;
        panneau.add(panneauBoutons, contraintes);

        Runnable miseAJour = () -> mettreAJourEtatConnexion(
                champLogin,
                champMotDePasse,
                boutonOk,
                boutonCancel,
                message
        );

        ajouterEcouteur(champLogin.getDocument(), miseAJour);
        ajouterEcouteur(champMotDePasse.getDocument(), miseAJour);

        boutonOk.addActionListener(e -> {
            String login = champLogin.getText().trim();
            String motDePasse = lireMotDePasse(champMotDePasse);

            boolean connexionReussie = authentificationService.connecter(login, motDePasse);

            if (connexionReussie) {
                JOptionPane.showMessageDialog(
                        this,
                        "Connexion réussie. Accès autorisé.",
                        "Connexion",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Login inconnu ou mot de passe incorrect.",
                        "Connexion refusée",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        boutonCancel.addActionListener(e -> {
            champLogin.setText("");
            champMotDePasse.setText("");
            miseAJour.run();
        });

        return panneau;
    }

    /**
     * Met à jour l'état du formulaire d'inscription.
     *
     * Règles appliquées :
     * - le champ mot de passe est désactivé si le login fait moins de 8 caractères ;
     * - le bouton Cancel est désactivé si tous les champs sont vides ;
     * - le bouton OK est activé uniquement si les champs semblent valides.
     */
    private void mettreAJourEtatInscription(
            JTextField champLogin,
            JTextField champEmail,
            JPasswordField champMotDePasse,
            JButton boutonOk,
            JButton boutonCancel,
            JLabel message
    ) {
        String login = champLogin.getText().trim();
        String email = champEmail.getText().trim();
        String motDePasse = lireMotDePasse(champMotDePasse);

        boolean loginSuffisant = login.length() >= 8;

        champMotDePasse.setEditable(loginSuffisant);
        champMotDePasse.setEnabled(loginSuffisant);

        if (!loginSuffisant) {
            champMotDePasse.setText("");
            message.setText("Le login doit contenir au moins 8 caractères pour saisir un mot de passe.");
        } else {
            message.setText("Mot de passe attendu : 8 caractères, majuscule, minuscule, chiffre et spécial.");
        }

        boolean formulaireVide = login.isBlank()
                && email.isBlank()
                && motDePasse.isBlank();

        boutonCancel.setEnabled(!formulaireVide);

        boolean emailValideSimple = email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
        boolean motDePasseValideInterface = motDePasseRespecteReglesInterface(motDePasse);

        boutonOk.setEnabled(loginSuffisant && emailValideSimple && motDePasseValideInterface);
    }

    /**
     * Met à jour l'état du formulaire de connexion.
     *
     * Pour rester proche des consignes, le mot de passe est activé seulement
     * quand le login contient au moins 8 caractères.
     */
    private void mettreAJourEtatConnexion(
            JTextField champLogin,
            JPasswordField champMotDePasse,
            JButton boutonOk,
            JButton boutonCancel,
            JLabel message
    ) {
        String login = champLogin.getText().trim();
        String motDePasse = lireMotDePasse(champMotDePasse);

        boolean loginSuffisant = login.length() >= 8;

        champMotDePasse.setEditable(loginSuffisant);
        champMotDePasse.setEnabled(loginSuffisant);

        if (!loginSuffisant) {
            champMotDePasse.setText("");
            message.setText("Le login doit contenir au moins 8 caractères.");
        } else {
            message.setText("Saisissez le mot de passe puis cliquez sur OK.");
        }

        boolean formulaireVide = login.isBlank() && motDePasse.isBlank();

        boutonCancel.setEnabled(!formulaireVide);
        boutonOk.setEnabled(loginSuffisant && !motDePasse.isBlank());
    }

    /**
     * Vérifie les règles minimales du mot de passe côté interface.
     *
     * Ces règles évitent d'activer le bouton OK trop tôt.
     * La validation complète reste faite dans ValidateurFormulaire.
     */
    private boolean motDePasseRespecteReglesInterface(String motDePasse) {
        return motDePasse.length() >= 8
                && motDePasse.matches(".*[A-Z].*")
                && motDePasse.matches(".*[a-z].*")
                && motDePasse.matches(".*[0-9].*")
                && motDePasse.matches(".*[^a-zA-Z0-9].*");
    }

    /**
     * Lit proprement le contenu d'un JPasswordField.
     *
     * JPasswordField retourne un tableau de caractères pour éviter
     * de manipuler directement le mot de passe comme une String.
     * Ici, on convertit en String pour pouvoir utiliser nos services.
     */
    private String lireMotDePasse(JPasswordField champMotDePasse) {
        return new String(champMotDePasse.getPassword());
    }

    /**
     * Vide les champs d'inscription.
     */
    private void viderChamps(
            JTextField champLogin,
            JTextField champEmail,
            JPasswordField champMotDePasse
    ) {
        champLogin.setText("");
        champEmail.setText("");
        champMotDePasse.setText("");
    }

    /**
     * Crée une configuration de base pour placer les composants dans la fenêtre.
     */
    private GridBagConstraints creerContraintesDeBase() {
        GridBagConstraints contraintes = new GridBagConstraints();
        contraintes.insets = new Insets(8, 8, 8, 8);
        contraintes.fill = GridBagConstraints.HORIZONTAL;
        return contraintes;
    }

    /**
     * Ajoute une ligne label + champ dans un panneau.
     */
    private void ajouterLigne(
            JPanel panneau,
            GridBagConstraints contraintes,
            int ligne,
            String texteLabel,
            JComponent champ
    ) {
        contraintes.gridx = 0;
        contraintes.gridy = ligne;
        contraintes.gridwidth = 1;
        panneau.add(new JLabel(texteLabel), contraintes);

        contraintes.gridx = 1;
        panneau.add(champ, contraintes);
    }

    /**
     * Ajoute un écouteur sur un champ texte.
     *
     * Swing utilise DocumentListener pour réagir aux modifications
     * dans les JTextField et JPasswordField.
     */
    private void ajouterEcouteur(Document document, Runnable action) {
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        });
    }
}