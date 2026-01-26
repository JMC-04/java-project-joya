package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.repository.EtudiantRepository;
import fr.univ.bibliotheque.model.Specialite;

import javax.swing.*;
import java.awt.*;

public class EtudiantFormDialog extends JDialog {
    
    private final EtudiantRepository etudiant_repository;
    private JTextField username_field;
    private JPasswordField password_field;
    private JComboBox<String> specialite_combo;
    private boolean etudiant_cree = false;
    
    public EtudiantFormDialog(Frame parent, EtudiantRepository etudiant_repository) {
        super(parent, "Créer un Étudiant", true);
        this.etudiant_repository = etudiant_repository;
        initialiser_interface();
    }
    
    private void initialiser_interface() {
        setSize(450, 200);
        setLocationRelativeTo(getParent());
        
        JPanel main_panel = new JPanel(new BorderLayout(10, 10));
        main_panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel form_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        form_panel.add(new JLabel("Username :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        username_field = new JTextField(20);
        form_panel.add(username_field, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Password :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        password_field = new JPasswordField(20);
        form_panel.add(password_field, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Spécialité :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        specialite_combo = new JComboBox<>();
        for (Specialite spec : etudiant_repository.get_specialites()) {
            specialite_combo.addItem(spec.get_nom());
        }
        form_panel.add(specialite_combo, gbc);
        
        main_panel.add(form_panel, BorderLayout.CENTER);
        
        JPanel boutons_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton creer_button = new JButton("Créer");
        creer_button.addActionListener(e -> creer());
        boutons_panel.add(creer_button);
        
        JButton annuler_button = new JButton("Annuler");
        annuler_button.addActionListener(e -> dispose());
        boutons_panel.add(annuler_button);
        
        main_panel.add(boutons_panel, BorderLayout.SOUTH);
        add(main_panel);
    }
    
    private void creer() {
        String username = username_field.getText().trim();
        String password = new String(password_field.getPassword()).trim();
        String nom_specialite = (String) specialite_combo.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty() || nom_specialite == null) {
            JOptionPane.showMessageDialog(this,
                "Tous les champs sont obligatoires.",
                "Validation",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean ajoute = etudiant_repository.ajouter_etudiant(username, password, nom_specialite);
        
        if (ajoute) {
            etudiant_cree = true;
            JOptionPane.showMessageDialog(this,
                "Étudiant '" + username + "' créé avec succès !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur: un étudiant avec ce username existe déjà.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean is_etudiant_cree() {
        return etudiant_cree;
    }
}
