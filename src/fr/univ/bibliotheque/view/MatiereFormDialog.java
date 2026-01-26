package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.repository.EtudiantRepository;
import fr.univ.bibliotheque.model.Specialite;

import javax.swing.*;
import java.awt.*;

public class MatiereFormDialog extends JDialog {
    
    private final EtudiantRepository etudiant_repository;
    private JTextField code_field;
    private JTextField intitule_field;
    private JComboBox<String> specialite_combo;
    private boolean matiere_creee = false;
    
    public MatiereFormDialog(Frame parent, EtudiantRepository etudiant_repository) {
        super(parent, "Créer une Matière", true);
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
        form_panel.add(new JLabel("Code :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        code_field = new JTextField(15);
        form_panel.add(code_field, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Intitulé :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        intitule_field = new JTextField(25);
        form_panel.add(intitule_field, gbc);
        
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
        String code = code_field.getText().trim();
        String intitule = intitule_field.getText().trim();
        String nom_specialite = (String) specialite_combo.getSelectedItem();
        
        if (code.isEmpty() || nom_specialite == null) {
            JOptionPane.showMessageDialog(this,
                "Le code de la matière et la spécialité sont obligatoires.",
                "Validation",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Specialite specialite = etudiant_repository.trouver_specialite_par_nom(nom_specialite);
        if (specialite != null && specialite.trouver_matiere_par_code(code) != null) {
            JOptionPane.showMessageDialog(this,
                "Une matière avec le code '" + code + "' existe déjà dans la spécialité '" + nom_specialite + "'.\nVeuillez choisir un code différent.",
                "Doublon Détecté",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean ajoute = etudiant_repository.ajouter_matiere(code, intitule, nom_specialite);
        
        if (ajoute) {
            matiere_creee = true;
            JOptionPane.showMessageDialog(this,
                "Matière '" + code + "' créée avec succès !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création de la matière.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean is_matiere_creee() {
        return matiere_creee;
    }
}
