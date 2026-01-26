package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.model.*;
import fr.univ.bibliotheque.repository.EtudiantRepository;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Dialog pour créer ou modifier un média.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class MediaFormDialog extends JDialog {
    
    private final Catalog catalog;
    private final EtudiantRepository etudiant_repository;
    private final Media media_existant;
    
    private JComboBox<String> type_combo;
    private JTextField titre_field;
    private JTextField auteur_field;
    private JTextField annee_field;
    private JTextArea description_area;
    private JTextField attribut_specifique_field;
    private JLabel attribut_specifique_label;
    private JTextField niveau_difficulte_field;
    private JLabel niveau_difficulte_label;
    private JList<String> matieres_list;
    private DefaultListModel<String> matieres_model;
    
    private boolean media_ajoute = false;
    private boolean media_modifie = false;
    
    public MediaFormDialog(Frame parent, Catalog catalog, EtudiantRepository etudiant_repository, Media media_existant) {
        super(parent, media_existant == null ? "Ajouter un Média" : "Modifier un Média", true);
        this.catalog = catalog;
        this.etudiant_repository = etudiant_repository;
        this.media_existant = media_existant;
        
        initialiser_interface();
        
        if (media_existant != null) {
            remplir_formulaire();
        }
    }
    
    private void initialiser_interface() {
        setSize(600, 700);
        setLocationRelativeTo(getParent());
        
        JPanel main_panel = new JPanel(new BorderLayout(10, 10));
        main_panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel form_panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Type de média
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Type de média :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] types = {"Document", "SeanceVideo", "QuizOnline"};
        type_combo = new JComboBox<>(types);
        type_combo.setEnabled(media_existant == null);
        type_combo.addActionListener(e -> adapter_champs_specifiques());
        form_panel.add(type_combo, gbc);
        row++;
        
        // Titre
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Titre :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        titre_field = new JTextField(30);
        form_panel.add(titre_field, gbc);
        row++;
        
        // Auteur
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Auteur :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        auteur_field = new JTextField(30);
        form_panel.add(auteur_field, gbc);
        row++;
        
        // Année
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form_panel.add(new JLabel("Année :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        annee_field = new JTextField(10);
        form_panel.add(annee_field, gbc);
        row++;
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form_panel.add(new JLabel("Description :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        description_area = new JTextArea(4, 30);
        description_area.setLineWrap(true);
        description_area.setWrapStyleWord(true);
        JScrollPane desc_scroll = new JScrollPane(description_area);
        form_panel.add(desc_scroll, gbc);
        row++;
        
        // Attribut spécifique 1 (nombre de pages / durée / durée estimée)
        gbc.gridy = row;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.weightx = 0;
        attribut_specifique_label = new JLabel("Nombre de pages :");
        form_panel.add(attribut_specifique_label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        attribut_specifique_field = new JTextField(10);
        form_panel.add(attribut_specifique_field, gbc);
        row++;
        
        // Attribut spécifique 2 (niveau difficulté pour quiz)
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        niveau_difficulte_label = new JLabel("Niveau difficulté :");
        niveau_difficulte_label.setVisible(false);
        form_panel.add(niveau_difficulte_label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        niveau_difficulte_field = new JTextField(20);
        niveau_difficulte_field.setVisible(false);
        form_panel.add(niveau_difficulte_field, gbc);
        row++;
        
        // Matières
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form_panel.add(new JLabel("Matières :"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        matieres_model = new DefaultListModel<>();
        for (Specialite spec : etudiant_repository.get_specialites()) {
            for (Matiere matiere : spec.get_matieres()) {
                matieres_model.addElement(matiere.get_code() + " - " + matiere.get_intitule() + " (" + spec.get_nom() + ")");
            }
        }
        matieres_list = new JList<>(matieres_model);
        matieres_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane matieres_scroll = new JScrollPane(matieres_list);
        form_panel.add(matieres_scroll, gbc);
        
        main_panel.add(form_panel, BorderLayout.CENTER);
        
        // Boutons
        JPanel boutons_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sauvegarder_button = new JButton(media_existant == null ? "Ajouter" : "Modifier");
        sauvegarder_button.setPreferredSize(new Dimension(100, 30));
        sauvegarder_button.addActionListener(e -> sauvegarder());
        boutons_panel.add(sauvegarder_button);
        
        JButton annuler_button = new JButton("Annuler");
        annuler_button.setPreferredSize(new Dimension(100, 30));
        annuler_button.addActionListener(e -> dispose());
        boutons_panel.add(annuler_button);
        
        main_panel.add(boutons_panel, BorderLayout.SOUTH);
        
        add(main_panel);
        
        adapter_champs_specifiques();
    }
    
    private void adapter_champs_specifiques() {
        String type = (String) type_combo.getSelectedItem();
        
        if ("Document".equals(type)) {
            attribut_specifique_label.setText("Nombre de pages :");
            attribut_specifique_label.setVisible(true);
            attribut_specifique_field.setVisible(true);
            niveau_difficulte_label.setVisible(false);
            niveau_difficulte_field.setVisible(false);
        } else if ("SeanceVideo".equals(type)) {
            attribut_specifique_label.setText("Durée (minutes) :");
            attribut_specifique_label.setVisible(true);
            attribut_specifique_field.setVisible(true);
            niveau_difficulte_label.setVisible(false);
            niveau_difficulte_field.setVisible(false);
        } else if ("QuizOnline".equals(type)) {
            attribut_specifique_label.setText("Durée estimée (min) :");
            attribut_specifique_label.setVisible(true);
            attribut_specifique_field.setVisible(true);
            niveau_difficulte_label.setText("Niveau difficulté :");
            niveau_difficulte_label.setVisible(true);
            niveau_difficulte_field.setVisible(true);
        }
    }
    
    private void remplir_formulaire() {
        titre_field.setText(media_existant.get_titre());
        auteur_field.setText(media_existant.get_auteur());
        annee_field.setText(String.valueOf(media_existant.get_annee()));
        description_area.setText(media_existant.get_description());
        
        if (media_existant instanceof Document) {
            type_combo.setSelectedItem("Document");
            attribut_specifique_field.setText(String.valueOf(((Document) media_existant).get_nombre_de_pages()));
        } else if (media_existant instanceof SeanceVideo) {
            type_combo.setSelectedItem("SeanceVideo");
            attribut_specifique_field.setText(String.valueOf(((SeanceVideo) media_existant).get_duree()));
        } else if (media_existant instanceof QuizOnline) {
            type_combo.setSelectedItem("QuizOnline");
            attribut_specifique_field.setText(String.valueOf(((QuizOnline) media_existant).get_duree_estimee()));
            niveau_difficulte_field.setText(((QuizOnline) media_existant).get_niveau_difficulte());
        }
        
        // Sélectionner les matières existantes
        var matieres_existantes = catalog.get_matieres_media(media_existant.get_id());
        List<Integer> indices_selection = new ArrayList<>();
        for (int i = 0; i < matieres_model.size(); i++) {
            String item = matieres_model.get(i);
            String code = item.split(" - ")[0];
            for (Matiere matiere : matieres_existantes) {
                if (matiere.get_code().equals(code)) {
                    indices_selection.add(i);
                    break;
                }
            }
        }
        int[] indices = indices_selection.stream().mapToInt(Integer::intValue).toArray();
        matieres_list.setSelectedIndices(indices);
    }
    
    private void sauvegarder() {
        try {
            // Validation
            String titre = titre_field.getText().trim();
            String auteur = auteur_field.getText().trim();
            String annee_str = annee_field.getText().trim();
            String description = description_area.getText().trim();
            String attribut_str = attribut_specifique_field.getText().trim();
            
            if (titre.isEmpty() || auteur.isEmpty() || annee_str.isEmpty() || description.isEmpty() || attribut_str.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Tous les champs obligatoires doivent être remplis.",
                    "Validation",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int annee;
            int attribut_valeur;
            try {
                annee = Integer.parseInt(annee_str);
                attribut_valeur = Integer.parseInt(attribut_str);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "L'année et l'attribut spécifique doivent être des nombres valides.",
                    "Validation",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (annee < 0) {
                JOptionPane.showMessageDialog(this,
                    "L'année ne peut pas être négative.",
                    "Validation",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (attribut_valeur <= 0) {
                JOptionPane.showMessageDialog(this,
                    "L'attribut spécifique doit être positif.",
                    "Validation",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Récupérer les matières sélectionnées
            List<Matiere> matieres_selectionnees = new ArrayList<>();
            for (int index : matieres_list.getSelectedIndices()) {
                String item = matieres_model.get(index);
                String code = item.split(" - ")[0];
                for (Specialite spec : etudiant_repository.get_specialites()) {
                    Matiere matiere = spec.trouver_matiere_par_code(code);
                    if (matiere != null) {
                        matieres_selectionnees.add(matiere);
                        break;
                    }
                }
            }
            
            if (media_existant == null) {
                // Création - Vérifier les doublons
                if (verifier_media_existe(titre, matieres_selectionnees)) {
                    JOptionPane.showMessageDialog(this,
                        "Un média avec ce titre existe déjà pour les mêmes spécialités.\n" +
                        "Veuillez choisir un titre différent.",
                        "Doublon Détecté",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Media nouveau_media = creer_media(titre, auteur, annee, description, attribut_valeur);
                boolean ajoute = catalog.ajouter_media(nouveau_media, matieres_selectionnees);
                
                if (ajoute) {
                    media_ajoute = true;
                    JOptionPane.showMessageDialog(this,
                        "Média ajouté avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur: le média existe déjà.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Modification - Vérifier les doublons (sauf si c'est le même média)
                if (!titre.equals(media_existant.get_titre()) || 
                    !matieres_egales(catalog.get_matieres_media(media_existant.get_id()), matieres_selectionnees)) {
                    if (verifier_media_existe(titre, matieres_selectionnees)) {
                        JOptionPane.showMessageDialog(this,
                            "Un autre média avec ce titre existe déjà pour les mêmes spécialités.\n" +
                            "Veuillez choisir un titre différent.",
                            "Doublon Détecté",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                boolean modifie = catalog.modifier_media(media_existant.get_id(), titre, auteur, annee, description);
                
                if (modifie) {
                    // Modifier attributs spécifiques
                    if (media_existant instanceof Document) {
                        ((Document) media_existant).set_nombre_de_pages(attribut_valeur);
                    } else if (media_existant instanceof SeanceVideo) {
                        ((SeanceVideo) media_existant).set_duree(attribut_valeur);
                    } else if (media_existant instanceof QuizOnline) {
                        ((QuizOnline) media_existant).set_duree_estimee(attribut_valeur);
                        String niveau = niveau_difficulte_field.getText().trim();
                        if (!niveau.isEmpty()) {
                            ((QuizOnline) media_existant).set_niveau_difficulte(niveau);
                        }
                    }
                    
                    // Modifier matières
                    catalog.modifier_matieres_media(media_existant.get_id(), matieres_selectionnees);
                    
                    media_modifie = true;
                    JOptionPane.showMessageDialog(this,
                        "Média modifié avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private Media creer_media(String titre, String auteur, int annee, String description, int attribut_valeur) {
        String type = (String) type_combo.getSelectedItem();
        
        switch (type) {
            case "Document":
                return new Document(titre, auteur, annee, description, attribut_valeur);
            case "SeanceVideo":
                return new SeanceVideo(titre, auteur, annee, description, attribut_valeur);
            case "QuizOnline":
                String niveau = niveau_difficulte_field.getText().trim();
                if (niveau.isEmpty()) {
                    niveau = QuizOnline.MOYEN;
                }
                return new QuizOnline(titre, auteur, annee, description, attribut_valeur, niveau);
            default:
                throw new IllegalArgumentException("Type de média inconnu : " + type);
        }
    }
    
    public boolean is_media_ajoute() {
        return media_ajoute;
    }
    
    public boolean is_media_modifie() {
        return media_modifie;
    }
    
    /**
     * Vérifie si un média avec le même titre existe déjà pour les mêmes spécialités.
     */
    private boolean verifier_media_existe(String titre, List<Matiere> matieres) {
        Set<String> specialites_cibles = new LinkedHashSet<>();
        for (Matiere matiere : matieres) {
            if (matiere != null && matiere.get_specialite() != null) {
                specialites_cibles.add(matiere.get_specialite().get_nom());
            }
        }
        
        for (Media media : catalog.get_all_medias()) {
            if (media.get_titre().equalsIgnoreCase(titre)) {
                Set<String> specialites_media = new LinkedHashSet<>();
                for (Matiere matiere : catalog.get_matieres_media(media.get_id())) {
                    if (matiere != null && matiere.get_specialite() != null) {
                        specialites_media.add(matiere.get_specialite().get_nom());
                    }
                }
                
                if (specialites_cibles.equals(specialites_media)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Vérifie si deux collections de matières sont égales.
     */
    private boolean matieres_egales(Set<Matiere> set1, List<Matiere> list2) {
        if (set1.size() != list2.size()) {
            return false;
        }
        return set1.containsAll(list2) && new java.util.HashSet<>(list2).containsAll(set1);
    }
}
