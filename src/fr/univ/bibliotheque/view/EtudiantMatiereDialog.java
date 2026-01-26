package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.repository.EtudiantRepository;
import fr.univ.bibliotheque.model.*;

import javax.swing.*;
import java.awt.*;

public class EtudiantMatiereDialog extends JDialog {
    
    private final EtudiantRepository etudiant_repository;
    private JComboBox<String> etudiant_combo;
    private JComboBox<String> matiere_combo;
    
    public EtudiantMatiereDialog(Frame parent, EtudiantRepository etudiant_repository) {
        super(parent, "Assigner Matière à Étudiant", true);
        this.etudiant_repository = etudiant_repository;
        initialiser_interface();
    }
    
    private void initialiser_interface() {
        setSize(500, 180);
        setLocationRelativeTo(getParent());
        
        JPanel main_panel = new JPanel(new BorderLayout(10, 10));
        main_panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel form_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        form_panel.add(new JLabel("Étudiant :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        etudiant_combo = new JComboBox<>();
        for (Etudiant etudiant : etudiant_repository.get_etudiants()) {
            etudiant_combo.addItem(etudiant.get_username());
        }
        form_panel.add(etudiant_combo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Matière :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        matiere_combo = new JComboBox<>();
        for (Specialite spec : etudiant_repository.get_specialites()) {
            for (Matiere matiere : spec.get_matieres()) {
                matiere_combo.addItem(matiere.get_code() + " - " + matiere.get_intitule());
            }
        }
        form_panel.add(matiere_combo, gbc);
        
        main_panel.add(form_panel, BorderLayout.CENTER);
        
        JPanel boutons_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton assigner_button = new JButton("Assigner");
        assigner_button.addActionListener(e -> assigner());
        boutons_panel.add(assigner_button);
        
        JButton annuler_button = new JButton("Annuler");
        annuler_button.addActionListener(e -> dispose());
        boutons_panel.add(annuler_button);
        
        main_panel.add(boutons_panel, BorderLayout.SOUTH);
        add(main_panel);
    }
    
    private void assigner() {
        String username = (String) etudiant_combo.getSelectedItem();
        String matiere_str = (String) matiere_combo.getSelectedItem();
        
        if (username == null || matiere_str == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un étudiant et une matière.",
                "Validation",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String code_matiere = matiere_str.split(" - ")[0];
        boolean assigne = etudiant_repository.assigner_matiere_a_etudiant(username, code_matiere);
        
        if (assigne) {
            JOptionPane.showMessageDialog(this,
                "Matière assignée avec succès !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur: impossible d'assigner (vérifier compatibilité spécialité).",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
