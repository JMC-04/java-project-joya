package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.model.Etudiant;
import fr.univ.bibliotheque.repository.EtudiantRepository;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    
    private final EtudiantRepository etudiant_repository;
    private JTextField username_field;
    private JPasswordField password_field;
    private JButton login_button;
    
    public LoginWindow(EtudiantRepository etudiant_repository) {
        this.etudiant_repository = etudiant_repository;
        initialiser_interface();
    }
    
    private void initialiser_interface() {
        setTitle("Connexion - Bibliothèque Universitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel main_panel = new JPanel(new BorderLayout(10, 10));
        main_panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel form_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        form_panel.add(new JLabel("Nom d'utilisateur :"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        username_field = new JTextField(20);
        form_panel.add(username_field, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Mot de passe :"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        password_field = new JPasswordField(20);
        form_panel.add(password_field, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        login_button = new JButton("Se connecter");
        login_button.setPreferredSize(new Dimension(150, 35));
        login_button.addActionListener(e -> tenter_connexion());
        form_panel.add(login_button, gbc);
        
        password_field.addActionListener(e -> tenter_connexion());
        
        main_panel.add(form_panel, BorderLayout.CENTER);
        
        JLabel help_label = new JLabel("<html><center><i>Connexion Admin: 'admin' / 'admin'<br>" +
                                       "Connexion Étudiant: username/password depuis universite.xml</i></center></html>");
        help_label.setHorizontalAlignment(SwingConstants.CENTER);
        main_panel.add(help_label, BorderLayout.SOUTH);
        
        add(main_panel);
    }
    
    private void tenter_connexion() {
        String username = username_field.getText().trim();
        String password = new String(password_field.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir un nom d'utilisateur et un mot de passe.",
                "Erreur de connexion",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            ouvrir_interface_admin();
            return;
        }
        
        Etudiant etudiant = etudiant_repository.trouver_etudiant_par_username(username);
        if (etudiant != null && etudiant.verifier_password(password)) {
            ouvrir_interface_etudiant(etudiant);
            return;
        }
        
        JOptionPane.showMessageDialog(this,
            "Nom d'utilisateur ou mot de passe incorrect.",
            "Erreur de connexion",
            JOptionPane.ERROR_MESSAGE);
        password_field.setText("");
    }
    
    private void ouvrir_interface_admin() {
        this.setVisible(false);
        AdminGUI admin_gui = new AdminGUI();
        admin_gui.setVisible(true);
    }
    
    private void ouvrir_interface_etudiant(Etudiant etudiant) {
        this.setVisible(false);
        EtudiantGUI etudiant_gui = new EtudiantGUI(etudiant);
        etudiant_gui.setVisible(true);
    }
}
