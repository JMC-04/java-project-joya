package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.repository.EtudiantRepository;

import javax.swing.*;
import java.awt.*;

public class SpecialiteFormDialog extends JDialog {
    
    private final EtudiantRepository etudiant_repository;
    private JTextField nom_field;
    private boolean specialite_creee = false;
    
    public SpecialiteFormDialog(Frame parent, EtudiantRepository etudiant_repository) {
        super(parent, "Créer une Spécialité", true);
        this.etudiant_repository = etudiant_repository;
        initialiser_interface();
    }
    
    private void initialiser_interface() {
        setSize(400, 150);
        setLocationRelativeTo(getParent());
        
        JPanel main_panel = new JPanel(new BorderLayout(10, 10));
        main_panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel form_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        form_panel.add(new JLabel("Nom de la spécialité :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nom_field = new JTextField(20);
        form_panel.add(nom_field, gbc);
        
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
        String nom = nom_field.getText().trim();
        
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Le nom de la spécialité ne peut pas être vide.",
                "Validation",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (etudiant_repository.trouver_specialite_par_nom(nom) != null) {
            JOptionPane.showMessageDialog(this,
                "Une spécialité avec le nom '" + nom + "' existe déjà.\nVeuillez choisir un nom différent.",
                "Doublon Détecté",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean ajoute = etudiant_repository.ajouter_specialite(nom);
        
        if (ajoute) {
            specialite_creee = true;
            JOptionPane.showMessageDialog(this,
                "Spécialité '" + nom + "' créée avec succès !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création de la spécialité.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean is_specialite_creee() {
        return specialite_creee;
    }
}
