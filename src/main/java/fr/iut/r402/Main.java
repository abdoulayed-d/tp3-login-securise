package fr.iut.r402;

import fr.iut.r402.repository.UtilisateurRepository;
import fr.iut.r402.security.MotDePasseService;
import fr.iut.r402.security.ValidateurFormulaire;
import fr.iut.r402.service.AuthentificationService;
import fr.iut.r402.ui.LoginFrame;

import javax.swing.*;

/**
 * Point d'entrée de l'application.
 *
 * Cette classe lance l'interface graphique Swing.
 *
 * Elle prépare les objets nécessaires :
 * - le repository pour accéder à la base H2 ;
 * - le service BCrypt pour les mots de passe ;
 * - le validateur de formulaire ;
 * - le service d'authentification.
 */
public class Main {

    public static void main(String[] args) {
        /*
         * SwingUtilities.invokeLater permet de lancer l'interface graphique
         * dans le bon thread Swing.
         *
         * C'est la bonne pratique pour démarrer une application Swing.
         */
        SwingUtilities.invokeLater(() -> {
            UtilisateurRepository utilisateurRepository = new UtilisateurRepository();

            /*
             * On s'assure que la table utilisateurs existe avant d'afficher l'interface.
             */
            utilisateurRepository.initialiserTable();

            AuthentificationService authentificationService = new AuthentificationService(
                    utilisateurRepository,
                    new MotDePasseService(),
                    new ValidateurFormulaire()
            );

            LoginFrame fenetre = new LoginFrame(authentificationService);
            fenetre.setVisible(true);
        });
    }
}