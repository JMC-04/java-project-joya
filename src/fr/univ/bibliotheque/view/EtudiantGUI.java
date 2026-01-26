package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.strategy.filter.*;
import fr.univ.bibliotheque.model.*;
import fr.univ.bibliotheque.repository.EtudiantRepository;
import fr.univ.bibliotheque.repository.MediaRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Interface graphique pour les etudiants.
 * 
 * @author Bibliotheque Universitaire
 * @version 2.0
 */
public class EtudiantGUI extends JFrame {
    
    private final Etudiant etudiant;
    private final Catalog catalog;
    private final EtudiantRepository etudiant_repository;
    private final MediaRepository media_repository;
    
    private JTextField recherche_field;
    private JComboBox<String> filtre_type_combo;
    private JTextField filtre_valeur_field;
    private JButton rechercher_button;
    private JTable resultats_table;
    private DefaultTableModel table_model;
    private JButton ouvrir_button;
    private JTextArea description_area;
    
    public EtudiantGUI(Etudiant etudiant) {
        this.etudiant = etudiant;
        this.catalog = ApplicationContext.get_instance().get_catalog();
        this.etudiant_repository = ApplicationContext.get_instance().get_etudiant_repository();
        this.media_repository = ApplicationContext.get_instance().get_media_repository();
        
        setTitle("Bibliotheque Universitaire - Étudiant : " + this.etudiant.get_username());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        initialiser_interface();
    }
    
    private void initialiser_interface() {
        JPanel main_panel = new JPanel(new BorderLayout(10, 10));
        main_panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel recherche_panel = creer_panel_recherche();
        main_panel.add(recherche_panel, BorderLayout.NORTH);
        
        JPanel resultats_panel = creer_panel_resultats();
        main_panel.add(resultats_panel, BorderLayout.CENTER);
        
        JPanel description_panel = creer_panel_description();
        main_panel.add(description_panel, BorderLayout.SOUTH);
        
        add(main_panel);
        
        rechercher();
    }
    
    private JPanel creer_panel_recherche() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("🔍 Recherche et Filtres"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel recherche_label = new JLabel("Rechercher par Titre ou ID :");
        recherche_label.setFont(recherche_label.getFont().deriveFont(Font.BOLD));
        panel.add(recherche_label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        recherche_field = new JTextField(30);
        recherche_field.addActionListener(e -> rechercher());
        panel.add(recherche_field, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel filtre_type_label = new JLabel("Appliquer un Filtre :");
        filtre_type_label.setFont(filtre_type_label.getFont().deriveFont(Font.BOLD));
        panel.add(filtre_type_label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] types_filtres = {"Aucun", "Auteur", "Specialite", "Annee de Publication"};
        filtre_type_combo = new JComboBox<>(types_filtres);
        panel.add(filtre_type_combo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel filtre_valeur_label = new JLabel("Valeur du Filtre :");
        filtre_valeur_label.setFont(filtre_valeur_label.getFont().deriveFont(Font.BOLD));
        panel.add(filtre_valeur_label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        filtre_valeur_field = new JTextField(30);
        filtre_valeur_field.addActionListener(e -> rechercher());
        panel.add(filtre_valeur_field, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        rechercher_button = new JButton("🔍 Lancer la Recherche");
        rechercher_button.setPreferredSize(new Dimension(200, 35));
        rechercher_button.setFont(rechercher_button.getFont().deriveFont(Font.BOLD));
        rechercher_button.addActionListener(e -> rechercher());
        panel.add(rechercher_button, gbc);
        
        return panel;
    }
    
    private JPanel creer_panel_resultats() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resultats de la Recherche"));
        
        String[] colonnes = {
            "Identifiant", 
            "Titre du Media", 
            "Type", 
            "Auteur/Createur", 
            "Annee", 
            "Specialite(s)", 
            "Matiere(s)", 
            "Nb Acces"
        };
        table_model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultats_table = new JTable(table_model);
        resultats_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultats_table.setRowHeight(25);
        resultats_table.getTableHeader().setFont(resultats_table.getTableHeader().getFont().deriveFont(Font.BOLD));
        resultats_table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mettre_a_jour_description();
            }
        });
        
        JScrollPane scroll_pane = new JScrollPane(resultats_table);
        panel.add(scroll_pane, BorderLayout.CENTER);
        
        // Info en bas
        JLabel info_label = new JLabel("💡 Selectionnez un media pour voir sa description complete");
        info_label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(info_label, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel creer_panel_description() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("📄 Description Detaillee du Media Selectionne"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        description_area = new JTextArea(6, 50);
        description_area.setEditable(false);
        description_area.setWrapStyleWord(true);
        description_area.setLineWrap(true);
        description_area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        JScrollPane scroll_pane = new JScrollPane(description_area);
        panel.add(scroll_pane, BorderLayout.CENTER);
        
        ouvrir_button = new JButton("📖 Ouvrir et Consulter ce Media");
        ouvrir_button.setEnabled(false);
        ouvrir_button.setPreferredSize(new Dimension(220, 40));
        ouvrir_button.setFont(ouvrir_button.getFont().deriveFont(Font.BOLD));
        ouvrir_button.addActionListener(e -> ouvrir_media());
        panel.add(ouvrir_button, BorderLayout.EAST);
        
        return panel;
    }
    
    private void rechercher() {
        try {
            String recherche = recherche_field.getText().trim();
            String type_filtre = (String) filtre_type_combo.getSelectedItem();
            String valeur_filtre = filtre_valeur_field.getText().trim();
            
            List<Media> resultats = new ArrayList<>();
            
            if (!recherche.isEmpty()) {
                Media media_par_id = catalog.trouver_media_par_id(recherche);
                if (media_par_id != null) {
                    resultats.add(media_par_id);
                } else {
                    resultats.addAll(catalog.rechercher_par_titre(recherche));
                }
            } else {
                resultats.addAll(catalog.get_all_medias());
            }
            
            if (!"Aucun".equals(type_filtre) && !valeur_filtre.isEmpty()) {
                MediaFilter filtre = creer_filtre(type_filtre, valeur_filtre);
                if (filtre != null) {
                    List<Media> resultats_filtres = new ArrayList<>();
                    for (Media media : resultats) {
                        if (filtre.matches(media)) {
                            resultats_filtres.add(media);
                        }
                    }
                    resultats = resultats_filtres;
                }
            }
            
            afficher_resultats(resultats);
            
            if (resultats.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun resultat trouve.",
                    "Aucun resultat",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private MediaFilter creer_filtre(String type_filtre, String valeur) {
        try {
            switch (type_filtre) {
                case "Auteur":
                    return new AuteurFilter(valeur);
                case "Specialite":
                    String valeur_lower = valeur.trim().toLowerCase();
                    Specialite specialite_trouvee = null;
                    for (Specialite spec : etudiant_repository.get_specialites()) {
                        if (spec.get_nom().toLowerCase().contains(valeur_lower)) {
                            specialite_trouvee = spec;
                            break;
                        }
                    }
                    if (specialite_trouvee != null) {
                        return new SpecialiteFilter(specialite_trouvee, catalog);
                    }
                    return null;
                case "Annee de Publication":
                    return new AnneeFilter(valeur);
                default:
                    return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la creation du filtre : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private void afficher_resultats(List<Media> resultats) {
        table_model.setRowCount(0);
        
        if (resultats == null) {
            return;
        }
        
        for (Media media : resultats) {
            if (media != null) {
                Object[] row = {
                    media.get_id() != null ? media.get_id() : "",
                    media.get_titre() != null ? media.get_titre() : "",
                    media.get_type_media() != null ? media.get_type_media() : "",
                    media.get_auteur() != null ? media.get_auteur() : "",
                    media.get_annee(),
                    obtenir_specialites_media(media),
                    obtenir_matieres_media(media),
                    media.get_compteur_acces()
                };
                table_model.addRow(row);
            }
        }
        
        if (table_model.getRowCount() > 0) {
            resultats_table.setAutoCreateRowSorter(true);
        }
        
        mettre_a_jour_description();
    }
    
    private String obtenir_specialites_media(Media media) {
        if (media == null) {
            return "";
        }
        
        var matieres = catalog.get_matieres_media(media.get_id());
        if (matieres.isEmpty()) {
            return "";
        }
        
        Set<String> specialites_noms = new LinkedHashSet<>();
        for (Matiere matiere : matieres) {
            if (matiere != null && matiere.get_specialite() != null) {
                specialites_noms.add(matiere.get_specialite().get_nom());
            }
        }
        
        return String.join(", ", specialites_noms);
    }
    
    private String obtenir_matieres_media(Media media) {
        if (media == null) {
            return "";
        }
        
        var matieres = catalog.get_matieres_media(media.get_id());
        if (matieres.isEmpty()) {
            return "";
        }
        
        List<String> matieres_formatees = new ArrayList<>();
        for (Matiere matiere : matieres) {
            if (matiere != null) {
                String code = matiere.get_code();
                String intitule = matiere.get_intitule();
                if (intitule != null && !intitule.trim().isEmpty()) {
                    matieres_formatees.add(code + " (" + intitule + ")");
                } else {
                    matieres_formatees.add(code);
                }
            }
        }
        
        return String.join(", ", matieres_formatees);
    }
    
    private void mettre_a_jour_description() {
        int view_row = resultats_table.getSelectedRow();
        if (view_row >= 0) {
            int model_row = resultats_table.convertRowIndexToModel(view_row);
            String id = (String) table_model.getValueAt(model_row, 0);
            Media media = catalog.trouver_media_par_id(id);
            if (media != null) {
                description_area.setText(media.get_informations());
                ouvrir_button.setEnabled(true);
            }
        } else {
            description_area.setText("");
            ouvrir_button.setEnabled(false);
        }
    }
    
    private void ouvrir_media() {
        try {
            int view_row = resultats_table.getSelectedRow();
            if (view_row < 0) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez selectionner un media a ouvrir.",
                    "Aucune selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int model_row = resultats_table.convertRowIndexToModel(view_row);
            String id = (String) table_model.getValueAt(model_row, 0);
            Media media = catalog.trouver_media_par_id(id);
            
            if (media == null) {
                JOptionPane.showMessageDialog(this,
                    "Media introuvable.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                rechercher();
                return;
            }
            
            String description = media.ouvrir();
            
            media_repository.sauvegarder_donnees(catalog, null);
            
            JOptionPane.showMessageDialog(this,
                "Media consulte avec succes !\n\n" + description,
                "Media Ouvert",
                JOptionPane.INFORMATION_MESSAGE);
            
            rechercher();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture du media : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
