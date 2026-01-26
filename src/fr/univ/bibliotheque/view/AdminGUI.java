package fr.univ.bibliotheque.view;

import fr.univ.bibliotheque.service.Catalog;
import fr.univ.bibliotheque.strategy.export.*;
import fr.univ.bibliotheque.strategy.report.*;
import fr.univ.bibliotheque.model.*;
import fr.univ.bibliotheque.repository.EtudiantRepository;
import fr.univ.bibliotheque.repository.MediaRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Interface graphique pour l'administrateur.
 * 
 * @author Bibliothèque Universitaire
 * @version 2.0
 */
public class AdminGUI extends JFrame {
    
    private final Catalog catalog;
    private final EtudiantRepository etudiant_repository;
    private final MediaRepository media_repository;
    
    private JTabbedPane onglets;
    
    // Onglet Gestion des médias
    private JTable medias_table;
    private DefaultTableModel medias_table_model;
    private JButton sauvegarder_button;
    
    // Onglet Statistiques
    private JComboBox<String> rapport_type_combo;
    private JComboBox<String> rapport_cible_combo;
    private JButton generer_rapport_button;
    private JTextArea rapport_area;
    private JButton exporter_xml_button;
    private JButton exporter_csv_button;
    private List<Media> rapport_resultats_actuels;
    
    public AdminGUI() {
        this.catalog = ApplicationContext.get_instance().get_catalog();
        this.etudiant_repository = ApplicationContext.get_instance().get_etudiant_repository();
        this.media_repository = ApplicationContext.get_instance().get_media_repository();
        
        setTitle("Bibliothèque Universitaire - Administration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        
        initialiser_interface();
        charger_medias();
    }
    
    private void initialiser_interface() {
        onglets = new JTabbedPane();
        
        onglets.addTab("Gestion des Médias", creer_onglet_gestion_medias());
        onglets.addTab("Statistiques & Exports", creer_onglet_statistiques());
        
        add(onglets);
    }
    
    private JPanel creer_onglet_gestion_medias() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de boutons verticaux à gauche
        JPanel boutons_panel = new JPanel();
        boutons_panel.setLayout(new BoxLayout(boutons_panel, BoxLayout.Y_AXIS));
        boutons_panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Actions"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        Dimension button_size = new Dimension(200, 40);
        
        // Section Gestion des Médias
        JLabel media_label = new JLabel("Gestion des Médias");
        media_label.setFont(media_label.getFont().deriveFont(Font.BOLD, 13f));
        media_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        boutons_panel.add(media_label);
        boutons_panel.add(Box.createVerticalStrut(10));
        
        JButton ajouter_button = new JButton("➕ Ajouter un Média");
        ajouter_button.setMaximumSize(button_size);
        ajouter_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        ajouter_button.addActionListener(e -> ouvrir_formulaire_ajout());
        boutons_panel.add(ajouter_button);
        boutons_panel.add(Box.createVerticalStrut(8));
        
        JButton modifier_button = new JButton("✏️ Modifier le Média");
        modifier_button.setMaximumSize(button_size);
        modifier_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        modifier_button.addActionListener(e -> ouvrir_formulaire_modification());
        boutons_panel.add(modifier_button);
        boutons_panel.add(Box.createVerticalStrut(8));
        
        JButton supprimer_button = new JButton("🗑️ Supprimer le Média");
        supprimer_button.setMaximumSize(button_size);
        supprimer_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        supprimer_button.addActionListener(e -> supprimer_media());
        boutons_panel.add(supprimer_button);
        boutons_panel.add(Box.createVerticalStrut(20));
        
        // Section Gestion des Entités
        JLabel entites_label = new JLabel("Gestion des Entités");
        entites_label.setFont(entites_label.getFont().deriveFont(Font.BOLD, 13f));
        entites_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        boutons_panel.add(entites_label);
        boutons_panel.add(Box.createVerticalStrut(10));
        
        JButton creer_specialite_button = new JButton("🎓 Créer Spécialité");
        creer_specialite_button.setMaximumSize(button_size);
        creer_specialite_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        creer_specialite_button.addActionListener(e -> ouvrir_formulaire_specialite());
        boutons_panel.add(creer_specialite_button);
        boutons_panel.add(Box.createVerticalStrut(8));
        
        JButton creer_matiere_button = new JButton("📚 Créer Matière");
        creer_matiere_button.setMaximumSize(button_size);
        creer_matiere_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        creer_matiere_button.addActionListener(e -> ouvrir_formulaire_matiere());
        boutons_panel.add(creer_matiere_button);
        boutons_panel.add(Box.createVerticalStrut(8));
        
        JButton creer_etudiant_button = new JButton("👤 Créer Étudiant");
        creer_etudiant_button.setMaximumSize(button_size);
        creer_etudiant_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        creer_etudiant_button.addActionListener(e -> ouvrir_formulaire_etudiant());
        boutons_panel.add(creer_etudiant_button);
        boutons_panel.add(Box.createVerticalStrut(8));
        
        JButton assigner_matiere_button = new JButton("🔗 Assigner Matière");
        assigner_matiere_button.setMaximumSize(button_size);
        assigner_matiere_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        assigner_matiere_button.addActionListener(e -> ouvrir_formulaire_assignation());
        boutons_panel.add(assigner_matiere_button);
        boutons_panel.add(Box.createVerticalStrut(20));
        
        // Section Sauvegarde
        JLabel sauvegarde_label = new JLabel("Sauvegarde");
        sauvegarde_label.setFont(sauvegarde_label.getFont().deriveFont(Font.BOLD, 13f));
        sauvegarde_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        boutons_panel.add(sauvegarde_label);
        boutons_panel.add(Box.createVerticalStrut(10));
        
        sauvegarder_button = new JButton("💾 Sauvegarder Données");
        sauvegarder_button.setMaximumSize(button_size);
        sauvegarder_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        sauvegarder_button.addActionListener(e -> sauvegarder_donnees());
        boutons_panel.add(sauvegarder_button);
        
        boutons_panel.add(Box.createVerticalGlue());
        
        panel.add(boutons_panel, BorderLayout.WEST);
        
        // Table des médias à droite avec en-têtes clairs
        JPanel table_panel = new JPanel(new BorderLayout());
        table_panel.setBorder(BorderFactory.createTitledBorder("Liste des Médias du Catalogue"));
        
        String[] colonnes = {
            "Identifiant", 
            "Titre du Média", 
            "Type", 
            "Auteur/Créateur", 
            "Année", 
            "Spécialité(s)", 
            "Matière(s)", 
            "Nb Accès"
        };
        medias_table_model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medias_table = new JTable(medias_table_model);
        medias_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medias_table.setRowHeight(25);
        medias_table.getTableHeader().setFont(medias_table.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        JScrollPane scroll_pane = new JScrollPane(medias_table);
        table_panel.add(scroll_pane, BorderLayout.CENTER);
        
        // Info en bas de la table
        JLabel info_label = new JLabel("💡 Sélectionnez un média puis utilisez les boutons d'action à gauche");
        info_label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        table_panel.add(info_label, BorderLayout.SOUTH);
        
        panel.add(table_panel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel creer_onglet_statistiques() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel de contrôles verticaux à gauche
        JPanel controles_panel = new JPanel();
        controles_panel.setLayout(new BoxLayout(controles_panel, BoxLayout.Y_AXIS));
        controles_panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Paramètres du Rapport"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        Dimension field_size = new Dimension(220, 30);
        
        // Type de rapport
        JLabel type_label = new JLabel("Type de Rapport");
        type_label.setFont(type_label.getFont().deriveFont(Font.BOLD, 13f));
        type_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        controles_panel.add(type_label);
        controles_panel.add(Box.createVerticalStrut(8));
        
        String[] types_rapports = {"Par spécialité", "Par matière"};
        rapport_type_combo = new JComboBox<>(types_rapports);
        rapport_type_combo.setMaximumSize(field_size);
        rapport_type_combo.setAlignmentX(Component.CENTER_ALIGNMENT);
        rapport_type_combo.addActionListener(e -> mettre_a_jour_rapport_cible());
        controles_panel.add(rapport_type_combo);
        controles_panel.add(Box.createVerticalStrut(20));
        
        // Cible du rapport
        JLabel cible_label = new JLabel("Sélectionner la Cible");
        cible_label.setFont(cible_label.getFont().deriveFont(Font.BOLD, 13f));
        cible_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        controles_panel.add(cible_label);
        controles_panel.add(Box.createVerticalStrut(8));
        
        rapport_cible_combo = new JComboBox<>();
        rapport_cible_combo.setMaximumSize(field_size);
        rapport_cible_combo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mettre_a_jour_rapport_cible();
        controles_panel.add(rapport_cible_combo);
        controles_panel.add(Box.createVerticalStrut(20));
        
        // Bouton générer
        JLabel action_label = new JLabel("Action");
        action_label.setFont(action_label.getFont().deriveFont(Font.BOLD, 13f));
        action_label.setAlignmentX(Component.CENTER_ALIGNMENT);
        controles_panel.add(action_label);
        controles_panel.add(Box.createVerticalStrut(8));
        
        generer_rapport_button = new JButton("📊 Générer le Rapport");
        generer_rapport_button.setMaximumSize(field_size);
        generer_rapport_button.setPreferredSize(new Dimension(220, 40));
        generer_rapport_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        generer_rapport_button.setFont(generer_rapport_button.getFont().deriveFont(Font.BOLD));
        generer_rapport_button.addActionListener(e -> generer_rapport());
        controles_panel.add(generer_rapport_button);
        
        controles_panel.add(Box.createVerticalGlue());
        
        panel.add(controles_panel, BorderLayout.WEST);
        
        // Panel de résultats à droite
        JPanel resultats_panel = new JPanel(new BorderLayout(10, 10));
        
        // Zone de résultat
        JPanel rapport_wrapper = new JPanel(new BorderLayout());
        rapport_wrapper.setBorder(BorderFactory.createTitledBorder("Résultat du Rapport Statistique"));
        
        rapport_area = new JTextArea(20, 60);
        rapport_area.setEditable(false);
        rapport_area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll_pane = new JScrollPane(rapport_area);
        rapport_wrapper.add(scroll_pane, BorderLayout.CENTER);
        
        resultats_panel.add(rapport_wrapper, BorderLayout.CENTER);
        
        // Panel d'export sous les résultats
        JPanel export_panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        export_panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Exporter le Rapport"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        exporter_xml_button = new JButton("💾 Exporter en XML");
        exporter_xml_button.setEnabled(false);
        exporter_xml_button.setPreferredSize(new Dimension(180, 40));
        exporter_xml_button.addActionListener(e -> exporter_rapport("XML"));
        export_panel.add(exporter_xml_button);
        
        exporter_csv_button = new JButton("💾 Exporter en CSV");
        exporter_csv_button.setEnabled(false);
        exporter_csv_button.setPreferredSize(new Dimension(180, 40));
        exporter_csv_button.addActionListener(e -> exporter_rapport("CSV"));
        export_panel.add(exporter_csv_button);
        
        resultats_panel.add(export_panel, BorderLayout.SOUTH);
        
        panel.add(resultats_panel, BorderLayout.CENTER);
        
        rapport_resultats_actuels = new ArrayList<>();
        
        return panel;
    }
    
    private void charger_medias() {
        medias_table_model.setRowCount(0);
        
        for (Media media : catalog.get_all_medias()) {
            Object[] row = {
                media.get_id(),
                media.get_titre(),
                media.get_type_media(),
                media.get_auteur(),
                media.get_annee(),
                obtenir_specialites_media(media),
                obtenir_matieres_media(media),
                media.get_compteur_acces()
            };
            medias_table_model.addRow(row);
        }
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
    
    private void mettre_a_jour_rapport_cible() {
        rapport_cible_combo.removeAllItems();
        
        String type_rapport = (String) rapport_type_combo.getSelectedItem();
        if ("Par spécialité".equals(type_rapport)) {
            for (Specialite spec : etudiant_repository.get_specialites()) {
                rapport_cible_combo.addItem(spec.get_nom());
            }
        } else if ("Par matière".equals(type_rapport)) {
            for (Specialite spec : etudiant_repository.get_specialites()) {
                for (Matiere matiere : spec.get_matieres()) {
                    rapport_cible_combo.addItem(matiere.get_code() + " - " + matiere.get_intitule());
                }
            }
        }
    }
    
    private void generer_rapport() {
        try {
            String type_rapport = (String) rapport_type_combo.getSelectedItem();
            String cible = (String) rapport_cible_combo.getSelectedItem();
            
            if (cible == null || cible.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une cible.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StatisticsReport rapport = null;
            
            if ("Par spécialité".equals(type_rapport)) {
                Specialite specialite = etudiant_repository.trouver_specialite_par_nom(cible);
                if (specialite == null) {
                    JOptionPane.showMessageDialog(this,
                        "Spécialité introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                rapport = new ParSpecialiteReport(catalog, specialite);
            } else if ("Par matière".equals(type_rapport)) {
                String code_matiere = cible.split(" - ")[0];
                Matiere matiere = null;
                for (Specialite spec : etudiant_repository.get_specialites()) {
                    for (Matiere m : spec.get_matieres()) {
                        if (m.get_code().equals(code_matiere)) {
                            matiere = m;
                            break;
                        }
                    }
                    if (matiere != null) break;
                }
                if (matiere == null) {
                    JOptionPane.showMessageDialog(this,
                        "Matière introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                rapport = new ParMatiereReport(matiere);
            }
            
            if (rapport != null) {
                List<Media> resultats = rapport.generer();
                rapport_resultats_actuels = resultats;
                afficher_rapport(rapport, resultats);
                exporter_xml_button.setEnabled(!resultats.isEmpty());
                exporter_csv_button.setEnabled(!resultats.isEmpty());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void afficher_rapport(StatisticsReport rapport, List<Media> resultats) {
        StringBuilder sb = new StringBuilder();
        sb.append(rapport.get_titre()).append("\n");
        sb.append("=".repeat(80)).append("\n\n");
        sb.append(rapport.get_description()).append("\n\n");
        sb.append(String.format("%-36s %-30s %-15s %-10s%n", 
            "ID", "Titre", "Type", "Accès"));
        sb.append("-".repeat(80)).append("\n");
        
        int total_acces = 0;
        for (Media media : resultats) {
            int compteur_acces = media.get_compteur_acces();
            total_acces += compteur_acces;
            sb.append(String.format("%-36s %-30s %-15s %-10d%n",
                media.get_id().substring(0, Math.min(36, media.get_id().length())),
                media.get_titre().substring(0, Math.min(30, media.get_titre().length())),
                media.get_type_media(),
                compteur_acces));
        }
        
        sb.append("-".repeat(80)).append("\n");
        sb.append(String.format("%-36s %-30s %-15s %-10d%n",
            "Total", "", "", total_acces));
        sb.append("\nNombre de médias : ").append(resultats.size());
        sb.append("\nTotal d'accès : ").append(total_acces);
        
        rapport_area.setText(sb.toString());
    }
    
    private void exporter_rapport(String format) {
        try {
            if (rapport_resultats_actuels == null || rapport_resultats_actuels.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun rapport à exporter.",
                    "Aucune donnée",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JFileChooser file_chooser = new JFileChooser();
            file_chooser.setDialogTitle("Choisir l'emplacement");
            
            MediaExporter exporteur;
            String extension;
            
            if ("XML".equals(format)) {
                exporteur = new XMLExporter(catalog);
                extension = ".xml";
            } else if ("CSV".equals(format)) {
                exporteur = new CSVExporter(catalog);
                extension = ".csv";
            } else {
                return;
            }
            
            String cible_rapport = (String) rapport_cible_combo.getSelectedItem();
            String nom_fichier = "rapport";
            if (cible_rapport != null && !cible_rapport.isEmpty()) {
                nom_fichier = cible_rapport.replaceAll("[^a-zA-Z0-9_-]", "_");
            }
            file_chooser.setSelectedFile(new File(nom_fichier + extension));
            
            int result = file_chooser.showSaveDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File fichier = file_chooser.getSelectedFile();
                String chemin = fichier.getAbsolutePath();
                
                if (!chemin.toLowerCase().endsWith(extension)) {
                    chemin += extension;
                }
                
                boolean succes = exporteur.exporter(rapport_resultats_actuels, chemin);
                
                if (succes) {
                    JOptionPane.showMessageDialog(this,
                        "Export réussi vers :\n" + chemin,
                        "Export réussi",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'export.",
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
    
    private void supprimer_media() {
        int row = medias_table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un média.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String id = (String) medias_table_model.getValueAt(row, 0);
        Media media = catalog.trouver_media_par_id(id);
        
        if (media != null) {
            int confirmation = JOptionPane.showConfirmDialog(this,
                "Supprimer le média :\n" + media.get_titre() + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmation == JOptionPane.YES_OPTION) {
                catalog.supprimer_media(id);
                charger_medias();
                JOptionPane.showMessageDialog(this,
                    "Média supprimé.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void sauvegarder_donnees() {
        boolean succes_medias = media_repository.sauvegarder_donnees(catalog, null);
        boolean succes_etudiants = etudiant_repository.sauvegarder_vers_xml(null);
        
        if (succes_medias && succes_etudiants) {
            JOptionPane.showMessageDialog(this,
                "Données sauvegardées avec succès.",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la sauvegarde.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ouvrir_formulaire_ajout() {
        MediaFormDialog dialog = new MediaFormDialog(this, catalog, etudiant_repository, null);
        dialog.setVisible(true);
        
        if (dialog.is_media_ajoute()) {
            charger_medias();
        }
    }
    
    private void ouvrir_formulaire_modification() {
        int row = medias_table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un média à modifier.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String id = (String) medias_table_model.getValueAt(row, 0);
        Media media = catalog.trouver_media_par_id(id);
        
        if (media != null) {
            MediaFormDialog dialog = new MediaFormDialog(this, catalog, etudiant_repository, media);
            dialog.setVisible(true);
            
            if (dialog.is_media_modifie()) {
                charger_medias();
            }
        }
    }
    
    private void ouvrir_formulaire_specialite() {
        SpecialiteFormDialog dialog = new SpecialiteFormDialog(this, etudiant_repository);
        dialog.setVisible(true);
        
        if (dialog.is_specialite_creee()) {
            mettre_a_jour_rapport_cible();
        }
    }
    
    private void ouvrir_formulaire_matiere() {
        MatiereFormDialog dialog = new MatiereFormDialog(this, etudiant_repository);
        dialog.setVisible(true);
        
        if (dialog.is_matiere_creee()) {
            mettre_a_jour_rapport_cible();
        }
    }
    
    private void ouvrir_formulaire_etudiant() {
        EtudiantFormDialog dialog = new EtudiantFormDialog(this, etudiant_repository);
        dialog.setVisible(true);
    }
    
    private void ouvrir_formulaire_assignation() {
        EtudiantMatiereDialog dialog = new EtudiantMatiereDialog(this, etudiant_repository);
        dialog.setVisible(true);
    }
}
