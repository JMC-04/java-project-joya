package fr.univ.bibliotheque;

import fr.univ.bibliotheque.view.ApplicationContext;
import fr.univ.bibliotheque.view.LoginWindow;

import javax.swing.*;

public class Main {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        
        ApplicationContext.get_instance();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginWindow login_window = new LoginWindow(
                    ApplicationContext.get_instance().get_etudiant_repository()
                );
                login_window.setVisible(true);
            }
        });
    }
}
